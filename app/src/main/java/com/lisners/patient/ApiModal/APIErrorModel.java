package com.lisners.patient.ApiModal;

public class APIErrorModel {
    boolean status ;
    String message ;

    public boolean isSuccess() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
