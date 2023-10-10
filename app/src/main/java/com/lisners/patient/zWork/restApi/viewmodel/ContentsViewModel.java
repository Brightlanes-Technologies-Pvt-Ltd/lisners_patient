package com.lisners.patient.zWork.restApi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.pojo.appointments.AppointmentPojo;
import com.lisners.patient.zWork.restApi.pojo.bookAppointments.BookAppointmentPojo;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.restApi.pojo.timeSlot.TimeSlotPojo;
import com.lisners.patient.zWork.restApi.repo.ApiSRepo;

import java.util.Map;


public class ContentsViewModel extends AndroidViewModel {
    private ApiSRepo apiSRepo;


    public ContentsViewModel(@NonNull Application application) {
        super(application);
        this.apiSRepo = new ApiSRepo(application);
    }



    public LiveData<BasePojo<SearchCounselorPojo>> getAppointments(int page, String searchText,
                                                                   String specialization_id) {

        return apiSRepo.getAppointments(page,searchText,specialization_id);
    }


    public LiveData<BasePojo<TimeSlotPojo>> getTimeSlots(String counselor_id,
                                                         String week_day_id) {

        return apiSRepo.getTimeSlots(counselor_id,week_day_id);
    }



    public LiveData<BasePojo<BookAppointmentPojo>> bookAppointments(Map<String, String> params) {

        return apiSRepo.bookAppointments(params);
    }

    public LiveData<BasePojo<BookedAppointment>> callNow(Map<String, String> params) {

        return apiSRepo.callNow(params);
    }



    public LiveData<BasePojo<AppointmentPojo>> getPendingAppointments(int page) {

        return apiSRepo.getPendingAppointments(page);
    }


    public LiveData<BasePojo<AppointmentPojo>> getCompletedAppointments(int page) {

        return apiSRepo.getCompletedAppointments(page);
    }


    public LiveData<BasePojo<String>> getRtmToken(String chennal_code) {

        return apiSRepo.getRtmToken(chennal_code);
    }

}
