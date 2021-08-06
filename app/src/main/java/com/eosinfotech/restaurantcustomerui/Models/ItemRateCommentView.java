package com.eosinfotech.restaurantcustomerui.Models;

public class ItemRateCommentView {

    private String userName;
    private String userDate;
    private String userComment;
    private String rating;
    private int image;

    public ItemRateCommentView(int image, String userName, String userDate, String userComment, String rating) {
        this.image = image;
        this.userName = userName;
        this.userDate = userDate;
        this.userComment = userComment;
        this.rating = rating;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}