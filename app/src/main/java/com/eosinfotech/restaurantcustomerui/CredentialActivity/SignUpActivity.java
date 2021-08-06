package com.eosinfotech.restaurantcustomerui.CredentialActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.OwnClasses.CustomEditText;
import com.eosinfotech.restaurantcustomerui.R;
import com.google.common.collect.Range;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    public CustomEditText displayName , userEmail , userPassword, userMobileNumber;
    private RadioGroup radioGroup;
    private RadioButton male, female;
    public Button submitValues;

    ProgressDialog progressDialog;
    private AwesomeValidation awesomeValidation;

    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;
    LightTextView companyidTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        new GetCompany(SignUpActivity.this).execute();

        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.setColor(getResources().getColor(R.color.colorHeadText));

        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.displayName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);
        awesomeValidation.addValidation(this, R.id.mobileNumber, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.userPassword, "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}", R.string.passworderror);

        LinearLayout plsLogIn = (LinearLayout)findViewById(R.id.pleaseLogin);
        plsLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoNext = new Intent(SignUpActivity.this , LoginActivity.class);
                startActivity(gotoNext);
            }
        });

        displayName = (CustomEditText) findViewById(R.id.displayName);
        userEmail = (CustomEditText) findViewById(R.id.displayEmail);
        userPassword = (CustomEditText) findViewById(R.id.userPassword);
        userMobileNumber = (CustomEditText) findViewById(R.id.mobileNumber);
        companyidTextView = (LightTextView) findViewById(R.id.companyuid);

        submitValues = (Button) findViewById(R.id.submitUserValues);
        submitValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Rgeister(SignUpActivity.this).execute();
            }
        });

    }

    private class Rgeister extends AsyncTask<String, String, Boolean> {

        private Context mContext;
        boolean result = false;

        public Rgeister(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignUpActivity.this);
            progressDialog.setMessage("Adding Customer...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.registerUrl);
                Log.i("url", "_" + url);

                byte[] data = (userPassword.getText().toString()).getBytes("UTF-8");

                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("customer_since", fDate);
                jsonObject.put("customer_number", "");
                jsonObject.put("imagepath", "");
                jsonObject.put("DOB", "");
                jsonObject.put("first_name", "");
                jsonObject.put("middle_name", "");
                jsonObject.put("last_name", "");
                jsonObject.put("display_name", displayName.getText().toString());
                jsonObject.put("password", Base64.encodeToString(data, Base64.NO_WRAP));
                jsonObject.put("status", "1");

                jsonObject.put("reference_by", "");
                jsonObject.put("reference_id", "0");
                jsonObject.put("gender", "");
                jsonObject.put("Notes", "");
                jsonObject.put("Allergy", "");
                jsonObject.put("membershipcard", "NA");
                jsonObject.put("discountper", "0");
                jsonObject.put("billto_id", "0");
                jsonObject.put("created_by", "0");
                jsonObject.put("creation_date", fDate);
                jsonObject.put("updated_by", "0");
                jsonObject.put("updation_date", fDate);
                jsonObject.put("company_id", companyidTextView.getText().toString());
                jsonObject.put("phone_number", userMobileNumber.getText().toString());
                jsonObject.put("phone_number1", "");
                jsonObject.put("phone_number2", "");
                jsonObject.put("phone_number3", "");
                jsonObject.put("phone_number4", "");

                jsonObject.put("email", userEmail.getText().toString());
                jsonObject.put("address_line_1", "");
                jsonObject.put("address_line_2", "");
                jsonObject.put("city", "");
                jsonObject.put("state", "");
                jsonObject.put("postal_code", "");
                jsonObject.put("country", "");
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
                Log.i("Request", "_" + jsonObject.toString());

                if (status == 200) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();

            if (result == false) {
                Toast.makeText(getApplicationContext(), "Could not create account", Toast.LENGTH_SHORT).show();
            } else if (result == true) {
                if (awesomeValidation.validate()) {
                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    new Login(SignUpActivity.this).execute();
                    /*Intent itTwo = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(itTwo);
                    finish();*/
                }
            }
        }
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

            progressDialog = new ProgressDialog(SignUpActivity.this);
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

                byte[] data = (userPassword.getText().toString()).getBytes("UTF-8");
                /*Base64.encodeToString(data, Base64.NO_WRAP)*/


                JSONObject jsonObject=new JSONObject();
                jsonObject.put("username",userMobileNumber.getText().toString());
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
                editor.putString("phoneno" , userMobileNumber.getText().toString());

                editor.commit();

                new SignUpActivity.FetchSetting(SignUpActivity.this).execute(companyid);
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

            Intent intentToast = new Intent(SignUpActivity.this, SuccessFullActivity.class);
            startActivity(intentToast);
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
                         companyidGetting=jsonObject1.getString("companyid");
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
            Log.i("CompanyId", "_"+companyidGetting);
            companyidTextView.setText(companyidGetting);

        }
    }
}
