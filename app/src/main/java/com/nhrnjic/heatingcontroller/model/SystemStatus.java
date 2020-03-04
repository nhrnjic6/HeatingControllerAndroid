package com.nhrnjic.heatingcontroller.model;

import org.joda.time.DateTime;

public class SystemStatus {
    private String id;
    private String temperature;
    private long updatedAt;

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

    public String formattedUpdatedAt(){
        DateTime dateTime = new DateTime(updatedAt);
        return dateTime.toString("dd:mm:ss");
    }
}
