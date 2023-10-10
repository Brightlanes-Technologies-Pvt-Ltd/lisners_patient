package com.lisners.patient.zWork.restApi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.pojo.WithdrawPojo;
import com.lisners.patient.zWork.restApi.repo.ApiSRepo;

import java.util.Map;


public class WithdrawViewModel extends AndroidViewModel {
    private ApiSRepo apiSRepo;


    public WithdrawViewModel(@NonNull Application application) {
        super(application);
        this.apiSRepo = new ApiSRepo(application);
    }


    public LiveData<BasePojo<WithdrawPojo>> submitWithdrawRequest(Map<String,String> param) {
        return apiSRepo.submitWithdrawRequest(param);
    }



}
