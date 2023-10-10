
package com.lisners.patient.zWork.restApi.pojo.timeSlot;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeSlotPojo {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("week_day_id")
    @Expose
    private int weekDayId;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("time_slot")
    @Expose
    private List<TimeSlot> timeSlot = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWeekDayId() {
        return weekDayId;
    }

    public void setWeekDayId(int weekDayId) {
        this.weekDayId = weekDayId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<TimeSlot> getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(List<TimeSlot> timeSlot) {
        this.timeSlot = timeSlot;
    }

}
