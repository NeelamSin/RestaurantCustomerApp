package com.eosinfotech.restaurantcustomerui.StatusActivity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.BookingType.TableBookingActivity;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.BookingType.TableFormActivity;
import com.eosinfotech.restaurantcustomerui.GetCurrentLocation;
import com.eosinfotech.restaurantcustomerui.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GettingUserLocation extends AppCompatActivity{

    LottieAnimationView lottieAnimationViewTwo;
    private TextView latitudeView, longitudeView, txtFullAddress;
    private Button getLocationBtn, gotUserLcoation;
    GetCurrentLocation currentLoc;
    private String latitude, longitude;
    String address;
    List<Address> addresses;
    Geocoder geocoder;
    String compAddress , hardCodeAddress;


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_getting_user_location);
        context = this;


        lottieAnimationViewTwo = (LottieAnimationView) findViewById(R.id.lottieAnimationViewTen);
        currentLoc = new GetCurrentLocation(this);

        txtFullAddress = (TextView) findViewById(R.id.fullAddress);
        latitudeView = (TextView) findViewById(R.id.lat);
        longitudeView = (TextView) findViewById(R.id.lng);
        getLocationBtn = (Button) findViewById(R.id.getLocation);
        gotUserLcoation = (Button) findViewById(R.id.selectuserlocation);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLocationBtn.setVisibility(View.INVISIBLE);
                gotUserLcoation.setVisibility(View.VISIBLE);

                latitude = currentLoc.latitude;
                longitude = currentLoc.longitude;

                if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
                    latitude = "0.00";
                    longitude = "0.00";
                }

                latitudeView.setText("Latitude: " + latitude);
                longitudeView.setText("Longitude: " + longitude);
                Log.i("Latitude", latitude);
                Log.i("Longitude", longitude);

                Double gotLati = Double.parseDouble(latitude);
                Double gotLong = Double.parseDouble(longitude);

                geocoder = new Geocoder(GettingUserLocation.this , Locale.getDefault());
                StringBuilder sb = new StringBuilder();
                try {
                    addresses = geocoder.getFromLocation(gotLati, gotLong, 1);

                    if (addresses != null && addresses.size() > 0) {
                        Address addresse = addresses.get(0);
                        if (sb.append(addresse.getAddressLine(0)) != null) {
                            address = addresses.get(0).getAddressLine(0);
                        }
                    }

                    String fullAddress = address;

                    if (fullAddress != null && !fullAddress.isEmpty()) {
                        Log.i("Address", fullAddress);
                        txtFullAddress.setText(fullAddress);
                        hardCodeAddress = "1-2/1/24/A/1,7, Hitech City Main Rd, Khanamet, Madhapur, Hyderabad, Telangana 500081, India";
                        compAddress = txtFullAddress.getText().toString();

                        if (compAddress.equals(hardCodeAddress)) {
                            gotUserLcoation.setText("you're in restaurant");
                        } else {
                            gotUserLcoation.setText("you're out of restaurant");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        gotUserLcoation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (compAddress.equals(hardCodeAddress)) {
                    gotUserLcoation.setText("you're in restaurant");
                    showCustomDialog();
                    /*Intent gotoNext =new Intent(GettingUserLocation.this , TableFormActivity.class);
                    startActivity(gotoNext);*/
                } else {
                    gotUserLcoation.setText("you're out of restaurant");
                    showCustomDialog();
                    /*Intent gotoNextOne =new Intent(GettingUserLocation.this , TableFormActivity.class);
                    startActivity(gotoNextOne);*/
                }
            }
        });
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT , WindowManager.LayoutParams.WRAP_CONTENT);

        Button btnDone = (Button) dialog.findViewById(R.id.btnYes);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoNext =new Intent(GettingUserLocation.this , TableFormActivity.class);
                startActivity(gotoNext);
                dialog.dismiss();
            }
        });

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoNextOne =new Intent(GettingUserLocation.this , TableBookingActivity.class);
                startActivity(gotoNextOne);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


        public void startCheckAnimation(){
        final ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationViewTwo.setProgress((Float)valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationViewTwo.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationViewTwo.setProgress(0f);
        }
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
}
