package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.Models.Cart;
import com.eosinfotech.restaurantcustomerui.OwnClasses.ViewPagerCustomDuration;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;
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

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.mrszero , R.drawable.mrsthree , R.drawable.mrstwo , R.drawable.mrsfour};

    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_custom_viewpager, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0){
                    Toast.makeText(context, "", Toast.LENGTH_LONG).show();
                } else if(position == 1){
                    Toast.makeText(context, "", Toast.LENGTH_LONG).show();
                } else if(position == 2){
                    Toast.makeText(context, "", Toast.LENGTH_LONG).show();
                } else if(position == 3){
                    Toast.makeText(context, "", Toast.LENGTH_LONG).show();
                }
            }
        });*/


        ViewPagerCustomDuration vp = (ViewPagerCustomDuration) container;
        vp.addView(view , 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPagerCustomDuration vp = (ViewPagerCustomDuration) container;
        View view = (View) object;
        vp.removeView(view);
    }


    private class BannerItems extends AsyncTask<String, String, Boolean>
    {
        private Context mContext;
        String image_url="";

        public BannerItems(Context context) {
            this.mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... params)
        {

            try {
                sharedpreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

                String url=mContext.getResources().getString(R.string.serverUrl)+mContext.getResources().getString(R.string.apiUrl)+mContext.getResources().getString(R.string.addItemratingUrl);
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("customer_id",sharedpreferences.getString("userid",null));
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

                if(status==200)
                {
                    JSONObject responseJsonObject=new JSONObject(response.body().string());
                    if(!(responseJsonObject.get("companyDetails") instanceof String))
                    {
                        JSONArray menuResultsJsonArray=responseJsonObject.getJSONArray("companyDetails");
                        Log.i("Aarray","_"+menuResultsJsonArray.length());

                        for(int i=0;i<menuResultsJsonArray.length();i++)
                        {
                            JSONObject menuResultsJsonObject=menuResultsJsonArray.getJSONObject(i);
                            image_url= "http://bouffage.eosinfotech.com/Restaurant-Control-Panel/uploadfiles/"+menuResultsJsonObject.getString("adv_imagenm");
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
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Log.i("banner","Banner");
        }
    }
}
