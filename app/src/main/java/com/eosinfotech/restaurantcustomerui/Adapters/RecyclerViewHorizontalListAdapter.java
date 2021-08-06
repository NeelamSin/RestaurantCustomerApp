
package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.AllMenuItemsActivity;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.Models.Category;
import com.eosinfotech.restaurantcustomerui.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


public class RecyclerViewHorizontalListAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalListAdapter.GroceryViewHolder>{
    private List<Category> horizontalGrocderyList;
    Context context;

    public RecyclerViewHorizontalListAdapter(List<Category> horizontalGrocderyList, DashboardActivity context){
        this.horizontalGrocderyList= horizontalGrocderyList;
        this.context = context;
    }

    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_view, parent, false);
        GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, final int position) {

        Picasso.get().load(horizontalGrocderyList.get(position).getImageResource()).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into( holder.imageView);
        holder.txtview.setText(horizontalGrocderyList.get(position).getPreviousHeader());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(context,"LinktolineId_"+horizontalGrocderyList.get(position).getLinktolineid(),Toast.LENGTH_SHORT).show();

                Intent intentToast = new Intent(context, AllMenuItemsActivity.class);
                intentToast.putExtra("selection", position);
                 intentToast.putExtra("linktolineid", horizontalGrocderyList.get(position).getLinktolineid());
                context.startActivity(intentToast);
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalGrocderyList.size();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        RegularTextView txtview;
        RelativeLayout relativeLayout;
        public GroceryViewHolder(View view) {
            super(view);
            relativeLayout=view.findViewById(R.id.relative_layout_view);
            imageView=view.findViewById(R.id.imageView);
            txtview=view.findViewById(R.id.textview_title_item);
        }
    }
}
