package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("card_sender")
    @Expose
    private Card_Sender card_sender;
    @SerializedName("card_receiver")
    @Expose
    private Card_receiver card_receiver;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("success")
    @Expose
    private boolean success;

    public Card_Sender getCard_sender() {
        return card_sender;
    }

    public void setCard_sender(Card_Sender card_sender) {
        this.card_sender = card_sender;
    }

    public Card_receiver getCard_receiver() {
        return card_receiver;
    }

    public void setCard_receiver(Card_receiver card_receiver) {
        this.card_receiver = card_receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
