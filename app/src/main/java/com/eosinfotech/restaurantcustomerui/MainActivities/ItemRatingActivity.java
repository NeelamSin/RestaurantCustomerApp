package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.Adapters.ItemRatingAdapter;
import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.Models.ItemRating;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.CashPaymentSuccessActivity;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemRatingActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    LottieAnimationView lottieAnimationViewThree;

    ArrayList<ItemRating> itemRatingArrayList;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_rating);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        linearLayout = (LinearLayout)findViewById(R.id.btnLL);
        lottieAnimationViewThree = (LottieAnimationView) findViewById(R.id.lottieAnimationViewThree);
        startCheckAnimation();
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("tableid", null);
                editor.putString("headerid", null);
                editor.putString("ordertype", null);
                editor.putString("tableconfirmed", null);
                editor.putString("discountid", null);
                editor.putString("discountpercent", null);
                editor.putString("discountedamount", null);

                editor.commit();
                Intent intent=new Intent(ItemRatingActivity.this,TableCategoryActivity.class);
                startActivity(intent);
                finish();

            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        /*mToolbar.setNavigationIcon(R.drawable.ic_back_button);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboardReturn= new Intent(ItemRatingActivity.this , DashboardActivity.class);
                startActivity(dashboardReturn);
                finish();
            }
        });*/

        itemRatingArrayList=new ArrayList<ItemRating>();

        new FetchPastOrdersItems(ItemRatingActivity.this).execute(sharedpreferences.getString("headerid",null));



    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("tableid", "");
        editor.putString("headerid", "");
        editor.putString("ordertype", "");
        editor.putString("tableconfirmed", "");
        editor.commit();
    }

    class FetchPastOrdersItems extends AsyncTask<String, String, ArrayList<ItemRating>>
    {
        Context mContext;
        private Context mContext1;
        DatabaseHandler db;
        ArrayList<String> list;
        float total1=0.0f;

        public FetchPastOrdersItems(Context mContext1) {
            this.mContext1 = mContext1;
            list=new ArrayList<String>();
        }

        @Override
        protected ArrayList<ItemRating> doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchOrderLines);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("header_id",params[0]);
                jsonObject.put("clientId","73gecKXtTSGCW1qsemzn");
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
                    if(!(responseJsonObject.get("menuItems") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("menuItems");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);

                            itemRatingArrayList.add(new ItemRating(0,
                                    menuResultsJsonObject.getString("name"),
                                    "",
                                    0,
                                    menuResultsJsonObject.getString("item_id")));
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

            return itemRatingArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemRating> aBoolean)
        {
            super.onPostExecute(aBoolean);

            RecyclerView rv= (RecyclerView) findViewById(R.id.androidList);
            rv.setLayoutManager(new LinearLayoutManager(ItemRatingActivity.this));
            rv.setAdapter(new ItemRatingAdapter(ItemRatingActivity.this, itemRatingArrayList));

        }
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
