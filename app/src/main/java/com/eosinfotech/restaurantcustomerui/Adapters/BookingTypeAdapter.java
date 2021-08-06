package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eosinfotech.restaurantcustomerui.BookingType.TableBookingActivity;
import com.eosinfotech.restaurantcustomerui.BookingType.TableFormActivity;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MainActivities.DashboardActivity;
import com.eosinfotech.restaurantcustomerui.MealModule.MealActivity;
import com.eosinfotech.restaurantcustomerui.Models.TableCategory;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.StatusActivity.GettingUserLocation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


public class BookingTypeAdapter extends RecyclerView.Adapter<BookingTypeAdapter.GroceryViewHolder> {
    private List<TableCategory> verticalGrocderyList;
    Context context;
    public static final String PREFS_NAME = "Restaurant" ;
    SharedPreferences sharedpreferences;


    public BookingTypeAdapter(List<TableCategory> verticalGrocderyList, Context context) {
        this.verticalGrocderyList = verticalGrocderyList;
        this.context = context;
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tablecategory, parent, false);
        GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, final int position) {

        Picasso.get().load(verticalGrocderyList.get(position).getTabImage()).placeholder(R.drawable.icon_ads).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        holder.txtview.setText(verticalGrocderyList.get(position).getTableName());
        holder.txtviewDes.setText(verticalGrocderyList.get(position).getTabProfile());

        Log.i("img","_"+verticalGrocderyList.get(position).getTabImage());
        Log.i("desc","_"+verticalGrocderyList.get(position).getTabProfile());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ordertype", verticalGrocderyList.get(position).getTableName());
                editor.commit();

                if (verticalGrocderyList.get(position).getTableName().equals("Dine In")) {
                    Intent gotoNext = new Intent(context, GettingUserLocation.class);
                    context.startActivity(gotoNext);
                } else if (verticalGrocderyList.get(position).getTableName().equals("MealBooking")) {
                    Intent gotoNextTwo = new Intent(context, MealActivity.class);
                    context.startActivity(gotoNextTwo);
                } else {
                    Intent gotoNextOne = new Intent(context, DashboardActivity.class);
                    context.startActivity(gotoNextOne);
                }
            }
        });

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("ordertype", verticalGrocderyList.get(position).getTableName());
                editor.commit();

                if (verticalGrocderyList.get(position).getTableName().equals("Dine In")) {
                    Intent gotoNext = new Intent(context, GettingUserLocation.class);
                    context.startActivity(gotoNext);
                } else if (verticalGrocderyList.get(position).getTableName().equals("MealBooking")) {
                    Intent gotoNextTwo = new Intent(context, MealActivity.class);
                    context.startActivity(gotoNextTwo);
                } else {
                    Intent gotoNextOne = new Intent(context, DashboardActivity.class);
                    context.startActivity(gotoNextOne);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return verticalGrocderyList.size();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        CardView cardView;
        RegularTextView txtview;
        LightTextView txtviewDes;
        LinearLayout frameLayout;

        public GroceryViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            imageView = view.findViewById(R.id.imageView);
            txtview = view.findViewById(R.id.textViewName);
            txtviewDes = view.findViewById(R.id.textViewVersion);
            frameLayout = view.findViewById(R.id.frameLayout);
        }
    }
}
