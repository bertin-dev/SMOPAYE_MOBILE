package com.ezpass.smopaye_mobile.web_service_historique_trans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllOperations {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("starting_date")
    @Expose
    private String starting_date;
    @SerializedName("end_date")
    @Expose
    private String end_date;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("card_id_sender")
    @Expose
    private String card_id_sender;
    @SerializedName("card_id_receiver")
    @Expose
    private String card_id_receiver;
    @SerializedName("account_id_sender")
    @Expose
    private String account_id_sender;
    @SerializedName("account_id_receiver")
    @Expose
    private String account_id_receiver;
    @SerializedName("transaction_type")
    @Expose
    private String transaction_type;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("servicecharge")
    @Expose
    private String servicecharge;
    @SerializedName("tarif_grid_id")
    @Expose
    private String tarif_grid_id;
    @SerializedName("device_id")
    @Expose
    private String device_id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCard_id_sender() {
        return card_id_sender;
    }

    public void setCard_id_sender(String card_id_sender) {
        this.card_id_sender = card_id_sender;
    }

    public String getCard_id_receiver() {
        return card_id_receiver;
    }

    public void setCard_id_receiver(String card_id_receiver) {
        this.card_id_receiver = card_id_receiver;
    }

    public String getAccount_id_sender() {
        return account_id_sender;
    }

    public void setAccount_id_sender(String account_id_sender) {
        this.account_id_sender = account_id_sender;
    }

    public String getAccount_id_receiver() {
        return account_id_receiver;
    }

    public void setAccount_id_receiver(String account_id_receiver) {
        this.account_id_receiver = account_id_receiver;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getServicecharge() {
        return servicecharge;
    }

    public void setServicecharge(String servicecharge) {
        this.servicecharge = servicecharge;
    }

    public String getTarif_grid_id() {
        return tarif_grid_id;
    }

    public void setTarif_grid_id(String tarif_grid_id) {
        this.tarif_grid_id = tarif_grid_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
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
