package com.eosinfotech.restaurantcustomerui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.ViewCartActivity;
import com.eosinfotech.restaurantcustomerui.Models.AddAddress;
import com.eosinfotech.restaurantcustomerui.Models.Coupons;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.MyViewHolder> {

    private List<Coupons> moviesList;
    private Activity mContext;
    String totalamt;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public BoldTextView couponCode;
        public RegularTextView applyCoupon, couponDescription;

        public MyViewHolder(View view) {
            super(view);
            couponCode = (BoldTextView) view.findViewById(R.id.couponCode);
            applyCoupon = (RegularTextView) view.findViewById(R.id.applyButton);
            couponDescription = (RegularTextView) view.findViewById(R.id.couponDescription);
        }
    }


    public CouponsAdapter(List<Coupons> moviesList , Activity context,String totalamt) {
        this.mContext = context;
        this.moviesList = moviesList;
        this.totalamt=totalamt;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupons, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Coupons movie = moviesList.get(position);
        holder.couponCode.setText("BOUFFAGE "+movie.getCouponPercent());
        holder.couponDescription.setText(movie.getCouponDescription());
        //Picasso.get().load(movie.getEditImage()).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.editImage);
        holder.applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appliedCoupon = new Intent();
                //appliedCoupon.putExtra("getPercent", );
                appliedCoupon.putExtra("discountpercent",movie.getCouponPercent());
                appliedCoupon.putExtra("discountid",movie.getCouponId());
                appliedCoupon.putExtra("totalamt",""+totalamt);

                mContext.setResult(3, appliedCoupon);
                mContext.finish();
               // mContext.startActivity(appliedCoupon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
