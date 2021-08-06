package com.eosinfotech.restaurantcustomerui.Models;

public class FoodMenu {
    private int id;
    private String name;
    private String imageResource;
    private String description;
    private String price;
    private String menutype;
    private String level;
    private String pricelistid;
    private String ItemCaloriesDtls;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenutype() {
        return menutype;
    }

    public void setMenutype(String menutype) {
        this.menutype = menutype;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPricelistid() {
        return pricelistid;
    }

    public void setPricelistid(String pricelistid) {
        this.pricelistid = pricelistid;
    }

    public String getItemCaloriesDtls() {
        return ItemCaloriesDtls;
    }

    public FoodMenu(int id, String name, String imageResource, String description, String price, String menutype, String level, String pricelistid, String ItemCaloriesDtls) {
        this.id=id;
        this.name = name;
        this.imageResource = imageResource;
        this.description=description;
        this.price=price;
        this.menutype=menutype;
        this.level=level;
        this.pricelistid=pricelistid;
        this.ItemCaloriesDtls=ItemCaloriesDtls;
    }
}
