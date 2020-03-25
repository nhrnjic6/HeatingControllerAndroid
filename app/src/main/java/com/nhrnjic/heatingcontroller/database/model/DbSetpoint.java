package com.nhrnjic.heatingcontroller.database.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Time;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DbSetpoint extends RealmObject implements Comparable<DbSetpoint> {
    @PrimaryKey
    private long id;
    private int day;
    private int hour;
    private int minute;
    private double temperature;

    public DbSetpoint() {

    }

    public DbSetpoint(int day, int hour, int minute, double temperature) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.temperature = temperature;
    }

    public long getId() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return dayToString(day) + " " + hour + ":" + minute + " " + temperature +"\u2103";
    }

    public String getTimeText(){
        DateTime now = DateTime.now();
        DateTime setpointTime = new DateTime(
                now.getYear(),
                now.getMonthOfYear(),
                now.getDayOfMonth(),
                hour, minute,
                DateTimeZone.UTC
        );

        return setpointTime.toString("HH:mm");
    }

    public String getTemperatureText(){
        return temperature + "\u2103";
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

    @Override
    public int compareTo(DbSetpoint o) {
        if(o.day < day){
            return 1;
        }else if(o.day == day){
            if(o.hour < hour){
                return 1;
            }else if(o.hour == hour){
                if(o.minute < minute){
                    return 1;
                }else if(o.minute == minute){
                    return 0;
                }else{
                    return -1;
                }
            }else{
                return -1;
            }
        }else{
            return -1;
        }
    }
}
