package com.lisners.patient.apis;

import com.lisners.patient.ApiModal.ChatResModel;
import com.lisners.patient.ApiModal.ChatSendModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitApi {
    @POST("chat")

        //on below line we are creating a method to post our data.
    Call<ChatResModel> createPost(@Header("accept") String accept, @Header("content-type") String contentType, @Header("authorization") String token, @Body ChatSendModel dataModal);
}
