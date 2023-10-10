package com.lisners.patient.ApiModal;

public class CalendarModel {
    String month , date , day ;

    public CalendarModel(String month, String date, String day) {
        this.month = month;
        this.date = date;
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }
}
