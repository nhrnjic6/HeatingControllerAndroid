package com.nhrnjic.heatingcontroller.database.model;

import org.joda.time.DateTime;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbTemperature extends RealmObject {
    @PrimaryKey
    private int id;
    private double temperature;
    private long updatedAt;

    public DbTemperature() {
    }

    public DbTemperature(double temperature, long updatedAt) {
        this.temperature = temperature;
        this.updatedAt = updatedAt;
    }

    public double getTemperature() {
        return temperature;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getFormattedUpdateAt(){
        DateTime dateTime = new DateTime(updatedAt);
        return dateTime.toString("hh:mm:ss");
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
