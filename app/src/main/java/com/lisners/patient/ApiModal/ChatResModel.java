package com.lisners.patient.ApiModal;

public class ChatResModel {
    String text;
    boolean isMsg;

    public boolean isMsg() {
        return isMsg;
    }

    public void setMsg(boolean msg) {
        isMsg = msg;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
