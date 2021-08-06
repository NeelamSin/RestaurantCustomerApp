package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.ItemDetailedActivity;
import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.Models.FoodMenu;
import com.eosinfotech.restaurantcustomerui.Models.Item;
import com.eosinfotech.restaurantcustomerui.OwnClasses.AddToCartButton;
import com.eosinfotech.restaurantcustomerui.OwnClasses.BadgeTextView;
import com.eosinfotech.restaurantcustomerui.OwnClasses.FavouriteView;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.OnRefreshViewListner;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class RecommendedToYouAdapter extends RecyclerView.Adapter<RecommendedToYouAdapter.ViewHolder> {

    private Context context;
    private List<Item> personUtils;

    DatabaseHandler db;
    OnRefreshViewListner mRefreshListner;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;


    public RecommendedToYouAdapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
        db=new DatabaseHandler(context);
        mRefreshListner = (OnRefreshViewListner)context;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setTag(personUtils.get(position));

        final Item pu = personUtils.get(position);

        final boolean[] isFav = {false};
        final boolean[] isSel = {false};

        try {
            isFav[0] =new RecommendedToYouAdapter.fetchuserfavourite(context).execute(""+pu.getId()).get();
            if(isFav[0] ==true)
            {
                holder.favButton.Like();
            }
            else if(isFav[0] ==false)
            {
                holder.favButton.setUnlike();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        holder.favButton.setOnThumbUp(new FavouriteView.OnThumbUp() {
            @Override
            public void like(boolean like) {
                if(like ==false)
                {
                    new RecommendedToYouAdapter.deluserfavourite(context).execute(""+pu.getId());
                }
                else  if(like ==true)
                {
                    new RecommendedToYouAdapter.AddUserFavourite(context).execute(""+pu.getId());
                }
            }
        });


        holder.pName.setText(Html.fromHtml(pu.getCardName()));
        holder.pJobProfile.setText(Html.fromHtml(pu.getDes()));
        Picasso.get().load(pu.getImageResourceId()).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.imageView);

        holder.fItemCost.setText("Rs"+" "+pu.getPrice());

        int count = db.getCartItem(Integer.parseInt(personUtils.get(position).getId()), Integer.parseInt(sharedpreferences.getString("userid",null)));
        if(count>0)
        {
            holder.btn.setNumber(String.valueOf(db.getRecomCartItems(Integer.parseInt(sharedpreferences.getString("userid",null)),personUtils.get(position).getId())));
        }

        holder.btn.setOnClickListener(new AddToCartButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number =  holder.btn.getNumber();

                //textView.setText(number);
                holder.btn.setNumber(number);
                if(Integer.parseInt(number)!=0) {
                    int count = db.getCartItem(Integer.parseInt(personUtils.get(position).getId()), Integer.parseInt(sharedpreferences.getString("userid",null)));
                    if (count != 0) {
                        long result = db.editCart(new Cart(
                                0,
                                Integer.parseInt(sharedpreferences.getString("userid",null)),
                                Integer.parseInt(personUtils.get(position).getId()),
                                personUtils.get(position).getCardName(),
                                personUtils.get(position).getDes(),
                                personUtils.get(position).getPrice(),
                                Integer.parseInt(number),
                                personUtils.get(position).getImageResourceId(),
                                ""+(Integer.parseInt(number)*Float.parseFloat(personUtils.get(position).getPrice()))
                                ,personUtils.get(position).getPricelistid()));
                        if (result > 0) {
                            //mRefreshListner.refreshView(false,"","");
                            mRefreshListner.refreshView(false,"","");
                        }
                    } else {
                        long result = db.createCart(new Cart(
                                0,
                                Integer.parseInt(sharedpreferences.getString("userid",null)),
                                Integer.parseInt(personUtils.get(position).getId()),
                                personUtils.get(position).getCardName(),
                                personUtils.get(position).getDes(),
                                personUtils.get(position).getPrice(),
                                Integer.parseInt(number),
                                personUtils.get(position).getImageResourceId(),
                                ""+(Integer.parseInt(number)*Float.parseFloat(personUtils.get(position).getPrice()))
                                ,personUtils.get(position).getPricelistid()));                        //Toast.makeText(mContext, "Add_" + pizza.getId() + "_1", Toast.LENGTH_SHORT).show();
                        if (result > 0) {
                            mRefreshListner.refreshView(false,"","");
                            // mRefreshListner.refreshView(false,"","");
                        }
                    }
                }else
                {
                    //Toast.makeText(context,"_"+number,Toast.LENGTH_SHORT).show();


                    db.deleteItem(Integer.parseInt(personUtils.get(position).getId()),Integer.parseInt(sharedpreferences.getString("userid",null)));
                    mRefreshListner.refreshView(false,"","");
                    // mRefreshListner.refreshView(false,"","");
                }
            }
        });


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(context, ItemDetailedActivity.class);
                newIntent.putExtra("itemid", pu.getId());
                newIntent.putExtra("itemName", pu.getCardName());
                newIntent.putExtra("itemCalories", pu.getCalories());
                newIntent.putExtra("itemDescription" , pu.getDes());
                newIntent.putExtra("itemImage", pu.getImageResourceId());
                context.startActivity(newIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public BoldTextView pName;
        public LightTextView pJobProfile;
        public RegularTextView fItemCost;
        public ImageView imageView;
        public LinearLayout linearLayout;
        AddToCartButton btn;
        public BadgeTextView actual_price;
        public FavouriteView favButton;

        public ViewHolder(View itemView) {
            super(itemView);

            pName = (BoldTextView) itemView.findViewById(R.id.restaurant_Id);
            pJobProfile = (LightTextView) itemView.findViewById(R.id.address_Id);
            imageView = (ImageView) itemView.findViewById(R.id.foodImage_Id);
            fItemCost = (RegularTextView) itemView.findViewById(R.id.currency_Id);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.clickLayout);
            btn=(AddToCartButton)itemView.findViewById(R.id.number_button2);
            favButton = (FavouriteView) itemView.findViewById(R.id.tpv);
            //actual_price = (BadgeTextView) itemView.findViewById(R.id.actual_price);

            /*actual_price.addPiece(new BadgeTextView.Piece.Builder("$256")
                        .textColor(Color.parseColor("#008800"))
                        .superscript()
                        .strike()
                        .textSizeRelative(0.9f)
                        .style(Typeface.NORMAL)
                        .build());
            actual_price.display();*/
        }
    }



    private class AddUserFavourite extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        boolean result=false;

        public AddUserFavourite(Context context) {
            this.mContext = context;
        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(mContext,"Already added to favourites",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);


            try {
                String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.adduserfavouriteUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("Mainmenuid",params[0]);
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
                jsonObject.put("companyid",sharedpreferences.getString("companyid",null));
                jsonObject.put("created_by",sharedpreferences.getString("userid",null));
                jsonObject.put("updated_by",sharedpreferences.getString("userid",null));
                jsonObject.put("creation_date",fDate);
                jsonObject.put("updated_date",fDate);
                jsonObject.put("clientId","73gecKXtTSGCW1qsemzn");
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("Favourite Status","_"+status);
                Log.i("Favourite Request","_"+jsonObject.toString());

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    result=true;

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(result==true) {
                Toast.makeText(mContext, "Added to Favourites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void filterList(List<Item> filteredList) {
        personUtils = filteredList;
        notifyDataSetChanged();
    }



    private class fetchuserfavourite extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        boolean result=false;

        public fetchuserfavourite(Context context) {
            this.mContext = context;
        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);


            try {
                String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.fetchuserfavouriteUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("Mainmenuid",params[0]);
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
                jsonObject.put("companyId",sharedpreferences.getString("companyid",null));
                jsonObject.put("userId",sharedpreferences.getString("userid",null));
                jsonObject.put("clientId","73gecKXtTSGCW1qsemzn");
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("FetchFavourite Status","_"+status);
                Log.i("FetchFavourite Request","_"+jsonObject.toString());

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    boolean error=responseJsonObject.getBoolean("error");
                    if(error==false) {
                        result = true;
                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
    }


    private class deluserfavourite extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        boolean result=false;

        public deluserfavourite(Context context) {
            this.mContext = context;
        }



        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(mContext,"Removed from favourites",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params)
        {
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

            try {
                String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.deluserfavouriteUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("Mainmenuid",params[0]);
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
                jsonObject.put("companyid",sharedpreferences.getString("companyid",null));
                jsonObject.put("clientId","73gecKXtTSGCW1qsemzn");
                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int status=response.code();
                Log.i("DelFavourite Status","_"+status);
                Log.i("DelFavourite Request","_"+jsonObject.toString());

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    boolean error=responseJsonObject.getBoolean("error");
                    if(error==false) {
                        result = true;
                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(result==true) {
                Toast.makeText(mContext, "Removed from Favourites", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
