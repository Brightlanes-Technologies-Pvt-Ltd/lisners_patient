package com.lisners.patient.zWork.restApi.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.google.gson.Gson;
import com.lisners.patient.ApiModal.APIErrorModel;
import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.api.ApiS;
import com.lisners.patient.zWork.restApi.pojo.ProfessionDatum;
import com.lisners.patient.zWork.restApi.pojo.SettingPojo;
import com.lisners.patient.zWork.restApi.pojo.UserData;
import com.lisners.patient.zWork.restApi.pojo.WithdrawPojo;
import com.lisners.patient.zWork.restApi.pojo.appointments.AppointmentPojo;
import com.lisners.patient.zWork.restApi.pojo.bookAppointments.BookAppointmentPojo;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.restApi.pojo.timeSlot.TimeSlotPojo;
import com.lisners.patient.zWork.utils.ApiErrorUtils;
import com.lisners.patient.zWork.utils.config.MainApplication;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiSRepo {
    private Application mApplication;
    private ApiS api;



    public ApiSRepo(Application application) {
        this.mApplication = application;
        this.api = MainApplication.get(application).getAppComponent().getApiS();

    }



    public LiveData<BasePojo<User>> imageUpdate(File file) {

        final MutableLiveData<BasePojo<User>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        MultipartBody.Part imageBody;
        if (file != null) {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"),
                            file
                    );
            imageBody = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        } else {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"),
                            ""
                    );
            imageBody = MultipartBody.Part.createFormData("image", "", requestFile);
        }



        Call<BasePojo<User>> call = api.imageUpdate(imageBody);
        call.enqueue(new Callback<BasePojo<User>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<User>> call, @NotNull Response<BasePojo<User>> response) {
                BasePojo<User> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();
                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<User>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }



    public LiveData<BasePojo<Void>> signUpSendOtp(String mobile_no) {

        final MutableLiveData<BasePojo<Void>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<Void>> call = api.signUpSendOtp(mobile_no);
        call.enqueue(new Callback<BasePojo<Void>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<Void>> call, @NotNull Response<BasePojo<Void>> response) {
                BasePojo<Void> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();
                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<Void>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<UserData>> verifySignUpOtp(Map<String, String> params) {

        final MutableLiveData<BasePojo<UserData>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<UserData>> call = api.verifySignUpOtp(params);
        call.enqueue(new Callback<BasePojo<UserData>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<UserData>> call, @NotNull Response<BasePojo<UserData>> response) {
                BasePojo<UserData> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();
                }  else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<UserData>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<UserData>> login(Map<String, String> params) {

        final MutableLiveData<BasePojo<UserData>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<UserData>> call = api.login(params);
        call.enqueue(new Callback<BasePojo<UserData>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<UserData>> call, @NotNull Response<BasePojo<UserData>> response) {
                BasePojo<UserData> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();
                }  else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<UserData>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<SearchCounselorPojo>> getAppointments(int page, String searchText,
                                                                   String specialization_id) {

        final MutableLiveData<BasePojo<SearchCounselorPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<SearchCounselorPojo>> call = api.getAppointments(page, searchText, specialization_id);
        call.enqueue(new Callback<BasePojo<SearchCounselorPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Response<BasePojo<SearchCounselorPojo>> response) {
                BasePojo<SearchCounselorPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();
                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<SearchCounselorPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<TimeSlotPojo>> getTimeSlots(String counselor_id,
                                                         String week_day_id) {

        final MutableLiveData<BasePojo<TimeSlotPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<TimeSlotPojo>> call = api.getTimeSlots(counselor_id, week_day_id);
        call.enqueue(new Callback<BasePojo<TimeSlotPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<TimeSlotPojo>> call, @NotNull Response<BasePojo<TimeSlotPojo>> response) {
                BasePojo<TimeSlotPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }

                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);


                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<TimeSlotPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));


            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<BookAppointmentPojo>> bookAppointments(Map<String, String> params) {

        final MutableLiveData<BasePojo<BookAppointmentPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<BookAppointmentPojo>> call = api.bookAppointments(params);
        call.enqueue(new Callback<BasePojo<BookAppointmentPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<BookAppointmentPojo>> call, @NotNull Response<BasePojo<BookAppointmentPojo>> response) {
                BasePojo<BookAppointmentPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<BookAppointmentPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<BookedAppointment>> callNow(Map<String, String> params) {

        final MutableLiveData<BasePojo<BookedAppointment>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<BookedAppointment>> call = api.callNow(params);
        call.enqueue(new Callback<BasePojo<BookedAppointment>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<BookedAppointment>> call, @NotNull Response<BasePojo<BookedAppointment>> response) {
                BasePojo<BookedAppointment> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<BookedAppointment>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }

    public LiveData<BasePojo<AppointmentPojo>> getPendingAppointments(int page) {

        final MutableLiveData<BasePojo<AppointmentPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<AppointmentPojo>> call = api.getPendingAppointments(page);
        call.enqueue(new Callback<BasePojo<AppointmentPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<AppointmentPojo>> call, @NotNull Response<BasePojo<AppointmentPojo>> response) {
                BasePojo<AppointmentPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);


                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<AppointmentPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));


            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<AppointmentPojo>> getCompletedAppointments(int page) {

        final MutableLiveData<BasePojo<AppointmentPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<AppointmentPojo>> call = api.getCompletedAppointments(page);
        call.enqueue(new Callback<BasePojo<AppointmentPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<AppointmentPojo>> call, @NotNull Response<BasePojo<AppointmentPojo>> response) {
                BasePojo<AppointmentPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);


                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<AppointmentPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));


            }
        });

        return responseLiveData;
    }



    public LiveData<BasePojo<User>> getProfile() {

        final MutableLiveData<BasePojo<User>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<User>> call = api.getProfile();
        call.enqueue(new Callback<BasePojo<User>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<User>> call, @NotNull Response<BasePojo<User>> response) {
                BasePojo<User> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                }
                else if(response.code() == 401){
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.UNAUTHORIZED, BasePojo.ErrorCode.UNAUTHORIZED);
                }
                else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);


                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<User>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));


            }
        });

        return responseLiveData;
    }



    public LiveData<BasePojo<List<ProfessionDatum>>> getProfession() {

        
        final MutableLiveData<BasePojo<List<ProfessionDatum>>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<List<ProfessionDatum>>> call = api.getProfession();
        call.enqueue(new Callback<BasePojo<List<ProfessionDatum>>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<List<ProfessionDatum>>> call, @NotNull Response<BasePojo<List<ProfessionDatum>>> response) {
                BasePojo<List<ProfessionDatum>> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                }
                else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                }
                else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);


                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<List<ProfessionDatum>>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));


            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<SettingPojo>> getSetting(String slug) {

        final MutableLiveData<BasePojo<SettingPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<SettingPojo>> call = api.getSetting(slug);
        call.enqueue(new Callback<BasePojo<SettingPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<SettingPojo>> call, @NotNull Response<BasePojo<SettingPojo>> response) {
                BasePojo<SettingPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);


                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<SettingPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));


            }
        });

        return responseLiveData;
    }



    public LiveData<BasePojo<String>> getRtmToken(String chennal_code) {

        final MutableLiveData<BasePojo<String>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<String>> call = api.getRtmToken(chennal_code);
        call.enqueue(new Callback<BasePojo<String>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<String>> call, @NotNull Response<BasePojo<String>> response) {
                BasePojo<String> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();

                } else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<String>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


    public LiveData<BasePojo<WithdrawPojo>> submitWithdrawRequest(Map<String, String> params) {

        final MutableLiveData<BasePojo<WithdrawPojo>> responseLiveData = new MutableLiveData<>();

        if (!MainApplication.get(mApplication).getMerlinsBeard().isConnected()) {
            responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.NO_INTERNET, BasePojo.ErrorCode.NO_INTERNET));
            return responseLiveData;
        }


        Call<BasePojo<WithdrawPojo>> call = api.submitWithdrawRequest(params);
        call.enqueue(new Callback<BasePojo<WithdrawPojo>>() {
            @Override
            public void onResponse(@NotNull Call<BasePojo<WithdrawPojo>> call, @NotNull Response<BasePojo<WithdrawPojo>> response) {
                BasePojo<WithdrawPojo> resModel;

                if (response.code() == 200 && response.body() != null) {
                    resModel = response.body();
                }  else if (response.code() == 422) {
                    APIErrorModel apiErrorModel = null;
                    try {
                        apiErrorModel = new Gson().fromJson(response.errorBody().string(), APIErrorModel.class);
                        if (apiErrorModel.getMessage() != null)
                            resModel = ApiErrorUtils.getErrorData(apiErrorModel.getMessage(), 0);
                        else {
                            resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        }

                    } catch (IOException e) {
                        resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);

                        e.printStackTrace();
                    }
                } else {
                    resModel = ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED);
                }
                responseLiveData.postValue(resModel);
            }

            @Override
            public void onFailure(@NotNull Call<BasePojo<WithdrawPojo>> call, @NotNull Throwable t) {
                t.printStackTrace();
                responseLiveData.postValue(ApiErrorUtils.getErrorData(BasePojo.ErrorMessage.SOME_ERROR_OCCURRED, BasePojo.ErrorCode.SOME_ERROR_OCCURRED));
            }
        });

        return responseLiveData;
    }


}
