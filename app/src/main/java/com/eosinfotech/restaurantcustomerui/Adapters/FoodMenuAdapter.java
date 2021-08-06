package com.eosinfotech.restaurantcustomerui.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.ItemDetailedActivity;
import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.Models.FoodMenu;
import com.eosinfotech.restaurantcustomerui.OwnClasses.AddToCartButton;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Neelam.
 */

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.CustomViewHolder> {
    private Context mContext;
    private List<FoodMenu> pizzas;
    public AddToCartButton elegantNumberButton;
    DatabaseHandler db;
    private OnRefreshViewListner mRefreshListner;
    private Bitmap bitmapImage;

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    public FoodMenuAdapter(List<FoodMenu> pizzas, Context mContext) {
        this.mContext = mContext;
        this.pizzas = pizzas;
        db=new DatabaseHandler(mContext);
        mRefreshListner = (OnRefreshViewListner)mContext;
        sharedpreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allmenu, parent, false);
        return new CustomViewHolder(itemView);
    }

    /**
     *  Populate the views with appropriate Text and Images
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final FoodMenuAdapter.CustomViewHolder holder, int position) {
        final FoodMenu pizza = pizzas.get(position);

        final boolean[] isFav = {false};
        final boolean[] isSel = {false};

        try {
            isFav[0] =new fetchuserfavourite(mContext).execute(""+pizza.getId()).get();
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
                    //isFav[0] =false;
                    new deluserfavourite(mContext).execute(""+pizza.getId());

                }
                else  if(like ==true)
                {
                    //isFav[0] =true;
                    new AddUserFavourite(mContext).execute(""+pizza.getId());

                }



            }
        });
        holder.name.setText(pizza.getName());
        holder.desc.setText(Html.fromHtml(pizza.getDescription()));
        holder.price.setText("Rs. "+pizza.getPrice());
        //holder.image.setImageResource(pizza.getImageResource());
        Log.i("Image","_"+pizza.getImageResource());
        Log.i("price","_"+pizza.getPrice());
        Log.i("Type","_"+pizza.getMenutype());
        Picasso.get().load(pizza.getImageResource()).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.image);

        holder.relativeLayoutNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(mContext, ItemDetailedActivity.class);
                newIntent.putExtra("itemid", pizza.getId());
                newIntent.putExtra("itemName", pizza.getName());
                newIntent.putExtra("itemCalories", pizza.getItemCaloriesDtls());
                newIntent.putExtra("itemDescription" , pizza.getDescription());
                newIntent.putExtra("itemImage", pizza.getImageResource());
                mContext.startActivity(newIntent);
            }
        });


        holder.relativeLayoutNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pizza.getMenutype().equals("Item")) {
                    Intent newIntent = new Intent(mContext, ItemDetailedActivity.class);
                    newIntent.putExtra("itemid", pizza.getId());
                    newIntent.putExtra("itemName", pizza.getName());
                    newIntent.putExtra("itemCalories", pizza.getItemCaloriesDtls());
                    newIntent.putExtra("itemDescription" , pizza.getDescription());
                    newIntent.putExtra("itemImage", pizza.getImageResource());
                    mContext.startActivity(newIntent);
                }
                else if(pizza.getMenutype().equals("Logical"))
                {
                   /* mRefreshListner.refreshView(true,String.valueOf(pizza.getId()),pizza.getLevel());*/
                    int nextlevel=Integer.parseInt(pizza.getLevel())+1;
                    mRefreshListner.refreshView(true,String.valueOf(pizza.getId()),String.valueOf(nextlevel));
                }
            }
        });

        if(pizza.getMenutype().equals("Logical"))
        {
            holder.elegantNumberButton.setVisibility(View.INVISIBLE);
            holder.favButton.setVisibility(View.INVISIBLE);
            holder.price.setVisibility(View.INVISIBLE);
        }
        else  if(pizza.getMenutype().equals("Item"))
        {
            holder.elegantNumberButton.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.VISIBLE);

        }

        int count = db.getCartItem(pizza.getId(), Integer.parseInt(sharedpreferences.getString("userid",null)));
        if(count>0)
        {
            holder.elegantNumberButton.setNumber(String.valueOf(db.getRecomCartItems(Integer.parseInt(sharedpreferences.getString("userid",null)),String.valueOf(pizza.getId()))));
        }

        holder.elegantNumberButton.setOnClickListener(new AddToCartButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = holder.elegantNumberButton.getNumber();
                //textView.setText(number);
                holder.elegantNumberButton.setNumber(number);
                if(Integer.parseInt(number)!=0) {
                    int count = db.getCartItem(pizza.getId(), Integer.parseInt(sharedpreferences.getString("userid",null)));
                    if (count != 0) {
                        //Toast.makeText(mContext,"_"+count,Toast.LENGTH_SHORT).show();
                        long result = db.editCart(new Cart(0, Integer.parseInt(sharedpreferences.getString("userid",null)), pizza.getId(), pizza.getName(), pizza.getDescription(), pizza.getPrice(), Integer.parseInt(number), "",""+(Integer.parseInt(number)*Float.parseFloat(pizza.getPrice())),pizza.getPricelistid()));
                        //Toast.makeText(mContext, "Edit_" + pizza.getId() + "_1", Toast.LENGTH_SHORT).show();
                        if (result > 0) {
                            mRefreshListner.refreshView(false,"","");
                        }
                    } else {
                        long result = db.createCart(new Cart(0, Integer.parseInt(sharedpreferences.getString("userid",null)), pizza.getId(), pizza.getName(), pizza.getDescription(), pizza.getPrice(), Integer.parseInt(number), "",""+(Integer.parseInt(number)*Float.parseFloat(pizza.getPrice())),pizza.getPricelistid()));
                        //Toast.makeText(mContext, "Add_" + pizza.getId() + "_1", Toast.LENGTH_SHORT).show();
                        if (result > 0) {
                            mRefreshListner.refreshView(false,"","");
                        }
                    }
                }else
                {
                    db.deleteItem(pizza.getId(),Integer.parseInt(sharedpreferences.getString("userid",null)));
                    mRefreshListner.refreshView(false,"","");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pizzas.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public RegularTextView price;
        public LightTextView desc;
        public BoldTextView name;
        public ImageView image, menu;
        public RelativeLayout relativeLayout;
        public LinearLayout linearLayout;
        public AddToCartButton elegantNumberButton;
        public FavouriteView favButton;
        public LinearLayout relativeLayoutNew;
        private ImageButton info;

        /**
         * Constructor to initialize the Views
         *
         * @param itemView
         */
        public CustomViewHolder(View itemView) {
            super(itemView);
            name = (BoldTextView) itemView.findViewById(R.id.name);
            desc = (LightTextView) itemView.findViewById(R.id.description);
            price = (RegularTextView) itemView.findViewById(R.id.itemPrice);
            image = (ImageView) itemView.findViewById(R.id.thumbnail);
            relativeLayoutNew = (LinearLayout) itemView.findViewById(R.id.clickLayout);
            //menu = (ImageView) itemView.findViewById(R.id.menuDots);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.statusAddCart);
            elegantNumberButton = (AddToCartButton) itemView.findViewById(R.id.number_button2);
            favButton = (FavouriteView) itemView.findViewById(R.id.tpv);
            //info=(ImageButton) itemView.findViewById(R.id.info);
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

    public void filterList(List<FoodMenu> filteredList) {
        pizzas = filteredList;
        notifyDataSetChanged();
    }
}
