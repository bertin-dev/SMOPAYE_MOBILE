package com.ezpass.smopaye_mobile.web_service_historique_trans;

import com.ezpass.smopaye_mobile.Profil_user.Entreprise;
import com.ezpass.smopaye_mobile.Profil_user.Particulier;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Users {

    @SerializedName("entreprise")
    @Expose
    private Entreprises entreprise;
    @SerializedName("particulier")
    @Expose
    private Particuliers particulier;

    public Entreprises getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprises entreprise) {
        this.entreprise = entreprise;
    }

    public Particuliers getParticulier() {
        return particulier;
    }

    public void setParticulier(Particuliers particulier) {
        this.particulier = particulier;
    }
}
