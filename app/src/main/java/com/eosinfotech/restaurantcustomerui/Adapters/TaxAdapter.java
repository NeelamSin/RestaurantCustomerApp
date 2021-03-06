package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.Models.TaxModel;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.OnRefreshViewListner;
import com.eosinfotech.restaurantcustomerui.db.DatabaseHandler;

import java.util.List;

public class TaxAdapter extends RecyclerView.Adapter<TaxAdapter.MyViewHolder> {

    private List<TaxModel> taxList;
    private Context context;
    DatabaseHandler db;
    private OnRefreshViewListner mRefreshListner;
    SharedPreferences sharedpreferences;
    public static final String PREFS_NAME = "Restaurant";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RegularTextView taxname, taxpercent;

        public MyViewHolder(View view) {
            super(view);
            taxname = (RegularTextView) view.findViewById(R.id.taxname);
            taxpercent = (RegularTextView) view.findViewById(R.id.taxpercent);
        }
    }


    public TaxAdapter(Context context , List<TaxModel> cartList) {
        this.context = context;
        this.taxList = cartList;
        db=new DatabaseHandler(context);
        mRefreshListner = (OnRefreshViewListner)context;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tax_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TaxModel tax = taxList.get(position);
        holder.taxname.setText(tax.getDesc());
        holder.taxpercent.setText("??? "+String.format("%.2f",(Float.parseFloat(tax.getCost())/100*tax.getTotal())));

    }

    @Override
    public int getItemCount() {
        return taxList.size();
    }
}
