package com.example.calorietracker12;

import java.util.Date;

public class Reports {
    Short repId;
    Date repDate;
    Double repConsumecal;
    Double repBurnedcal;
    Integer repStep;
    Double repGoalcal;
    Users repUserid;

    public Reports() {
    }

    public Reports(Short repId, Date repDate, Double repConsumecal, Double repBurnedcal, Integer repStep, Double repGoalcal, Users repUserid) {
        this.repId = repId;
        this.repDate = repDate;
        this.repConsumecal = repConsumecal;
        this.repBurnedcal = repBurnedcal;
        this.repStep = repStep;
        this.repGoalcal = repGoalcal;
        this.repUserid = repUserid;
    }

    public Short getRepId() {
        return repId;
    }

    public void setRepId(Short repId) {
        this.repId = repId;
    }

    public Date getRepDate() {
        return repDate;
    }

    public void setRepDate(Date repDate) {
        this.repDate = repDate;
    }

    public Double getRepConsumecal() {
        return repConsumecal;
    }

    public void setRepConsumecal(Double repConsumecal) {
        this.repConsumecal = repConsumecal;
    }

    public Double getRepBurnedcal() {
        return repBurnedcal;
    }

    public void setRepBurnedcal(Double repBurnedcal) {
        this.repBurnedcal = repBurnedcal;
    }

    public Integer getRepStep() {
        return repStep;
    }

    public void setRepStep(Integer repStep) {
        this.repStep = repStep;
    }

    public Double getRepGoalcal() {
        return repGoalcal;
    }

    public void setRepGoalcal(Double repGoalcal) {
        this.repGoalcal = repGoalcal;
    }

    public Users getRepUserid() {
        return repUserid;
    }

    public void setRepUserid(Users repUserid) {
        this.repUserid = repUserid;
    }
}
