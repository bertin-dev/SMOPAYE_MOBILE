package com.ezpass.smopaye_mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.ezpass.smopaye_mobile.Manage_Register.SouscriptionUploadIMGidCard;
import com.ezpass.smopaye_mobile.Provider.PrefManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Manage_Offre_Smopaye.Demarrage;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.Manage_Register.Souscription_User_AutoEnreg;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.AccessToken;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
 *   <b>Login est la classe représentant l'activity d'authentification.</b>
 *   <p>
 *   cette page permet à l'utilisateur de faire 3 chose:
 *   <ul>
 *   <li>s'authentifier</li>
 *   <li>s'auto-enregistrer</li>
 *   <li>Reinitialiser le mot de passe</li>
 *   </ul>
 *   </p>
 *
 * Classe Login qui hérite AppCompatActivity et implemente ModalDialog_PasswordForgot.ExampleDialogListener, ConnectivityReceiver.ConnectivityReceiverListener
 *
 * @see Login
 * @see AppCompatActivity
 *
 * @author Bertin-dev
 * @powered by smopaye sarl
 * @version 1.4.0
 * @since 2019
 * @Copyright 2019-2021
 */

public class Login extends AppCompatActivity
        implements ModalDialog_PasswordForgot.ExampleDialogListener, ConnectivityReceiver.ConnectivityReceiverListener {

    /**
     * Initialisation des variables et des objets utilisé dans la page login
     */
    private static final String TAG = "Login";
    String currentLanguage = (Locale.getDefault().getLanguage().contentEquals("fr")) ? "fr" : "en", currentLang;
    /*Récupération des id des widgets xml avec la library ButterKnife*/
    @BindView(R.id.til_telephone)
    TextInputLayout til_telephone;
    @BindView(R.id.til_password)
    TextInputLayout til_password;
    @BindView(R.id.tie_telephone)
    TextInputEditText tie_telephone;
    @BindView(R.id.tie_password)
    TextInputEditText tie_password;
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
    int appTheme;
    int themeColor;
    int appColor;
    @BindView(R.id.titleLogin)
    TextView titleLogin;
    @BindView(R.id.btnlogin)
    Button btnlogin1;
    @BindView(R.id.btnAutoRegister)
    Button btnAutoRegister1;
    @BindView(R.id.txt_passwordForgot)
    TextView txt_passwordForgot;
    private ProgressDialog dialog;
    private AlertDialog.Builder build_error;
    private ACProgressPie dialog2;
    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private TokenManager tokenManager;
    private AwesomeValidation validator;
    private Call<AccessToken> call;
    private Call<AllMyResponse> call2;
    /*Déclaration des objets du service Google Firebase*/
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String verificationId;
    private FirebaseUser firebaseUser;
    private APIService apiService;
    private FirebaseUser fuser;
    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;
    /*Déclaration des objets qui permettent d'écrire sur le fichier*/
    private String tmp_number = "tmp_number";
    private String fileContents;
    private int c;
    private String temp_number = "";
    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences app_preferences;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                //inputCodeSMS.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    static final int PERMISSION_READ_STATE = 123;
    private String strPhoneType = "";
    private PrefManager prf;

    /**
     *permet de rendre la toolbar isTranslucentNavigationBar
     *
     * @see isTranslucentNavigationBar
     * @param activity
     * @return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
     *                 && (flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
     *                 == WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
     */
    public static boolean isTranslucentNavigationBar(Activity activity) {
        Window w = activity.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        int flags = lp.flags;
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && (flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                == WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

    }

    /**
     * Methode callback qui permet de demander la permission à l'utilisateur de recuperer les informations de l'utilisateur
     *
     * @see onStart
     *
     * @return ne retourne rien
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Initialisation de tous les objets du service google firebase
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //langue par défaut
        currentLanguage = getIntent().getStringExtra(currentLang);
        prf = new PrefManager(getApplicationContext());


        //Permission pour avoir les infos sur le telehone de l'utilisateur
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){

            if((prf.getString("key_IMEINumber")).equalsIgnoreCase("") || (prf.getString("key_SIMSerialNumber")).equalsIgnoreCase("")){
                MyTelephoneManager();
            }
        }else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
        }
    }

    /**
     * @param savedInstanceState Callback method onCreate() she using for the started activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_login);

        /*Mise en cache de la barre des titre dans le login*/
        //getSupportActionBar().hide();

        /*Initialisation de tous les objets qui seront manipulés*/
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        build_error = new AlertDialog.Builder(Login.this);

        /*Appels de toutes les méthodes qui seront utilisées*/
        setupRulesValidatForm();
        readTempNumberInFile();
        readNumberAfterUpdate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }


        //verification si l'utilisateur a déjà enregistré son token dans le sharepreference. si oui alors il est redirigé directement dans le MainActivity
        /*if(tokenManager.getToken().getAccessToken() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {

        /*final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
            titleLogin.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edittextborder) );
        } else {
            titleLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder));
        }*/

        if (Constant.color == getResources().getColor(R.color.colorPrimaryRed)) {
            titleLogin.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            titleLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            txt_passwordForgot.setTextColor(getResources().getColor(R.color.colorPrimaryRed));

            //telephone
            tie_telephone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_phone_android_red_24dp, 0);
            tie_telephone.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            tie_telephone.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //password
            tie_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_red_24dp, 0);
            tie_password.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            tie_password.setBackground(ContextCompat.getDrawable(this, R.drawable.edittextborder_red));
            //Button
            btnlogin1.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));
            btnAutoRegister1.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));

            //network not available ic_wifi_red
            conStatusIv.setImageResource(R.drawable.ic_wifi_red);
            titleNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            msgNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            findViewById(R.id.btnReessayer).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));

            //setTheme(R.style.colorPrimaryDark);
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
        } else {
            //getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0) {
            setTheme(Constant.theme);
        } else if (appTheme == 0) {
            setTheme(Constant.theme);
        } else {
            setTheme(appTheme);
        }
    }

    /**
     * login() méthode permettant d'éffectuer la requête d'authentification vers le serveur smopaye et google firebase
     *
     * @since 2019
     */
    @OnClick(R.id.btnlogin)
    void login() {

        if (!validateTelephone(til_telephone) | !validatePassword(til_password)) {
            return;
        }

        String numero = til_telephone.getEditText().getText().toString();
        String psw = til_password.getEditText().getText().toString();
        validator.clear();

        /*Action à poursuivre si tous les champs sont remplis*/
        if (validator.validate()) {

            dialog2 = new ACProgressPie.Builder(this)
                    .ringColor(Color.WHITE)
                    .pieColor(Color.WHITE)
                    .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                    .build();
            dialog2.show();

            submitDataInSmopayeServer(numero, psw);
        }

    }

    /**
     * checkNetworkConnectionStatus() méthode permettant de verifier si la connexion existe ou si le serveur est accessible
     *
     * @since 2019
     */
    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus() {
        boolean isConnected = ConnectivityReceiver.isConnected();

        showSnackBar(isConnected);

        if (isConnected) {
            changeActivity();
        }
    }

    private void changeActivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            dialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
            dialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                    recreate();
                    //finish();
                    //startActivity(getIntent());
                }
            }, 2000); // 2000 milliseconds delay

        } else {
            dialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(Login.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if (isConnected) {
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.login), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            if (Constant.color == getResources().getColor(R.color.colorPrimaryRed))
                textView.setBackgroundResource(R.color.colorPrimaryRed);
            else
                textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else {
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(findViewById(R.id.login), message, Snackbar.LENGTH_INDEFINITE);
            view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        }


        if (isTranslucentNavigationBar(this)) {
            final FrameLayout snackBarView = (FrameLayout) snackbar.getView();
            final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
            params.setMargins(params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    params.bottomMargin);

            snackBarView.getChildAt(0).setLayoutParams(params);
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
     * resetPasswordOpenDialog() méthode permettant d'ouvrir une boite de dialogue afin que l'utilisateur insert les éléments clés de son identification afin
     * d'reinitialiser son mot de passe
     *
     * @see resetPasswordOpenDialog
     * @since 2019
     */
    @OnClick(R.id.txt_passwordForgot)
    void resetPasswordOpenDialog() {
        ModalDialog_PasswordForgot exampleDialog = new ModalDialog_PasswordForgot();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    /**
     * autoRegister() méthode permettant d'ouvrir l'activity d'auto-enregistrement
     *
     * @see autoRegister
     * @since 2019
     */
    @OnClick(R.id.btnAutoRegister)
    void autoRegister() {
        Intent intent = new Intent(getApplicationContext(), Souscription_User_AutoEnreg.class);
        startActivity(intent);
    }

    /**
     * Récupération des données inseré par l'utilisateur dans la boite de dialogue
     *
     * @see applyTexts
     * @param getTel
     * @param getPJ
     *
     * @return ne retourne rien
     */
    @Override
    public void applyTexts(String getTel, String getPJ) {
        //Toast.makeText(this, getTel + "---" + getPJ, Toast.LENGTH_SHORT).show();
        //envoi de la requete
        sendRequestResetPassword(getTel, getPJ);
    }

    /**
     * Methode permettant d'envoyer les paramètres dans la webservice afin de reinitialiser le mot de passe de l'utilisateur
     * @param tel
     * @param pj
     */
    private void sendRequestResetPassword(String tel, String pj) {

        //********************DEBUT***********
        dialog2 = new ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
        dialog2.show();
        //*******************FIN*****

        call2 = service.reset_password(tel, pj);
        call2.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {
                dialog2.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onResponse " + response);

                if (response.isSuccessful()) {
                    //tokenManager.saveToken(response.body());
                    assert response.body() != null;
                    if (response.body().isSuccess()) {
                        successResponse(tel, response.body().getMessage()); //ID CARTE A RECUPERER DANS LE WEB SERVICE
                    } else {
                        errorResponse(response.body().getMessage());
                    }
                } else {
                   /* if(response.code() == 422){
                        handleErrors(response.errorBody());
                        //errorResponse(response.message());
                    }
                    else if(response.code() == 401) {
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(Login.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        errorResponse(apiError.getMessage());
                    } else{
                        errorResponse(response.message());
                    }*/
                    ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                    Toast.makeText(Login.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    errorResponse(apiError.getMessage());
                }
            }

            @Override
            public void onFailure(Call<AllMyResponse> call, Throwable t) {
                dialog2.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if (!(activeInfo != null && activeInfo.isConnected())) {
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else {
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(Login.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     *
     * @see submitDataInSmopayeServer
     * @param numero1 soumission du numéro de téléphone de l'utilisateur qui essaie de se connecter
     * @param psw1    soumission du mot de passe de l'utilisateur
     * @exception t
     * @return ne retourne rien
     */
    private void submitDataInSmopayeServer(String numero1, String psw1) {
        call = service.login(numero1, psw1, "Cnqactz7vnCGKBB7E12yN+17a+2Q/+d7PTkv1jOgcus=");
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                dialog2.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onResponse " + response);

                if (response.isSuccessful()) {

                    writeTempNumberInFile();
                    assert response.body() != null;
                    tokenManager.saveToken(response.body());

                    if (firebaseUser != null) {
                        // ouverture d'une session
                        Toast.makeText(getApplicationContext(), getString(R.string.ouvertureEncours), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("telephone", numero1);
                        startActivity(intent);
                        Animatoo.animateDiagonal(Login.this);
                        finish();
                    } else {
                        submitDataInGoogleFirebaseServer("sm" + numero1 + "@smopaye.cm", numero1);
                    }


                } else {

                    ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                    Toast.makeText(Login.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();

                    if (response.code() == 422) {
                        handleErrors(response.errorBody());
                        Log.w(TAG, "onResponse: " + response.errorBody());
                    } else {
                        Log.w(TAG, "onResponse: " + response.errorBody());
                        errorResponse(apiError.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

                dialog2.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if (!(activeInfo != null && activeInfo.isConnected())) {
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else {
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(Login.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * permet d'afficher la boite de dialogue en cas d'erreur
     *
     * @see errorResponse
     * @param message
     * @return ne retourne rien
     */
    private void errorResponse(String message) {
        View view = LayoutInflater.from(Login.this).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
        statutOperation.setText(message);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }


    /**
     *
     * permet de rechercher l'utilisateur courant et le notifier
     * @see successResponse
     * @param telephone
     * @param response
     * @return ne retourne rien
     */
    private void successResponse(String telephone, String response) {

        /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
        //SERVICE GOOGLE FIREBASE

        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("tel")
                .equalTo(telephone);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getTel().equals(telephone)) {
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.mdp), response, "success");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(Login.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.mdp), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.mdp), response, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


        View view = LayoutInflater.from(Login.this).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Login.this, Demarrage.class);
                startActivity(intent);
                finish();
            }
        });
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    /**
     * methode qui permet de soumettre les données de l'utilisateur pour authentification à google firebase
     * @see submitDataInGoogleFirebaseServer
     *
     * @param email1
     * @param tel1
     * @exception e
     * @return ouvre le MainActivity en cas de succès de l'opération ou encore envoi un toast en cas d'erreur
     */
    private void submitDataInGoogleFirebaseServer(String email1, String tel1) {
        auth.signInWithEmailAndPassword(email1, tel1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            /* ouverture d'une session */
                            Toast.makeText(getApplicationContext(), getString(R.string.ouvertureEncours), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("telephone", tel1);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, getString(R.string.echecAuthenfication), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Google Firebase server onFailure" + e.getMessage());

                        /*Vérification si la connexion internet accessible*/
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                        if (!(activeInfo != null && activeInfo.isConnected())) {
                            authWindows.setVisibility(View.GONE);
                            internetIndisponible.setVisibility(View.VISIBLE);
                            Toast.makeText(Login.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                        }
                        /*Vérification si le serveur est inaccessible*/
                        else {
                            authWindows.setVisibility(View.GONE);
                            internetIndisponible.setVisibility(View.VISIBLE);
                            conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                            titleNetworkLimited.setText(getString(R.string.connexionLimite));
                            //msgNetworkLimited.setText();
                            Toast.makeText(Login.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    /**
     * validateTelephone() méthode permettant de verifier si le mot de passe inséré est valide
     *
     * @see validatePassword
     * @param til_password1
     * @return Boolean
     */
    private boolean validatePassword(TextInputLayout til_password1) {
        String psw = til_password1.getEditText().getText().toString().trim();
        if (psw.isEmpty()) {
            til_password1.setError(getString(R.string.insererPassword));
            til_password1.requestFocus();
            return false;
        } else if (psw.length() < 5) {
            til_password1.setError(getString(R.string.passwordCourt));
            til_password1.requestFocus();
            return false;
        } else {
            til_password1.setError(null);
            return true;
        }
    }

    /**
     * validateTelephone() méthode permettant de verifier si le numéro de téléphone inséré est valide
     *
     * @see validateTelephone
     * @param til_telephone1
     * @return Boolean
     */
    private Boolean validateTelephone(TextInputLayout til_telephone1) {
        String myTel = til_telephone1.getEditText().getText().toString().trim();
        if (myTel.isEmpty()) {
            til_telephone1.setError(getString(R.string.insererTelephone));
            til_telephone1.requestFocus();
            return false;
        } else if (myTel.length() < 9) {
            til_telephone1.setError(getString(R.string.telephoneCourt));
            til_telephone1.requestFocus();
            return false;
        } else {
            til_telephone1.setError(null);
            return true;
        }
    }

    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     * @return change la couleur des champs de saisi selectionnés en cas d'erreur
     */
    private void setupRulesValidatForm() {

        //coloration des champs lorsqu'il y a erreur
        til_telephone.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        til_password.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));

        validator.addValidation(this, R.id.til_telephone, RegexTemplate.NOT_EMPTY, R.string.verifierNumero);
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.insererPassword);
    }

    /**
     * handleErrors() méthode permettant de boucler sur toutes les erreurs trouvées dans les données retournées par l'API Rest
     *
     * @see handleErrors
     * @param responseBody
     * @return affiche les messages d'erreurs dans til_telephone et til_password
     */
    private void handleErrors(ResponseBody responseBody) {

        ApiError apiError = Utils_manageError.convertErrors(responseBody);
        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {

            if (error.getKey().equals("phone")) {
                til_telephone.setError(error.getValue().get(0));
            }

            if (error.getKey().equals("password")) {
                til_password.setError(error.getValue().get(0));
            }
        }

    }

    /**
     * readTempNumberInFile() methodes permettant la lecture du contenu du fichier tmp_number
     * et insertion de celui-ci dans le tie_telephone à travers un setText()
     *
     * @exception e
     * @return ne retourne rien
     */
    private void readTempNumberInFile() {
        try {
            FileInputStream fInTel = openFileInput(tmp_number);
            while ((c = fInTel.read()) != -1) {
                temp_number = temp_number + Character.toString((char) c);
            }
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
            e.printStackTrace();
        }
        if (!temp_number.equalsIgnoreCase("")) {
            tie_telephone.setText(temp_number);
        }
    }

    /**
     * writeTempNumberInFile() methodes permettant l'écriture du numéro de téléphone dans le fichier tmp_number
     * et insertion de celui-ci dans le tie_telephone
     *
     * @exception e
     */
    private void writeTempNumberInFile() {
        fileContents = til_telephone.getEditText().getText().toString().trim();
        try {
            //ecrire du numero de telephone
            FileOutputStream fOut = openFileOutput(tmp_number, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(), tmp_number);
            //Toast.makeText(getBaseContext(), "File Saved at " + fileDir, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * readNumberAfterUpdate() methode permettant de lire le nouveau numéro de téléphone provenant du module modification du compte
     */
    private void readNumberAfterUpdate() {
        //numéro provenant de la modification du compte
        Intent in = getIntent();
        if (in.getStringExtra("telephone") != null)
            til_telephone.getEditText().setText(in.getStringExtra("telephone"));
    }

    /**
     * onDestroy() methode Callback qui permet de détruire une activity et libérer l'espace mémoire
     *
     * @since 2020
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (call != null) {
            call.cancel();
            call = null;
        }

        if (call2 != null) {
            call2.cancel();
            call2 = null;
        }
    }

    /**
     * fonction permettant de notifier les utilisateurs distants
     *
     * @see RemoteNotification
     *
     * @param receiver
     * @param username
     * @param title
     * @param message
     * @param statut_notif
     *
     * @exception t
     *
     * @return ne retourne rien
     */
    private void RemoteNotification(final String receiver, final String username, final String title, final String message, final String statut_notif) {

        //service google firebase
        apiService = Client.getClient(Global.adresseURLGoogleAPI).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(fuser.getUid(), R.mipmap.logo_official, username + ": " + message, title, receiver, statut_notif);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(Login.this, getString(R.string.echoue), Toast.LENGTH_SHORT).show();
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

    /**
     * permet de s'auto notifier après une opération éffectuée
     * @param titles
     * @param subtitles
     * @return ne renvoi rien
     */
    public void LocalNotification(String titles, String subtitles) {

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
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public void onBackPressed() {
        Deconnexion();
    }

    private void Deconnexion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.confirmExit));
        alertDialogBuilder.setIcon(R.drawable.ic_exit_to_app_black_24dp);
        alertDialogBuilder.setMessage(getString(R.string.confirmExitMsg));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Login.super.onBackPressed();
                Exit();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.non), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /**
     * permet d'initialiser la rotation de la boite de dialogue pendant 2 seconde ensuite la ferme et ferme l'application
     */
    private void Exit() {
        ProgressDialog dialog = ProgressDialog.show(this, getString(R.string.deconnexion), getString(R.string.encours), true);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
                finish();
            }
        }, 2000); // 2000 milliseconds delay
    }

    /**
     * verifyCode(String code) permet de verifier le code entrer par l'utilisateur avec google firebase. Mais je ne l'utilise pas
     *
     * @see verifyCode
     * @param code
     * @since 2019
     */
    /* -----------------------------------------------AUTHENTIFICATION VIA NUMERO DE TELEPHONE SOUS GOOGLE FIREBASE --(PAS UTILISE)-----------------------------------*/
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    /**
     * signInWithCredential(String code) d'enregistrer un numéro de téléphone au service google firebase. Mais je ne l'utilise pas
     *
     * @see signInWithCredential
     * @param credential
     * @since 2019
     */
    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(Login.this, resultatBDCache.getText(), Toast.LENGTH_SHORT).show();
                            //pbLoader.setVisibility(View.GONE);

                            Log.d(TAG, "signInWithCredential:success");
                            //---------------------------------------DEBUT-------------------------------------------
                            //String username = nom.getText().toString().trim() + " " + prenom.getText().toString().trim();
                            String username = "Bertin Mounok";

                            //Enregistrement de l'utilisateur dans la base de données distante Google Firebase
                            FirebaseUser user = task.getResult().getUser();
                            assert user != null;
                            String userid = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());
                            hashMap.put("id_carte", "00112233");
                            //----------------------------------fin-----------------------------------

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("telephone", "694048925");
                                    //intent.putExtra("resultatBD", resultatBDCache.getText().toString().trim());
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Login.this, "Votre Code est Invalid", Toast.LENGTH_SHORT).show();
                            }
                            //pbLoader.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * sendVerificationCode(String code) permet d'envoyer le code de verification par sms à l'utilisateur avec google firebase. Mais je ne l'utilise pas
     *
     * @param number
     * @since 2019
     */
    private void sendVerificationCode(String number) {
        //pbLoader.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    /*-------------------------------------------------------------------------------FIN----------------------------------------------------------------*/



    /*-------------------------------------------------------------------------------DETAILS PHONE USER----------------------------------------------------------------*/

    /**
     * recupère les informations du téléphone de l'utilisateur
     *
     * @see MyTelephoneManager
     */
    private void MyTelephoneManager() {

            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int phoneType = manager.getPhoneType();
            switch (phoneType){
                case (TelephonyManager.PHONE_TYPE_CDMA):
                    strPhoneType = "CDMA";
                    break;

                case (TelephonyManager.PHONE_TYPE_GSM):
                    strPhoneType = "GSM";
                    break;
                case (TelephonyManager.PHONE_TYPE_NONE):
                    strPhoneType = "NONE";
                    break;
            }

            boolean isRoaming = manager.isNetworkRoaming();
            String PhoneType = strPhoneType;
            String IMEINumber = manager.getDeviceId();
            String subscriberID = manager.getDeviceId();
            String SIMSerialNumber = manager.getSimSerialNumber();
            String networkCountryISO = manager.getNetworkCountryIso();
            String SIMCountryISO = manager.getSimCountryIso();
            String softwareVersion = manager.getDeviceSoftwareVersion();
            String voiceMailNumber = manager.getVoiceMailNumber();

            // Storing data
        prf.setString("key_PhoneType", PhoneType);
        prf.setString("key_IMEINumber", IMEINumber);
        prf.setString("key_subscriberID", subscriberID);
        prf.setString("key_SIMSerialNumber", SIMSerialNumber);
        prf.setString("key_networkCountryISO", networkCountryISO);
        prf.setString("key_SIMCountryISO", SIMCountryISO);
        prf.setString("key_softwareVersion", softwareVersion);
        prf.setString("key_voiceMailNumber", voiceMailNumber);
        prf.setBoolean("key_isRoaming", isRoaming);
    }

    /**
     * demande la permission à l'utilisateur de recupérer ses informations de téléphone
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
     * @see onRequestPermissionsResult
     *
     * @return en cas de succès alors la fonction MyTelephoneManager() est appelé. en cas d'échec alors un toast est affiché à l'utilisateur
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_READ_STATE) {
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MyTelephoneManager();
            } else {
                Toast.makeText(this, getString(R.string.authorizationFailed), Toast.LENGTH_SHORT).show();
            }
        }


    }
}
