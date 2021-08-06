package com.eosinfotech.restaurantcustomerui.StatusActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.Adapters.ItemRateCommentAdapter;
import com.eosinfotech.restaurantcustomerui.MainActivities.ItemDetailedActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.MainActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.TrackOrderActivity;
import com.eosinfotech.restaurantcustomerui.Models.ItemRateCommentView;
import com.eosinfotech.restaurantcustomerui.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class OrderSuccessActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationViewTwo;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    ArrayList<String> tokenarraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_order_success);

        lottieAnimationViewTwo = (LottieAnimationView) findViewById(R.id.lottieAnimationViewTwo);
        tokenarraylist=new ArrayList<>();
        //startCheckAnimation();

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        fetchFCMDetailed();


        try {
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchFCMDetailed() {
        new OrderSuccessActivity.FetchFCMDetailedToKitchen(OrderSuccessActivity.this).execute();
    }

    public void startCheckAnimation(){
        final ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) { lottieAnimationViewTwo.setProgress((Float)valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationViewTwo.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationViewTwo.setProgress(0f);
        }
    }


    private class FetchFCMDetailedToKitchen extends AsyncTask<String, String, String> {

        String kitchenToken="";
        private Context mContext;
        public FetchFCMDetailedToKitchen(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchFcmDetailsUrl);
                OkHttpClient client = new OkHttpClient();
                final JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("clientId",getResources().getString(R.string.clientId));
                jsonObject.put("user_type","Kitchen");

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("FCMToken_Status","_"+status);
                Log.i("FCMToken_Req","_"+jsonObject.toString());

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    Log.i("FCMToken_Res","_"+responseJsonObject.toString());


                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("Tokendetails");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);

                            kitchenToken=menuResultsJsonObject.getString("fcm_token");
                            Log.i("FCMToken_token","_"+kitchenToken);
                            tokenarraylist.add(kitchenToken);

                        }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return kitchenToken;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);
            //  progressDialog.dismiss();
            /*mAdapter = new ItemRateCommentAdapter(ItemDetailedActivity.this, personUtilsList);
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);*/

            for(int i=0;i<tokenarraylist.size();i++)
            {
                new OrderSuccessActivity.SendFcm(OrderSuccessActivity.this).execute(tokenarraylist.get(i),""+i);
            }
        }
    }

    private class SendFcm extends AsyncTask<String, String, String> {

        URL url= null;
        HttpURLConnection client = null;
        String position="0";

        private Context mContext;
        public SendFcm(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params)
        {
            position=params[1];

            try {
                url= new URL("https://fcm.googleapis.com/fcm/send");
                client = (HttpURLConnection) url.openConnection();
                client.setDoOutput(true);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestProperty("Authorization", "key="+sharedpreferences.getString("firebaseAPIKey",null));
                client.connect();

                final JSONObject payload =new JSONObject();
                /*payload.put("body",news.text);
                payload.put("title",news.title);*/
                payload.put("title","New Order");
                payload.put("body","You have received a New Order");

                JSONObject notif = new JSONObject();
                notif.put("to", params[0]);
                notif.put("notification", payload);

                OutputStream outputPost = client.getOutputStream();
                outputPost.write(notif.toString().getBytes("UTF-8"));
                outputPost.flush();
                outputPost.close();

                Log.i("FCMToken_Notification","_"+notif.toString());

                // Read the response into a string
                InputStream is = client.getInputStream();
                String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
                is.close();
            }
            catch (Exception e)
            {
                if (e.getMessage() != null) {
                    Log.e("SEND NOTIFY TO FB", e.getMessage());
                }
                else {
                    Log.e("SEND NOTIF TO FB", e.toString());
                }
            }

            finally {
                if(client!=null) client.disconnect();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);
            //  progressDialog.dismiss();

            if(Integer.parseInt(position)==(tokenarraylist.size()-1))
            {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startCheckAnimation();
                        Intent intent=new Intent(OrderSuccessActivity.this,TrackOrderActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },2600);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }

}
