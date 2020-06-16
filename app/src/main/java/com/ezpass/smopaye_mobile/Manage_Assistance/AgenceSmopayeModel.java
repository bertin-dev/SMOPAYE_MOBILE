package com.ezpass.smopaye_mobile.Manage_Assistance;

public class AgenceSmopayeModel {

    private String ville;
    private String quartier;
    private String distance;

    public AgenceSmopayeModel(String ville, String quartier, String distance) {
        this.ville = ville;
        this.quartier = quartier;
        this.distance = distance;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
