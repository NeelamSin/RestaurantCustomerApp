package com.eosinfotech.restaurantcustomerui.CredentialActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.OwnClasses.CustomEditText;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    CustomEditText userName, pasword;
    ProgressDialog progressDialog;

    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;
    int doubleBackToExitPressed = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        LinearLayout plsSignUp = (LinearLayout) findViewById(R.id.pleasesignup);
        plsSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoNext = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(gotoNext);
            }
        });

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        LinearLayout signUpPlease = (LinearLayout) findViewById(R.id.pleasesignup);
        signUpPlease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAcc = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(createAcc);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        userName = (CustomEditText) findViewById(R.id.userName);
        pasword = (CustomEditText) findViewById(R.id.userPassword);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(userName.getText().toString().equals("")) && !(pasword.toString().equals(""))) {
                    new Login(LoginActivity.this).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Username/Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class Login extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        boolean result=true;
        String companyid="";
        String menuid="";
        String userloginid="";
        String userdisplayname="";


        public Login(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Signing In...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.loginCustomerUrl);
                OkHttpClient client = new OkHttpClient();

                byte[] data = (pasword.getText().toString()).getBytes("UTF-8");
                /*Base64.encodeToString(data, Base64.NO_WRAP)*/


                JSONObject jsonObject=new JSONObject();
                jsonObject.put("username",userName.getText().toString());
                jsonObject.put("usersecret",Base64.encodeToString(data, Base64.NO_WRAP));

                //jsonObject.put("usersecret",pasword.getText().toString());
                jsonObject.put("clientId",getResources().getString(R.string.clientId));

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                int status=response.code();

                Log.i("Status","_"+jsonObject.toString());
                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());

                    Log.i("Res","_"+responseJsonObject.toString());


                    if(responseJsonObject.getBoolean("error")==false)
                    {
                        if(responseJsonObject.get("customer_id") instanceof String)
                        {
                            result = true;

                        }
                        else
                        {
                            result = false;
                            JSONArray jsonArray_validUserDetails = responseJsonObject.getJSONArray("customer_id");
                            JSONObject jsonObject_validUserDetails=jsonArray_validUserDetails.getJSONObject(0);
                            companyid = jsonObject_validUserDetails.getString("company_id");
                            userloginid = jsonObject_validUserDetails.getString("customer_id");
                            userdisplayname = jsonObject_validUserDetails.getString("display_name");
                        }
                    }
                    else
                    {
                        result=true;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();

            if(result==true)
            {
                Toast.makeText(getApplicationContext(),"No User Registered with this credentials",Toast.LENGTH_SHORT).show();
            }
            else {
                Log.i("Userid","_"+userloginid);
                Log.i("companyid","_"+companyid);
                Log.i("displayusername" , "_"+userdisplayname);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("userid", userloginid);
                editor.putString("companyid", companyid);
                editor.putString("displayusername" , userdisplayname);
                editor.putString("phoneno" , userName.getText().toString());
                editor.commit();

                new FetchSetting(LoginActivity.this).execute(companyid);
            }
        }
    }

    private class FetchSetting extends AsyncTask<String, String, String> {
        private Context mContext;
        String upi="",restaurantname="",ImagePath="", deliveryCost="";

        public FetchSetting(Context context) {
            this.mContext = context;
        }

        @Override
        protected String  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.settingsUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId",params[0]);
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
                            ImagePath=jsonObject1.getString("field_value");
                        }
                        else if(jsonObject1.getString("fieldname").equals("ImagePath"))
                        {
                            ImagePath=jsonObject1.getString("field_value");
                        }
                        else if(jsonObject1.getString("fieldname").equals("Delivery_Charges"))
                        {
                            deliveryCost = jsonObject1.getString("field_value");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return upi;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);

            progressDialog.dismiss();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("imagepath",ImagePath );
            editor.putString("deliverycost" , deliveryCost);
            editor.putString("restaurantname",restaurantname );
            editor.commit();

            Intent intentToast = new Intent(LoginActivity.this, SuccessFullActivity.class);
            startActivity(intentToast);
        }
    }


    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(),"We're working to make things right...",Toast.LENGTH_SHORT).show();
        if (doubleBackToExitPressed == 2) {
            finishAffinity();
            System.exit(0);
        }
        else {
            doubleBackToExitPressed++;
            Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressed=1;
            }
        }, 2000);
    }
}
