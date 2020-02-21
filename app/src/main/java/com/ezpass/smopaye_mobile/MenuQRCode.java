package com.ezpass.smopaye_mobile;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpass.smopaye_mobile.ChaineConnexion.decryptData;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class MenuQRCode extends AppCompatActivity implements QRCodeModalDialog.ExampleDialogListener{

    private LinearLayout btn_scan, btn_readLecture;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;
    private LinearLayout internetIndisponible, authWindows;
    private Button btnReessayer;
    private ImageView conStatusIv;
    private TextView titleNetworkLimited, msgNetworkLimited;

    private String carteAccepteur = "";
    private String carteUtilisateur = "";
    private String montant = "";


    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    //SERVICES GOOGLE FIREBASE
    APIService apiService;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_qrcode);

        getSupportActionBar().setTitle(getString(R.string.AccueilQRCode));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_scan = (LinearLayout) findViewById(R.id.btn_scan);
        btn_readLecture = (LinearLayout) findViewById(R.id.btn_readLecture);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);

        //pour enchainer deux boites de dialogue simultanément il faut mettre "this" à la place de "getApplicationContext()"
        progressDialog = new ProgressDialog(this);
        //service google firebase
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MenuQRCode.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt(getString(R.string.ezpassScan));
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });


        btn_readLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AffichageQRCode.class);
                startActivity(intent);
            }
        });

        btnReessayer.setOnClickListener(this::checkNetworkConnectionStatus);
    }

    private void checkNetworkConnectionStatus(View view) {

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
            Toast.makeText(MenuQRCode.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
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
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
// handle scan result

        if(scanResult != null && scanResult.getContents() != null){

            int pos = scanResult.getContents().indexOf("E-ZPASS");
            if (pos >= 0) {
                String carteNumber = scanResult.getContents().substring(7, 15).toUpperCase();
                openDialog(carteNumber);
            } else{
                Toast.makeText(this, getString(R.string.erreurQRcode), Toast.LENGTH_SHORT).show();
            }

            /*String mdp = "E-ZPASS by " + getString(R.string.app_name) +  "/" + getPackageName() + "/123456789";
            HashMap<String, byte[]> map1 = getContentQRCode(scanResult.getContents());
            byte[] decrypted = decryptData(map1, mdp);
            if (decrypted != null)
            {
                String decryptedString = new String(decrypted);
                Log.e("MYAPP", "Decrypted String is : " + decryptedString);
                Toast.makeText(this, decryptedString, Toast.LENGTH_SHORT).show();
            }*/

            //openDialog(scanResult.getContents());
            /*FragmentManager fm = getSupportFragmentManager();
            Fragment newFrame = AccueilFragment.newInstanceQRCode(scanResult.toString(), carteUtilisateur, montant);
            fm.beginTransaction().replace(R.id.fragment_container, newFrame).commit();*/
        }
    }

    /*private static HashMap<String, byte[]> getContentQRCode(String resultatContentQRCode)
    {

        String [] split1 = resultatContentQRCode.split(",");

        String [] split2 = split1[0].split("=\\[");
        byte[] encrypt = split2[1].getBytes();


        String [] split3 = split1[1].split("=\\[");
        byte[] iv = split3[1].getBytes();


        String [] split4 = split1[2].split("=\\=[");
        byte[] salt = split4[1].getBytes();



        HashMap<String,byte[]> map = new HashMap<String,byte[]>();
        map.put("salt", encrypt);
        map.put("iv", iv);
        map.put("encrypted", salt);


          //equivalence de ce qui est en haut
        String [] split = resultatContentQRCode.split(",");
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();
        for(String temp : split){
            String [] tempo = temp.split("=\\[");
            map.put(tempo[0],tempo[1].getBytes());
        }


        return map;
    }*/


    public void openDialog(String accepteurNumCarte) {
        QRCodeModalDialog exampleDialog = new QRCodeModalDialog().newInstanceCode(accepteurNumCarte);
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String numCarteAccepteur, String numCarteUtilisateur, String montantUtilisateur) {

        new GetHttpResponse(numCarteAccepteur, numCarteUtilisateur, montantUtilisateur).execute();
        //LoadQRCode(numCarteAccepteur, numCarteUtilisateur, montantUtilisateur);
    }


    public class GetHttpResponse extends AsyncTask<Void, Void, Void> {

        private String accepteurNumber;
        private String userNumber;
        private String userAmont;

        public GetHttpResponse(String accepteurNumber, String userNumber, String userAmont) {
            this.accepteurNumber = accepteurNumber;
            this.userNumber = userNumber;
            this.userAmont = userAmont;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LoadQRCode(this.accepteurNumber, this.userNumber, this.userAmont);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }






    private void LoadQRCode(final String numCarteAccepteur, final String numCarteCli, final String montantCliPayer){
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
                    builder.appendQueryParameter("auth","Card");
                    builder.appendQueryParameter("login", "transfert");
                    builder.appendQueryParameter("MONTANT", montantCliPayer);
                    builder.appendQueryParameter("CARDNDON", numCarteCli);
                    builder.appendQueryParameter("CARDN", numCarteAccepteur);
                    builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());


                    URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() +builder.build().toString());//"http://192.168.20.11:1234/recharge.php"
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
                            Toast.makeText(MenuQRCode.this, getString(R.string.encoursTraitement), Toast.LENGTH_SHORT).show();
                        }
                    });

                    while (bufferedReader.ready() || data==""){
                        data+=bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();


                    final String f = data.trim();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();


                            int pos = f.toLowerCase().indexOf("succès");
                            if (pos >= 0) {


                                /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                //SERVICE GOOGLE FIREBASE
                                final String id_carte_sm = numCarteAccepteur.trim();

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
                                                    RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.paiementQRCode), f, "success");
                                                    //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MenuQRCode.this, getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(MenuQRCode.this, getString(R.string.userNonReconnu), Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
                                LocalNotification(getString(R.string.paiementQRCode), f);

                                ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                dbHandler = new DbHandler(getApplicationContext());
                                aujourdhui = new Date();
                                shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                dbHandler.insertUserDetails(getString(R.string.paiementQRCode), f, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


                                build_error = new AlertDialog.Builder(MenuQRCode.this);
                                View view = LayoutInflater.from(MenuQRCode.this).inflate(R.layout.alert_dialog_success, null);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                title.setText(getString(R.string.information));
                                imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                statutOperation.setText(f);
                                build_error.setPositiveButton("OK", null);
                                build_error.setCancelable(false);
                                build_error.setView(view);
                                build_error.show();
                            } else{


                                /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
                                //SERVICE GOOGLE FIREBASE
                                final String id_carte_sm = numCarteAccepteur.trim();

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
                                                    RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.paiementQRCode), f, "error");
                                                    //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MenuQRCode.this, getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(MenuQRCode.this, getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
                                LocalNotification(getString(R.string.paiementQRCode), f);

                                ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                dbHandler = new DbHandler(getApplicationContext());
                                aujourdhui = new Date();
                                shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                                dbHandler.insertUserDetails(getString(R.string.paiementQRCode), f, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

                                build_error = new AlertDialog.Builder(MenuQRCode.this);
                                View view = LayoutInflater.from(MenuQRCode.this).inflate(R.layout.alert_dialog_success, null);
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




                            Toast.makeText(getApplicationContext(), f, Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(MenuQRCode.this, jsonObject.getString("telephone"), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MenuQRCode.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                            } else{
                                progressDialog.dismiss();
                                authWindows.setVisibility(View.GONE);
                                internetIndisponible.setVisibility(View.VISIBLE);
                                conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                                titleNetworkLimited.setText(getString(R.string.connexionLimite));
                                //msgNetworkLimited.setText();
                                Toast.makeText(MenuQRCode.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    e.printStackTrace();
                    try {
                        Thread.sleep(2000);
                        //Toast.makeText(RechargePropreCompte.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(MenuQRCode.this, getString(R.string.echoue), Toast.LENGTH_SHORT).show();
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


}
