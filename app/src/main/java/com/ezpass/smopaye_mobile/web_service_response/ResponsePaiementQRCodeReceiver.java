package com.ezpass.smopaye_mobile.web_service_response;

import com.ezpass.smopaye_mobile.web_service_response.Recharge.DataAllUserCard;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponsePaiementQRCodeReceiver {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private List<DataQRCode> data;
    @SerializedName("message")
    @Expose
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataQRCode> getData() {
        return data;
    }

    public void setData(List<DataQRCode> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
