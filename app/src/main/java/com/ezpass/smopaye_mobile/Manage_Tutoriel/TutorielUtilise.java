package com.ezpass.smopaye_mobile.Manage_Tutoriel;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;

public class TutorielUtilise extends AppCompatActivity {

    private int[] imageUrls = new int[]{R.raw.tutoriel_menu_slide, R.raw.recette, R.raw.debit, R.raw.historique, R.raw.localisation, R.raw.menuslide, R.raw.numero, R.raw.recharge, R.raw.retrait, R.raw.solde, R.raw.souscription, R.raw.abonnement};
    private String telephone, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriel_utilise);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.tutoriel));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        Intent intent = getIntent();
        role = intent.getStringExtra("role");
        telephone = intent.getStringExtra("telephone");

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
            intent.putExtra("role", role);
            intent.putExtra("telephone", telephone);
            startActivity(intent);
            Animatoo.animateZoom(this);  //fire the zoom animation
        }

        if (id == R.id.modifierCompte) {
            Intent intent = new Intent(getApplicationContext(), UpdatePassword.class);
            startActivity(intent);
            Animatoo.animateZoom(this);  //fire the zoom animation
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
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
