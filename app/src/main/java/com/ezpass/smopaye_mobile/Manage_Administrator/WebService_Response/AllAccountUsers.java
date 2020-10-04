package com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllAccountUsers {

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
    @SerializedName("principal_account_id")
    @Expose
    private String principal_account_id;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;

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

    public String getPrincipal_account_id() {
        return principal_account_id;
    }

    public void setPrincipal_account_id(String principal_account_id) {
        this.principal_account_id = principal_account_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
