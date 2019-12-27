package com.ezpass.smopaye_mobile.vuesAccepteur;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.Tutoriel;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
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
import java.util.Timer;

import static android.content.ContentValues.TAG;

public class ConsulterSolde extends AppCompatActivity {

    private Button btn, consulter;
    private EditText editText;
    private  AlertDialog.Builder build, build_error;
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
    final String card = "";
    private ProgressDialog progressDialog;

    String file = "tmp_number";
    int c;
    String tmp_number = "";

    LinearLayout internetIndisponible, authWindows;
    Button btnReessayer;
    ImageView conStatusIv;
    TextView titleNetworkLimited, msgNetworkLimited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_solde);

        getSupportActionBar().setTitle("Vérifier son solde");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(ConsulterSolde.this);
        build_error = new AlertDialog.Builder(ConsulterSolde.this);

        btn = (Button)findViewById(R.id.soldeBtn);
        consulter = (Button) findViewById(R.id.consulter);
        editText = (EditText)findViewById(R.id.solde);

        authWindows = (LinearLayout) findViewById(R.id.authWindows);
        internetIndisponible = (LinearLayout) findViewById(R.id.internetIndisponible);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);




        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = openFileInput(file);
            while ((c = fIn.read()) != -1){
                tmp_number = tmp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        btn.setOnClickListener(new View.OnClickListener() {

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
                readThread = new ConsulterSolde.ReadThread();
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

                                    new AlertDialog.Builder(ConsulterSolde.this)
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
                                    //editText.setText(StringUtil.toHexString(uid));
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

        });


        consulter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals(""))
                {
                    View view = LayoutInflater.from(ConsulterSolde.this).inflate(R.layout.alert_dialog_success, null);
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
                else{
                    Toast.makeText(ConsulterSolde.this, editText.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                    //-----------DEBUT
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
                                final Uri.Builder builder = new Uri.Builder();

                                builder.appendQueryParameter("aze","Card");
                                builder.appendQueryParameter("qsd", "consulsolde");
                                builder.appendQueryParameter("CARDN",editText.getText().toString().trim());
                                //utilisation du numero de telephone annulée
                                //builder.appendQueryParameter("tel", tmp_number);
                                builder.appendQueryParameter("uniquser", tmp_number);
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
                                        Toast.makeText(ConsulterSolde.this, "Encours de traitement...", Toast.LENGTH_SHORT).show();
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

                                        int pos = f.toLowerCase().indexOf("solde");
                                        if (pos >= 0) {
                                            View view = LayoutInflater.from(ConsulterSolde.this).inflate(R.layout.alert_dialog_success, null);
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

                                            progressDialog.dismiss();
                                            Toast.makeText(ConsulterSolde.this, f, Toast.LENGTH_LONG).show();
                                            editText.setText("");
                                        }
                                        else{
                                            View view = LayoutInflater.from(ConsulterSolde.this).inflate(R.layout.alert_dialog_success, null);
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

                                            progressDialog.dismiss();
                                            Toast.makeText(ConsulterSolde.this, f, Toast.LENGTH_LONG).show();
                                            editText.setText("");
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
                                                Toast.makeText(ConsulterSolde.this, jsonObject.getString("montant"), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(ConsulterSolde.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                                        } else{
                                            progressDialog.dismiss();
                                            authWindows.setVisibility(View.GONE);
                                            internetIndisponible.setVisibility(View.VISIBLE);
                                            conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                            titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                            //msgNetworkLimited.setText();
                                            Toast.makeText(ConsulterSolde.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                e.printStackTrace();
                                try {
                                    Thread.sleep(2000);
                                    // Toast.makeText(Consulter_Solde.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //-----------FIN
                }

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
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(ConsulterSolde.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
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
            editText.setText(StringUtil.toHexString(data));
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
}
