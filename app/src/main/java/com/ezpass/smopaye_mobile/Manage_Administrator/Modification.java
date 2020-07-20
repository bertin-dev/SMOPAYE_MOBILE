package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class Modification extends AppCompatActivity {

    private EditText nom,prenom,telephone,cni,numCarte, idUsers, adresse;
    private Spinner sexe, statut, operateur, typeChauffeur, typePjustificative;
    private Button btnmodifier, btnAnnuler, btnOpenNFC;
    private ProgressDialog progressDialog;
    /////////////////////////////////////////////////////////////////////////////////
    Handler handler;
    Runnable runnable;
    Timer timer;
    Thread readThread;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    long time1, time2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    Nfc nfc = new Nfc(this);
    DialogInterface dialog;
    AlertDialog.Builder build_error;
    /*NOTIFICATION*/
    private NotificationManagerCompat notificationManager;


    //BD LOCALE
    private DbHandler dbHandler;
    private DbUser dbUser;
    private Date aujourdhui;
    private DateFormat shortDateFormat;


    private CheckBox AbonnementMensuel;
    private CheckBox AbonnementHebdomadaire;
    private CheckBox AbonnementService;


    private String abonnement = "service";


    private LinearLayout internetIndisponible, authWindows;
    private Button btnReessayer;
    private ImageView conStatusIv;
    private TextView titleNetworkLimited, msgNetworkLimited;


    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    private String file = "tmp_number";
    private int c;
    private String temp_number = "";

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
        setContentView(R.layout.activity_modification);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.modification));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(Modification.this);
        build_error = new AlertDialog.Builder(Modification.this);


        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        sexe = (Spinner) findViewById(R.id.sexe);
        telephone = (EditText) findViewById(R.id.numeroTel);
        cni = (EditText) findViewById(R.id.cni);
        statut = (Spinner) findViewById(R.id.statut);
        numCarte = (EditText) findViewById(R.id.numCarte);
        btnmodifier = (Button) findViewById(R.id.btnmodifier);
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

        idUsers = (EditText) findViewById(R.id.idUsers);
        final Intent intent = getIntent();
        String string = intent.getStringExtra("ID_USER");
        final String[] id_users = string.split("-");
        idUsers.setText(id_users[0]);


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

        if(intent.hasExtra("ACTION")){
            if(intent.getStringExtra("ACTION").equalsIgnoreCase("Modifier"))
            {
                if (intent.hasExtra("ID_USER")) {
                    //btnmodifier.setText("Modifier");

                    //Toast.makeText(Modification.this, id_users[0], Toast.LENGTH_SHORT).show();

                    //ENVOI DE L'ID DE L'ELEMENT CLIQUE VERS LE SERVEUR
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Uri.Builder builder = new Uri.Builder();
                                builder.appendQueryParameter("auth", "Users");
                                builder.appendQueryParameter("login", "updateUser");
                                builder.appendQueryParameter("iduserlist", idUsers.getText().toString().trim());
                                //builder.appendQueryParameter("numUser", temp_number);
                                builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                                builder.appendQueryParameter("uhtdgG18", ChaineConnexion.getSalt());
                                //URL url = new URL("http://192.168.20.11:1234/listing.php"+builder.build().toString());
                                URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());
                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                httpURLConnection.setConnectTimeout(5000);
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.connect();

                                InputStream inputStream = httpURLConnection.getInputStream();

                                final BufferedReader bufferedReader  =  new BufferedReader(new InputStreamReader(inputStream));

                                String string="";
                                String data="";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(Modification.this, "Encours de traitement...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                while (bufferedReader.ready() || data==""){
                                    data+=bufferedReader.readLine();
                                }
                                bufferedReader.close();
                                inputStream.close();


                                final String f = data.trim();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //CHARGEMENT DES DONNEES RECU DANS LES DIFFERENTS CHAMPS CONCERNES
                                        // Toast.makeText(Modification.this, f, Toast.LENGTH_LONG).show();
                                    }
                                });


                                //    JSONObject jsonObject = new JSONObject(data);
                                //  jsonObject.getString("status");
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i=0;i<jsonArray.length();i++){
                                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //CHARGEMENT DES DONNEES RECU DANS LES DIFFERENTS CHAMPS CONCERNES
                                                //Toast.makeText(Modification.this, jsonObject.getString("ID_USER"), Toast.LENGTH_SHORT).show();
                                                nom.setText(jsonObject.getString("NOM"));
                                                prenom.setText(jsonObject.getString("PRENOM"));
                                                //sexe.setSelected(jsonObject.getString("GENRE"));
                                                telephone.setText(jsonObject.getString("TELEPHONE"));

                                                int position = jsonObject.getString("CNI").indexOf("-");
                                                String[] cni1 = jsonObject.getString("CNI").split("-");
                                                if(position > 0){
                                                    cni.setText(cni1[1]);
                                                } else{
                                                    cni.setText(cni1[0]);
                                                }

                                                adresse.setText(jsonObject.getString("Adresse"));
                                                //statut.setSelected(jsonObject.getString("STATUT"));
                                                numCarte.setText(jsonObject.getString("ID_CARTE"));
                                                // operateur.setSelected(jsonObject.getString("Opérateur"));

                                                 /* new AlertDialog.Builder(Modification.this)
                                                          .setMessage(f)
                                                          .setPositiveButton("OK", null)
                                                          .setCancelable(false)
                                                          .show();*/


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                            } catch (final IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Modification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                else {
                    Toast.makeText(this, getString(R.string.erreurModif), Toast.LENGTH_SHORT).show();
                }

            }else {

                /***************************************/
                AlertDialog.Builder alert = new AlertDialog.Builder(Modification.this);
                alert.setTitle(getString(R.string.information));
                alert.setMessage(getString(R.string.suppression));
                // Set an EditText view to get user input
                final EditText input = new EditText(Modification.this);
                alert.setView(input);
                input.setText(getString(R.string.suppQuestion) + " " + id_users[1].toString() + " ???");
                input.setEnabled(false);
                alert.setPositiveButton(getString(R.string.supprimer), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (intent.hasExtra("ID_USER")) {
                            String string = intent.getStringExtra("ID_USER");
                            //final String[] id_users = string.split("-");
                            //Toast.makeText(Modification.this, id_users[0], Toast.LENGTH_SHORT).show();

                            //ENVOI DE L'ID DE L'ELEMENT CLIQUE VERS LE SERVEUR
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        Uri.Builder builder = new Uri.Builder();
                                        builder.appendQueryParameter("idUser",idUsers.toString().trim());
                                        //builder.appendQueryParameter("numUser", temp_number);
                                        //URL url = new URL("http://192.168.20.11:1234/deleteUser.php"+builder.build().toString());
                                        URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());
                                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                        httpURLConnection.setConnectTimeout(5000);
                                        httpURLConnection.setRequestMethod("POST");
                                        httpURLConnection.connect();

                                        InputStream inputStream = httpURLConnection.getInputStream();

                                        final BufferedReader bufferedReader  =  new BufferedReader(new InputStreamReader(inputStream));

                                        String string="";
                                        String data="";
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Toast.makeText(Modification.this, "Encours de traitement...", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        while (bufferedReader.ready() || data==""){
                                            data+=bufferedReader.readLine();
                                        }
                                        bufferedReader.close();
                                        inputStream.close();


                                        final String f = data.trim();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //CHARGEMENT DES DONNEES RECU DANS LES DIFFERENTS CHAMPS CONCERNES
                                                Toast.makeText(Modification.this, f, Toast.LENGTH_LONG).show();

                                                ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                                dbHandler = new DbHandler(getApplicationContext());
                                                aujourdhui = new Date();
                                                shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                                //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                                notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                                notifications(getString(R.string.suppCompte), f);
                                                //dbHandler.insertUserDetails("Suppression du compte",f, "0", shortDateFormat.format(aujourdhui));

                                                new AlertDialog.Builder(Modification.this)
                                                        .setMessage(f)
                                                        .setPositiveButton("OK", null)
                                                        .setCancelable(false)
                                                        .show();
                                            }
                                        });


                                        //    JSONObject jsonObject = new JSONObject(data);
                                        //  jsonObject.getString("status");
                                        JSONArray jsonArray = new JSONArray(data);
                                        for (int i=0;i<jsonArray.length();i++){
                                            final JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        //CHARGEMENT DES DONNEES RECU DANS LES DIFFERENTS CHAMPS CONCERNES
                                                        //Toast.makeText(Modification.this, jsonObject.getString("ID_USER"), Toast.LENGTH_SHORT).show();
                                                          /*nom.setText(jsonObject.getString("NOM"));
                                                         /prenom.setText(jsonObject.getString("PRENOM"));
                                                          //sexe.setSelected(jsonObject.getString("GENRE"));
                                                          telephone.setText(jsonObject.getString("TELEPHONE"));
                                                          cni.setText(jsonObject.getString("CNI"));
                                                          adresse.setText(jsonObject.getString("Adresse"));
                                                          //statut.setSelected(jsonObject.getString("STATUT"));
                                                          numCarte.setText(jsonObject.getString("ID_CARTE"));
                                                          // operateur.setSelected(jsonObject.getString("Opérateur"));

                                                          new AlertDialog.Builder(Modification.this)
                                                                  .setMessage(f)
                                                                  .setPositiveButton("OK", null)
                                                                  .setCancelable(false)
                                                                  .show();*/
                                                        Toast.makeText(Modification.this, jsonObject.getString("montant"), Toast.LENGTH_SHORT).show();

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }

                                    } catch (final IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(Modification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        }else {
                            Toast.makeText(Modification.this, getString(R.string.erreurSurvenue2), Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                alert.setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(Modification.this, getString(R.string.suppAnnule), Toast.LENGTH_SHORT).show();

                        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                        dbHandler = new DbHandler(getApplicationContext());
                        aujourdhui = new Date();
                        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                        //////////////////////////////////NOTIFICATIONS////////////////////////////////
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notifications(getString(R.string.suppressionCompte), getString(R.string.annulationSuppCompte));
                        //dbHandler.insertUserDetails("Supression du compte","Annulation de la suppression du compte", "0", shortDateFormat.format(aujourdhui));
                    }
                });

                alert.show();
            }


        }

        btnmodifier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toast.makeText(Modification.this, nom.getText().toString()+" im here "+prenom.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                if(nom.getText().toString().trim().equals("") || prenom.getText().toString().trim().equals("") || telephone.getText().toString().trim().equals("")
                        || cni.getText().toString().trim().equals("") || numCarte.getText().toString().trim().equals("")) {
                    Toast.makeText(Modification.this, getString(R.string.champsVide), Toast.LENGTH_SHORT).show();
                }else{

                    if(isValid(nom.getText().toString().trim()) && isValid(prenom.getText().toString().trim()) && isValid(cni.getText().toString().trim())) {
                        /* ---------------DEBUT--------------------------------- */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //********************DEBUT***********
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // On ajoute un message à notre progress dialog
                                            progressDialog.setMessage(getString(R.string.connexionserver));
                                            // On donne un titre à notre progress dialog
                                            progressDialog.setTitle(getString(R.string.attenteReponseServer));
                                            // On spécifie le style
                                            //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                            // On affiche notre message
                                            progressDialog.show();
                                            //build.setPositiveButton("ok", new View.OnClickListener()
                                        }
                                    });
                                    //*******************FIN*****
                                    Uri.Builder builder = new Uri.Builder();
                                    builder.appendQueryParameter("auth","Users");
                                    builder.appendQueryParameter("login", "updateUser");
                                    builder.appendQueryParameter("IDUSER", idUsers.getText().toString().trim());
                                    builder.appendQueryParameter("NOM", nom.getText().toString().trim().toLowerCase());
                                    builder.appendQueryParameter("PRENOM", prenom.getText().toString().trim().toLowerCase());
                                    builder.appendQueryParameter("GENRE", sexe.getSelectedItem().toString().trim().toUpperCase());
                                    builder.appendQueryParameter("TELEPHONE", telephone.getText().toString().trim());
                                    builder.appendQueryParameter("CNI", typePjustificative.getSelectedItem().toString().trim()+"-"+cni.getText().toString().trim());
                                    builder.appendQueryParameter("sessioncompte", statut.getSelectedItem().toString().trim());
                                    builder.appendQueryParameter("Adresse", adresse.getText().toString().trim());
                                    builder.appendQueryParameter("IDCARTE", numCarte.getText().toString().trim());
                                    builder.appendQueryParameter("IDCathegorie", typeChauffeur.getSelectedItem().toString().trim());
                                    //builder.appendQueryParameter("typeAbon", abonnement);
                                    builder.appendQueryParameter("uniquser", temp_number);
                                    builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());
                                    //builder.appendQueryParameter("operateur",operateur.getSelectedItem().toString());



                                    //URL url = new URL("http://192.168.20.11:1234/updateUser.php"+builder.build().toString());
                                    URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                    httpURLConnection.setConnectTimeout(5000);
                                    httpURLConnection.setRequestMethod("POST");
                                    httpURLConnection.connect();


                                    InputStream inputStream = httpURLConnection.getInputStream();

                                    final BufferedReader bufferedReader  =  new BufferedReader(new InputStreamReader(inputStream));

                                    String string="";
                                    String data="";
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Toast.makeText(Modification.this, "ooooo", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    while (bufferedReader.ready() || data==""){
                                        data+=bufferedReader.readLine();
                                    }
                                    bufferedReader.close();
                                    inputStream.close();


                                    final String f = data.trim();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();


                                            ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                            dbHandler = new DbHandler(getApplicationContext());
                                            aujourdhui = new Date();
                                            shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                            //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                            notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                            notifications(getString(R.string.modificationCompte), f);
                                            //dbHandler.insertUserDetails("Modification du compte",f,"0", shortDateFormat.format(aujourdhui));




                                            View view = LayoutInflater.from(Modification.this).inflate(R.layout.alert_dialog_success, null);
                                            TextView title = (TextView) view.findViewById(R.id.title);
                                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                            title.setText(getString(R.string.information));
                                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                            statutOperation.setText(f);
                                            build_error.setPositiveButton("OK", null);
                                            build_error.setCancelable(false);
                                            build_error.setView(view);
                                            build_error.show();

                                            nom.setText("");
                                            prenom.setText("");
                                            telephone.setText("");
                                            cni.setText("");
                                            numCarte.setText("");


                                            // Toast.makeText(Modification.this, f, Toast.LENGTH_LONG).show();
                                        }
                                    });


                                    //    JSONObject jsonObject = new JSONObject(data);
                                    //  jsonObject.getString("status");
                                    JSONArray jsonArray = new JSONArray(data);
                                    for (int i=0;i<jsonArray.length();i++){
                                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(Modification.this, jsonObject.getString("nom"), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }

                                } catch (final IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Modification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    e.printStackTrace();
                                    try {
                                        Thread.sleep(2000);
                                        //Toast.makeText(Modification.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                                    } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        /* ---------------FIN--------------------------------- */

                    }
                    else{
                        Toast.makeText(getApplicationContext(), getString(R.string.champsinValide), Toast.LENGTH_SHORT).show();

                        View view = LayoutInflater.from(Modification.this).inflate(R.layout.alert_dialog_success, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                        title.setText(getString(R.string.information));
                        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                        statutOperation.setText(getString(R.string.champsinValide));
                        build_error.setPositiveButton("OK", null);
                        build_error.setCancelable(false);
                        build_error.setView(view);
                        build_error.show();
                    }

                }
            }

        });


        btnOpenNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nfc.open();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // On ajoute un message à notre progress dialog
                            progressDialog.setMessage(getString(R.string.passageCarte));
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

                            new AlertDialog.Builder(Modification.this)
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
                            numCarte.setText(StringUtil.toHexString(uid));
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



        // Initializing a String Array
        String[] statut1 = new String[]{
                "Utilisateur",
                "Agent",
                "Administrateur",
                "Accepteur"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,statut1);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        statut.setAdapter(spinnerArrayAdapter1);


        // Initializing a String Array
        String[] typeChauffeur1 = new String[]{
                "moto_taxi",
                "Chauffeur",
                "cargo",
                "bus inter urbain",
                "Commerçant",
                "restaurant etudiant",
                "Chauffeur",
                "autre"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,typeChauffeur1);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(spinnerArrayAdapter);


        // Initializing a String Array
        String[] sexe1 = new String[]{
                "Masculin",
                "Feminin"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,sexe1);
        spinnerArrayAdapter3.setDropDownViewResource(R.layout.spinner_item);
        sexe.setAdapter(spinnerArrayAdapter3);

        // Initializing a String Array
        String[] pieceJ = new String[]{
                "CNI",
                "passeport",
                "recipissé",
                "carte de séjour",
                "carte d'étudiant"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,pieceJ);
        spinnerArrayAdapter4.setDropDownViewResource(R.layout.spinner_item);
        typePjustificative.setAdapter(spinnerArrayAdapter4);



        //VERIFICATION DE L'ETAT DU CHANGEMENT DE STATUT
        statut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("accepteur")){
                    //typeChauffeur.setVisibility(View.VISIBLE);
                    addItemsOnSpinner2();
                }
                else if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("utilisateur")){
                    //typeChauffeur.setVisibility(View.VISIBLE);
                    addItemsOnSpinner3();
                }
                else {
                    addItemsOnSpinner1();
                    // typeChauffeur.setVisibility(View.GONE);
                    //typeChauffeur.setAd
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }

    public void addItemsOnSpinner3() {

        //spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("étudiant");
        list.add("élève");
        list.add("particulier");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(dataAdapter);



        /*ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,typeChauffeur1);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(spinnerArrayAdapter);*/

    }


    public void addItemsOnSpinner2() {

        //spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("moto_taxis");
        list.add("Chauffeur");
        list.add("mini_bus");
        list.add("bus inter urbain");
        list.add("restaurant étudiant");
        list.add("monetbil");
        list.add("moreals");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(dataAdapter);



        /*ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,typeChauffeur1);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(spinnerArrayAdapter);*/

    }

    public void addItemsOnSpinner1() {

        //spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("smopaye");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(dataAdapter);
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



    public void notifications(String titles, String subtitles){

        ///////////////DEBUT NOTIFICATIONS///////////////////////////////
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
                .setSmallIcon(R.drawable.logo_min)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(1, notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
    }

    public static boolean isValid(String str)
    {
        boolean isValid = false;
        String expression = "^[a-z_A-Z0-9éè ]*$";
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

        if( id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //gestion des abonnements
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

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
                if (checked)
                {
                    Toast.makeText(this, AbonnementService.getText().toString(), Toast.LENGTH_SHORT).show();
                    AbonnementMensuel.setChecked(false);
                    AbonnementHebdomadaire.setChecked(false);
                    abonnement = "service";
                }
                else{
                    AbonnementService.setChecked(true);
                    AbonnementMensuel.setChecked(false);
                    AbonnementHebdomadaire.setChecked(false);
                    abonnement = "service";
                }
                break;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));

            //network not available ic_wifi_red
            conStatusIv.setImageResource(R.drawable.ic_wifi_red);
            titleNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            msgNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            findViewById(R.id.btnReessayer).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));

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
