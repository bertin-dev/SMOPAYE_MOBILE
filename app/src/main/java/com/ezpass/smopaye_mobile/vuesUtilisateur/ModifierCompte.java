package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TutorielUtilise;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class ModifierCompte extends AppCompatActivity {

    private Button btnValidNouveauPass;
    private EditText modifTelephone, ancienPass,NouveauPass, confirmPass;
    private AlertDialog.Builder build, build_error;
    private ProgressDialog progressDialog;
    private String telephone1;
    /*NOTIFICATION*/
    private NotificationManagerCompat notificationManager;

    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_compte);

        getSupportActionBar().setTitle("Modifier Compte");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        btnValidNouveauPass = (Button)findViewById(R.id.btnValidNouveauPass);
        ancienPass = (EditText)findViewById(R.id.ancienPass);
        NouveauPass =(EditText) findViewById(R.id.NouveauPass);
        confirmPass =(EditText) findViewById(R.id.confirmPass);
        modifTelephone =(EditText) findViewById(R.id.modifTel);





        Intent intent = getIntent();
        telephone1 = intent.getStringExtra("telephone");
        if(telephone1 != null)
            modifTelephone.setText(telephone1);



        progressDialog = new ProgressDialog(ModifierCompte.this);


        btnValidNouveauPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ModifierCompte.this, ancienPass.getText().toString() + "-" + NouveauPass.getText().toString() + "-" + confirmPass.getText().toString() + "-" + modifTelephone.getText().toString(), Toast.LENGTH_SHORT).show();

                if(ancienPass.getText().length() <= 0 && NouveauPass.getText().length() <= 0 &&
                        confirmPass.getText().length() <= 0 && modifTelephone.toString().length() <= 0){

                    build_error = new AlertDialog.Builder(ModifierCompte.this);
                    View view = LayoutInflater.from(ModifierCompte.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.champsVide));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);

                    build_error.setView(view);
                    build_error.show();

                }
                else if (!NouveauPass.getText().toString().equalsIgnoreCase(confirmPass.getText().toString().trim())){
                    build_error = new AlertDialog.Builder(ModifierCompte.this);
                    View view = LayoutInflater.from(ModifierCompte.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.passDeConfirmation));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);

                    build_error.setView(view);
                    build_error.show();
                }

                else if (modifTelephone.getText().length() < 0  && modifTelephone.getText().length() > 9){
                    build_error = new AlertDialog.Builder(ModifierCompte.this);
                    View view = LayoutInflater.from(ModifierCompte.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(getString(R.string.nbrChiffreInferieur));
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);

                    build_error.setView(view);
                    build_error.show();
                }
                else
                {
                    //Toast.makeText(Login.this, typeUser.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), ancienPass.getText().toString()+" im here "+NouveauPass.getText().toString().trim() +" "+ confirmPass.getText().toString(), Toast.LENGTH_SHORT).show();
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
                                builder.appendQueryParameter("auth","users");
                                builder.appendQueryParameter("login", "updateCpt");
                                builder.appendQueryParameter("TELEPHONE",modifTelephone.getText().toString().trim());
                                builder.appendQueryParameter("pwd",ancienPass.getText().toString().trim());
                                builder.appendQueryParameter("newPass", NouveauPass.getText().toString().trim());
                                builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                                builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

                                //Connexion au serveur
                                //URL url = new URL("http://192.168.20.6:1234/index.php"+builder.build().toString());
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
                                        Toast.makeText(getApplicationContext(), "Encours de traitement...", Toast.LENGTH_SHORT).show();
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
                                        //Retour serveur
                                        //Toast.makeText(getApplicationContext(), f, Toast.LENGTH_LONG).show();




                                        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                        dbHandler = new DbHandler(getApplicationContext());
                                        aujourdhui = new Date();
                                        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                        //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                        notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                        notifications("Modification du Mot de Passe", f);
                                        //dbHandler.insertUserDetails("Modification du Mot de Passe",f, "0", shortDateFormat.format(aujourdhui));

                                        int pos = f.toLowerCase().indexOf("succes");
                                        if (pos >= 0) {
                                            build_error = new AlertDialog.Builder(ModifierCompte.this);
                                            View view = LayoutInflater.from(ModifierCompte.this).inflate(R.layout.alert_dialog_success, null);
                                            TextView title = (TextView) view.findViewById(R.id.title);
                                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                            title.setText(getString(R.string.information));
                                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                            statutOperation.setText(f);
                                            build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ancienPass.setText("");
                                                    NouveauPass.setText("");
                                                    confirmPass.setText("");
                                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                                    intent.putExtra("telephone", telephone1);
                                                    startActivity(intent);
                                                }
                                            });
                                            build_error.setCancelable(false);
                                            build_error.setView(view);
                                            build_error.show();
                                        } else{
                                            build_error = new AlertDialog.Builder(ModifierCompte.this);
                                            View view = LayoutInflater.from(ModifierCompte.this).inflate(R.layout.alert_dialog_success, null);
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
                                        }




                                      /*  if(f.equalsIgnoreCase("nouveau mot de passe non valide")){
                                            new AlertDialog.Builder(ModifierCompte.this)
                                                    .setMessage("Numéro ou ancien mot de passe incorrects")
                                                    .setPositiveButton("OK", null)
                                                    .setCancelable(false)
                                                    .show();
                                            Toast.makeText(ModifierCompte.this, "Numéro ou ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(f.equalsIgnoreCase("Erreur de la requete")){
                                            new AlertDialog.Builder(ModifierCompte.this)
                                                    .setMessage("Numéro ou ancien mot de passe incorrects")
                                                    .setPositiveButton("OK", null)
                                                    .setCancelable(false)
                                                    .show();
                                            Toast.makeText(ModifierCompte.this, "Login ou ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(f.equalsIgnoreCase("Modification Effectuée avec succes"))
                                        {
                                            ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                            dbHandler = new DbHandler(getApplicationContext());
                                            aujourdhui = new Date();
                                            shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                            //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                            notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                            notifications("Modification du Mot de Passe", f);
                                            dbHandler.insertUserDetails("Modification du Mot de Passe",f, "0", shortDateFormat.format(aujourdhui));

                                            ancienPass.setText("");
                                            NouveauPass.setText("");
                                            confirmPass.setText("");
                                            Intent intent = new Intent(getApplicationContext(), Login.class);
                                            intent.putExtra("telephone", telephone1);
                                            startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(ModifierCompte.this, "Une Erreur est survenue lors de la modification de votre numéro", Toast.LENGTH_SHORT).show();

                                            ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                            dbHandler = new DbHandler(getApplicationContext());
                                            aujourdhui = new Date();
                                            shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                            //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                            notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                            notifications("Modification du Mot de Passe", "Une Erreur est survenue lors de la modification de votre numéro");
                                            dbHandler.insertUserDetails("Modification du Mot de Passe","Une Erreur est survenue lors de la modification de votre numéro", "0", shortDateFormat.format(aujourdhui));
                                        }*/
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
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                e.printStackTrace();
                                try {
                                    Thread.sleep(2000);
                                    //Toast.makeText(Modifier_Password.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }
        });
    }



    public void notifications(String titles, String subtitles){

        ///////////////DEBUT NOTIFICATIONS///////////////////////////////
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notif_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notif_expanded);

        Intent clickIntent = new Intent(getApplicationContext(), NotifReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, clickIntent, 0);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1, titles);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, subtitles);

        expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.logo2);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_min)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(1, notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
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
}
