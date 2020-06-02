package com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;

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

    private String myId_card = "10";  //Identifiant de la carte de celui qui modifie

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bd);

        Toolbar toolbar = findViewById(R.id.myToolbar);
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

        call = service.updateCard(Integer.parseInt(card_id), model_card.getCode_number(), model_card.getSerial_number(), model_card.getEnd_date(), Integer.parseInt(myId_card));
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
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
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

}
