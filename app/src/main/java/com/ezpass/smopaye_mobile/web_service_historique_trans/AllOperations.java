package com.ezpass.smopaye_mobile.web_service_historique_trans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllOperations {

    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("Status")
    @Expose
    private String Status;
    @SerializedName("Operation")
    @Expose
    private String Operation;
    @SerializedName("Montant")
    @Expose
    private String Montant;
    @SerializedName("Frais")
    @Expose
    private String Frais;
    @SerializedName("user")
    @Expose
    List<Particuliers> user;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getOperation() {
        return Operation;
    }

    public void setOperation(String operation) {
        Operation = operation;
    }

    public String getMontant() {
        return Montant;
    }

    public void setMontant(String montant) {
        Montant = montant;
    }

    public String getFrais() {
        return Frais;
    }

    public void setFrais(String frais) {
        Frais = frais;
    }

    public List<Particuliers> getUser() {
        return user;
    }

    public void setUser(List<Particuliers> user) {
        this.user = user;
    }
}
