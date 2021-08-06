package com.eosinfotech.restaurantcustomerui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.ViewCartActivity;
import com.eosinfotech.restaurantcustomerui.MealModule.MealDetailActivity;
import com.eosinfotech.restaurantcustomerui.Models.AddAddress;
import com.eosinfotech.restaurantcustomerui.R;
import java.util.List;

public class AddAddressAdapter extends RecyclerView.Adapter<AddAddressAdapter.MyViewHolder>{

    private List<AddAddress> moviesList;
    private Activity mContext;

    public static final String PREFS_NAME = "Restaurant";
    SharedPreferences sharedpreferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LightTextView editImage;
        public LightTextView deliveryType, completeAddress;
        public RelativeLayout relativeLayout;

        public MyViewHolder(View view) {
            super(view);
            deliveryType = (LightTextView) view.findViewById(R.id.deliveryType);
            completeAddress = (LightTextView) view.findViewById(R.id.completeAddress);
            editImage = (LightTextView) view.findViewById(R.id.imageViewOne);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);

        }
    }


    public AddAddressAdapter(List<AddAddress> moviesList , Activity context) {
        this.mContext = context;
        this.moviesList = moviesList;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_address, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AddAddress movie = moviesList.get(position);
        holder.deliveryType.setText(movie.getDeliveryType());
        holder.completeAddress.setText(movie.getFulLAddress());
        //Picasso.get().load(movie.getEditImage()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.editImage);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedpreferences.getString("ordertype", null).equals("MealBooking"))
                {
                    Intent gettingText = new Intent();
                    gettingText.putExtra("selectedOrderType", movie.getDeliveryType());
                    gettingText.putExtra("selectedFullAddress", movie.getFulLAddress());
                    gettingText.putExtra("selectedAddressId", movie.getAddressId());
                    mContext.setResult(5, gettingText);
                    mContext.finish();

                }
                else
                {
                    Intent gettingText = new Intent();
                    gettingText.putExtra("selectedOrderType", movie.getDeliveryType());
                    gettingText.putExtra("selectedFullAddress", movie.getFulLAddress());
                    gettingText.putExtra("selectedAddressId", movie.getAddressId());
                    mContext.setResult(5, gettingText);
                    mContext.finish();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
