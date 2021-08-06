package com.eosinfotech.restaurantcustomerui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.eosinfotech.restaurantcustomerui.BookingType.TableBookingActivity;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.MainActivity;
import com.eosinfotech.restaurantcustomerui.Models.TableCategory;
import com.eosinfotech.restaurantcustomerui.StatusActivity.TableConfirmActivity;

import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Restaurant" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if(sharedpreferences.contains("userid"))
        {

            if(!(sharedpreferences.getString("userid",null).equals("") || sharedpreferences.getString("userid",null).equals(null)))
            {
                Log.i("xxmainUserid","_"+sharedpreferences.getString("userid",null));

                //if(sharedpreferences.contains("otp")) {
                //if (!(sharedpreferences.getString("otp", null).equals("") || sharedpreferences.getString("otp", null).equals(null)))
                //{
                if(sharedpreferences.contains("tableid"))
                {
                    if(!(sharedpreferences.getString("tableid",null).equals("") || sharedpreferences.getString("tableid",null).equals(null)))
                    {
                        if(sharedpreferences.contains("tableconfirmed")) {
                            if (!(sharedpreferences.getString("tableconfirmed", null).equals("") || sharedpreferences.getString("tableconfirmed", null).equals(null))) {

                                Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                startActivity(intentToast);
                            } else {

                                Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                startActivity(intentToast);

                            }
                        }
                        else
                        {
                            Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                            startActivity(intentToast);
                        }
                    }
                    else
                    {
                        if(sharedpreferences.contains("ordertype"))
                        {
                            if(!(sharedpreferences.getString("ordertype",null).equals("") || sharedpreferences.getString("ordertype",null).equals(null)))
                            {
                                if(!(sharedpreferences.getString("ordertype",null).contains("Dine")))
                                {
                                    Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                    startActivity(intentToast);
                                }
                                else
                                {
                                    if(!(sharedpreferences.getString("tableid",null).equals(""))) {
                                        Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                        startActivity(intentToast);
                                    }
                                    else{
                                        Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                        startActivity(intentToast);
                                    }
                                }
                            }
                            else
                            {
                                Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                startActivity(intentToast);

                            }
                        }
                        else
                        {
                            Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                            startActivity(intentToast);

                        }
                    }


                }
                else
                {
                    //Intent intentToast = new Intent(WalkthroughActivity.this, BookingTypeActivity.class);
                    //startActivity(intentToast);
                    if(sharedpreferences.contains("ordertype")) {
                        if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {

                            if(sharedpreferences.contains("tableid"))
                            {
                                if(!(sharedpreferences.getString("tableid",null).equals("") || sharedpreferences.getString("tableid",null).equals(null)))
                                {

                                    if (!(sharedpreferences.getString("ordertype", null).contains("Dine"))) {
                                        //Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                        //startActivity(intentToast);

                                        if(sharedpreferences.contains("tableconfirmed"))
                                        {
                                            if (!(sharedpreferences.getString("tableconfirmed", null).equals("") || sharedpreferences.getString("tableconfirmed", null).equals(null)))
                                            {
                                                if(sharedpreferences.getString("tableconfirmed", null).equals("confirmed"))
                                                {
                                                    Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                                    startActivity(intentToast);
                                                }
                                                else if(sharedpreferences.getString("tableconfirmed", null).equals("accepted"))
                                                {
                                                    Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                                    startActivity(intentToast);
                                                }
                                                else if(sharedpreferences.getString("tableconfirmed", null).equals("requested"))
                                                {
                                                    Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                                    startActivity(intentToast);
                                                }
                                            }
                                            else
                                            {
                                                Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                                startActivity(intentToast);
                                            }
                                        }
                                    } else {
                                        Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                        startActivity(intentToast);
                                    }

                                }
                            }
                            else
                            {
                                if(sharedpreferences.contains("tableconfirmed"))
                                {
                                    if (!(sharedpreferences.getString("tableconfirmed", null).equals("") || sharedpreferences.getString("tableconfirmed", null).equals(null)))
                                    {
                                        if(sharedpreferences.getString("tableconfirmed", null).equals("confirmed"))
                                        {
                                            Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                            startActivity(intentToast);
                                        }
                                        else if(sharedpreferences.getString("tableconfirmed", null).equals("accepted"))
                                        {
                                            Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                            startActivity(intentToast);
                                        }
                                        else if(sharedpreferences.getString("tableconfirmed", null).equals("requested"))
                                        {
                                            Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                            startActivity(intentToast);
                                        }
                                    }
                                    else
                                    {
                                        Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                        startActivity(intentToast);
                                    }
                                }
                                else
                                {
                                    Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                    startActivity(intentToast);
                                }
                                    //Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                    //startActivity(intentToast);
                            }
                        }
                    }
                    else
                    {
                        if(sharedpreferences.contains("tableconfirmed"))
                        {
                            if (!(sharedpreferences.getString("tableconfirmed", null).equals("") || sharedpreferences.getString("tableconfirmed", null).equals(null)))
                            {
                                if(sharedpreferences.getString("tableconfirmed", null).equals("confirmed"))
                                {
                                    Intent intentToast = new Intent(LauncherActivity.this, DashboardActivity.class);
                                    startActivity(intentToast);
                                }
                                else if(sharedpreferences.getString("tableconfirmed", null).equals("accepted"))
                                {
                                    Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                    startActivity(intentToast);
                                }
                                else if(sharedpreferences.getString("tableconfirmed", null).equals("requested"))
                                {
                                    Intent intentToast = new Intent(LauncherActivity.this, TableConfirmActivity.class);
                                    startActivity(intentToast);
                                }
                            }
                            else
                            {
                                Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                startActivity(intentToast);
                            }
                        }
                        else
                        {
                            Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                            startActivity(intentToast);
                        }

                                //Intent intentToast = new Intent(LauncherActivity.this, TableCategoryActivity.class);
                                //startActivity(intentToast);
                    }
                }
                    /*}
                    else
                    {
                        Intent intentToast = new Intent(WalkthroughActivity.this, SuccessFullActivity.class);
                        //Intent intentToast = new Intent(WalkthroughActivity.this, DashboardActivity.class);

                        startActivity(intentToast);
                    }*/
                /*}
                else
                {
                    Intent intentToast = new Intent(WalkthroughActivity.this, SuccessFullActivity.class);
                    //Intent intentToast = new Intent(WalkthroughActivity.this, DashboardActivity.class);
                    startActivity(intentToast);
                }*/


            }
            else
            {
                Intent intentToast = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intentToast);
            }
        }
        else
        {
            Intent intentToast = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intentToast);
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
