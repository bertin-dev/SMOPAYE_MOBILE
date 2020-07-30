package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Home_toggle {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("card_sender")
    @Expose
    private Card_Sender card_sender;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Card_Sender getCard_sender() {
        return card_sender;
    }

    public void setCard_sender(Card_Sender card_sender) {
        this.card_sender = card_sender;
    }
}

