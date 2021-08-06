package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.AddAddressAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.CouponsAdapter;
import com.eosinfotech.restaurantcustomerui.Models.AddAddress;
import com.eosinfotech.restaurantcustomerui.Models.Coupons;
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

public class CouponsActivity extends AppCompatActivity {

    private List<Coupons> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CouponsAdapter mAdapter;

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        toolbar.setTitle("Apply Coupons");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorHeadText));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(CouponsActivity.this , ViewCartActivity.class);
                startActivity(backIntent);
                finish();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new CouponsAdapter(movieList,CouponsActivity.this,getIntent().getExtras().getString("totalamt"));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new CouponsActivity.GetCoupons(CouponsActivity.this).execute();
    }


    private class GetCoupons extends AsyncTask<String, String, Boolean> {
        private Context mContext;

        public GetCoupons(Context context) {
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
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchCouponsDetailsUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("company_id",sharedpreferences.getString("companyid",null));
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
                    if(!(responseJsonObject.get("Coupons") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("Coupons");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            movieList.add(new Coupons(menuResultsJsonObject.getString("Discount_Percent"),menuResultsJsonObject.getString("Discount_Description"),menuResultsJsonObject.getString("ID")));
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

            mAdapter = new CouponsAdapter(movieList,CouponsActivity.this,getIntent().getExtras().getString("totalamt"));
            recyclerView.setAdapter(mAdapter);
        }
    }
}
