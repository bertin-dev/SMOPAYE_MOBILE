package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.AccessToken;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Souscription extends AppCompatActivity {

    private CheckBox AbonnementMensuel;
    private CheckBox AbonnementHebdomadaire;
    private CheckBox AbonnementService;
    private String abonnement = "service";

    private static final String TAG = "Souscription";



    //recuperation des vues
    @BindView(R.id.til_nom)
    TextInputLayout til_nom;
    @BindView(R.id.til_prenom)
    TextInputLayout til_prenom;
    @BindView(R.id.til_numeroTel)
    TextInputLayout til_numeroTel;
    @BindView(R.id.til_cni)
    TextInputLayout til_cni;
    @BindView(R.id.til_adresse)
    TextInputLayout til_adresse;
    @BindView(R.id.til_numCarte)
    TextInputLayout til_numCarte;

    ApiService service;
    Call<AccessToken> call;
    AwesomeValidation validator;
    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souscription);

        getSupportActionBar().setTitle(getString(R.string.souscription));
        //getSupportParentActivityIntent().putExtra("resultatBD", "Administrateur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //récupération des vues en lien notre activity

        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        setupRulesValidatForm();
        init();

    }


    private void setupRulesValidatForm(){
        validator.addValidation(this, R.id.til_nom, RegexTemplate.NOT_EMPTY, Integer.parseInt(R.string.veuillezInserer + " " + R.string.nom));
        validator.addValidation(this, R.id.til_prenom, RegexTemplate.NOT_EMPTY, Integer.parseInt(R.string.veuillezInserer + " " + R.string.prenom));
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.NOT_EMPTY, Integer.parseInt(R.string.veuillezInserer + " " + R.string.numeroTel));
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.TELEPHONE, R.string.verifierNumero);
        validator.addValidation(this, R.id.til_cni, RegexTemplate.NOT_EMPTY, Integer.parseInt(R.string.veuillezInserer + " " + R.string.numeroDe));
        validator.addValidation(this, R.id.til_adresse, RegexTemplate.NOT_EMPTY, Integer.parseInt(R.string.veuillezInserer + " " + R.string.adresse));
        validator.addValidation(this, R.id.til_numCarte, RegexTemplate.NOT_EMPTY, Integer.parseInt(R.string.veuillezInserer + " " + R.string.numeroCarte));

        //validator.addValidation(this, R.id.til_numCarte, Patterns.PHONE, R.string.verifierNumero);
        //validator.addValidation(this, R.id.til_numCarte, "[a-zA-Z0-9]{6,0}", R.string.verifierNumero);


    }


    private void init(){
        AbonnementMensuel = (CheckBox) findViewById(R.id.AbonnementMensuel);
        AbonnementHebdomadaire = (CheckBox) findViewById(R.id.AbonnementHebdomadaire);
        AbonnementService = (CheckBox) findViewById(R.id.AbonnementService);
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
            //intent.putExtra("telephone", temp_number);
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


    @OnClick(R.id.btnSouscription)
    void register(){

        String nom = til_nom.getEditText().getText().toString();
        String prenom = til_prenom.getEditText().getText().toString();
        String telephone = til_numeroTel.getEditText().getText().toString();
        String cni = til_cni.getEditText().getText().toString();
        String adresse = til_adresse.getEditText().getText().toString();
        String num_carte = til_numCarte.getEditText().getText().toString();



        til_nom.setError(null);
        til_prenom.setError(null);
        til_numeroTel.setError(null);
        til_cni.setError(null);
        til_adresse.setError(null);
        til_numCarte.setError(null);

       validator.clear();

       if(validator.validate()){
           call = service.register(nom, prenom, telephone, cni, adresse, num_carte);
           call.enqueue(new Callback<AccessToken>() {
               @Override
               public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                   if(response.isSuccessful()){

                       /*sauvegarde du token retourné par l'api si la reponse est bonne
                         nous utiliserons la classe TokenManager pour cela
                        */
                       tokenManager.saveToken(response.body());

                       //retoure sur le formulaire d'enregistrement
                       Intent intent = new Intent(getApplicationContext(), Souscription.class);
                       startActivity(intent);
                       finish();

                   } else{
                       handleErrors(response.errorBody());

                   }

               }

               @Override
               public void onFailure(Call<AccessToken> call, Throwable t) {

                   //si on a une erreur de type 500 alors cela viendra ici
                   Log.w(TAG, "onFailure: " + t.getMessage());
               }
           });
       }

    }

    private void handleErrors(ResponseBody responseBody){

        ApiError apiError = Utils_manageError.convertErrors(responseBody);
        /*
        boucle sur toutes les erreurs trouvées
         */

        for(Map.Entry<String, List<String>> error: apiError.getErrors().entrySet()){

            if(error.getKey().equals("nom")){
                til_nom.setError(error.getValue().get(0));
            }


            if(error.getKey().equals("prenom")){
                til_prenom.setError(error.getValue().get(0));
            }


            if(error.getKey().equals("tel")){
                til_numeroTel.setError(error.getValue().get(0));
            }


            if(error.getKey().equals("numcarte")){
                til_numCarte.setError(error.getValue().get(0));
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }
}
