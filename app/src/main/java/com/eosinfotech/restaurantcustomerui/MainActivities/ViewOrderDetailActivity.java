package com.eosinfotech.restaurantcustomerui.MainActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Adapters.CouponsAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.DetailCartAdapter;
import com.eosinfotech.restaurantcustomerui.Adapters.TaxAdapter_R;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.Models.CartLines;
import com.eosinfotech.restaurantcustomerui.Models.Coupons;
import com.eosinfotech.restaurantcustomerui.Models.TaxModel_R;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.CashPaymentSuccessActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.OrderFailureActivity;
import com.eosinfotech.restaurantcustomerui.StatusActivity.PaymentSuccessActivity;
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

public class ViewOrderDetailActivity extends AppCompatActivity implements OnRefreshViewListner {

    private ArrayList<CartLines> cartList = new ArrayList<>();
    private List<TaxModel_R> taxList = new ArrayList<>();
    private RecyclerView recyclerView,recyclerView1;
    private DetailCartAdapter mAdapter;
    private TaxAdapter_R tAdapter;
    private Context context;

    ProgressDialog progressDialog;
    RegularTextView itemCost,totalCost;

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;
    String status;

    String payeeAddress = "";
    String payeeName = "Neelam Sai";
    String transactionNote = "Order Payment";
   // String amount = "1";
    String currencyUnit = "INR";
    private String TAG = "MainActivity";

    RelativeLayout applyCoupons;
    BoldTextView couponlabel;
    ImageView couponarrow;
    RegularTextView coupondiscount,originalcost;

