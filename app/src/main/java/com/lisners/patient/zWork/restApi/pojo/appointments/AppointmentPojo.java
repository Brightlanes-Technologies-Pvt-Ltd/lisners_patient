package com.lisners.patient.zWork.restApi.pojo.appointments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lisners.patient.ApiModal.BookedAppointment;

import java.util.List;

public class AppointmentPojo {



    @SerializedName("data")
    @Expose
    private List<BookedAppointment> data = null;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("per_page")
    @Expose
    private int perPage;
    @SerializedName("current_page")
    @Expose
    private int currentPage;
    @SerializedName("last_page")
    @Expose
    private int lastPage;

    public List<BookedAppointment> getData() {
        return data;
    }

    public void setData(List<BookedAppointment> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

}
