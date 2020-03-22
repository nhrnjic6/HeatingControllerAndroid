package com.nhrnjic.heatingcontroller.model;

import com.nhrnjic.heatingcontroller.database.model.DbSetpoint;

import org.joda.time.DateTime;

import java.util.List;

public class SystemStatus {
    private String id;
    private String temperature;
    private long updatedAt;
    private List<DbSetpoint> rules;

    public SystemStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DbSetpoint> getRules() {
        return rules;
    }

    public void setRules(List<DbSetpoint> rules) {
        this.rules = rules;
    }

    public String formattedUpdatedAt(){
        DateTime dateTime = new DateTime(updatedAt * 1000);
        return dateTime.toString("dd:mm:ss");
    }
}
