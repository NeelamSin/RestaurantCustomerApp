package com.eosinfotech.restaurantcustomerui.Models;

public class Category {
    private String previousHeader;
    private String linktolineid;
    private String imageResource;
    /**
     * here my model description
     * @param imageResource
     * @param linktolineid
     */
    public Category(String previousHeader, String linktolineid,String imageResource) {
        this.imageResource = imageResource;
        this.previousHeader = previousHeader;
        this.linktolineid=linktolineid;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getPreviousHeader() {
        return previousHeader;
    }

    public String getLinktolineid() {
        return linktolineid;
    }
}
