package com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveBD extends AppCompatActivity {

    private static final String TAG = "SaveBD";
    private Context context;
    ///////////////////////////////////////////
    private EditText uid_editText = null;
    private Thread readThread;
    private Handler handler;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    private byte blockNum_1 = 1;
    private byte blockNum_2 = 2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    private Nfc nfc = new Nfc(this);
    private long time1, time2;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;
    private String myId_card = "10";  //PAS ENCORE INITIALISER AVEC UNE VALEUR

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private TokenManager tokenManager;
    private Call<AllMyResponse> call;
    //DatePicker expire_date;
    private EditText expire_date;

    @BindView(R.id.numCartePriveAutoSaveBD)
    EditText numCartePriveAutoSaveBD;
    @BindView(R.id.numCartePublicAutoSaveBD)
    EditText numCartePublicAutoSaveBD;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Toolbar toolbar;



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_save_bd);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.writeInCard));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this);
        context = this;
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        progressDialog = new ProgressDialog(SaveBD.this);
        build_error = new AlertDialog.Builder(SaveBD.this);

        expire_date = (EditText) findViewById(R.id.expire_date);

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

                            /*new AlertDialog.Builder(EnregCarte.this)
                                    .setMessage(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                            "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                            "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();*/

                            uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                    "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                    "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi));

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

                          /*  new AlertDialog.Builder(Manage_Recharge.this)
                                   .setMessage(getString(R.string.card_type) + getString(R.string.type_a) + " " + type +
                                            "\r\n" + getString(R.string.atqa_data) + StringUtil.toHexString(atqa) +
                                            "\r\n" + getString(R.string.sak_data) + StringUtil.toHexString(sak) +
                                            "\r\n" + getString(R.string.uid_data) + StringUtil.toHexString(uid))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();*/

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }

    @OnClick(R.id.BtnSaveCarte)
    void save_data(){

        String id_card = numCartePriveAutoSaveBD.getText().toString();
        String serial_card = numCartePublicAutoSaveBD.getText().toString();
        //expire_date.init(2021, 11, 30, null);
        //String exp = expire_date.getYear() + "-" + (expire_date.getMonth() + 1) + "-" +  expire_date.getDayOfMonth();
        String exp = expire_date.getText().toString().trim();

        if(id_card.trim().equalsIgnoreCase("") || serial_card.trim().equalsIgnoreCase("")){
            Toast.makeText(SaveBD.this, getString(R.string.champsVide), Toast.LENGTH_SHORT).show();
        }
        else {
            if(id_card.trim().equalsIgnoreCase("00000000") || serial_card.trim().equalsIgnoreCase("00000000")) {
                errorResponse(getString(R.string.cardNotReset));
                Toast.makeText(SaveBD.this, getString(R.string.cardNotReset), Toast.LENGTH_SHORT).show();
            }
            else {
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
                insertDataInDB(id_card, serial_card, exp, myId_card);
            }
        }
    }

    @OnClick(R.id.BtnPasserCarteSaveBDAuto)
    void openNFC(){
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
            readThread = new ReadThread();
            readThread.start();

        } catch (TelpoException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            nfc.close();
        } catch (TelpoException e) {
            e.printStackTrace();
        }

        if(call != null){
            call.cancel();
            call = null;
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
            readBlockData();

            //OwriteValueData();
            readValueDataCourt();
        } else {
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "m1CardAuthenticate fail!");
        }
    }



   private void readBlockData() {
        byte[] data = null;
        try {

            time1 = System.currentTimeMillis();
            data = nfc.m1_read_block(blockNum_1);
            time2 = System.currentTimeMillis();
            Log.e("yw read_block", (time2 - time1) + "");


        } catch (TelpoException e) {
            e.printStackTrace();
        }

        if (data == null) {
            Log.e(TAG, "readBlockBtn fail!");
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
        } else {
            numCartePublicAutoSaveBD.setText(StringUtil.toHexString(data));
        }
    }

    /*public void writeBlockData() {
        byte[] blockData = null;
        String blockStr;
        Boolean status = true;

        Log.d(TAG, "writeBlockData...");
        blockStr = numCartePublicAuto.getText().toString();
        blockData = toByteArray(blockStr);

        try {
            nfc.m1_write_block(blockNum_1, blockData, blockData.length);
        } catch (TelpoException e) {
            status = false;
            Log.e("yw", e.toString());
            e.printStackTrace();
        }

        if (status) {
            Log.d(TAG, "writeBlockData success!");
            Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "writeBlockData fail!");
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public void writeValueDataCourt() {
        byte[] valueData = null;
        String valueStr;
        boolean status = true;

        Log.d(TAG, "writeValueBtn...");
        valueStr = numCartePriveAuto.getText().toString();
        valueData = toByteArray(valueStr);

        try {
            nfc.m1_write_value(blockNum_2, valueData, valueData.length);
        } catch (TelpoException e) {
            status = false;
            e.printStackTrace();
        }

        if (status) {
            Log.d(TAG, "writeValueData success!");
            Toast.makeText(this, getString(R.string.operation_succss), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "writeValueData fail!");
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public static byte[] toByteArray(String hexString) {
        int hexStringLength = hexString.length();
        byte[] byteArray = null;
        int count = 0;
        char c;
        int i;

        // Count number of hex characters
        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        boolean first = true;
        int len = 0;
        int value;
        for (i = 0; i < hexStringLength; i++) {
            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {
                    byteArray[len] = (byte) (value << 4);
                } else {
                    byteArray[len] |= value;
                    len++;
                }
                first = !first;
            }
        }
        return byteArray;
    }

*/

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
            numCartePriveAutoSaveBD.setText(StringUtil.toHexString(data));
        }
    }


    private void insertDataInDB(String code_number, String serial_number, String end_date, String created_by){

        call = service.createCard(code_number, serial_number, end_date, Integer.parseInt(created_by));
        call.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()){

                    if(response.body().isSuccess()){
                        //tokenManager.saveToken(response.body());
                        successResponse(response.message());
                    } else {
                        errorResponse(response.errorBody().toString());
                    }
                } else{
                        errorResponse(response.errorBody().toString());
                }

            }

            @Override
            public void onFailure(Call<AllMyResponse> call, Throwable t) {

                progressDialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
            }
        });
    }


    private void successResponse(String response) {

        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    private void errorResponse(String response){
        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
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

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));

            TextView automatique = (TextView) findViewById(R.id.automatique);
            automatique.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            automatique.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));

            TextView num_serie_auto = (TextView) findViewById(R.id.num_serie_auto);
            num_serie_auto.setTextColor(getResources().getColor(R.color.colorPrimaryRed));

            TextView numCarte_serie = (TextView) findViewById(R.id.numCarte_serie);
            numCarte_serie.setTextColor(getResources().getColor(R.color.colorPrimaryRed));


            TextView exp = (TextView) findViewById(R.id.exp);
            exp.setTextColor(getResources().getColor(R.color.colorPrimaryRed));

            //ID CARTE
            numCartePriveAutoSaveBD.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            numCartePriveAutoSaveBD.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));

            //ID CARTE PUBLIC
            numCartePublicAutoSaveBD.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            numCartePublicAutoSaveBD.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //EXPIRATION
            expire_date.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            expire_date.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));

            //Button
            findViewById(R.id.BtnPasserCarteSaveBDAuto).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));
            findViewById(R.id.BtnSaveCarte).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));


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
