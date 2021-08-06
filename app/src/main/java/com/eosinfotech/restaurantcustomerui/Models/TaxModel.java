package com.eosinfotech.restaurantcustomerui.Models;

/**
 * Created by eosinfotech on 15-09-2018.
 */

public class TaxModel {

    String desc,cost;
    float total;

    public TaxModel(String desc, String cost,float total)
    {
        this.desc=desc;
        this.cost=cost;
        this.total=total;
    }

    public float getTotal() {
        return total;
    }

    public String getCost() {
        return cost;
    }

    public String getDesc() {
        return desc;
    }
}
