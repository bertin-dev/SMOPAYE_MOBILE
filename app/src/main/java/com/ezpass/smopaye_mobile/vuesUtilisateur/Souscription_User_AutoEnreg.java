package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.NotifApp;
import com.ezpass.smopaye_mobile.Profil_user.Categorie;
import com.ezpass.smopaye_mobile.Profil_user.Role;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.telpo.tps550.api.nfc.Nfc;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Souscription_User_AutoEnreg extends AppCompatActivity
                                         implements ConnectivityReceiver.ConnectivityReceiverListener{


    private static final String TAG = "Souscription_User_AutoE";
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;
    /////////////////////////////////////////////////////////////////////////////////
    private Handler handler;
    private Runnable runnable;
    private Timer timer;
    private Thread readThread;
    private byte blockNum_1 = 1;
    private byte blockNum_2 = 2;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    long time1, time2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    private Nfc nfc = new Nfc(this);


    private String[] sexe1;
    private String[] pieceJ;
    private String num_statut = "";
    private String num_categorie = "";

    private HashMap<String, String> listAllSession = new HashMap<>();
    private HashMap<String, String> listAllCategorie = new HashMap<>();
    private HashMap<String, String> listFILTRECategorie = new HashMap<>();

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private Call<List<Categorie>> call;
    private AwesomeValidation validator;
    private TokenManager tokenManager;

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    private String file = "tmp_number";
    private  int c;
    private String temp_number = "";

    //recuperation des vues
    @BindView(R.id.til_nom)
    TextInputLayout til_nom;
    @BindView(R.id.til_prenom)
    TextInputLayout til_prenom;
    @BindView(R.id.til_numeroTel)
    TextInputLayout til_numeroTel;
    @BindView(R.id.til_cni)
    TextInputLayout til_cni;
    @BindView(R.id.tie_cni)
    TextInputEditText tie_cni;
    @BindView(R.id.til_adresse)
    TextInputLayout til_adresse;

    @BindView(R.id.authWindows)
    LinearLayout authWindows;
    @BindView(R.id.internetIndisponible)
    LinearLayout internetIndisponible;
    @BindView(R.id.conStatusIv)
    ImageView conStatusIv;
    @BindView(R.id.titleNetworkLimited)
    TextView titleNetworkLimited;
    @BindView(R.id.msgNetworkLimited)
    TextView msgNetworkLimited;

    @BindView(R.id.typePjustificative)
    Spinner typePjustificative;
    @BindView(R.id.sexe)
    Spinner sexe;
    @BindView(R.id.statut)
    Spinner statut;
    @BindView(R.id.typeChauffeur)
    Spinner typeChauffeur;


    @Override
    protected void onStart() {
        super.onStart();
        //Check si la connexion existe
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if(!(activeInfo != null && activeInfo.isConnected())){
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        //verification si on a pas le token dans les sharepreferences alors on retourne vers le login activity
        if(tokenManager.getToken() == null){
            startActivity(new Intent(Souscription_User_AutoEnreg.this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call = service.getAllCategories();
        call.enqueue(new Callback<List<Categorie>>() {
            @Override
            public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse " +response);

                if(response.isSuccessful()){

                    listAllSession.clear();
                    listAllCategorie.clear();

                    List<Categorie> mycategories = response.body();
                    for(int i = 0; i<mycategories.size(); i++){

                        Role myRole = mycategories.get(i).getRole();

                        listAllSession.put(myRole.getId(),  myRole.getname());
                        listAllCategorie.put(mycategories.get(i).getId(), mycategories.get(i).getname());
                    }
                    List<StringWithTag> itemList = new ArrayList<>();
                    /* Iterate through your original collection, in this case defined with an Integer key and String value. */
                    for (Map.Entry<String, String> entry : listAllSession.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        /* Build the StringWithTag List using these keys and values. */
                        itemList.add(new StringWithTag(value, key));
                    }
                    /* Set your ArrayAdapter with the StringWithTag, and when each entry is shown in the Spinner, .toString() is called. */
                    ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(Souscription_User_AutoEnreg.this, R.layout.spinner_item, itemList);
                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
                    statut.setAdapter(spinnerAdapter);
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(Souscription_User_AutoEnreg.this, Login.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Categorie>> call, Throwable t) {
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                //Vérification si la connexion internet accessible
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                //Vérification si le serveur est inaccessible
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    /**
     * @author bertin mounok
     * @powered by smopaye sarl
     * @Copyright 2019-2020
     * @param savedInstanceState Callback method onCreate() she using for the started activity
     * @since 2019
     * @version 1.2.7
     * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souscription_users_autoenreg);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.Souscription_User_AutoEnregEtape1));
        toolbar.setSubtitle(getString(R.string.fillForm));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //récupération des vues en lien dans notre activity
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        progressDialog = new ProgressDialog(Souscription_User_AutoEnreg.this);
        build_error = new AlertDialog.Builder(Souscription_User_AutoEnreg.this);

        setupRulesValidatForm();


        // Initializing a String Array
        //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            // Initializing a String Array
            sexe1 = new String[]{
                    "Masculin",
                    "Feminin"
            };
            // Initializing a String Array
            pieceJ = new String[]{
                    "CNI",
                    "passeport",
                    "recipissé",
                    "carte de séjour",
                    "carte d'étudiant"
            };
        } else {
            // Initializing a String Array
            sexe1 = new String[]{
                    "Male",
                    "Feminine"
            };
            // Initializing a String Array
            pieceJ = new String[]{
                    "CNI",
                    "passport",
                    "receipt",
                    "residence permit",
                    "student card"
            };
        }

        // Initializing an ArrayAdapter gender
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                this, R.layout.spinner_item, sexe1);
        spinnerArrayAdapter3.setDropDownViewResource(R.layout.spinner_item);
        sexe.setAdapter(spinnerArrayAdapter3);


        // Initializing an ArrayAdapter justificatives
        ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<String>(
                this, R.layout.spinner_item, pieceJ);
        spinnerArrayAdapter4.setDropDownViewResource(R.layout.spinner_item);
        typePjustificative.setAdapter(spinnerArrayAdapter4);


        readTempNumberInFile();



        typePjustificative.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            tie_cni.setHint("N° CNI");
                        } else {
                            tie_cni.setHint("N° CNI");
                        }
                        break;
                    case 1:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            tie_cni.setHint("N° Passeport");
                        } else {
                            tie_cni.setHint("N° Passport");
                        }
                        break;
                    case 2:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            tie_cni.setHint("N° Recipissé");
                        } else {
                            tie_cni.setHint("N° Receipt");
                        }
                        break;
                    case 3:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            tie_cni.setHint("N° Carte de séjour");
                        } else {
                            tie_cni.setHint("N° Residence permit");
                        }
                        break;
                    case 4:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            tie_cni.setHint("N° Carte d'étudiant");
                        } else {
                            tie_cni.setHint("N° Student card");
                        }
                        break;
                    default:
                        tie_cni.setHint("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        statut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StringWithTag swt = (StringWithTag) parent.getItemAtPosition(position);
                String key = (String) swt.tag;
                //Toast.makeText(Souscription.this, listAllSession.get(key), Toast.LENGTH_SHORT).show();
                //Toast.makeText(Souscription.this, String.valueOf(key), Toast.LENGTH_SHORT).show();

                if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase(listAllSession.get(key))){
                    num_statut = String.valueOf(key);

                    call = service.getAllCategories();
                    call.enqueue(new Callback<List<Categorie>>() {
                        @Override
                        public void onResponse(Call<List<Categorie>> call, Response<List<Categorie>> response) {

                            Log.w(TAG, "SMOPAYE_SERVER onResponse " +response);
                            if(response.isSuccessful()){

                                listFILTRECategorie.clear();

                                List<Categorie> mycategories = response.body();
                                for(int i = 0; i<mycategories.size(); i++){

                                    if(num_statut.equals(mycategories.get(i).getRole_id())){
                                        listFILTRECategorie.put(mycategories.get(i).getId(), mycategories.get(i).getname());
                                    }

                                }

                                //------------------------------

                                List<StringWithTag> itemList1 = new ArrayList<>();

                                /* Iterate through your original collection, in this case defined with an Integer key and String value. */
                                for (Map.Entry<String, String> entry : listFILTRECategorie.entrySet()) {
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    /* Build the StringWithTag List using these keys and values. */

                                    itemList1.add(new StringWithTag(value, key));
                                }

                                /* Set your ArrayAdapter with the StringWithTag, and when each entry is shown in the Spinner, .toString() is called. */
                                ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(Souscription_User_AutoEnreg.this, R.layout.spinner_item, itemList1);
                                spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
                                typeChauffeur.setAdapter(spinnerAdapter);
                                //------------------------------


                            }
                            else{
                                tokenManager.deleteToken();
                                startActivity(new Intent(Souscription_User_AutoEnreg.this, Login.class));
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Categorie>> call, Throwable t) {

                            Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                            //Vérification si la connexion internet accessible
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                            if(!(activeInfo != null && activeInfo.isConnected())){
                                authWindows.setVisibility(View.GONE);
                                internetIndisponible.setVisibility(View.VISIBLE);
                                Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                            }
                            //Vérification si le serveur est inaccessible
                            else{
                                authWindows.setVisibility(View.GONE);
                                internetIndisponible.setVisibility(View.VISIBLE);
                                conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                //msgNetworkLimited.setText();
                                Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        typeChauffeur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StringWithTag swt = (StringWithTag) parent.getItemAtPosition(position);
                String key = (String) swt.tag;
                //Toast.makeText(Souscription.this, listAllCategorie.get(key), Toast.LENGTH_SHORT).show();
                //Toast.makeText(Souscription.this, String.valueOf(key), Toast.LENGTH_SHORT).show();

                if(typeChauffeur.getSelectedItem().toString().toLowerCase().equalsIgnoreCase(listAllCategorie.get(key))){
                    num_categorie = String.valueOf(key);
                    //Toast.makeText(Souscription.this, num_categorie, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @OnClick(R.id.btnSuivant)
    void auto_register(){

        if(!validateNom(til_nom) | !validatePrenom(til_prenom) | !validateTel(til_numeroTel) | !validateCni(til_cni)
                | !validateAdress(til_adresse) | !validateRole(statut) | !validateCategorie(typeChauffeur)){
            return;
        }

        validator.clear();

        /*Action à poursuivre si tous les champs sont remplis*/
        if(validator.validate()){


            Intent intent = new Intent(getApplicationContext(), SouscriptionUploadIMGidCard.class);
            intent.putExtra("NOM", til_nom.getEditText().getText().toString().trim().toLowerCase());
            intent.putExtra("PRENOM", til_prenom.getEditText().getText().toString().trim().toLowerCase());
            intent.putExtra("GENRE", (sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("masculin") || sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("male") ? "MASCULIN" : "FEMININ" ));
            intent.putExtra("TELEPHONE", til_numeroTel.getEditText().getText().toString().trim());
            intent.putExtra("CNI", typePjustificative.getSelectedItem().toString().trim()+"-"+til_cni.getEditText().getText().toString().trim());
            intent.putExtra("sessioncompte", num_statut);
            intent.putExtra("Adresse", til_adresse.getEditText().getText().toString().trim());
            intent.putExtra("IDCathegorie", num_categorie);
            intent.putExtra("uniquser", temp_number);
            intent.putExtra("sessioncompteValue", statut.getSelectedItem().toString().trim());
            intent.putExtra("IDCathegorieValue", typeChauffeur.getSelectedItem().toString().trim());
            startActivity(intent);

        }


    }

    /**
     * validateNom() méthode permettant de verifier si le nom inséré est valide
     * @param til_nom
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateNom(TextInputLayout til_nom){
        String my_name = til_nom.getEditText().getText().toString().trim();
        if(my_name.isEmpty()){
            til_nom.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.nom));
            return false;
        } else if(!isValid(my_name)){
            til_nom.setError(getString(R.string.votre) + " " + getString(R.string.nom) + " " + getString(R.string.invalidCararatere));
            return false;
        } else {
            til_nom.setError(null);
            return true;
        }
    }


    /**
     * validateStatut() méthode permettant de verifier si le statut listé est chargé
     * @param status
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateRole(Spinner status){

        if(status.getCount() == 0){
            Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertStatutListDeroulante), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * validateCat() méthode permettant de verifier si la categorie listé est bien chargé
     * @param cat
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateCategorie(Spinner cat){

        if(cat.getCount() == 0){
            Toast.makeText(Souscription_User_AutoEnreg.this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertCategorieListDeroulante), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * validatePrenom() méthode permettant de verifier si le prenom inséré est valide
     * @param til_prenom
     * @return Boolean
     * @since 2019
     * */
    private Boolean validatePrenom(TextInputLayout til_prenom){
        String my_surname = til_prenom.getEditText().getText().toString().trim();
        if(my_surname.isEmpty()){
            til_prenom.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.prenom));
            return false;
        } else if(!isValid(my_surname)){
            til_prenom.setError(getString(R.string.votre) + " " + getString(R.string.prenom) + " " + getString(R.string.invalidCararatere));
            return false;
        } else {
            til_prenom.setError(null);
            return true;
        }
    }


    /**
     * validateCni() méthode permettant de verifier si le cni inséré est valide
     * @param til_cni
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateCni(TextInputLayout til_cni){
        String my_cni = til_cni.getEditText().getText().toString().trim();
        if(my_cni.isEmpty()){
            til_cni.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.numeroDe) + " " + typePjustificative.getSelectedItem().toString());
            return false;
        } else if(!isValid(my_cni)){
            til_cni.setError(getString(R.string.votre) + " " + typePjustificative.getSelectedItem().toString() + " " + getString(R.string.invalidCararatere));
            return false;
        } else {
            til_cni.setError(null);
            return true;
        }
    }


    /**
     * validateAdress() méthode permettant de verifier si le cni inséré est valide
     * @param til_adress
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateAdress(TextInputLayout til_adress){
        String my_adress = til_adress.getEditText().getText().toString().trim();
        if(my_adress.isEmpty()){
            til_adress.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.adresse));
            return false;
        } else if(!isValid(my_adress)){
            til_adress.setError(getString(R.string.votre) + " " + getString(R.string.adresse) + " " + getString(R.string.invalidCararatere));
            return false;
        } else {
            til_adress.setError(null);
            return true;
        }
    }


    /**
     * validateTel() méthode permettant de verifier si le telephone inséré est valide
     * @param til_tel
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateTel(TextInputLayout til_tel){
        String my_phone = til_tel.getEditText().getText().toString().trim();
        if(my_phone.isEmpty()){
            til_tel.setError(getString(R.string.insererTelephone));
            return false;
        } else if(my_phone.length() < 9){
            til_tel.setError(getString(R.string.telephoneCourt));
            return false;
        } else {
            til_tel.setError(null);
            return true;
        }
    }

    private void readTempNumberInFile() {
        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getApplicationContext().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     * @since 2020
     * */
    private void setupRulesValidatForm(){

        //coloration des champs lorsqu'il y a erreur
        til_nom.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_prenom.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_numeroTel.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_cni.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_adresse.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));

        validator.addValidation(this, R.id.til_nom, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererNom);
        validator.addValidation(this, R.id.til_prenom, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererPrenom);
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererTelephone);
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.TELEPHONE, R.string.verifierNumero);
        validator.addValidation(this, R.id.til_cni, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererPJ);
        validator.addValidation(this, R.id.til_adresse, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererAdresse);
    }


    /**
     * checkNetworkConnectionStatus() méthode permettant de verifier si la connexion existe ou si le serveur est accessible
     * @since 2019
     * */
    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus(){
        boolean isConnected = ConnectivityReceiver.isConnected();

        showSnackBar(isConnected);

       // if(!isConnected){ changeActivity(); }
        if(isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {
        /*Intent intent = new Intent(this, OfflineActivity.class);
        startActivity(intent);*/
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if(activeInfo != null && activeInfo.isConnected()){
            progressDialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
            progressDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    recreate();
                }
            }, 2000); // 2000 milliseconds delay

        } else{
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.auto_souscription), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else{
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(findViewById(R.id.auto_souscription), message, Snackbar.LENGTH_INDEFINITE);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        }
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        /*if(!isConnected){
            changeActivity();
        }*/
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        //register connection status listener
        NotifApp.getInstance().setConnectivityListener(this);
    }



    /**
     * onDestroy() methode Callback qui permet de détruire une activity et libérer l'espace mémoire
     * @since 2020
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }



    private static boolean isValid(String str)
    {
        boolean isValid = false;
        String expression = "^[a-z_A-Z0-9éèê'çà ]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if(id == R.id.modifierCompte){
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("telephone", temp_number);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private static class StringWithTag {
        public String string;
        public Object tag;

        private StringWithTag(String string, Object tag) {
            this.string = string;
            this.tag = tag;
        }

        @Override
        public String toString() {
            return string;
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
