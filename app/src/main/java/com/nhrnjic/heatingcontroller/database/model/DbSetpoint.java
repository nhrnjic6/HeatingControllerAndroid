package com.nhrnjic.heatingcontroller.database.model;

import com.nhrnjic.heatingcontroller.exception.FieldNotSetException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DbSetpoint implements Comparable<DbSetpoint> {
    private Integer day;
    private Integer hour;
    private Integer minute;
    private Double temperature;

    public DbSetpoint() {
    }

    public DbSetpoint(DbSetpoint setpoint) {
        this.day = setpoint.day;
        this.hour = setpoint.hour;
        this.minute = setpoint.minute;
        this.temperature = setpoint.temperature;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getTimeText() {
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
