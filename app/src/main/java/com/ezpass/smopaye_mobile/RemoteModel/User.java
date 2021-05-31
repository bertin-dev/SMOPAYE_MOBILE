package com.ezpass.smopaye_mobile.RemoteModel;


import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;

/**
 * Structure du model de la classe User
 *
 * @see User
 */

public class User {

    private String id;
    private String nom;
    private String prenom;
    private String sexe;
    private String tel;
    private String cni;
    private String session;
    private String adresse;
    private String id_carte;
    private String typeUser;
    private String imageURL;
    private String status;
    private String search;
    private String abonnement;



    public User() {
    }

    public User(String id, String nom, String prenom, String sexe, String tel, String cni, String session, String adresse, String id_carte, String typeUser, String imageURL, String status, String search, String abonnement) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.tel = tel;
        this.cni = cni;
        this.session = session;
        this.adresse = adresse;
        this.id_carte = id_carte;
        this.typeUser = typeUser;
        this.imageURL = imageURL;
        this.status = status;
        this.search = search;
        this.abonnement = abonnement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCni() {
        return cni;
    }

    public void setCni(String cni) {
        this.cni = cni;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getId_carte() {
        return id_carte;
    }

    public void setId_carte(String id_carte) {
        this.id_carte = id_carte;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getAbonnement() { return abonnement; }

    public void setAbonnement(String abonnement) { this.abonnement = abonnement; }
}
