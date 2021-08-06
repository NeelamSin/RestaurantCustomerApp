package com.eosinfotech.restaurantcustomerui.Models;

public class ItemRating {

    private int itemRating;
    private String itemName;
    private String itemComment;
    private int itemImage;

    private String itemId;

    public ItemRating( int itemRating,String itemName,String itemComment,int itemImage,String itemId)
    {
        this.itemRating=itemRating;
        this.itemName=itemName;
        this.itemComment=itemComment;
        this.itemImage=itemImage;
        this.itemId=itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemRating() {
        return itemRating;
    }

    public void setItemRating(int itemRating) {
        this.itemRating = itemRating;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemImage() {
        return itemImage;
    }

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemComment() {
        return itemComment;
    }

    public void setItemComment(String itemComment) {
        this.itemComment = itemComment;
    }
}
