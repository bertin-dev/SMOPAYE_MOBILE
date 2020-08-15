package com.ezpass.smopaye_mobile.Manage_Transactions_History;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MenuHistoriqueTransaction extends AppCompatActivity {

    private RelativeLayout historiqueRecharge, historiqueCodeQr, historiqueDebit, historiqueTransfert, historiqueFacture, historiqueRetrait;
    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    private Toolbar toolbar;


    private RadarChart radarChart;
    private String[] labels = {"Debit", "Code QR", "Transfert", "Recharge", "Retrait"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_menu_historique_transaction);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.historiqueTransaction));
            toolbar.setSubtitle(getString(R.string.voirDetails));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        historiqueRecharge = (RelativeLayout) findViewById(R.id.historiqueRecharge);
        historiqueRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "RECHARGE");
                startActivity(intent);
            }
        });

        historiqueCodeQr = (RelativeLayout) findViewById(R.id.historiqueCodeQr);
        historiqueCodeQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "QRCODE");
                startActivity(intent);
            }
        });

        historiqueDebit = (RelativeLayout) findViewById(R.id.historiqueDebit);
        historiqueDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "DEBIT");
                startActivity(intent);
            }
        });

        historiqueTransfert = (RelativeLayout) findViewById(R.id.historiqueTransfert);
        historiqueTransfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "TRANSFERT");
                startActivity(intent);
            }
        });

        historiqueFacture = (RelativeLayout) findViewById(R.id.historiqueFacture);
        historiqueFacture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "FACTURE");
                startActivity(intent);
            }
        });

        historiqueRetrait = (RelativeLayout) findViewById(R.id.historiqueRetrait);
        historiqueRetrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "RETRAIT");
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }


        //GRAPHIQUE
        radarChart = findViewById(R.id.radar_chart);
        RadarDataSet dataSet1 = new RadarDataSet(dataValues1(), "Utilisateur 1");
        RadarDataSet dataSet2 = new RadarDataSet(dataValues2(), "Utilisateur 2");

        dataSet1.setColor(Color.RED);
        dataSet2.setColor(Color.BLUE);

        RadarData data = new RadarData();
        data.addDataSet(dataSet1);
        data.addDataSet(dataSet2);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        radarChart.setData(data);
        radarChart.invalidate();
    }


    private ArrayList<RadarEntry> dataValues1(){
        ArrayList<RadarEntry> dataVals = new ArrayList<>();
        dataVals.add(new RadarEntry(4));
        dataVals.add(new RadarEntry(7));
        dataVals.add(new RadarEntry(1));
        dataVals.add(new RadarEntry(5));
        dataVals.add(new RadarEntry(9));
        return dataVals;
    }


    private ArrayList<RadarEntry> dataValues2(){
        ArrayList<RadarEntry> dataVals = new ArrayList<>();
        dataVals.add(new RadarEntry(7));
        dataVals.add(new RadarEntry(4));
        dataVals.add(new RadarEntry(8));
        dataVals.add(new RadarEntry(2));
        dataVals.add(new RadarEntry(6));
        return dataVals;
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
        ImageView img1 = (ImageView) findViewById(R.id.historiqueDirection1);
        TextView rechargeDirection1 = (TextView) findViewById(R.id.rechargeDirection1);

        ImageView img2 = (ImageView) findViewById(R.id.historiqueDirection2);
        TextView rechargeDirection2 = (TextView) findViewById(R.id.qrcodeDirection2);

        ImageView img3 = (ImageView) findViewById(R.id.historiqueDirection3);
        TextView rechargeDirection3 = (TextView) findViewById(R.id.debitDirection3);

        ImageView img4 = (ImageView) findViewById(R.id.historiqueDirection4);
        TextView rechargeDirection4 = (TextView) findViewById(R.id.transfertDirection4);

        ImageView img5 = (ImageView) findViewById(R.id.historiqueDirection5);
        TextView rechargeDirection5 = (TextView) findViewById(R.id.factureDirection5);

        ImageView img6 = (ImageView) findViewById(R.id.historiqueDirection6);
        TextView rechargeDirection6 = (TextView) findViewById(R.id.retraitDirection6);

        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));


            img1.setImageResource(R.drawable.ic_history_red_24dp);
            rechargeDirection1.setTextColor(ContextCompat.getColor(this, R.color.bgColorStandardRed));
            rechargeDirection1.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            rechargeDirection1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp_red, 0);

            img2.setImageResource(R.drawable.ic_history_red_24dp);
            rechargeDirection2.setTextColor(ContextCompat.getColor(this, R.color.bgColorStandardRed));
            rechargeDirection2.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            rechargeDirection2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp_red, 0);

            img3.setImageResource(R.drawable.ic_history_red_24dp);
            rechargeDirection3.setTextColor(ContextCompat.getColor(this, R.color.bgColorStandardRed));
            rechargeDirection3.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            rechargeDirection3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp_red, 0);

            img4.setImageResource(R.drawable.ic_history_red_24dp);
            rechargeDirection4.setTextColor(ContextCompat.getColor(this, R.color.bgColorStandardRed));
            rechargeDirection4.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            rechargeDirection4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp_red, 0);


            img5.setImageResource(R.drawable.ic_history_red_24dp);
            rechargeDirection5.setTextColor(ContextCompat.getColor(this, R.color.bgColorStandardRed));
            rechargeDirection5.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            rechargeDirection5.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp_red, 0);


            img6.setImageResource(R.drawable.ic_history_red_24dp);
            rechargeDirection6.setTextColor(ContextCompat.getColor(this, R.color.bgColorStandardRed));
            rechargeDirection6.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            rechargeDirection6.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_right_black_24dp_red, 0);



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
