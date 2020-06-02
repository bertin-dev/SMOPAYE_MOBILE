package com.ezpass.smopaye_mobile.Profil_user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Abonnement {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("subscriptionCharge")
    @Expose
    private String subscriptionCharge;
    @SerializedName("subscription_id")
    @Expose
    private String subscription_id;
    @SerializedName("subscription_type")
    @Expose
    private String subscription_type;
    @SerializedName("compte_id")
    @Expose
    private String compte_id;
    @SerializedName("starting_date")
    @Expose
    private String starting_date;
    @SerializedName("end_date")
    @Expose
    private String end_date;
    @SerializedName("validate")
    @Expose
    private String validate;
    @SerializedName("transaction_number")
    @Expose
    private String transaction_number;
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

    public String getSubscriptionCharge() {
        return subscriptionCharge;
    }

    public void setSubscriptionCharge(String subscriptionCharge) {
        this.subscriptionCharge = subscriptionCharge;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getSubscription_type() {
        return subscription_type;
    }

    public void setSubscription_type(String subscription_type) {
        this.subscription_type = subscription_type;
    }

    public String getCompte_id() {
        return compte_id;
    }

    public void setCompte_id(String compte_id) {
        this.compte_id = compte_id;
    }

    public String getStarting_date() {
        return starting_date;
    }

    public void setStarting_date(String starting_date) {
        this.starting_date = starting_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public String getTransaction_number() {
        return transaction_number;
    }

    public void setTransaction_number(String transaction_number) {
        this.transaction_number = transaction_number;
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
