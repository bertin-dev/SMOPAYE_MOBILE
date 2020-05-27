package com.ezpass.smopaye_mobile.vuesAdmin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class ModifSuppGoogleUser extends AppCompatActivity {

    private EditText nom,prenom,telephone,cni,numCarte, adresse;
    private Spinner sexe, statut, typeChauffeur, typePjustificative;
    private Button btnUpdate, btnDelete, btnOpenNFC;
    private ProgressDialog progressDialog;
    /////////////////////////////////////////////////////////////////////////////////
    Handler handler;
    Runnable runnable;
    Timer timer;
    Thread readThread;
    private byte blockNum_1 = 1;
    private byte blockNum_2 = 2;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    long time1, time2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    Nfc nfc = new Nfc(this);
    DialogInterface dialog;
    AlertDialog.Builder build_error;


    private CheckBox AbonnementMensuel;
    private CheckBox AbonnementHebdomadaire;
    private CheckBox AbonnementService;


    //BD LOCALE
    private DbHandler dbHandler;
    private DbUser dbUser;
    private Date aujourdhui;
    private DateFormat shortDateFormat;



    //SERVICES GOOGLE FIREBASE
    FirebaseAuth auth;
    DatabaseReference reference;
    APIService apiService;
    FirebaseUser fuser;


    private String abonnement = "service";


    private LinearLayout internetIndisponible, authWindows;
    private Button btnReessayer;
    ImageView conStatusIv;
    TextView titleNetworkLimited, msgNetworkLimited;




    //-----------------
    private String key;
    private String name;
    private String carte;
    private String number;
    private String session;


    private String last_name;
    private String genre;
    private String cni_or_password;
    private String adress;
    private String typeUtilisateur;
    private String abonn;
    //------------------


    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modif_supp_google_user);



        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.modifSuppGoogleUser));
        //getSupportParentActivityIntent().putExtra("resultatBD", "Administrateur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        //-----------------------------------------
        key = getIntent().getStringExtra("key");
        name = getIntent().getStringExtra("nom");
        carte = getIntent().getStringExtra("id_carte");
        number = getIntent().getStringExtra("telephone");
        session = getIntent().getStringExtra("session");


        last_name = getIntent().getStringExtra("prenom");
        genre = getIntent().getStringExtra("sexe");


        int position = getIntent().getStringExtra("cni").indexOf("-");
        String[] cni1 = getIntent().getStringExtra("cni").split("-");
        if(position > 0){
            cni_or_password = cni1[1];
        } else{
            cni_or_password = cni1[0];
        }
        //String[] parts = getIntent().getStringExtra("cni").split("-");
        //cni_or_password = getIntent().getStringExtra("cni");

        adress = getIntent().getStringExtra("adresse");
        typeUtilisateur = getIntent().getStringExtra("typeUser");
        abonn = getIntent().getStringExtra("abonnement");
        //------------------------------------------


        progressDialog = new ProgressDialog(ModifSuppGoogleUser.this);
        build_error = new AlertDialog.Builder(ModifSuppGoogleUser.this);


        nom = (EditText) findViewById(R.id.nom);
        nom.setText(name);

        prenom = (EditText) findViewById(R.id.prenom);
        prenom.setText(last_name);

        sexe = (Spinner) findViewById(R.id.sexe);
        sexe.setSelection(getIndex_SpinnerItem(sexe, genre));

        telephone = (EditText) findViewById(R.id.numeroTel);
        telephone.setText(number);

        cni = (EditText) findViewById(R.id.cni);
        cni.setText(cni_or_password);

        statut = (Spinner) findViewById(R.id.statut);
        statut.setSelection(getIndex_SpinnerItem(statut, session));

        numCarte = (EditText) findViewById(R.id.numCarte);
        numCarte.setText(carte);

        typeChauffeur = (Spinner) findViewById(R.id.typeChauffeur);
        typeChauffeur.setSelection(getIndex_SpinnerItem(typeChauffeur, typeUtilisateur));

        adresse = (EditText) findViewById(R.id.adresse);
        adresse.setText(adress);


        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnOpenNFC = (Button) findViewById(R.id.btnOpenNFC);
        // operateur = (Spinner) findViewById(R.id.operateurs);

        AbonnementMensuel = (CheckBox) findViewById(R.id.AbonnementMensuel);
        AbonnementHebdomadaire = (CheckBox) findViewById(R.id.AbonnementHebdomadaire);
        AbonnementService = (CheckBox) findViewById(R.id.AbonnementService);
        typePjustificative = (Spinner) findViewById(R.id.typePjustificative);

        authWindows = (LinearLayout) findViewById(R.id.authWindows);
        internetIndisponible = (LinearLayout) findViewById(R.id.internetIndisponible);
        btnReessayer = (Button) findViewById(R.id.btnReessayer);
        conStatusIv = (ImageView) findViewById(R.id.conStatusIv);
        titleNetworkLimited = (TextView) findViewById(R.id.titleNetworkLimited);
        msgNetworkLimited = (TextView) findViewById(R.id.msgNetworkLimited);

        //SERVICE GOOGLE FIREBASE
        auth = FirebaseAuth.getInstance();
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();




        // Initializing a String Array
        String[] statut1 = new String[]{
                "Utilisateur",
                "Agent",
                "Administrateur",
                "Accepteur"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,statut1);
        spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
        statut.setAdapter(spinnerArrayAdapter1);


        // Initializing a String Array
        String[] typeChauffeur1 = new String[]{
                "moto_taxi",
                "Chauffeur",
                "cargo",
                "bus inter urbain",
                "Commerçant",
                "restaurant etudiant",
                "Chauffeur",
                "autre"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,typeChauffeur1);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(spinnerArrayAdapter);


        // Initializing a String Array
        String[] sexe1 = new String[]{
                "Masculin",
                "Feminin"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,sexe1);
        spinnerArrayAdapter3.setDropDownViewResource(R.layout.spinner_item);
        sexe.setAdapter(spinnerArrayAdapter3);


        // Initializing a String Array
        String[] pieceJ = new String[]{
                "CNI",
                "passport",
                "recipissé",
                "carte de séjour",
                "carte d'étudiant"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter4 = new ArrayAdapter<String>(
                this,R.layout.spinner_item,pieceJ);
        spinnerArrayAdapter4.setDropDownViewResource(R.layout.spinner_item);
        typePjustificative.setAdapter(spinnerArrayAdapter4);


        //VERIFICATION DE L'ETAT DU CHANGEMENT DE STATUT
        statut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("accepteur")){
                    //typeChauffeur.setVisibility(View.VISIBLE);
                    addItemsOnSpinner2();
                }
                else if(statut.getSelectedItem().toString().toLowerCase().equalsIgnoreCase("utilisateur")){
                    //typeChauffeur.setVisibility(View.VISIBLE);
                    addItemsOnSpinner3();
                }
                else {
                    addItemsOnSpinner1();
                    // typeChauffeur.setVisibility(View.GONE);
                    //typeChauffeur.setAd
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        //DETECTION DE TYPE DE CARTE ET SON ID
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHECK_NFC_TIMEOUT: {
                        Toast.makeText(getApplicationContext(), "Check card time out!", Toast.LENGTH_LONG).show();
                       /* open_btn.setEnabled(true);
                        close_btn.setEnabled(false);
                        check_btn.setEnabled(false);*/
                    }
                    break;
                    case SHOW_NFC_DATA: {
                        byte[] uid_data = (byte[]) msg.obj;
                        if (uid_data[0] == 0x42) {
                            // TYPE B类（暂时只支持cpu卡）
                            byte[] atqb = new byte[uid_data[16]];
                            byte[] pupi = new byte[4];
                            String type = null;

                            System.arraycopy(uid_data, 17, atqb, 0, uid_data[16]);
                            System.arraycopy(uid_data, 29, pupi, 0, 4);

                            if (uid_data[1] == B_CPU) {
                                type = "CPU";
                               /* sendApduBtn.setEnabled(true);
                                getAtsBtn.setEnabled(true);*/
                            } else {
                                type = "unknow";
                            }

                            new AlertDialog.Builder(ModifSuppGoogleUser.this)
                                    .setMessage(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                            "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                            "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();

                           /* uid_editText.setText(getString(R.string.card_type) + getString(R.string.type_b) + " " + type +
                                    "\r\n" + getString(R.string.atqb_data) + StringUtil.toHexString(atqb) +
                                    "\r\n" + getString(R.string.pupi_data) + StringUtil.toHexString(pupi));*/

                        } else if (uid_data[0] == 0x41) {
                            // TYPE A类（CPU, M1）
                            byte[] atqa = new byte[2];
                            byte[] sak = new byte[1];
                            byte[] uid = new byte[uid_data[5]];
                            String type = null;

                            System.arraycopy(uid_data, 2, atqa, 0, 2);
                            System.arraycopy(uid_data, 4, sak, 0, 1);
                            System.arraycopy(uid_data, 6, uid, 0, uid_data[5]);

                            if (uid_data[1] == A_CPU) {
                                type = "CPU";
                                /*sendApduBtn.setEnabled(true);
                                getAtsBtn.setEnabled(true);*/
                            } else if (uid_data[1] == A_M1) {
                                type = "M1";
                                // authenticateBtn.setEnabled(true);
                            } else {
                                type = "unknow";
                            }
                           /* new AlertDialog.Builder(Login.this)
                                    .setMessage(getString(R.string.card_type) + getString(R.string.type_a) + " " + type +
                                            "\r\n" + getString(R.string.atqa_data) + StringUtil.toHexString(atqa) +
                                            "\r\n" + getString(R.string.sak_data) + StringUtil.toHexString(sak) +
                                            "\r\n" + getString(R.string.uid_data) + StringUtil.toHexString(uid))
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();*/
                            //numCarte.setText(StringUtil.toHexString(uid));
                            m1CardAuthenticate();
                            progressDialog.dismiss();
                            try {
                                nfc.close();
                            } catch (TelpoException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.e(TAG, "unknow type card!!");
                        }
                    }
                    break;

                    default:
                        break;
                }
            }
        };


        btnReessayer.setOnClickListener(this::checkNetworkConnectionStatus);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {

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


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //TOUT EST OK
                                User user = new User();
                                user.setNom(nom.getText().toString());
                                user.setPrenom(prenom.getText().toString());
                                user.setTel(telephone.getText().toString());
                                user.setCni(cni.getText().toString());
                                user.setAdresse(adresse.getText().toString());
                                user.setSexe(sexe.getSelectedItem().toString());
                                user.setSession(statut.getSelectedItem().toString());
                                user.setTypeUser(typeChauffeur.getSelectedItem().toString());
                                user.setId_carte(numCarte.getText().toString());
                                user.setAbonnement(abonnement);


                                new GoogleUtilisateur().updateUser(key, user, new GoogleUtilisateur.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<User> users, List<String> keys) {

                                    }

                                    @Override
                                    public void DataIsInserted() {

                                    }

                                    @Override
                                    public void DataIsUpdated() {

                                        FirebaseUser userUpdate = FirebaseAuth.getInstance().getCurrentUser();
                                        // Get auth credentials from the user for re-authentication
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential("sm"+number+"@smopaye.cm", number); // Current Login Credentials \\
                                        // Prompt the user to re-provide their sign-in credentials
                                        userUpdate.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "User re-authenticated.");
                                                        //Now change your email address \\
                                                        //----------------Code for Changing Email Address----------\\
                                                        FirebaseUser userUpdate1 = FirebaseAuth.getInstance().getCurrentUser();
                                                        userUpdate1.updateEmail("sm"+telephone.getText().toString().trim()+"@smopaye.cm")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "User email address updated.");

                                                                            progressDialog.dismiss();

                                                                            Toast.makeText(ModifSuppGoogleUser.this, getString(R.string.modifierEffectue), Toast.LENGTH_SHORT).show();

                                                                            View view = LayoutInflater.from(ModifSuppGoogleUser.this).inflate(R.layout.alert_dialog_success, null);
                                                                            TextView title = (TextView) view.findViewById(R.id.title);
                                                                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                                            title.setText(getString(R.string.information));
                                                                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                                                            statutOperation.setText(getString(R.string.modifierEffectue));
                                                                            build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    finish();
                                                                                }
                                                                            });
                                                                            build_error.setCancelable(false);
                                                                            build_error.setView(view);
                                                                            build_error.show();

                                                                        } else {
                                                                            Toast.makeText(ModifSuppGoogleUser.this, "Une Erreur est survenue lors de la modification.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                        //----------------------------------------------------------\\
                                                    }
                                                });

                                    }

                                    @Override
                                    public void DataIsDeleted() {

                                    }
                                });


                                progressDialog.dismiss();

                            }
                        });

                    }
                }).start();




            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {

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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //TOUT EST OK
                                new GoogleUtilisateur().deleteUser(key, new GoogleUtilisateur.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<User> users, List<String> keys) {

                                    }

                                    @Override
                                    public void DataIsInserted() {

                                    }

                                    @Override
                                    public void DataIsUpdated() {

                                    }

                                    @Override
                                    public void DataIsDeleted() {

                                        final FirebaseUser userDelete = FirebaseAuth.getInstance().getCurrentUser();
                                        // Get auth credentials from the user for re-authentication. The example below shows
                                        // email and password credentials but there are multiple possible providers,
                                        // such as GoogleAuthProvider or FacebookAuthProvider.
                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential("sm"+number+"@smopaye.cm", number);

                                        // Prompt the user to re-provide their sign-in credentials
                                        userDelete.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        userDelete.delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "User account deleted.");

                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(ModifSuppGoogleUser.this, getString(R.string.suppressionEffectue), Toast.LENGTH_SHORT).show();

                                                                            View view = LayoutInflater.from(ModifSuppGoogleUser.this).inflate(R.layout.alert_dialog_success, null);
                                                                            TextView title = (TextView) view.findViewById(R.id.title);
                                                                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                                            title.setText(getString(R.string.information));
                                                                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                                                            statutOperation.setText(getString(R.string.suppressionEffectue));
                                                                            build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    finish();
                                                                                }
                                                                            });
                                                                            build_error.setCancelable(false);
                                                                            build_error.setView(view);
                                                                            build_error.show();


                                                                        } else{
                                                                            Toast.makeText(ModifSuppGoogleUser.this, getString(R.string.erreurSurvenue2), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                });

                                    }
                                });

                                progressDialog.dismiss();
                            }
                        });



                    }
                }).start();


            }
        });


    }


    //--------------------------------------

    private int getIndex_SpinnerItem(Spinner spinner, String item){
        int index = 0;
        for(int i=0; i<spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).equals(item)){
                index = i;
                break;
            }
        }
        return index;
    }
    //--------------------------------------


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
            Toast.makeText(ModifSuppGoogleUser.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }



    public class ReadThread extends Thread {
        byte[] nfcData = null;

        @Override
        public void run() {
            try {

                time1 = System.currentTimeMillis();
                nfcData = nfc.activate(10 * 1000); // 10s
                time2 = System.currentTimeMillis();
                Log.e("yw activate", (time2 - time1) + "");
                if (null != nfcData) {
                    handler.sendMessage(handler.obtainMessage(SHOW_NFC_DATA, nfcData));
                } else {
                    Log.d(TAG, "Check Card timeout...");
                    handler.sendMessage(handler.obtainMessage(CHECK_NFC_TIMEOUT, null));
                }
            } catch (TelpoException e) {
                Log.e("yw", e.toString());
                e.printStackTrace();
            }
        }
    }



    public static boolean isValid(String str)
    {
        boolean isValid = false;
        String expression = "^[a-z_A-Z0-9éè ]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
    }


    public void addItemsOnSpinner3() {

        //spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("étudiant");
        list.add("élève");
        list.add("particulier");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(dataAdapter);



        /*ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,typeChauffeur1);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(spinnerArrayAdapter);*/

    }


    public void addItemsOnSpinner2() {

        //spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("moto_taxis");
        list.add("Chauffeur");
        list.add("mini-bus");
        list.add("bus inter urbain");
        list.add("restaurant étudiant");
        list.add("monetbil");
        list.add("autre");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(dataAdapter);



        /*ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,typeChauffeur1);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(spinnerArrayAdapter);*/

    }

    public void addItemsOnSpinner1() {

        //spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("smopaye");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeChauffeur.setAdapter(dataAdapter);
    }




    public void m1CardAuthenticate() {
        Boolean status = true;
        byte[] passwd = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        try {

            time1 = System.currentTimeMillis();
            nfc.m1_authenticate(blockNum_1, (byte) 0x0B, passwd);//0x0B
            time2 = System.currentTimeMillis();
            Log.e("yw m1_authenticate", (time2 - time1) + "");


        } catch (TelpoException e) {
            status = false;
            e.printStackTrace();
            Log.e("yw", e.toString());
        }

        if (status) {
            Log.d(TAG, "m1CardAuthenticate success!");
            //writeBlockData();
            //readBlockData();

            //OwriteValueData();
            readValueDataCourt();
        } else {
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "m1CardAuthenticate fail!");
        }
    }


    public void readValueDataCourt() {
        byte[] data = null;
        try {
            data = nfc.m1_read_value(blockNum_2);
        } catch (TelpoException e) {
            e.printStackTrace();
        }

        if (null == data) {
            Log.e(TAG, "readValueBtn fail!");
            Toast.makeText(this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
        } else {
            numCarte.setText(StringUtil.toHexString(data));
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


    //gestion des abonnements
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

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
                if (checked)
                {
                    Toast.makeText(this, AbonnementService.getText().toString(), Toast.LENGTH_SHORT).show();
                    AbonnementMensuel.setChecked(false);
                    AbonnementHebdomadaire.setChecked(false);
                    abonnement = "service";
                }
                else{
                    AbonnementService.setChecked(true);
                    AbonnementMensuel.setChecked(false);
                    AbonnementHebdomadaire.setChecked(false);
                    abonnement = "service";
                }
                break;
        }
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
