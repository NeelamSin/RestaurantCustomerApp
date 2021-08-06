package com.eosinfotech.restaurantcustomerui.Models;

/**
 * Created by eosinfotech on 15-09-2018.
 */

public class TaxModel_R {

    String desc,cost,totalcost;

    public TaxModel_R(String desc, String cost, String totalcost)
    {
        this.desc=desc;
        this.cost=cost;
        this.totalcost=totalcost;
    }

    public String getCost() {
        return cost;
    }

    public String getDesc() {
        return desc;
    }

    public String getTotalcost() {
        return totalcost;
    }
}
