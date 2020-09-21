package com.ezpass.smopaye_mobile.Manage_Transactions_History;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;

import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_historique_trans.AllOperations;
import com.ezpass.smopaye_mobile.web_service_historique_trans.Home_AllHistoriques;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Pulse;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private LineChart mChart;
    private Intent intent;
    private static final String TAG = "HistoriqueTransactions";
    private ApiService service;
    private TokenManager tokenManager;
    private Call<Home_AllHistoriques> transaction;
    private List<AllOperations> historique;
    private int j = 0;
    private ProgressBar progressBar;
    private Sprite wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_historique_transactions);

        progressBar = (ProgressBar)findViewById(R.id.spinKit_history);
        wave = new Pulse();
        progressBar.setIndeterminateDrawable(wave);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        //web service
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        titleTypeTransaction = (TextView) findViewById(R.id.titleTypeTransaction);
        mois = (TextView) findViewById(R.id.txt_moisHistorique);
        annee = (TextView) findViewById(R.id.txt_anneeHistorique);

        intent = getIntent();
        typeHistoriqueTransaction = intent.getStringExtra("typeHistoriqueTransaction");

        if (typeHistoriqueTransaction.toUpperCase().equalsIgnoreCase("RECHARGE")) {
            titleTypeTransaction.setText(getString(R.string.recharge));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.recharge));
            toolbar.setSubtitle(getString(R.string.ezpass));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toUpperCase().equalsIgnoreCase("PAYEMENT_VIA_QR-CODE")){
            titleTypeTransaction.setText(getString(R.string.qrcode));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.qrcode));
            toolbar.setSubtitle(getString(R.string.ezpass));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toUpperCase().equalsIgnoreCase("DEBIT_CARTE")){
            titleTypeTransaction.setText(getString(R.string.debitCarte));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.debitCarte));
            toolbar.setSubtitle(getString(R.string.ezpass));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toUpperCase().equalsIgnoreCase("TRANSFERT")){
            titleTypeTransaction.setText(getString(R.string.transfert));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.transfert));
            toolbar.setSubtitle(getString(R.string.ezpass));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }else if(typeHistoriqueTransaction.toUpperCase().equalsIgnoreCase("PAYEMENT_FACTURE")){
            titleTypeTransaction.setText(getString(R.string.facture));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.facture));
            toolbar.setSubtitle(getString(R.string.ezpass));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else if(typeHistoriqueTransaction.toUpperCase().equalsIgnoreCase("RETRAIT")){
            titleTypeTransaction.setText(getString(R.string.retrait));
            getSupportActionBar().setTitle(getString(R.string.historique) +" "+ getString(R.string.retrait));
            toolbar.setSubtitle(getString(R.string.ezpass));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        else{
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


    @Override
    protected void onResume() {
        super.onResume();
        loadDataApi(new ArrayList<>());
    }

    private void loadDataApi(ArrayList<Entry> yVals) {

        transaction = service.allTransactions();
        transaction.enqueue(new Callback<Home_AllHistoriques>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Home_AllHistoriques> call, Response<Home_AllHistoriques> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse Bottom Sheet " +response);

                if(response.isSuccessful()){

                    assert response.body() != null;
                    historique = response.body().getData();
                    //CHARGEMENT DES DONNEES GRAPHIQUES
                    mChart = (LineChart) findViewById(R.id.mChart);
                    //mChart.setBackgroundColor(Color.WHITE);
                    //mChart.setGridBackgroundColor(Color.CYAN);
                    //mChart.setDrawGridBackground(true);

                    mChart.setDrawBorders(false);
                    mChart.setBorderWidth(0);
                    mChart.getDescription().setEnabled(false);
                    mChart.setPinchZoom(true);
                    //mChart.setTouchEnabled(true);
                    //mChart.setScaleMinima(200,200);

                    XAxis xl = mChart.getXAxis();
                    xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xl.setDrawAxisLine(false);
                    xl.setDrawGridLines(false);
                    //xl.setAxisMaximum(70);
                    //xl.setAxisMinimum(0);
                    xl.setGranularity(10f);

                    Legend l = mChart.getLegend();
                    l.setEnabled(true);
                    YAxis leftAxis = mChart.getAxisLeft();
                    mChart.getAxisRight().setEnabled(false);
                    leftAxis.removeAllLimitLines();
                    //coordonnée x est caché
                    mChart.getXAxis().setEnabled(false);
                    //leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

                    //leftAxis.setAxisMaximum(70);
                    //leftAxis.setAxisMinimum(0);
                    leftAxis.setDrawAxisLine(false);
                    leftAxis.setDrawZeroLine(false);
                    leftAxis.setDrawGridLines(false);
                    mChart.animateXY(2000, 2000);


                    yVals.clear();
                    for(int i=0; i<historique.size(); i++){

                        if(historique.get(i).getTransaction_type().toLowerCase().equalsIgnoreCase(typeHistoriqueTransaction.toLowerCase())){

                            if(historique.get(i).getState().toLowerCase().contains("success")){
                                j++;
                                yVals.add(new Entry(j, Float.parseFloat(historique.get(i).getAmount())));
                            }
                        }
                    }

                    LineDataSet set1;

                    if (mChart.getData() != null &&
                            mChart.getData().getDataSetCount() > 0) {
                        set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                        set1.setValues(yVals);
                        mChart.getData().notifyDataChanged();
                        mChart.notifyDataSetChanged();
                    } else {
                        // create a dataset and give it a type
                        set1 = new LineDataSet(yVals, getString(R.string.montant).toUpperCase() + " " + typeHistoriqueTransaction);

                        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        set1.setCubicIntensity(0.2f);
                        set1.setDrawFilled(true);
                        set1.setDrawCircles(true);
                        set1.setLineWidth(1.8f);
                        set1.setCircleRadius(4f);
                        set1.setCircleColor(Color.WHITE);
                        set1.setHighLightColor(Color.rgb(244, 117, 117));
                        set1.setColor(Color.WHITE);
                        set1.setFillColor(Color.WHITE);
                        set1.setFillAlpha(100);
                        set1.setDrawHorizontalHighlightIndicator(false);
                        set1.setFillFormatter(new IFillFormatter() {
                            @Override
                            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                                return mChart.getAxisLeft().getAxisMinimum();
                            }
                        });

                        //hide progessbar
                        progressBar.setVisibility(View.GONE);

                        // create a data object with the data sets
                        LineData data = new LineData(set1);
                        data.setValueTextSize(9f);
                        data.setDrawValues(false);
                        mChart.setData(data);
                    }

                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(HistoriqueTransactions.this, Login.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Home_AllHistoriques> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "SMOPAYE_SERVER onFailure Bottom Sheet " + t.getMessage());
            }
        });

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


    @SuppressLint("SimpleDateFormat")
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
            adapter.addFragment(TabLayoutScrollable.newInstance(histJour, histMois, histAnnee, typeHistoriqueTransaction), String.valueOf(currentDate3));
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



    static class ViewPagerAdapter extends FragmentPagerAdapter {
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(transaction != null){
            transaction.cancel();
            transaction = null;
        }
    }

}
