package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.WebServiceResponse;

import com.ezpass.smopaye_mobile.web_service_response.Card_Sender;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("sender")
    @Expose
    private Card_Sender sender;
    @SerializedName("notif")
    @Expose
    private String notif;

    public Card_Sender getSender() {
        return sender;
    }

    public void setSender(Card_Sender sender) {
        this.sender = sender;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
