package com.ezpass.smopaye_mobile.Manage_Settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Apropos.InformationLegales;
import com.ezpass.smopaye_mobile.BuildConfig;
import com.ezpass.smopaye_mobile.Manage_Offre_Smopaye.Demarrage;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.Methods;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;

import java.util.Calendar;
import java.util.Locale;

public class Setting extends AppCompatActivity {

    private ListView listAllSetting;
    private String [] config;
    private Dialog myDialog, myDialogTheme;
    private TextView txtclose, titleLanguage, myVersion, copy;
    private RadioButton radioFr, radioEn, radioDefaultTheme, radioRedTheme;
    private  Boolean checked;
    String currentLanguage = (Locale.getDefault().getLanguage().contentEquals("fr")) ? "fr" : "en", currentLang;

    //update account
    private String nom;
    private String prenom;
    private String numeroTel;
    private String cni;
    private String adresse;
    private String numero_card;
    private String idUser;


    private SharedPreferences sharedPreferences, app_preferences;
    private SharedPreferences.Editor editor;
    private Methods methods;
    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;
    private Toolbar toolbar;
    private String role;
    private String categorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_setting);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.config));
        toolbar.setSubtitle(getString(R.string.ezpass));
        toolbar.setLogo(R.mipmap.logo_official);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //toolbar.setBackgroundColor(Constant.color);
        methods = new Methods();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();



        Intent intent = getIntent();
        nom = intent.getStringExtra("nom");
        prenom = intent.getStringExtra("prenom");
        numeroTel = intent.getStringExtra("numeroTel");
        cni = intent.getStringExtra("cni");
        adresse = intent.getStringExtra("adresse");
        numero_card = intent.getStringExtra("numero_card");
        idUser = intent.getStringExtra("idUser");
        role = intent.getStringExtra("role");
        categorie = intent.getStringExtra("categorie");


        listAllSetting = (ListView)findViewById(R.id.listAllSetting);
        currentLanguage = getIntent().getStringExtra(currentLang);

        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            // Initializing a String Array
            config = new String[]{
                    "Rechercher les mises à jour",
                    "Mon Compte",
                    "Langue par défaut",
                    "Changer votre Thème",
                    "Informations légales",
                    "Fournir des commentaires",
                    "A propos de l'Application"
            };
        } else {
            // Initializing a String Array
            config = new String[]{
                    "Check for updates",
                    "My Account",
                    "Default language",
                    "Change your theme",
                    "Legal information",
                    "Provide feedback",
                    "About the App"
            };
        }


        // Initialize an array adapter
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, config){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position,convertView,parent);
                // Set the typeface/font for the current item
                //item.setTypeface(mTypeface);

                // Set the list view item's text color
                if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
                    item.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
                } else{
                    item.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                // return the view
                return item;
            }
        };
        // Data bind the list view with array adapter items
        listAllSetting.setAdapter(adapter);


        listAllSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    //rechercher les mises à jours
                    case 0:
                        try {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.ezpass.smopaye_mobile&hl=fr"));
                            startActivity(viewIntent);
                        }catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                         /*intent = new Intent(Intent.ACTION_VIEW);
                        Uri.Builder uriBuilder = Uri.parse("https://play.google.com/store/apps/details")
                                .buildUpon()
                                .appendQueryParameter("id", "com.ezpass.smopaye_mobile")
                                .appendQueryParameter("launch", "true");

                          // Optional parameters, such as referrer, are passed onto the launched
                         // instant app. You can retrieve these parameters using
                        // Activity.getIntent().getData().
                        uriBuilder.appendQueryParameter("referrer", "exampleCampaignId");

                        intent.setData(uriBuilder.build());
                        intent.setPackage("com.android.vending");
                        startActivity(intent);*/

                        break;
                    case 1:
                        //Mon Compte
                         intent = new Intent(Setting.this, MyAccount.class);
                        intent.putExtra("nom", nom);
                        intent.putExtra("prenom", prenom);
                        intent.putExtra("numeroTel", numeroTel);
                        intent.putExtra("cni", cni);
                        intent.putExtra("adresse", adresse);
                        intent.putExtra("numero_card", numero_card);
                        intent.putExtra("idUser", idUser);
                        intent.putExtra("role", role);
                        intent.putExtra("categorie", categorie);
                        startActivity(intent);
                        break;
                    case 2:
                        //Change language
                        myDialog = new Dialog(Setting.this);
                        myDialog.setContentView(R.layout.layout_dialog_change_language);

                        radioFr = (RadioButton) myDialog.findViewById(R.id.radioFr);
                        radioEn = (RadioButton) myDialog.findViewById(R.id.radioEn);
                        titleLanguage = (TextView) myDialog.findViewById(R.id.titleLanguage);

                        radioFr.setChecked(true);
                        radioFr.setText(getString(R.string.french));
                        radioEn.setText(getString(R.string.english));
                        titleLanguage.setText(getString(R.string.defaultLanguage));


                        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
                            radioFr.setChecked(true);
                        } else {
                            radioEn.setChecked(true);
                        }



                        radioFr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    setLocale("fr");
                                    myDialog.dismiss();
                                }

                            }
                        });
                        radioEn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    setLocale("en");
                                    myDialog.dismiss();
                                }
                            }
                        });

                        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
                            txtclose.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
                        }
                        txtclose.setText("X");
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                        break;
                    case 3:
                        //Change your thème
                        myDialogTheme = new Dialog(Setting.this);
                        myDialogTheme.setContentView(R.layout.layout_dialog_change_theme);

                        radioDefaultTheme = (RadioButton) myDialogTheme.findViewById(R.id.radioDefaultTheme);
                        radioRedTheme = (RadioButton) myDialogTheme.findViewById(R.id.radioRedTheme);

                        radioDefaultTheme.setChecked(true);



                        if(Constant.color != getResources().getColor(R.color.colorPrimaryRed)) {
                            radioDefaultTheme.setChecked(true);
                        } else {
                            radioRedTheme.setChecked(true);
                        }

                        radioDefaultTheme.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    Constant.color = getResources().getColor(R.color.colorPrimaryDark);

                                    methods.setColorTheme();
                                    editor.putInt("color", getResources().getColor(R.color.colorPrimaryDark));
                                    editor.putInt("theme",Constant.theme);
                                    editor.commit();

                                    Intent intent = new Intent(Setting.this, Demarrage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    myDialogTheme.dismiss();
                                }

                            }
                        });
                        radioRedTheme.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    Constant.color = getResources().getColor(R.color.colorPrimaryRed);

                                    methods.setColorTheme();
                                    editor.putInt("color", getResources().getColor(R.color.colorPrimaryRed));
                                    editor.putInt("theme",Constant.theme);
                                    editor.commit();

                                    Intent intent = new Intent(Setting.this, Demarrage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    myDialogTheme.dismiss();
                                }
                            }
                        });

                        txtclose =(TextView) myDialogTheme.findViewById(R.id.txtclose);
                        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
                            txtclose.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
                        }
                        txtclose.setText("X");
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialogTheme.dismiss();
                            }
                        });
                        myDialogTheme.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialogTheme.show();
                        break;
                    case 4:
                        //Information légale
                        intent = new Intent(Setting.this, InformationLegales.class);
                        startActivity(intent);
                        break;
                    case 5:
                        //Information légale
                        intent = new Intent(Setting.this, SendEmail.class);
                        startActivity(intent);
                        break;

                    case 6:
                        //Apropos Application
                        myDialog = new Dialog(Setting.this);
                        myDialog.setContentView(R.layout.layout_dialog_apropos_ezpass);
                        myVersion = (TextView) myDialog.findViewById(R.id.myVersion);
                        copy = (TextView) myDialog.findViewById(R.id.copy);
                        myVersion.setText("Version " + BuildConfig.VERSION_NAME);
                        copy.setText("© " + Calendar.getInstance().get(Calendar.YEAR) + " E-ZPASS by SMOPAYE");

                        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
                            txtclose.setBackgroundColor(getResources().getColor(R.color.colorPrimaryRed));
                        }
                        txtclose.setText("X");
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                        break;

                }

                /*if (listAllSetting.getItemAtPosition(position).toString().equalsIgnoreCase("Informations légales")){
                    Intent intent = new Intent(Manage_Settings.this, InformationLegales.class);
                    startActivity(intent);
                }
                else if (listView.getItemAtPosition(position).toString().equalsIgnoreCase("Logiciels tiers")){
                    Intent intent = new Intent(Apropos.this, LogicielsTiers.class);
                    startActivity(intent);
                }*/

                /*else if (listView.getItemAtPosition(position).toString().equalsIgnoreCase("Badge de confiance")){
                    Intent intent = new Intent(this, AideAdmin.class);
                    startActivity(intent);
                }*/


            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
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

    private void setLocale(String localeName) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", localeName);
        editor.apply();

        if (!localeName.equals(currentLanguage)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Locale locale = new Locale(localeName);
                Locale.setDefault(locale);

                Configuration configuration = this.getResources().getConfiguration();
                configuration.setLocale(locale);
                configuration.setLayoutDirection(locale);
                createConfigurationContext(configuration);
            } else {

                Locale locale = new Locale(localeName);
                Locale.setDefault(locale);

                Resources resources = getResources();

                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    configuration.setLayoutDirection(locale);
                }

                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            }

            Intent refresh = new Intent(this, Demarrage.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
            finish();

        } else{
            Toast.makeText(Setting.this, getString(R.string.selectedLanguage), Toast.LENGTH_SHORT).show();
        }



        /*if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, Login.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(Login.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }*/
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
}
