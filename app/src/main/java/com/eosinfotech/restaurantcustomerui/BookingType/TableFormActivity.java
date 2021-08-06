package com.eosinfotech.restaurantcustomerui.BookingType;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.OwnClasses.CustomEditText;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.TableConfirmActivity;
import com.google.common.collect.Range;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class TableFormActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;
    private CustomEditText fullName, mobileNumber, numberOfGuests, groupBookings, bookingDate, bookingTime;
    BoldTextView book;

    private AwesomeValidation awesomeValidation;
    ArrayList<String> tokenarraylist;
    String date_str;
    TimePickerDialog picker;
    DatePickerDialog pickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table_form);
        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.setColor(getResources().getColor(R.color.colorHeadText));
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        fullName = (CustomEditText) findViewById(R.id.fullName);
        mobileNumber = (CustomEditText) findViewById(R.id.mobileNumber);
        numberOfGuests = (CustomEditText) findViewById(R.id.numberOfGuest);
        groupBookings = (CustomEditText) findViewById(R.id.groupBooking);
        bookingDate = (CustomEditText) findViewById(R.id.bookingDate);
        bookingTime = (CustomEditText) findViewById(R.id.bookingTime);
        bookingTime.setInputType(InputType.TYPE_NULL);
        bookingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(TableFormActivity.this, R.style.DialogTheme ,new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                bookingTime.setText(sHour + ":" + sMinute + ":00");
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        bookingDate.setInputType(InputType.TYPE_NULL);
        bookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                pickers = new DatePickerDialog(TableFormActivity.this,  R.style.DialogTheme ,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        StringBuilder date = new StringBuilder();
                        date.delete(0, date.length());
                        date.append(year)
                                .append("-")
                                .append((monthOfYear + 1) < 10 ? "0" : "")
                                .append((monthOfYear + 1))
                                .append("-")
                                .append((dayOfMonth < 10 ? "0" : ""))
                                .append(dayOfMonth);

                              bookingDate.setText(date.toString());
                            //bookingDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                pickers.getDatePicker().setMinDate(System.currentTimeMillis());
                pickers.getDatePicker().setMaxDate(System.currentTimeMillis() + 2*24*60*60*1000l);
                pickers.show();
            }
        });


        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.fullName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.mobileNumber, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.bookingDate, "^(([0-9][0-9])?[0-9][0-9])-(0[0-9]||1[0-2])-([0-2][0-9]||3[0-1])$", R.string.dateerror);
        awesomeValidation.addValidation(this, R.id.bookingTime, "^(([0-9])|([0-1][0-9])|([2][0-3])):(([0-9])|([0-5][0-9])):(([0-9])|([0-5][0-9]))$", R.string.timeerror);
        awesomeValidation.addValidation(this, R.id.numberOfGuest, Range.closed(1, 99), R.string.numberOfGuest);


        book = (BoldTextView) findViewById(R.id.book);
        book.setOnClickListener(this);
    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        if (awesomeValidation.validate()) {
            //Toast.makeText(this, "Validation Successfull", Toast.LENGTH_LONG).show();
            //Context context=TableFormActivity.this;
            new TableFormActivity.AddTableRequest(TableFormActivity.this).execute();
        }
    }
    @Override
    public void onClick(View view) {
        if (view == book) {
            submitForm();
        }
    }


    private class AddTableRequest extends AsyncTask<String, String, Boolean>
    {
        boolean error=true;

        private Context mContext;
        public AddTableRequest(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"Restaurant Busy",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.addTableReservationUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("clientId",getResources().getString(R.string.clientId));
                jsonObject.put("company_id",sharedpreferences.getString("companyid",null));
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
                jsonObject.put("Guest_name",fullName.getText().toString());
                jsonObject.put("mobile_number",mobileNumber.getText().toString());
                jsonObject.put("NewCustomer","N");
                jsonObject.put("table_number","0");
                jsonObject.put("group_booking",groupBookings.getText().toString());
                jsonObject.put("number_of_guests",numberOfGuests.getText().toString());
                jsonObject.put("booking_date",bookingDate.getText().toString());
                jsonObject.put("booking_time",fDate +" " +bookingTime.getText().toString());
                jsonObject.put("special_instruction","");
                jsonObject.put("creation_date",fDate);
                jsonObject.put("created_by",sharedpreferences.getString("userid",null));
                jsonObject.put("updated_date",fDate);
                jsonObject.put("updated_by",sharedpreferences.getString("userid",null));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("AddtableStatus","_"+status);
                Log.i("tabel JsOn", "_"+jsonObject.toString());
                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    error=responseJsonObject.getBoolean("error");

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return error;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean==false)
            {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("tableconfirmed", "requested");
                editor.commit();

                    tokenarraylist=new ArrayList<>();
                    new TableFormActivity.FetchFCMDetailedToKitchen(TableFormActivity.this).execute();
            }
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
                jsonObject.put("user_type","Restaurant");

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
                new TableFormActivity.SendFcm(TableFormActivity.this).execute(tokenarraylist.get(i),""+i);
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
                payload.put("title","Bouffage");
                payload.put("body","New Table Request");

                JSONObject notif = new JSONObject();
                notif.put("to", params[0]);
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
            Intent gotoNext = new Intent(TableFormActivity.this , TableConfirmActivity.class);
            startActivity(gotoNext);
            finish();

        }
    }
}
