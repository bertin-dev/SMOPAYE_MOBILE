package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.WebServiceResponse;

import com.ezpass.smopaye_mobile.web_service_response.Card_Sender;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageAccount {

    @SerializedName("sender")
    @Expose
    private SenderAccount sender;
    @SerializedName("notif")
    @Expose
    private String notif;

    public SenderAccount getSender() {
        return sender;
    }

    public void setSender(SenderAccount sender) {
        this.sender = sender;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
