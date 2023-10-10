package com.lisners.patient.ApiModal;

public class TimeSlot {
    int id , appointment_id ,is_booked ;
    String start_time , end_time ;

    public int getId() {
        return id;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }
    public int getIs_Booked() {
      return   is_booked;
    }

}
