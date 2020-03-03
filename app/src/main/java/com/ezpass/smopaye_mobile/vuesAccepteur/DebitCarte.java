package com.ezpass.smopaye_mobile.vuesAccepteur;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.Assistance.MessageActivity;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Login;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.telpo.emv.EmvService;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.printer.UsbThermalPrinter;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class DebitCarte extends AppCompatActivity {

    private Button debitCarte, onMakePaymentPress;
    private AlertDialog.Builder build, build_error;
    private EditText inputAmt, carte;
    private String Amount;
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
    private DatabaseManager databaseManager;
    private ProgressDialog progressDialog;
    MediaPlayer FAILplayer;
    String telephone;
    private TelephonyManager telephonyManager;
    //private String sn;
    private String serialNumber;


    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;


    //SERVICES GOOGLE FIREBASE
    APIService apiService;
    FirebaseUser fuser;


    ImageView conStatusIv;
    TextView titleNetworkLimited, msgNetworkLimited;
    private LinearLayout internetIndisponible, authWindows;
    private Button btnReessayer;

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit_carte);


        getSupportActionBar().setTitle(getString(R.string.numeroCarte));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //SERVICE GOOGLE FIREBASE
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        debitCarte = (Button) findViewById(R.id.btndebit);
        carte = (EditText) findViewById(R.id.debicarte);
        inputAmt = (EditText) findViewById(R.id.debitmontant);

        authWindows = (LinearLayout) findViewById(R.id.authWindows);
        internetIndisponible = (LinearLayout) findViewById(R.id.internetIndisponible);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);


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

        /*Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("id_carte")
                .equalTo("12345");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals("12345")) {
                            RemoteNotification(user.getId(), user.getPrenom(), "Debit Carte", "Debit effectué avec success");
                            //sendNotification(fuser.getUid(), user.getNom(), "Debit effctue avec success");
                            Toast.makeText(DebitCarte.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DebitCarte.this, "CARTE INEXISTANTE", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(DebitCarte.this, "impossible", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals("12345")) {
                            recepteurIdCarte = user.getId_carte();
                            sendNotification(user.getId(), user.getNom(), "Debit effectué avec success");
                            //sendNotification(fuser.getUid(), user.getNom(), "Debit effctue avec success");
                            Toast.makeText(DebitCarte.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DebitCarte.this, "CARTE INEXISTANTE", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(DebitCarte.this, "impossible", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



        query.addListenerForSingleValueEvent(valueEventListener);*/



        TextView montantAct = (TextView) findViewById(R.id.montantTotaliser);
        TextView nbrCarte = (TextView) findViewById(R.id.nbrCarte);
        FAILplayer = MediaPlayer.create(DebitCarte.this, R.raw.fail1);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //sn = telephonyManager.getImei();
         //serialNumber = Build.SERIAL != Build.UNKNOWN ? Build.SERIAL : Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // inputAmt.setSelection(inputAmt.getText().length());

        //Toast.makeText(DebitCarte.this,sn,Toast.LENGTH_LONG).show();
        //debitCard.setEnabled(false);
        databaseManager = new DatabaseManager(this);
        build = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(DebitCarte.this);
        List<ScoreData> scores = databaseManager.readTop10();
        //carte.setText("9CD01FA3 ");

        int nbrC = databaseManager.nbrCarte();
        nbrCarte.setText(""+nbrC);
        for ( ScoreData score : scores ) {
            montantAct.setText("montant  "+score.getMontant()+" FCFA");
            montantAct.setText(+score.getMontant()+" FCFA");
            // Toast.makeText(this, score.getMontant(), Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(this, nbrC, Toast.LENGTH_SHORT).show();




        debitCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* build_error = new AlertDialog.Builder(DebitCarte.this);
                View view = LayoutInflater.from(DebitCarte.this).inflate(R.layout.alert_dialog_success, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                title.setText("Information");
                imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                statutOperation.setText("Opération Effectuée avec success");
                build_error.setPositiveButton("OK", null);
                build_error.setCancelable(false);

                build_error.setView(view);
                build_error.show();*/

               //numero inutilisé dans le debit
                Intent in = getIntent();
                telephone = in.getStringExtra("telephone");
                if(inputAmt.getText().toString().equalsIgnoreCase("")){
                    FAILplayer.start();
                    Toast.makeText(DebitCarte.this, "Veuillez Entrer le Montant", Toast.LENGTH_SHORT).show();
                }
                else{

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
                    readThread = new DebitCarte.ReadThread();
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

                                        new AlertDialog.Builder(DebitCarte.this)
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
                                        //carte.setText(StringUtil.toHexString(uid));
                                        m1CardAuthenticate();
                                        progressDialog.dismiss();
                                        try {
                                            nfc.close();
                                        } catch (TelpoException e) {
                                            e.printStackTrace();
                                        }
                                        //DEBUT

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {

                                                    //********************DEBUT***********
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // On ajoute un message à notre progress dialog
                                                            progressDialog.setMessage(getString(R.string.information));
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


                                                    final Uri.Builder builder = new Uri.Builder();


                                                    builder.appendQueryParameter("auth","Card");
                                                    builder.appendQueryParameter("login", "DebitCard");
                                                    builder.appendQueryParameter("idTelephone", getSerialNumber().trim());
                                                    //builder.appendQueryParameter("TELEPHONE", temp_number);
                                                    builder.appendQueryParameter("uniquser", temp_number);
                                                    builder.appendQueryParameter("CARDN",carte.getText().toString().trim());
                                                    builder.appendQueryParameter("MONTANT",inputAmt.getText().toString().trim());
                                                    //builder.appendQueryParameter("DebitCarte", "DebitCarte");
                                                    builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                                                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

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
                                                            Toast.makeText(DebitCarte.this, getString(R.string.encoursTraitement), Toast.LENGTH_SHORT).show();
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
                                                            String[] parts = f.split("-");
                                                            if(parts[0].equals(f)){
                                                                /*for ( ScoreData score : scores ) {
                                                                    build.setMessage(f);
                                                                    build.setCancelable(true);
                                                                    AlertDialog dialog = build.create();
                                                                    dialog.show();
                                                                    Toast.makeText(DebitCarte.this, f, Toast.LENGTH_LONG).show();
                                                                }*/

                                                                FAILplayer.start();
                                                                build_error = new AlertDialog.Builder(DebitCarte.this);
                                                                View view = LayoutInflater.from(DebitCarte.this).inflate(R.layout.alert_dialog_success, null);
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



                                                                /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                                                //SERVICE GOOGLE FIREBASE
                                                                final String id_carte_sm = carte.getText().toString().trim();

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
                                                                                    RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.debitCarte), f, "error");
                                                                                    //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                                                } else {
                                                                                    Toast.makeText(DebitCarte.this, getString(R.string.numCompteExistPas), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        }
                                                                        else{
                                                                            Toast.makeText(DebitCarte.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });


                                                                ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                                                dbHandler = new DbHandler(getApplicationContext());
                                                                aujourdhui = new Date();
                                                                shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                                                dbHandler.insertUserDetails(getString(R.string.debitCarte), f, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));


                                                                //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
                                                                LocalNotification(getString(R.string.debitCarte), f);



                                                            }else{
                                                                String nom = parts[0]; // Nom
                                                                String somme = parts[1]; // Montant

                                                                if(estUnEntier(somme)){


                                                                    //INSERTION DU MONTANT DEBITE DANS LA BASE DE DONNEES
                                                                    databaseManager.insertScore(carte.getText().toString().trim(), Integer.parseInt(inputAmt.getText().toString()));
                                                                    final List<ScoreData> scores = databaseManager.readTop10();
                                                                    for (ScoreData score : scores) {
                                                                        // carte.append(score.getMontant() + " ");
                                                                    }
                                                                    // databaseManager.insertScore(carte.getText().toString().trim(), Integer.parseInt(inputAmt.getText().toString()));

                                                                    // Amount=inputAmt.getText().toString();


                                                                    final int montant = Integer.parseInt(somme);
                                                                    MediaPlayer OKplayer = MediaPlayer.create(DebitCarte.this, R.raw.success1);
                                                                    OKplayer.start();
                                                                    build_error = new AlertDialog.Builder(DebitCarte.this);
                                                                    View view = LayoutInflater.from(DebitCarte.this).inflate(R.layout.alert_dialog_success, null);
                                                                    TextView title = (TextView) view.findViewById(R.id.title);
                                                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                                    title.setText(getString(R.string.information));
                                                                    imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                                                    statutOperation.setText(getString(R.string.transaction_success));
                                                                    build_error.setPositiveButton("OK", null);
                                                                    build_error.setCancelable(false);

                                                                    build_error.setView(view);
                                                                    build_error.show();
                                                                    Toast.makeText(DebitCarte.this,getString(R.string.monsieur) + " " + nom +" "+getString(R.string.votreCompteDebite) + montant + " FCFA", Toast.LENGTH_SHORT).show();



                                                                    /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                                                    //SERVICE GOOGLE FIREBASE
                                                                    final String id_carte_sm = carte.getText().toString().trim();
                                                                    final String nom1 = nom;

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
                                                                                        RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.votreCompteDebite), getString(R.string.compteDe) + " " + nom1 + " " + getString(R.string.aEteDebite) + montant + " FCFA", "success");
                                                                                        //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(DebitCarte.this, getString(R.string.numCompteExistPas), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }
                                                                            else{
                                                                                Toast.makeText(DebitCarte.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });


                                                                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                                                    dbHandler = new DbHandler(getApplicationContext());
                                                                    aujourdhui = new Date();
                                                                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                                                    dbHandler.insertUserDetails(getString(R.string.debitCarte),getString(R.string.compteDe)+ " " + nom +  getString(R.string.aEteDebite)+ " " + montant + " FCFA", "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


                                                                    //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                                                    LocalNotification(getString(R.string.debitCarte), getString(R.string.compteDe)+ " " + nom + getString(R.string.aEteDebite)+ " " + montant + " FCFA");


                                                                    //vider le champs Montant
                                                                    inputAmt.setText("");
                                                                     /*int result = printer(nom, 4, 01, carte.getText().toString().trim(), montant, "30/05/2019", "20", "1414");

                                                                    if(result == 100){
                                                                        Toast.makeText(DebitCarte.this, "Monsieur " + nom + " Votre Compte a été Débité de " + montant + " FCFA", Toast.LENGTH_SHORT).show();

                                                                        build_error = new AlertDialog.Builder(DebitCarte.this);
                                                                        View view = LayoutInflater.from(DebitCarte.this).inflate(R.layout.alert_dialog_success, null);
                                                                        TextView title = (TextView) view.findViewById(R.id.title);
                                                                        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                                        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                                        title.setText(getString(R.string.information));
                                                                        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                                                        statutOperation.setText(getString(R.string.transaction_success));
                                                                        build_error.setPositiveButton("OK", null);
                                                                        build_error.setCancelable(false);

                                                                        build_error.setView(view);
                                                                        build_error.show();

                                                                        notifications("Débit Carte", "Monsieur " + nom + " Votre Compte a été Débité de " + montant + " FCFA");

                                                                    }else{
                                                                        FAILplayer.start();
                                                                        build_error = new AlertDialog.Builder(DebitCarte.this);
                                                                        View view = LayoutInflater.from(DebitCarte.this).inflate(R.layout.alert_dialog_success, null);
                                                                        TextView title = (TextView) view.findViewById(R.id.title);
                                                                        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                                        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                                        title.setText(getString(R.string.information));
                                                                        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                                                                        statutOperation.setText("Une Erreur est survenue");
                                                                        build_error.setPositiveButton("OK", null);
                                                                        build_error.setCancelable(false);

                                                                        build_error.setView(view);
                                                                        build_error.show();

                                                                        notifications("Débit Carte", "Une Erreur est survenue pendant l'opération");
                                                                    }*/

                                                                }else{
                                                                    FAILplayer.start();
                                                                    Toast.makeText(DebitCarte.this, getString(R.string.impossibleConvertir), Toast.LENGTH_LONG).show();
                                                                    /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                                                    //SERVICE GOOGLE FIREBASE
                                                                    final String id_carte_sm = carte.getText().toString().trim();

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
                                                                                        RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.debitCarte), f, "error");
                                                                                        //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(DebitCarte.this,  getString(R.string.numCompteExistPas), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }
                                                                            else{
                                                                                Toast.makeText(DebitCarte.this,  getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });


                                                                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                                                    dbHandler = new DbHandler(getApplicationContext());
                                                                    aujourdhui = new Date();
                                                                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                                                    dbHandler.insertUserDetails( getString(R.string.debitCarte),f, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));


                                                                    //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
                                                                    LocalNotification( getString(R.string.debitCarte), f);
                                                                }
                                                            }

                                                            //build.setPositiveButton("ok", new View.OnClickListener()
                                                            // Toast.makeText(DebitCarte.this, f, Toast.LENGTH_LONG).show();
                                                            //int montant = Integer.parseInt(f);
                                                            //printer("bertin", 4, 01, carte.getText().toString().trim(), montant, "28/05/2019", "20", "1414");


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
                                                                    Toast.makeText(DebitCarte.this, jsonObject.getString("montant"), Toast.LENGTH_SHORT).show();
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
                                                                authWindows.setVisibility(View.GONE);
                                                                internetIndisponible.setVisibility(View.VISIBLE);
                                                                Toast.makeText(DebitCarte.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                                                            } else{
                                                                progressDialog.dismiss();
                                                                authWindows.setVisibility(View.GONE);
                                                                internetIndisponible.setVisibility(View.VISIBLE);
                                                                conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                                                titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                                                //msgNetworkLimited.setText();
                                                                Toast.makeText(DebitCarte.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                    e.printStackTrace();
                                                    try {
                                                        Thread.sleep(2000);
                                                        //Toast.makeText(DebitCarte.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                                                    } catch (InterruptedException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                    progressDialog.dismiss();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        //databaseManager.viderdata();
                                        //retrait.setText("");
                                        // retraitNumcarte.setText("");

                                        //FIN DE SINON

                                        //FIN

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

                //LECTURE DU TABLEAU DE BORD APRES UN CLIQUE
               /* nbrCarte.setText(""+nbrC);
                for ( ScoreData score : scores ) {
                    montantAct.setText("montant  "+score.getMontant()+" FCFA");
                    montantAct.setText(+score.getMontant()+" FCFA");

                }*/
            }
        });

        btnReessayer.setOnClickListener(this::checkNetworkConnectionStatus);
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

    public void checkNetworkConnectionStatus(View view) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if(activeInfo != null && activeInfo.isConnected()){

            ProgressDialog dialog = ProgressDialog.show(this,  getString(R.string.connexion),  getString(R.string.encours), true);
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
            Toast.makeText(DebitCarte.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
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
            carte.setText(StringUtil.toHexString(data));
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


    //methode permmettant l'impression
    private int printer(String merchantName, int merchantNum, int terminalNum, String cardNum1, int Amount1, String expDate, String batchNum, String referNum) {
        MediaPlayer OKplayer;
        OKplayer = MediaPlayer.create(DebitCarte.this, R.raw.success1);

        MediaPlayer stopPlayer;
        MediaPlayer rejectPlayer;
        MediaPlayer FAILplayer;

        FAILplayer = MediaPlayer.create(DebitCarte.this, R.raw.fail1);
        stopPlayer = MediaPlayer.create(DebitCarte.this, R.raw.trans_stop1);
        rejectPlayer = MediaPlayer.create(DebitCarte.this, R.raw.trans_reject1);

        Bitmap bitmap;
		/*WritePadDialog writePadDialog;
		writePadDialog=new WritePadDialog(NfcActivity_tps900.this, new DialogListener() {
			@Override
			public void refreshActivity(Object object) {
				bitmap=(Bitmap)object;
				bitmap= ThumbnailUtils.extractThumbnail(bitmap,360,256);
			}
		});*/

        Context context;
        context = DebitCarte.this;

        int ret;
        EmvService emvService;
        UsbThermalPrinter usbThermalPrinter = new UsbThermalPrinter(DebitCarte.this);

        boolean userCancel = false;
        long startMs;
        boolean isSupportNfc = true;
        /*------------------------DEBUT------------------------*/


        OKplayer.start();
        try {
            usbThermalPrinter.start(1);
            usbThermalPrinter.reset();
            usbThermalPrinter.setMonoSpace(true);
            usbThermalPrinter.setGray(7);
            usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
            Bitmap bitmap1 = BitmapFactory.decodeResource(DebitCarte.this.getResources(), R.drawable.logo_moy);
            Bitmap bitmap2 = ThumbnailUtils.extractThumbnail(bitmap1, 244, 116);
            usbThermalPrinter.printLogo(bitmap2, true);

            usbThermalPrinter.setTextSize(30);
            usbThermalPrinter.addString("POS SALES SLIP\n");
            usbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
            usbThermalPrinter.setTextSize(24);
            usbThermalPrinter.addString("MERCHANT NAME:           " + merchantName);//SMOPAYE
            usbThermalPrinter.addString("MERCHANT NO:                  " + merchantNum);
            usbThermalPrinter.addString("TERMINAL NO:                  " + terminalNum);
            int i = usbThermalPrinter.measureText("CARD NO:" + cardNum1);
            int i1 = usbThermalPrinter.measureText(" ");
            int SpaceNumber = (384 - i) / i1;
            String spaceString = "";
            for (int j = 0; j < SpaceNumber; j++) {
                spaceString += " ";
            }

            usbThermalPrinter.addString("CARD NO:" + spaceString + cardNum1);
            usbThermalPrinter.addString("TRANS TYPE:                GOODS");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            usbThermalPrinter.addString("DATE/TIME:   " + str);
            usbThermalPrinter.addString("EXP DATE:             " + expDate);//format de date: 2019-12-30
            usbThermalPrinter.addString("BATCH NO:             " + batchNum);//format 2019000168
            usbThermalPrinter.addString("REFER NO:             " + referNum);//format 2019001232
            i = usbThermalPrinter.measureText("AMOUNT:" + "XAF " + Amount1);
            i1 = usbThermalPrinter.measureText(" ");
            SpaceNumber = (384 - i) / i1;
            spaceString = "";
            for (int j = 0; j < SpaceNumber; j++) {
                spaceString += " ";
            }
            usbThermalPrinter.addString("AMOUNT:" + spaceString + "XAF " + Amount1);
            usbThermalPrinter.addString("CARD HOLDER SIGNATURE:");
            usbThermalPrinter.printLogo(bitmap1, true);
            usbThermalPrinter.printString();
            usbThermalPrinter.walkPaper(10);
        } catch (TelpoException e) {
            e.printStackTrace();
        } finally {
            usbThermalPrinter.stop();
        }
        return 100;
        /*-----------------------------FIN-------------------*/
    }

    public boolean estUnEntier(String chaine) {
        try {
            Integer.parseInt(chaine);
        } catch (NumberFormatException e){
            return false;
        }

        return true;
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
                                            Toast.makeText(DebitCarte.this,  getString(R.string.echoue), Toast.LENGTH_SHORT).show();
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
