package com.lisners.patient.zWork.restApi.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.google.gson.Gson;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.ApiModal.UserlistModel;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.api.ApiS;
import com.lisners.patient.zWork.restApi.pojo.ProfessionDatum;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.utils.ApiErrorUtils;
import com.lisners.patient.zWork.utils.config.MainApplication;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppDataRepo {

    private static ApiS mainApi;
    private static Call<BasePojo<SearchCounselorPojo>> searchCall = null;
    private static Call<BasePojo<SearchCounselorPojo>> advanceSearchCall = null;
    private static Call<BasePojo<SearchCounselorPojo>> counselorCall = null;
    private static Call<BasePojo<UserlistModel>> favCounsellorCall = null;


    public static LiveData<BasePojo<SearchCounselorPojo>> searchCounselor(int page,
                                                                          String searchText,
                                                                          String specialization_id) {

        final MutableLiveData<BasePojo<SearchCounselorPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(MainApplication.getInstance()).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        if (searchCall != null) {
            searchCall.cancel();
        }

        searchCall = mainApi.searchCounselor(
                page,
                searchText,
                specialization_id);

        searchCall.enqueue(new Callback<BasePojo<SearchCounselorPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Response<BasePojo<SearchCounselorPojo>> response) {
                BasePojo<SearchCounselorPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled()) {
                    responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
                }
            }
        });

        return responseLiveData;
    }


    public static LiveData<BasePojo<SearchCounselorPojo>> advanceSearchCounselor(int page,
                                                                                 String searchText,
                                                                                 List<String> profession_id,
                                                                                 List<String> specialization_id,
                                                                                 List<String> languageIDs,
                                                                                 String gender,
                                                                                 String location) {

        final MutableLiveData<BasePojo<SearchCounselorPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(MainApplication.getInstance()).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        if (advanceSearchCall != null) {
            advanceSearchCall.cancel();
        }

       /* Map<String, String> sp_id_map = new HashMap<String, String>();
        Map<String, String> language_id_map = new HashMap<String, String>();
        for (int i = 0; i < specialization_id.size(); i++) {
            sp_id_map.put("specialization_id[" + (i + 1) + "]", specialization_id.get(i));
        }

        for (int j = 0; j < languageIDs.size(); j++) {
            language_id_map.put("language_id[" + (j + 1) + "]", languageIDs.get(j));
        }*/

        advanceSearchCall = mainApi.advanceSearchCounselor(
                page,
                searchText,
                profession_id,
                specialization_id,
                languageIDs,
                gender,
                location);

        advanceSearchCall.enqueue(new Callback<BasePojo<SearchCounselorPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Response<BasePojo<SearchCounselorPojo>> response) {
                BasePojo<SearchCounselorPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled()) {
                    responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
                }
            }
        });

        return responseLiveData;
    }


    public static LiveData<BasePojo<SearchCounselorPojo>> getAppointments(int page,
                                                                          String searchText,
                                                                          String specialization_id) {

        final MutableLiveData<BasePojo<SearchCounselorPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(MainApplication.getInstance()).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        if (counselorCall != null) {
            counselorCall.cancel();
        }

        counselorCall = mainApi.getAppointments(page, searchText, specialization_id);


        counselorCall.enqueue(new Callback<BasePojo<SearchCounselorPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Response<BasePojo<SearchCounselorPojo>> response) {
                BasePojo<SearchCounselorPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled()) {
                    responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
                }
            }
        });

        return responseLiveData;
    }



    public static LiveData<BasePojo<UserlistModel>> getFavoriteCounsellor(int page) {

        final MutableLiveData<BasePojo<UserlistModel>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(MainApplication.getInstance()).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        if (favCounsellorCall != null) {
            favCounsellorCall.cancel();
        }

       /* Map<String, String> sp_id_map = new HashMap<String, String>();
        Map<String, String> language_id_map = new HashMap<String, String>();
        for (int i = 0; i < specialization_id.size(); i++) {
            sp_id_map.put("specialization_id[" + (i + 1) + "]", specialization_id.get(i));
        }

        for (int j = 0; j < languageIDs.size(); j++) {
            language_id_map.put("language_id[" + (j + 1) + "]", languageIDs.get(j));
        }*/

        favCounsellorCall = mainApi.getFavoriteCounsellor(page);

        favCounsellorCall.enqueue(new Callback<BasePojo<UserlistModel>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<UserlistModel>> call, @NotNull Response<BasePojo<UserlistModel>> response) {
                BasePojo<UserlistModel> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<UserlistModel>> call, @NotNull Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled()) {
                    responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
                }
            }
        });

        return responseLiveData;
    }

}
