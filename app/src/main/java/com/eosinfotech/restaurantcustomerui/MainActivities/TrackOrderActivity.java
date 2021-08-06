package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.eosinfotech.restaurantcustomerui.Adapters.TimeLineAdapter;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.DeliveryGmaps.DrawMarker;
import com.eosinfotech.restaurantcustomerui.DeliveryGmaps.DrawRouteMaps;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderFailureActivity;
import com.eosinfotech.restaurantcustomerui.Utils.OrderStatus;
import com.eosinfotech.restaurantcustomerui.Utils.Orientation;
import com.eosinfotech.restaurantcustomerui.Utils.TimeLineModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;


    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();

    String order_current_status="";
    String orderno="";
    RegularTextView orderno_TextView;

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        public void run() {
            Log.d("Runnable","Handler is working");
            try {
                try {
                    order_current_status = new FetchOpenOrdercust(TrackOrderActivity.this).execute().get();
                    initView();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                if(order_current_status.equals("Completed"))
                {
                    handler.removeCallbacks(this);
                    Log.d("Runnable","ok");
                }
                else
                {
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
        setContentView(R.layout.activity_track_order);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Past Orders");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorHeadText));
        mToolbar.setNavigationIcon(R.drawable.ic_back_button);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent dashboardReturn = new Intent(TrackOrderActivity.this, DashboardActivity.class);
                startActivity(dashboardReturn);*/
                finish();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        orderno_TextView =(RegularTextView) findViewById(R.id.b_orderMeal_cancel) ;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);

        Button orderstatusbtn=(Button)findViewById(R.id.orderstatusbtn);
        orderstatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    private void initView() {
        mDataList.clear();
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, Orientation.VERTICAL, true);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private void setDataListItems(){
        String status_new="";
        String status_confirmed="";
        String status_inprogress="";
        String status_completed="";

        if(order_current_status.equals("New"))
        {
            mDataList.add(new TimeLineModel("ORDER PLACED", "we have recieved your order.", OrderStatus.ACTIVE, R.drawable.orderplaced));
            mDataList.add(new TimeLineModel("ORDER CONFIRMED", "your order has been confirmed.", OrderStatus.INACTIVE, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("IN KITCHEN", "your delicious food is being prepared.", OrderStatus.INACTIVE, R.drawable.order_confirmed));
            //mDataList.add(new TimeLineModel("FOOD IS PACKED", "delivery boy collecting your food.", OrderStatus.ACTIVE, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("ORDER DELIVERED", "your food has been delivered in estimated minutes.", OrderStatus.INACTIVE,R.drawable.ready));
        }
        else if(order_current_status.equals("Confirmed"))
        {
            mDataList.add(new TimeLineModel("ORDER PLACED", "we have recieved your order.", OrderStatus.COMPLETED, R.drawable.orderplaced));
            mDataList.add(new TimeLineModel("ORDER CONFIRMED", "your order has been confirmed.", OrderStatus.ACTIVE, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("IN KITCHEN", "your delicious food is being prepared.", OrderStatus.INACTIVE, R.drawable.order_confirmed));
            //mDataList.add(new TimeLineModel("FOOD IS PACKED", "delivery boy collecting your food.", OrderStatus.ACTIVE, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("ORDER DELIVERED", "your food has been delivered in estimated minutes.", OrderStatus.INACTIVE,R.drawable.ready));
        }
        else if(order_current_status.equals("InProgress"))
        {
            mDataList.add(new TimeLineModel("ORDER PLACED", "we have recieved your order.", OrderStatus.COMPLETED, R.drawable.orderplaced));
            mDataList.add(new TimeLineModel("ORDER CONFIRMED", "your order has been confirmed.", OrderStatus.COMPLETED, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("IN KITCHEN", "your delicious food is being prepared.", OrderStatus.ACTIVE, R.drawable.order_confirmed));
            //mDataList.add(new TimeLineModel("FOOD IS PACKED", "delivery boy collecting your food.", OrderStatus.ACTIVE, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("ORDER DELIVERED", "your food has been delivered in estimated minutes.", OrderStatus.INACTIVE,R.drawable.ready));
        }
        else if(order_current_status.equals("Completed"))
        {
            mDataList.add(new TimeLineModel("ORDER PLACED", "we have recieved your order.", OrderStatus.COMPLETED, R.drawable.orderplaced));
            mDataList.add(new TimeLineModel("ORDER CONFIRMED", "your order has been confirmed.", OrderStatus.COMPLETED, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("IN KITCHEN", "your delicious food is being prepared.", OrderStatus.COMPLETED, R.drawable.order_confirmed));
            //mDataList.add(new TimeLineModel("FOOD IS PACKED", "delivery boy collecting your food.", OrderStatus.ACTIVE, R.drawable.order_confirmed));
            mDataList.add(new TimeLineModel("ORDER DELIVERED", "your food has been delivered in estimated minutes.", OrderStatus.ACTIVE,R.drawable.ready));
        }
       /* mDataList.add(new TimeLineModel("Order Placed", "We have recieved your order", OrderStatus.COMPLETED, R.drawable.orderplaced));
        mDataList.add(new TimeLineModel("Order Confirmed", "Your order has been confirmed", OrderStatus.COMPLETED, R.drawable.order_confirmed));
        mDataList.add(new TimeLineModel("In Kitchen", "Your delicious food is being prepared", OrderStatus.ACTIVE, R.drawable.orderplaced));
        mDataList.add(new TimeLineModel("Ready To Pickup", "Our executive started to deliver your food",OrderStatus.INACTIVE,R.drawable.order_confirmed));
        mDataList.add(new TimeLineModel("Order Delivered", "Your food has been delivered in estimated minutes", OrderStatus.INACTIVE,R.drawable.ready));*/
    }

    @Override
    public void onBackPressed() {
        /*Intent backPressed = new Intent(TrackOrderActivity.this , DashboardActivity.class);
        startActivity(backPressed);*/
        finish();
    }


    private class FetchOpenOrdercust extends AsyncTask<String, String, String> {
        private Context mContext;
        String order_status="";

        public FetchOpenOrdercust(Context context) {
            this.mContext = context;
        }

        @Override
        protected String  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.FetchOpenOrdercustUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));

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
                    JSONArray jsonArray=responseJsonObject.getJSONArray("menuItems");
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);
                    order_status=jsonObject1.getString("order_status");
                    orderno=jsonObject1.getString("order_number");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return order_status;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            orderno_TextView.setText(orderno);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_silver_sparse));
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        enableCurrentLocationButton();
        LatLng origin = new LatLng(17.4587854, 78.3858261);
        LatLng destination = new LatLng(17.4463477, 78.374593);
        DrawRouteMaps.getInstance(this).draw(origin, destination, mMap);
        DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.userlocation, "Origin Location");
        DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.motorcycle, "Destination Location");

        LatLngBounds bounds = new LatLngBounds.Builder().include(origin).include(destination).build();
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 230, 30));
    }


    private void enableCurrentLocationButton() {
        //before further proceed check if google map is null or not because this method is calling after giving permission
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(TrackOrderActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else {
                mMap.getUiSettings().setMyLocationButtonEnabled(true);//enable Location button, if you don't want MyLocationButton set it false
                mMap.setMyLocationEnabled(true);//enable blue dot
            }
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TrackOrderActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(TrackOrderActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
        else {
            ActivityCompat.requestPermissions(TrackOrderActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }
}
