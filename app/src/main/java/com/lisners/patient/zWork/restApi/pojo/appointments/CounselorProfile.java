
package com.lisners.patient.zWork.restApi.pojo.appointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CounselorProfile {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("clinic_name")
    @Expose
    private String clinicName;
    @SerializedName("voice_call")
    @Expose
    private String voiceCall;
    @SerializedName("video_call")
    @Expose
    private String videoCall;
    @SerializedName("default_duration")
    @Expose
    private int defaultDuration;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

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

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getVoiceCall() {
        return voiceCall;
    }

    public void setVoiceCall(String voiceCall) {
        this.voiceCall = voiceCall;
    }

    public String getVideoCall() {
        return videoCall;
    }

    public void setVideoCall(String videoCall) {
        this.videoCall = videoCall;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
