package com.eosinfotech.restaurantcustomerui.Models;

public class ViewOrderDetail {

    private String itemName;
    private String allItems;
    private String itemCost;
    private String itemQuantity;


    public ViewOrderDetail(String itemName, String allItems, String itemCost, String itemQuantity) {
        this.itemName = itemName;
        this.allItems = allItems;
        this.itemCost = itemCost;
        this.itemQuantity = itemQuantity;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAllItems() {
        return allItems;
    }

    public void setAllItems(String allItems) {
        this.allItems = allItems;
    }

    public String getItemCost() {
        return itemCost;
    }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }
}