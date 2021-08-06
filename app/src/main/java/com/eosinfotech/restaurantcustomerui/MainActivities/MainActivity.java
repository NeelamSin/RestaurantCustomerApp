package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;

import com.eosinfotech.restaurantcustomerui.CredentialActivity.LoginActivity;
import com.eosinfotech.restaurantcustomerui.Delegate;
import com.eosinfotech.restaurantcustomerui.MealModule.MealActivity;
import com.eosinfotech.restaurantcustomerui.Onboarding.AhoyOnboarderActivity;
import com.eosinfotech.restaurantcustomerui.Onboarding.AhoyOnboarderCard;
import com.eosinfotech.restaurantcustomerui.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AhoyOnboarderActivity {

    public static final String PREFS_NAME = "Restaurant" ;
    public static boolean isAppRunning;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Delegate.theMainActivity = this;
        Context mContext = MainActivity.this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);


        AhoyOnboarderCard OnboarderCard1 = new AhoyOnboarderCard("DINE IN", "Book a table and order your meal before arrival.", R.drawable.dinin);
        AhoyOnboarderCard  OnboarderCard2 = new AhoyOnboarderCard("FRESHLY", "Discover new dishes everyday and hold your favourite restaurant.", R.drawable.dishes);
        AhoyOnboarderCard  OnboarderCard3 = new AhoyOnboarderCard("EASY ORDERING", "Just a click away from your favourite eats.", R.drawable.easyordering);
        AhoyOnboarderCard  OnboarderCard5 = new AhoyOnboarderCard("TRACKING", "As soon as you hear the sound of the door. Your food has come.", R.drawable.tracking);
        //OnboarderCard OnboarderCard6 = new OnboarderCard("QUICK DELIVERY", "Your food is freshly cooked and delivered to your table.", android.R.drawable.ic_menu_report_image);


            OnboarderCard1.setBackgroundColor(R.color.black_transparent);
            OnboarderCard2.setBackgroundColor(R.color.black_transparent);
            OnboarderCard3.setBackgroundColor(R.color.black_transparent);
            OnboarderCard5.setBackgroundColor(R.color.black_transparent);
            //OnboarderCard6.setBackgroundColor(R.color.black_transparent);

            List<AhoyOnboarderCard> pages = new ArrayList<>();

            pages.add(OnboarderCard1);
            pages.add(OnboarderCard2);
            pages.add(OnboarderCard3);
            pages.add(OnboarderCard5);
            //pages.add(OnboarderCard6);

            for (AhoyOnboarderCard page : pages) {
                page.setTitleColor(R.color.colorBackground);
                page.setDescriptionColor(R.color.grey_300);
                //page.setTitleTextSize(dpToPixels(12, this));
                //page.setDescriptionTextSize(dpToPixels(8, this));
                //page.setIconLayoutParams(width, height, marginTop, marginLeft, marginRight, marginBottom);
            }

            setFinishButtonTitle("Finish");
            showNavigationControls(true);
            setColorBackground(R.color.colorLightBrand);
            //setImageBackground(R.drawable.onboardingbackground);

            /*List<Integer> colorList = new ArrayList<>();
            colorList.add(R.color.colorBackground);
            colorList.add(R.color.colorBackground);
            colorList.add(R.color.colorBackground);

            setColorBackground(colorList);*/

            //set the button style you created
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
            }

            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/AleoLight.otf");
            setFont(face);
            setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent it = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(it);

        /*new MainActivity.SendFcm(MainActivity.this).execute();
        Log.i("REfresh Token", FirebaseInstanceId.getInstance().getToken());*/
    }



    /*private class SendFcm extends AsyncTask<String, String, String> {

        URL url= null;
        HttpURLConnection client = null;

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
            try {
                url= new URL("https://fcm.googleapis.com/fcm/send");
                client = (HttpURLConnection) url.openConnection();
                client.setDoOutput(true);
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestProperty("Authorization", "key=AAAAwXn4q80:APA91bFU-wcSH6rreaze2rXkOuW_zFZ9tcx-b0SPV1wg_EOSE71Xwv9QkAmWm2tpQKACRfkednwKjfs_B_yLYlIUDoWEU9Y9YuXwi8RlOA4C1vN2wrfh3sGF9Y32bb4BRmwf3ykZ1jEv");
                client.connect();


                final JSONObject payload =new JSONObject();
                *//*payload.put("body",news.text);
                payload.put("title",news.title);*//*
                payload.put("title","Hey Neelam");
                payload.put("body","Your order has been picked up from EOS Infotech and you deliver is on the way!");

                JSONObject notif = new JSONObject();
                notif.put("to", FirebaseInstanceId.getInstance().getToken());
                notif.put("notification", payload);

                OutputStream outputPost = client.getOutputStream();
                outputPost.write(notif.toString().getBytes("UTF-8"));
                outputPost.flush();
                outputPost.close();

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

        }
    }*/
}
