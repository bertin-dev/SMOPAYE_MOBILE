package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.os.Message;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ezpass.smopaye_mobile.web_service_response.AllMyHomeResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.Constant;
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
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;
import com.ezpass.smopaye_mobile.web_service_response.HomeResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.nfc.Nfc;
import com.telpo.tps550.api.util.StringUtil;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRetraitChezSmopaye extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "RetraitChezSmopaye";
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;
    /////////////////////////////////////////////////////////////////////////////////
    private Handler handler;
    private Runnable runnable;
    private Timer timer;
    private Thread readThread;
    private final int CHECK_NFC_TIMEOUT = 1;
    private final int SHOW_NFC_DATA = 2;
    long time1, time2;
    private byte blockNum_1 = 1;
    private byte blockNum_2 = 2;
    private final byte B_CPU = 3;
    private final byte A_CPU = 1;
    private final byte A_M1 = 2;
    private Nfc nfc = new Nfc(getContext());

    /*NOTIFICATION*/
    private NotificationManagerCompat notificationManager;


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
    private Call<HomeResponse> call;
    private Call<AllMyHomeResponse> call2;


    @BindView(R.id.til_numCartSmopaye)
    TextInputLayout til_numCartSmopaye;
    @BindView(R.id.til_montantSmopaye)
    TextInputLayout til_montantSmopaye;

    @BindView(R.id.tie_numCartSmopaye)
    TextInputEditText tie_numCartSmopaye;
    @BindView(R.id.tie_montantSmopaye)
    TextInputEditText tie_montantSmopaye;

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




    private String file = "tmp_number";
    private int c;
    private String tmp_number = "";

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    private String file2 = "tmp_card_number";
    private int c2;
    private String tmp_card_number = "";


    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Spinner typeRetrait;
    @BindView(R.id.til_numCompteBeneficiaire)
    TextInputLayout til_numCompteBeneficiaire;
    @BindView(R.id.tie_numCompteBeneficiaire)
    TextInputEditText tie_numCompteBeneficiaire;

    private String operation = "";
    int positionTypeTransfert = 0;

    public FragmentRetraitChezSmopaye() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changeTheme();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_retrait_chez_smopaye, container, false);



        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this, view);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        //service = RetrofitBuilder.createService(ApiService.class);
        //tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        progressDialog = new ProgressDialog(getContext());
        build_error = new AlertDialog.Builder(getContext());
        //service google firebase
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        typeRetrait = (Spinner) view.findViewById(R.id.typeRetrait);


        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file);
            while ((c = fIn.read()) != -1){
                tmp_number = tmp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn2 = getActivity().openFileInput(file2);
            while ((c2 = fIn2.read()) != -1){
                tmp_card_number = tmp_card_number + Character.toString((char)c2);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        /*Appels de toutes les méthodes qui seront utilisées*/
        setupRulesValidatForm();
        callHandlerMethod();

        tie_numCartSmopaye.setText(tmp_card_number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget(view);
        }


        typeRetrait.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    til_numCompteBeneficiaire.setVisibility(View.VISIBLE);
                    til_numCartSmopaye.setVisibility(View.GONE);
                    positionTypeTransfert = position;
                    //operation = "RETRAIT_COMPTE_VIA_MONETBILL";
                    // Toast.makeText(getContext(), operation, Toast.LENGTH_LONG).show();
                } else {
                    til_numCompteBeneficiaire.setVisibility(View.GONE);
                    til_numCartSmopaye.setVisibility(View.VISIBLE);
                    positionTypeTransfert = position;
                    operation = "RETRAIT_SMOPAYE";
                    //Toast.makeText(getContext(), operation, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    private void callHandlerMethod() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHECK_NFC_TIMEOUT: {
                        Toast.makeText(getContext(), "Check card time out!", Toast.LENGTH_LONG).show();
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

                            new AlertDialog.Builder(getActivity())
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

                            progressDialog.dismiss();
                            m1CardAuthenticate();

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
    }

    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     * @since 2020
     * */
    private void setupRulesValidatForm(){

        //coloration des champs lorsqu'il y a erreur
        til_numCartSmopaye.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_montantSmopaye.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        til_numCompteBeneficiaire.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));

        validator.addValidation(getActivity(), R.id.til_numCartSmopaye, RegexTemplate.NOT_EMPTY, R.string.verifierNumero);
        validator.addValidation(getActivity(), R.id.til_montantSmopaye, RegexTemplate.NOT_EMPTY, R.string.insererPassword);
        validator.addValidation(getActivity(), R.id.til_numCompteBeneficiaire, RegexTemplate.NOT_EMPTY, R.string.insererCompte);
    }

    /**
     * openNFC() méthode permettant recupérer le contenu d'une carte NFC
     * @since 2019
     * */
    @OnClick(R.id.btnOpenNFC)
    void openNFC(){
        try {
            nfc.open();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // On ajoute un message à notre progress dialog
                    progressDialog.setMessage(getString(R.string.passerCarte));
                    // On donne un titre à notre progress dialog
                    progressDialog.setTitle(getString(R.string.attenteCarte));
                    // On spécifie le style
                    //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    // On affiche notre message
                    progressDialog.show();
                    //build.setPositiveButton("ok", new View.OnClickListener()

                }
            });

        } catch (TelpoException e) {
            e.printStackTrace();
        }
        readThread = new ReadThread();
        readThread.start();
    }


    /**
     * retrait() méthode permettant de démarrer l'opération de retrait
     * @since 2019
     * */
    @OnClick(R.id.btnRetraitSmopaye)
    void retrait(){

        //compte
        if(positionTypeTransfert == 1){
            if(!validateCompte(til_numCompteBeneficiaire) | !validateMontant(til_montantSmopaye)){
                return;
            }

            String account_number_receiver = til_numCompteBeneficiaire.getEditText().getText().toString();
            String montant = til_montantSmopaye.getEditText().getText().toString();
            validator.clear();

            /*Action à poursuivre si tous les champs sont remplis*/
            if(validator.validate()){

                //********************DEBUT***********
                getActivity().runOnUiThread(new Runnable() {
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
                retraitCompteACompteInSmopayeServer(montant, account_number_receiver);
            }
        } //carte
        else {

            if (!validateCompte(til_numCartSmopaye) | !validateMontant(til_montantSmopaye)) {
                return;
            }

            String id_card = til_numCartSmopaye.getEditText().getText().toString();
            String montant = til_montantSmopaye.getEditText().getText().toString();
            validator.clear();
            /*Action à poursuivre si tous les champs sont remplis*/
            if (validator.validate()) {

                //********************DEBUT***********
                getActivity().runOnUiThread(new Runnable() {
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
                retraitSmopaye(id_card, montant, tmp_card_number, tmp_number);
            }

        }
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if(activeInfo != null && activeInfo.isConnected()){
            progressDialog = ProgressDialog.show(getContext(), getString(R.string.connexion), getString(R.string.encours), true);
            progressDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    getActivity().recreate();
                }
            }, 2000); // 2000 milliseconds delay

        } else{
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(getView().findViewById(R.id.retrait_smopaye), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else{
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(getView().findViewById(R.id.retrait_smopaye), message, Snackbar.LENGTH_INDEFINITE);
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
    public void onResume() {
        super.onResume();

        //register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        getActivity().registerReceiver(connectivityReceiver, intentFilter);

        //register connection status listener
        NotifApp.getInstance().setConnectivityListener(this);
    }



    /**
     * onDestroy() methode Callback qui permet de détruire une activity et libérer l'espace mémoire
     * @since 2020
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }

        if(call2 != null){
            call2.cancel();
            call2 = null;
        }
    }

    private void retraitSmopaye(String id_card, String montant, String idcard_donataire, String telephone) {

        call = service.transaction(Float.parseFloat(montant), idcard_donataire, id_card, operation);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()){

                    String msgReceiver = response.body().getMessage().getCard_receiver().getNotif();
                    String msgSender = response.body().getMessage().getCard_sender().getNotif();

                    successResponse(id_card, msgReceiver);
                } else{

                    ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();

                    if(response.code() == 422){
                        handleErrors(response.errorBody());
                    } else {
                        errorResponse(id_card, apiError.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {

                progressDialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(getContext(), getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });

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
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.retraitSmopaye), response, "success");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.retraitSmopaye), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.retraitSmopaye), response, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
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
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.retraitSmopaye), response, "error");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.retraitSmopaye), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.retraitSmopaye), response, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
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

            if(error.getKey().equals("card_number")){
                til_numCartSmopaye.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("amount")){
                til_montantSmopaye.setError(error.getValue().get(0));
            }
        }

    }

    /**
     * validateCompte() méthode permettant de verifier si le numéro de compte inséré est valide
     * @param til_num_compte1
     * @return Boolean
     * @since 2019
     * */
    private Boolean validateCompte(TextInputLayout til_num_compte1){
        String my_numCompte = til_num_compte1.getEditText().getText().toString().trim();
        if(my_numCompte.isEmpty()){
            til_num_compte1.setError(getString(R.string.insererCompte));
            return false;
        } else if(my_numCompte.length() < 8){
            til_num_compte1.setError(getString(R.string.compteCourt));
            return false;
        } else {
            til_num_compte1.setError(null);
            return true;
        }
    }

    /**
     * validateMontant() méthode permettant de verifier si le montant inséré est valide
     * @param til_montant1
     * @return Boolean
     * @since 2019
     * */
    private boolean validateMontant(TextInputLayout til_montant1){
        String montant = til_montant1.getEditText().getText().toString().trim();
        if(montant.isEmpty()){
            til_montant1.setError(getString(R.string.insererMontant));
            return false;
        } else {
            til_montant1.setError(null);
            return true;
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
            Toast.makeText(getContext(), getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
        } else {
            til_numCartSmopaye.getEditText().setText(StringUtil.toHexString(data));
        }
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
                                            Toast.makeText(getActivity(), getString(R.string.echoue), Toast.LENGTH_SHORT).show();
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
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        RemoteViews collapsedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notif_collapsed);
        RemoteViews expandedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notif_expanded);

        Intent clickIntent = new Intent(getContext(), NotifReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(getContext(),
                0, clickIntent, 0);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1, titles);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, subtitles);

        expandedView.setImageViewResource(R.id.image_view_expanded, R.mipmap.logo_official);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo_official)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget(View view) {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            //desctiption du module
            LinearLayout desc = (LinearLayout) view.findViewById(R.id.descModule);
            TextView myTitle = (TextView) view.findViewById(R.id.descContent);
            myTitle.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            desc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edittextborder_red));
            TextView typRetraitTittle = (TextView) view.findViewById(R.id.typRetraitTittle);
            typRetraitTittle.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            //numero carte
            tie_numCartSmopaye.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
            tie_numCartSmopaye.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edittextborder_red));
            //montant
            tie_montantSmopaye.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
            tie_montantSmopaye.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edittextborder_red));
            //Button
            view.findViewById(R.id.btnRetraitSmopaye).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_rounded_red));
            view.findViewById(R.id.btnOpenNFC).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_rounded_red));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            getActivity().setTheme(Constant.theme);
        }else if (appTheme == 0){
            getActivity().setTheme(Constant.theme);
        }else{
            getActivity().setTheme(appTheme);
        }
    }

    private void retraitCompteACompteInSmopayeServer(String montant, String account_number_receiver) {

        call2 = service.retrait_compte_A_Compte(Float.parseFloat(montant), account_number_receiver);
        call2.enqueue(new Callback<AllMyHomeResponse>() {
            @Override
            public void onResponse(Call<AllMyHomeResponse> call, Response<AllMyHomeResponse> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse: " + response);
                progressDialog.dismiss();

                assert response.body() != null;
                if(response.isSuccessful()){
                    String msgReceiver = response.body().getMessage().getCompte_receiver().getNotif();
                    String msgSender = response.body().getMessage().getSender().getNotif();

                    successResponse("", msgReceiver);

                }
                else{

                    if(response.code() == 422){
                        handleErrors(response.errorBody());
                    } else{
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        errorResponse("", apiError.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<AllMyHomeResponse> call, Throwable t) {

                progressDialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(getContext(), getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


}
