package com.lisners.patient.zWork.restApi.pojo.bookAppointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookAppointmentPojo {


    @SerializedName("total_amount")
    @Expose
    private int totalAmount;

    @SerializedName("is_promotional")
    @Expose
    private int isPromotional;

    @SerializedName("specialization_id")
    @Expose
    private String specializationId;

    @SerializedName("specialization_name")
    @Expose
    private String specializationName;

    @SerializedName("counselor")
    @Expose
    private Counselor counselor;

    @SerializedName("appointment_detail")
    @Expose
    private AppointmentDetail appointmentDetail;



    @SerializedName("specialization")
    @Expose
    private List<Specialization> specialization = null;


    public Counselor getCounselor() {
        return counselor;
    }

    public void setCounselor(Counselor counselor) {
        this.counselor = counselor;
    }

    public AppointmentDetail getAppointmentDetail() {
        return appointmentDetail;
    }

    public void setAppointmentDetail(AppointmentDetail appointmentDetail) {
        this.appointmentDetail = appointmentDetail;
    }


    public int getTotalAmount() {
        return totalAmount;
    }

    public List<Specialization> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(List<Specialization> specialization) {
        this.specialization = specialization;
    }


    public String getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(String specializationId) {
        this.specializationId = specializationId;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }

    public int getIsPromotional() {
        return isPromotional;
    }

    public void setIsPromotional(int isPromotional) {
        this.isPromotional = isPromotional;
    }
}
