package com.eosinfotech.restaurantcustomerui.StatusActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.AsyncTask.OrderPaymentAsyncTask;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.ItemRatingActivity;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PaymentSuccessActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationViewSix;
    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment_success);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        lottieAnimationViewSix = (LottieAnimationView) findViewById(R.id.lottieAnimationViewSix);
        startCheckAnimation();
        try {
            boolean result=new OrderPaymentAsyncTask(PaymentSuccessActivity.this).execute(
                    getIntent().getExtras().getString("ordertotal"),
                    sharedpreferences.getString("tableid",null),
                    getIntent().getExtras().getString("txnRemarks"),
                    getIntent().getExtras().getString("txnRef")
            ).get();
            if(result==true)
            {/*
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("tableid", "");
                editor.putString("headerid", "");
                editor.putString("ordertype", "");
                editor.putString("tableconfirmed", "");
                editor.commit();
                Intent intent=new Intent(PaymentSuccessActivity.this,TableCategoryActivity.class);
                startActivity(intent);


                Log.i("Status", "_" + result);
                Intent holding = new Intent(PaymentSuccessActivity.this  , TableClosedActivity.class);
                startActivity(holding);

                */
               new ReleaseTable(PaymentSuccessActivity.this).execute(sharedpreferences.getString("tableid",null));

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class ReleaseTable extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        String tableid="0";

        public ReleaseTable(Context context) {
            this.mContext = context;
        }



        @Override
        protected Boolean doInBackground(String... params)
        {
            tableid=params[0];
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);
            String sDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            //String space=fDate+"\t"+params[2];
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.booktableUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("tableid",params[0]);
                jsonObject.put("booking_date","");
                jsonObject.put("booking_time","");
                jsonObject.put("released_time",fDate);
                jsonObject.put("StaffDtlsid","0");
                jsonObject.put("updated_by",sharedpreferences.getString("userid",null));
                jsonObject.put("updated_date",sDate);
                jsonObject.put("customer_id","");
                jsonObject.put("clientId",getResources().getString(R.string.clientId));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("Status","_"+status);
                Log.i("TableRequest",""+jsonObject.toString());

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tableid", null);
            editor.putString("headerid", null);
            editor.putString("ordertype", null);
            editor.putString("tableconfirmed", null);
            editor.putString("discountid", null);
            editor.putString("discountpercent", null);
            editor.putString("discountedamount", null);
            editor.commit();

            Intent intent=new Intent(PaymentSuccessActivity.this,TableCategoryActivity.class);
            startActivity(intent);

            //Intent intent=new Intent(PaymentSuccessActivity.this,ItemRatingActivity.class);
            //startActivity(intent);


        }
    }

    public void startCheckAnimation(){
        final ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationViewSix.setProgress((Float)valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationViewSix.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationViewSix.setProgress(0f);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }
}
