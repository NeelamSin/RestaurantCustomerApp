package com.eosinfotech.restaurantcustomerui.Models;

public class Cart
{
    private int id,userid,itemid,itemquantity;
    private String itemimage,itemname,itemdescription,itemprice,itemtotalprice,pricelistid,status;

    public Cart() {
    }

    public Cart(int id,int userid,int itemid,String itemname,String itemdescription,String itemprice,int itemquantity,String itemimage,String itemtotalprice,String pricelistid,String status)
    {
        this.id = id;
        this.userid = userid;
        this.itemid = itemid;
        this.itemname = itemname;
        this.itemdescription = itemdescription;
        this.itemprice = itemprice;
        this.itemquantity = itemquantity;
        this.itemimage=itemimage;
        this.itemtotalprice=itemtotalprice;
        this.pricelistid=pricelistid;
        this.status=status;
    }
    public Cart(int id,int userid,int itemid,String itemname,String itemdescription,String itemprice,int itemquantity,String itemimage,String itemtotalprice,String pricelistid)
    {
        this.id = id;
        this.userid = userid;
        this.itemid = itemid;
        this.itemname = itemname;
        this.itemdescription = itemdescription;
        this.itemprice = itemprice;
        this.itemquantity = itemquantity;
        this.itemimage=itemimage;
        this.itemtotalprice=itemtotalprice;
        this.pricelistid=pricelistid;
    }

    public String getStatus() {
        return status;
    }

    public String getPricelistid() {
        return pricelistid;
    }

    public String getItemtotalprice() {
        return itemtotalprice;
    }

    public int getId() {
        return id;
    }

    public int getItemid() {
        return itemid;
    }

    public int getItemquantity() {
        return itemquantity;
    }

    public int getUserid() {
        return userid;
    }

    public String getItemdescription() {
        return itemdescription;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItemprice() {
        return itemprice;
    }

    public String getItemimage() {
        return itemimage;
    }
}
