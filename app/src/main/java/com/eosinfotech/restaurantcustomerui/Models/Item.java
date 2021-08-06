package com.eosinfotech.restaurantcustomerui.Models;

public class Item {

    public String cardName;
    public String imageResourceId;
    public String Id;
    public String price;
    public String des;
    public String pricelistid;
    public String calories;


    public Item(String productName, String productImage, String Id, String price, String des, String pricelistid, String calories) {
        this.imageResourceId = productImage;
        this.cardName = productName;
        this.Id=Id;
        this.price=price;
        this.des=des;
        this.pricelistid=pricelistid;
        this.calories=calories;
    }

    public String getCalories() {
        return calories;
    }

    public String getPricelistid() {
        return pricelistid;
    }

    public void setPricelistid(String pricelistid) {
        this.pricelistid = pricelistid;
    }

    public String getCardName() {
        return cardName;
    }
    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(String imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getId() {
        return Id;
    }

    public String getDes() {
        return des;
    }

    public String getPrice() {
        return price;
    }
}