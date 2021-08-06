package com.eosinfotech.restaurantcustomerui.Models;

public class Tablebooking {

    private String tableStatus;
    private String tableCapacity;
    private int imageResource;
    private String tableNumber;


    public Tablebooking(int imageResource, String tableStatus, String tableCapacity, String tableNumber) {
        this.imageResource = imageResource;
        this.tableStatus = tableStatus;
        this.tableCapacity = tableCapacity;
        this.tableNumber = tableNumber;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }

    public String getTableCapacity() {
        return tableCapacity;
    }

    public void setTableCapacity(String tableCapacity) {
        this.tableCapacity = tableCapacity;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
}