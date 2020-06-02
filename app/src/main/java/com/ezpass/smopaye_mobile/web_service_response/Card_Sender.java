package com.ezpass.smopaye_mobile.web_service_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card_Sender {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("code_number")
    @Expose
    private String code_number;
    @SerializedName("serial_number")
    @Expose
    private String serial_number;
    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("unity")
    @Expose
    private String unity;
    @SerializedName("deposit")
    @Expose
    private String deposit;
    @SerializedName("starting_date")
    @Expose
    private String starting_date;
    @SerializedName("end_date")
    @Expose
    private String end_date;
    @SerializedName("card_state")
    @Expose
    private String card_state;
    @SerializedName("user_created")
    @Expose
    private String user_created;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("notif")
    @Expose
    private String notif;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode_number() {
        return code_number;
    }

    public void setCode_number(String code_number) {
        this.code_number = code_number;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUnity() {
        return unity;
    }

    public void setUnity(String unity) {
        this.unity = unity;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
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

    public String getCard_state() {
        return card_state;
    }

    public void setCard_state(String card_state) {
        this.card_state = card_state;
    }

    public String getUser_created() {
        return user_created;
    }

    public void setUser_created(String user_created) {
        this.user_created = user_created;
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

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
