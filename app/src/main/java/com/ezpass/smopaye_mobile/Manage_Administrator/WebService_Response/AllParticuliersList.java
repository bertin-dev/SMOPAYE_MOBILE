package com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllParticuliersList {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("cni")
    @Expose
    private String cni;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("user")
    @Expose
    private AllUsersList user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCni() {
        return cni;
    }

    public void setCni(String cni) {
        this.cni = cni;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public AllUsersList getUser() {
        return user;
    }

    public void setUser(AllUsersList user) {
        this.user = user;
    }
}
