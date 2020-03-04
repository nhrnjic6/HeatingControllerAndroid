package com.nhrnjic.heatingcontroller.model;

public class Setpoint {
    private int day;
    private int hour;
    private int minute;
    private int temperature;

    public Setpoint(int day, int hour, int minute, int temperature) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.temperature = temperature;
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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return dayToString(day) + " " + hour + ":" + minute + " " + temperature +"\u2103";
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
