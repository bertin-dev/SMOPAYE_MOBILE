package com.ezpass.smopaye_mobile.Manage_Consult;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.telecollecte.DatabaseManager;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.telecollecte.ScoreData;

import java.util.List;

public class ConsulterRecette extends AppCompatActivity {

    private LinearLayout detailRecette;
    private FloatingActionButton btnrecette;
    private TextView heureMinute, jour, mois, annee, montant;

    private Button totalMontantRecette;
   // private TextView listeRecette;
    private DatabaseManager databaseManager;
    private ProgressDialog progressDialog;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_consulter_recette);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.Recette));
        toolbar.setSubtitle(getString(R.string.ezpass));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
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
            Intent intent = new Intent(getApplicationContext(), UpdatePassword.class);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));
            totalMontantRecette.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));
            FloatingActionButton ttt = (FloatingActionButton) findViewById(R.id.btnrecette);
            //ttt.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
            ttt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryRed)));

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
        } else{
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
    }

}
