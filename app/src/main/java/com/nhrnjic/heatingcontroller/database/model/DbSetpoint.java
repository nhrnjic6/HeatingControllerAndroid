package com.nhrnjic.heatingcontroller.database.model;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbSetpoint extends RealmObject {
    @PrimaryKey
    private long id;
    private String day;
    private String hour;
    private String minute;
    private String temperature;

    public DbSetpoint() {

    }

    public DbSetpoint(String day, String hour, String minute, String temperature) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.temperature = temperature;
    }

    public long getId() {
        return id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return dayToString(Integer.valueOf(day)) + " " + hour + ":" + minute + " " + temperature +"\u2103";
    }

    private String dayToString(int day){
        switch (day){
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            case 7: return "Sunday";
            case 8: return "Work Day";
            case 9: return "Weekend";
            default: return "Unknown";
        }
    }
}
