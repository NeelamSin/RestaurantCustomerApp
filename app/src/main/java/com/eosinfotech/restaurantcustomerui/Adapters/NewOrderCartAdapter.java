package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.NewOrderDetailedCart;
import com.eosinfotech.restaurantcustomerui.R;

import java.util.List;

public class NewOrderCartAdapter extends RecyclerView.Adapter<NewOrderCartAdapter.MyViewHolder> {

    private List<NewOrderDetailedCart> cartList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RegularTextView itemName, itemCost, itemQuantity;

        public MyViewHolder(View view) {
            super(view);
            itemName = (RegularTextView) view.findViewById(R.id.itemName);
            itemQuantity = (RegularTextView) view.findViewById(R.id.itemQuantity);
            itemCost = (RegularTextView) view.findViewById(R.id.itemCost);
        }
    }


    public NewOrderCartAdapter(Context context , List<NewOrderDetailedCart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_order_cart_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NewOrderDetailedCart cart = cartList.get(position);
        holder.itemName.setText(cart.getItemname());
        holder.itemQuantity.setText(context.getString(R.string.item_quantity , cart.getItemquantity()));
        holder.itemCost.setText(context.getString(R.string.item_cost , cart.getItemcost()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}

