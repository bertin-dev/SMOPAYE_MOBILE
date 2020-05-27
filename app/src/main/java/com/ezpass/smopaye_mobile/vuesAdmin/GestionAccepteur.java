package com.ezpass.smopaye_mobile.vuesAdmin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GestionAccepteur extends AppCompatActivity {

    private Button totalAccepteur, totalAccepteurActifs, totalAccepteurInactifs;
    private LinearLayout lnModifAccepteurs, lnSuppAccepteurs, lnListAccepteurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_accepteur);

        getSupportActionBar().setTitle(getString(R.string.gesAccepteur));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totalAccepteur = (Button) findViewById(R.id.totalAccepteur);
        totalAccepteurActifs = (Button) findViewById(R.id.totalAccepteurActifs);
        totalAccepteurInactifs = (Button) findViewById(R.id.totalAccepteurInactifs);
        lnModifAccepteurs = (LinearLayout) findViewById(R.id.lnModifAccepteurs);
        lnSuppAccepteurs = (LinearLayout) findViewById(R.id.lnSuppAccepteurs);
        lnListAccepteurs = (LinearLayout) findViewById(R.id.lnListAccepteurs);


        lnListAccepteurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListeAccepteursSimple.class);
                startActivity(intent);
            }
        });


        lnModifAccepteurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListeAccepteurs.class);
                intent.putExtra("ACTION", "Modifier");
                startActivity(intent);
            }
        });

        lnSuppAccepteurs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListeAccepteurs.class);
                intent.putExtra("ACTION", "Supprimer");
                startActivity(intent);
            }
        });

        getStatutTotalAccepteur();
    }


    private void getStatutTotalAccepteur(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Uri.Builder builder = new Uri.Builder();
                    builder.appendQueryParameter("auth","Users");
                    builder.appendQueryParameter("login", "listing");
                    builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

                    //Connexion au serveur
                    // URL url = new URL("http://192.168.20.11:1234/nombreRecepteur.php"+builder.build().toString());
                    URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());
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
                            //Toast.makeText(getApplicationContext(), "Encours de traitement...", Toast.LENGTH_SHORT).show();
                        }
                    });

                    while (bufferedReader.ready() || data==""){
                        data+=bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();

                    JSONArray jsonArray = new JSONArray(data);
                    final int[] compteurAac = {0};
                    final int[] compteurAin = {0};
                    final int[] compteurAtotal = {0};

                    for (int i=0;i<jsonArray.length();i++){


                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        compteurAtotal[0]++;


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(jsonObject.getString("etat_compte").toLowerCase().equalsIgnoreCase("inactif")){
                                        compteurAin[0]++;
                                    } else if(jsonObject.getString("etat_compte").toLowerCase().equalsIgnoreCase("actif")){
                                        compteurAac[0]++;
                                    }
                                    totalAccepteur.setText(String.valueOf(compteurAtotal[0]));
                                    totalAccepteurActifs.setText(String.valueOf(compteurAac[0]));
                                    totalAccepteurInactifs.setText(String.valueOf(compteurAin[0]));

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
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    try {
                        Thread.sleep(2000);
                        //Toast.makeText(Inscription.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
