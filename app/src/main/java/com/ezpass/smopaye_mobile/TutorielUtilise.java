package com.ezpass.smopaye_mobile;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

public class TutorielUtilise extends AppCompatActivity {

    private int[] imageUrls = new int[]{R.raw.tutoriel_menu_slide, R.raw.recette, R.raw.debit, R.raw.historique, R.raw.localisation, R.raw.menuslide, R.raw.numero, R.raw.recharge, R.raw.retrait, R.raw.solde, R.raw.souscription, R.raw.abonnement};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriel_utilise);

        getSupportActionBar().setTitle(getString(R.string.tutoriel));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TutorielUtiliseViewPagerAdapter adapter = new TutorielUtiliseViewPagerAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);
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
            return true;
        }

        if (id == R.id.modifierCompte) {
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            startActivity(intent);
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


}