    int coupouncount;
    float coupounamount;
    String discountpercent,discountid;
    LinearLayout discountlinear;
    String contact_id;
    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orderdetail);

        sharedpreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);

        itemCost=(RegularTextView) findViewById(R.id.itemCost);
        totalCost=(RegularTextView) findViewById(R.id.totalCost);

        RegularTextView deliveryCost=(RegularTextView) findViewById(R.id.deliveryCost);
        applyCoupons = (RelativeLayout) findViewById(R.id.relativeLayout);
        couponlabel = (BoldTextView) findViewById(R.id.selectCoupon);
        couponarrow = (ImageView) findViewById(R.id.image1);
        coupondiscount=(RegularTextView)findViewById(R.id.discount);
        discountlinear=(LinearLayout)findViewById(R.id.discountlinear);
        coupondiscount=(RegularTextView)findViewById(R.id.discount);
        originalcost=(RegularTextView)findViewById(R.id.originalcost);
        couponlabel = (BoldTextView) findViewById(R.id.selectCoupon);
        couponarrow = (ImageView) findViewById(R.id.image1);

        originalcost.setVisibility(View.GONE);

        couponarrow.setVisibility(View.INVISIBLE);

        coupouncount=0;
        coupounamount=0.0f;

        if (sharedpreferences.contains("ordertype"))
        {
            if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null)))
            {
                if(sharedpreferences.getString("ordertype",null).contains("Dine"))
                {
                    deliveryCost.setVisibility(View.VISIBLE);
                }
                else
                {
                    deliveryCost.setVisibility(View.GONE);
                }
            }
        }

        deliveryCost.setText(sharedpreferences.getString("deliverycost",""));

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerView1.setLayoutManager(mLayoutManager1);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

        //avoid automatically appear android keyboard when activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BoldTextView placeOrder = (BoldTextView) findViewById(R.id.proceedToPay);

       // placeOrder.setText("Re-Order");
        status=getIntent().getExtras().getString("order_status");
        if(status.equals("New"))
        {
            placeOrder.setText("Track Order");
        }
        else if(status.equals("Confirmed"))
        {
            placeOrder.setText("Track Order");

        }
        else if(status.equals("InProgress"))
        {
            placeOrder.setText("Track Order");
        }
        else if(status.equals("Completed"))
        {
            if (sharedpreferences.contains("ordertype")) {
                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                    if (!(sharedpreferences.getString("ordertype", null).equals("Dine In"))) {

                        placeOrder.setText("Track Order");

                } else {
                    placeOrder.setText("Proceed To Pay");

                }
            }
            }

        }
        else if(status.equals("Closed"))
        {
            placeOrder.setText("Re-Order");

        }
        context=this;
        totalCost.setText("₹ " + getIntent().getExtras().getString("order_total"));



        new FetchPastOrdersItems(context).execute(getIntent().getExtras().getString("header_id"),getIntent().getExtras().getString("order_total"));

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(status.equals("New"))
                {
                    Intent intent=new Intent(context,TrackOrderActivity.class);
                    context.startActivity(intent);
                }
                else if(status.equals("Confirmed"))
                {
                    Intent intent=new Intent(context,TrackOrderActivity.class);
                    context.startActivity(intent);
                }
                else if(status.equals("InProgress"))
                {
                    Intent intent=new Intent(context,TrackOrderActivity.class);
                    context.startActivity(intent);
                }
                else if(status.equals("Completed"))
                {

                    if (sharedpreferences.contains("ordertype")) {
                        if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                            if (!(sharedpreferences.getString("ordertype", null).equals("Dine In"))) {

                                Intent intent = new Intent(context, TrackOrderActivity.class);
                            context.startActivity(intent);
                        } else {
                            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ViewOrderDetailActivity.this);
                            View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
                            mBottomSheetDialog.setContentView(sheetView);
                            LinearLayout linearCOCD = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_cocd);
                            LinearLayout linearOther = (LinearLayout) mBottomSheetDialog.findViewById(R.id.fragment_history_bottom_sheet_other);

                            linearCOCD.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    String cost = totalCost.getText().toString();
                                    String arr[] = cost.split("\\s+");
                                    Log.i("ordamount", "_" + arr[1]);

                                    Intent orderPlace = new Intent(ViewOrderDetailActivity.this, CashPaymentSuccessActivity.class);
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

                                    Uri uri = Uri.parse("upi://pay?pa=" + payeeAddress + "&pn=" + payeeName + "&tn=" + transactionNote + "&am=" + (totalCost.getText().toString()).replaceAll("₹ ", "") + "&cu=" + currencyUnit);
                                    Log.d(TAG, "onClick: uri: " + uri);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    //intent.setClassName("in.org.npci.upiapp","in.org.npci.upiapp.HomeActivity");
                                    startActivityForResult(intent, 1);
                                }
                            });
                            mBottomSheetDialog.show();
                        }
                    }
                    }


                }
                else if(status.equals("Closed"))
                {
                    DatabaseHandler db=new DatabaseHandler(context);

                    for(CartLines s : cartList)
                    {
                        int count = db.getCartItem(s.getId(), Integer.parseInt(sharedpreferences.getString("userid",null)));

                        if (count>0) {
                            long result = db.editCart(new Cart(
                                    0,
                                    s.getUserid(),
                                    s.getItemid(),
                                    s.getItemname(),
                                    s.getItemdescription(),
                                    s.getItemprice(),
                                    s.getItemquantity(),
                                    s.getItemimage(),
                                    ""+(s.getItemquantity()*Integer.parseInt(s.getItemprice()))
                                    ,s.getPricelistid()));
                            if (result > 0) {

                                // Log.i("Edititem","_"+itemslist.size());
                                //Log.i("Edititem","_"+itemslist.size());


                            }
                        } else {
                            long result = db.createCart(new Cart(
                                    0,
                                    s.getUserid(),
                                    s.getItemid(),
                                    s.getItemname(),
                                    s.getItemdescription(),
                                    s.getItemprice(),
                                    s.getItemquantity(),
                                    s.getItemimage(),
                                    ""+(s.getItemquantity()*Integer.parseInt(s.getItemprice()))
                                    ,s.getPricelistid()));
                            if (result > 0) {

                                // Log.i("Additem","_"+itemslist.size());

                            }
                        }


                    }
                    Intent buttonBill = new Intent(context, ViewCartActivity.class);
                    context.startActivity(buttonBill);
                }


            }
        });
    }

    private class FetchSetting extends AsyncTask<String, String, String> {
        private Context mContext;
        String upi="",restaurantname="",ImagePath="",cvisits="0",camt="0.00";;

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
            payeeAddress=upi;
            payeeName=restaurantname;

            coupounamount=Float.parseFloat(camt);
            coupouncount=Integer.parseInt(cvisits);


            try {
                ArrayList<String> countarr=new FetchPastOrdersCount(ViewOrderDetailActivity.this).execute().get();
                cnt = countarr.size();
            }catch(Exception e)
            {

            }

        }
    }

    private class FetchPastOrdersCount extends AsyncTask<String, String, ArrayList<String>> {


        private Context mContext;
        ArrayList<String> ordercount = new ArrayList<String>();

        public FetchPastOrdersCount(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Toast.makeText(getApplicationContext(),"No Orders found",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
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
                            ordercount.add(menuResultsJsonObject.getString("order_date"));
                        }
                    } else {
                        publishProgress("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ordercount;
        }

        @Override
        protected void onPostExecute(ArrayList<String> aBoolean) {
            super.onPostExecute(aBoolean);

            String cartamt = totalCost.getText().toString();
            String arramt[] = cartamt.split("\\s+");

            Log.i("Discount Starts", "Discount Starts");

            applyCoupons.setVisibility(View.GONE);
            couponlabel.setText("APPLY COUPON");
            discountlinear.setVisibility(View.GONE);
            couponarrow.setVisibility(View.VISIBLE);

            Log.i("totalcost",""+arramt[1]);
            Log.i("coupounamount", ""+coupounamount);

            if(getIntent().getExtras().getString("order_status").equals("Closed"))
            {
                if (!(getIntent().getExtras().getString("DiscountID").equals("")) && !(getIntent().getExtras().getString("DiscountID").equals("0"))) {

                    applyCoupons.setVisibility(View.VISIBLE);
                    discountlinear.setVisibility(View.VISIBLE);
                    couponarrow.setVisibility(View.GONE);
                    originalcost.setVisibility(View.VISIBLE);

                    discountid = getIntent().getExtras().getString("DiscountID");
                    try {
                        discountpercent = new GetCoupons(ViewOrderDetailActivity.this).execute().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    couponlabel.setText("COUPON APPLIED - BOUFFAGE " + discountpercent);
                    String cartamt1 = originalcost.getText().toString();
                    String arramt1[] = cartamt1.split("\\s+");
                    String cost = "" + Float.parseFloat(arramt1[1]);


                    float originatamt=Float.parseFloat(arramt1[1]);
                    float discountedamount = originatamt-Float.parseFloat(getIntent().getExtras().getString("order_total"));

                    coupondiscount.setText("₹ " + discountedamount);
                    //originalcost.setText("₹ " + cost);
                    //float finalprice = Float.parseFloat(cost) - discountedamount;
                    //totalCost.setText("₹ " + finalprice);
                    originalcost.setPaintFlags(originalcost.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                }
            }
            else if(getIntent().getExtras().getString("order_status").equals("Completed"))
            {
            if (ordercount.size() >= coupouncount || Float.parseFloat(arramt[1]) >= coupounamount) {
                originalcost.setVisibility(View.GONE);

                applyCoupons.setVisibility(View.VISIBLE);
                discountlinear.setVisibility(View.GONE);
                couponarrow.setVisibility(View.VISIBLE);

                applyCoupons.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent couponIntent = new Intent(ViewOrderDetailActivity.this , CouponsActivity.class);
                        couponIntent.putExtra("totalamt",getIntent().getExtras().getString("order_total"));
                        startActivityForResult(couponIntent,3);
                        //startActivity(couponIntent);
                    }
                });

                }
            }
        }
    }


    private class GetCoupons extends AsyncTask<String, String, String>
    {
        private Context mContext;
        String discountper="0";

        public GetCoupons(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Toast.makeText(getApplicationContext(),"No Saved Address",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params)
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

                            if(menuResultsJsonObject.getString("ID").equals(getIntent().getExtras().getString("DiscountID")))
                            {
                                discountper=menuResultsJsonObject.getString("Discount_Percent");
                            }
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
            return discountper;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
        Log.d(TAG, "onActivityResult: resultCode: " + resultCode);
        //txnId=UPI20b6226edaef4c139ed7cc38710095a3&responseCode=00&ApprovalRefNo=null&Status=SUCCESS&txnRef=undefined
        //txnId=UPI608f070ee644467aa78d1ccf5c9ce39b&responseCode=ZM&ApprovalRefNo=null&Status=FAILURE&txnRef=undefined
        Log.d(TAG, "onActivityResult: data: " + resultCode);

        if(resultCode==3)
        {

            discountpercent=data.getStringExtra("discountpercent");
            discountid=data.getStringExtra("discountid");
            String totalamt=data.getStringExtra("totalamt");

            originalcost.setVisibility(View.VISIBLE);

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
            String txnRef = resarr[1];

            String[] resarrref = res.split("txnId=");
            String txnId = resarrref[1];

            String[] resarrref1 = txnId.split("&responseCode");
            String txnId1 = resarrref1[0];

            Log.i("Txn", "" + txnRef);
            Log.i("TxnId", "" + txnId1);

            if (res.toLowerCase().contains(search.toLowerCase())) {

                Intent orderPlace = new Intent(ViewOrderDetailActivity.this, PaymentSuccessActivity.class);
                orderPlace.putExtra("txnRef", txnRef);
                orderPlace.putExtra("ordertotal", totalCost.getText().toString());
                orderPlace.putExtra("txnRemarks", "SUCCESS, " + txnRef);
                startActivity(orderPlace);

            } else {
                Intent orderFailure = new Intent(ViewOrderDetailActivity.this, OrderFailureActivity.class);
                orderFailure.putExtra("txnRef", txnRef);
                orderFailure.putExtra("ordertotal", totalCost.getText().toString());
                orderFailure.putExtra("txnRemarks", "FAILURE, " + txnRef);
                startActivity(orderFailure);
            }
        }
    }
    }

    private class RefreshTax extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        float taxtotal=0.0f;

        public RefreshTax(Context context) {
            this.mContext = context;
        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"Tax Details Not Available",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.taxUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("sessionid","1");
                jsonObject.put("company_id",Integer.parseInt(sharedpreferences.getString("companyid",null)));
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
                    taxList.clear();
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    if(!(responseJsonObject.get("company_id") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("company_id");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            taxList.add(new TaxModel_R(menuResultsJsonObject.getString("taxtype"),menuResultsJsonObject.getString("taxpercent"),itemCost.getText().toString()));
                            taxtotal=taxtotal+Float.parseFloat(menuResultsJsonObject.getString("taxpercent"))/100*Integer.parseInt(itemCost.getText().toString());
                            Log.i("Tax","_"+taxtotal);
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
            tAdapter = new TaxAdapter_R(context , taxList);
            tAdapter.notifyDataSetChanged();
            recyclerView1.setAdapter(tAdapter);
//            totalCost.setText("₹ "+String.format("%.2f",(Float.parseFloat(itemCost.getText().toString())+taxtotal+20.00)));

        }
    }
    private class FetchTax extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        float taxtotal=0.0f;

        public FetchTax(Context context) {
            this.mContext = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ViewOrderDetailActivity.this);
            progressDialog.setMessage("Fetching Order Details...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(),"Tax Details Not Available",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            try {
                String url=getResources().getString(R.string.serverUrl)+getResources().getString(R.string.apiUrl)+getResources().getString(R.string.taxUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("sessionid","1");
                jsonObject.put("company_id",Integer.parseInt(sharedpreferences.getString("companyid",null)));
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
                    if(!(responseJsonObject.get("company_id") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("company_id");
                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            taxList.add(new TaxModel_R(menuResultsJsonObject.getString("taxtype"),menuResultsJsonObject.getString("taxpercent"),itemCost.getText().toString()));
                           taxtotal=taxtotal+Float.parseFloat(menuResultsJsonObject.getString("taxpercent"))/100*Float.parseFloat(itemCost.getText().toString());
                            Log.i("Tax","_"+taxtotal);
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
            progressDialog.dismiss();
            tAdapter = new TaxAdapter_R(context , taxList);
            tAdapter.notifyDataSetChanged();
            recyclerView1.setAdapter(tAdapter);

            if (sharedpreferences.contains("ordertype")) {
                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {
                    if(sharedpreferences.getString("ordertype",null).contains("Dine"))
                    {
                        originalcost.setText("₹ "+String.format("%.2f",(Float.parseFloat(itemCost.getText().toString())+taxtotal+Float.parseFloat(sharedpreferences.getString("deliverycost","")))));
                    }
                    else
                    {
                        originalcost.setText("₹ "+String.format("%.2f",(Float.parseFloat(itemCost.getText().toString())+taxtotal+0.00)));
                    }
                }
            }

            new FetchSetting(ViewOrderDetailActivity.this).execute();


            //totalCost.setText("₹ "+String.format("%.2f",(Float.parseFloat(itemCost.getText().toString())+taxtotal+Float.parseFloat(sharedpreferences.getString("deliverycost","")))));


        }
    }

    class FetchPastOrdersItems extends AsyncTask<String, String, ArrayList<CartLines>>
    {
        Context mContext;
        private Context mContext1;
        DatabaseHandler db;
        ArrayList<String> list;
        float total1=0.0f;

        public FetchPastOrdersItems(Context mContext1) {
            this.mContext1 = mContext1;
            list=new ArrayList<String>();
        }



        @Override
        protected ArrayList<CartLines> doInBackground(String... params)
        {
            try {
                String url= context.getResources().getString(R.string.serverUrl) + context.getResources().getString(R.string.apiUrl) + context.getResources().getString(R.string.fetchOrderLines);
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
                            cartList.add(new CartLines(0,
                                    Integer.parseInt(menuResultsJsonObject.getString("created_by")),
                                    Integer.parseInt(menuResultsJsonObject.getString("item_id")),
                                    menuResultsJsonObject.getString("name"),
                                    "",
                                    menuResultsJsonObject.getString("SellingPrice"),
                                    Integer.parseInt(menuResultsJsonObject.getString("order_qty")),
                                    "",
                                    String.valueOf(Integer.parseInt(menuResultsJsonObject.getString("order_qty"))*Float.parseFloat(menuResultsJsonObject.getString("SellingPrice"))),
                                    menuResultsJsonObject.getString("pricelistid"),
                                    menuResultsJsonObject.getString("line_id"),
                                    menuResultsJsonObject.getString("status")));

                            total1=total1+(Integer.parseInt(menuResultsJsonObject.getString("order_qty"))*Float.parseFloat(menuResultsJsonObject.getString("SellingPrice")));

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
        protected void onPostExecute(ArrayList<CartLines> aBoolean) {
            super.onPostExecute(aBoolean);

            Log.i("cartlist","_"+cartList.size());


            //cartList=db.getAllCartItems(1);
            mAdapter = new DetailCartAdapter(context , cartList,status);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);

            itemCost.setText(""+total1);

            new FetchTax(ViewOrderDetailActivity.this).execute();



        }
    }

    @Override
    public void refreshView(boolean issubmenu, String linktolineid, String level)
    {
        cartList.clear();
        new FetchPastOrdersItems(context).execute(getIntent().getExtras().getString("header_id"),getIntent().getExtras().getString("order_total"));
        if(cartList.size()>0) {
            mAdapter = new DetailCartAdapter(context, cartList,status);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(mAdapter);


            new RefreshTax(ViewOrderDetailActivity.this).execute();
        }
        else
        {
            Intent intent=new Intent(ViewOrderDetailActivity.this,DashboardActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent backPressed = new Intent(ViewOrderDetailActivity.this , DashboardActivity.class);
        startActivity(backPressed);
    }

}
