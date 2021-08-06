package com.eosinfotech.restaurantcustomerui.MealModule;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.R;

public class MealPastOrderActivity extends Activity {

    Button btnShowLocation, btnDistance;
    TextView txtLatitude, txtLongitude, manualLatitude, manualLongitude;
    private static final int REQUEST_CODE_PERMISSION = 3;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_past_order);

        btnDistance = (Button) findViewById(R.id.getCount);
        txtLatitude = (TextView) findViewById(R.id.latitudeText);
        txtLongitude = (TextView) findViewById(R.id.longitudeText);
        manualLatitude = (TextView) findViewById(R.id.latitudeManual);
        manualLongitude = (TextView) findViewById(R.id.longitudeManual);
        manualLatitude.setText("17.450500");
        manualLongitude.setText("78.380890");
        txtLatitude.setText("17.437930");
        txtLongitude.setText("78.390040");

        try {
            if (ActivityCompat.checkSelfPermission(MealPastOrderActivity.this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MealPastOrderActivity.this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnShowLocation = (Button) findViewById(R.id.button);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MealPastOrderActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    //txtLatitude.setText(""+latitude);
                    //txtLongitude.setText(""+longitude);
                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });


        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double manualDoubleLat = Double.parseDouble(manualLatitude.getText().toString());
                Double manualDoubleLong = Double.parseDouble(manualLongitude.getText().toString());
                Double doubleLat = Double.parseDouble(txtLatitude.getText().toString());
                Double doubleLong = Double.parseDouble(txtLongitude.getText().toString());
                float R = 6371; // Earth radius in miles
                Double dLat = (manualDoubleLat - doubleLat) * Math.PI / 180;
                Double dLon = (manualDoubleLong - doubleLong) * Math.PI / 180;
                Double lat1 = doubleLat * Math.PI / 180;
                Double lat2 = manualDoubleLat * Math.PI / 180;
                Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
                Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                Double d = (R * c);

                Toast.makeText(getApplicationContext() , "Distance :" +d , Toast.LENGTH_SHORT).show();

                /*Double manualDoubleLat = Double.parseDouble(manualLatitude.getText().toString());
                Double manualDoubleLong = Double.parseDouble(manualLongitude.getText().toString());
                Double doubleLat = Double.parseDouble(txtLatitude.getText().toString());
                Double doubleLong = Double.parseDouble(txtLongitude.getText().toString());

                double distance;
                Location locationA = new Location("Point A");
                locationA.setLatitude(manualDoubleLat);
                locationA.setLongitude(manualDoubleLong);

                Location locationB = new Location("Point B");
                locationB.setLatitude(doubleLat);
                locationB.setLongitude(doubleLong);

                // distance = locationA.distanceTo(locationB);   // in meters
                distance = locationA.distanceTo(locationB);   // in km
                Toast.makeText(getApplicationContext(), "Distacne in KM"+distance,Toast.LENGTH_SHORT).show();*/
            }
        });
    }
}