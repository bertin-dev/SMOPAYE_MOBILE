package com.ezpass.smopaye_mobile.vuesAccepteur;

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
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.Tutoriel;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.telecollecte.DatabaseManager;
import com.ezpass.smopaye_mobile.telecollecte.ScoreData;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.ezpass.smopaye_mobile.vuesUtilisateur.RechargePropreCompte;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;
import static com.telpo.tps550.api.util.StringUtil.toHexString;

public class MenuRetraitTelecollecte extends AppCompatActivity {

    private LinearLayout Retrait,telecollecteAccepteur;
    private AlertDialog.Builder build, build_error;
    private ProgressDialog progressDialog;
    private DatabaseManager databaseManager;
    private EditText idCarteCacheTelecollecte;


    /////////////////////////////////////////////////////////////////////////////////
    Handler handler;
    Runnable runnable;
    Timer timer;
    Thread readThread;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    long time1, time2;
    private byte blockNum_1 = 1;
    private byte blockNum_2 = 2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    Nfc nfc = new Nfc(this);
    DialogInterface dialog;


    //private ScrollView comback;


    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    //SERVICES GOOGLE FIREBASE
    FirebaseAuth auth;
    DatabaseReference reference;
    Query dbRefrenceUser;
    APIService apiService;
    FirebaseUser fuser;


