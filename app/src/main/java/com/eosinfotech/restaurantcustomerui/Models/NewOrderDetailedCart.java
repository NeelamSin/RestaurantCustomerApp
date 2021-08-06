package com.eosinfotech.restaurantcustomerui.Models;

public class NewOrderDetailedCart {
    private String itemname, itemquantity, itemcost;

    public NewOrderDetailedCart() {
    }

    public NewOrderDetailedCart(String itemname, String itemquantity, String itemcost) {
        this.itemname = itemname;
        this.itemquantity = itemquantity;
        this.itemcost = itemcost;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String name) {
        this.itemname = name;
    }

    public String getItemcost() {
        return itemcost;
    }

    public void setItemcost(String cost) {
        this.itemcost = cost;
    }

    public String getItemquantity() {
        return itemquantity;
    }

    public void setItemquantity(String cost) {
        this.itemquantity = cost;
    }
}
