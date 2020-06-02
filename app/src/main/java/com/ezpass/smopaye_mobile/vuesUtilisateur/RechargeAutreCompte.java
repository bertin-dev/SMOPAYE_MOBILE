package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;

import static android.content.ContentValues.TAG;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class RechargeAutreCompte extends AppCompatActivity {

    public Button btnOpenNFC,rbtnRechargerAutreCompte;
    private EditText numCarteDonataire,numCarte,montant, idchampCache;
    private  AlertDialog.Builder build, build_error;
    private ProgressDialog progressDialog;
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

    /*NOTIFICATION*/
    private NotificationManagerCompat notificationManager;

    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_autre_compte);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.rechargeCompte));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        numCarteDonataire = (EditText) findViewById(R.id.rnumCarteDonataire);
        numCarte = (EditText) findViewById(R.id.rnumCarteBeneficiaire);
        montant = (EditText)findViewById(R.id.montant);
        idchampCache = (EditText) findViewById(R.id.idchampCache);

        btnOpenNFC = (Button)findViewById(R.id.rbtnOpenNFC);
        rbtnRechargerAutreCompte =(Button)findViewById(R.id.rbtnRechargeAutreCompte);
        build = new AlertDialog.Builder(this);
        build_error = new AlertDialog.Builder(RechargeAutreCompte.this);

        progressDialog = new ProgressDialog(RechargeAutreCompte.this);
        //////////////////////////////////////////////////////

        //  openDialog();
        DialogInterface dialog;

        btnOpenNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
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
                    try {
                        Thread.sleep(2000);
                        //Toast.makeText(RechargeAutreCompte.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
                readThread = new RechargeAutreCompte.ReadThread();
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

                            new AlertDialog.Builder(RechargeAutreCompte.this)
                                    .setMessage(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                            "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                            "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi))
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

                          /*  new AlertDialog.Builder(Recharge.this)
                                   .setMessage(getString(R.string.card_type) + getString(R.string.type_a) + " " + type +
                                            "\r\n" + getString(R.string.atqa_data) + StringUtil.toHexString(atqa) +
                                            "\r\n" + getString(R.string.sak_data) + StringUtil.toHexString(sak) +
                                            "\r\n" + getString(R.string.uid_data) + StringUtil.toHexString(uid))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();*/

                            if(numCarteDonataire.getText().toString().trim().equals("")){
                                //numCarteDonataire.setText(StringUtil.toHexString(uid));
                                m1CardAuthenticateDonataire();
                                numCarte.setEnabled(true);
                                progressDialog.dismiss();
                                try {
                                    nfc.close();
                                } catch (TelpoException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{

                                //permet de charger l'id N°2 dans un champs caché
                                m1CardAuthenticate();

                                if(!numCarteDonataire.getText().toString().trim().equals(idchampCache.getText().toString().trim())){
                                    //numCarte.setText(StringUtil.toHexString(uid));
                                    m1CardAuthenticate3();
                                    progressDialog.dismiss();
                                    try {
                                        nfc.close();
                                    } catch (TelpoException e) {
                                        e.printStackTrace();
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e1) {
                                            e1.printStackTrace();
                                        }
                                        progressDialog.dismiss();
                                    }
                                }else {
                                    Toast.makeText(RechargeAutreCompte.this, "N°Carte Identique", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    try {
                                        nfc.close();
                                    } catch (TelpoException e) {
                                        e.printStackTrace();
                                    }
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


        rbtnRechargerAutreCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numCarteDonataire.getText().length() > 0 && numCarte.getText().length() > 0 && montant.getText().length() > 0) {
                    //recharge("http://192.168.20.11:1234/recharge_autre.php");
                    recharge(ChaineConnexion.getAdresseURLsmopayeServer());
                }else{
                    View view = LayoutInflater.from(RechargeAutreCompte.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.champsVide));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();
                }
            }
        });


    }



    public  void recharge(final String adresse){
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
                    builder.appendQueryParameter("numdon",numCarteDonataire.getText().toString()); //numdon (celui qui offre )
                    builder.appendQueryParameter("numcarte", numCarte.getText().toString().trim());//celui qui reçoit ou beneficiaire
                    builder.appendQueryParameter("montant",montant.getText().toString());
                    builder.appendQueryParameter("typeTransfer", "Recharges");


                    URL url = new URL(adresse+builder.build().toString());//"http://192.168.20.11:1234/recharge.php"
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
                            Toast.makeText(RechargeAutreCompte.this, getString(R.string.encoursTraitement), Toast.LENGTH_SHORT).show();
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
                            notifications(getString(R.string.rechargeUneAutreCarte), f);
                            //dbHandler.insertUserDetails("Recharge Autre Compte",f, "0", shortDateFormat.format(aujourdhui));


                            View view = LayoutInflater.from(RechargeAutreCompte.this).inflate(R.layout.alert_dialog_success, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                            title.setText(getString(R.string.etatCompte));
                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            statutOperation.setText(f);
                            build_error.setPositiveButton("OK", null);
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();

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
                                    Toast.makeText(RechargeAutreCompte.this, jsonObject.getString("telephone"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RechargeAutreCompte.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void notifications(String titles, String subtitles){

        ///////////////DEBUT NOTIFICATIONS///////////////////////////////
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
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
                .setVibrate(new long[]{0,1000,500,1000})
                .build();

        notificationManager.notify(1, notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
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

    public void m1CardAuthenticateDonataire() {
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
            readValueDataCourtDonataire();
        } else {
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "m1CardAuthenticate fail!");
        }
    }

    public void m1CardAuthenticate3() {
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
            readValueDataCourt3();
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
            idchampCache.setText(StringUtil.toHexString(data));
        }
    }

    public void readValueDataCourtDonataire() {
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
            numCarteDonataire.setText(StringUtil.toHexString(data));
        }
    }

    public void readValueDataCourt3() {
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
}
