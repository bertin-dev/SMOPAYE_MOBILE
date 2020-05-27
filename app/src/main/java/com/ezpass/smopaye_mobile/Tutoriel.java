package com.ezpass.smopaye_mobile;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

import java.util.ArrayList;
import java.util.List;

public class Tutoriel extends AppCompatActivity {

    ViewPager viewPager;
    ModelAdapterSlideTutoriel adapter;
    List<ModelSlideTutoriel> models;
    Integer[] color = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriel);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.tutoriel));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);



        models = new ArrayList<>();
        //models.add(new ModelSlideHistorique(R.drawable.ic_help_black_24dp, "brochure", "desc"));
        models.add(new ModelSlideTutoriel(R.raw.tutoriel_menu_slide,"Lundi", "5000 FCFA"));
        models.add(new ModelSlideTutoriel(R.drawable.carte_smopaye,"Mardi, 09", "1500 FCFA"));
        models.add(new ModelSlideTutoriel(R.drawable.logo,"Mercredi, 10", "9500 FCFA"));

        adapter = new ModelAdapterSlideTutoriel(models, this);
        viewPager = findViewById(R.id.viewPagerTutoriel);
        viewPager.setAdapter(adapter);
       // viewPager.setPadding(130, 0, 130, 0);

        /*Integer[] colors_temp = {
                getResources().getColor(R.color.bgColorStandard),
                        getResources().getColor(R.color.textColorBlack),
                                getResources().getColor(R.color.colorAccent)};

        color = colors_temp;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {

                if(position < (adapter.getCount() - 1) &&  position < (color.length - 1)){
                    viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, color[position], color[position + 1]));

                }
                else{
                    viewPager.setBackgroundColor(color[color.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });*/
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
