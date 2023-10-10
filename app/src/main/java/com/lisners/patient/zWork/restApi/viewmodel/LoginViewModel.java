package com.lisners.patient.zWork.restApi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lisners.patient.ApiModal.User;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.pojo.UserData;
import com.lisners.patient.zWork.restApi.repo.ApiSRepo;

import java.io.File;
import java.util.Map;


public class LoginViewModel extends AndroidViewModel {
    private ApiSRepo apiSRepo;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.apiSRepo = new ApiSRepo(application);
    }


    public LiveData<BasePojo<Void>> signUpSendOtp(String mobile_no) {

        return apiSRepo.signUpSendOtp(mobile_no);
    }

    public LiveData<BasePojo<UserData>> verifySignUpOtp(Map<String,String> param) {
        return apiSRepo.verifySignUpOtp(param);
    }

    public LiveData<BasePojo<UserData>> login(Map<String,String> param) {
        return apiSRepo.login(param);
    }

    public LiveData<BasePojo<User>> imageUpdate(File imageFile) {
        return apiSRepo.imageUpdate(imageFile);
    }


}
