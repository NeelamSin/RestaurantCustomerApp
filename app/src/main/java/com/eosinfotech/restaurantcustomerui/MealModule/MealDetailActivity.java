package com.eosinfotech.restaurantcustomerui.MealModule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.AddAddressActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.ViewCartActivity;
import com.eosinfotech.restaurantcustomerui.OwnClasses.AddToCartButton;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.MealOrderSuccessActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderFailureActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderSuccessActivity;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.google.firebase.iid.FirebaseInstanceId;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MealDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    RelativeLayout relativeLayout;
    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Restaurant" ;
    ProgressDialog progressDialog;
    RegularTextView itemPrice;
    DatabaseHandler db;
    AddToCartButton addToCartButton;
    RegularTextView addAddressChange;
    LightTextView retriveAddress;

    String number;
    String stringItemId , stringItemMenuDay, stringItemCalories, stringImageView, stringPrice, stringDescription, stringName, selectedFullAddress="";

    String contact_id="0",addrresstext="";

    RegularTextView mealNameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        relativeLayout = findViewById(R.id.relativeLayout);





        Intent intent = getIntent();
        stringItemId = intent.getStringExtra("itemid");
        stringName = intent.getStringExtra("itemName");
        stringDescription = intent.getStringExtra("itemDescription");
        stringPrice = intent.getStringExtra("itemPrice");
        stringImageView = intent.getStringExtra("itemImage");
        stringItemCalories = intent.getStringExtra("itemCalories");
        stringItemMenuDay = intent.getStringExtra("itemMenuDay");

        addrresstext = intent.getStringExtra("selectedFullAddress");

        contact_id=getIntent().getExtras().getString("selectedAddressId");

        number="0";

        mealNameDisplay = (RegularTextView) findViewById(R.id.mealNamePrint);
        mealNameDisplay.setText(Html.fromHtml(stringName));

        retriveAddress = (LightTextView) findViewById(R.id.address);
        retriveAddress.setText(addrresstext);

        addAddressChange = (RegularTextView) findViewById(R.id.addressChange);
        addAddressChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoAddress = new Intent(MealDetailActivity.this , AddAddressActivity.class);
                startActivityForResult(gotoAddress,5);
               // startActivity(gotoAddress);

            }
        });
        addToCartButton = (AddToCartButton) findViewById(R.id.number_button7);
        addToCartButton.setOnClickListener(new AddToCartButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = addToCartButton.getNumber();
                addToCartButton.setNumber(number);
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.sushiimg);

        if (!(stringImageView.equals(""))) {
            Picasso.get().load(stringImageView).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
        }

        RegularTextView itemName = (RegularTextView) findViewById(R.id.sushi_name);
        itemName.setText(Html.fromHtml(stringName));

        itemPrice = (RegularTextView) findViewById(R.id.sushi_rate);
        itemPrice.setText("₹ " + Html.fromHtml(stringPrice));

        LightTextView itemCompanyName = (LightTextView) findViewById(R.id.sushi_type);
        itemCompanyName.setText(sharedpreferences.getString("restaurantname", null));
        RegularTextView itemServing = (RegularTextView) findViewById(R.id.numberServing);
        RegularTextView itemCalories = (RegularTextView) findViewById(R.id.itemCalories);
        itemCalories.setText(Html.fromHtml(stringItemCalories));
        LightTextView itemAllitems = (LightTextView) findViewById(R.id.itemAllItems);
        itemAllitems.setText(Html.fromHtml(stringDescription));

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DAY_OF_WEEK, +1); //Adds a day
        Date dd = cal.getTime();
        final String dayOfTheWeek1 = sdf.format(dd);

        Log.i("WeekDay", "_" + dayOfTheWeek);
        Log.i("NextDay", "_" + dayOfTheWeek1);
        Log.i("MenuDay", "_" + stringItemMenuDay);
        final Button orderNow = (Button) findViewById(R.id.order_now);
            if (dayOfTheWeek1.equals(stringItemMenuDay)) {
                //orderNow.setBackgroundColor(getResources().getColor(R.color.colorHeadText));
                addToCartButton.setVisibility(View.VISIBLE);
                retriveAddress.setVisibility(View.VISIBLE);
                addAddressChange.setVisibility(View.VISIBLE);
            } else {
                orderNow.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));
                addToCartButton.setVisibility(View.INVISIBLE);
                retriveAddress.setVisibility(View.INVISIBLE);
                addAddressChange.setVisibility(View.INVISIBLE);
            }


        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext() , "", Toast.LENGTH_SHORT).show();
                if (number.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Please select quantity", Toast.LENGTH_SHORT).show();
                } else {
                    if(contact_id.equals("0"))
                    {
                        Toast.makeText(getApplicationContext(), "Please select delivery address", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (dayOfTheWeek1.equals(stringItemMenuDay)) {
                            new PlaceOrder(MealDetailActivity.this).execute();

                        } else {
                            orderNow.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));
                            Toast.makeText(getApplicationContext(), "Invalid Date You Checked", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if(requestCode==5)
        //{
            addrresstext=data.getStringExtra("selectedFullAddress");
            contact_id=data.getStringExtra("selectedAddressId");

            retriveAddress.setText(addrresstext);
            Log.i("Animal", selectedFullAddress);

        //}
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
            // Collapsed
            relativeLayout.setVisibility(View.VISIBLE);
        } else if (verticalOffset == 0) {
            // Expanded
            relativeLayout.setVisibility(View.INVISIBLE);
        } else {
            // Somewhere in between
            relativeLayout.setVisibility(View.INVISIBLE);
        }
    }


    private class PlaceOrder extends AsyncTask<String, String, String> {
        private Context mContext;
        String headerid = "";

        public PlaceOrder(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MealDetailActivity.this);
            progressDialog.setMessage("Processing Order...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), "Order could not be placed", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            Date cDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(cDate);
            cal.add(Calendar.DAY_OF_WEEK, +1); //Adds a day
            Date dd=cal.getTime();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(dd);


            String cost = itemPrice.getText().toString();
            String arr[] = cost.split("\\s+");
            Log.i("ordamount", "_" + arr[1]);


            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.addOrderHeaderUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("order_number", "");
                jsonObject.put("customer_id", sharedpreferences.getString("userid", null));
                if(sharedpreferences.contains("tableid")) {
                    jsonObject.put("tableno", sharedpreferences.getString("tableid", null));
                }
                else
                {
                    jsonObject.put("tableno","");
                }


                jsonObject.put("order_type", sharedpreferences.getString("ordertype", null));
                jsonObject.put("order_date", fDate);
                jsonObject.put("order_total", "" + Float.parseFloat(arr[1])*Float.parseFloat(number));
                jsonObject.put("company_id", sharedpreferences.getString("companyid", null));
                jsonObject.put("created_by", sharedpreferences.getString("userid", null));
                jsonObject.put("creation_date", fDate);
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                jsonObject.put("updated_date", fDate);
                jsonObject.put("DiscountID", "");
                jsonObject.put("contact_id", contact_id);
                jsonObject.put("Comp_API_token",sharedpreferences.getString("firebaseAPIKey",null));

                jsonObject.put("user_token", FirebaseInstanceId.getInstance().getToken());
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Order Status", "_" + status);
                Log.i("Order Lines Request", "_" + jsonObject.toString());
                Log.i("Refresh Token", "_" + FirebaseInstanceId.getInstance().getToken());

                if (status == 200) {
                    JSONObject responseJsonObject = new JSONObject(response.body().string());
                    headerid = String.valueOf(responseJsonObject.getInt("header_id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return headerid;
        }

        @Override
        protected void onPostExecute(String aBoolean) {
            super.onPostExecute(aBoolean);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("headerid", headerid);
            editor.commit();

                new MealDetailActivity.PlaceOrderLines(MealDetailActivity.this)
                        .execute(headerid,
                                number,
                                "0",
                                "0",
                                "0",
                                stringItemId
                        );
        }
    }



    private class PlaceOrderLines extends AsyncTask<String, String, Boolean> {
        private Context mContext;
       // int count = 0;
        int currentIndex = 0;
        boolean orderSuccess=false;

        public PlaceOrderLines(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), "Order could not be placed", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            int total = 0;
            //count = Integer.parseInt(params[3]);
            currentIndex = Integer.parseInt(params[4]);
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.addOrderLinesUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("header_id", params[0]);
                jsonObject.put("item_id", params[5]);
                jsonObject.put("order_date", fDate);
                jsonObject.put("order_qty", params[1]);
                jsonObject.put("pricelistid", params[2]);
                //jsonObject.put("special_instruction", suggestions.getText().toString());
                jsonObject.put("special_instruction", "");
                jsonObject.put("special_discount", "");
                jsonObject.put("company_id", sharedpreferences.getString("companyid", null));
                jsonObject.put("status", "New");
                jsonObject.put("created_by", sharedpreferences.getString("userid", null));
                jsonObject.put("creation_date", fDate);
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                jsonObject.put("updated_date", fDate);
               // jsonObject.put("Comp_API_token",sharedpreferences.getString("firebaseAPIKey",null));
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Order Lines Status", "_" + status);
                Log.i("Order Lines Request", "_" + jsonObject.toString());

                Log.i("Order Header", "_" + params[0]);
                if (status == 200) {
                    orderSuccess=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return orderSuccess;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


                if(aBoolean==true) {
                    progressDialog.dismiss();
                    //db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                    Intent orderPlace = new Intent(MealDetailActivity.this, MealOrderSuccessActivity.class);
                    startActivity(orderPlace);
                    //Toast.makeText(getApplicationContext(), "Order Placed", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.dismiss();
                    // db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                    //Intent orderPlace = new Intent(MealDetailActivity.this, OrderFailureActivity.class);
                    //startActivity(orderPlace);
                    Toast.makeText(getApplicationContext(), "Order Could Not Be Placed", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
