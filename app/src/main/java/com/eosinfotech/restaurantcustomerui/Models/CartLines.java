package com.eosinfotech.restaurantcustomerui.Models;

public class CartLines
{
    private int id,userid,itemid,itemquantity;
    private String itemimage,itemname,itemdescription,itemprice,itemtotalprice,pricelistid,lineid,orderstatus;

    public CartLines() {
    }

    public CartLines(int id, int userid, int itemid, String itemname, String itemdescription, String itemprice, int itemquantity, String itemimage, String itemtotalprice, String pricelistid, String lineid)
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
        this.lineid=lineid;
    }

    public CartLines(int id,int userid,int itemid,String itemname,String itemdescription,String itemprice,int itemquantity,String itemimage,String itemtotalprice,String pricelistid,String lineid,String orderstatus)
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
        this.lineid=lineid;
        this.orderstatus=orderstatus;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public String getLineid() {
        return lineid;
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
