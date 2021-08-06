package com.eosinfotech.restaurantcustomerui.BookingType;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.Adapters.BookingTypeAdapter;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.TableCategory;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TableCategoryActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    RecyclerView groceryRecyclerView;

    List<TableCategory> groceryList = new ArrayList<>();
    public BookingTypeAdapter groceryAdapter;

    RegularTextView routeNavigate;
    int doubleBackToExitPressed = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_table_category);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        BoldTextView companytext = (BoldTextView) findViewById(R.id.sampleText);
        companytext.setText(sharedpreferences.getString("restaurantname", null));

        routeNavigate = (RegularTextView) findViewById(R.id.routeNavigate);
        routeNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=17.4442341,78.3845129&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                //mapIntent.set                                                                                                        ClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mapIntent);
            }
        });

        groceryRecyclerView = findViewById(R.id.recycleViewContainer);
        new FetchOffersCompany(TableCategoryActivity.this).execute();
    }


    private class FetchOffersCompany extends AsyncTask<String, String, List<TableCategory>> {
        private Context mContext;
        String typedes1 = "", typedes2="", typedes3="";
        String type1="",type2="",type3="";
        String img1="",img2="",img3="";

        public FetchOffersCompany(Context context) {
            this.mContext = context;
        }

        @Override
        protected List<TableCategory>  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.getoffersCompanyUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
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
                    JSONArray jsonArray=responseJsonObject.getJSONArray("companyDetails");

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        type1=jsonObject1.getString("offer_name");
                        typedes1=jsonObject1.getString("Description");
                        img1= sharedpreferences.getString("imagePathUrl",null)+jsonObject1.getString("imagePath");
                        //img1="http://reddygariruchulu.eosinfotech.com/Restaurant-Control-Panel/uploadfiles/"+jsonObject1.getString("imagePath");
                        Log.i("ImagePath","_"+img1);
                        groceryList.add(new TableCategory(typedes1,type1, img1));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return groceryList;
        }

        @Override
        protected void onPostExecute(List<TableCategory> aBoolean) {
            super.onPostExecute(aBoolean);

            if(groceryList.isEmpty()){
                //Toast.makeText(AllMenuItemsActivity.this,"No data", Toast.LENGTH_LONG).show();
                LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.layout_empty);
                emptyLayout.setVisibility(View.VISIBLE);
            }else {
                groceryAdapter = new BookingTypeAdapter(groceryList, TableCategoryActivity.this);
                LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(TableCategoryActivity.this, LinearLayoutManager.VERTICAL, false);
                groceryRecyclerView.setLayoutManager(verticalLayoutManager);
                groceryAdapter.notifyDataSetChanged();
                groceryRecyclerView.setAdapter(groceryAdapter);
            }
        }
    }


    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(),"Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
        if (doubleBackToExitPressed == 2) {
            finishAffinity();
            System.exit(0);
        }
        else {
            doubleBackToExitPressed++;
            Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressed=1;
            }
        }, 2000);
    }
}
