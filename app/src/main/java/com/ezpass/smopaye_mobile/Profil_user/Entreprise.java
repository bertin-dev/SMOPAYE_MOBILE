package com.ezpass.smopaye_mobile.Profil_user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Entreprise {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("raison_social")
    @Expose
    private String raison_social;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("principal_id")
    @Expose
    private String principal_id;

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

    public String getRaison_social() {
        return raison_social;
    }

    public void setRaison_social(String raison_social) {
        this.raison_social = raison_social;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPrincipal_id() {
        return principal_id;
    }

    public void setPrincipal_id(String principal_id) {
        this.principal_id = principal_id;
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
