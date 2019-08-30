package com.example.calorietracker12;

import java.util.Date;

public class Users {
    private Short userId;
    private String userName;
    private String userSurname;
    private String userEmail;
    private String userAddress;
    private String userPostcode;
    private String userGender;
    private Short userActivelevel;
    private Short userSteppermile;
    private Double userWeight;
    private Double userHeight;
    private Date userDob;

    public Users(Short userId, String userName, String userSurname, String userEmail, String userAddress, String userPostcode, String userGender, Short userActivelevel, Short userSteppermile, Double userWeight, Double userHeight, Date userDob) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userPostcode = userPostcode;
        this.userGender = userGender;
        this.userActivelevel = userActivelevel;
        this.userSteppermile = userSteppermile;
        this.userWeight = userWeight;
        this.userHeight = userHeight;
        this.userDob = userDob;
    }

    public Users() {
    }

    public Users(Short userId) {
        this.userId = userId;
    }

    public Short getUserId() {
        return userId;
    }

    public void setUserId(Short userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserPostcode() {
        return userPostcode;
    }

    public void setUserPostcode(String userPostcode) {
        this.userPostcode = userPostcode;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Short getUserActivelevel() {
        return userActivelevel;
    }

    public void setUserActivelevel(Short userActivelevel) {
        this.userActivelevel = userActivelevel;
    }

    public Short getUserSteppermile() {
        return userSteppermile;
    }

    public void setUserSteppermile(Short userSteppermile) {
        this.userSteppermile = userSteppermile;
    }

    public Double getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(Double userWeight) {
        this.userWeight = userWeight;
    }

    public Double getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(Double userHeight) {
        this.userHeight = userHeight;
    }

    public Date getUserDob() {
        return userDob;
    }

    public void setUserDob(Date userDob) {
        this.userDob = userDob;
    }
}
