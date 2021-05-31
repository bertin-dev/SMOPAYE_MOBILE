package com.ezpass.smopaye_mobile.Manage_Update_ProfilUser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ezpass.smopaye_mobile.Config.Global;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.NotifApp;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

/**
 * Activity permettant de modifier le profil utilisateur
 *
 * @see UpdatePassword
 */
public class UpdatePassword extends AppCompatActivity
                            implements ConnectivityReceiver.ConnectivityReceiverListener{


    private static final String TAG = "UpdatePassword";
    private AlertDialog.Builder build_error;
    private ACProgressPie dialog2;
    private ProgressDialog progressDialog;
    private String telephone1;

    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;


    //SERVICES GOOGLE FIREBASE
    private APIService apiService;
    private FirebaseUser fuser;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private TokenManager tokenManager;
    private AwesomeValidation validator;
    private Call<AllMyResponse> call;

    @BindView(R.id.til_modifTel)
    TextInputLayout til_modifTel;
    @BindView(R.id.til_ancienPass)
    TextInputLayout til_ancienPass;
    @BindView(R.id.til_NouveauPass)
    TextInputLayout til_NouveauPass;
    @BindView(R.id.til_confirmPass)
    TextInputLayout til_confirmPass;

    @BindView(R.id.tie_modifTel)
    TextInputEditText tie_modifTel;
    @BindView(R.id.tie_ancienPass)
    TextInputEditText tie_ancienPass;
    @BindView(R.id.tie_NouveauPass)
    TextInputEditText tie_NouveauPass;
    @BindView(R.id.tie_confirmPass)
    TextInputEditText tie_confirmPass;

    @BindView(R.id.authWindows)
    LinearLayout authWindows;
    @BindView(R.id.internetIndisponible)
    LinearLayout internetIndisponible;
    @BindView(R.id.conStatusIv)
    ImageView conStatusIv;
    @BindView(R.id.titleNetworkLimited)
    TextView titleNetworkLimited;
    @BindView(R.id.msgNetworkLimited)
    TextView msgNetworkLimited;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_update_password);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.ModifierCompte));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        //service = RetrofitBuilder.createService(ApiService.class);
        //tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        build_error = new AlertDialog.Builder(UpdatePassword.this);
        //service google firebase
        apiService = Client.getClient(Global.adresseURLGoogleAPI).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        Intent intent = getIntent();
        telephone1 = intent.getStringExtra("numeroTel");
        if(telephone1 != null)
            til_modifTel.getEditText().setText(telephone1);


        /*Appels de toutes les méthodes qui seront utilisées*/
        setupRulesValidatForm();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }

    @OnClick(R.id.btnValidNouveauPass)
    void modifierCompte(){

        if(!validateTel(til_modifTel) | !validateOldPassword(til_ancienPass) | !validateNewPassword(til_NouveauPass) | !validatePasswordConfirm(til_confirmPass)){
            return;
        }

        if(!validate(til_NouveauPass, til_confirmPass)){
            return;
        }

        String modifTel = til_modifTel.getEditText().getText().toString();
        String ancienPass = til_ancienPass.getEditText().getText().toString();
        String nouveauPass = til_NouveauPass.getEditText().getText().toString();
        validator.clear();

        /*Action à poursuivre si tous les champs sont remplis*/
        if(validator.validate()){
            //********************DEBUT***********
            dialog2 = new ACProgressPie.Builder(this)
                    .ringColor(Color.WHITE)
                    .pieColor(Color.WHITE)
                    .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                    .build();
            dialog2.show();
            //*******************FIN*****
            modifierCompteSmopaye(modifTel, ancienPass, nouveauPass, "");
        }

    }

    private void modifierCompteSmopaye(String modifTel, String ancienPass, String nouveauPass, String id_card) {


        call = service.update_account(ancienPass, nouveauPass);
        call.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {

                dialog2.dismiss();

                if(response.isSuccessful()){

                    if(response.body().isSuccess()){
                        //tokenManager.saveToken(response.body());
                        successResponse(id_card, response.body().getMessage());
                    } else {
                        errorResponse(id_card, response.body().getMessage());
                    }

                } else{

                    if(response.code() == 422){
                        handleErrors(response.errorBody());
                    }
                    else if(response.code() == 401){
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(UpdatePassword.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else{
                        errorResponse(id_card, response.message());
                    }

                }

            }

            @Override
            public void onFailure(Call<AllMyResponse> call, Throwable t) {

                dialog2.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(UpdatePassword.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(UpdatePassword.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * checkNetworkConnectionStatus() méthode permettant de verifier si la connexion existe ou si le serveur est accessible
     * @since 2019
     * */
    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus(){
        boolean isConnected = ConnectivityReceiver.isConnected();

        showSnackBar(isConnected);

        if(isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if(activeInfo != null && activeInfo.isConnected()){
            progressDialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
            progressDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    recreate();
                }
            }, 2000); // 2000 milliseconds delay

        } else{
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.update_password), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            if(Constant.color == getResources().getColor(R.color.colorPrimaryRed))
                textView.setBackgroundResource(R.color.colorPrimaryRed);
            else
                textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else{
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(findViewById(R.id.update_password), message, Snackbar.LENGTH_INDEFINITE);
            view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        }
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        /*if(!isConnected){
            changeActivity();
        }*/
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        //register connection status listener
        NotifApp.getInstance().setConnectivityListener(this);
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

    /**
     * validateTel() méthode permettant de verifier si le numéro de téléphone inséré est valide
     * @param til_tel1
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateTel(TextInputLayout til_tel1){
        String my_numCompte = til_tel1.getEditText().getText().toString().trim();
        if(my_numCompte.isEmpty()){
            til_tel1.setError(getString(R.string.insererTelephone));
            return false;
        } else if(my_numCompte.length() < 9){
            til_tel1.setError(getString(R.string.telephoneCourt));
            return false;
        } else {
            til_tel1.setError(null);
            return true;
        }
    }


    /**
     * validate() méthode permettant de verifier si le nouveau mot de passe et le mot de passe de confirmation sont différents
     * @param til_newPassword, til_passwordConfirm
     * @return Boolean
     * @since 2019
     * */
    private Boolean validate(TextInputLayout til_newPassword, TextInputLayout til_passwordConfirm){
        String new_pass = til_newPassword.getEditText().getText().toString().trim();
        String pass_confirm = til_passwordConfirm.getEditText().getText().toString().trim();
        if(!new_pass.equalsIgnoreCase(pass_confirm)){
            til_newPassword.setError(getString(R.string.passDeConfirmation));
            til_passwordConfirm.setError(getString(R.string.passDeConfirmation));
            return false;
        } else {
            til_newPassword.setError(null);
            til_passwordConfirm.setError(null);
            return true;
        }
    }

    /**
     * validateOldPassword() méthode permettant de verifier si l'ancien mot de passe inséré est valide
     * @param til_password1
     * @return Boolean
     * @since 2019
     * */
    private boolean validateOldPassword(TextInputLayout til_password1){
        String psw = til_password1.getEditText().getText().toString().trim();
        if(psw.isEmpty()){
            til_password1.setError(getString(R.string.insererPassword));
            return false;
        } else if(psw.length() < 5){
            til_password1.setError(getString(R.string.passwordCourt));
            return false;
        } else {
            til_password1.setError(null);
            return true;
        }
    }


    /**
     * validateNewPassword() méthode permettant de verifier si le nouveau mot de passe inséré est valide
     * @param til_password1
     * @return Boolean
     * @since 2019
     * */
    private boolean validateNewPassword(TextInputLayout til_password1){
        String psw = til_password1.getEditText().getText().toString().trim();
        if(psw.isEmpty()){
            til_password1.setError(getString(R.string.insererPassword));
            return false;
        } else if(psw.length() < 5){
            til_password1.setError(getString(R.string.passwordCourt));
            return false;
        } else {
            til_password1.setError(null);
            return true;
        }
    }


    /**
     * validatePasswordConfirm() méthode permettant de verifier si le mot de passe de confirmation inséré est valide
     * @param til_password1
     * @return Boolean
     * @since 2019
     * */
    private boolean validatePasswordConfirm(TextInputLayout til_password1){
        String psw = til_password1.getEditText().getText().toString().trim();
        if(psw.isEmpty()){
            til_password1.setError(getString(R.string.insererPassword));
            return false;
        } else if(psw.length() < 5){
            til_password1.setError(getString(R.string.passwordCourt));
            return false;
        } else {
            til_password1.setError(null);
            return true;
        }
    }


    private void successResponse(String id_card, String response) {

        /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
        //SERVICE GOOGLE FIREBASE
        final String id_carte_sm = id_card;

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
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.ModifMotDePasse), response, "success");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdatePassword.this, getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(UpdatePassword.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.ModifMotDePasse), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.ModifMotDePasse), response, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


        View view = LayoutInflater.from(UpdatePassword.this).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                til_ancienPass.getEditText().setText("");
                til_NouveauPass.getEditText().setText("");
                til_confirmPass.getEditText().setText("");
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.putExtra("telephone", telephone1);
                startActivity(intent);
            }
        });
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    private void errorResponse(String id_card, String response){

        /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
        //SERVICE GOOGLE FIREBASE
        final String id_carte_sm = id_card;

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
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.ModifMotDePasse), response, "error");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdatePassword.this, getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(UpdatePassword.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.ModifMotDePasse), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.ModifMotDePasse), response, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

        View view = LayoutInflater.from(UpdatePassword.this).inflate(R.layout.alert_dialog_success, null);
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
     * handleErrors() méthode permettant de boucler sur toutes les erreurs trouvées dans les données retournées par l'API Rest
     * @param responseBody
     * @since 2020
     * */
    private void handleErrors(ResponseBody responseBody){

        ApiError apiError = Utils_manageError.convertErrors(responseBody);
        for(Map.Entry<String, List<String>> error: apiError.getErrors().entrySet()){

            if(error.getKey().equals("phone")){
                til_modifTel.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("old_password")){
                til_ancienPass.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("new_password")){
                til_NouveauPass.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("password_confirm")){
                til_confirmPass.setError(error.getValue().get(0));
            }
        }

    }

    private void setupRulesValidatForm() {

        //coloration des champs lorsqu'il y a erreur
        til_modifTel.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_ancienPass.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_NouveauPass.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_confirmPass.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));

        validator.addValidation(this, R.id.til_modifTel, RegexTemplate.NOT_EMPTY, R.string.insererTelephone);
        validator.addValidation(this, R.id.til_ancienPass, RegexTemplate.NOT_EMPTY, R.string.insererOldPassword);
        validator.addValidation(this, R.id.til_NouveauPass, RegexTemplate.NOT_EMPTY, R.string.insererNewPassword);
        validator.addValidation(this, R.id.til_confirmPass, RegexTemplate.NOT_EMPTY, R.string.insererPasswordConfirm);
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
            finish();
            return true;
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            startActivity(intent);
            finish();
            return true;
        }

        /*if (id == android.R.id.home) {
            // todo: goto back activity from here
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("telephone", "123456789");
            intent.putExtra("resultatBD", "bertin-ezpass-Agent-Actif-partenaire");
            startActivity(intent);
            finish();
            return true;
        }*/
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        /*if(id == R.id.modifierCompte){
            return true;
        }*/

        return super.onOptionsItemSelected(item);
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
                                            Toast.makeText(UpdatePassword.this, getString(R.string.echoue), Toast.LENGTH_SHORT).show();
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
            //telephone
            tie_modifTel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_phone_android_red_24dp, 0);
            tie_modifTel.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            tie_modifTel.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //old password
            tie_ancienPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_red_24dp, 0);
            tie_ancienPass.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            tie_ancienPass.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //new password
            tie_NouveauPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_red_24dp, 0);
            tie_NouveauPass.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            tie_NouveauPass.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //password confirm
            tie_confirmPass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_red_24dp, 0);
            tie_confirmPass.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            tie_confirmPass.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //Button
            findViewById(R.id.btnValidNouveauPass).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));

            //network not available ic_wifi_red
            conStatusIv.setImageResource(R.drawable.ic_wifi_red);
            titleNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            msgNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            findViewById(R.id.btnReessayer).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));

            //setTheme(R.style.colorPrimaryDark);
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
