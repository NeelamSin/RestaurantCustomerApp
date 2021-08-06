package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.FoodMenuAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.FoodMenuAdapterOne;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.FoodMenu;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.OnRefreshViewListner;
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

public class AllMenuItemsActivity extends AppCompatActivity implements OnRefreshViewListner {

    List<FoodMenu> pizzas = new ArrayList<>();
    List<FoodMenu> pizzaOne = new ArrayList<>();
    private FoodMenuAdapter pAdapter;
    private FoodMenuAdapterOne pAdapter1;
    ProgressDialog progressDialog;
    ArrayList<String> categorylist,categoryidlist;
    RegularTextView one,two;
    LinearLayout linearLayout;
    private RecyclerView recyclerView, recyclerView1;

    private SearchView searchView;
    DatabaseHandler db;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_menu_items);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        toolbar.setTitle("GLOBAL CUISINES");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorHeadText));
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
        });

        one = (RegularTextView) findViewById(R.id.one);
        two = (RegularTextView) findViewById(R.id.two);
        db=new DatabaseHandler(this);
        int cartcount=db.getCartCount(Integer.parseInt(sharedpreferences.getString("userid",null)));
        one.setText(cartcount+" Items");

        //BottomLayout
        linearLayout = (LinearLayout) findViewById(R.id.sampleBottom);
        if(cartcount>0)
        {
            linearLayout.setVisibility(View.VISIBLE);
            two.setText("Rs. "+db.getTotal(Integer.parseInt(sharedpreferences.getString("userid",null))));
        }
        else
        {
            linearLayout.setVisibility(View.GONE);
        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bottomIntent = new Intent(AllMenuItemsActivity.this , ViewCartActivity.class);
                startActivity(bottomIntent);
            }
        });


        //TopHorizontalRecyclerView
        recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view);



        //RecommendedToYou
        recyclerView = (RecyclerView) findViewById(R.id.androidList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        categorylist=new ArrayList<>();
        categoryidlist=new ArrayList<>();

        new FetchCategory(AllMenuItemsActivity.this).execute();
    }

    @Override
    public void onBackPressed() {
        Intent backPressed = new Intent(AllMenuItemsActivity.this , DashboardActivity.class);
        startActivity(backPressed);
    }


    private void populateFoodDetails() {
        try {
            Log.i("getExtras","_"+getIntent().getExtras().getInt("linktolineid"));
            new FetchMenu(AllMenuItemsActivity.this).execute("" + getIntent().getExtras().getString("linktolineid"),"1");
        }
        catch (Exception e)
        {
            new FetchMenu(AllMenuItemsActivity.this).execute("1","1");
        }
    }

    @Override
    public void refreshView(boolean issubmenu,String linktolineid,String level) {

        if(issubmenu==true)
        {
            new FetchMenu(AllMenuItemsActivity.this).execute(linktolineid,level);
        }
        else {

            int cartcount = db.getCartCount(Integer.parseInt(sharedpreferences.getString("userid",null)));
            one.setText(cartcount + " Items");
            two.setText("Rs. " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid",null))));
            if (cartcount > 0) {
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayout.setVisibility(View.GONE);

            }
        }
    }


    private class FetchMenu extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;

        public FetchMenu(Context context) {
            this.mContext = context;
        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params)
        {


              try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchMenuUrl);
                OkHttpClient client = new OkHttpClient();

                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("userId",sharedpreferences.getString("userid",null));
                jsonObject.put("level",params[1]);
                jsonObject.put("linktolineid",params[0]);
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
                pizzas.clear();
                if(status==200)
                {

                    Log.i("jsonObject","_"+jsonObject.toString());

                    JSONObject responseJsonObject=new JSONObject(response.body().string());

                    if(!(responseJsonObject.get("MenuResults") instanceof String))
                    {

                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("MenuResults");

                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);

                            /*if(!(menuResultsJsonObject.getString("Menutype").equals("Logical")))
                            {*/
                                pizzas.add(new FoodMenu(
                                        Integer.parseInt(menuResultsJsonObject.getString("nextHeaderId")),
                                        menuResultsJsonObject.getString("nextHeader"),
                                        sharedpreferences.getString("imagepath", null) + menuResultsJsonObject.getString("NextImagepath"),
                                        menuResultsJsonObject.getString("nextDescription"),
                                        menuResultsJsonObject.getString("Itemprice"),
                                        menuResultsJsonObject.getString("Menutype"),
                                        params[1],
                                        menuResultsJsonObject.getString("PriceListid"),
                                        menuResultsJsonObject.getString("ItemCaloriesDtls")));
                            /*}
                            else
                            {
                                pizzaOne.add(new FoodMenu(
                                        Integer.parseInt(menuResultsJsonObject.getString("nextHeaderId")),
                                        menuResultsJsonObject.getString("nextHeader"),
                                        sharedpreferences.getString("imagepath", null) + menuResultsJsonObject.getString("NextImagepath"),
                                        menuResultsJsonObject.getString("nextDescription"),
                                        menuResultsJsonObject.getString("Itemprice"),
                                        menuResultsJsonObject.getString("Menutype"),
                                        params[1],
                                        menuResultsJsonObject.getString("PriceListid"),
                                        menuResultsJsonObject.getString("ItemCaloriesDtls")));

                            }*/

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
            // progressDialog.dismiss();

            if(pizzas.isEmpty()){
                //Toast.makeText(AllMenuItemsActivity.this,"No data", Toast.LENGTH_LONG).show();
                LinearLayout emptyLayout = (LinearLayout) findViewById(R.id.layout_empty);
                emptyLayout.setVisibility(View.VISIBLE);
            }else {
                pAdapter = new FoodMenuAdapter(pizzas, AllMenuItemsActivity.this);
                pAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(pAdapter);
            }
                    /*pAdapter1 = new FoodMenuAdapterOne(pizzaOne,AllMenuItemsActivity.this);
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(AllMenuItemsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView1.setLayoutManager(horizontalLayoutManager);
                    pAdapter1.notifyDataSetChanged();
                    recyclerView1.setAdapter(pAdapter1);*/
        }
    }


    private class FetchCategory extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;

        public FetchCategory(Context context) {
            this.mContext = context;
        }

       /* protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(AllMenuItemsActivity.this);
            progressDialog.setMessage("Fetching Categories...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }*/

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"No Categories found",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchMenuUrl);
                OkHttpClient client = new OkHttpClient();

                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("userId",sharedpreferences.getString("userid",null));
                jsonObject.put("level","0");
                jsonObject.put("linktolineid","0");
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

                    if(!(responseJsonObject.get("MenuResults") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("MenuResults");

                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            categorylist.add(menuResultsJsonObject.getString("nextHeader"));
                            categoryidlist.add(menuResultsJsonObject.getString("nextHeaderId"));
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
            //progressDialog.dismiss();
            populateFoodDetails();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_searchview, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("search by item");

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.colorSecondaryText));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        searchAutoComplete.setTextSize(15);

        /*View searchplate = (View)searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.setBackgroundResource(android.R.color.transparent);*/

        /*ImageView searchCloseIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchCloseIcon.setImageResource(R.drawable.ic_cancel);*/

        /*ImageView searchIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchIcon.setImageResource(R.drawable.ic_search);*/

        return super.onCreateOptionsMenu(menu);
    }
}
