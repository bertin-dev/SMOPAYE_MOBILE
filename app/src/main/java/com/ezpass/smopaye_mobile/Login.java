package com.ezpass.smopaye_mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.vuesUtilisateur.Souscription;
import com.ezpass.smopaye_mobile.vuesUtilisateur.Souscription_User_AutoEnreg;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.telpo.tps550.api.nfc.Nfc;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class Login extends AppCompatActivity {

    private TextInputLayout mNumeroTel, mPassword;
    private TextInputEditText telNumber;
   // private ShowHidePasswordEditText loginPass;
    private AlertDialog.Builder build, build_error;
    private ProgressDialog progressDialog;
    private LinearLayout internetIndisponible, authWindows;
    private Button btnReessayer, btnSouscription, loginBtn;
    private Spinner langue;

    /*service google firebase*/
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String verificationId;

    private String file = "tmp_number";
    private String file2 = "tmp_data_user";
    private String fileContents;
    private int c;
    private String temp_number = "";

    ImageView conStatusIv;
    TextView titleNetworkLimited, msgNetworkLimited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loadLocale();
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        progressDialog=new ProgressDialog(Login.this);

        /* Verification si l'utilisateur a cliqué pour voir le mot de passe ou pas. */

        loginBtn = (Button) findViewById(R.id.btnlogin);
        btnSouscription = (Button) findViewById(R.id.btnSouscription);
        //loginPass = (ShowHidePasswordEditText) findViewById(R.id.loginPass);

        mNumeroTel = findViewById(R.id.telephone);
        mPassword = findViewById(R.id.password);
        authWindows = (LinearLayout) findViewById(R.id.authWindows);
        internetIndisponible = (LinearLayout) findViewById(R.id.internetIndisponible);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);
        build = new AlertDialog.Builder(this);

        langue = (Spinner) findViewById(R.id.changLangue);
        telNumber = findViewById(R.id.telNumber);




        loginBtn.setOnClickListener(this::submitData);
        btnSouscription.setOnClickListener(this::openActivityRegister);

        mNumeroTel.setErrorTextColor(ColorStateList.valueOf(Color.BLUE));
        mPassword.setErrorTextColor(ColorStateList.valueOf(Color.BLUE));

        btnReessayer.setOnClickListener(this::checkNetworkConnectionStatus);



        //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {

            // Initializing a String Array
            String[] lang = new String[]{
                    "Par défaut",
                    "Français",
                    "Anglais"
            };
            // Initializing an ArrayAdapter
            ArrayAdapter<String> spinnerArrayLangue = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, lang);
            spinnerArrayLangue.setDropDownViewResource(R.layout.spinner_item);
            langue.setAdapter(spinnerArrayLangue);
        } else {
            // Initializing a String Array
            String[] lang = new String[]{
                    "Default",
                    "French",
                    "English"
            };
            // Initializing an ArrayAdapter
            ArrayAdapter<String> spinnerArrayLangue = new ArrayAdapter<String>(
                    this, R.layout.spinner_item, lang);
            spinnerArrayLangue.setDropDownViewResource(R.layout.spinner_item);
            langue.setAdapter(spinnerArrayLangue);

        }

        langue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){

                }

                //french
                if(position == 1){
                    setLocale("fr");
                    //recreate();
                }
                //english
                if(position == 2) {
                    setLocale("en");
                    //recreate();
                }

                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //chargement des informations du dernier numéro inséré dans la BD locale pour le mettre dans l'EditText du télephone
        //DbUser db = new DbUser(Login.this);
        //telNumber.setText(db.GetLastNumberInsertBD());

        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(!temp_number.equalsIgnoreCase("")){
            telNumber.setText(temp_number);
        }


    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared preferences
       /* SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();*/

        Intent refresh = new Intent(this, Login.class);
        finish();
        startActivity(refresh);
    }

    //load language saved in shared preferences
    private void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }

    private void openActivityRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), Souscription_User_AutoEnreg.class);
        startActivity(intent);
    }

    public void checkNetworkConnectionStatus(View view) {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();

        if(activeInfo != null && activeInfo.isConnected()){

            ProgressDialog dialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
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
            Toast.makeText(Login.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validatePassword(){
        String password = mPassword.getEditText().getText().toString().trim();

        if(password.isEmpty()){
            mPassword.setError(getString(R.string.insererPassword));
            return false;
        } else if(password.length() < 5){
          mPassword.setError(getString(R.string.passwordCourt));
            return false;
        } else {
            mPassword.setError(null);
            return true;
        }
    }


    private Boolean validateTelephone(){
        String numero = mNumeroTel.getEditText().getText().toString().trim();
        if(numero.isEmpty()){
            mNumeroTel.setError(getString(R.string.passwordCourt));
            return false;
        } else if(numero.length() < 9){
            mNumeroTel.setError(getString(R.string.telephoneCourt));
            return false;
        } else {
            mNumeroTel.setError(null);
            return true;
        }
    }

    private void submitData(View view){

        if(!validateTelephone() | !validatePassword()){
            return;
        }

        //service google firebase
        auth = FirebaseAuth.getInstance();
        String tel = mNumeroTel.getEditText().getText().toString().trim();
        String pass = mPassword.getEditText().getText().toString().trim();
        //numéro provenant de la modification du compte
        Intent in = getIntent();
        if(in.getStringExtra("telephone") != null)
            mNumeroTel.getEditText().setText(in.getStringExtra("telephone"));

        /////////////////////////ECRIRE DANS LES FICHIERS/////////////////////////////////
        fileContents = mNumeroTel.getEditText().getText().toString();
        try{
            //ecrire numero de telephone
            FileOutputStream fOut = openFileOutput(file, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(), file);
            //Toast.makeText(getBaseContext(), "File Saved at " + fileDir, Toast.LENGTH_LONG).show();
        } catch (Exception e){
            e.printStackTrace();
        }



        // si les deux champs sont remplis
        /*Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
        intent1.putExtra("telephone", tel);
        intent1.putExtra("resultatBD", "bertin-dev-Accepteur-Actif-partenaire-2022-10-07-inactif-A M1-7ABD6953-Mensuel");
        startActivity(intent1);*/
        /*Connxion connexion = new Connxion();*/


        // LoginGoogleFirebase("sm" + tel + "@smopaye.cm", tel, "bertin-dev-Administrateur-Actif-partenaire-2022-10-07-actif-A M1-7ABD6953-Mensuel");

        AuthLogin(tel, pass);
    }


    private void LoginGoogleFirebase(String email1, final String tel1, final String f1) {

        auth.signInWithEmailAndPassword(email1, tel1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){


                            /////////////////////////ECRIRE DANS LES FICHIERS/////////////////////////////////
                            try{
                                //ecrire les données renvoyées par le web service lors de la connexion
                                FileOutputStream fOut2 = openFileOutput(file2, MODE_PRIVATE);
                                fOut2.write(f1.getBytes());
                                fOut2.close();
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("telephone", tel1);
                            intent.putExtra("resultatBD", f1);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(Login.this, getString(R.string.echecAuthenfication), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void AuthLogin(final String telephone, final String psw) {

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
                    builder.appendQueryParameter("auth","login");
                    builder.appendQueryParameter("login", "login");
                    builder.appendQueryParameter("telephone",telephone);
                    builder.appendQueryParameter("pwd",psw);
                    builder.appendQueryParameter("fgfggergJHGS",ChaineConnexion.getEncrypted_password());
                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());


                    //Connexion au serveur

                    URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());
                    //URL url = new URL("http://192.168.20.6:1234/index.php?auth=login&login=login&telephone="+tel+"&pwd="+pass+"&fgfggergJHGS="+encrypted_password+"&uhtdgG18="+salt+""+builder.build().toString());
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
                            Toast.makeText(getApplicationContext(), getString(R.string.ouvertureEncours), Toast.LENGTH_SHORT).show();
                        }
                    });

                    while (bufferedReader.ready() || data==""){
                        data+=bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();


                    final String f = data.trim();
                    // boolean d=data;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressDialog.dismiss();
                            String[] parts = f.split("-");

                            if(parts[0].toString().trim().equalsIgnoreCase(f))
                            {
                                build_error = new AlertDialog.Builder(Login.this);
                                View view = LayoutInflater.from(Login.this).inflate(R.layout.alert_dialog_success, null);
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

                                Toast.makeText(getApplicationContext(), f, Toast.LENGTH_LONG).show();
                            }
                            else{

                                String nom = parts[0]; //Nom
                                String prenom = parts[1]; //Prenom
                                String session = parts[2]; // Accepteur, Administrateur, Utilisateur, Agent
                                String etat = parts[3]; // Actif, Inactif, Offline

                                //String categorie = parts[4]; // chauffeur, moto_taxi, bus, cargo

                                if(etat.toLowerCase().equalsIgnoreCase("offline"))
                                {
                                    build_error = new AlertDialog.Builder(Login.this);
                                    View view = LayoutInflater.from(Login.this).inflate(R.layout.alert_dialog_success, null);
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                    title.setText(getString(R.string.information));
                                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                                    statutOperation.setText(prenom + " " + nom + " " + getString(R.string.bloque));
                                    build_error.setPositiveButton("OK", null);
                                    build_error.setCancelable(false);
                                    build_error.setView(view);
                                    build_error.show();
                                }

                                else if(session.toLowerCase().equalsIgnoreCase("utilisateur") && etat.equalsIgnoreCase("offline")){
                                    build_error = new AlertDialog.Builder(Login.this);
                                    View view = LayoutInflater.from(Login.this).inflate(R.layout.alert_dialog_success, null);
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                    title.setText(getString(R.string.information));
                                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                                    statutOperation.setText(prenom + " " + nom + " " + getString(R.string.bloque));
                                    build_error.setPositiveButton("OK", null);
                                    build_error.setCancelable(false);
                                    build_error.setView(view);
                                    build_error.show();
                                }

                                else if(session.toLowerCase().equalsIgnoreCase("agent") && etat.equalsIgnoreCase("offline")){
                                    build_error = new AlertDialog.Builder(Login.this);
                                    View view = LayoutInflater.from(Login.this).inflate(R.layout.alert_dialog_success, null);
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                    title.setText(getString(R.string.information));
                                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                                    statutOperation.setText(prenom + " " + nom + " " + getString(R.string.bloque));
                                    build_error.setPositiveButton("OK", null);
                                    build_error.setCancelable(false);
                                    build_error.setView(view);
                                    build_error.show();
                                }

                                else{


                                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                        FirebaseAuth.getInstance().signOut();
                                        /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("telephone", telephone);
                                        intent.putExtra("resultatBD", f);
                                        startActivity(intent);
                                        finish();
                                        return;*/
                                    }

                                    LoginGoogleFirebase("sm" + telephone + "@smopaye.cm", telephone, f);


                                }
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
                                    Toast.makeText(getApplicationContext(), jsonObject.getString("montant"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Login.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                            } else{
                                progressDialog.dismiss();
                                authWindows.setVisibility(View.GONE);
                                internetIndisponible.setVisibility(View.VISIBLE);
                                conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                //msgNetworkLimited.setText();
                                Toast.makeText(Login.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                            }


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
        //
        mNumeroTel.getEditText().setText("");
        mPassword.getEditText().setText("");
    }





    /* -----------------------------------------------AUTHENTIFICATION VIA NUMERO DE TELEPHONE SOUS GOOGLE FIREBASE --(PAS UTILISE)-----------------------------------*/


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

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
                            String username = "Keline";

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


    /*-------------------------------------------------------------------------------FIN----------------------------------------------------------------*/

}
