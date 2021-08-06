package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.eosinfotech.restaurantcustomerui.Adapters.AddAddressAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.PastOrderAdapter;
import com.eosinfotech.restaurantcustomerui.CredentialActivity.SignUpActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.GetCurrentLocation;
import com.eosinfotech.restaurantcustomerui.Models.AddAddress;
import com.eosinfotech.restaurantcustomerui.Models.PastOrder;
import com.eosinfotech.restaurantcustomerui.OwnClasses.CustomEditText;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.GettingUserLocation;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener{

    private RegularTextView userCurrentLoc, exactLocation, statusText;
    private CustomEditText phoneNumber , completeAddress;
    private Button homeButton, officeButton, othersButton, addAddressButton;
    GetCurrentLocation currentLoc;
    private String latitude, longitude;
    List<Address> addresses;
    Geocoder geocoder;
    String address, city, state, postalCode, country;
    Double needLatitude, needLongitude;

    ProgressDialog progressDialog;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;


    private List<com.eosinfotech.restaurantcustomerui.Models.AddAddress> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AddAddressAdapter mAdapter;

    private AwesomeValidation awesomeValidation;
    private String editString , editCompleteAddress, editExactAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        awesomeValidation = new AwesomeValidation(ValidationStyle.COLORATION);
        awesomeValidation.setColor(getResources().getColor(R.color.colorHeadText));


        awesomeValidation.addValidation(this, R.id.completeAddress, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentLoc = new GetCurrentLocation(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Addresses");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorHeadText));
        mToolbar.setNavigationIcon(R.drawable.ic_back_button);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent dashboardReturn = new Intent(AddAddressActivity.this, ViewCartActivity.class);
                startActivity(dashboardReturn);*/
                finish();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new AddAddressAdapter(movieList,AddAddressActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new AddAddressActivity.GetCustomerContacts(AddAddressActivity.this).execute();


        statusText = (RegularTextView) findViewById(R.id.statusText);
        homeButton = (Button) findViewById(R.id.test1_button);
        officeButton = (Button) findViewById(R.id.test2_button);
        othersButton = (Button) findViewById(R.id.test3_button);
        homeButton.setOnClickListener(this);
        officeButton.setOnClickListener(this);
        othersButton.setOnClickListener(this);


        addAddressButton = (Button) findViewById(R.id.addAddressButton);
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editString = exactLocation.getText().toString();
                editCompleteAddress = completeAddress.getText().toString();
                editExactAddress = exactLocation.getText().toString();
                if(TextUtils.isEmpty(editString) || TextUtils.isEmpty(editCompleteAddress) || TextUtils.isEmpty(editExactAddress)) {
                    Toast.makeText(AddAddressActivity.this, "please fields cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                        new AddAddressActivity.AddAddress(AddAddressActivity.this).execute();
                    }
                }
        });

        exactLocation = (RegularTextView) findViewById(R.id.exactLocation);
        phoneNumber = (CustomEditText) findViewById(R.id.phoneNumber);
        completeAddress = (CustomEditText) findViewById(R.id.completeAddress);
        userCurrentLoc = (RegularTextView) findViewById(R.id.useCurrentLocation);
        userCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                latitude = currentLoc.latitude;
                longitude = currentLoc.longitude;

                if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                    latitude = "0.00";
                    longitude = "0.00";
                }

                Log.i("Latitude", latitude);
                Log.i("Longitude", longitude);

                Double gotLati = Double.parseDouble(latitude);
                Double gotLong = Double.parseDouble(longitude);

                geocoder = new Geocoder(AddAddressActivity.this , Locale.getDefault());
                StringBuilder sb = new StringBuilder();
                try {
                    addresses = geocoder.getFromLocation(gotLati, gotLong, 1);

                    if (addresses != null && addresses.size() > 0) {
                        Address addresse = addresses.get(0);
                        if (sb.append(addresse.getAddressLine(0)) != null) {
                            address = addresses.get(0).getAddressLine(0);
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();
                            postalCode = addresses.get(0).getPostalCode();
                            needLatitude = addresses.get(0).getLatitude();
                            needLongitude = addresses.get(0).getLongitude();
                        }
                    }

                    String fullAddress = address;
                    if (fullAddress != null && !fullAddress.isEmpty()) {
                        Log.i("Address", fullAddress);
                        exactLocation.setText(fullAddress);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        currentLoc.connectGoogleApi();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentLoc.disConnectGoogleApi();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test1_button){
            homeButton.setSelected(true);
            officeButton.setSelected(false);
            othersButton.setSelected(false);
            statusText.setText("Home");
        }
        else if (v.getId() == R.id.test2_button){
            homeButton.setSelected(false);
            officeButton.setSelected(true);
            othersButton.setSelected(false);
            statusText.setText("Work");
        } else{
            homeButton.setSelected(false);
            officeButton.setSelected(false);
            othersButton.setSelected(true);
            statusText.setText("Others");
        }
    }


    private class AddAddress extends AsyncTask<String, String, Boolean> {

        private Context mContext;
        boolean result = false;

        public AddAddress(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddAddressActivity.this);
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
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.addCustomerContactsUrl);
                Log.i("url", "_" + url);

                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("customer_id", sharedpreferences.getString("userid",null));
                jsonObject.put("company_id", sharedpreferences.getString("companyid",null));
                jsonObject.put("phone_number", phoneNumber.getText().toString());
                jsonObject.put("latitude", needLatitude);
                jsonObject.put("longitude", needLongitude);
                jsonObject.put("email", "");
                jsonObject.put("type", statusText.getText().toString());
                jsonObject.put("address_line_1", editString);
                jsonObject.put("address_line_2", completeAddress.getText().toString());
                jsonObject.put("city", city);
                jsonObject.put("state", state);
                jsonObject.put("postal_code", postalCode);
                jsonObject.put("country", country);
                jsonObject.put("status", "1");
                jsonObject.put("created_by", sharedpreferences.getString("userid",null));
                jsonObject.put("creation_date", fDate);
                jsonObject.put("updated_by", sharedpreferences.getString("userid",null));
                jsonObject.put("updatation_date", fDate);

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
            if (result == true) {
                if (awesomeValidation.validate()) {
                    Toast.makeText(getApplicationContext(), "Address Added Successfully", Toast.LENGTH_SHORT).show();
                        Intent itTwo = new Intent(AddAddressActivity.this, ViewCartActivity.class);
                        startActivity(itTwo);
                        finish();
                }
            }
        }
    }


    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }


public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AddAddressActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AddAddressActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }




    private class GetCustomerContacts extends AsyncTask<String, String, Boolean> {
        private Context mContext;

        public GetCustomerContacts(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"No Saved Address",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.getCustomerContactsUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("sessionid",sharedpreferences.getString("companyid",null));
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
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
                    if(!(responseJsonObject.get("contact_id") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("contact_id");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            movieList.add(new com.eosinfotech.restaurantcustomerui.Models.AddAddress(menuResultsJsonObject.getString("type"),menuResultsJsonObject.getString("address_line_1"),menuResultsJsonObject.getString("contact_id")));
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

            mAdapter = new AddAddressAdapter(movieList,AddAddressActivity.this);
            recyclerView.setAdapter(mAdapter);
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
