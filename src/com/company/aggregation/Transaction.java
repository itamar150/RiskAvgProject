package com.company.aggregation;

import java.util.Date;

/**
* This entity is being represented as a line in files input
*/
public class Transaction {
    private String id;
    private String userName;
    private String organization;
    private Date eventTime;
    private String eventType;
    private int risk;

    public int getRisk() {
        return risk;
    }

    public void setRisk(int risk) {
        this.risk = risk;
    }

    public String getId() {
        return id;
    }

   public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrganization() {
       return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

}

