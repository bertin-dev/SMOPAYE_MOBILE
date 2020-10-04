package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.WebServiceResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HomeRetraitAccount {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private MessageAccount message;


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

    public MessageAccount getMessage() {
        return message;
    }

    public void setMessage(MessageAccount message) {
        this.message = message;
    }
}
