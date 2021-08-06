package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.ViewOrderDetail;
import com.eosinfotech.restaurantcustomerui.OwnClasses.CustomEditText;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.List;


public class ViewOrderDetailAdapter extends RecyclerView.Adapter<ViewOrderDetailAdapter.ViewHolder> {

    private Context context;
    private List<ViewOrderDetail> personUtils;

    public ViewOrderDetailAdapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_cart_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));

        ViewOrderDetail pu = personUtils.get(position);
        holder.pName.setText(pu.getItemName());
        holder.pJobProfile.setText(pu.getAllItems());
        holder.fItemCost.setText("Rs" +pu.getItemCost());
        holder.itemQuantity.setText("Quantity :"+" "+pu.getItemQuantity());
    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public BoldTextView pName;
        public CustomEditText pJobProfile;
        public RegularTextView fItemCost , itemQuantity;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            pName = (BoldTextView) itemView.findViewById(R.id.restaurant_Id);
            pJobProfile = (CustomEditText) itemView.findViewById(R.id.address_Id);
            fItemCost = (RegularTextView) itemView.findViewById(R.id.currency_Id);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.clickLayout);
            itemQuantity = (RegularTextView) itemView.findViewById(R.id.date_Id);

        }
    }

}
