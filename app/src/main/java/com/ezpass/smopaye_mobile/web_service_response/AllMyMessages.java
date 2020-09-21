package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllMyMessages {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("sender")
    @Expose
    private CompteSender sender;
    @SerializedName("compte_receiver")
    @Expose
    private CompteReceiver compte_receiver;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CompteSender getSender() {
        return sender;
    }

    public void setSender(CompteSender sender) {
        this.sender = sender;
    }

    public CompteReceiver getCompte_receiver() {
        return compte_receiver;
    }

    public void setCompte_receiver(CompteReceiver compte_receiver) {
        this.compte_receiver = compte_receiver;
    }
}
