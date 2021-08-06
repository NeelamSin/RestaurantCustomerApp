package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.eosinfotech.restaurantcustomerui.BookingType.TableCategoryActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.Models.ItemRating;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.CashPaymentSuccessActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemRatingAdapter extends RecyclerView.Adapter<ItemRatingAdapter.MyHolder> {

    private ArrayList<ItemRating> itemRatings;
    private Context c;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    public ItemRatingAdapter(Context c,ArrayList<ItemRating> itemRatings) {
        this.itemRatings = itemRatings;
        this.c = c;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.item_rating_each_item,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        ItemRating s=itemRatings.get(position);

        holder.itemName.setText(s.getItemName());
        //holder.img.setImageResource(s.getItemImage());
        Picasso.get().load(s.getItemImage()).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.img);
        holder.ratingBar.setRating(s.getItemRating());

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                new AddItemRating(c).execute(itemRatings.get(position).getItemId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemRatings.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        BoldTextView itemName;
        ImageView img;
        RatingBar ratingBar;

        public MyHolder(View itemView) {
            super(itemView);

            itemName= (BoldTextView) itemView.findViewById(R.id.itemName);
            img= (ImageView) itemView.findViewById(R.id.foodImage_Id);
            ratingBar= (RatingBar) itemView.findViewById(R.id.RatingBar_Id);

        }
    }

    private class AddItemRating extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;

        public AddItemRating(Context context) {
            this.mContext = context;
        }



        @Override
        protected Boolean doInBackground(String... params)
        {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);
            String sDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            //String space=fDate+"\t"+params[2];
            try {
                sharedpreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.addItemratingUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
                jsonObject.put("itemid",params[0]);
                jsonObject.put("orderid",sharedpreferences.getString("headerid",null));
                jsonObject.put("rating_given","0");
                jsonObject.put("review_comments","Test");
                jsonObject.put("company_id",sharedpreferences.getString("companyid",null));
                jsonObject.put("created_by",sharedpreferences.getString("userid",null));
                jsonObject.put("creation_date",sDate);
                jsonObject.put("updated_date",sDate);
                jsonObject.put("updated_by",sharedpreferences.getString("userid",null));

                jsonObject.put("clientId",mContext.getResources().getString(R.string.clientId));
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("Status","_"+status);
                Log.i("TableRequest",""+jsonObject.toString());

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
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

            Log.i("rating","rating added");

           /* SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("tableid", "");
            editor.putString("headerid", "");
            editor.putString("ordertype", "");
            editor.putString("tableconfirmed", "");
            editor.commit();
            Intent intent=new Intent(mContext,TableCategoryActivity.class);
            mContext.startActivity(intent);
            */
        }
    }

}
