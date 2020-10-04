package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.WebServiceResponse;

import com.ezpass.smopaye_mobile.web_service_response.MyData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeRetrait {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private Message message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
