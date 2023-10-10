
package com.lisners.patient.zWork.restApi.pojo.appointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pivot {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("specialization_id")
    @Expose
    private String specializationId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(String specializationId) {
        this.specializationId = specializationId;
    }

}
