package com.lisners.patient.ApiModal;

import java.util.ArrayList;

public class TherapistDayTimeSlot {
    int id , user_id , week_day_id;
    String day ;
    ArrayList<TimeSlot> time_slot ;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getWeek_day_id() {
        return week_day_id;
    }

    public String getDay() {
        return day;
    }

    public ArrayList<TimeSlot> getTime_slot() {
        return time_slot;
    }
}
