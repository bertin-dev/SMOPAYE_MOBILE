package com.ezpass.smopaye_mobile.Setting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.NotifApp;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendEmail extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "SendEmail";

    @BindView(R.id.til_desc_comment)
    TextInputLayout til_desc_comment;
    @BindView(R.id.tie_desc_comment)
    TextInputEditText tie_desc_comment;


    @BindView(R.id.til_email)
    TextInputLayout til_email;
    @BindView(R.id.tie_email)
    TextInputEditText tie_email;

    @BindView(R.id.titleComment)
    Spinner titleComment;

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

    private AwesomeValidation validator;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        getSupportActionBar().setTitle(getString(R.string.giveComment));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        progressDialog = new ProgressDialog(SendEmail.this);
        build_error = new AlertDialog.Builder(SendEmail.this);

        /*Appels de toutes les méthodes qui seront utilisées*/
        setupRulesValidatForm();
    }


    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     * @since 2020
     * */
    private void setupRulesValidatForm(){

        //coloration des champs lorsqu'il y a erreur
        til_desc_comment.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        validator.addValidation(this, R.id.til_desc_comment, RegexTemplate.NOT_EMPTY, R.string.insererCommentaire);

        til_email.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        validator.addValidation(this, R.id.til_email, RegexTemplate.NOT_EMPTY, R.string.insererEmail);
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
            Toast.makeText(SendEmail.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }

    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.send_email), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else{
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(findViewById(R.id.send_email), message, Snackbar.LENGTH_INDEFINITE);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
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

    public void sendScreeShotClicked(View view) {
        Toast.makeText(this, "cliked", Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.btn_sendEmail)
     void sendEmail(){

        if(!validateEmail(til_email) | !validateComments(til_desc_comment)){
            return;
        }
        /*Action à poursuivre si tous les champs sont remplis*/
        if(validator.validate()){
        String mail = tie_email.getText().toString().trim();
        String[]  tab_Email = mail.split(",");
        String subject = titleComment.getSelectedItem().toString();
        String message = tie_desc_comment.getText().toString();


        // 1ère méthode: Envoi d'email avec un service de me ssagerie votre téléphone disponible
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, tab_Email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));

        // 2èùe méthode directement à partir d'une API intégrée dans l'application
        //JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message);
        //javaMailAPI.execute();
        }

    }


    /**
     * validateComments() méthode permettant de verifier si le commentaire inséré est valide
     * @param til_desc_comment1
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateComments(TextInputLayout til_desc_comment1){
        String my_email= til_desc_comment1.getEditText().getText().toString().trim();
        if(my_email.isEmpty()){
            til_desc_comment1.setError(getString(R.string.insererCommentaire));
            return false;
        } else {
            til_desc_comment1.setError(null);
            return true;
        }
    }

    /**
     * validateEmail() méthode permettant de verifier si l"email inséré est valide
     * @param til_email1
     * @return Boolean
     * @since 2019
     * */
    private boolean validateEmail(TextInputLayout til_email1){
        String email = til_email1.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            til_email1.setError(getString(R.string.insererEmail));
            return false;
        } else {
            til_email1.setError(null);
            return true;
        }
    }


}
