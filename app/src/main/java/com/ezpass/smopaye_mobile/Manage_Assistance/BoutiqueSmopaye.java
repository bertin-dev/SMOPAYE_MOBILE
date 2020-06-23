package com.ezpass.smopaye_mobile.Manage_Assistance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.ezpass.smopaye_mobile.Manage_Geolocalisation.HomeGoogleMap;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.service_indisponible.ServicesIndisponible;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;

public class BoutiqueSmopaye extends AppCompatActivity {

    private LinearLayout proximite, agenceSmopaye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boutique_smopaye);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.boutiqueSmopaye));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setDisplayShowHomeEnabled(true);

        agenceSmopaye = (LinearLayout) findViewById(R.id.btn_AgenceSmopaye);
        agenceSmopaye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AgenceSmopaye.class);
                startActivity(intent);
                Animatoo.animateZoom(BoutiqueSmopaye.this);  //fire the zoom animation
            }
        });

        proximite = (LinearLayout) findViewById(R.id.btn_proximite);
        proximite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeGoogleMap.class);
                startActivity(intent);
                Animatoo.animateZoom(BoutiqueSmopaye.this);  //fire the zoom animation
            }
        });


    }



    /*                    GESTION DU MENU DROIT                  */
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help_assistance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            Intent intent = new Intent(getApplicationContext(), Menu_Assistance.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }*/

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
