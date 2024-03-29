package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;

public class Menu_GestionComptes extends AppCompatActivity {

    private LinearLayout accepteur, userAgent, usersGoogle, btnGesAdmin, btnGesUser;
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
        setContentView(R.layout.activity_menu__gestion_comptes);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.gesComptes));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        accepteur = (LinearLayout) findViewById(R.id.btnGesCommercants);
        userAgent = (LinearLayout) findViewById(R.id.btnGesCommerciaux);
        usersGoogle = (LinearLayout) findViewById(R.id.usersGoogle);
        btnGesAdmin = (LinearLayout) findViewById(R.id.btnGesAdmin);
        btnGesUser = (LinearLayout) findViewById(R.id.btnGesUser);


        accepteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GestionAccepteur.class);
                startActivity(intent);
            }
        });


        userAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GestionAgents.class);
                startActivity(intent);
            }
        });


        btnGesAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GestionAdministrateur.class);
                startActivity(intent);
            }
        });


        btnGesUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GestionUtilisateurs.class);
                startActivity(intent);
            }
        });


        usersGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccueilUserGoogle.class);
                startActivity(intent);
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
