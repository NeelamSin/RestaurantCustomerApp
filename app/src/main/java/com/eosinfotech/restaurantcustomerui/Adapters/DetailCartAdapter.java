package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.CartLines;
import com.eosinfotech.restaurantcustomerui.OwnClasses.AddToCartButton;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.OnRefreshViewListner;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailCartAdapter extends RecyclerView.Adapter<DetailCartAdapter.MyViewHolder> {

    private List<CartLines> cartList;
    private Context context;
    DatabaseHandler db;
    OnRefreshViewListner mRefreshListner;
    String status;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RegularTextView itemName, itemCost, itemQuantity,update,delete,itemstatus;
        public AddToCartButton addToCart;

        public MyViewHolder(View view) {
            super(view);
            itemName = (RegularTextView) view.findViewById(R.id.itemName);
            itemQuantity = (RegularTextView) view.findViewById(R.id.itemQuantity);
            itemCost = (RegularTextView) view.findViewById(R.id.itemCost);
            addToCart = (AddToCartButton) view.findViewById(R.id.number_button3);
            update = (RegularTextView) view.findViewById(R.id.updateitem);
            delete = (RegularTextView) view.findViewById(R.id.deleteitem);
            itemstatus = (RegularTextView) view.findViewById(R.id.itemStatus);

        }
    }


    public DetailCartAdapter(Context context , List<CartLines> cartList, String status) {
        this.context = context;
        this.cartList = cartList;
        db=new DatabaseHandler(context);
        mRefreshListner = (OnRefreshViewListner)context;
        this.status=status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CartLines cart = cartList.get(position);
        holder.itemName.setText(cart.getItemname());
        holder.itemstatus.setText(cart.getOrderstatus());
        holder.itemQuantity.setText(context.getString(R.string.item_quantity , String.valueOf(cart.getItemquantity())));
        holder.itemCost.setText(context.getString(R.string.item_cost , cart.getItemtotalprice()));
        holder.addToCart.setNumber(""+cart.getItemquantity());

        if(status.equals("InProgress") || status.equals("Completed") || status.equals("Closed"))
        {
            holder.addToCart.setVisibility(View.GONE);
            holder.update.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);

        }
        else
        {
            holder.addToCart.setVisibility(View.VISIBLE);
            holder.update.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new UpdateOrderLines(context).execute("",
                        cart.getLineid(),
                        ""+holder.addToCart.getNumber(),
                        ""+cart.getItemid(),
                        cart.getPricelistid(),
                        status);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DeleteOrderLines(context).execute("",
                        cart.getLineid(),
                        ""+holder.addToCart.getNumber(),
                        ""+cart.getItemid(),
                        cart.getPricelistid(),
                        status);
            }
        });


        holder.addToCart.setOnClickListener(new AddToCartButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = holder.addToCart.getNumber();
                //textView.setText(number);
                holder.addToCart.setNumber(number);
                if(Integer.parseInt(number)!=0) {

             //   mRefreshListner.refreshView(false,"","");

                }
            }
        });


        Log.i("Price","_"+cart.getItemprice());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    class UpdateOrderLines extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        int count = 0;
        int currentIndex = 0;

        public static final String PREFS_NAME = "Restaurant";
        SharedPreferences sharedpreferences;

        public UpdateOrderLines(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            int total = 0;
            count = Integer.parseInt(params[3]);
            currentIndex = Integer.parseInt(params[4]);
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
            sharedpreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            try {
                String url = mContext.getResources().getString(R.string.serverUrl) + mContext.getString(R.string.apiUrl) + mContext.getString(R.string.UpdateOrderLinesUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("header_id", sharedpreferences.getString("headerid", null));
                jsonObject.put("line_id", params[1]);
                jsonObject.put("item_id", params[2]);
                jsonObject.put("order_qty", params[3]);
                jsonObject.put("pricelistid", params[4]);
                jsonObject.put("special_instruction", "");
                jsonObject.put("special_discount", "");
                jsonObject.put("status", params[5]);
                jsonObject.put("updated_by", sharedpreferences.getString("userid", null));
                jsonObject.put("updated_date", fDate);
                jsonObject.put("staffid", "0");
                jsonObject.put("clientId", mContext.getResources().getString(R.string.clientId));
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

                if (status == 200) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Toast.makeText(mContext, "Upated Order Item",Toast.LENGTH_SHORT).show();

        }
    }

    class DeleteOrderLines extends AsyncTask<String, String, Boolean> {
        private Context mContext;
        int count = 0;
        int currentIndex = 0;

        public static final String PREFS_NAME = "Restaurant";
        SharedPreferences sharedpreferences;

        public DeleteOrderLines(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            int total = 0;
            count = Integer.parseInt(params[3]);
            currentIndex = Integer.parseInt(params[4]);
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
            sharedpreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            try {
                String url = mContext.getResources().getString(R.string.serverUrl) + mContext.getString(R.string.apiUrl) + mContext.getString(R.string.UpdateOrderLinesDeleteUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("header_id", sharedpreferences.getString("headerid", null));
                jsonObject.put("line_id", params[1]);
                jsonObject.put("clientId", mContext.getResources().getString(R.string.clientId));
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

                if (status == 200) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Toast.makeText(mContext, "Deleted Order Item", Toast.LENGTH_SHORT).show();
            mRefreshListner.refreshView(false,"","");

        }
    }
}


