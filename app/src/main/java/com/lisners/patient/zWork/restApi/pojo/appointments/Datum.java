
package com.lisners.patient.zWork.restApi.pojo.appointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Datum {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("appointment_detail_id")
    @Expose
    private int appointmentDetailId;
    @SerializedName("call_type")
    @Expose
    private String callType;
    @SerializedName("call_rate")
    @Expose
    private String callRate;
    @SerializedName("add_notes")
    @Expose
    private String addNotes;
    @SerializedName("prescriprion")
    @Expose
    private Object prescriprion;
    @SerializedName("call_date")
    @Expose
    private Object callDate;
    @SerializedName("call_time")
    @Expose
    private Object callTime;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("share_amount")
    @Expose
    private String shareAmount;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("comment")
    @Expose
    private Object comment;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("current_time")
    @Expose
    private String currentTime;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("chenal_code")
    @Expose
    private String chenalCode;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("counselor_id")
    @Expose
    private int counselorId;
    @SerializedName("counselor")
    @Expose
    private Counselor counselor;
    @SerializedName("appointment_detail")
    @Expose
    private AppointmentDetail appointmentDetail;
    @SerializedName("specialization")
    @Expose
    private List<Specialization__1> specialization = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAppointmentDetailId() {
        return appointmentDetailId;
    }

    public void setAppointmentDetailId(int appointmentDetailId) {
        this.appointmentDetailId = appointmentDetailId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallRate() {
        return callRate;
    }

    public void setCallRate(String callRate) {
        this.callRate = callRate;
    }

    public String getAddNotes() {
        return addNotes;
    }

    public void setAddNotes(String addNotes) {
        this.addNotes = addNotes;
    }

    public Object getPrescriprion() {
        return prescriprion;
    }

    public void setPrescriprion(Object prescriprion) {
        this.prescriprion = prescriprion;
    }

    public Object getCallDate() {
        return callDate;
    }

    public void setCallDate(Object callDate) {
        this.callDate = callDate;
    }

    public Object getCallTime() {
        return callTime;
    }

    public void setCallTime(Object callTime) {
        this.callTime = callTime;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(String shareAmount) {
        this.shareAmount = shareAmount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Object getComment() {
        return comment;
    }

    public void setComment(Object comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getChenalCode() {
        return chenalCode;
    }

    public void setChenalCode(String chenalCode) {
        this.chenalCode = chenalCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }

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

    public List<Specialization__1> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(List<Specialization__1> specialization) {
        this.specialization = specialization;
    }

}
