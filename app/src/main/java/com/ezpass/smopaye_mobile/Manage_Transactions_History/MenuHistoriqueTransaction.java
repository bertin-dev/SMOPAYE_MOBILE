package com.ezpass.smopaye_mobile.Manage_Transactions_History;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.Profil_user.Card;
import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_historique_trans.AllOperations;
import com.ezpass.smopaye_mobile.web_service_historique_trans.Home_AllHistoriques;
import com.ezpass.smopaye_mobile.web_service_historique_trans.RadarMarkerView;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
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
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpass.smopaye_mobile.R2.id.DoubleBounce;
import static com.ezpass.smopaye_mobile.R2.id.off;

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
    private String[] labels = {"Debit", "Code QR", "Transfert", "Recharge", "Retrait", "Facture"};
    String[] countries = new String[]{"India", "USA", "UK", "Libya", "Cuba"};

    private static final String TAG = "MenuHistoriqueTransacti";
    private ApiService service;
    private TokenManager tokenManager;
    private Call<Home_AllHistoriques> transaction;
    private List<AllOperations> historique;
    private int nbreDebit;
    private int nbreCodeQR;
    private int nbreTransfert;
    private int nbreRecharge;
    private int nbreRetrait;
    private int nbreFacture;
    private ArrayList<RadarEntry> dataValueChart;
    private RadarDataSet dataSet1;
    private float totalDebit;
    private float totalCodeQR;
    private float totalTransfert;
    private float totalRecharge;
    private float totalRetrait;
    private float totalFacture;

    private ProgressBar progressBar;
    private Sprite wave;
    DecimalFormat df = new DecimalFormat("0.00");

    @BindView(R.id.recharge)
    TextView recharge;
    @BindView(R.id.retrait)
    TextView retrait;
    @BindView(R.id.transfert)
    TextView transfert;
    @BindView(R.id.facture)
    TextView facture;
    @BindView(R.id.debit)
    TextView debit;
    @BindView(R.id.codeQr)
    TextView codeQr;

    private PieChart mChart;

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

        /*Initialisation de tous les objets qui seront manipulés*/
        ButterKnife.bind(this);
         progressBar = (ProgressBar)findViewById(R.id.spinKit_history);
         wave = new Pulse();
        progressBar.setIndeterminateDrawable(wave);

        //web service
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

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


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(transaction != null){
            transaction.cancel();
            transaction = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAllTransactions();
    }

    private void loadAllTransactions() {
        transaction = service.allTransactions();
        transaction.enqueue(new Callback<Home_AllHistoriques>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Home_AllHistoriques> call, Response<Home_AllHistoriques> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse " +response);

                if(response.isSuccessful()){

                    assert response.body() != null;
                     historique = response.body().getData();
                    //CHARGEMENT DES DONNEES GRAPHIQUES
                    radarChart = findViewById(R.id.radar_chart);
                    dataValueChart = new ArrayList<>();
                    init();
                    for(int i=0; i<historique.size(); i++){

                        if(historique.get(i).getTransaction_type().toLowerCase().contains("facture")){
                            nbreFacture++;
                            totalFacture = totalFacture + Float.parseFloat(historique.get(i).getAmount());
                        } else if(historique.get(i).getTransaction_type().toLowerCase().contains("debit")){
                            nbreDebit++;
                            totalDebit = totalDebit + Float.parseFloat(historique.get(i).getAmount());
                        } else if(historique.get(i).getTransaction_type().toLowerCase().contains("transfert")){
                            nbreTransfert++;
                            totalTransfert = totalTransfert + Float.parseFloat(historique.get(i).getAmount());
                        } else if(historique.get(i).getTransaction_type().toLowerCase().contains("recharge")){
                            nbreRecharge++;
                            totalRecharge = totalRecharge + Float.parseFloat(historique.get(i).getAmount());
                        } else if(historique.get(i).getTransaction_type().toLowerCase().contains("retrait")){
                            nbreRetrait++;
                            totalRetrait = totalRetrait + Float.parseFloat(historique.get(i).getAmount());
                        } else if(historique.get(i).getTransaction_type().toLowerCase().contains("qrcode")){
                            nbreCodeQR++;
                            totalCodeQR = totalCodeQR + Float.parseFloat(historique.get(i).getAmount());
                        }
                    }

                    dataValueChart.add(new RadarEntry(nbreDebit));
                    dataValueChart.add(new RadarEntry(nbreCodeQR));
                    dataValueChart.add(new RadarEntry(nbreTransfert));
                    dataValueChart.add(new RadarEntry(nbreRecharge));
                    dataValueChart.add(new RadarEntry(nbreRetrait));
                    dataValueChart.add(new RadarEntry(nbreFacture));
                    dataSet1 = new RadarDataSet(dataValueChart, getString(R.string.user1));


                    dataSet1.setColor(Color.rgb(103, 110, 129));
                    dataSet1.setFillColor(Color.rgb(103, 110, 129));
                    dataSet1.setDrawFilled(true);
                    dataSet1.setFillAlpha(180);
                    dataSet1.setLineWidth(2f);
                    dataSet1.setDrawHighlightCircleEnabled(true);
                    dataSet1.setDrawHighlightIndicators(false);



                    radarChart.getDescription().setEnabled(false);

                    radarChart.setWebLineWidth(1f);
                    radarChart.setWebColor(Color.LTGRAY);
                    radarChart.setWebLineWidthInner(1f);
                    radarChart.setWebColorInner(Color.LTGRAY);
                    radarChart.setWebAlpha(100);

                    //radar marker
                    progressBar.setVisibility(View.GONE);
                    MarkerView mv = new RadarMarkerView(MenuHistoriqueTransaction.this, R.layout.radar_markerview);
                    mv.setChartView(radarChart); // For bounds control
                    radarChart.setMarker(mv); // Set the marker to the chart

                    radarChart.animateXY(2000, 2000);
                    dataSet1.setColor(Color.RED);
                    RadarData data = new RadarData();


                    data.setValueTextSize(8f);
                    data.setDrawValues(false);
                    data.setValueTextColor(Color.WHITE);

                    data.addDataSet(dataSet1);
                    XAxis xAxis = radarChart.getXAxis();

                    xAxis.setTextSize(9f);
                    xAxis.setYOffset(0f);
                    xAxis.setXOffset(0f);

                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

                    xAxis.setTextColor(Color.WHITE);
                    YAxis yAxis = radarChart.getYAxis();
                    //yAxis.setLabelCount(6, false);
                    //yAxis.setTextSize(9f);
                    //yAxis.setAxisMinimum(0f);
                    //yAxis.setAxisMaximum(80f);
                    yAxis.setDrawLabels(false);


                    radarChart.setData(data);
                    radarChart.invalidate();




                    Legend l = radarChart.getLegend();
                    l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                    l.setDrawInside(false);
                    l.setXEntrySpace(7f);
                    l.setYEntrySpace(5f);
                    l.setTextColor(Color.WHITE);

                    //Toast.makeText(MenuHistoriqueTransaction.this, "DEBIT: " + totalDebit + " RECHARGE " + totalRecharge, Toast.LENGTH_SHORT).show();

                    recharge.setText(df.format(totalRecharge) + " FCFA");
                    retrait.setText(df.format(totalRetrait) + " FCFA");
                    transfert.setText(df.format(totalTransfert) + " FCFA");
                    debit.setText(df.format(totalDebit) + " FCFA");
                    facture.setText(df.format(totalFacture) + " FCFA");
                    codeQr.setText(df.format(totalCodeQR) + " FCFA");

                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(MenuHistoriqueTransaction.this, Login.class));
                    finish();
                }


            }

            @Override
            public void onFailure(Call<Home_AllHistoriques> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
            }
        });
    }

    private void init() {
        dataValueChart.clear();
        nbreFacture = 0;
        nbreDebit = 0;
        nbreTransfert = 0;
        nbreRecharge = 0;
        nbreRetrait = 0;
        nbreCodeQR = 0;

        totalDebit = 0;
        totalCodeQR = 0;
        totalFacture = 0;
        totalRecharge = 0;
        totalRetrait = 0;
    }


    /*private ArrayList<RadarEntry> dataValues1(){
        ArrayList<RadarEntry> dataVals = new ArrayList<>();
        dataVals.add(new RadarEntry(4));
        dataVals.add(new RadarEntry(7));
        dataVals.add(new RadarEntry(1));
        dataVals.add(new RadarEntry(5));
        dataVals.add(new RadarEntry(9));
        dataVals.add(new RadarEntry(19));
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
    }*/


    @OnClick(R.id.floatingDashboard)
    void loadPieChart(){

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this)
                .inflate(
                        R.layout.layout_bottom_sheet_chart,
                        (LinearLayout)bottomSheetDialog.findViewById(R.id.bottom_sheet)
                );

        mChart = bottomSheetView.findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.WHITE);

        progressBar = bottomSheetView.findViewById(R.id.spinKit_graph);
        wave = new Pulse();
        progressBar.setIndeterminateDrawable(wave);

        //moveOffScreen();

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setMaxAngle(180);
        mChart.setRotationAngle(180);
        mChart.setCenterTextOffset(0,-20);

        setData(4, 100);

        mChart.animateY(1000, Easing.EaseInOutCubic);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setYOffset(50f);

        /*TextView updateDateDialog = bottomSheetView.findViewById(R.id.updateDateDialog);

        String current = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss", Locale.getDefault()).format(new Date());
        updateDateDialog.setText(getString(R.string.updateTo) + " " + current);*/


        /*bottomSheetView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.fermeture), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });*/

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void setData(int count, int range){
        ArrayList<PieEntry> values = new ArrayList<>();

        for(int i=0; i<count; i++){
            float val = (float)((Math.random()*range)+range/5);
            values.add(new PieEntry(val, countries[i]));
        }

        PieDataSet dataSet = new PieDataSet(values, "Partner");
        dataSet.setSelectionShift(5f);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }

    private void moveOffScreen(PieChart mChart){
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;

        int offset = (int) (height*0.5);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mChart.getLayoutParams();
        params.setMargins(0,0, 0, -offset);
        mChart.setLayoutParams(params);
    }

}
