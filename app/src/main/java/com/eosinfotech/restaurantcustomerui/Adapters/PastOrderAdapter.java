package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.TrackOrderActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.ViewCartActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.ViewOrderDetailActivity;
import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.Models.NewOrderDetailedCart;
import com.eosinfotech.restaurantcustomerui.Models.PastOrder;
import com.eosinfotech.restaurantcustomerui.OwnClasses.RoundRectCornerImageView;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class PastOrderAdapter extends RecyclerView.Adapter<PastOrderAdapter.ViewHolder> {

    private Context context;
    private List<PastOrder> personUtils;

    DatabaseHandler db;
    private List<NewOrderDetailedCart> cartView = new ArrayList<>();
    private NewOrderCartAdapter mAdapter;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;


    public PastOrderAdapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
        this.db=db;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(personUtils.get(position));

        holder.fItemCost.setText("₹ "+personUtils.get(position).getItemcost());

        PastOrder cart = personUtils.get(position);


        if(personUtils.get(position).getStatus().equals("New"))
        {
            holder.btn.setText("Track Order");
            holder.imageView.setImageResource(R.drawable.sample_track_order);
            holder.btn.setTextColor(context.getResources().getColor(R.color.colorHeadText));
        }
        else if(personUtils.get(position).getStatus().equals("Confirmed"))
        {
            holder.btn.setText("Track Order");
            holder.imageView.setImageResource(R.drawable.sample_track_order);
            holder.btn.setTextColor(context.getResources().getColor(R.color.colorHeadText));
        }
        else if(personUtils.get(position).getStatus().equals("InProgress"))
        {
            holder.btn.setText("Track Order");
            holder.imageView.setImageResource(R.drawable.sample_track_order);
            holder.btn.setTextColor(context.getResources().getColor(R.color.colorHeadText));
        }
        else if(personUtils.get(position).getStatus().equals("Completed"))
        {
            if (sharedpreferences.contains("ordertype")) {
                if (!(sharedpreferences.getString("ordertype", null).equals("") || sharedpreferences.getString("ordertype", null).equals(null))) {

                    if (!(sharedpreferences.getString("ordertype", null).equals("Dine In"))) {

                        holder.btn.setText("Track Order");
                        holder.imageView.setImageResource(R.drawable.sample_track_order);
                        holder.btn.setTextColor(context.getResources().getColor(R.color.colorHeadText));
                    } else {
                        holder.btn.setText("Proceed to Pay");
                        holder.imageView.setImageResource(R.drawable.sample_pay);
                        holder.btn.setTextColor(context.getResources().getColor(R.color.colorMain));
                    }
                }
            }



        }
        else if(personUtils.get(position).getStatus().equals("Closed"))
        {
            try {
                boolean isorderOpen=new FetchOpenOrdercust(context).execute().get();
                if(isorderOpen==true)
                {
                    holder.imageView.setImageResource(R.drawable.sample_bag);
                    holder.btn.setVisibility(View.INVISIBLE);
                }
                else
                {

                    holder.btn.setVisibility(View.VISIBLE);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            holder.btn.setText("Re-Order");
            holder.imageView.setImageResource(R.drawable.sample_bag);
            holder.btn.setTextColor(context.getResources().getColor(R.color.colorPrimaryText));
        }
        try {
            final ArrayList<Cart> itemslist=new FetchPastOrdersItems(context,db).execute(personUtils.get(position).getHeaderid()).get();
            Log.i("ItemsHeader","_"+personUtils.get(position).getHeaderid());
            Log.i("ItemsLIst","_"+itemslist.size());
            StringBuilder sb = new StringBuilder();
            float total1=0.0f;

            for (Cart s : itemslist)
            {
                sb.append(s.getItemname()+" x"+s.getItemquantity());
                sb.append(",");
                total1=total1+(s.getItemquantity())*Float.parseFloat(s.getItemprice());

            }
            holder.pName.setText(sb.toString());
            holder.pJobProfile.setText(sb.toString());
            // holder.fItemCost.setText("₹ "+total1);


            String mytime=cart.getItemnames();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date myDate = null;
            try {
                myDate = dateFormat.parse(mytime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy");
            String finalDate = timeFormat.format(myDate);


            holder.fitemDate.setText(finalDate);

            // holder.fItemCost.setText("₹ "+cart.getItemcost());

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ViewOrderDetailActivity.class);
                    intent.putExtra("header_id",personUtils.get(position).getHeaderid());
                    intent.putExtra("order_total",personUtils.get(position).getItemcost());
                    intent.putExtra("order_status",personUtils.get(position).getStatus());
                    intent.putExtra("DiscountID",personUtils.get(position).getDiscountID());

                    context.startActivity(intent);

                }
            });

            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(personUtils.get(position).getStatus().equals("New"))
                    {
                        Intent intent=new Intent(context,TrackOrderActivity.class);
                        context.startActivity(intent);
                    }
                    else if(personUtils.get(position).getStatus().equals("Confirmed"))
                    {
                        Intent intent=new Intent(context,TrackOrderActivity.class);
                        context.startActivity(intent);
                    }
                    else if(personUtils.get(position).getStatus().equals("InProgress"))
                    {
                        Intent intent=new Intent(context,TrackOrderActivity.class);
                        context.startActivity(intent);
                    }
                    else if(personUtils.get(position).getStatus().equals("Completed"))
                    {
                        Intent intent = new Intent(context, ViewOrderDetailActivity.class);
                        intent.putExtra("header_id",personUtils.get(position).getHeaderid());
                        intent.putExtra("order_total",personUtils.get(position).getItemcost());
                        intent.putExtra("order_status",personUtils.get(position).getStatus());
                        intent.putExtra("DiscountID",personUtils.get(position).getDiscountID());

                        context.startActivity(intent);
                    }
                    else if(personUtils.get(position).getStatus().equals("Closed"))
                    {
                        DatabaseHandler db=new DatabaseHandler(context);

                        for(Cart s : itemslist)
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
                                        ""+(s.getItemquantity()*Float.parseFloat(s.getItemprice()))
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
                                        ""+(s.getItemquantity()*Float.parseFloat(s.getItemprice()))
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

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public BoldTextView pName;
        public LightTextView pJobProfile;
        public RegularTextView fItemCost, fitemDate , btn;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            pName = (BoldTextView) itemView.findViewById(R.id.restaurant_Id);
            pJobProfile = (LightTextView) itemView.findViewById(R.id.address_Id);
            imageView = (ImageView) itemView.findViewById(R.id.foodImage_Id);
            fItemCost = (RegularTextView) itemView.findViewById(R.id.currency_Id);
            fitemDate = (RegularTextView) itemView.findViewById(R.id.date_Id);
            btn = (RegularTextView) itemView.findViewById(R.id.order_status);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.clickLayout);


            /*linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Neelam" , Toast.LENGTH_LONG).show();
                    *//*PastOrder cpu = (PastOrder) view.getTag();
                    Toast.makeText(view.getContext(), cpu.getItemName()+" is "+ cpu.getAllItems(), Toast.LENGTH_SHORT).show();*//*
                }
            });*/

        }
    }

    private class FetchOpenOrdercust extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        boolean openOrderExists=false;

        public FetchOpenOrdercust(Context context) {
            this.mContext = context;
        }


        @Override
        protected Boolean  doInBackground(String... params) {
            try {
                String url = context.getResources().getString(R.string.serverUrl) + context.getResources().getString(R.string.apiUrl) + context.getResources().getString(R.string.FetchOpenOrdercustUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", context.getResources().getString(R.string.clientId));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));

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
                    if(responseJsonObject.getBoolean("error")==false) {

                        openOrderExists=true;
                    }
                    else
                    {
                        openOrderExists=false;                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return openOrderExists;
        }

    }


    class FetchPastOrdersItems extends AsyncTask<String, String, ArrayList<Cart>>
    {
        Context mContext;
        private Context mContext1;
        DatabaseHandler db;
        ArrayList<Cart> list;
        ArrayList<Cart> cartlist1;


        public FetchPastOrdersItems(Context mContext1,DatabaseHandler db) {
            this.mContext1 = mContext1;
            this.db=db;
            list=new ArrayList<Cart>();
            cartlist1=new ArrayList<Cart>();

        }



        @Override
        protected ArrayList<Cart> doInBackground(String... params)
        {
            try {
                //String url="http://app.eosinfotech.com/Invention/v1/DigitalMenu/fetchOrderID";
                String url = context.getResources().getString(R.string.serverUrl) + context.getResources().getString(R.string.apiUrl) + context.getResources().getString(R.string.fetchOrderLines);
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
                        Log.i("Aarray","_"+menuResultsJsonArray.length());

                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);

                            cartlist1.add(new Cart(0,
                                    Integer.parseInt(menuResultsJsonObject.getString("created_by")),
                                    Integer.parseInt(menuResultsJsonObject.getString("item_id")),
                                    menuResultsJsonObject.getString("name"),
                                    "",
                                    menuResultsJsonObject.getString("SellingPrice"),
                                    Integer.parseInt(menuResultsJsonObject.getString("order_qty")),
                                    "",
                                    String.valueOf(Integer.parseInt(menuResultsJsonObject.getString("order_qty"))*Float.parseFloat(menuResultsJsonObject.getString("SellingPrice"))),
                                    menuResultsJsonObject.getString("pricelistid")));
                            //list.add(menuResultsJsonObject.getString("name")+" x"+menuResultsJsonObject.getString("order_qty"));


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

            return cartlist1;
        }

        @Override
        protected void onPostExecute(ArrayList<Cart> aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }

}
