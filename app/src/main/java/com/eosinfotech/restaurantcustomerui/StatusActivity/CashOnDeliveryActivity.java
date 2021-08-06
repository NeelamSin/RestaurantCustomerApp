package com.eosinfotech.restaurantcustomerui.StatusActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.AsyncTask.OrderPaymentAsyncTask;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class CashOnDeliveryActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationViewTwo;
    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cashondelivery);

        lottieAnimationViewTwo = (LottieAnimationView) findViewById(R.id.lottieAnimationViewTwo);
        //startCheckAnimation();

        new FetchOpenOrdercust(CashOnDeliveryActivity.this).execute();

    }

    public class FetchOpenOrdercust extends AsyncTask<String, String, String> {
        private Context mContext;
        String order_headerid = "";
        float or_total = 0.0f;
        String tableid = "";

        public FetchOpenOrdercust(Context context) {
            this.mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.FetchOpenOrdercustUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId", sharedpreferences.getString("companyid", null));
                jsonObject.put("customer_id", sharedpreferences.getString("userid", null));

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Status", "_" + jsonObject.toString());
                if (status == 200) {
                    JSONObject responseJsonObject = new JSONObject(response.body().string());
                    if (responseJsonObject.getBoolean("error") == false) {
                        JSONArray jsonArray = responseJsonObject.getJSONArray("menuItems");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        order_headerid = jsonObject1.getString("header_id");
                        or_total = Float.parseFloat(jsonObject1.getString("order_total"));
                        tableid = jsonObject1.getString("tableno");
                    } else {
                        order_headerid = "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return order_headerid;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!(order_headerid.equals(""))) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("tableid", tableid);
                editor.putString("headerid", order_headerid);
                editor.commit();

                try {
                    final boolean result = new CashOrderPaymentAsyncTask(CashOnDeliveryActivity.this).execute(
                            String.valueOf(or_total),
                            sharedpreferences.getString("tableid", null),
                            getIntent().getExtras().getString("txnRemarks"),
                            getIntent().getExtras().getString("txnRef")
                    ).get();
                    if (result == true) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startCheckAnimation();
                                Log.i("Status", "_" + result);
                                Intent holding = new Intent(CashOnDeliveryActivity.this, TableClosedActivity.class);
                                startActivity(holding);
                                //new ReleaseTable(PaymentSuccessActivity.this).execute(sharedpreferences.getString("tableid",null));

                            }
                        }, 6750);
                        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public void startCheckAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationViewTwo.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationViewTwo.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationViewTwo.setProgress(0f);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }
}
