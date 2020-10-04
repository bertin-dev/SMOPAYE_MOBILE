package com.ezpass.smopaye_mobile.web_service_historique_trans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Particuliers {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("entite")
    @Expose
    private String entite;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntite() {
        return entite;
    }

    public void setEntite(String entite) {
        this.entite = entite;
    }
}
