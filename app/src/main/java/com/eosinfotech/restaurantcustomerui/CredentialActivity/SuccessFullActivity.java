package com.eosinfotech.restaurantcustomerui.CredentialActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.AsyncTask.TableBookingStatus;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.LauncherActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.Models.ItemRating;
import com.eosinfotech.restaurantcustomerui.Models.TableCategory;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.TableConfirmActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SuccessFullActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    BoldTextView name;
    String isConfirmed="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_success_full);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        ImageView logoImg = (ImageView) findViewById(R.id.mainLogo) ;
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        logoImg.startAnimation(animation);

        name=(BoldTextView) findViewById(R.id.name);
        name.setText(""+sharedpreferences.getString("restaurantname",null));
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Intent intent=new Intent(SuccessFullActivity.this,BookingTypeActivity.class);
                startActivity(intent);
                finish();*/
                new AddFcmDetails(SuccessFullActivity.this).execute();
            }
        },5000);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"We're working to make things right...",Toast.LENGTH_SHORT).show();
    }


    private class FetchSetting extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        String upi="",restaurantname="",imagePath="";

        public FetchSetting(Context context) {
            this.mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
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

                        if(jsonObject1.getString("fieldname").equals("mobile_number"))
                        {
                            upi=jsonObject1.getString("field_value");
                        }
                        else if(jsonObject1.getString("fieldname").equals("company_name"))
                        {
                            restaurantname=jsonObject1.getString("field_value");
                        }
                        else if(jsonObject1.getString("fieldname").equals("ImagePath"))
                        {
                            imagePath=jsonObject1.getString("field_value");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.i("image", "_"+imagePath);
            //name.setText("B O U F F A G E !! : "+restaurantname);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("imagePathUrl", imagePath);
            editor.commit();

            new GetCompany(SuccessFullActivity.this).execute();

        }
    }

    private class GetCompany extends AsyncTask<String, String, String> {
        private Context mContext;
        String companyidGetting="";

        public GetCompany(Context context) {
            this.mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.getAllCompanyUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
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
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    JSONArray jsonArray=responseJsonObject.getJSONArray("companyDetails");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        companyidGetting=jsonObject1.getString("Firebase_APIKey");
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return companyidGetting;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);
            Log.i("CompAPI", "_"+companyidGetting);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("firebaseAPIKey", companyidGetting);
            editor.commit();

            //companyidTextView.setText(companyidGetting);

            new FetchTable(SuccessFullActivity.this).execute();


        }
    }

    private class FetchTable extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        String tableid="";
        String customerid="";
        String staffid="";
        public FetchTable(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"No Tables found",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchtableUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
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

                Date cDate = new Date();
                String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
                String userid=sharedpreferences.getString("userid",null);

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    if(!(responseJsonObject.get("tabledetails") instanceof String))
                    {
                        JSONArray tabledetailsJsonArray=responseJsonObject.getJSONArray("tabledetails");
                        for(int i=0;i<tabledetailsJsonArray.length();i++)
                        {
                            Log.i("Status","_"+tabledetailsJsonArray.getJSONObject(i));
                            JSONObject tabledetailsJsonObject=tabledetailsJsonArray.getJSONObject(i);
                            if(tabledetailsJsonObject.getString("booking_date").equals(fDate)
                                    && tabledetailsJsonObject.getString("customer_id").equals(userid)
                                    && (!(tabledetailsJsonObject.getString("StaffDtlsid").equals("0")) || !(tabledetailsJsonObject.getString("StaffDtlsid").equals(""))))
                            {
                                tableid=tabledetailsJsonObject.getString("tableid");
                            }
                            staffid=tabledetailsJsonObject.getString("StaffDtlsid");
                        }
                    }
                    else
                    {
                        publishProgress("");
                    }
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
            Log.i("tableid","_"+tableid);

            if(!(tableid.equals("")))
            {
                if(!(staffid.equals("")) && !(staffid.equals("0")))
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableid", tableid);
                    editor.putString("tableconfirmed", "yes");
                    editor.putString("ordertype","Dine In");
                    editor.commit();

                    Intent gotoNext = new Intent(SuccessFullActivity.this , DashboardActivity.class);
                    startActivity(gotoNext);
                    finish();
                }
                else {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableid", tableid);
                    //editor.putString("tableconfirmed", "yes");
                    editor.putString("ordertype", "Dine In");
                    editor.commit();

                    Intent gotoNext = new Intent(SuccessFullActivity.this, TableConfirmActivity.class);
                    startActivity(gotoNext);
                    finish();
                }
            }
            else
            {
               /* Intent gotoNext = new Intent(SuccessFullActivity.this , TableCategoryActivity.class);
                startActivity(gotoNext);
                finish();*/

               try{
                   isConfirmed = new TableBookingStatus(SuccessFullActivity.this).execute("", null).get();

                   Log.i("Status","__"+isConfirmed);

                   if (isConfirmed.equals("Confirmed"))
                   {
                       SharedPreferences.Editor editor = sharedpreferences.edit();
                       editor.putString("tableconfirmed", "confirmed");
                       editor.commit();

                       Intent gotoNext = new Intent(SuccessFullActivity.this , DashboardActivity.class);
                       startActivity(gotoNext);
                       finish();
                   }
                   else  if (isConfirmed.equals("Requested"))
                   {
                       SharedPreferences.Editor editor = sharedpreferences.edit();
                       editor.putString("ordertype", "Dine In");
                       editor.putString("tableconfirmed", "requested");
                       editor.commit();

                       Intent intentToast = new Intent(SuccessFullActivity.this, TableConfirmActivity.class);
                       startActivity(intentToast);
                   }
                   else  if (isConfirmed.equals("Accepted"))
                   {
                       SharedPreferences.Editor editor = sharedpreferences.edit();
                       editor.putString("ordertype", "Dine In");
                       editor.putString("tableconfirmed", "accepted");
                       editor.commit();

                       Intent intentToast = new Intent(SuccessFullActivity.this, TableConfirmActivity.class);
                       startActivity(intentToast);
                   }
                   else  if (isConfirmed.equals("Rejected"))
                   {
                       SharedPreferences.Editor editor = sharedpreferences.edit();
                       editor.putString("ordertype", "Dine In");
                       editor.putString("tableconfirmed", "rejected");
                       editor.commit();

                       Intent intentToast = new Intent(SuccessFullActivity.this, TableCategoryActivity.class);
                       startActivity(intentToast);
                   }
                   else  if (isConfirmed.equals(""))
                   {

                       Intent intentToast = new Intent(SuccessFullActivity.this, TableCategoryActivity.class);
                       startActivity(intentToast);
                   }
                   else  if (isConfirmed.equals("New"))
                   {
                       SharedPreferences.Editor editor = sharedpreferences.edit();
                       editor.putString("ordertype", "Dine In");
                       editor.putString("tableconfirmed", "requested");
                       editor.commit();
                       Intent intentToast = new Intent(SuccessFullActivity.this, TableConfirmActivity.class);
                       startActivity(intentToast);
                   }
               }
               catch(Exception e)
               {
                   e.printStackTrace();
               }

            }
        }
    }



    private class AddFcmDetails extends AsyncTask<String, String, Boolean> {
        private Context mContext;

        public AddFcmDetails(Context context) {
            this.mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.addFcmDetailsUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("company_id",sharedpreferences.getString("companyid",null));
                jsonObject.put("user_type","Customer");
                jsonObject.put("user_key",sharedpreferences.getString("userid",null));
                jsonObject.put("fcm_token", FirebaseInstanceId.getInstance().getToken());
                jsonObject.put("created_by",sharedpreferences.getString("userid", null));
                jsonObject.put("creation_date",fDate);
                jsonObject.put("updated_by",sharedpreferences.getString("userid", null));
                jsonObject.put("updated_date",fDate);
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Status For FCM", "_" + status);
                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.i("Add Fcm Detail","added Successfully");

            new FetchSetting(SuccessFullActivity.this).execute();

        }





    }




}
