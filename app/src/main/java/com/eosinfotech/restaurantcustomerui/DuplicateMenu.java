package com.eosinfotech.restaurantcustomerui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.DuplicateMenuAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.ItemRatingAdapter;
import com.eosinfotech.restaurantcustomerui.MainActivities.ItemRatingActivity;
import com.eosinfotech.restaurantcustomerui.Models.DuplicateModel;
import com.eosinfotech.restaurantcustomerui.Models.ItemRating;
import com.eosinfotech.restaurantcustomerui.StatusActivity.TableConfirmActivity;
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

public class DuplicateMenu extends AppCompatActivity {

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<DuplicateModel> personUtilsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_menu);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        personUtilsList = new ArrayList<>();
        new DuplicateMenu.FetchPastOrdersItems(DuplicateMenu.this).execute();
    }


    class FetchPastOrdersItems extends AsyncTask<String, String, ArrayList<DuplicateModel>>
    {
        Context mContext;
        private Context mContext1;
        ArrayList<String> list;

        public FetchPastOrdersItems(Context mContext1) {
            this.mContext1 = mContext1;
            list=new ArrayList<String>();
        }

        @Override
        protected ArrayList<DuplicateModel> doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.getItemandPriceUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyid",sharedpreferences.getString("companyid",null));
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
                    if(!(responseJsonObject.get("itemDetails") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("itemDetails");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);

                            personUtilsList.add(new DuplicateModel(menuResultsJsonObject.getString("name"),
                                    menuResultsJsonObject.getString("Description")));
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

            return personUtilsList;
        }

        @Override
        protected void onPostExecute(ArrayList<DuplicateModel> aBoolean)
        {
            super.onPostExecute(aBoolean);

            recyclerView = (RecyclerView) findViewById(R.id.recycleViewContainer);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(DuplicateMenu.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new DuplicateMenuAdapter(DuplicateMenu.this, personUtilsList));
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(DuplicateMenu.this , "Sorry! You cannot go back",Toast.LENGTH_SHORT).show();
    }
}
