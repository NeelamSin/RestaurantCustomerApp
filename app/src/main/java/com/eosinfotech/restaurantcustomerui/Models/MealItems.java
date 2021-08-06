package com.eosinfotech.restaurantcustomerui.Models;

import com.eosinfotech.restaurantcustomerui.Utils.RecyclerViewItem;

//Object of food item
public class MealItems extends RecyclerViewItem {
    private String itemId;
    private String title;
    private String description;
    private String imageUrl;
    private String price;
    private boolean isHot = false;
    private String itemCalories;
    private String itemMenuDay;

    public MealItems(String itemId, String itemCalories, String title, String description, String imageUrl, String price, boolean isHot, String itemMenuDay) {
        this.itemId = itemId;
        this.itemCalories = itemCalories;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isHot = isHot;
        this.itemMenuDay = itemMenuDay;
    }

    public String getItemMenuDay() {
        return itemMenuDay;
    }

    public void setItemMenuDay(String itemMenuDay) {
        this.itemMenuDay = itemMenuDay;
    }

    public String getItemCalories() {
        return itemCalories;
    }

    public void setItemCalories(String itemCalories) {
        this.itemCalories = itemCalories;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }
}
