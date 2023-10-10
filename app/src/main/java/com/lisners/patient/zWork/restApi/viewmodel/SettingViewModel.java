package com.lisners.patient.zWork.restApi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lisners.patient.ApiModal.User;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.pojo.SettingPojo;
import com.lisners.patient.zWork.restApi.repo.ApiSRepo;


public class SettingViewModel extends AndroidViewModel {
    private ApiSRepo apiSRepo;


    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.apiSRepo = new ApiSRepo(application);
    }


    public LiveData<BasePojo<SettingPojo>> getSetting(String slug) {

        return apiSRepo.getSetting(slug);
    }



    public LiveData<BasePojo<User>> getProfile() {

        return apiSRepo.getProfile();
    }



}
