package com.eosinfotech.restaurantcustomerui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eosinfotech.restaurantcustomerui.CustomFonts.BoldTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.LightTextView;
import com.eosinfotech.restaurantcustomerui.CustomFonts.RegularTextView;
import com.eosinfotech.restaurantcustomerui.MealModule.MealDetailActivity;
import com.eosinfotech.restaurantcustomerui.Models.MealItems;
import com.eosinfotech.restaurantcustomerui.R;
import com.eosinfotech.restaurantcustomerui.Utils.Footer;
import com.eosinfotech.restaurantcustomerui.Utils.Header;
import com.eosinfotech.restaurantcustomerui.Utils.RecyclerViewItem;

import java.util.List;


public class MealItemsAdapter extends RecyclerView.Adapter {
    //Declare List of Recyclerview Items
    List<RecyclerViewItem> recyclerViewItems;
    //Header Item Type
    private static final int HEADER_ITEM = 0;
    //Footer Item Type
    private static final int FOOTER_ITEM = 1;
    ////Food Item Type
    private static final int FOOD_ITEM = 2;
    Context mContext;

    public MealItemsAdapter(List<RecyclerViewItem> recyclerViewItems, Context mContext) {
        this.recyclerViewItems = recyclerViewItems;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View row;
        //Check fot view Type inflate layout according to it
        if (viewType == HEADER_ITEM) {
            row = inflater.inflate(R.layout.custom_row_header, parent, false);
            return new HeaderHolder(row);
        } else if (viewType == FOOTER_ITEM) {
            row = inflater.inflate(R.layout.custom_row_footer, parent, false);
            return new FooterHolder(row);
        } else if (viewType == FOOD_ITEM) {
            row = inflater.inflate(R.layout.custom_row_mealitem, parent, false);
            return new FoodItemHolder(row);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewItem recyclerViewItem = recyclerViewItems.get(position);
        //Check holder instance to populate data  according to it
        if (holder instanceof HeaderHolder) {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            Header header = (Header) recyclerViewItem;
            //set data
            headerHolder.texViewHeaderText.setText(header.getHeaderText());
            headerHolder.textViewCategory.setText(header.getCategory());
            Glide.with(mContext).load(header.getImageUrl()).placeholder(R.drawable.icon_ads).into(headerHolder.imageViewHeader);

        } else if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            Footer footer = (Footer) recyclerViewItem;
            //set data
            footerHolder.texViewQuote.setText(footer.getQuote());
            footerHolder.textViewAuthor.setText(footer.getAuthor());
            Glide.with(mContext).load(footer.getImageUrl()).placeholder(R.drawable.icon_ads).into(footerHolder.imageViewFooter);

        } else if (holder instanceof FoodItemHolder) {
            final FoodItemHolder foodItemHolder = (FoodItemHolder) holder;
            final MealItems foodItem = (MealItems) recyclerViewItem;
            //set data
            foodItemHolder.texViewFoodTitle.setText(Html.fromHtml(foodItem.getTitle()));
            foodItemHolder.texViewDescription.setText(Html.fromHtml(foodItem.getDescription()));
            foodItemHolder.textViewPrice.setText(Html.fromHtml("₹"+ foodItem.getPrice()));
            foodItemHolder.textViewCalories.setText(Html.fromHtml(foodItem.getItemCalories()));
            Glide.with(mContext).load(foodItem.getImageUrl()).placeholder(R.drawable.icon_ads).into(foodItemHolder.imageViewFood);

            foodItemHolder.itemcardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(), foodItem.getTitle()+"  ", Toast.LENGTH_SHORT).show();
                    Intent gotoIntent = new Intent(mContext , MealDetailActivity.class);
                    gotoIntent.putExtra("itemid", foodItem.getItemId());
                    gotoIntent.putExtra("itemName", foodItem.getTitle());
                    gotoIntent.putExtra("itemDescription", foodItem.getDescription());
                    gotoIntent.putExtra("itemPrice", foodItem.getPrice());
                    gotoIntent.putExtra("itemImage", foodItem.getImageUrl());
                    gotoIntent.putExtra("itemCalories", foodItem.getItemCalories());
                    gotoIntent.putExtra("itemMenuDay", foodItem.getItemMenuDay());
                    mContext.startActivity(gotoIntent);
                }
            });

            //check food item is hot or not to set visibility of hot text on image
            if (foodItem.isHot())
                foodItemHolder.textViewIsHot.setVisibility(View.VISIBLE);
            else
                foodItemHolder.textViewIsHot.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //here we can set view type
        RecyclerViewItem recyclerViewItem = recyclerViewItems.get(position);
        //if its header then return header item
        if (recyclerViewItem instanceof Header)
            return HEADER_ITEM;
            //if its Footer then return Footer item
        else if (recyclerViewItem instanceof Footer)
            return FOOTER_ITEM;
        //if its FoodItem then return Food item
        else if (recyclerViewItem instanceof MealItems)
            return FOOD_ITEM;
        else
            return super.getItemViewType(position);

    }

    @Override
    public int getItemCount() {
        return recyclerViewItems.size();
    }
    //Food item holder
    private class FoodItemHolder extends RecyclerView.ViewHolder {
        RegularTextView texViewFoodTitle;
        LightTextView texViewDescription;
        RegularTextView textViewPrice;
        RegularTextView textViewCalories;
        RegularTextView textViewIsHot;
        ImageView imageViewFood;
        CardView itemcardview;

        FoodItemHolder(View itemView) {
            super(itemView);
            texViewFoodTitle = itemView.findViewById(R.id.texViewFoodTitle);
            texViewDescription = itemView.findViewById(R.id.texViewDescription);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewIsHot = itemView.findViewById(R.id.textViewIsHot);
            itemcardview = itemView.findViewById(R.id.card_view);
            textViewCalories = itemView.findViewById(R.id.itemCalories);
        }
    }
    //header holder
    private class HeaderHolder extends RecyclerView.ViewHolder {
        BoldTextView texViewHeaderText;
        RegularTextView textViewCategory;
        ImageView imageViewHeader;

        HeaderHolder(View itemView) {
            super(itemView);
            texViewHeaderText = itemView.findViewById(R.id.texViewHeaderText);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            imageViewHeader = itemView.findViewById(R.id.imageViewHeader);
        }
    }
    //footer holder
    private class FooterHolder extends RecyclerView.ViewHolder {
        BoldTextView texViewQuote;
        RegularTextView textViewAuthor;
        ImageView imageViewFooter;

        FooterHolder(View itemView) {
            super(itemView);
            texViewQuote = itemView.findViewById(R.id.texViewQuote);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            imageViewFooter = itemView.findViewById(R.id.imageViewFooter);
        }
    }
}
