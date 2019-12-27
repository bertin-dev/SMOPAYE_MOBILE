package com.ezpass.smopaye_mobile.Assistance;

public class AgenceSmopayeModel {

    private String ville;
    private String quartier;


    public AgenceSmopayeModel(String ville, String quartier) {
        this.ville = ville;
        this.quartier = quartier;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }
}
