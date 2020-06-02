package com.ezpass.smopaye_mobile.Profil_user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataUser {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("phone")
    @Expose
    private String phone;

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

    @SerializedName("compte_id")
    @Expose
    private String compte_id;

    @SerializedName("role_id")
    @Expose
    private String role_id;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;


    @SerializedName("categorie")
    @Expose
    private CategoryUser categorie;

    @SerializedName("particulier")
    @Expose
    private List<Particulier> particulier;

    @SerializedName("role")
    @Expose
    private Role role;

    @SerializedName("compte")
    @Expose
    private Compte compte;

    @SerializedName("cards")
    @Expose
    private List<DataUserCard> cards;

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

    public String getCompte_id() {
        return compte_id;
    }

    public void setCompte_id(String compte_id) {
        this.compte_id = compte_id;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
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

    public CategoryUser getCategorie() {
        return categorie;
    }

    public void setCategorie(CategoryUser categorie) {
        this.categorie = categorie;
    }

    public List<Particulier> getParticulier() {
        return particulier;
    }

    public void setParticulier(List<Particulier> particulier) {
        this.particulier = particulier;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public List<DataUserCard> getCards() {
        return cards;
    }

    public void setCards(List<DataUserCard> cards) {
        this.cards = cards;
    }

    public List<Entreprise> getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(List<Entreprise> enterprise) {
        this.enterprise = enterprise;
    }
}
