package com.ezpass.smopaye_mobile.web_service_response.Recharge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListAllUserCardResponse {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<DataAllUserCard> data;
    @SerializedName("message")
    @Expose
    private String message;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataAllUserCard> getData() {
        return data;
    }

    public void setData(List<DataAllUserCard> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
