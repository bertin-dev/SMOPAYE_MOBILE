package com.ezpass.smopaye_mobile.Manage_Register;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.NotifApp;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.Profil_user.Categorie;
import com.ezpass.smopaye_mobile.Profil_user.Role;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode.QRCodeShow;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.AccessToken;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpass.smopaye_mobile.ChaineConnexion.getsecurity_keys;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class Souscription extends AppCompatActivity
                          implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "Souscription";
    private String abonnement = "service";
    private ProgressDialog progressDialog, progressDialogGoogle;
    private AlertDialog.Builder build_error;

    private String[] sexe1;
    private String[] pieceJ;

    private String num_statut = "";
    private String num_categorie = "";

    private HashMap<String, String> listAllSession = new HashMap<>();
    private HashMap<String, String> listAllCategorie = new HashMap<>();
    private HashMap<String, String> listFILTRECategorie = new HashMap<>();

    /*Déclaration des objets qui permettent d'écrire sur le fichier*/
    private String tmp_number = "tmp_number";
    private int c;
    private String temp_number = "";


    //BD LOCALE
    private DbHandler dbHandler;
    private DbUser dbUser;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    //SERVICES GOOGLE FIREBASE
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private APIService apiService;
    private FirebaseUser fuser;


    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private Call<AccessToken> call;
    private Call<List<Categorie>> call2;
    private AwesomeValidation validator;
    private TokenManager tokenManager;



    /////////////////////////////////////////////////////////////////////////////////
    private Handler handler;
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



    //recuperation des vues
    @BindView(R.id.til_nom)
    TextInputLayout til_nom;
    @BindView(R.id.til_prenom)
    TextInputLayout til_prenom;
    @BindView(R.id.til_numeroTel)
    TextInputLayout til_numeroTel;
    @BindView(R.id.til_cni)
    TextInputLayout til_cni;
    @BindView(R.id.til_adresse)
    TextInputLayout til_adresse;
    @BindView(R.id.til_numCarte)
    TextInputLayout til_numCarte;
    @BindView(R.id.tie_numCarte)
    TextInputEditText tie_numCarte;
    @BindView(R.id.tie_cni)
    TextInputEditText tie_cni;

    @BindView(R.id.AbonnementMensuel)
    CheckBox AbonnementMensuel;
    @BindView(R.id.AbonnementHebdomadaire)
    CheckBox AbonnementHebdomadaire;
    @BindView(R.id.AbonnementService)
    CheckBox AbonnementService;
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
            Toast.makeText(Souscription.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        //verification si on a pas le token dans les sharepreferences alors on retourne vers le login activity
        if(tokenManager.getToken() == null){
            startActivity(new Intent(Souscription.this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        call2 = service.getAllCategories();
        call2.enqueue(new Callback<List<Categorie>>() {
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
                    ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(Souscription.this, R.layout.spinner_item, itemList);
                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
                    statut.setAdapter(spinnerAdapter);
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(Souscription.this, Login.class));
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
                    Toast.makeText(Souscription.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                //Vérification si le serveur est inaccessible
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(Souscription.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_souscription);

        /*Mise de la barre des titre dans la fenêtre souscription*/
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.souscription));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //récupération des vues en lien dans notre activity
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        progressDialog = new ProgressDialog(Souscription.this);
        build_error = new AlertDialog.Builder(Souscription.this);

        setupRulesValidatForm();
        callHandlerMethod();

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

        //selection des roles(utilisateur, accepteur, administrateur, agent)
        statut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StringWithTag swt = (StringWithTag) parent.getItemAtPosition(position);
                String key = (String) swt.tag;

                if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase(listAllSession.get(key))){
                    num_statut = String.valueOf(key);
                    call2 = service.getAllCategories();
                    call2.enqueue(new Callback<List<Categorie>>() {
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
                                ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(Souscription.this, R.layout.spinner_item, itemList1);
                                spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
                                typeChauffeur.setAdapter(spinnerAdapter);
                                //------------------------------


                            }
                            else{
                                tokenManager.deleteToken();
                                startActivity(new Intent(Souscription.this, Login.class));
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
                                Toast.makeText(Souscription.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                            }
                            //Vérification si le serveur est inaccessible
                            else{
                                authWindows.setVisibility(View.GONE);
                                internetIndisponible.setVisibility(View.VISIBLE);
                                conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                //msgNetworkLimited.setText();
                                Toast.makeText(Souscription.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
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

                if(typeChauffeur.getSelectedItem().toString().toLowerCase().equalsIgnoreCase(listAllCategorie.get(key))){
                    num_categorie = String.valueOf(key);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void readTempNumberInFile() {
        try{
            FileInputStream fInTel = openFileInput(tmp_number);
            while ((c = fInTel.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
        }
    }


    private void callHandlerMethod() {

        //DETECTION DE TYPE DE CARTE ET SON ID
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHECK_NFC_TIMEOUT: {
                        Toast.makeText(getApplicationContext(), "Check card time out!", Toast.LENGTH_LONG).show();
                       /* open_btn.setEnabled(true);
                        close_btn.setEnabled(false);
                        check_btn.setEnabled(false);*/
                    }
                    break;
                    case SHOW_NFC_DATA: {
                        byte[] uid_data = (byte[]) msg.obj;
                        if (uid_data[0] == 0x42) {
                            // TYPE B类（暂时只支持cpu卡）
                            byte[] atqb = new byte[uid_data[16]];
                            byte[] pupi = new byte[4];
                            String type = null;

                            System.arraycopy(uid_data, 17, atqb, 0, uid_data[16]);
                            System.arraycopy(uid_data, 29, pupi, 0, 4);

                            if (uid_data[1] == B_CPU) {
                                type = "CPU";
                               /* sendApduBtn.setEnabled(true);
                                getAtsBtn.setEnabled(true);*/
                            } else {
                                type = "unknow";
                            }

                            new AlertDialog.Builder(Souscription.this)
                                    .setMessage(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                            "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                            "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();

                           /* uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                    "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                    "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi));*/

                        } else if (uid_data[0] == 0x41) {
                            // TYPE A类（CPU, M1）
                            byte[] atqa = new byte[2];
                            byte[] sak = new byte[1];
                            byte[] uid = new byte[uid_data[5]];
                            String type = null;

                            System.arraycopy(uid_data, 2, atqa, 0, 2);
                            System.arraycopy(uid_data, 4, sak, 0, 1);
                            System.arraycopy(uid_data, 6, uid, 0, uid_data[5]);

                            if (uid_data[1] == A_CPU) {
                                type = "CPU";
                                /*sendApduBtn.setEnabled(true);
                                getAtsBtn.setEnabled(true);*/
                            } else if (uid_data[1] == A_M1) {
                                type = "M1";
                                // authenticateBtn.setEnabled(true);
                            } else {
                                type = "unknow";
                            }
                           /* new AlertDialog.Builder(Login.this)
                                    .setMessage(getString(R.string.card_type) + getString(R.string.type_a) + " " + type +
                                            "\r\n" + getString(R.string.atqa_data) + StringUtil.toHexString(atqa) +
                                            "\r\n" + getString(R.string.sak_data) + StringUtil.toHexString(sak) +
                                            "\r\n" + getString(R.string.uid_data) + StringUtil.toHexString(uid))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();*/
                            //numCarte.setText(StringUtil.toHexString(uid));
                            m1CardAuthenticate();
                            progressDialog.dismiss();
                            try {
                                nfc.close();
                            } catch (TelpoException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.e(TAG, "unknow type card!!");
                        }
                    }
                    break;

                    default:
                        break;
                }
            }
        };
    }


    /**
     * checkNetworkConnectionStatus() méthode permettant de verifier si la connexion existe ou si le serveur est accessible
     * @since 2019
     * */
    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus(){
        boolean isConnected = ConnectivityReceiver.isConnected();

        showSnackBar(isConnected);

        if(isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {
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
            Toast.makeText(Souscription.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.souscription), message, Snackbar.LENGTH_LONG);
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
            snackbar = Snackbar.make(findViewById(R.id.souscription), message, Snackbar.LENGTH_INDEFINITE);
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

        if(call2 != null){
            call2.cancel();
            call2 = null;
        }
    }


    /**
     * openNfc() méthode permettant d'activer la communication avec les supports NFC
     * @since 2019
     * */
    @OnClick(R.id.btnOpenNFC)
    void openNfc(){
        try {
            nfc.open();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // On ajoute un message à notre progress dialog
                    progressDialog.setMessage(getString(R.string.passerCarte));
                    // On donne un titre à notre progress dialog
                    progressDialog.setTitle(getString(R.string.attenteCarte));
                    // On spécifie le style
                    //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    // On affiche notre message
                    progressDialog.show();
                    //build.setPositiveButton("ok", new View.OnClickListener()
                }
            });
        } catch (TelpoException e) {
            e.printStackTrace();
        }
        readThread = new ReadThread();
        readThread.start();
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
        til_numCarte.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));

        validator.addValidation(this, R.id.til_nom, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererNom);
        validator.addValidation(this, R.id.til_prenom, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererPrenom);
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererTelephone);
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.TELEPHONE, R.string.verifierNumero);
        validator.addValidation(this, R.id.til_cni, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererPJ);
        validator.addValidation(this, R.id.til_adresse, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererAdresse);
        validator.addValidation(this, R.id.til_numCarte, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererNumCompte);

        //validator.addValidation(this, R.id.til_numCarte, Patterns.PHONE, R.string.verifierNumero);
        //validator.addValidation(this, R.id.til_numCarte, "[a-zA-Z0-9]{6,0}", R.string.verifierNumero);
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
            Intent intent = new Intent(getApplicationContext(), UpdatePassword.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.putExtra("telephone", temp_number);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }



    //gestion des abonnements
    public void onCheckboxClicked1(View view) {
        // Is the view now checked?
        final boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.AbonnementMensuel:
                if (checked)
                {
                    Toast.makeText(this, AbonnementMensuel.getText().toString(), Toast.LENGTH_SHORT).show();
                    AbonnementHebdomadaire.setChecked(false);
                    AbonnementService.setChecked(false);
                    //AbonnementMensuel.setBackgroundColor(Color.parseColor("#039BE5"));
                    abonnement = "mensuel";
                }
                else{
                    AbonnementMensuel.setChecked(true);
                    AbonnementHebdomadaire.setChecked(false);
                    AbonnementService.setChecked(false);
                    abonnement = "mensuel";
                }
                break;
            case R.id.AbonnementHebdomadaire:
                if (checked)
                {
                    Toast.makeText(this, AbonnementHebdomadaire.getText().toString(), Toast.LENGTH_SHORT).show();
                    AbonnementMensuel.setChecked(false);
                    AbonnementService.setChecked(false);
                    abonnement = "hebdomadaire";
                }
                else{
                    AbonnementHebdomadaire.setChecked(true);
                    AbonnementMensuel.setChecked(false);
                    AbonnementService.setChecked(false);
                    abonnement = "hebdomadaire";
                }
                break;
            case R.id.AbonnementService:
                if(checked){
                    Toast.makeText(this, AbonnementService.getText().toString(), Toast.LENGTH_SHORT).show();
                    AbonnementMensuel.setChecked(false);
                    AbonnementHebdomadaire.setChecked(false);
                    abonnement = "service";
                } else{
                    AbonnementService.setChecked(true);
                    AbonnementMensuel.setChecked(false);
                    AbonnementService.setChecked(false);
                    abonnement = "service";
                }
        }
    }


    @OnClick(R.id.btnSouscription)
    void register(){

        if(!validateNom(til_nom) | !validatePrenom(til_prenom) | !validateTel(til_numeroTel) | !validateCni(til_cni)
                | !validateAdress(til_adresse) | !validateRole(statut) | !validateCategorie(typeChauffeur) | !validateCompte(til_numCarte)){
            return;
        }

        String nom = til_nom.getEditText().getText().toString();
        String prenom = til_prenom.getEditText().getText().toString();
        String telephone = til_numeroTel.getEditText().getText().toString();
        String cni = til_cni.getEditText().getText().toString();
        String adresse = til_adresse.getEditText().getText().toString();
        String num_carte = til_numCarte.getEditText().getText().toString();


       validator.clear();

       if(validator.validate()){

           //********************DEBUT***********
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   progressDialog.setMessage(getString(R.string.connexionServeurSmopaye));
                   progressDialog.setTitle(getString(R.string.etape1EnvoiDesDonnees));
                   progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                   progressDialog.show();
               }
           });
           //*******************FIN*****

          String genre = sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("masculin") || sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("male") ? "MASCULIN" : "FEMININ";

           registerDataInSmopayeServer(nom, prenom, genre, telephone, typePjustificative.getSelectedItem().toString().trim(),
                   cni, "", adresse, num_carte, "",
                   "offline", abonnement);
       }

    }


    /**
     * validateCompte() méthode permettant de verifier si le numéro de compte inséré est valide
     * @param til_num_compte1
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateCompte(TextInputLayout til_num_compte1){
        String my_numCompte = til_num_compte1.getEditText().getText().toString().trim();
        if(my_numCompte.isEmpty()){
            til_num_compte1.setError(getString(R.string.insererCompte));
            return false;
        } else if(my_numCompte.length() < 8){
            til_num_compte1.setError(getString(R.string.compteCourt));
            return false;
        } else {
            til_num_compte1.setError(null);
            return true;
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
            Toast.makeText(this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertStatutListDeroulante), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertCategorieListDeroulante), Toast.LENGTH_SHORT).show();
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

    /**
     * @param nom1 soumission du numéro du nom pour enregistrement
     * @param prenom1 soumission du prenom pour enregistrement
     * @param sexe1 soumission du sexe pour enregistrement
     * @param telephone1 soumission du telephone pour enregistrement
     * @param typePJ1 soumission du type de piece justificatif pour enregistrement
     * @param cni1 soumission du numero de piece justificative pour enregistrement
     * @param session1 soumission de la session pour enregistrement
     * @param adresse1 soumission de l'adresse pour enregistrement
     * @param num_carte1 soumission du numéro de compte de l'utilisateur pour enregistrement
     * @param typeUser1 soumission du type d'utilisateur pour enregistrement
     * @param status1 soumission du status de l'utilisateur pour enregistrement
     * @param abonnement1 soumission du type d'abonement chois pour enregistrement
     * @throws t
     * @since 2020
     * */

    //ETAPE 1: envoi des données vers le serveur smopaye
    private void registerDataInSmopayeServer(String nom1, String prenom1, String sexe1, String telephone1, String typePJ1,
                                             String cni1, String session1, String adresse1, String num_carte1, String typeUser1,
                                             String status1, String abonnement1) {


        call = service.register(nom1, prenom1, sexe1.toLowerCase(), telephone1, adresse1, num_categorie, num_statut, cni1.toLowerCase(), num_carte1);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                progressDialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onResponse: " + response);


                if(response.isSuccessful()){
                    Log.w(TAG, "SMOPAYE_SERVER SUCCESS onResponse: " + response.body() );
                          /*sauvegarde du token retourné par l'api si la reponse est bonne
                         nous utiliserons la classe TokenManager pour cela
                        */
                    assert response.body() != null;
                    tokenManager.saveToken(response.body());
                        /*new AsyncTaskRegisterDataInGoogleFirebaseServer(nom1, prenom1, sexe1, telephone1, typePJ1,
                                cni1, session1, adresse1, num_carte1, typeUser1,
                                status1, abonnement1, response.toString()).execute();*/
                    Toast.makeText(Souscription.this, "enregistrement éffectué", Toast.LENGTH_SHORT).show();

                } else{

                    if(response.code() == 422){
                        handleErrors(response.errorBody());
                    } else{
                        errorResponse(response.message());
                    }

                }

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

                //si on a une erreur de type 500 alors cela viendra ici
                progressDialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(Souscription.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(Souscription.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void errorResponse(String message) {

        View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
        statutOperation.setText(message);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    private void handleErrors(ResponseBody responseBody){

        ApiError apiError = Utils_manageError.convertErrors(responseBody);
        /*
        boucle sur toutes les erreurs trouvées
         */

        for(Map.Entry<String, List<String>> error: apiError.getErrors().entrySet()){

            if(error.getKey().equals("lastname")){
                til_nom.setError(error.getValue().get(0));
            }


            if(error.getKey().equals("firstname")){
                til_prenom.setError(error.getValue().get(0));
            }


            if(error.getKey().equals("phone")){
                til_numeroTel.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("cni")){
                til_cni.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("adress")){
                til_adresse.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("numcarte")){
                til_numCarte.setError(error.getValue().get(0));
            }

        }

    }



    private class ReadThread extends Thread {
        byte[] nfcData = null;

        @Override
        public void run() {
            try {

                time1 = System.currentTimeMillis();
                nfcData = nfc.activate(10 * 1000); // 10s
                time2 = System.currentTimeMillis();
                Log.e("yw activate", (time2 - time1) + "");
                if (null != nfcData) {
                    handler.sendMessage(handler.obtainMessage(SHOW_NFC_DATA, nfcData));
                } else {
                    Log.d(TAG, "Check Card timeout...");
                    handler.sendMessage(handler.obtainMessage(CHECK_NFC_TIMEOUT, null));
                }
            } catch (TelpoException e) {
                Log.e("yw", e.toString());
                e.printStackTrace();
            }
        }
    }


    private void m1CardAuthenticate() {
        Boolean status = true;
        byte[] passwd = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        try {

            time1 = System.currentTimeMillis();
            nfc.m1_authenticate(blockNum_1, (byte) 0x0B, passwd);//0x0B
            time2 = System.currentTimeMillis();
            Log.e("yw m1_authenticate", (time2 - time1) + "");


        } catch (TelpoException e) {
            status = false;
            e.printStackTrace();
            Log.e("yw", e.toString());
        }

        if (status) {
            Log.d(TAG, "m1CardAuthenticate success!");
            //writeBlockData();
            //readBlockData();

            //OwriteValueData();
            readValueDataCourt();
        } else {
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "m1CardAuthenticate fail!");
        }
    }


    private void readValueDataCourt() {
        byte[] data = null;
        try {
            data = nfc.m1_read_value(blockNum_2);
        } catch (TelpoException e) {
            e.printStackTrace();
        }

        if (null == data) {
            Log.e(TAG, "readValueBtn fail!");
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
        } else {
            tie_numCarte.setText(StringUtil.toHexString(data));
        }
    }


    //ETAPE 2: Envoi des données vers le serveur Google
    private class AsyncTaskRegisterDataInGoogleFirebaseServer extends AsyncTask<Void,Void,String> {

       private String nom1;
       private String prenom1;
       private String sexe1;
       private String telephone1;
       private String typePJ1;
       private String cni1;
       private String session1;
       private String adresse1;
       private String num_carte1;
       private String typeUser1;
       private String status1;
       private String abonnement1;
       private String response1;


        public AsyncTaskRegisterDataInGoogleFirebaseServer(String nom1, String prenom1, String sexe1, String telephone1, String typePJ1,
                                                           String cni1, String session1, String adresse1, String num_carte1,
                                                           String typeUser1, String status1, String abonnement1, String response1) {
            this.nom1 = nom1;
            this.prenom1 = prenom1;
            this.sexe1 = sexe1;
            this.telephone1 = telephone1;
            this.typePJ1 = typePJ1;
            this.cni1 = cni1;
            this.session1 = session1;
            this.adresse1 = adresse1;
            this.num_carte1 = num_carte1;
            this.typeUser1 = typeUser1;
            this.status1 = status1;
            this.abonnement1 = abonnement1;
            this.response1 = response1;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialogGoogle = ProgressDialog.show(Souscription.this, getString(R.string.etape2EnvoiDesDonnees), getString(R.string.connexionServeurSmopaye),true,true);

            //INITIALISATION DES SERVICES GOOGLE FIREBASE
            auth = FirebaseAuth.getInstance();
            apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialogGoogle.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            registerGoogleFirebase(this.nom1, this.prenom1,this.sexe1, this.telephone1, this.typePJ1,
                    this.cni1, "", this.adresse1, this.num_carte1, "",
                    "sm" + this.telephone1 + "@smopaye.cm", "default",
                    this.status1, this.abonnement1, this.response1);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialogGoogle.dismiss();
        }
    }

    private void registerGoogleFirebase(String nom1, String prenom1, String sexe1,
                                         String tel1, String typePJ1, String cni1, String session1,
                                         String adresse1, String id_carte1, String typeUser1,
                                        String email1, String imageURL1, String status1, String abonnement1, String response1) {
        auth.createUserWithEmailAndPassword(email1, tel1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("nom", nom1);
                            hashMap.put("prenom", prenom1);
                            hashMap.put("sexe", sexe1);
                            hashMap.put("tel", tel1);
                            hashMap.put("cni", typePJ1+"-"+cni1);
                            hashMap.put("session", session1);
                            hashMap.put("adresse", adresse1);
                            hashMap.put("id_carte", id_carte1);
                            hashMap.put("typeUser", typeUser1);
                            hashMap.put("imageURL", imageURL1);
                            hashMap.put("status", status1);
                            hashMap.put("search", nom1.toLowerCase());
                            hashMap.put("abonnement", abonnement1);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Souscription.this, getString(R.string.operationReussie), Toast.LENGTH_SHORT).show();

                                    /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////

                                    //SERVICE GOOGLE FIREBASE
                                    Query query = FirebaseDatabase.getInstance().getReference("Users")
                                            .orderByChild("id_carte")
                                            .equalTo(id_carte1);

                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                    User user = userSnapshot.getValue(User.class);
                                                    if (user.getId_carte().equals(id_carte1)) {
                                                        RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.souscription), response1, "success");
                                                        //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(Souscription.this, getString(R.string.numCompteExistPas), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                            else{
                                                Toast.makeText(Souscription.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                    dbHandler = new DbHandler(getApplicationContext());
                                    dbUser = new DbUser(getApplicationContext());
                                    aujourdhui = new Date();
                                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                    //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                    LocalNotification(getString(R.string.souscription), response1);
                                    dbHandler.insertUserDetails(getString(R.string.souscription), response1, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


                                    ////////////////////INSERTION DES DONNEES UTILISATEURS DANS LA BD LOCALE/////////////////////////
                                    dbUser.insertInfoUser(nom1, prenom1, sexe1,
                                            tel1, cni1, session1,
                                            adresse1, id_carte1, typeUser1,
                                            "default", "offline" , abonnement1, shortDateFormat.format(aujourdhui));


                                    String num_carte = id_carte1;

                                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                    title.setText(getString(R.string.information));
                                    imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                    statutOperation.setText(response1);
                                    build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(Souscription.this, QRCodeShow.class);
                                            intent.putExtra("id_carte", "E-ZPASS" +num_carte + getsecurity_keys());
                                            intent.putExtra("nom_prenom", nom1 + " " + prenom1);
                                            startActivity(intent);
                                        }
                                    });
                                    build_error.setCancelable(false);
                                    build_error.setView(view);
                                    build_error.show();

                                    til_nom.getEditText().setText("");
                                    til_prenom.getEditText().setText("");
                                    til_numeroTel.getEditText().setText("");
                                    til_cni.getEditText().setText("");
                                    til_adresse.getEditText().setText("");
                                    til_numCarte.getEditText().setText("");
                                }
                            });
                        }
                        else{

                            ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                            dbHandler = new DbHandler(getApplicationContext());
                            aujourdhui = new Date();
                            shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                            //////////////////////////////////NOTIFICATIONS////////////////////////////////
                            LocalNotification(getString(R.string.souscription), getString(R.string.impossibleRegister));
                            dbHandler.insertUserDetails(getString(R.string.souscription),getString(R.string.impossibleRegister), "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

                            Toast.makeText(Souscription.this, getString(R.string.impossibleRegister), Toast.LENGTH_SHORT).show();
                            View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                            title.setText(getString(R.string.information));
                            imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                            statutOperation.setText(getString(R.string.impossibleRegister));
                            build_error.setPositiveButton("OK", null);
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();
                        }
                    }
                });
    }




    private void RemoteNotification(final String receiver, final String username, final String title, final String message, final String statut_notif){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(fuser.getUid(), R.mipmap.logo_official, username + ": " + message, title, receiver, statut_notif);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(Souscription.this, getString(R.string.echoue), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void LocalNotification(String titles, String subtitles){

        ///////////////DEBUT NOTIFICATIONS///////////////////////////////
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notif_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notif_expanded);

        Intent clickIntent = new Intent(getApplicationContext(), NotifReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, clickIntent, 0);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1, titles);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, subtitles);

        expandedView.setImageViewResource(R.id.image_view_expanded, R.mipmap.logo_official);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo_official)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
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
