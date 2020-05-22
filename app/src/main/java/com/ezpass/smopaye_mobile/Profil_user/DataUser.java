package com.ezpass.smopaye_mobile.Profil_user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataUser {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("cni")
    @Expose
    private String cni;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("category_id")
    @Expose
    private int category_id;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("parent_id")
    @Expose
    private String parent_id;

    @SerializedName("created_by")
    @Expose
    private String created_by;

    @SerializedName("card_number")
    @Expose
    private String card_number;

    @SerializedName("role_id")
    @Expose
    private int role_id;

    @SerializedName("role")
    @Expose
    private Role role;

    @SerializedName("categorie")
    @Expose
    private Categorie categorie;


    @SerializedName("cards")
    @Expose
    private List<DataUserCard> cards;


    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCni() {
        return cni;
    }

    public void setCni(String cni) {
        this.cni = cni;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<DataUserCard> getCards() {
        return cards;
    }

    public void setCards(List<DataUserCard> cards) {
        this.cards = cards;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }
}
