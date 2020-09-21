package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Home_toggle {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("message")
    @Expose
    private MessageToggle message;

    public Home_toggle() {
    }

    public Home_toggle(boolean success, MessageToggle message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MessageToggle getMessage() {
        return message;
    }

    public void setMessage(MessageToggle message) {
        this.message = message;
    }
}

