package com.ezpass.smopaye_mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.telecollecte.DatabaseManager;
import com.ezpass.smopaye_mobile.telecollecte.ScoreData;
import com.ezpass.smopaye_mobile.vuesAccepteur.MenuRetraitTelecollecte;
import com.ezpass.smopaye_mobile.vuesAccepteur.RetraitAccepteur;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.telpo.tps550.api.TelpoException;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MenuRetraitOperateur extends AppCompatActivity {

    private LinearLayout lnRetraitSmopaye,lnRetraitOperateur;

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_retrait_operateur);


        getSupportActionBar().setTitle("Menu Retrait");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lnRetraitSmopaye = (LinearLayout) findViewById(R.id.lnRetraitSmopaye);
        lnRetraitOperateur = (LinearLayout) findViewById(R.id.lnRetraitOperateur);


        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getApplicationContext().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        lnRetraitSmopaye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuRetraitOperateur.this, RetraitChezSmopaye.class));
            }
        });


        lnRetraitOperateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuRetraitOperateur.this, RetraitAccepteur.class));
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
}
