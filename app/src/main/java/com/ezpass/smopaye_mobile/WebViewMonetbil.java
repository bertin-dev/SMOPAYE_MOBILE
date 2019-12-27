package com.ezpass.smopaye_mobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

public class WebViewMonetbil extends AppCompatActivity {

    private WebView mywebView;
    private String urlMonetbil;
    //private ProgressDialog progressDialog;
    private ProgressBar mProgressBar;


    /*@Override
    protected void onStart() {
        super.onStart();
        progressDialog=new ProgressDialog(WebViewMonetbil.this);

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
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_monetbil);

        getSupportActionBar().setTitle("SMOPAYE SARL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mProgressBar = findViewById(R.id.progressBar1);
        mProgressBar.setMax(100);

        mywebView = (WebView) findViewById(R.id.webViewMonetbil);




        Intent intent = getIntent();
        urlMonetbil = intent.getStringExtra("urlMonetbil");

        if(!urlMonetbil.equals("")){

            // Initialize a payment
           // PaymentRequest paymentRequest = new PaymentRequest("EGVL96bVrRD1ug8Mtj05Fz25HvRdqfZG");
            //paymentRequest.setPayment_listener(MyPaymentListener.class);


            mywebView.loadUrl(urlMonetbil);
            WebSettings webSettings = mywebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            mywebView.setWebViewClient(new WebViewClient());

            mywebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    mProgressBar.setVisibility(View.VISIBLE);
                    setTitle("Encours de Chargement...");
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mProgressBar.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
            });

            /*mywebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    mProgressBar.setProgress(newProgress);
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                }

                @Override
                public void onReceivedIcon(WebView view, Bitmap icon) {
                    super.onReceivedIcon(view, icon);
                }
            });*/

        }
        else {
            Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onBackPressed() {
        if(mywebView.canGoBack()){
            mywebView.goBack();
        }
        else {
            super.onBackPressed();
        }

    }


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

        /*if (id == android.R.id.home) {
            if(mywebView.canGoBack()){
                mywebView.goBack();
            }
            else {
                super.onBackPressed();
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

}


