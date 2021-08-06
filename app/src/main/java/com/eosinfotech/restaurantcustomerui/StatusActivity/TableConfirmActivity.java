package com.eosinfotech.restaurantcustomerui.StatusActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.AsyncTask.TableBookingStatus;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.DuplicateMenu;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.concurrent.ExecutionException;

public class TableConfirmActivity extends AppCompatActivity{

    LottieAnimationView lottieAnimationView;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    String isConfirmed="";
    BoldTextView waitingStatus;
    RegularTextView waitingDescription;

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        public void run() {
            Log.d("Runnable","Handler is working");
            try {
                sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                isConfirmed = new TableBookingStatus(TableConfirmActivity.this).execute(sharedpreferences.getString("tableid", null)).get();

                if (isConfirmed.equals("Confirmed"))
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableconfirmed", "confirmed");
                    editor.commit();

                    //handler.removeCallbacks(this);
                    handler.removeCallbacks(this);

                    Log.d("Runnable","ok");
                    Intent gotoNext = new Intent(TableConfirmActivity.this , DashboardActivity.class);
                    startActivity(gotoNext);
                    finish();
                }
                else  if (isConfirmed.equals("Accepted"))
                {
                    waitingStatus.setText("Reservation Accepted");
                    waitingDescription.setText("Awesome, we've got space for you. Feel free to come a bit earlier and grab a table at our restaurant. See you soon.");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableconfirmed", "accepted");
                    editor.commit();

                    handler.postDelayed(this, 2000);
                }
                else  if (isConfirmed.equals("Rejected"))
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableconfirmed", "rejected");
                    editor.commit();

                    Log.i("Rpt","Rpt");
                    handler.removeCallbacks(this);
                    Log.d("Runnable","ok");
                    Intent gotoNext = new Intent(TableConfirmActivity.this , TableCategoryActivity.class);
                    startActivity(gotoNext);
                    finish();
                }
                else  if (isConfirmed.equals(""))
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("tableconfirmed", "requested");
                    editor.commit();

                    handler.postDelayed(this, 2000);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_confirm);

        waitingStatus = (BoldTextView) findViewById(R.id.textViewOne);
        waitingDescription = (RegularTextView) findViewById(R.id.textViewTwo);
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);

        RegularTextView viewMenu = (RegularTextView) findViewById(R.id.textViewThree);
        viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoNewMenu = new Intent(TableConfirmActivity.this , DuplicateMenu.class);
                startActivity(gotoNewMenu);
                finish();
                }
        });

        startCheckAnimation();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);

    }

    public void startCheckAnimation(){
        final ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationView.setProgress((Float)valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationView.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationView.setProgress(0f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }
}
