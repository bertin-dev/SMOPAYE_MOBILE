package com.ezpass.smopaye_mobile.vuesAccepteur;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class VerifierNumCarte extends AppCompatActivity {


    LinearLayout showNumCarte;
    Button id_carte;
    EditText carte, valeurAbonnement, dateExpi;
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
    private ProgressDialog progressDialog;


    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_data_user";
    int c;
    String temp_data_user = "";
    String annee, mois, jour, abonn, currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifier_num_carte);


        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.numeroCarte));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_carte = (Button) findViewById(R.id.btnPasserCarte);
        showNumCarte = (LinearLayout) findViewById(R.id.afficheNumCarte);
        carte = (EditText) findViewById(R.id.id_carte);
        valeurAbonnement = (EditText) findViewById(R.id.valeurAbonnement);
        dateExpi = (EditText) findViewById(R.id.dateExpi);

        progressDialog = new ProgressDialog(VerifierNumCarte.this);


        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_data_user = temp_data_user + Character.toString((char)c);
            }
            String[] parts = temp_data_user.split("-");
             annee = parts[5]; //2019
             mois = parts[6]; //11
             jour = parts[7]; // 10
             abonn = parts[11]; // service, hebdomadaire, mensuel
        }
        catch (Exception e){
            e.printStackTrace();
        }



        //TRAITEMENT DE LA DATE D'EXPIRATION
        String times = jour + "/" + mois + "/" + annee;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d = simpleDate.parse(times);
             currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(d.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        valeurAbonnement.setText(abonn);
        dateExpi.setText(currentDate);

        id_carte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(showNumCarte.getVisibility() == View.GONE){
                    showNumCarte.setVisibility(View.VISIBLE);
                }
                else{
                    showNumCarte.setVisibility(View.GONE);
                }*/
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
                readThread = new VerifierNumCarte.ReadThread();
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

                            new AlertDialog.Builder(VerifierNumCarte.this)
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

                            progressDialog.dismiss();
                            if(showNumCarte.getVisibility() == View.GONE){
                               // carte.setText(StringUtil.toHexString(uid));
                                m1CardAuthenticate();
                                showNumCarte.setVisibility(View.VISIBLE);
                            }
                            else{
                                showNumCarte.setVisibility(View.GONE);
                            }

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
            valeurAbonnement.setText(abonn);
            dateExpi.setText(currentDate);
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
        try {
            nfc.close();
        } catch (TelpoException e) {
            e.printStackTrace();
        }
        super.onBackPressed();
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
