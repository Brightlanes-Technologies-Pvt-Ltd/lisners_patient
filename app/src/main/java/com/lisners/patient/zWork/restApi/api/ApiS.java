package com.lisners.patient.zWork.restApi.api;

import com.lisners.patient.ApiModal.BookedAppointment;
import com.lisners.patient.ApiModal.User;
import com.lisners.patient.ApiModal.UserlistModel;
import com.lisners.patient.zWork.base.BasePojo;
import com.lisners.patient.zWork.restApi.pojo.ProfessionDatum;
import com.lisners.patient.zWork.restApi.pojo.SettingPojo;
import com.lisners.patient.zWork.restApi.pojo.UserData;
import com.lisners.patient.zWork.restApi.pojo.WithdrawPojo;
import com.lisners.patient.zWork.restApi.pojo.appointments.AppointmentPojo;
import com.lisners.patient.zWork.restApi.pojo.bookAppointments.BookAppointmentPojo;
import com.lisners.patient.zWork.restApi.pojo.searchCounselor.SearchCounselorPojo;
import com.lisners.patient.zWork.restApi.pojo.timeSlot.TimeSlotPojo;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiS {

    @Multipart
    @POST("profile-image-update")
    Call<BasePojo<User>> imageUpdate( @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("signup")
    Call<BasePojo<Void>> signUpSendOtp(@Field("mobile_no") String mobile_no);

    @FormUrlEncoded
    @POST("signup-otp")
    Call<BasePojo<UserData>> verifySignUpOtp(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("login")
    Call<BasePojo<UserData>> login(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("search")
    Call<BasePojo<SearchCounselorPojo>> getAppointments(@Field("page") int page,
                                                        @Field("search_text") String searchText,
                                                        @Field("specialization_id") String specialization_id);


    @FormUrlEncoded
    @POST("user-appointment-date")
    Call<BasePojo<TimeSlotPojo>> getTimeSlots(@Field("counselor_id") String counselor_id,
                                              @Field("week_day_id") String week_day_id);


    @FormUrlEncoded
    @POST("user-appointment-save")
    Call<BasePojo<BookAppointmentPojo>> bookAppointments(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("store-call-now")
    Call<BasePojo<BookedAppointment>> callNow(@FieldMap Map<String, String> params);

    @GET("get-pending-user-appointment")
    Call<BasePojo<AppointmentPojo>> getPendingAppointments(@Query("page") int page);


    @GET("get-complete-user-appointment")
    Call<BasePojo<AppointmentPojo>> getCompletedAppointments(@Query("page") int page);


    @FormUrlEncoded
    @POST("search")
    Call<BasePojo<SearchCounselorPojo>> searchCounselor(@Field("page") int page,
                                                        @Field("search_text") String searchText,
                                                        @Field("specialization_id") String specialization_id);


    /*@FieldMap("specialization_id[]") List<String> specialization_id,
    @Field("language_id[]") List<String> languageIDs,*/

    /*@FormUrlEncoded
    @POST("advance-search")
    Call<BasePojo<SearchCounselorPojo>> advanceSearchCounselor(@Field("page") int page,
                                                               @Field("search_text") String searchText,
                                                               @FieldMap Map<String, String> specialization_ids,
                                                               @FieldMap Map<String, String> languageIDs,
                                                               @Field("gender") String gender,
                                                               @Field("location") String location);*/


    @FormUrlEncoded
    @POST("advance-search")
    Call<BasePojo<SearchCounselorPojo>> advanceSearchCounselor(@Field("page") int page,
                                                               @Field("search_text") String searchText,
                                                               @Field("profession_id[]") List<String> profession_id,
                                                               @Field("specialization_id[]") List<String> specialization_id,
                                                               @Field("language_id[]") List<String> languageIDs,
                                                               @Field("gender") String gender,
                                                               @Field("location") String location);



    @FormUrlEncoded
    @POST("withdraw-requests")
    Call<BasePojo<WithdrawPojo>> submitWithdrawRequest(@FieldMap Map<String, String> params);




    @GET("get-profile")
    Call<BasePojo<User>> getProfile();


    @GET("get-favorite-counselor")
    Call<BasePojo<UserlistModel>> getFavoriteCounsellor(@Query("page") int page);

    @FormUrlEncoded
    @POST("https://admin.lisners.com/api/v1/setting")
    Call<BasePojo<SettingPojo>> getSetting(@Field("slug") String slug);




    @GET("https://admin.lisners.com/api/v1/professions")
    Call<BasePojo<List<ProfessionDatum>>> getProfession();

    @FormUrlEncoded
    @POST("agora/token")
    Call<BasePojo<String>> getRtmToken(@Field("chenal_code") String chennal_code);






}
