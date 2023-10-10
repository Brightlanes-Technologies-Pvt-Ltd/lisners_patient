package com.lisners.patient.zWork.restApi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.pojo.ProfessionDatum;
import com.lisners.patient.zWork.restApi.repo.ApiSRepo;

import java.util.List;

public class AdvanceSearchViewModel extends AndroidViewModel {
    private final ApiSRepo apiSRepo;


    public AdvanceSearchViewModel(@NonNull Application application) {
        super(application);
        this.apiSRepo = new ApiSRepo(application);
    }


    public LiveData<BasePojo<List<ProfessionDatum>>> getProfession() {
        return apiSRepo.getProfession();
    }

}
