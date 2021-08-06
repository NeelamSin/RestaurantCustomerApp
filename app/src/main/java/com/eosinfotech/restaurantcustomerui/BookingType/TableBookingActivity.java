package com.eosinfotech.restaurantcustomerui.BookingType;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.PastOrderAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.TableBookingAdapter;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.PastOrderActivity;
import com.eosinfotech.restaurantcustomerui.Models.PastOrder;
import com.eosinfotech.restaurantcustomerui.Models.Tablebooking;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.TableConfirmActivity;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class TableBookingActivity extends AppCompatActivity {

    TableBookingAdapter mAdapter;
    private ArrayList<String> listCountry;
    private ArrayList<Integer> listFlag;
    private ArrayList<String> listTableNumber;
    private ArrayList<String> listTableStatus;
    private GridView gridView;
    ProgressDialog progressDialog;

    public RegularTextView selectedDate, selectedTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public TimePickerDialog timePickerDialog;
    public Button chooseDate,chooseTime;
    Dialog dialog;

    ArrayList<String> tokenarraylist;


    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table_booking);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prepareList();

        //mAdapter = new TableBookingAdapter(TableBookingActivity.this, gridViewImageId,gridTableStatus,gridTableSeater, gridTableNumber);
        gridView=(GridView)findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                
            }
        });
    }

    public void prepareList()
    {
        listCountry = new ArrayList<String>();
        listFlag = new ArrayList<Integer>();
        listTableNumber = new ArrayList<String>();
        listTableStatus = new ArrayList<String>();
        new FetchTable(TableBookingActivity.this).execute();
    }

    @Override
    public void onBackPressed() {
    }


    private class FetchTable extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        public FetchTable(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TableBookingActivity.this);
            progressDialog.setMessage("Fetching Tables...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
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
                            if(tabledetailsJsonObject.getString("booking_date").equals("0000-00-00") || tabledetailsJsonObject.getString("booking_date").equals(""))
                            {
                                listCountry.add("Empty");
                                listFlag.add(R.drawable.table_empty);
                            }
                            else
                            {
                                if(tabledetailsJsonObject.getString("StaffDtlsid").equals("0"))
                                {
                                    listCountry.add("Reserved");
                                    listFlag.add(R.drawable.table_reserved);
                                }
                                else
                                {
                                    listCountry.add("Occupied");
                                    listFlag.add(R.drawable.table_occupied);
                                }
                            }
                            //listCountry.add("Empty");
                            listTableNumber.add(tabledetailsJsonObject.getString("tableno"));
                            listTableStatus.add(tabledetailsJsonObject.getString("tablename"));
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
            progressDialog.dismiss();
            mAdapter = new TableBookingAdapter(TableBookingActivity.this,listCountry, listFlag,listTableNumber,listTableStatus);
            // Set custom adapter to gridview
            gridView = (GridView) findViewById(R.id.gridView1);
            gridView.setAdapter(mAdapter);
            // Implement On Item click listener
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
                                        long arg3) {

                    if (listCountry.get(position).equals("Empty")) {
                        /*

                        dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.date_time_dialog_box);

                        Calendar currentCalendar = Calendar.getInstance();
                        String todaysCurrentDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentCalendar.getTime());
                        BoldTextView selectedTodayDate = (BoldTextView) dialog.findViewById(R.id.selectDate);
                        selectedTodayDate.setText(""+todaysCurrentDate);
                        selectedDate = (RegularTextView) dialog.findViewById(R.id.selectedDate);
                        selectedTime = (RegularTextView) dialog.findViewById(R.id.selectedTime);
                        chooseDate = (Button) dialog.findViewById(R.id.chooseDate);
                        chooseTime = (Button) dialog.findViewById(R.id.chooseTime);

                        chooseDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Get Current Date
                                final Calendar c = Calendar.getInstance();
                                mYear = c.get(Calendar.YEAR);
                                mMonth = c.get(Calendar.MONTH);
                                mDay = c.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(TableBookingActivity.this,
                                        new DatePickerDialog.OnDateSetListener() {

                                            @Override
                                            public void onDateSet(DatePicker view, int year,
                                                                  int monthOfYear, int dayOfMonth) {

                                                selectedDate.setText(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth);

                                            }
                                        }, mYear, mMonth, mDay);
                                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                                datePickerDialog.show();
                            }
                        });
                        dialog.show();
                        chooseTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Get Current Time
                                final Calendar c = Calendar.getInstance();
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(TableBookingActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {

                                                selectedTime.setText(hourOfDay + ":" + minute);
                                                new BookTable(TableBookingActivity.this).execute(listTableNumber.get(position),selectedDate.getText().toString(),selectedTime.getText().toString());
                                                //dialog.dismiss();
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        });
                        dialog.show();
                        */

                        new BookTable(TableBookingActivity.this).execute(listTableNumber.get(position),"","");

                    }
                }
            });
        }
    }


    private class BookTable extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        String tableid="0";

        public BookTable(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(TableBookingActivity.this);
            progressDialog.setMessage("Booking Table...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"Table could not be booked",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            tableid=params[0];
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
            String fDate1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);

            String space=fDate+"\t"+params[2];
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.addTableReservationUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("clientId",getResources().getString(R.string.clientId));
                jsonObject.put("company_id",sharedpreferences.getString("companyid",null));
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
                jsonObject.put("Guest_name",sharedpreferences.getString("displayusername",null));
                jsonObject.put("mobile_number",sharedpreferences.getString("phoneno",null));
                jsonObject.put("NewCustomer","N");
                jsonObject.put("table_number",params[0]);
                jsonObject.put("group_booking","N");
                jsonObject.put("number_of_guests","");
                jsonObject.put("booking_date",fDate);
                jsonObject.put("booking_time",fDate1);
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
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            //dialog.dismiss();
            /*SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tableid", tableid);
            editor.commit();

            Intent gotoNext = new Intent(TableBookingActivity.this , TableConfirmActivity.class);
            startActivity(gotoNext);
            finish();*/

            if(aBoolean==false)
            {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("tableconfirmed", "requested");
                editor.commit();

                tokenarraylist=new ArrayList<>();
                new TableBookingActivity.FetchFCMDetailedToKitchen(TableBookingActivity.this).execute();
            }
        }
    }



    private class FetchFCMDetailedToKitchen extends AsyncTask<String, String, String> {

        String kitchenToken = "";
        private Context mContext;

        public FetchFCMDetailedToKitchen(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.fetchFcmDetailsUrl);
                OkHttpClient client = new OkHttpClient();
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("companyId", sharedpreferences.getString("companyid", null));
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("user_type", "Restaurant");

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("FCMToken_Status", "_" + status);
                Log.i("FCMToken_Req", "_" + jsonObject.toString());

                if (status == 200) {
                    JSONObject responseJsonObject = new JSONObject(response.body().string());
                    Log.i("FCMToken_Res", "_" + responseJsonObject.toString());


                    JSONArray menuResultsJsonArray = responseJsonObject.getJSONArray("Tokendetails");
                    for (int i = 0; i < menuResultsJsonArray.length(); i++) {
                        JSONObject menuResultsJsonObject = menuResultsJsonArray.getJSONObject(i);

                        kitchenToken = menuResultsJsonObject.getString("fcm_token");
                        Log.i("FCMToken_token", "_" + kitchenToken);
                        tokenarraylist.add(kitchenToken);

                    }

                }
            } catch (Exception e) {
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

            for (int i = 0; i < tokenarraylist.size(); i++) {
                new TableBookingActivity.SendFcm(TableBookingActivity.this).execute(tokenarraylist.get(i), "" + i);
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
            Intent gotoNext = new Intent(TableBookingActivity.this , TableConfirmActivity.class);
            startActivity(gotoNext);
            finish();

        }
    }
}
