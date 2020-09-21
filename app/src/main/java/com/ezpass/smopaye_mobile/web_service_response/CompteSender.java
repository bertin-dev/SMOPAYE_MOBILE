package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompteSender {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("account_number")
    @Expose
    private String account_number;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("account_state")
    @Expose
    private String account_state;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("notif")
    @Expose
    private String notif;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAccount_state() {
        return account_state;
    }

    public void setAccount_state(String account_state) {
        this.account_state = account_state;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
