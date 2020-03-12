package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.QRCodeShow;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.TutorielUtilise;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.ezpass.smopaye_mobile.ChaineConnexion.getsecurity_keys;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class Souscription extends AppCompatActivity {

    private EditText nom,prenom,telephone,cni,numCarte, adresse;
    private Spinner sexe, statut, typeChauffeur, typePjustificative;
    private Button btnSouscription, btnAnnuler, btnOpenNFC;
    private ProgressDialog progressDialog, progressDialog1, progressDialog2;
    /////////////////////////////////////////////////////////////////////////////////
    Handler handler;
    Thread readThread;
    private byte blockNum_1 = 1;
    private byte blockNum_2 = 2;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    long time1, time2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    Nfc nfc = new Nfc(this);
    AlertDialog.Builder build_error;


    private CheckBox AbonnementMensuel;
    private CheckBox AbonnementHebdomadaire;
    private CheckBox AbonnementService;
    private String abonnement = "service";
    private LinearLayout internetIndisponible, authWindows;
    private Button btnReessayer;
    ImageView conStatusIv;
    TextView titleNetworkLimited, msgNetworkLimited;

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";


    BufferedInputStream is;
    String line = null;
    String result = null;

    String[] id_session;
    String[] nom_session;

    String[] IDCathegorie;
    String[] NOMCath;
    String[] typeuser;


    ArrayList<String> listStatut = new ArrayList<>();
    List<String> idStatut = new ArrayList<String>();

     String num_statut = "";
     String num_categorie = "";

    ArrayList<String> maListeIDCat = new ArrayList<>();
    ArrayList<String> ListIDTypeUser = new ArrayList<>();
    ArrayList<String> maListe = new ArrayList<>();

    HashMap<Integer, String> listAllSession = new HashMap<>();
    HashMap<Integer, String> listAllCategorie = new HashMap<>();
    HashMap<Integer, String> listFILTRECategorie = new HashMap<>();


    ArrayList<String> maListeIDCat1 = new ArrayList<>();
    ArrayList<String> ListIDTypeUser1 = new ArrayList<>();
    ArrayList<String> maListe1 = new ArrayList<>();
    String[] IDCathegorie1;
    String[] NOMCath1;
    String[] typeuser1;


    //BD LOCALE
    private DbHandler dbHandler;
    private DbUser dbUser;
    private Date aujourdhui;
    private DateFormat shortDateFormat;



    //SERVICES GOOGLE FIREBASE
    FirebaseAuth auth;
    DatabaseReference reference;
    APIService apiService;
    FirebaseUser fuser;


    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;

    private String[] sexe1;
    private String[] pieceJ;



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

        new loadDataSpinner(getApplication(), statut).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souscription);

        getSupportActionBar().setTitle(getString(R.string.souscription));
        //getSupportParentActivityIntent().putExtra("resultatBD", "Administrateur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        progressDialog = new ProgressDialog(Souscription.this);
        build_error = new AlertDialog.Builder(Souscription.this);


        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        sexe = (Spinner) findViewById(R.id.sexe);
        telephone = (EditText) findViewById(R.id.numeroTel);
        cni = (EditText) findViewById(R.id.cni);
        statut = (Spinner) findViewById(R.id.statut);
        numCarte = (EditText) findViewById(R.id.numCarte);
        btnSouscription = (Button) findViewById(R.id.btnSouscription);
        //btnAnnuler = (Button) findViewById(R.id.btnAnnuler);
        btnOpenNFC = (Button) findViewById(R.id.btnOpenNFC);
        // operateur = (Spinner) findViewById(R.id.operateurs);

        typeChauffeur = (Spinner) findViewById(R.id.typeChauffeur);

        adresse = (EditText) findViewById(R.id.adresse);
        AbonnementMensuel = (CheckBox) findViewById(R.id.AbonnementMensuel);
        AbonnementHebdomadaire = (CheckBox) findViewById(R.id.AbonnementHebdomadaire);
        AbonnementService = (CheckBox) findViewById(R.id.AbonnementService);
        typePjustificative = (Spinner) findViewById(R.id.typePjustificative);

        authWindows = (LinearLayout) findViewById(R.id.authWindows);
        internetIndisponible = (LinearLayout) findViewById(R.id.internetIndisponible);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);

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

        typePjustificative.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cni.setHint("N° CNI");
                        } else {
                            cni.setHint("N° CNI");
                        }
                        break;
                    case 1:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cni.setHint("N° Passeport");
                        } else {
                            cni.setHint("N° Passport");
                        }
                        break;
                    case 2:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cni.setHint("N° Recipissé");
                        } else {
                            cni.setHint("N° Receipt");
                        }
                        break;
                    case 3:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cni.setHint("N° Carte de séjour");
                        } else {
                            cni.setHint("N° Residence permit");
                        }
                        break;
                    case 4:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cni.setHint("N° Carte d'étudiant");
                        } else {
                            cni.setHint("N° Student card");
                        }
                        break;
                    default:
                        cni.setHint("");
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
                Integer key = (Integer) swt.tag;
                //Toast.makeText(Souscription.this, listAllSession.get(key), Toast.LENGTH_SHORT).show();
                //Toast.makeText(Souscription.this, String.valueOf(key), Toast.LENGTH_SHORT).show();

                if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase(listAllSession.get(key))){
                    num_statut = String.valueOf(key);
                    new AsyncTaskFiltreCategorie(String.valueOf(key), Souscription.this).execute();
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
                Integer key = (Integer) swt.tag;
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





        btnSouscription.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                if(nom.getText().toString().trim().equals("")){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.nom), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.nom));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(prenom.getText().toString().trim().equals("")){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.prenom), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.prenom));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(telephone.getText().toString().trim().equals("")){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.numeroTel), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.numeroTel));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }


                if(telephone.length()< 9){
                    Toast.makeText(Souscription.this, getString(R.string.verifierNumero), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.verifierNumero));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(cni.getText().toString().trim().equals("")){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.numeroDe) + " " + typePjustificative.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.numeroDe) + " " + typePjustificative.getSelectedItem().toString());
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(adresse.getText().toString().trim().equals("")){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.adresse), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.adresse));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(numCarte.getText().toString().isEmpty()){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.numeroCarte), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.numeroCarte));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }


                if(statut.getCount() == 0){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertStatutListDeroulante), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.AlertStatutListDeroulante));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(typeChauffeur.getCount() == 0){
                    Toast.makeText(Souscription.this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertCategorieListDeroulante), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.veuillezInserer) + " " + getString(R.string.AlertCategorieListDeroulante));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(!isValid(nom.getText().toString().trim())){
                    Toast.makeText(Souscription.this, getString(R.string.votre) + " " + getString(R.string.nom) + " " + getString(R.string.invalidCararatere), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.votre) + " " + getString(R.string.nom) + " " + getString(R.string.invalidCararatere));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(!isValid(prenom.getText().toString().trim())){
                    Toast.makeText(Souscription.this, getString(R.string.votre) + " " + getString(R.string.prenom) + " " + getString(R.string.invalidCararatere), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.votre) + " " + getString(R.string.prenom) + " " + getString(R.string.invalidCararatere));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(!isValid(cni.getText().toString().trim())){
                    Toast.makeText(Souscription.this, getString(R.string.votre) + " " + typePjustificative.getSelectedItem().toString() + " " + getString(R.string.invalidCararatere), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.votre) + " " + typePjustificative.getSelectedItem().toString() + " " + getString(R.string.invalidCararatere));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }

                if(!isValid(adresse.getText().toString().trim())){
                    Toast.makeText(Souscription.this, getString(R.string.votre) + " " + getString(R.string.adresse) + " " + getString(R.string.invalidCararatere), Toast.LENGTH_SHORT).show();
                    View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.votre) + " " + getString(R.string.adresse) + " " + getString(R.string.invalidCararatere));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                    return;
                }



                /*Intent intent = new Intent(getApplicationContext(), SouscriptionUploadIMGidCard.class);
                intent.putExtra("NOM", nom.getText().toString().trim().toLowerCase());
                intent.putExtra("PRENOM", prenom.getText().toString().trim().toLowerCase());
                intent.putExtra("GENRE", sexe.getSelectedItem().toString().trim().toUpperCase());
                intent.putExtra("TELEPHONE", telephone.getText().toString().trim());
                intent.putExtra("CNI", typePjustificative.getSelectedItem().toString().trim()+"-"+cni.getText().toString().trim());
                intent.putExtra("sessioncompte", num_statut);
                intent.putExtra("Adresse", adresse.getText().toString().trim());
                intent.putExtra("IDCARTE", numCarte.getText().toString().trim());
                intent.putExtra("IDCathegorie", num_categorie);
                intent.putExtra("typeAbon", abonnement);
                intent.putExtra("uniquser", temp_number);
                intent.putExtra("sessioncompteValue", statut.getSelectedItem().toString().trim());
                intent.putExtra("IDCathegorieValue", typeChauffeur.getSelectedItem().toString().trim());
                intent.putExtra("register", "EnregStandard");
                startActivity(intent);*/

                UploadDataToServer();

            }

        });

        //PASSAGE DE LA CARTE
        btnOpenNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });


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


        btnReessayer.setOnClickListener(this::checkNetworkConnectionStatus);

    }

    public void checkNetworkConnectionStatus(View view) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if(activeInfo != null && activeInfo.isConnected()){

            ProgressDialog dialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
            dialog.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                    //this.recreate();
                    finish();
                    startActivity(getIntent());
                }
            }, 3000); // 3000 milliseconds delay

        } else{
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(Souscription.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    public class ReadThread extends Thread {
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

    public static boolean isValid(String str) {
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


    public void m1CardAuthenticate() {
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


    public void readValueDataCourt() {
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
            numCarte.setText(StringUtil.toHexString(data));
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


    public class loadDataSpinner extends AsyncTask<Void, Void, Void> {

        Context c;
        Spinner sp;

        public loadDataSpinner(Context c, Spinner sp) {
            this.c = c;
            this.sp = sp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //chargement et affichage des données du statut EX: Administrateur, Accepteur, Agent, Utilisateur
            LoadDbAllSessionInSpinner();

            //chargement sans affichage des données des Categories EX: mini-bus, restaurant smopaye
            LoadDbALLCategorieInpinner();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            List<StringWithTag> itemList = new ArrayList<>();

            /* Iterate through your original collection, in this case defined with an Integer key and String value. */
            for (Map.Entry<Integer, String> entry : listAllSession.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();

                /* Build the StringWithTag List using these keys and values. */
                itemList.add(new StringWithTag(value, key));
            }

            /* Set your ArrayAdapter with the StringWithTag, and when each entry is shown in the Spinner, .toString() is called. */
            ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(c, R.layout.spinner_item, itemList);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
            sp.setAdapter(spinnerAdapter);
        }
    }

    private void LoadDbAllSessionInSpinner(){
//connection
        try{
            final Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("auth","Users");
            builder.appendQueryParameter("login", "register");
            builder.appendQueryParameter("infoname", "status");
            builder.appendQueryParameter("uniquser", temp_number);
            builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
            builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

            URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

            //URL url = new URL(adressUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

//content
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

//JSON
        try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;

            listStatut.clear();
            idStatut.clear();

            listAllSession.clear();


            id_session = new String[ja.length()];
            nom_session = new String[ja.length()];

            for(int i=0; i<=ja.length();i++){
                jo = ja.getJSONObject(i);
                id_session[i] = jo.getString("id_session");
                nom_session[i] = jo.getString("nom_session");

                listStatut.add(nom_session[i]);
                idStatut.add(id_session[i]);

                listAllSession.put(Integer.parseInt(id_session[i]),  nom_session[i]);

                //Enlever Agent et Administrateur dans la liste
                if(nom_session[i].toLowerCase().equalsIgnoreCase("agent")){
                    listAllSession.remove(3);
                }



            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }


    }

    private void LoadDbALLCategorieInpinner(){
//connection

        try{
            final Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("auth","Users");
            builder.appendQueryParameter("login", "register");
            builder.appendQueryParameter("infoname", "cath");
            builder.appendQueryParameter("uniquser", temp_number);
            builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
            builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

            URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

            //URL url = new URL(adressUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

//content
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

//JSON
        try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;

            maListe.clear();
            maListeIDCat.clear();
            ListIDTypeUser.clear();

            IDCathegorie = new String[ja.length()];
            NOMCath = new String[ja.length()];
            typeuser = new String[ja.length()];

            for(int i=0; i<=ja.length();i++){
                jo = ja.getJSONObject(i);

                IDCathegorie[i] = jo.getString("IDCathegorie");
                NOMCath[i] = jo.getString("NOMCath");
                typeuser[i] = jo.getString("typeuser");

                ListIDTypeUser.add(typeuser[i]);
                maListe.add(NOMCath[i]);
                maListeIDCat.add(IDCathegorie[i]);

                listAllCategorie.put(Integer.parseInt(IDCathegorie[i]),  NOMCath[i]);

            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //FILTRE DES CATEGORIES
    private class AsyncTaskFiltreCategorie extends AsyncTask<Void, Void, Void>{

        private String id;
        private Context context;

        public AsyncTaskFiltreCategorie(String id, Context context) {
            this.id = id;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LoadDbFILTRECategorieInpinner(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /*ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, maListe1);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            typeChauffeur.setAdapter(spinnerArrayAdapter);*/



            //------------------------------

            List<StringWithTag> itemList1 = new ArrayList<>();

            /* Iterate through your original collection, in this case defined with an Integer key and String value. */
            for (Map.Entry<Integer, String> entry : listFILTRECategorie.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();
                /* Build the StringWithTag List using these keys and values. */

                itemList1.add(new StringWithTag(value, key));
            }

            /* Set your ArrayAdapter with the StringWithTag, and when each entry is shown in the Spinner, .toString() is called. */
            ArrayAdapter<StringWithTag> spinnerAdapter = new ArrayAdapter<StringWithTag>(context, R.layout.spinner_item, itemList1);
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
            typeChauffeur.setAdapter(spinnerAdapter);
            //------------------------------

        }
    }

    private void LoadDbFILTRECategorieInpinner(String id){
//connection

        try{
            final Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("auth","Users");
            builder.appendQueryParameter("login", "register");
            builder.appendQueryParameter("infoname", "cath");
            builder.appendQueryParameter("uniquser", temp_number);
            builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
            builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

            URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

            //URL url = new URL(adressUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

//content
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

//JSON
        try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;

            maListe1.clear();
            maListeIDCat1.clear();
            ListIDTypeUser1.clear();

            listFILTRECategorie.clear();

            IDCathegorie1 = new String[ja.length()];
            NOMCath1 = new String[ja.length()];
            typeuser1 = new String[ja.length()];

            for(int i=0; i<=ja.length();i++){
                jo = ja.getJSONObject(i);

                IDCathegorie1[i] = jo.getString("IDCathegorie");
                NOMCath1[i] = jo.getString("NOMCath");
                typeuser1[i] = jo.getString("typeuser");


                if(typeuser1[i].equals(id)){
                    ListIDTypeUser1.add(typeuser1[i]);
                    maListe1.add(NOMCath1[i]);
                    maListeIDCat1.add(IDCathegorie1[i]);
                    listFILTRECategorie.put(Integer.parseInt(IDCathegorie1[i]),  NOMCath1[i]);
                }


            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private static class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String string, Object tag) {
            this.string = string;
            this.tag = tag;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    //ETAPE 1: envoi des données vers le serveur smopaye
    private void UploadDataToServer(){

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = new ProgressDialog(Souscription.this);
                progressDialog.setMessage(getString(R.string.connexionServeurSmopaye));
                progressDialog.setTitle(getString(R.string.etape1EnvoiDesDonnees));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                //progressDialog = ProgressDialog.show(Souscription.this, getString(R.string.etape1EnvoiDesDonnees), getString(R.string.connexionServeurSmopaye),true,true);
                build_error = new AlertDialog.Builder(Souscription.this);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                progressDialog.dismiss();
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                String f = string1.toLowerCase().trim();

                progressDialog.dismiss();

                int pos = f.indexOf("success");
                    if (pos >= 0) {

                        new AsyncTaskGoogleFirebase(f).execute();
                    }
                    else{
                        dbHandler = new DbHandler(getApplicationContext());
                        aujourdhui = new Date();
                        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                        //////////////////////////////////NOTIFICATIONS////////////////////////////////
                        LocalNotification(getString(R.string.souscription), f);
                        dbHandler.insertUserDetails(getString(R.string.souscription), f, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

                        build_error = new AlertDialog.Builder(Souscription.this);
                        View view = LayoutInflater.from(Souscription.this).inflate(R.layout.alert_dialog_success, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                        title.setText(getString(R.string.information));
                        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                        statutOperation.setText(f);
                        build_error.setPositiveButton("OK", null);
                        build_error.setCancelable(false);
                        build_error.setView(view);
                        build_error.show();


                        Toast.makeText(Souscription.this, f, Toast.LENGTH_SHORT).show();
                    }



            }

            @Override
            protected String doInBackground(Void... params) {

                httpURLConnectionProcess imageProcessClass = new httpURLConnectionProcess();

                    Uri.Builder builder = new Uri.Builder();
                    builder.appendQueryParameter("enregUser", "users");
                    builder.appendQueryParameter("enregReg", "register");
                    builder.appendQueryParameter("NOM", nom.getText().toString().trim().toLowerCase());
                    builder.appendQueryParameter("PRENOM", prenom.getText().toString().trim().toLowerCase());
                    builder.appendQueryParameter("GENRE",  (sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("masculin") || sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("male") ? "MASCULIN" : "FEMININ" ));
                    builder.appendQueryParameter("TELEPHONE", telephone.getText().toString().trim().toLowerCase());
                    builder.appendQueryParameter("CNI", typePjustificative.getSelectedItem().toString().trim()+"-"+cni.getText().toString().trim());
                    builder.appendQueryParameter("sessioncompte", num_statut);
                    builder.appendQueryParameter("Adresse", adresse.getText().toString().trim().toLowerCase());
                    builder.appendQueryParameter("IDCARTE", numCarte.getText().toString().trim().toUpperCase());
                    builder.appendQueryParameter("IDCathegorie", num_categorie);
                    builder.appendQueryParameter("typeAbon", abonnement);
                    builder.appendQueryParameter("uniquser", temp_number);
                    builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

                String FinalData = imageProcessClass.ImageHttpRequest(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }



    //ETAPE 2: Envoi des données vers le serveur Google
    private class AsyncTaskGoogleFirebase extends  AsyncTask<Void,Void,String>{

        private String result;

        public AsyncTaskGoogleFirebase(String result) {
            this.result = result;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            /*progressDialog1 = new ProgressDialog(Souscription.this);
            progressDialog1.setMessage(getString(R.string.connexionServeurSmopaye));
            progressDialog1.setTitle(getString(R.string.etape2EnvoiDesDonnees));
            progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog1.show();*/
            progressDialog1 = ProgressDialog.show(Souscription.this, getString(R.string.etape2EnvoiDesDonnees), getString(R.string.connexionServeurSmopaye),true,true);


            //SERVICE GOOGLE FIREBASE
            auth = FirebaseAuth.getInstance();
            apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog1.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            registerGoogleFirebase(nom.getText().toString().trim(), prenom.getText().toString().trim(), sexe.getSelectedItem().toString().trim(),
                    telephone.getText().toString().trim(),  typePjustificative.getSelectedItem().toString().trim(), cni.getText().toString().trim(), statut.getSelectedItem().toString().trim(),
                    adresse.getText().toString().trim(), numCarte.getText().toString().trim(), typeChauffeur.getSelectedItem().toString().trim(),
                    "sm" + telephone.getText().toString().trim() + "@smopaye.cm", telephone.getText().toString().trim(),"default",  "offline", abonnement, result);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog1.dismiss();
        }
    }


    private void registerGoogleFirebase(final String nom1, final String prenom1, final String sexe1,
                                        final String tel1, final String typePJ1, final String cni1, final String session1,
                                        final String adresse1, final String id_carte1, final String typeUser1,
                                        String email1, String password1, final String imageURL1, final String status1, final String abonnement1, final String f1)
    {

        auth.createUserWithEmailAndPassword(email1, password1)
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
                                                        RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.souscription), f1, "success");
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
                                    LocalNotification(getString(R.string.souscription), f1);
                                    dbHandler.insertUserDetails(getString(R.string.souscription), f1, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


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
                                    statutOperation.setText(f1);
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

                                    nom.setText("");
                                    prenom.setText("");
                                    telephone.setText("");
                                    cni.setText("");
                                    adresse.setText("");
                                    numCarte.setText("");
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


    private class httpURLConnectionProcess{

        public String ImageHttpRequest(String requestURL) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);

                httpURLConnection.setConnectTimeout(5000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                //bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        /*private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }*/

    }
}
