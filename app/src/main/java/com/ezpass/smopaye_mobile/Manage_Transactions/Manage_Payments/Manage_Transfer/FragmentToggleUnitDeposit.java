package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Transfer;

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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

import static android.content.Context.MODE_PRIVATE;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;


public class FragmentToggleUnitDeposit extends Fragment
                                       implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "FragmentToggleUnitDepos";
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;
    private ACProgressPie dialog2;
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

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    @BindView(R.id.til_montant)
    TextInputLayout til_montant;
    @BindView(R.id.tie_montant)
    TextInputEditText tie_montant;
    @BindView(R.id.typeSolde)
    Spinner typeSolde;

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

    private String[] statut1;
    private String myId_card;
    private String code_number_sender;


    public FragmentToggleUnitDeposit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changeTheme();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toggle_unit_deposit, container, false);

        Intent intent = getActivity().getIntent();
        myId_card = intent.getStringExtra("myId_card");
        code_number_sender = intent.getStringExtra("compte");

        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this, view);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        //service = RetrofitBuilder.createService(ApiService.class);
        //tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        progressDialog = new ProgressDialog(getActivity());
        build_error = new AlertDialog.Builder(getActivity());
        //service google firebase
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();


        /*Appels de toutes les méthodes qui seront utilisées*/
        setupRulesValidatForm();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget(view);
        }



        //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            // Initializing a String Array
            statut1 = new String[]{"DEPOT", "UNITE"
            };
        } else{
            // Initializing a String Array
            statut1 = new String[]{"DEPOSIT", "UNIT"};
        }
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            // Initializing an ArrayAdapter
            ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(getContext(),R.layout.spinner_item_red,statut1);
            spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item_red);
            typeSolde.setAdapter(spinnerArrayAdapter1);
        } else{
            // Initializing an ArrayAdapter
            ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,statut1);
            spinnerArrayAdapter1.setDropDownViewResource(R.layout.spinner_item);
            typeSolde.setAdapter(spinnerArrayAdapter1);
        }


        return view;
    }


    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     * @since 2020
     * */
    private void setupRulesValidatForm(){

        //coloration des champs lorsqu'il y a erreur
        til_montant.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        validator.addValidation(getActivity(), R.id.til_montant, RegexTemplate.NOT_EMPTY, R.string.insererMontant);
    }


    /**
     * transfert() méthode permettant de démarrer l'opération de transfert
     * @since 2019
     * */
    @OnClick(R.id.btnToggleUnitDeposit)
    void toggleUnitDeposit(){

            if(!validateMontant(til_montant)){
                return;
            }
            String montant = til_montant.getEditText().getText().toString();
        String typeSold = (typeSolde.getSelectedItem().toString().trim().toUpperCase().equalsIgnoreCase("UNITE") ||
                typeSolde.getSelectedItem().toString().trim().toUpperCase().equalsIgnoreCase("UNIT") ? "unity" : "deposit");
            validator.clear();

            /*Action à poursuivre si tous les champs sont remplis*/
            if(validator.validate()){
                //********************DEBUT***********
                dialog2 = new ACProgressPie.Builder(getActivity())
                        .ringColor(Color.WHITE)
                        .pieColor(Color.WHITE)
                        .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                        .build();
                dialog2.show();
                //*******************FIN*****
                toggleUnitDepositInSmopayeServer(myId_card, typeSold,  montant);
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
            progressDialog = ProgressDialog.show(getActivity(), getString(R.string.connexion), getString(R.string.encours), true);
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
            Toast.makeText(getActivity(), getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }

    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(getActivity().findViewById(R.id.fragment_toggle_unit_deposit), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
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
            snackbar = Snackbar.make(getActivity().findViewById(R.id.fragment_toggle_unit_deposit), message, Snackbar.LENGTH_INDEFINITE);
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


    private void toggleUnitDepositInSmopayeServer(String card_id, String action, String withDrawalAmount) {

        call = service.toggleBalance(card_id, action, Float.parseFloat(withDrawalAmount));
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse: " + response);
                dialog2.dismiss();

                assert response.body() != null;
                if(response.isSuccessful()){
                    //String msgReceiver = response.body().getMessage().getCard_receiver().getNotif();
                    //String msgSender = response.body().getMessage().getCard_sender().getNotif();
                    //successResponse(code_number_sender, msgReceiver, "", msgSender);

                    successResponse("", "", code_number_sender, response.message());

                }
                else{

                    if(response.code() == 422){
                        handleErrors(response.errorBody());
                    } else{
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        errorResponse(code_number_sender, apiError.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {

                dialog2.dismiss();
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

    private void successResponse(String id_cardReceiver, String msgReceiver, String id_cardSender, String msgSender) {

        /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
        //SERVICE GOOGLE FIREBASE

        /*****************************************************RECEIVER MESSAGE******************************/
        Query queryReceiver = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("id_carte")
                .equalTo(id_cardReceiver);

        queryReceiver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals(id_cardReceiver)) {
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.transfert), msgReceiver, "success");
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


/*****************************************************SENDER MESSAGE******************************/
        Query querySender = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("id_carte")
                .equalTo(id_cardSender);

        querySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals(id_cardSender)) {
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.transfert), id_cardSender, "success");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getActivity(), getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.transfert), msgSender);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.transfert), msgSender, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(msgSender);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();

        //supression des champs de saisis
        tie_montant.setText("");
    }

    private void errorResponse(String id_cardReceiver, String msgReceiver){
        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.transfert), msgReceiver);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.transfert), msgReceiver, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
        statutOperation.setText(msgReceiver);
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

            if(error.getKey().equals("withDrawalAmount")){
                til_montant.setError(error.getValue().get(0));
            }
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


    private void LocalNotification(String titles, String subtitles){

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


    /*private void status(String status){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        status("online");
    }



    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }*/


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget(View view) {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            //toolbar
            //desctiption du module
            LinearLayout desc = (LinearLayout) view.findViewById(R.id.descModule);
            TextView myTitle = (TextView) view.findViewById(R.id.descContent);
            myTitle.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            desc.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edittextborder_red));
            TextView typeTransfer = (TextView) view.findViewById(R.id.titleTypeSolde);
            typeTransfer.setTextColor(getResources().getColor(R.color.colorPrimaryRed));

            //montant
            tie_montant.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryRed));
            tie_montant.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edittextborder_red));
            //Button
            view.findViewById(R.id.btnTransfert).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_rounded_red));
            view.findViewById(R.id.btnOpenNFC).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_rounded_red));

            //network not available ic_wifi_red
            conStatusIv.setImageResource(R.drawable.ic_wifi_red);
            titleNetworkLimited.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryRed));
            msgNetworkLimited.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryRed));
            view.findViewById(R.id.btnReessayer).setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.btn_rounded_red));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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


}