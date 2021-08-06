package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.eosinfotech.restaurantcustomerui.Adapters.CartAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.DetailCartAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.PastOrderAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.TaxAdapter;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.Models.CartLines;
import com.eosinfotech.restaurantcustomerui.Models.PastOrder;
import com.eosinfotech.restaurantcustomerui.Models.TaxModel;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.CashPaymentSuccessActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderFailureActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderSuccessActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.PaymentSuccessActivity;
import com.eosinfotech.restaurantcustomerui.Utils.OnRefreshViewListner;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewCartActivity extends AppCompatActivity implements OnRefreshViewListner {

    private List<Cart> cartList = new ArrayList<>();
    private List<PastOrder> cartListorder = new ArrayList<>();

    private List<TaxModel> taxList = new ArrayList<>();
    private RecyclerView recyclerView, recyclerView1;
    private CartAdapter mAdapter;
    private TaxAdapter tAdapter;
    private Context context;
    //Payment Options
    private String TAG = "MainActivity";
    String payeeAddress = "";
    String payeeName = "Neelam Sai";
    String transactionNote = "Order Payment";
    String amount = "1";
    String currencyUnit = "INR";
    ProgressDialog progressDialog;
    DatabaseHandler db;
    RegularTextView itemCost, totalCost,originalcost;

    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;

    RegularTextView deliveryChargeCost;
    LinearLayout deliverychargesLinear;
    BoldTextView placeOrder;

    float prev_ordertotal;
    String prev_orderstatus = "";
    RelativeLayout addressRelative;

    int coupouncount;
    float coupounamount;
    String discountpercent,discountid;

    RelativeLayout applyCoupons;


    LinearLayout discountlinear;
    RegularTextView coupondiscount;

    String contact_id;

    BoldTextView couponlabel;
    ImageView couponarrow;

    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cart);
        db = new DatabaseHandler(this);
        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_button);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));

            }
        });

        discountid="";

        addressRelative=(RelativeLayout)findViewById(R.id.relativeNine);
        deliverychargesLinear=(LinearLayout)findViewById(R.id.asdfghjkl);


        Log.i("ordertype","__"+sharedpreferences.getString("ordertype", null));

        if(sharedpreferences.contains("ordertype")) {
            if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                if (sharedpreferences.getString("ordertype", null).equals("Delivery")) {
                    addressRelative.setVisibility(View.VISIBLE);
                    deliverychargesLinear.setVisibility(View.VISIBLE);

                } else {
                    addressRelative.setVisibility(View.INVISIBLE);
                    deliverychargesLinear.setVisibility(View.INVISIBLE);

                }
            }
        }
        coupouncount=0;
        coupounamount=0.0f;


        discountlinear=(LinearLayout)findViewById(R.id.discountlinear);
        coupondiscount=(RegularTextView)findViewById(R.id.discount);
        originalcost=(RegularTextView)findViewById(R.id.originalcost);
        couponlabel = (BoldTextView) findViewById(R.id.selectCoupon);
        couponarrow = (ImageView) findViewById(R.id.image1);

        coupondiscount.setText("0");


        RegularTextView addAddress = (RegularTextView) findViewById(R.id.addressChange);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent = new Intent(ViewCartActivity.this , AddAddressActivity.class);
                startActivity(addAddressIntent);
                finish();
            }
        });

        //avoid automatically appear android keyboard when activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        deliveryChargeCost = (RegularTextView) findViewById(R.id.deliveryCost);
        if (sharedpreferences.contains("ordertype")) {
            if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                if (sharedpreferences.getString("ordertype", null).contains("Dine")) {
                    deliveryChargeCost.setVisibility(View.VISIBLE);
                } else {
                    deliveryChargeCost.setVisibility(View.GONE);
                }
            }
        }


        deliveryChargeCost.setText("₹ " + Float.parseFloat(sharedpreferences.getString("deliverycost", "")));
        itemCost = (RegularTextView) findViewById(R.id.itemCost);
        totalCost = (RegularTextView) findViewById(R.id.totalCost);
        //itemCost.setText("₹ " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
        placeOrder = (BoldTextView) findViewById(R.id.proceedToPay);
        //suggestions = (MyEditText) findViewById(R.id.suggestions);

        applyCoupons = (RelativeLayout) findViewById(R.id.relativeLayout);

        Log.i("TotalVisits","__"+Float.parseFloat(db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null)))));

              applyCoupons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String cost="";

                    if(sharedpreferences.contains("discountid")) {
                        if (!(sharedpreferences.getString("discountid", null).equals("") || sharedpreferences.getString("discountid", null).equals(null))) {

                            String cartamt = originalcost.getText().toString();
                            String arramt[] = cartamt.split("\\s+");
                            cost = "" + Float.parseFloat(arramt[1]);
                        }
                        else
                        {
                            String cartamt = totalCost.getText().toString();
                            String arramt[] = cartamt.split("\\s+");
                            cost = "" + Float.parseFloat(arramt[1]);
                        }
                    }
                    else
                    {
                        String cartamt = totalCost.getText().toString();
                        String arramt[] = cartamt.split("\\s+");
                        cost = "" + Float.parseFloat(arramt[1]);
                    }

                    Intent couponIntent = new Intent(ViewCartActivity.this , CouponsActivity.class);
                    couponIntent.putExtra("totalamt",""+cost);
                    startActivityForResult(couponIntent,3);
                    //startActivity(couponIntent);
                }
            });

        new FetchOpenOrdercust(ViewCartActivity.this).execute();

        context = ViewCartActivity.this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);


        cartList = db.getAllCartItems(Integer.parseInt(sharedpreferences.getString("userid", null)));


        if (sharedpreferences.contains("headerid")) {

            if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null))) {
                try{
                new FetchPastOrdersItems_Prev(context).execute(sharedpreferences.getString("headerid", null));

                }catch(Exception e)
                {

                }
            }
            else
            {
                mAdapter = new CartAdapter(context, cartList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                mAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mAdapter);

                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                recyclerView1.setLayoutManager(mLayoutManager1);
                recyclerView1.setItemAnimator(new DefaultItemAnimator());
                new FetchTax(ViewCartActivity.this).execute();

            }
        }
        else
        {
            mAdapter = new CartAdapter(context, cartList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);

            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
            recyclerView1.setLayoutManager(mLayoutManager1);
            recyclerView1.setItemAnimator(new DefaultItemAnimator());
            new FetchTax(ViewCartActivity.this).execute();

        }

        cartListorder = new ArrayList<>();

        boolean isDeleivered = false;

        if (sharedpreferences.contains("tableid")) {
            if (!(sharedpreferences.getString("tableid", null).equals("") || sharedpreferences.getString("tableid", null).equals(null))) {

                if (sharedpreferences.contains("headerid")) {

                    if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null))) {
                        try {
                            isDeleivered = new FetchPastOrders(ViewCartActivity.this, db).execute().get();
                            if (isDeleivered == true) {
                                placeOrder.setText("Proceed To Pay");

                            } else {
                                placeOrder.setText("Update Order");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else {

                        placeOrder.setText("Place Order");
                    }
                } else {

                    placeOrder.setText("Place Order");
                }

            } else {
                placeOrder.setText("Proceed To Pay");
            }
        } else {

            if (sharedpreferences.contains("headerid"))
            {

                if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null)))
                {
                    placeOrder.setText("Track Order");

                }
                else
                {
                    placeOrder.setText("Proceed To Pay");

                }
            }
            else {

                placeOrder.setText("Proceed To Pay");
            }
        }

        final boolean finalIsDeleivered = isDeleivered;
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (sharedpreferences.contains("tableid")) {
                    if (!(sharedpreferences.getString("tableid", null).equals("") || sharedpreferences.getString("tableid", null).equals(null))) {
                        if (sharedpreferences.contains("headerid")) {
                            if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null))) {

                                if (finalIsDeleivered == true) {

                                    String cost = totalCost.getText().toString();
                                    final String arr[] = cost.split("\\s+");
                                    Log.i("ordamount", "_" + arr[1]);

                                    Log.i("payeeName", "_" + payeeName);
                                    Log.i("payeeAddress", "_" + payeeAddress);
                                    Log.i("amount", "_" + arr[1]);

                                    final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                                    View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
                                    mBottomSheetDialog.setContentView(sheetView);
                                    LinearLayout linearCOCD = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_cocd);
                                    LinearLayout linearOther = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_other);

                                    if(sharedpreferences.contains("ordertype")) {
                                        if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                                            if (sharedpreferences.getString("ordertype", null).equals("Dine In")) {
                                                linearCOCD.setVisibility(View.VISIBLE);
                                            } else {
                                                linearCOCD.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                    linearCOCD.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String cost=totalCost.getText().toString();
                                            String arr[]=cost.split("\\s+");
                                            Log.i("ordamount","_"+arr[1]);

                                            Intent orderPlace = new Intent(ViewCartActivity.this, CashPaymentSuccessActivity.class);
                                            orderPlace.putExtra("txnRef", "");
                                            orderPlace.putExtra("ordertotal", arr[1]);
                                            orderPlace.putExtra("txnRemarks","");

                                            startActivity(orderPlace);

                                            mBottomSheetDialog.dismiss();

                                        }
                                    });
                                    linearOther.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mBottomSheetDialog.dismiss();

                                            String cost = totalCost.getText().toString();
                                            final String arr[] = cost.split("\\s+");
                                            Log.i("ordamount", "_" + arr[1]);


                                            Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote +
                                                    "&am=" + arr[1] + "&cu=" + currencyUnit);
                                            Log.d(TAG, "onClick: uri: " + uri);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            //intent.setClassName("in.org.npci.upiapp","in.org.npci.upiapp.HomeActivity");
                                            startActivityForResult(intent, 1);
                                        }
                                    });
                                    mBottomSheetDialog.show();

                                } else {

                                    String cost = itemCost.getText().toString();
                                    String arr[] = cost.split("\\s+");
                                    Log.i("ordamount", "_" + arr[1]);


                                    new UpdateOrderAsyncTask(ViewCartActivity.this).execute();

                                }
                            } else {

                                new PlaceOrder(ViewCartActivity.this).execute();
                            }
                        } else {

                            new PlaceOrder(ViewCartActivity.this).execute();
                        }
                    } else {
                        String cost = totalCost.getText().toString();
                        String arr[] = cost.split("\\s+");
                        Log.i("ordamount", "_" + arr[1]);

                        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
                        mBottomSheetDialog.setContentView(sheetView);
                        LinearLayout linearCOCD = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_cocd);
                        LinearLayout linearOther = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_other);

                                if(sharedpreferences.contains("ordertype")) {
                                    if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                                        if (sharedpreferences.getString("ordertype", null).equals("Dine In")) {
                                            linearCOCD.setVisibility(View.VISIBLE);
                                        } else {
                                            linearCOCD.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                }

                        linearCOCD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String cost=totalCost.getText().toString();
                                String arr[]=cost.split("\\s+");
                                Log.i("ordamount","_"+arr[1]);

                                Intent orderPlace = new Intent(ViewCartActivity.this, CashPaymentSuccessActivity.class);
                                orderPlace.putExtra("txnRef", "");
                                orderPlace.putExtra("ordertotal", arr[1]);
                                orderPlace.putExtra("txnRemarks","");

                                startActivity(orderPlace);
                                mBottomSheetDialog.dismiss();

                            }
                        });
                        linearOther.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                                String cost = totalCost.getText().toString();
                                final String arr[] = cost.split("\\s+");
                               Log.i("ordamount", "_" + arr[1]);

                                Uri uri = Uri.parse("upi://pay?pa="+ payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote +
                                        "&am=" + arr[1] + "&cu=" + currencyUnit);
                                Log.d(TAG, "onClick: uri: " + uri);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                //intent.setClassName("in.org.npci.upiapp","in.org.npci.upiapp.HomeActivity");
                                startActivityForResult(intent, 1);
                            }
                        });
                        mBottomSheetDialog.show();

                        /*Uri uri = Uri.parse("upi://pay?pa="+payeeAddress+"&pn="+payeeName+"&tn="+transactionNote+
                                "&am="+arr[1]+"&cu="+currencyUnit);
                        Log.d(TAG, "onClick: uri: "+uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivityForResult(intent,1);*/
                    }
          }
          else {

                    Log.i("TABLEID", "_DOESNOTEXIST" );


                    if (sharedpreferences.contains("headerid")) {

                        Log.i("HEADERID", "_EXISTS" );


                        if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null))) {
                            //placeOrder.setText("Track Order");
                            Intent intent=new Intent(context,TrackOrderActivity.class);
                            startActivity(intent);

                        } else {

                            Log.i("HEADERID", "_EXISTSBUTNULL" );


                            String cost = totalCost.getText().toString();
                            String arr[] = cost.split("\\s+");
                            Log.i("ordamount", "_" + arr[1]);

                            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                            View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
                            mBottomSheetDialog.setContentView(sheetView);
                            LinearLayout linearCOCD = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_cocd);
                            LinearLayout linearOther = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_other);


                            if (sharedpreferences.contains("ordertype")) {
                                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                                    if (sharedpreferences.getString("ordertype", null).equals("Dine In")) {
                                        linearCOCD.setVisibility(View.VISIBLE);
                                    } else {
                                        linearCOCD.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            linearCOCD.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String cost = totalCost.getText().toString();
                                    String arr[] = cost.split("\\s+");
                                    Log.i("ordamount", "_" + arr[1]);

                                    Intent orderPlace = new Intent(ViewCartActivity.this, CashPaymentSuccessActivity.class);
                                    orderPlace.putExtra("txnRef", "");
                                    orderPlace.putExtra("ordertotal", arr[1]);
                                    orderPlace.putExtra("txnRemarks", "");

                                    startActivity(orderPlace);

                                    mBottomSheetDialog.dismiss();

                                }
                            });
                            linearOther.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mBottomSheetDialog.dismiss();

                                    String cost = totalCost.getText().toString();
                                    final String arr[] = cost.split("\\s+");
                                    Log.i("ordamount", "_" + arr[1]);


                                    Log.i("PAYMENT", "_PAYMENTS" );

                                   Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote +
                                            "&am=" + arr[1] + "&cu=" + currencyUnit);
                                    Log.d(TAG, "onClick: uri: " + uri);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.setPackage("com.google.android.apps.nbu.paisa.user");
                                    startActivityForResult(intent, 1);

                                   // new PlaceOrder(ViewCartActivity.this).execute();

                                }
                            });
                            mBottomSheetDialog.show();
                        }
                    }
                    else
                    {
                        Log.i("HEADERID", "_DOESNOTEXIST" );

                        String cost = totalCost.getText().toString();
                        String arr[] = cost.split("\\s+");
                        Log.i("ordamount", "_" + arr[1]);

                        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ViewCartActivity.this);
                        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
                        mBottomSheetDialog.setContentView(sheetView);
                        LinearLayout linearCOCD = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_cocd);
                        LinearLayout linearOther = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_other);


                        if (sharedpreferences.contains("ordertype")) {
                            if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                                if (sharedpreferences.getString("ordertype", null).equals("Dine In")) {
                                    linearCOCD.setVisibility(View.VISIBLE);
                                } else {
                                    linearCOCD.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        linearCOCD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String cost = totalCost.getText().toString();
                                String arr[] = cost.split("\\s+");
                                Log.i("ordamount", "_" + arr[1]);

                                Intent orderPlace = new Intent(ViewCartActivity.this, CashPaymentSuccessActivity.class);
                                orderPlace.putExtra("txnRef", "");
                                orderPlace.putExtra("ordertotal", arr[1]);
                                orderPlace.putExtra("txnRemarks", "");

                                startActivity(orderPlace);

                                mBottomSheetDialog.dismiss();

                            }
                        });
                        linearOther.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();

                                String cost = totalCost.getText().toString();
                                final String arr[] = cost.split("\\s+");
                                Log.i("ordamount", "_" + arr[1]);

                                Log.i("PAYMENT", "_PAYMENTS" );


                                Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote +
                                        "&am=" + arr[1] + "&cu=" + currencyUnit);
                                Log.d(TAG, "onClick: uri: " + uri);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setPackage("com.google.android.apps.nbu.paisa.user");
                                startActivityForResult(intent, 1);

                            }
                        });
                        mBottomSheetDialog.show();
                    }
                }
            }
        });

        }

    class FetchPastOrdersItems_Prev extends AsyncTask<String, String, List<Cart>>
    {
        Context mContext;
        private Context mContext1;
//        ArrayList<Cart> list;

        public FetchPastOrdersItems_Prev(Context mContext1) {
            this.mContext1 = mContext1;
  //          list=new ArrayList<Cart>();
        }

        @Override
        protected List<Cart> doInBackground(String... params)
        {
            try {
                String url= getResources().getString(R.string.serverUrl) +getResources().getString(R.string.apiUrl) + getResources().getString(R.string.fetchOrderLines);
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
                            //list.add(menuResultsJsonObject.getString("name")+" x"+menuResultsJsonObject.getString("order_qty"));
                            cartList.add(new Cart(0,
                                    Integer.parseInt(menuResultsJsonObject.getString("created_by")),
                                    Integer.parseInt(menuResultsJsonObject.getString("item_id")),
                                    menuResultsJsonObject.getString("name"),
                                    "",
                                    menuResultsJsonObject.getString("SellingPrice"),
                                    Integer.parseInt(menuResultsJsonObject.getString("order_qty")),
                                    "",
                                    String.valueOf(Integer.parseInt(menuResultsJsonObject.getString("order_qty"))*Float.parseFloat(menuResultsJsonObject.getString("SellingPrice"))),
                                    menuResultsJsonObject.getString("pricelistid"),
                                    menuResultsJsonObject.getString("status")));


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
            Log.i("cartlist","_"+cartList.size());
            return cartList;
        }

        @Override
        protected void onPostExecute(List<Cart> carts) {
            super.onPostExecute(carts);


                mAdapter = new CartAdapter(context, cartList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                mAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mAdapter);

                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                recyclerView1.setLayoutManager(mLayoutManager1);
                recyclerView1.setItemAnimator(new DefaultItemAnimator());
                new FetchTax(ViewCartActivity.this).execute();


        }
    }


    private class FetchSetting extends AsyncTask<String, String, String> {
        private Context mContext;
        String upi = "", restaurantname = "", ImagePath = "",cvisits="0",camt="0.00";

        public FetchSetting(Context context) {
            this.mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.settingsUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId", sharedpreferences.getString("companyid", null));
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
                    JSONArray jsonArray = responseJsonObject.getJSONArray("companyDetails");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        if (jsonObject1.getString("fieldname").equals("mobile_number")) {
                            upi = jsonObject1.getString("field_value");
                        }  if (jsonObject1.getString("fieldname").equals("company_name")) {
                            restaurantname = jsonObject1.getString("field_value");
                        }  if (jsonObject1.getString("fieldname").equals("ImagePath")) {
                            ImagePath = jsonObject1.getString("field_value");
                        }

                        if (jsonObject1.getString("fieldname").equals("Coupon_Eligibility")) {

                            if (jsonObject1.getString("display_text").equals("visits")) {
                                cvisits = jsonObject1.getString("field_value");
                            }

                            if (jsonObject1.getString("display_text").equals("order_amount")) {
                                camt = jsonObject1.getString("field_value");
                            }
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
            //payeeAddress = upi;
            payeeAddress="sandhya.sn.nair@okhdfcbank";
            payeeName = restaurantname;
            coupounamount=Float.parseFloat(camt);
            coupouncount=Integer.parseInt(cvisits);

            Log.i("SettingEnds","SettingEnds");

            try {
                ArrayList<String> countarr=new FetchPastOrdersCount(ViewCartActivity.this, db).execute().get();

                Log.i("SettingEnds1","SettingEnds1");

                cnt = countarr.size();
            }catch(Exception e)
            {
                e.printStackTrace();

            }




        }
    }

    private class FetchPastOrdersCount extends AsyncTask<String, String, ArrayList<String>> {
        DatabaseHandler db1;

        private Context mContext;
        ArrayList<String> ordercount=new ArrayList<String>();
        public FetchPastOrdersCount(Context context,DatabaseHandler db1) {
            this.mContext = context;
            this.db1=db1;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Toast.makeText(getApplicationContext(),"No Orders found",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params)
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
                Log.i("pastcountStatus","_"+status);
                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    if(!(responseJsonObject.get("menuItems") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("menuItems");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            ordercount.add(menuResultsJsonObject.getString("order_date"));
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
            return ordercount;
        }

        @Override
        protected void onPostExecute(ArrayList<String> aBoolean) {
            super.onPostExecute(aBoolean);

            String cartamt = totalCost.getText().toString();
            String arramt[] = cartamt.split("\\s+");

            Log.i("Discount Starts","Discount Starts");

            Log.i("Discount Starts",arramt[1]);
            Log.i("Discount Starts",""+coupounamount);

            if((ordercount.size()>=coupouncount || Float.parseFloat(arramt[1])>=coupounamount) && !(sharedpreferences.getString("ordertype", null).equals("Dine In")))
            {
                Log.i("Discount If","Discount If");

                applyCoupons.setVisibility(View.VISIBLE);
                discountlinear.setVisibility(View.GONE);

                if(sharedpreferences.contains("discountid"))
                {
                    if (!(sharedpreferences.getString("discountid", null).equals("") || sharedpreferences.getString("discountid", null).equals(null)))
                    {
                        applyCoupons.setVisibility(View.VISIBLE);
                        discountlinear.setVisibility(View.VISIBLE);

                        discountid=sharedpreferences.getString("discountid", null);
                        discountpercent=sharedpreferences.getString("discountpercent", null);
                        //float discountedamount=Float.valueOf(sharedpreferences.getString("discountedamount", null));

                        couponlabel.setText("COUPON APPLIED - BOUFFAGE "+sharedpreferences.getString("discountpercent", null));
                        String cartamt1 = totalCost.getText().toString();
                        String arramt1[] = cartamt1.split("\\s+");
                        String cost=""+Float.parseFloat(arramt1[1]);

                        float dis=Float.parseFloat(discountpercent)/100;
                        float discountedamount=dis*Float.parseFloat(cost);

                        coupondiscount.setText("₹ " +discountedamount);
                        originalcost.setText("₹ " +cost);
                        float finalprice=Float.parseFloat(cost)-discountedamount;
                        totalCost.setText("₹ " +finalprice);
                        originalcost.setPaintFlags(originalcost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                    }

                }
            }
            else
            {
                applyCoupons.setVisibility(View.GONE);
                discountlinear.setVisibility(View.GONE);

            }

            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode);
        //txnId=UPI20b6226edaef4c139ed7cc38710095a3&responseCode=00&ApprovalRefNo=null&Status=SUCCESS&txnRef=undefined
        //txnId=UPI608f070ee644467aa78d1ccf5c9ce39b&responseCode=ZM&ApprovalRefNo=null&Status=FAILURE&txnRef=undefined

        if(resultCode==3)
        {

            discountpercent=data.getStringExtra("discountpercent");
            discountid=data.getStringExtra("discountid");
            String totalamt=data.getStringExtra("totalamt");

            couponlabel.setText("COUPON APPLIED - BOUFFAGE "+discountpercent);

             String cost=""+Float.parseFloat(totalamt);

            float dis=Float.parseFloat(discountpercent)/100;
            float discountedamount=dis*Float.parseFloat(cost);

            coupondiscount.setText("₹ " +discountedamount);
            originalcost.setText("₹ " +cost);
            float finalprice=Float.parseFloat(cost)-discountedamount;
            totalCost.setText("₹ " +finalprice);
            originalcost.setPaintFlags(originalcost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("discountid", discountid);
            editor.putString("discountpercent", discountpercent);
            editor.putString("discountedamount", ""+discountedamount);
            editor.commit();

        }
        else {
            if (data != null) {
                Log.d(TAG, "onActivityResult: data: " + data.getStringExtra("response"));
                String res = data.getStringExtra("response");
                String search = "SUCCESS";

                String[] resarr = res.split("txnRef=");

                try
                {
                String txnRef = resarr[1];

                String[] resarrref = res.split("txnId=");
                String txnId = resarrref[1];

                String[] resarrref1 = txnId.split("&responseCode");
                String txnId1 = resarrref1[0];

                Log.i("Txn", "" + txnRef);
                Log.i("TxnId", "" + txnId1);

                if (res.toLowerCase().contains(search.toLowerCase())) {

                    if (sharedpreferences.contains("headerid")) {
                        if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null))) {
                            Intent orderPlace = new Intent(ViewCartActivity.this, PaymentSuccessActivity.class);
                            orderPlace.putExtra("txnRef", txnRef);
                            orderPlace.putExtra("ordertotal", totalCost.getText().toString());
                            orderPlace.putExtra("txnRemarks", "SUCCESS, " + txnRef);

                            startActivity(orderPlace);
                        } else {
                            new PlaceOrder(ViewCartActivity.this).execute();

                        }

                    } else {
                        new PlaceOrder(ViewCartActivity.this).execute();

                    }

                } else {
                    Intent orderFailure = new Intent(ViewCartActivity.this, OrderFailureActivity.class);
                    orderFailure.putExtra("txnRef", txnRef);
                    orderFailure.putExtra("ordertotal", totalCost.getText().toString());
                    orderFailure.putExtra("txnRemarks", "FAILURE, " + txnRef);
                    startActivity(orderFailure);
                }
            }
            catch (Exception e)
            {
                Toast.makeText(ViewCartActivity.this,"Transanction Cancelled!!!",Toast.LENGTH_SHORT).show();
            }
            }
        }
    }

    @Override
    public void refreshView(boolean issubmenu, String linktolineid, String level) {
        if (issubmenu == true) {
            cartList.clear();
            cartList = db.getAllCartItems(Integer.parseInt(sharedpreferences.getString("userid", null)));


            if (cartList.size() > 0) {



                if (sharedpreferences.contains("headerid")) {

                    if (!(sharedpreferences.getString("headerid", null).equals("") || sharedpreferences.getString("headerid", null).equals(null))) {
                        try {
                            new FetchPastOrdersItems_Prev(context).execute(sharedpreferences.getString("headerid", null));

                        } catch (Exception e) {

                        }
                    }
                    else {
                        mAdapter = new CartAdapter(context, cartList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);

                        //itemCost.setText("₹ " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
                        new RefreshTax(ViewCartActivity.this).execute();
                    }
                }
                else {
                    mAdapter = new CartAdapter(context, cartList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);

                    //itemCost.setText("₹ " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
                    new RefreshTax(ViewCartActivity.this).execute();
                }
            } else {
                Intent intent = new Intent(ViewCartActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        } else {
            //itemCost.setText("₹ " + db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
            //new RefreshTax(ViewCartActivity.this).execute();

            float itemtotal=0.0f;
            float taxtotal=0.0f;
            itemtotal=Float.valueOf(db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
            ArrayList<TaxModel> refreshtaxList=new ArrayList<TaxModel>();

            for (int i = 0; i < taxList.size(); i++)
            {
                refreshtaxList.add(new TaxModel(taxList.get(i).getDesc(), taxList.get(i).getCost(),itemtotal));


                taxtotal = taxtotal + Float.parseFloat(taxList.get(i).getCost()) / 100 * Float.valueOf(db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
            }

            tAdapter = new TaxAdapter(ViewCartActivity.this, refreshtaxList);
            tAdapter.notifyDataSetChanged();
            recyclerView1.setAdapter(tAdapter);


            itemCost.setText("₹ " + itemtotal);


            if (sharedpreferences.contains("ordertype")) {
                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                    if (sharedpreferences.getString("ordertype", null).contains("Dine")) {
                        totalCost.setText("₹ " + String.format("%.2f", (itemtotal + taxtotal + 0.00)));
                        Log.i("Tax111", "_" + taxtotal);

                    } else {
                        totalCost.setText("₹ " + String.format("%.2f", (itemtotal + taxtotal + Float.parseFloat(sharedpreferences.getString("deliverycost", "")))));
                    }
                }
            }
        }

        String cartamt = totalCost.getText().toString();
        String arramt[] = cartamt.split("\\s+");

        if(cnt>=coupouncount || Float.parseFloat(arramt[1])>=coupounamount)
        {
            applyCoupons.setVisibility(View.VISIBLE);
            discountlinear.setVisibility(View.GONE);

            if(sharedpreferences.contains("discountid"))
            {
                if (!(sharedpreferences.getString("discountid", null).equals("") || sharedpreferences.getString("discountid", null).equals(null)))
                {
                    applyCoupons.setVisibility(View.VISIBLE);
                    discountlinear.setVisibility(View.VISIBLE);

                    discountid=sharedpreferences.getString("discountid", null);
                    discountpercent=sharedpreferences.getString("discountpercent", null);
                    //float discountedamount=Float.valueOf(sharedpreferences.getString("discountedamount", null));

                    couponlabel.setText("COUPON APPLIED - BOUFFAGE "+sharedpreferences.getString("discountpercent", null));
                    String cartamt1 = totalCost.getText().toString();
                    String arramt1[] = cartamt1.split("\\s+");
                    String cost=""+Float.parseFloat(arramt1[1]);

                    float dis=Float.parseFloat(discountpercent)/100;
                    float discountedamount=dis*Float.parseFloat(cost);

                    coupondiscount.setText("₹ " +discountedamount);
                    originalcost.setText("₹ " +cost);
                    float finalprice=Float.parseFloat(cost)-discountedamount;
                    totalCost.setText("₹ " +finalprice);
                    originalcost.setPaintFlags(originalcost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                }
                else
                {
                    discountid="";
                    couponlabel.setText("APPLY COUPON");
                    discountlinear.setVisibility(View.GONE);

                }

            }
            else
            {
                discountid="";
                couponlabel.setText("APPLY COUPON");
                discountlinear.setVisibility(View.GONE);

            }
        }
        else
        {
            applyCoupons.setVisibility(View.GONE);
            discountlinear.setVisibility(View.GONE);
            coupondiscount.setText("");
            originalcost.setText("");
            discountid="";

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("discountid", null);
            editor.putString("discountpercent", null);
            editor.putString("discountedamount", null);
            editor.commit();

        }



    }


    private class FetchPastOrders extends AsyncTask<String, String, Boolean> {
        DatabaseHandler db1;
        boolean isCompleted = false;

        private Context mContext;

        public FetchPastOrders(Context context, DatabaseHandler db1) {
            this.mContext = context;
            this.db1 = db1;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // CustomToast.makeText(getApplicationContext(),"No Orders found",CustomToast.LENGTH_SHORT, CustomToast.WARNING,false).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.fetchOrdersUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("companyId", sharedpreferences.getString("companyid", null));
                //jsonObject.put("status","New");
                jsonObject.put("customer_id", sharedpreferences.getString("userid", null));
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
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
                    if (!(responseJsonObject.get("menuItems") instanceof String)) {
                        JSONArray menuResultsJsonArray = responseJsonObject.getJSONArray("menuItems");
                        for (int i = 0; i < menuResultsJsonArray.length(); i++) {
                            JSONObject menuResultsJsonObject = menuResultsJsonArray.getJSONObject(i);
                            cartListorder.add(new PastOrder(menuResultsJsonObject.getString("order_date"), menuResultsJsonObject.getString("order_total"), menuResultsJsonObject.getString("header_id"), menuResultsJsonObject.getString("order_status")));
                            if (sharedpreferences.getString("headerid", null) == menuResultsJsonObject.getString("header_id")) {
                                if (menuResultsJsonObject.getString("order_status").equals("Completed")) {
                                    isCompleted = true;
                                }

                            }
                        }
                    } else {
                        publishProgress("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isCompleted;
        }


    }

    private class RefreshTax extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        float taxtotal = 0.0f;
        float itemtotal=0.0f;

        public RefreshTax(Context context) {
            this.mContext = context;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), "Tax Details Not Available", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.taxUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sessionid", "1");
                jsonObject.put("company_id", sharedpreferences.getString("companyid", null));
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
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
                    taxList.clear();
                    JSONObject responseJsonObject = new JSONObject(response.body().string());
                    if (!(responseJsonObject.get("company_id") instanceof String)) {
                        JSONArray menuResultsJsonArray = responseJsonObject.getJSONArray("company_id");
                        for(int k=0;k<cartList.size();k++)
                        {
                            itemtotal=itemtotal+Float.valueOf(cartList.get(k).getItemtotalprice());
                        }
                        for (int i = 0; i < menuResultsJsonArray.length(); i++) {
                            JSONObject menuResultsJsonObject = menuResultsJsonArray.getJSONObject(i);
                            taxList.add(new TaxModel(menuResultsJsonObject.getString("taxtype"), menuResultsJsonObject.getString("taxpercent"),itemtotal));
                            taxtotal = taxtotal + Float.parseFloat(menuResultsJsonObject.getString("taxpercent")) / 100 * itemtotal;
                            Log.i("Tax", "_" + taxtotal);
                        }
                    } else {
                        publishProgress("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //progressDialog.dismiss();
            Log.i("Tax", "_" + taxList.toString());

            tAdapter = new TaxAdapter(context, taxList);
            tAdapter.notifyDataSetChanged();
            recyclerView1.setAdapter(tAdapter);

            itemCost.setText("₹ " + itemtotal);


            if (sharedpreferences.contains("ordertype")) {
                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                    if (sharedpreferences.getString("ordertype", null).contains("Dine")) {
                        totalCost.setText("₹ " + String.format("%.2f", (itemtotal + taxtotal + 0.00)));
                        Log.i("Tax111", "_" + taxtotal);

                    } else {
                        totalCost.setText("₹ " + String.format("%.2f", (itemtotal + taxtotal + Float.parseFloat(sharedpreferences.getString("deliverycost", "")))));
                    }
                }
            }
            //totalCost.setText("₹ "+String.format("%.2f",(Float.parseFloat(db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))))+taxtotal+Float.parseFloat(sharedpreferences.getString("deliverycost","")))));

        }
    }

    private class FetchTax extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        float taxtotal = 0.0f;
        float itemtotal = 0.0f;


        public FetchTax(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewCartActivity.this);
            progressDialog.setMessage("Fetching Order Details...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), "Tax Details Not Available", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.taxUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sessionid", "1");
                jsonObject.put("company_id", sharedpreferences.getString("companyid", null));
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
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
                    if (!(responseJsonObject.get("company_id") instanceof String)) {
                        JSONArray menuResultsJsonArray = responseJsonObject.getJSONArray("company_id");
                        for(int k=0;k<cartList.size();k++)
                        {
                            itemtotal=itemtotal+Float.valueOf(cartList.get(k).getItemtotalprice());
                        }
                        for (int i = 0; i < menuResultsJsonArray.length(); i++) {
                            JSONObject menuResultsJsonObject = menuResultsJsonArray.getJSONObject(i);
                            taxList.add(new TaxModel(menuResultsJsonObject.getString("taxtype"), menuResultsJsonObject.getString("taxpercent"),itemtotal));

                            taxtotal = taxtotal + Float.parseFloat(menuResultsJsonObject.getString("taxpercent")) / 100 * itemtotal;

                            // taxtotal = taxtotal + Float.parseFloat(menuResultsJsonObject.getString("taxpercent")) / 100 * Float.parseFloat(db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))));
                            Log.i("Tax", "_" + taxtotal);
                        }
                    } else {
                        publishProgress("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            tAdapter = new TaxAdapter(context, taxList);
            tAdapter.notifyDataSetChanged();
            recyclerView1.setAdapter(tAdapter);

            itemCost.setText("₹ " + itemtotal);


            if (sharedpreferences.contains("ordertype")) {
                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                    if (sharedpreferences.getString("ordertype", null).contains("Dine")) {
                        totalCost.setText("₹ " + String.format("%.2f", (itemtotal + taxtotal + 0.00)));
                        Log.i("Tax111", "_" + taxtotal);

                    } else {
                        totalCost.setText("₹ " + String.format("%.2f", (itemtotal + taxtotal + Float.parseFloat(sharedpreferences.getString("deliverycost", "")))));
                    }
                }
            }

            new FetchSetting(ViewCartActivity.this).execute();

            //totalCost.setText("₹ "+String.format("%.2f",(Float.parseFloat(db.getTotal(Integer.parseInt(sharedpreferences.getString("userid", null))))+taxtotal+Float.parseFloat(sharedpreferences.getString("deliverycost","")))));
        }
    }

    @Override
    public void onBackPressed() {
        Intent backPressed = new Intent(ViewCartActivity.this, DashboardActivity.class);
        startActivity(backPressed);
    }

    private void prepareCartDate() {
        mAdapter.notifyDataSetChanged();
    }

    private class PlaceOrder extends AsyncTask<String, String, String> {
        private Context mContext;
        String headerid = "";

        public PlaceOrder(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewCartActivity.this);
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
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            String cost = totalCost.getText().toString();
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
                jsonObject.put("order_total", "" + arr[1]);
                jsonObject.put("company_id", sharedpreferences.getString("companyid", null));
                jsonObject.put("created_by", sharedpreferences.getString("userid", null));
                jsonObject.put("creation_date", fDate);
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                jsonObject.put("updated_date", fDate);
                jsonObject.put("DiscountID", discountid);
                jsonObject.put("contact_id", "15");
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

            for (int j = 0; j < cartList.size(); j++) {
                new PlaceOrderLines(ViewCartActivity.this)
                        .execute(headerid,
                                String.valueOf(cartList.get(j).getItemquantity()),
                                cartList.get(j).getPricelistid(),
                                String.valueOf(cartList.size()),
                                String.valueOf(j),
                                String.valueOf(cartList.get(j).getItemid())
                        );
            }
        }
    }

    private class PlaceOrderLines extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        int count = 0;
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
            count = Integer.parseInt(params[3]);
            currentIndex = Integer.parseInt(params[4]);
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            String cost = totalCost.getText().toString();
            String arr[] = cost.split("\\s+");
            Log.i("ordamount", "_" + arr[1]);

            float ordertotal=Float.parseFloat(params[1])*Float.parseFloat(arr[1]);


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

            if (currentIndex == (count - 1)) {

                if(aBoolean==true) {

                    progressDialog.dismiss();
                    db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                    Intent orderPlace = new Intent(ViewCartActivity.this, OrderSuccessActivity.class);
                    startActivity(orderPlace);
                }
                else
                {
                    progressDialog.dismiss();
                    // db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                    Intent orderPlace = new Intent(ViewCartActivity.this, OrderFailureActivity.class);
                    startActivity(orderPlace);
                }
            }
        }
    }

    private class UpdateOrderLines extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        int count = 0;
        int currentIndex = 0;
        boolean orderupdateSuccess=false;

        public UpdateOrderLines(Context context) {
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
            count = Integer.parseInt(params[3]);
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
                jsonObject.put("special_instruction","");
                jsonObject.put("special_discount", "");
                jsonObject.put("company_id", sharedpreferences.getString("companyid", null));
                jsonObject.put("status", "New");
                jsonObject.put("created_by", sharedpreferences.getString("userid", null));
                jsonObject.put("creation_date", fDate);
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                jsonObject.put("updated_date", fDate);
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
                    orderupdateSuccess=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return orderupdateSuccess;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (currentIndex == (count - 1)) {

                /*progressDialog.dismiss();
                db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                Intent orderPlace = new Intent(ViewCartActivity.this, OrderSuccessActivity.class);
                startActivity(orderPlace);*/

                if(aBoolean==true) {

                    progressDialog.dismiss();
                    db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                    Intent orderPlace = new Intent(ViewCartActivity.this, OrderSuccessActivity.class);
                    startActivity(orderPlace);
                }
                else
                {
                    progressDialog.dismiss();
                    // db.deleteCart(Integer.parseInt(sharedpreferences.getString("userid", null)));
                    Intent orderPlace = new Intent(ViewCartActivity.this, OrderFailureActivity.class);
                    startActivity(orderPlace);
                }
            }
        }
    }


    private class FetchOpenOrdercust extends AsyncTask<String, String, String> {
        private Context mContext;
        String order_headerid = "";
        float or_total = 0.0f;

        public FetchOpenOrdercust(Context context) {
            this.mContext = context;
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                String url = getResources().getString(R.string.serverUrl) + getResources().getString(R.string.apiUrl) + getResources().getString(R.string.FetchOpenOrdercustUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", getResources().getString(R.string.clientId));
                jsonObject.put("companyId", sharedpreferences.getString("companyid", null));
                jsonObject.put("customer_id", sharedpreferences.getString("userid", null));

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
                    if (responseJsonObject.getBoolean("error") == false) {
                        JSONArray jsonArray = responseJsonObject.getJSONArray("menuItems");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        order_headerid = jsonObject1.getString("header_id");
                        or_total = Float.parseFloat(jsonObject1.getString("order_total"));
                        prev_orderstatus = jsonObject1.getString("order_status");
                    } else {
                        order_headerid = "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return order_headerid;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!(order_headerid.equals(""))) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("headerid", order_headerid);
                editor.commit();

                prev_ordertotal = or_total;
                prev_orderstatus = prev_orderstatus;
            } else {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("headerid", "");
                editor.commit();
            }
        }
    }

    public class UpdateOrderAsyncTask extends AsyncTask<String, String, Boolean> {
        boolean result = false;

        private Context mContext;

        public static final String PREFS_NAME = "Restaurant";
        SharedPreferences sharedpreferences;

        public UpdateOrderAsyncTask(Context context) {
            this.mContext = context;
            sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // CustomToast.makeText(getApplicationContext(),"No Orders found",CustomToast.LENGTH_SHORT, CustomToast.WARNING,false).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            String cost = totalCost.getText().toString();
            String arr[] = cost.split("\\s+");
            float comp_total=Float.parseFloat(arr[1]);

            try {
                String url = mContext.getResources().getString(R.string.serverUrl) + mContext.getResources().getString(R.string.apiUrl) + mContext.getResources().getString(R.string.UdpateOrderHeaderUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Transaction_Amount", "");
                jsonObject.put("Transaction_remarks", "");
                jsonObject.put("transaction_reference", "");
                if(prev_orderstatus.equals("Completed"))
                {
                    jsonObject.put("status", "New");

                }
                else {
                    jsonObject.put("status", prev_orderstatus);
                }
                //jsonObject.put("status", "New");

                jsonObject.put("updated_date", fDate);
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                if(sharedpreferences.contains("discountid"))
                {
                    if (!(sharedpreferences.getString("discountid", null).equals("") || sharedpreferences.getString("discountid", null).equals(null)))
                    {
                        jsonObject.put("DiscountID",sharedpreferences.getString("discountid",null));
                    }
                    else
                    {
                        jsonObject.put("DiscountID","");

                    }
                }
                else
                {
                    jsonObject.put("DiscountID","");
                }
                jsonObject.put("order_total", "" + comp_total);

                if(sharedpreferences.contains("tableid"))
                {
                    if (!(sharedpreferences.getString("tableid", null).equals("") || sharedpreferences.getString("tableid", null).equals(null)))
                    {
                        jsonObject.put("tableno", sharedpreferences.getString("tableid", null));
                    }
                    else
                    {
                        jsonObject.put("tableno","");

                    }
                }
                else
                {
                    jsonObject.put("DiscountID","");
                }
                jsonObject.put("header_id", sharedpreferences.getString("headerid", null));
                jsonObject.put("clientId", mContext.getResources().getString(R.string.clientId));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status = response.code();
                Log.i("Status", "_" + status);
                Log.i("UpdateReq", "_" + jsonObject.toString());

                if (status == 200) {
                    result = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            ArrayList<Cart> updatedCartList=new ArrayList<Cart>();
            for (int k = 0; k < cartList.size(); k++) {

                if(cartList.get(k).getStatus().equals("N")) {

                    updatedCartList.add(new Cart(cartList.get(k).getId(),
                            cartList.get(k).getUserid(),
                            cartList.get(k).getItemid(),
                            cartList.get(k).getItemname(),
                            cartList.get(k).getItemdescription(),
                            cartList.get(k).getItemprice(),
                            cartList.get(k).getItemquantity(),
                            cartList.get(k).getItemimage(),
                            "" + (cartList.get(k).getItemquantity()) * Float.parseFloat(cartList.get(k).getItemprice())
                            , cartList.get(k).getPricelistid(),
                            cartList.get(k).getStatus()));
                }
            }




            for (int j = 0; j < updatedCartList.size(); j++) {

                    new UpdateOrderLines(ViewCartActivity.this)
                            .execute(sharedpreferences.getString("headerid", null),
                                    String.valueOf(updatedCartList.get(j).getItemquantity()),
                                    updatedCartList.get(j).getPricelistid(),
                                    String.valueOf(updatedCartList.size()),
                                    String.valueOf(j),
                                    String.valueOf(updatedCartList.get(j).getItemid())
                            );

            }
        }
    }
}
