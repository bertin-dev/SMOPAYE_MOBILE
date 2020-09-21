package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllMyHomeResponse {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private MyData data;
    @SerializedName("message")
    @Expose
    private AllMyMessages message;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MyData getData() {
        return data;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    public AllMyMessages getMessage() {
        return message;
    }

    public void setMessage(AllMyMessages message) {
        this.message = message;
    }
}
