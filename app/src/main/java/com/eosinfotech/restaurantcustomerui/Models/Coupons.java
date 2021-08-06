package com.eosinfotech.restaurantcustomerui.Models;

public class Coupons {
    private String couponPercent, couponDescription, couponId;

    public Coupons() {
    }

    public Coupons(String couponPercent, String couponDescription, String couponId) {
        this.couponPercent = couponPercent;
        this.couponDescription = couponDescription;
        this.couponId = couponId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public void setCouponDescription(String couponDescription) {
        this.couponDescription = couponDescription;
    }

    public String getCouponPercent() {
        return couponPercent;
    }

    public void setCouponPercent(String couponPercent) {
        this.couponPercent = couponPercent;
    }
}
