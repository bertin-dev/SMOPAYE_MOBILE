package com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response;

import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.Profil_user.Role;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllUsersList {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("cards")
    @Expose
    private List<DataUserCard> cards;

    @SerializedName("role")
    @Expose
    private Role role;

    @SerializedName("compte")
    @Expose
    private AllAccountUsers compte;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DataUserCard> getCards() {
        return cards;
    }

    public void setCards(List<DataUserCard> cards) {
        this.cards = cards;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AllAccountUsers getCompte() {
        return compte;
    }

    public void setCompte(AllAccountUsers compte) {
        this.compte = compte;
    }
}
