package com.eosinfotech.restaurantcustomerui.Models;

public class PastOrder {
    private String itemnames, itemcost,headerid,status,DiscountID;

    public PastOrder() {
    }

    public PastOrder(String itemnames, String itemcost,String headerid,String status) {
        this.itemnames = itemnames;
        this.itemcost = itemcost;
        this.headerid=headerid;
        this.status=status;
    }
    public PastOrder(String itemnames, String itemcost,String headerid,String status,String DiscountID) {
        this.itemnames = itemnames;
        this.itemcost = itemcost;
        this.headerid=headerid;
        this.status=status;
        this.DiscountID=DiscountID;
    }

    public String getDiscountID() {
        return DiscountID;
    }

    public String getStatus() {
        return status;
    }

    public String getItemnames() {
        return itemnames;
    }

    public void setItemnames(String name) {
        this.itemnames = name;
    }


    public String getItemcost() {
        return itemcost;
    }

    public void setItemcost(String cost) {
        this.itemcost = cost;
    }

    public String getHeaderid() {
        return headerid;
    }

    public void setHeaderid(String headerid) {
        this.headerid = headerid;
    }
}