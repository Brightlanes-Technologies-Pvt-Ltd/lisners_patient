package com.lisners.patient.zWork.utils;


import com.lisners.patient.zWork.base.BasePojo;

public class ApiErrorUtils {

    public static BasePojo getErrorData(String message, int errorCode) {
        BasePojo bM = new BasePojo();
        bM.setMessage(message);
        bM.setErrorStatus(errorCode);
        bM.setStatus(false);
        return bM;
    }



}
