package com.ezpass.smopaye_mobile.Setting;

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
import android.support.v7.app.AppCompatActivity;
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

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.Apropos.InformationLegales;
import com.ezpass.smopaye_mobile.BuildConfig;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

import java.util.Locale;

public class Setting extends AppCompatActivity {

    private ListView listAllSetting;
    private String [] config;
    private Dialog myDialog;
    private TextView txtclose, titleLanguage, myVersion;
    private RadioButton radioFr, radioEn;
    private  Boolean checked;
    String currentLanguage = (Locale.getDefault().getLanguage().contentEquals("fr")) ? "fr" : "en", currentLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /*Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setTitle(getString(R.string.config));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        /*getSupportActionBar().setTitle(getString(R.string.config));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/


        listAllSetting = (ListView)findViewById(R.id.listAllSetting);
        currentLanguage = getIntent().getStringExtra(currentLang);

        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            // Initializing a String Array
            config = new String[]{
                    "Rechercher les mises à jour",
                    "Mon Compte",
                    "Langue par défaut",
                    "Informations légales",
                    "Fournir des commentaires",
                    "Contactez nous",
                    "A propos de l'Application"
            };
        } else {
            // Initializing a String Array
            config = new String[]{
                    "Research update",
                    "My Account",
                    "Default language",
                    "Legale informations",
                    "give comments",
                    "Contact-us",
                    "Apropos Application"
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
                item.setTextColor(Color.parseColor("#039BE5"));

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
                       /* try {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.ezpass.smopaye_mobile&hl=fr"));
                            startActivity(viewIntent);
                        }catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }*/

                         intent = new Intent(Intent.ACTION_VIEW);
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
                        startActivity(intent);

                        break;
                    case 1:
                        //Mon Compte
                         intent = new Intent(Setting.this, MyAccount.class);
                        startActivity(intent);
                        break;
                    case 2:
                        //Change language
                        myDialog = new Dialog(Setting.this);
                        myDialog.setContentView(R.layout.layout_dialog_change_language);

                        radioFr = (RadioButton) myDialog.findViewById(R.id.radioFr);
                        radioEn = (RadioButton) myDialog.findViewById(R.id.radioEn);
                        titleLanguage = (TextView) myDialog.findViewById(R.id.titleLanguage);

                        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
                            radioFr.setChecked(true);
                            radioFr.setText("Français");
                            radioEn.setText("Anglais");
                            titleLanguage.setText("Langue par défaut");
                        } else {
                            radioEn.setChecked(true);
                            radioFr.setText("French");
                            radioEn.setText("English");
                            titleLanguage.setText("Default Language");
                        }

                        radioFr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    setLocale("fr");
                                }

                            }
                        });
                        radioEn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    setLocale("en");
                                }
                            }
                        });

                        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
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
                        //Information légale
                        intent = new Intent(Setting.this, InformationLegales.class);
                        startActivity(intent);
                        break;
                    case 4:
                        //Information légale
                        intent = new Intent(Setting.this, SendEmail.class);
                        startActivity(intent);
                        break;

                    case 6:
                        //Apropos Application
                        myDialog = new Dialog(Setting.this);
                        myDialog.setContentView(R.layout.layout_dialog_apropos_ezpass);
                        myVersion = (TextView) myDialog.findViewById(R.id.myVersion);
                        myVersion.setText("Version " + BuildConfig.VERSION_NAME);

                        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
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
                    Intent intent = new Intent(Setting.this, InformationLegales.class);
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

            Intent refresh = new Intent(this, Setting.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);

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
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            startActivity(intent);
        }

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