    LinearLayout internetIndisponible;
    Button btnReessayer;
    ImageView conStatusIv;
    TextView titleNetworkLimited, msgNetworkLimited;

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_retrait_telecollecte);

        getSupportActionBar().setTitle("Retrait et Télécollecte");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Retrait = (LinearLayout) findViewById(R.id.lnRetrait);
        telecollecteAccepteur = (LinearLayout) findViewById(R.id.lntelecollecteAccepteur);
        idCarteCacheTelecollecte = (EditText) findViewById(R.id.idCarteCacheTelecollecte);
        //comback = (ScrollView) findViewById(R.id.menuRetraitTelecollecte);

        build = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(MenuRetraitTelecollecte.this);

        //DEBUT
        //debitCard.setEnabled(false);
        build = new AlertDialog.Builder(MenuRetraitTelecollecte.this);
        databaseManager = new DatabaseManager(MenuRetraitTelecollecte.this);
        //databaseManager.insertScore("ZOME", 3000);


        //service google firebase
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        internetIndisponible = (LinearLayout) findViewById(R.id.internetIndisponible);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);



        final List<ScoreData> scores = databaseManager.readTop10();
        for ( ScoreData score : scores ) {
            // montantActuel.setText("montant  "+score.getMontant()+" fcfa");

        }


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

        Retrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuRetraitTelecollecte.this, RetraitAccepteur.class));
            }
        });

        telecollecteAccepteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DEBUT
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
                readThread = new MenuRetraitTelecollecte.ReadThread();
                readThread.start();

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

                                    new AlertDialog.Builder(MenuRetraitTelecollecte.this)
                                            .setMessage(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                                    "\r\n" + getString(R.string.atqb_data) + toHexString(atqb) +
                                                    "\r\n" + getString(R.string.pupi_data) + toHexString(pupi))
                                            .setPositiveButton("OK", null)
                                            .setCancelable(false)
                                            .show();

                           /* uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                    "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                    "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi));*/
                                    progressDialog.dismiss();

                                } else if (uid_data[0] == 0x41) {
                                    // TYPE A类（CPU, M1）
                                    byte[] atqa = new byte[2];
                                    byte[] sak = new byte[1];
                                    final byte[] uid = new byte[uid_data[5]];
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

                          /*  new AlertDialog.Builder(Recharge.this)
                                   .setMessage(getString(R.string.card_type) + getString(R.string.type_a) + " " + type +
                                            "\r\n" + getString(R.string.atqa_data) + StringUtil.toHexString(atqa) +
                                            "\r\n" + getString(R.string.sak_data) + StringUtil.toHexString(sak) +
                                            "\r\n" + getString(R.string.uid_data) + StringUtil.toHexString(uid))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();*/

                                    m1CardAuthenticate();

                                    if(!toHexString(uid).equals("")){
                                        progressDialog.dismiss();
                                        //final String idCard = StringUtil.toHexString(uid);
                                        try {
                                            nfc.close();
                                        } catch (TelpoException e) {
                                            e.printStackTrace();
                                        }
                                        //Toast.makeText(MenuRetraitTelecollecte.this, toHexString(uid), Toast.LENGTH_SHORT).show();
                                        //recharge("http://192.168.20.11:1234/fullcptrecepteur.php", StringUtil.toHexString(uid).toString().trim());

                                        //DEBUT -------

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {

                                                    /********************DEBUT***********/
                                                    /*try {
                                                        nfc.open();*/

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

                                                   /* } catch (TelpoException e) {
                                                        e.printStackTrace();
                                                    }*/
                                                    /*readThread = new MenuRechargeTelecollecte.ReadThread();
                                                    readThread.start();*/


                                                    /*******************FIN******/
                                                    final Uri.Builder builder = new Uri.Builder();
                                                    final List<ScoreData> scores = databaseManager.readTop10();

                                                    builder.appendQueryParameter("auth","Fullcptrecepteur");
                                                    builder.appendQueryParameter("login", "depot");
                                                    builder.appendQueryParameter("idTelephone", getSerialNumber().trim());
                                                    builder.appendQueryParameter("numcarte", idCarteCacheTelecollecte.getText().toString().trim());
                                                    for ( ScoreData score : scores ) {
                                                        if(score.getMontant()==0){
                                                            builder.appendQueryParameter("montant",0+"");
                                                        }
                                                        else {
                                                            builder.appendQueryParameter("montant", score.getMontant() + "");
                                                        }
                                                    }
                                                    builder.appendQueryParameter("uniquser", temp_number);
                                                    builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                                                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

                                                    //Uri.Builder builder = new Uri.Builder(); http://192.168.20.11:1234/fullcptrecepteur.php
                                                    //builder.appendQueryParameter("montant",0+"");

                                                    URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());//"http://192.168.20.11:1234/recharge.php"
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
                                                            Toast.makeText(MenuRetraitTelecollecte.this, "Encours de traitement...", Toast.LENGTH_SHORT).show();
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
                                                            for ( ScoreData score : scores ) {
                                                                //Toast.makeText(MenuRetraitTelecollecte.this, "votre compte a bien ete credite d'un montant de " + score.getMontant()+ " fcfa", Toast.LENGTH_LONG).show();
                                                       /* build.setMessage("votre compte a bien ete credite d'un montant de  "+score.getMontant()+" fcfa");
                                                        build.setCancelable(true);
                                                        AlertDialog dialog = build.create();
                                                        build.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                        dialog.show();*/
                                                                //  databaseManager.viderdata();


                                                                //Arrêt de l'attente du serveur
                                                                progressDialog.dismiss();
                                                                String[] parts = f.split("-");

                                                                String etat = parts[0].toLowerCase(); //ok


                                                                if (etat.equalsIgnoreCase("ok")) {
                                                                    databaseManager.viderdata();

                                                                    String frais_service = parts[1]; //frais de services
                                                                    String montantTelecollecte = parts[2]; // Montant télécollecte
                                                                    String soldeCompte = parts[3]; // soldeCompte

                                                                    build_error = new AlertDialog.Builder(MenuRetraitTelecollecte.this);
                                                                    View view = LayoutInflater.from(MenuRetraitTelecollecte.this).inflate(R.layout.alert_dialog_success, null);
                                                                    TextView title = (TextView) view.findViewById(R.id.title);
                                                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                                    title.setText(getString(R.string.information));
                                                                    imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                                                    statutOperation.setText("Le Montant de la Télécollecte " + montantTelecollecte + " Fcfa, Frais de services " + frais_service + " Fcfa, le Solde " + soldeCompte + " Fcfa.");

                                                                    //statutOperation.setText("Votre compte a bien été crédite d'un montant de  " + score.getMontant() + " FCFA");


                                                                    /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                                                    //SERVICE GOOGLE FIREBASE
                                                                    final String id_carte_sm = idCarteCacheTelecollecte.getText().toString().trim();
                                                                    final int montantAct = score.getMontant();

                                                                    Query query = FirebaseDatabase.getInstance().getReference("Users")
                                                                            .orderByChild("id_carte")
                                                                            .equalTo(id_carte_sm);

                                                                    query.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                            if(dataSnapshot.exists()){
                                                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                                                    User user = userSnapshot.getValue(User.class);
                                                                                    if (user.getId_carte().equals(id_carte_sm)) {
                                                                                        RemoteNotification(user.getId(), user.getPrenom(), "Télécollecte", "Votre compte a bien été crédité d'un montant de " + montantAct + " FCFA", "success");
                                                                                        //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(MenuRetraitTelecollecte.this, "Ce numéro de Carte n'existe pas", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }
                                                                            else{
                                                                                Toast.makeText(MenuRetraitTelecollecte.this, "Impossible d'envoyer votre notification", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });


                                                                    //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
                                                                    LocalNotification("Télécollecte", "Votre compte a bien été crédité d'un montant de  " + score.getMontant() + " FCFA");


                                                                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                                                    dbHandler = new DbHandler(getApplicationContext());
                                                                    aujourdhui = new Date();
                                                                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                                                    dbHandler.insertUserDetails("Télécollecte","Votre compte a bien été crédité d'un montant de  " + score.getMontant() + " FCFA", "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


                                                                    build_error.setPositiveButton("OK", null);

                                                                   /* build_error.setPositiveButton("Télécollecte", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            //String value = f;
                                                                            //Arrêt de l'attente du serveur
                                                                            progressDialog.dismiss();
                                                                            //Arrêt du processus D'attente de carte
                                                                            try {
                                                                                nfc.close();
                                                                            } catch (TelpoException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            Intent intent = new Intent(getApplicationContext(), RechargePropreCompte.class);
                                                                            intent.putExtra("id_carte", idCarteCacheTelecollecte.getText().toString().trim());
                                                                            startActivity(intent);

                                                                        }
                                                                    });
                                                                    build_error.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            // Canceled.
                                                                            progressDialog.dismiss();
                                                                            try {
                                                                                nfc.close();
                                                                            } catch (TelpoException e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                        }
                                                                    });*/

                                                                    build_error.setCancelable(false);
                                                                    build_error.setView(view);
                                                                    build_error.show();


                                                                   /* AlertDialog.Builder alert = new AlertDialog.Builder(MenuRetraitTelecollecte.this);
                                                                    alert.setTitle("INFORMATION");
                                                                    alert.setMessage("COMPTE ACTUEL");
                                                                    // Set an EditText view to get user input
                                                                    final EditText input = new EditText(MenuRetraitTelecollecte.this);

                                                                    alert.setView(input);
                                                                    input.setText("Votre compte a bien été crédite d'un montant de  " + score.getMontant() + " FCFA");
                                                                    input.setEnabled(false);

                                                                    alert.setPositiveButton("Télécollecte", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                                            //String value = f;
                                                                            //Arrêt de l'attente du serveur
                                                                            progressDialog.dismiss();
                                                                            //Arrêt du processus D'attente de carte
                                                                            try {
                                                                                nfc.close();
                                                                            } catch (TelpoException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            Intent intent = new Intent(getApplicationContext(), RechargePropreCompte.class);
                                                                            intent.putExtra("id_carteUser", toHexString(uid).toString().trim());
                                                                            startActivity(intent);
                                                                            // Do something with value!
                                                                        }
                                                                    });

                                                                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                                            // Canceled.
                                                                            progressDialog.dismiss();
                                                                            try {
                                                                                nfc.close();
                                                                            } catch (TelpoException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    });
                                                                    alert.show();*/

                                                                } else {
                                                                    build_error = new AlertDialog.Builder(MenuRetraitTelecollecte.this);
                                                                    View view = LayoutInflater.from(MenuRetraitTelecollecte.this).inflate(R.layout.alert_dialog_success, null);
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
                                                                    Toast.makeText(MenuRetraitTelecollecte.this, f, Toast.LENGTH_SHORT).show();


                                                                    /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                                                    //SERVICE GOOGLE FIREBASE
                                                                    final String id_carte_sm = idCarteCacheTelecollecte.getText().toString().trim();

                                                                    Query query = FirebaseDatabase.getInstance().getReference("Users")
                                                                            .orderByChild("id_carte")
                                                                            .equalTo(id_carte_sm);

                                                                    query.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                            if(dataSnapshot.exists()){
                                                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                                                    User user = userSnapshot.getValue(User.class);
                                                                                    if (user.getId_carte().equals(id_carte_sm)) {
                                                                                        RemoteNotification(user.getId(), user.getPrenom(), "Télécollecte", f, "error");
                                                                                        //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(MenuRetraitTelecollecte.this, "Ce numéro de Carte n'existe pas", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }
                                                                            else{
                                                                                Toast.makeText(MenuRetraitTelecollecte.this, "Impossible d'envoyer votre notification", Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                                    //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
                                                                    LocalNotification("Télécollecte", f);


                                                                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                                                    dbHandler = new DbHandler(getApplicationContext());
                                                                    aujourdhui = new Date();
                                                                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                                                    dbHandler.insertUserDetails("Télécollecte",f, "0", R.drawable.ic_notifications_red_48dp,shortDateFormat.format(aujourdhui));




                                                                }
                                                            }

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
                                                                    Toast.makeText(MenuRetraitTelecollecte.this, jsonObject.getString("telephone"), Toast.LENGTH_SHORT).show();
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
                                                            // Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            //Check si la connexion existe
                                                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                                            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                                                            if(!(activeInfo != null && activeInfo.isConnected())){
                                                                progressDialog.dismiss();
                                                                telecollecteAccepteur.setVisibility(View.GONE);
                                                                internetIndisponible.setVisibility(View.VISIBLE);
                                                                Toast.makeText(MenuRetraitTelecollecte.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                                                            } else{
                                                                progressDialog.dismiss();
                                                                telecollecteAccepteur.setVisibility(View.GONE);
                                                                internetIndisponible.setVisibility(View.VISIBLE);
                                                                conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                                                titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                                                //msgNetworkLimited.setText();
                                                                Toast.makeText(MenuRetraitTelecollecte.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                    try {
                                                        Thread.sleep(2000);
                                                        //Toast.makeText(MenuRechargeTelecollecte.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                                                    } catch (InterruptedException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                    progressDialog.dismiss();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();

                                        //FIN -------
                                        final List<ScoreData> scores = databaseManager.readTop10();
                                        for ( ScoreData score : scores ) {
                                            // montantActuel.setText("montant  "+score.getMontant()+" fcfa");
                                        }

                                    }
                                    else{
                                        Toast.makeText(MenuRetraitTelecollecte.this, "Impossible de lire la carte", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        //final String idCard = StringUtil.toHexString(uid);
                                        try {
                                            nfc.close();
                                        } catch (TelpoException e) {
                                            e.printStackTrace();
                                        }
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
        });

        btnReessayer.setOnClickListener(this::checkNetworkConnectionStatus);
    }


    public void checkNetworkConnectionStatus(View view) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if(activeInfo != null && activeInfo.isConnected()){

            ProgressDialog dialog = ProgressDialog.show(this, "Connexion", "Encours...", true);
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
            telecollecteAccepteur.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(MenuRetraitTelecollecte.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }


    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
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
            idCarteCacheTelecollecte.setText(StringUtil.toHexString(data));
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

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
        try {
            nfc.close();
        } catch (TelpoException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }




    public void LocalNotification(String titles, String subtitles){

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

        expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.logo2);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo2)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
    }

    private void RemoteNotification(final String receiver, final String username, final String title, final String message, final String etat_notif){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(fuser.getUid(), R.drawable.logo2, username + ": " + message, title, receiver, etat_notif);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(MenuRetraitTelecollecte.this, "Failed", Toast.LENGTH_SHORT).show();
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
}
