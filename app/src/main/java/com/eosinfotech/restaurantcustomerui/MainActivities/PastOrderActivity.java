package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.PastOrderAdapter;
import com.eosinfotech.restaurantcustomerui.Models.PastOrder;
import com.eosinfotech.restaurantcustomerui.OwnClasses.NonScrollListView;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PastOrderActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    PastOrderAdapter lAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<PastOrder> personUtilsList;

    DatabaseHandler db;

    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_order);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_button);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboardReturn= new Intent(PastOrderActivity.this , DashboardActivity.class);
                startActivity(dashboardReturn);
                finish();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorHeadText);

        recyclerView = (RecyclerView) findViewById(R.id.androidList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        personUtilsList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);
                mAdapter = new PastOrderAdapter(PastOrderActivity.this, personUtilsList);
                recyclerView.setAdapter(mAdapter);
            }
        });

        //Adding Data into ArrayList

        db = new DatabaseHandler(this);

        new FetchPastOrders(PastOrderActivity.this, db).execute();

    }

    private class FetchPastOrders extends AsyncTask<String, String, Boolean> {
        DatabaseHandler db1;

        private Context mContext;
        public FetchPastOrders(Context context,DatabaseHandler db1) {
            this.mContext = context;
            this.db1=db1;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"No Orders found",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchOrdersUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                //jsonObject.put("status","New");
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
                    if(!(responseJsonObject.get("menuItems") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("menuItems");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            personUtilsList.add(new PastOrder(menuResultsJsonObject.getString("order_date"),menuResultsJsonObject.getString("order_total"),menuResultsJsonObject.getString("header_id"),menuResultsJsonObject.getString("order_status"),menuResultsJsonObject.getString("DiscountID")));
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

            if(personUtilsList.isEmpty()){
                //Toast.makeText(AllMenuItemsActivity.this,"No data", Toast.LENGTH_LONG).show();
                LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.layout_empty);
                emptyLayout.setVisibility(View.VISIBLE);
            }else {
                mAdapter = new PastOrderAdapter(PastOrderActivity.this, personUtilsList);
                recyclerView.setAdapter(mAdapter);
            }


        }
    }

}

