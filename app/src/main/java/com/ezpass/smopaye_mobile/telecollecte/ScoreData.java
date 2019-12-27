package com.ezpass.smopaye_mobile.telecollecte;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScoreData  {

    private int idRecette;
    private String idCarte;
    private int montant;
    private Date dateEnregistrement;

    public ScoreData(int montant) {
        this.montant = montant;
    }

    public ScoreData(int idRecette, String idCarte, int montant, Date dateEnregistrement) {
        this.idRecette = idRecette;
        this.idCarte = idCarte;
        this.montant = montant;
        this.dateEnregistrement = dateEnregistrement;
    }

    public int getIdRecette() {
        return idRecette;
    }

    public void setIdRecette(int idRecette) {
        this.idRecette = idRecette;
    }

    public String getIdCarte() {
        return idCarte;
    }

    public void setIdCarte(String idCarte) {
        this.idCarte = idCarte;
    }

    public ScoreData() {
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public Date getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(Date dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }




    @Override
    public String toString() {
        String annee = dateEnregistrement.getYear()-100+"";
        String moi = 1+dateEnregistrement.getMonth()+"";
        String jour = dateEnregistrement.getDay()+"";
        String heure = dateEnregistrement.getHours()+"";
        String semaine = dateEnregistrement.getDate()+"";
        String minute = dateEnregistrement.getMinutes()+"";
        return idRecette + ": "+ getMontant()+" fcfa  le "+semaine+"/"+moi+"/"+annee+" à " +heure+"h "+minute+"  ";
    }


    public String heure_minutes(){
        return dateEnregistrement.getHours() + "h " +dateEnregistrement.getMinutes() + "min";
    }

    /*public String jours(){
        return (dateEnregistrement.getDay() + "");
    }*/

    public String mois(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        String months [] = {"Janvier","Fevrier","Mars","Avril","Mai","Juin","Juillet","Août","Sept","Octobre","Novembre","Décembre"};
        return ( dateEnregistrement.getDate()+" "+(months[dateEnregistrement.getMonth()])+" "+ year);
    }

   /* public String annees(){
        return (dateEnregistrement.getYear() - 100 + "");
    }*/

    public String getMontant1() {
        return montant + "";
    }

}
