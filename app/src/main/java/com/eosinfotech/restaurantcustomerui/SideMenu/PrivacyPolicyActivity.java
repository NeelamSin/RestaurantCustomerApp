package com.eosinfotech.restaurantcustomerui.SideMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class PrivacyPolicyActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    LightTextView content_faq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_privacy_policy);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        content_faq=(LightTextView)findViewById(R.id.content_faq);

        new FaqAsyncTask(PrivacyPolicyActivity.this).execute();


    }

    private class FaqAsyncTask extends AsyncTask<String, String, String> {
        private Context mContext;
        String faqcontent="";

        public FaqAsyncTask(Context context) {
            this.mContext = context;
        }


        @Override
        protected String  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.settingsUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Status", "_" + status);
                if (status == 200) {
                    JSONObject responseJsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray=responseJsonObject.getJSONArray("companyDetails");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        if(jsonObject1.getString("fieldname").equals("terms"))
                        {
                            faqcontent=jsonObject1.getString("field_value");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return faqcontent;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);
            content_faq.setText(Html.fromHtml(faqcontent));
        }
    }

}