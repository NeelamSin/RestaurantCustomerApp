package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.RecommendedToYouAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.RecyclerViewHorizontalListAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.ViewPagerAdapter;
import com.eosinfotech.restaurantcustomerui.CredentialActivity.LoginActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.Category;
import com.eosinfotech.restaurantcustomerui.Models.Item;
import com.eosinfotech.restaurantcustomerui.OwnClasses.ViewPagerCustomDuration;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.SideMenu.AboutAppActivity;
import com.eosinfotech.restaurantcustomerui.SideMenu.Faq;
import com.eosinfotech.restaurantcustomerui.SideMenu.PrivacyPolicyActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.TableClosedActivity;
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
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity implements OnRefreshViewListner {

    private RecyclerView recyclerView;
    private ArrayList<Category> items;
    private RecyclerViewHorizontalListAdapter groceryAdapter;

    private ViewPagerCustomDuration viewPager;
    private LinearLayout sliderDotsPanel;
    private int dotscount;
    private ImageView[] dots;


    //RecommendedToYou
    RecyclerView recyclerViewOne;
    RecyclerView.Adapter mAdapter;
    RecommendedToYouAdapter lAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Item> personUtilsList;

    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Restaurant" ;

    LinearLayout linearLayout;
    DatabaseHandler db;
    RegularTextView one,two;

    BoldTextView companynamePrint;

    RegularTextView companyNameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        companynamePrint = (BoldTextView)findViewById(R.id.companyNameId);
        companynamePrint.setSelected(true);
        companynamePrint.setText(sharedpreferences.getString( "restaurantname", null));


        new FetchOpenOrdercust(DashboardActivity.this).execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorHeadText));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        companyNameDisplay = (RegularTextView)headerview.findViewById(R.id.pNametxt);
        companyNameDisplay.setText(sharedpreferences.getString( "displayusername", null));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch(id)
                {
                    case R.id.aboutUs:
                        Toast.makeText(DashboardActivity.this, "About Us",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.aboutEos:
                        Intent aboutEos = new Intent(DashboardActivity.this , AboutAppActivity.class);
                        startActivity(aboutEos);
                        break;
                    case R.id.faq:
                        Intent faQ = new Intent(DashboardActivity.this , Faq.class);
                        startActivity(faQ);
                        break;
                    case R.id.privacyPlicy:
                        Intent privacy = new Intent(DashboardActivity.this , PrivacyPolicyActivity.class);
                        startActivity(privacy);
                        break;
                    case R.id.logout:
                        SharedPreferences sharedpreferences;
                        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        /*editor.putString("userid", null);
                        editor.putString("companyid", null);
                        editor.putString("ordertype", "");
                        editor.putString("tableconfirmed", "");
                        editor.putString("tableid", "");
                        editor.putString("headerid", "");
                        editor.putString("deliverycost","");
                        editor.putString("displayusername","");
                        editor.putString("imagepath","");*/
                        //  editor.putString("menuid", menuid);
                        editor.clear();
                        editor.commit();

                        Intent intentToast = new Intent(DashboardActivity.this, LoginActivity.class);
                        startActivity(intentToast);
                        finish();
                    break;
                }
                // Menu item clicked on, and close Drawerlayout
                menuItem.setChecked(true);
                drawer.closeDrawers();
                //Toast.makeText(getApplicationContext(), msgString, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        one = (RegularTextView) findViewById(R.id.one);
        two = (RegularTextView) findViewById(R.id.two);

        db = new DatabaseHandler(this);
        int cartcount = db.getCartCount(Integer.parseInt(sharedpreferences.getString("userid", null)));
        one.setText(cartcount + " Items");

        //Click On Button Pay Logics
        linearLayout = (LinearLayout) findViewById(R.id.btnLL);
        if (cartcount > 0) {
            linearLayout.setVisibility(View.VISIBLE);
            two.setText("Rs. " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));

        } else {
            linearLayout.setVisibility(View.GONE);
        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonBill = new Intent(DashboardActivity.this, ViewCartActivity.class);
                startActivity(buttonBill);
            }
        });
        items = new ArrayList<Category>();
        createApps();


        //ViewPager Programmatically
        viewPager = (ViewPagerCustomDuration) findViewById(R.id.viewPager);
        sliderDotsPanel = (LinearLayout) findViewById(R.id.sliderDots);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000 , 4000);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext() , R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDotsPanel.addView(dots[i] , params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext() , R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0 ; i < dotscount ; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext() , R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext() , R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //RecommendedToYou
        recyclerViewOne = (RecyclerView) findViewById(R.id.my_recomm_view);
        recyclerViewOne.setNestedScrollingEnabled(false);
        recyclerViewOne.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewOne.setLayoutManager(layoutManager);
        personUtilsList = new ArrayList<Item>();

        //Adding Data into ArrayList

        populateitemList();


    }


    private void populateitemList(){
        new FetchRecommended(DashboardActivity.this).execute();
    }

    @Override
    public void refreshView(boolean issubmenu, String linktolineid, String level) {
        int cartcount = db.getCartCount(Integer.parseInt(sharedpreferences.getString("userid",null)));
        one.setText(cartcount + " Items");
        two.setText("Rs. " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid",null))));
        if (cartcount > 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }


    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {

            DashboardActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    } else if (viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    } else if (viewPager.getCurrentItem() == 2){
                        viewPager.setCurrentItem(3);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    private class FetchRecommended extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        public FetchRecommended(Context context) {
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
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchRecom);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
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
                            personUtilsList.add(new Item(menuResultsJsonObject.getString("previousHeader"),
                                    "http://bouffage.eosinfotech.com/Restaurant-Control-Panel/uploadfiles/"+menuResultsJsonObject.getString("NextImagepath"),
                                    menuResultsJsonObject.getString("nextHeaderId"),
                                    menuResultsJsonObject.getString("Itemprice"),
                                    menuResultsJsonObject.getString("nextDescription"),
                                    menuResultsJsonObject.getString("PriceListid"),
                                    menuResultsJsonObject.getString("ItemCaloriesDtls")));
                        }
                    }
                    else
                    {
                        //publishProgress("");
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
            //  progressDialog.dismiss();
            mAdapter = new RecommendedToYouAdapter(DashboardActivity.this, personUtilsList);
            mAdapter.notifyDataSetChanged();
            recyclerViewOne.setAdapter(mAdapter);
        }
    }

    public class FetchOpenOrdercust extends AsyncTask<String, String, String> {
        private Context mContext;
        String order_status1="";
        public FetchOpenOrdercust(Context context) {
            this.mContext = context;
        }


        @Override
        protected String  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.fetchOrderHeaderId);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("header_id", sharedpreferences.getString("headerid",null));

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Status", "_" + jsonObject.toString());
                if (status == 200) {
                    JSONObject responseJsonObject = new JSONObject(response.body().string());
                    if(responseJsonObject.getBoolean("error")==false) {
                        JSONArray jsonArray = responseJsonObject.getJSONArray("menuItems");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        order_status1 = jsonObject1.getString("order_status");
                    }
                    else
                    {
                        order_status1="";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return order_status1;
        }


        @Override
        protected void onPostExecute(String s) {

            if(order_status1.equals("Checkedout")){
                Intent gotoNext = new Intent(DashboardActivity.this , TableClosedActivity.class);
                startActivity(gotoNext);
                finish();
            }

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(getApplicationContext() , "Sorry, you can't go back", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miCompose) {
            Intent pastOrder = new Intent(DashboardActivity.this , PastOrderActivity.class);
            startActivity(pastOrder);
        }

        return super.onOptionsItemSelected(item);
    }

    private void createApps() {

        new FetchMenu(DashboardActivity.this).execute();

    }

    private class FetchMenu extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        public FetchMenu(Context context) {
            this.mContext = context;
        }

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

                            items.add(new Category(menuResultsJsonObject.getString("previousHeader"),menuResultsJsonObject.getString("nextHeaderId"),"http://bouffage.eosinfotech.com/Restaurant-Control-Panel/uploadfiles/"+menuResultsJsonObject.getString("NextImagepath")));
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
            groceryAdapter = new RecyclerViewHorizontalListAdapter(items, DashboardActivity.this);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            groceryAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(groceryAdapter);
        }
    }


    private class FetchSetting extends AsyncTask<String, String, String> {
        private Context mContext;
        String upi="",restaurantname="",ImagePath="";

        public FetchSetting(Context context) {
            this.mContext = context;
        }


        @Override
        protected String  doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.settingsUrl);
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

                        if(jsonObject1.getString("fieldname").equals("mobile_number"))
                        {
                            upi=jsonObject1.getString("field_value");
                        }
                        else if(jsonObject1.getString("fieldname").equals("company_name"))
                        {
                            restaurantname=jsonObject1.getString("field_value");
                        }
                        else if(jsonObject1.getString("fieldname").equals("ImagePath"))
                        {
                            ImagePath=jsonObject1.getString("field_value");
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return upi;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }
}
