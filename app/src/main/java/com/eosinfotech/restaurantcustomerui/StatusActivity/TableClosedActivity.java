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
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.ItemRatingActivity;
import com.eosinfotech.restaurantcustomerui.Models.ItemRating;
import com.eosinfotech.restaurantcustomerui.Models.TableCategory;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class TableClosedActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationViewFour;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    String order_current_status="";

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        public void run() {
            Log.d("Runnable","Handler is working");

            try {
                Log.d("Runnable","Handler is working1");


                order_current_status = new FetchOpenOrdercust(TableClosedActivity.this).execute().get();
                //initView();
                if(order_current_status.equals("Closed"))
                {
                    Log.d("Runnable","Handler is working2");


                    handler.removeCallbacks(this);
                    Log.d("Runnable","ok");

                    /*SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableid", "");
                    editor.putString("headerid", "");
                    editor.putString("ordertype", "");
                    editor.putString("tableconfirmed", "");
                    editor.commit();


                    Intent nextActivity = new Intent(TableClosedActivity.this , TableCategoryActivity.class);
                    startActivity(nextActivity);*/

                    Intent nextActivity = new Intent(TableClosedActivity.this , ItemRatingActivity.class);
                    startActivity(nextActivity);
                    finish();
                }
                else
                {
                    Log.d("Runnable","Handler is working3");
                    handler.postDelayed(this, 2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table_closed);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        lottieAnimationViewFour = (LottieAnimationView) findViewById(R.id.lottieAnimationViewFour);
        startCheckAnimation();

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);

    }


    public class FetchOpenOrdercust extends AsyncTask<String, String, String> {
        private Context mContext;
        String order_status1="";
        public FetchOpenOrdercust(Context context) {
            this.mContext = context;
        }


        @Override
        protected String  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.fetchOrderHeaderId);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("header_id", sharedpreferences.getString("headerid",null));

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
                    if(responseJsonObject.getBoolean("error")==false) {
                        JSONArray jsonArray = responseJsonObject.getJSONArray("menuItems");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        order_status1 = jsonObject1.getString("order_status");
                    }
                    else
                    {
                        order_status1="";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return order_status1;
        }
    }

    public void startCheckAnimation() {
        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationViewFour.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationViewFour.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationViewFour.setProgress(0f);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }
}
