package com.ezpass.smopaye_mobile.Manage_Transactions_History;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;

public class MenuHistoriqueTransaction extends AppCompatActivity {

    private LinearLayout historiqueRecharge, historiqueTelecollecte, historiqueDebit, historiqueTransfert;
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
        setContentView(R.layout.activity_menu_historique_transaction);


        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.historiqueTransaction));
        toolbar.setSubtitle(getString(R.string.voirDetails));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        /*Intent intent = getIntent();
        telephone = intent.getStringExtra("telephone");

        Toast.makeText(this, telephone, Toast.LENGTH_SHORT).show();*/




        historiqueRecharge = (LinearLayout) findViewById(R.id.historiqueRecharge);
        historiqueRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "Manage_Recharge");
                startActivity(intent);
            }
        });

        historiqueTelecollecte = (LinearLayout) findViewById(R.id.historiqueTelecollecte);
        historiqueTelecollecte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "Telecollecte");
                startActivity(intent);
            }
        });

        historiqueDebit = (LinearLayout) findViewById(R.id.historiqueDebit);
        historiqueDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "debitcarte");
                startActivity(intent);
            }
        });

        historiqueTransfert = (LinearLayout) findViewById(R.id.historiqueTransfert);
        historiqueTransfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "transfert");
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


}
