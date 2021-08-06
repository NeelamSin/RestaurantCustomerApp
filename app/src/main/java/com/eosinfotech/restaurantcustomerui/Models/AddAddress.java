package com.eosinfotech.restaurantcustomerui.Models;

public class AddAddress {
    private String deliveryType, fulLAddress, addressId;

    public AddAddress() {
    }

    public AddAddress(String deliveryType, String fulLAddress, String addressId) {
        this.deliveryType = deliveryType;
        this.fulLAddress = fulLAddress;
        this.addressId = addressId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getFulLAddress() {
        return fulLAddress;
    }

    public void setFulLAddress(String fulLAddress) {
        this.fulLAddress = fulLAddress;
    }

}
