package com.eosinfotech.restaurantcustomerui.MealModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.MealItemsAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.RecyclerViewHorizontalListAdapter;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.Models.Category;
import com.eosinfotech.restaurantcustomerui.Models.MealItems;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.Footer;
import com.eosinfotech.restaurantcustomerui.Utils.Header;
import com.eosinfotech.restaurantcustomerui.Utils.RecyclerViewItem;
import com.eosinfotech.restaurantcustomerui.Utils.Space;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MealActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Restaurant" ;

    List<RecyclerViewItem> recyclerViewItems;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Daily Meal Order");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorHeadText));
        setSupportActionBar(toolbar);
        //init RecyclerView
        initRecyclerView();
    }

    private void initRecyclerView() {

        recyclerViewItems = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //add space item decoration and pass space you want to give
        recyclerView.addItemDecoration(new Space(20));
        //finally set adapter in API call
        new FetchMealMenu(MealActivity.this).execute();

    }
    //Method to create dummy data
    private List<RecyclerViewItem> createDummyList() {





       /* String[] imageUrls = {"https://cdn.pixabay.com/photo/2016/11/18/17/42/barbecue-1836053_640.jpg",
                "https://cdn.pixabay.com/photo/2016/07/11/03/23/chicken-rice-1508984_640.jpg",
                "https://cdn.pixabay.com/photo/2017/03/30/08/10/chicken-intestine-2187505_640.jpg",
                "https://cdn.pixabay.com/photo/2017/02/15/15/17/meal-2069021_640.jpg",
                "https://cdn.pixabay.com/photo/2017/06/01/07/15/food-2362678_640.jpg"};
        String[] titles = {"Monday Meal",
                "Tuesday Meal",
                "Wednesday Meal", "Thursday Meal", "Friday Meal"};
        String[] descriptions = {" 1.Lobiya 2.Sorakaya Curry (Bottle Gourd – Andhra Style) 3.Dal Fry 4.Plain Rice 5.2 Roti 6.Curd",
                " 1.Kabuli Channa Masala 2.Aloo Gobi 3.Palak Daal 4.Plain Rice 5.2 Roti 6.Curd",
                " 1.Mix Vegetable Curry 2.Capcicum Curry 3.Channa Daal 4.Plain Rice 5.2 Roti 6.Curd",
                " 1.Palak Paneer 2.Cabage Curry 3.Tamater Daal 4.Plain Rice 5.2 Roti 6.Curd",
                " 1.Black Channa Masala 2.Ladies Finger Curry 3.Channa Daal 4.Jeera Rice 5.2 Roti 6.Curd"};
        String[] price = {"₹70", "₹70", "₹70", "₹70", "₹70"};
        String[] calories = {"102", "20", "350", "400", "120"};
        boolean[] isHot = {true, false, true, true, false};
        for (int i = 0; i < imageUrls.length; i++) {
            MealItems foodItem = new MealItems("",calories[i],titles[i], descriptions[i], imageUrls[i], price[i],isHot[i]);
            //add food items
            recyclerViewItems.add(foodItem);
        }
        */


        return recyclerViewItems;
    }


    private class FetchMealMenu extends AsyncTask<String, String, Boolean> {
        private Context mContext;

        public FetchMealMenu(Context context) {
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
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.getItemandPricedailyUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("companyid",sharedpreferences.getString("companyid",null));
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
                    if(!(responseJsonObject.get("itemDetails") instanceof String))
                    {
                        Header header = new Header("Bouffage Food Express", "A Digital Menu",
                                "https://cdn.pixabay.com/photo/2017/09/30/15/10/pizza-2802332_640.jpg");
                        //add header
                        recyclerViewItems.add(header);

                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("itemDetails");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {

                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            //add food items
                            recyclerViewItems.add(new MealItems(menuResultsJsonObject.getString("Daily_Itemid"),menuResultsJsonObject.getString("ItemCalaroiesDtls"),menuResultsJsonObject.getString("name"), menuResultsJsonObject.getString("Description"), menuResultsJsonObject.getString("ItemImagepath"), menuResultsJsonObject.getString("Unit_Price"),true, menuResultsJsonObject.getString("MenuDay")));
                            //recyclerViewItems.add(new Category(menuResultsJsonObject.getString("name"),menuResultsJsonObject.getString("nextHeaderId"),"http://bouffage.eosinfotech.com/Restaurant-Control-Panel/uploadfiles/"+menuResultsJsonObject.getString("NextImagepath")));
                        }

                        Footer footer = new Footer("Your diet is a bank account. Good food choices are good investments.",
                                "Eos Infotech", "https://cdn.pixabay.com/photo/2017/09/30/15/10/pizza-2802332_640.jpg");
                        //add footer
                        recyclerViewItems.add(footer);
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

            recyclerView.setAdapter(new MealItemsAdapter(recyclerViewItems, MealActivity.this));

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.meal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mealOrders:
                Intent gotoPast = new Intent(MealActivity.this , MealPastOrderActivity.class);
                startActivity(gotoPast);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}

