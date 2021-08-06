package com.eosinfotech.restaurantcustomerui.StatusActivity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.AsyncTask.OrderPaymentAsyncTask;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.TrackOrderActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.ViewCartActivity;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.concurrent.ExecutionException;

public class OrderFailureActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimationViewThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_failure);
        lottieAnimationViewThree = (LottieAnimationView) findViewById(R.id.lottieAnimationViewThree);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startCheckAnimation();
                Intent intent=new Intent(OrderFailureActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }

    public void startCheckAnimation(){
        final ValueAnimator animator = ValueAnimator.ofFloat(0f,1f).setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lottieAnimationViewThree.setProgress((Float)valueAnimator.getAnimatedValue());
            }
        });

        if (lottieAnimationViewThree.getProgress() == 0f) {
            animator.start();
        } else {
            lottieAnimationViewThree.setProgress(0f);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }

}
