package com.ezpass.smopaye_mobile.web_service_response.Recharge;

import com.ezpass.smopaye_mobile.web_service_response.Card_receiver;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageRechargeCardByAccount {

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("message")
    @Expose
    private Card_receiver message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Card_receiver getMessage() {
        return message;
    }

    public void setMessage(Card_receiver message) {
        this.message = message;
    }
}

