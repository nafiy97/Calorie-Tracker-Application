package com.example.calorietracker12;

import java.util.Date;

public class Credential {
    private Short creId;
    private Users creUserid;
    private Date creSignupdate;
    private String creUsername;
    private String crePasshash;

    public Credential(Short creId, Users creUserid, Date creSignupdate, String creUsername, String crePasshash) {
        this.creId = creId;
        this.creUserid = creUserid;
        this.creSignupdate = creSignupdate;
        this.creUsername = creUsername;
        this.crePasshash = crePasshash;
    }

    public Credential() {
    }

    public Short getCreId() {
        return creId;
    }

    public void setCreId(Short creId) {
        this.creId = creId;
    }

    public Date getCreSignupdate() {
        return creSignupdate;
    }

    public void setCreSignupdate(Date creSignupdate) {
        this.creSignupdate = creSignupdate;
    }

    public String getCreUsername() {
        return creUsername;
    }

    public void setCreUsername(String creUsername) {
        this.creUsername = creUsername;
    }

    public String getCrePasshash() {
        return crePasshash;
    }

    public void setCrePasshash(String crePasshash) {
        this.crePasshash = crePasshash;
    }

    public Users getCreUserid() {
        return creUserid;
    }

    public void setCreUserid(Users creUserid) {
        this.creUserid = creUserid;
    }
}

