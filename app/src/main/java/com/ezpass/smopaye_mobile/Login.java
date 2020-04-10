package com.ezpass.smopaye_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
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

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    @BindView(R.id.til_telephone)
    TextInputLayout til_telephone;
    @BindView(R.id.til_password)
    TextInputLayout til_password;

    ApiService service;
    TokenManager tokenManager;
    AwesomeValidation validator;
    Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRulesValidatForm();

        //verification si l'utilisateur a déjà enregistré son token dans le sharepreference. si oui alors il est redirigé directement dans le MainActity
        if(tokenManager.getToken().getAccessToken() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.btnlogin)
    void login(){

        String numero = til_telephone.getEditText().getText().toString();
        String psw = til_password.getEditText().getText().toString();

        til_telephone.setError(null);
        til_password.setError(null);


        validator.clear();

        if(validator.validate()){

            call = service.login(numero, psw);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                    if(response.isSuccessful()){
                        tokenManager.saveToken(response.body());
                        /*
                        ouverture d'une session en fonction du statut de l'utilisateur
                         */
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.putExtra("telephone", "694048925");
                        //intent.putExtra("resultatBD", resultatBDCache.getText().toString().trim());
                        startActivity(intent);
                        finish();

                    } else{

                        if(response.code() == 422){
                            handleErrors(response.errorBody());
                        }
                        if(response.code() == 401){
                            ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                            Toast.makeText(Login.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(TAG, "onFailure " + t.getMessage());
                }
            });

        }

    }


    private void setupRulesValidatForm(){
        validator.addValidation(this, R.id.til_telephone, RegexTemplate.NOT_EMPTY, R.string.verifierNumero);
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.insererPassword);
    }

    private void handleErrors(ResponseBody responseBody){

        ApiError apiError = Utils_manageError.convertErrors(responseBody);
        /*
        boucle sur toutes les erreurs trouvées
         */

        for(Map.Entry<String, List<String>> error: apiError.getErrors().entrySet()){

            if(error.getKey().equals("telephone")){
                til_telephone.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("password")){
                til_password.setError(error.getValue().get(0));
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
