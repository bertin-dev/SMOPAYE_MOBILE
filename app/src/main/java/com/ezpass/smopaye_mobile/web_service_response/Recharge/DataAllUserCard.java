package com.ezpass.smopaye_mobile.web_service_response.Recharge;

import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.Profil_user.Entreprise;
import com.ezpass.smopaye_mobile.Profil_user.Particulier;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DataAllUserCard {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("cards")
    @Expose
    private List<DataUserCard> cards;
    @SerializedName("particulier")
    @Expose
    private List<Particulier> particulier;
    @SerializedName("enterprise")
    @Expose
    private List<Entreprise> enterprise;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<DataUserCard> getCards() {
        return cards;
    }

    public void setCards(List<DataUserCard> cards) {
        this.cards = cards;
    }

    public List<Particulier> getParticulier() {
        return particulier;
    }

    public void setParticulier(List<Particulier> particulier) {
        this.particulier = particulier;
    }

    public List<Entreprise> getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(List<Entreprise> enterprise) {
        this.enterprise = enterprise;
    }
}
