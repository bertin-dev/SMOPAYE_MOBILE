package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.adapter.ViewPagerAdapterRetrait;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;

public class MenuRetraitOperateur extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static final String TAG = "MenuRetraitOperateur";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_retrait_operateur);


        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.retrait));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = findViewById(R.id.viewPager);

        addTabs(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager( viewPager );

    }



    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapterRetrait adapter = new ViewPagerAdapterRetrait(getSupportFragmentManager());
        adapter.addFrag(new FragmentRetraitChezSmopaye(), getString(R.string.smopaye));
        adapter.addFrag(new FragmentRetraitChezOperateurs(), getString(R.string.operateur));
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
            onBackPressed();
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

