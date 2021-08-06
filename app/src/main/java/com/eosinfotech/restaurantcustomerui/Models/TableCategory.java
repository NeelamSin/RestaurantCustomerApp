package com.eosinfotech.restaurantcustomerui.Models;

public class TableCategory {

    private String tableName;
    private String tabProfile;
    private String tabImage;

    public TableCategory(String tabProfile , String tableName , String tabImage) {
        this.tabImage = tabImage;
        this.tableName = tableName;
        this.tabProfile = tabProfile;
    }

    public String getTabImage() {
        return tabImage;
    }

    public void setTabImage(String tabImage) {
        this.tabImage = tabImage;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTabProfile() {
        return tabProfile;
    }

    public void setTabProfile(String tabProfile) {
        this.tabProfile = tabProfile;
    }
}
