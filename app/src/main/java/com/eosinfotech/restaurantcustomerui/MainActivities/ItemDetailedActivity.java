package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.eosinfotech.restaurantcustomerui.Adapters.ItemRateCommentAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.RecommendedToYouAdapter;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.Item;
import com.eosinfotech.restaurantcustomerui.Models.ItemRateCommentView;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderFailureActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ItemDetailedActivity extends AppCompatActivity{

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private ImageView mainBannerImage;
    private LightTextView itemDescription;
    private LightTextView itemCalories;
    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Restaurant" ;
    RelativeLayout firstButtonOne, firstButtonTwo, firstButtonThree, firstButtonFour, firstButtonFive;
    LightTextView textStoredvalue;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    List<ItemRateCommentView> personUtilsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detailed);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        //toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorHeadText), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),AllMenuItemsActivity.class));
                finish();
            }
        });


        textStoredvalue = (LightTextView) findViewById(R.id.storedValue);

        firstButtonOne = (RelativeLayout) findViewById(R.id.testButtonFive);
        firstButtonTwo = (RelativeLayout) findViewById(R.id.testButtonFour);
        firstButtonThree = (RelativeLayout) findViewById(R.id.testButtonThree);
        firstButtonFour = (RelativeLayout) findViewById(R.id.testButtonTwo);
        firstButtonFive = (RelativeLayout) findViewById(R.id.testbutton);


        Intent intent = getIntent();
        String stringName = intent.getStringExtra("itemName");
        String stringCalories = intent.getStringExtra("itemCalories");
        String stringDescription = intent.getStringExtra("itemDescription");
        String stringImageView = intent.getStringExtra("itemImage");

        mainBannerImage = (ImageView) findViewById(R.id.imageViewTwo);
        Picasso.get().load(stringImageView).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(mainBannerImage);
        itemDescription= (LightTextView) findViewById(R.id.item_description);
        itemDescription.setText(Html.fromHtml(stringDescription));
        itemCalories = (LightTextView) findViewById(R.id.itemCalories);
        itemCalories.setText(Html.fromHtml(stringCalories));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(stringName);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


        recyclerView = (RecyclerView) findViewById(R.id.androidList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        personUtilsList = new ArrayList<>();

        firstButtonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchItemReview("1");

            }
        });

        firstButtonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchItemReview("2");

            }
        });

        firstButtonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchItemReview("3");

            }
        });
        firstButtonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchItemReview("4");

            }
        });

        firstButtonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchItemReview("5");

            }
        });

    }

    private void fetchItemReview(String rating){
        new ItemDetailedActivity.FetchItemReview(ItemDetailedActivity.this).execute(rating);

    }

    private class FetchItemReview extends AsyncTask<String, String, String>
    {
        private Context mContext;
        public FetchItemReview(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.fetchItemRevieByIdUrl);
                OkHttpClient client = new OkHttpClient();
                final JSONObject jsonObject=new JSONObject();
                jsonObject.put("company_id",sharedpreferences.getString("companyid",null));
                jsonObject.put("clientId",getResources().getString(R.string.clientId));
                jsonObject.put("itemid","1");
                jsonObject.put("ratingselected",params[0]);

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
                    personUtilsList.clear();
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    if(!(responseJsonObject.get("ItemRating") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("ItemRating");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            personUtilsList.add(new ItemRateCommentView(R.drawable.ic_user,
                                    menuResultsJsonObject.getString("customer_id"),
                                    menuResultsJsonObject.getString("creation_date"),
                                    menuResultsJsonObject.getString("review_comments"),
                                    menuResultsJsonObject.getString("rating_given")));
                        }
                    }
                    else
                    {

                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);
            //  progressDialog.dismiss();
            mAdapter = new ItemRateCommentAdapter(ItemDetailedActivity.this, personUtilsList);
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);
        }
    }

}
