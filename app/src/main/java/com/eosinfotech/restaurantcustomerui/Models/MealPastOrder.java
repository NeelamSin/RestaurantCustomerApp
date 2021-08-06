package com.eosinfotech.restaurantcustomerui.Models;

public class MealPastOrder {
    private String headerId,contactId, orderDate, orderTotal, discountID, orderStatus;

    public MealPastOrder() {
    }

    public MealPastOrder(String headerId, String contactId, String orderDate, String orderTotal, String discountID, String orderStatus) {
        this.headerId = headerId;
        this.contactId = contactId;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        this.discountID = discountID;
        this.orderStatus = orderStatus;
    }


    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getDiscountID() {
        return discountID;
    }

    public void setDiscountID(String discountID) {
        this.discountID = discountID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
