package com.ezpass.smopaye_mobile.vuesAccepteur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.telecollecte.DatabaseManager;
import com.ezpass.smopaye_mobile.telecollecte.ScoreData;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

import java.util.List;

public class ConsulterRecette extends AppCompatActivity {

    private LinearLayout detailRecette;
    private FloatingActionButton btnrecette;
    private TextView heureMinute, jour, mois, annee, montant;

    private Button totalMontantRecette;
   // private TextView listeRecette;
    private DatabaseManager databaseManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_recette);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.Recette));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        detailRecette = (LinearLayout) findViewById(R.id.detailRecette);
        btnrecette = (FloatingActionButton)findViewById(R.id.btnrecette);
        totalMontantRecette = (Button) findViewById(R.id.totalRecette);

        heureMinute = ((TextView) findViewById(R.id.txt_heureMinute));
        jour = (TextView) findViewById(R.id.txt_jour);
        mois = (TextView) findViewById(R.id.txt_mois);
        annee = (TextView) findViewById(R.id.txt_annee);
        montant = (TextView) findViewById(R.id.txt_montant);

        progressDialog=new ProgressDialog(ConsulterRecette.this);


        databaseManager = new DatabaseManager(this);

        List<ScoreData> totals = databaseManager.readTop10();
        for (ScoreData score : totals) {
            totalMontantRecette.setText(score.getMontant() + " FCFA");
        }

        btnrecette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detailRecette.getVisibility() == View.GONE){

                    //********************DEBUT***********
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // On ajoute un message à notre progress dialog
                            progressDialog.setMessage(getString(R.string.connexion));
                            // On donne un titre à notre progress dialog
                            progressDialog.setTitle(getString(R.string.attenteReponseServer));
                            // On spécifie le style
                            //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            // On affiche notre message
                            progressDialog.show();
                            //build.setPositiveButton("ok", new View.OnClickListener()
                        }
                    });
                    //*******************FIN*****
                    detailRecette.setVisibility(View.VISIBLE);


                    LinearLayout llDate = (LinearLayout) findViewById(R.id.linearDate);







                    List<ScoreData> scores = databaseManager.readAll();

                    for (int i = 0; i < scores.size(); i++) {
                        /*TextView textView1 = new TextView(ConsulterRecette.this); // Création d'un TextView
                        textView1.setText(scores.get(i).heure_minutes()); // Attache le texte
                        // Définition de la largeur et hauteur du textview
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        textView1.setLayoutParams(params);
                        llDate.addView(textView1, params); // Attache le TextView au layout parent*/

                    }

                    for (ScoreData score : scores) {

                       /* heureMinute.setText(score.heure_minutes().toString());
                        jour.setText(score.jours().toString());
                        mois.setText(score.mois().toString());
                        annee.setText(score.annees().toString());
                        montant.setText(score.getMontant1().toString());*/


                        mois.append( score.mois() + "\n\n" );
                        montant.append( score.getMontant() + " FCFA \n\n" );
                        heureMinute.append( score.heure_minutes() + "\n\n" );
                       // jour.append( score.jours() + "\n\n" );
//                        mois.append( score.mois() + "\n\n" );
                        /*annee.append( score.annees() + "\n\n" );*/
                    }






                    btnrecette.setEnabled(false);
                    progressDialog.dismiss();
                }
                else{
                    detailRecette.setVisibility(View.GONE);
                }

                //progressDialog.dismiss();

            }
        });
    }


    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.apropos) {
            Intent intent = new Intent(getApplicationContext(), Apropos.class);
            startActivity(intent);
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            startActivity(intent);
        }

        if(id == R.id.modifierCompte){
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            startActivity(intent);
        }

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     * @param newBase
     * @since 2020
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
