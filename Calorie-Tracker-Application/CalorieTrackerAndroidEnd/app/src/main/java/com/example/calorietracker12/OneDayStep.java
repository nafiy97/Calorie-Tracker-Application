package com.example.calorietracker12;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity
public class OneDayStep {
    @PrimaryKey(autoGenerate = true)
    public int stepId;

    @ColumnInfo(name = "step")
    public int step;

    @ColumnInfo(name = "time")
    public String time;

    public OneDayStep(int step,String time) {
        this.step=step;
        this.time=time;
    }
    public int getStepId() {
        return stepId;
    }
    public void setStepId(int stepId) {
        this.stepId=stepId;
    }
    public int getStep() {
        return step;
    }
    public void setStep(int step) {
        this.step=step;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time) {
        this.time=time;
    }

}
