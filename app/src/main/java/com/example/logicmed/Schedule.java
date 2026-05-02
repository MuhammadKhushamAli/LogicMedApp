package com.example.logicmed;

public class Schedule {
    private String day;
    private String fromTime;
    private String endTime;

    public Schedule() {
    }

    public Schedule(String day, String fromTime, String endTime) {
        this.day = day;
        this.fromTime = fromTime;
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
