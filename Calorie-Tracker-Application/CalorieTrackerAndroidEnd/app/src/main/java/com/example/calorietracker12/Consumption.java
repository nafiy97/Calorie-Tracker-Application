package com.example.calorietracker12;

import java.util.Date;

public class Consumption {
    Short conId;
    Date conDate;
    Double conFoodamount;
    Food conFoodid;
    Users conUserid;

    public Consumption() {
    }

    public Consumption(Short conId, Date conDate, Double conFoodamount, Food conFoodid, Users conUserid) {
        this.conId = conId;
        this.conDate = conDate;
        this.conFoodamount = conFoodamount;
        this.conFoodid = conFoodid;
        this.conUserid = conUserid;
    }

    public Short getConId() {
        return conId;
    }

    public void setConId(Short conId) {
        this.conId = conId;
    }

    public Date getConDate() {
        return conDate;
    }

    public void setConDate(Date conDate) {
        this.conDate = conDate;
    }

    public Double getConFoodamount() {
        return conFoodamount;
    }

    public void setConFoodamount(Double conFoodamount) {
        this.conFoodamount = conFoodamount;
    }

    public Food getConFoodid() {
        return conFoodid;
    }

    public void setConFoodid(Food conFoodid) {
        this.conFoodid = conFoodid;
    }

    public Users getConUserid() {
        return conUserid;
    }

    public void setConUserid(Users conUserid) {
        this.conUserid = conUserid;
    }
}
