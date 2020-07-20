package com.ezpass.smopaye_mobile.Manage_Transactions_History;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class HistoriqueTransactions extends AppCompatActivity {

    private TextView mois, annee, titleTypeTransaction;
    private TabLayout tabLayout;
    private ViewPager vpContent;
    private String typeHistoriqueTransaction;
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
        setContentView(R.layout.activity_historique_transactions);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        titleTypeTransaction = (TextView) findViewById(R.id.titleTypeTransaction);
        mois = (TextView) findViewById(R.id.txt_moisHistorique);
        annee = (TextView) findViewById(R.id.txt_anneeHistorique);

        Intent intent = getIntent();
        typeHistoriqueTransaction = intent.getStringExtra("typeHistoriqueTransaction");

        if (typeHistoriqueTransaction.toLowerCase().equalsIgnoreCase("recharge")) {
            titleTypeTransaction.setText(getString(R.string.recharge));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.recharge));
            toolbar.setSubtitle(getString(R.string.recharge));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toLowerCase().equalsIgnoreCase("debitcarte")){
            titleTypeTransaction.setText(getString(R.string.debitCarte));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.debitCarte));
            toolbar.setSubtitle(getString(R.string.debitCarte));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toLowerCase().equalsIgnoreCase("transfert")){
            titleTypeTransaction.setText(getString(R.string.transfert));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.transfert));
            toolbar.setSubtitle(getString(R.string.transfert));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toLowerCase().equalsIgnoreCase("telecollecte")){
            titleTypeTransaction.setText(getString(R.string.telecollecte));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.telecollecte));
            toolbar.setSubtitle(getString(R.string.telecollecte));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else{
            getSupportActionBar().setTitle(getString(R.string.historiqueTransaction));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }


        Calendar calendar = Calendar.getInstance();
        // String currentDate = DateFormat.getDateInstance().format(calendar.getTime());// 31.07.2019
        String currentDate2 = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String[] part =currentDate2.split(" ");
        if(part[0].equalsIgnoreCase(currentDate2)){
            Toast.makeText(this, getString(R.string.dateSystemePosePB), Toast.LENGTH_SHORT).show();
        }
        else {
            mois.setText(part[2]);
            annee.setText(part[3]);
        }

        /* SLIDE DES DATES AU NIVEAU DES TRANSACTIONS*/
        tabLayout = (TabLayout) findViewById(R.id.tlTab);
        vpContent = (ViewPager) findViewById(R.id.vpContent);

        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setViewPager(vpContent);

        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(HistoriqueTransactions.this, String.valueOf(tab.getPosition()), Toast.LENGTH_SHORT).show();
                //vpContent.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        /*vpContenidos.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                Toast.makeText(HistoriqueTransactions.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
            }
        });*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
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


    private void setViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFragment(new TabLayoutScrollable().newInstance(24, 10, 2019), "Movie");

        Date d1 = new Date();

        /*Iterator<Date> i = new DateIterator(d1);
        while(i.hasNext())
        {
            Date date = i.next();
            adapter.addFragment(new TabLayoutScrollable(), date + ", Octobre");
        }*/


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        Date d2 = cal.getTime();
        String currentDate3;
        int histJour, histMois, histAnnee;

        Iterator<Date> i = new DateIterator(d1, d2);
        while(i.hasNext())
        {
            Date date = i.next();

            //Toast.makeText(this, String.valueOf(date.getDay()) + " Jours", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, String.valueOf(date.getMonth()) + " Mois", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, String.valueOf(new SimpleDateFormat("yyyy").format(date)) + " Annee", Toast.LENGTH_SHORT).show();

             currentDate3 = DateFormat.getDateInstance(DateFormat.FULL).format(date.getTime());

            histJour = Integer.parseInt(new SimpleDateFormat("dd").format(date));
            histMois = Integer.parseInt(new SimpleDateFormat("MM").format(date));
            histAnnee = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
            adapter.addFragment(new TabLayoutScrollable().newInstance(histJour, histMois, histAnnee, typeHistoriqueTransaction), String.valueOf(currentDate3));
        }


        //viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(30, true); //demarrage de la page par defaut
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

        return super.onOptionsItemSelected(item);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
            //return TabLayoutScrollable.getInstance(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        /*public void removeFragment(Fragment fragment, int position) {
            mFragmentList.remove(position);
            mFragmentTitleList.remove(position);
        }*/

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

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
