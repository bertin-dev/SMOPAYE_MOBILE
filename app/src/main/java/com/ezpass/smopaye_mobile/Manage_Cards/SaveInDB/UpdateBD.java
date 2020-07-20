package com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Button;
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

import java.io.FileInputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBD extends AppCompatActivity {

    private static final String TAG = "UpdateBD";
    private EditText expire_date;
    private EditText serial_number_card;
    private EditText card_numbers;
    private Button BtnUpdateDB;
    private Button BtnDeleteDB;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private TokenManager tokenManager;
    private Call<AllMyResponse> call;

    //private String myId_card = "10";  //Identifiant de la carte de celui qui modifie

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    private int appTheme;
    private int themeColor;
    private int appColor;
    private Toolbar toolbar;

    private String file = "tmp_card_id";
    private int c;
    private String temp_card_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_update_b_d);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.modifier));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        card_numbers = (EditText) findViewById(R.id.card_numbers);
        serial_number_card = (EditText) findViewById(R.id.serial_number_card);
        expire_date = (EditText) findViewById(R.id.expire_date);
        BtnUpdateDB = (Button) findViewById(R.id.BtnUpdateDB);
        BtnDeleteDB = (Button) findViewById(R.id.BtnDeleteDB);


        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        progressDialog = new ProgressDialog(UpdateBD.this);
        build_error = new AlertDialog.Builder(UpdateBD.this);

        //lecture de l'id dela carte de l'utilisateur connecté
        readTempIDCARDInFile();
        Toast.makeText(this, "ID CARD: " + temp_card_id, Toast.LENGTH_SHORT).show();


        Bundle extras = getIntent().getExtras();
        final String card_id = extras.getString("card_id");
        String card_number = extras.getString("card_number");
        String serial_number = extras.getString("serial_number");
        String exp_date = extras.getString("exp_date");

        card_numbers.setText(card_number);
        serial_number_card.setText(serial_number);
        expire_date.setText(exp_date);

        if(card_id != null && card_id.trim().length() > 0 ){
            card_numbers.setFocusable(false);
        }

        BtnUpdateDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model_Card model_card = new Model_Card();
                model_card.setCode_number(card_numbers.getText().toString().trim());
                model_card.setSerial_number(serial_number_card.getText().toString().trim());
                model_card.setEnd_date(expire_date.getText().toString().trim());

                if(card_id != null && card_id.trim().length() > 0 ){
                    //update card
                    updateCard(card_id, model_card);
                } else{
                    errorResponse(getString(R.string.champsVide));
                }

            }
        });


        BtnDeleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(UpdateBD.this).inflate(R.layout.alert_dialog_success, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                title.setText(getString(R.string.information));
                imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                statutOperation.setText("ÊTES-VOUS SUR DE VOULOIR SUPPRIMER CET UTILISATEUR ???");
                build_error.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCard(Integer.parseInt(card_id));
                    }
                });
                build_error.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(UpdateBD.this, "Désolé vous n'êtes pas habileté à effectuer une suppression", Toast.LENGTH_SHORT).show();
                    }
                });
                build_error.setCancelable(false);
                build_error.setView(view);
                build_error.show();

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }


    private void readTempIDCARDInFile() {
        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getApplication().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_card_id = temp_card_id + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteCard(int card_id) {

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

        call = service.deleteCard(card_id);
        call.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {

                Log.w(TAG, "SMOPAYE SERVER onResponse: " + response );
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
                Log.w(TAG, "SMOPAYE SERVER onFailure: " + t.getMessage() );
            }
        });

    }

    private void updateCard(String card_id, Model_Card model_card) {

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

        call = service.updateCard(Integer.parseInt(card_id), model_card.getCode_number(), model_card.getSerial_number(), model_card.getEnd_date(), Integer.parseInt(temp_card_id));
        call.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: " + response );
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
                Log.w(TAG, " SMOPAYE SERVER onFailure: " + t.getMessage());
            }
        });


    }


    private void successResponse(String response) {

        View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_success, null);
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
        View view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_success, null);
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
            card_numbers.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            card_numbers.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));

            //ID CARTE PUBLIC (N° SERIE)
            serial_number_card.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            serial_number_card.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //EXPIRATION
            expire_date.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            expire_date.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));

            //Button
            BtnDeleteDB.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));
            BtnUpdateDB.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));


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
