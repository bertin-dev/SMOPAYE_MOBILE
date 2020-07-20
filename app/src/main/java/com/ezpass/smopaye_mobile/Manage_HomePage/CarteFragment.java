package com.ezpass.smopaye_mobile.Manage_HomePage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.NotifApp;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.Profil_user.Card;
import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
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
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class CarteFragment extends Fragment
                           implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "CarteFragment";
    private ProgressDialog progressDialog;
    private AlertDialog.Builder build_error;
    private ACProgressFlower dialog;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<AllMyResponse> call;
    private Call<Card> cardCall;

    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    //SERVICES GOOGLE FIREBASE
    private APIService apiService;
    private FirebaseUser fuser;


    @BindView(R.id.AccountNumber)
    TextView AccountNumber;
    @BindView(R.id.AccountCard)
    TextView AccountCard;
    @BindView(R.id.tAbonnement)
    TextView tAbonnement;
    @BindView(R.id.tVdateExpirationCarte)
    TextView tVdateExpirationCarte;
    @BindView(R.id.sWtVerouillerCarte)
    Switch sWtVerouillerCarte;
    @BindView(R.id.etatDelaCarte)
    TextView etatDelaCarte;
    @BindView(R.id.updateDate)
    TextView updateDate;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.tel)
    TextView tel;

    private String etatCart = "";
    private String telephone = "";
    private String abonnement = "";

    private String i = "0";
    private String file = "tmp_etat_carte";
    private int c;
    private String temp_number = "";
    private String card_id = "";
    private String myId_card;
    private String myPersonalAccountNumber;
    private String myPersonalAccountState;
    private String myPersonalAccountAmount;
    private String unity;
    private String deposit;
    private String serial_number;
    private String typeCard;
    private String cardState;
    DecimalFormat df = new DecimalFormat("0.00"); // import java.text.DecimalFormat;





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


    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private String profil_complet;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        //********************DEBUT***********
       /* getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // On ajoute un message à notre progress dialog
                progressDialog.setMessage(getString(R.string.connexionserver));
                // On donne un titre à notre progress dialog
                progressDialog.setTitle(getString(R.string.encoursTelechargement));
                // On spécifie le style
                //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // On affiche notre message
                progressDialog.show();
                //build.setPositiveButton("ok", new View.OnClickListener()
            }
        });*/
        //*******************FIN*****


        dialog = new ACProgressFlower.Builder(getContext())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        //service google firebase
        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        assert getArguments() != null;
        telephone = getArguments().getString("telephone");
        abonnement = getArguments().getString("abonnement");
        myId_card = getArguments().getString("myId_card");
        myPersonalAccountNumber = getArguments().getString("myPersonalAccountNumber");
        myPersonalAccountState = getArguments().getString("myPersonalAccountState");
        myPersonalAccountAmount = getArguments().getString("myPersonalAccountAmount");
        profil_complet = getArguments().getString("profil_complet");

        build_error = new AlertDialog.Builder(getContext());
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        changeTheme();
        View view = inflater.inflate(R.layout.fragment_carte, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.maCarteSmopaye));

        detailsCard(myId_card);


        sWtVerouillerCarte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                final Switch btn = (Switch) v;
                final boolean switchChecked = btn.isChecked();

                if(switchChecked){
                    Toast.makeText(getActivity(), getString(R.string.votreCarteVerouille), Toast.LENGTH_SHORT).show();
                    checkStateCarte(card_id, "desactiver");
                    //i = "1";
                    etatDelaCarte.setText(getString(R.string.votreCarteVerouille));
                } else{
                    Toast.makeText(getActivity(), getString(R.string.votreCarteDeverouille), Toast.LENGTH_SHORT).show();
                    checkStateCarte(card_id, "activer");
                    //i = "2";
                    etatDelaCarte.setText(getString(R.string.balacerBouton));
                }

                /////////////////////////ECRIRE DANS LES FICHIERS/////////////////////////////////
                /*try{
                    //ecrire donnée renvoyées par le web service lors de la connexion
                    FileOutputStream fOut = getActivity().openFileOutput(file, MODE_PRIVATE);
                    fOut.write(i.getBytes());
                    fOut.close();
                } catch (Exception e){
                    e.printStackTrace();
                }*/


            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
                view.findViewById(R.id.btnDetails).setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));
                view.findViewById(R.id.sWtVerouillerCarte).setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));

                //network not available ic_wifi_red
                conStatusIv.setImageResource(R.drawable.ic_wifi_red);
                titleNetworkLimited.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
                msgNetworkLimited.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
                view.findViewById(R.id.btnReessayer).setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_rounded_red));
            }
        }

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.carte_smopaye, "Recto de ma Carte"));
        slideModels.add(new SlideModel(R.drawable.pub_back_card, "Verso de ma carte"));
        slideModels.add(new SlideModel(R.drawable.pub_qrcode, "Mon QR CODE"));
        slideModels.add(new SlideModel(R.drawable.pub_distributor, "Ma solution de paiement"));
        slideModels.add(new SlideModel(R.drawable.pub_kiosk, "Distributeur Smopaye"));
        slideModels.add(new SlideModel(R.drawable.pub_parasol, "Parasole Smopaye"));
        imageSlider.setImageList(slideModels, true);
        imageSlider.startSliding(5000);

        return view;
    }





    private void detailsCard(String id) {
        cardCall = service.mycards(Integer.parseInt(id));
        cardCall.enqueue(new Callback<Card>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse " +response);

                if(response.isSuccessful()){

                    assert response.body() != null;

                    if(response.body().isSuccess()){
                        DataUserCard card = response.body().getData();

                        unity = card.getUnity();
                        deposit = card.getDeposit();


                        AccountNumber.setText(myPersonalAccountNumber);
                        serial_number = card.getSerial_number();
                        typeCard = card.getType();
                        cardState = card.getCard_state();
                        serial_number = card.getSerial_number();

                        card_id = card.getCode_number();
                        AccountCard.setText(card_id);
                        tel.setText("+237 " + telephone);



                        if(abonnement.isEmpty()){
                            tAbonnement.setText(getString(R.string.services));
                        } else {
                            tAbonnement.setText(abonnement);
                        }
                        etatCart = card.getCard_state();

                        if (etatCart.equalsIgnoreCase("activer")){
                            sWtVerouillerCarte.setChecked(false);
                            //verouillerCarte.setEnabled(true);
                        }
                        else {
                            sWtVerouillerCarte.setChecked(true);
                            //verouillerCarte.setEnabled(false);
                            etatDelaCarte.setText(getString(R.string.votreCarteVerouille));
                        }



                        //TRAITEMENT DE LA DATE D'EXPIRATION
                        String[] parts = card.getCreated_at().split("-");
                        String annee = parts[0]; // 2019
                        String mois = parts[1]; // Septembre
                        String jour[] = parts[2].split("T"); // 09

                        String times = jour[0] + "/" + mois + "/" + annee;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date d = simpleDate.parse(times);
                            String currentDate1 = DateFormat.getDateInstance(DateFormat.FULL).format(d.getTime());
                            tVdateExpirationCarte.setText(currentDate1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //FIN
                        //recupere l'heure et la date courrante
                        String current = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss", Locale.getDefault()).format(new Date());
                        updateDate.setText("Mise à jour le " + current);


                    } else{
                        Toast.makeText(getContext(), getString(R.string.erreurSurvenue1), Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(getContext(), Login.class));
                    getActivity().finish();
                }
                progressDialog.dismiss();
                progressBar2.setVisibility(View.GONE);
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                progressDialog.dismiss();
                dialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                //Vérification si la connexion internet accessible
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                //Vérification si le serveur est inaccessible
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


    private void checkStateCarte(final String id_card, final String etat){

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
        checkStateCardSmopayeServer(id_card, etat);
    }

    private void checkStateCardSmopayeServer(String id_card, String etat) {

        call = service.etat_carte(myId_card, etat);
        call.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: " + response);
                progressDialog.dismiss();

                if(response.isSuccessful()){
                    assert response.body() != null;

                    if(response.body().isSuccess()){
                        //successResponse(id_card, response.body().getMessage());
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else{
                        errorResponse(id_card, response.body().getMessage());
                    }
                } else{
                    if(response.code() == 401){
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        errorResponse(id_card, apiError.getMessage());
                    }
                    errorResponse(id_card, response.message());
                }
            }

            @Override
            public void onFailure(Call<AllMyResponse> call, Throwable t) {

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
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.monCompte), response, "success");
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
        LocalNotification(getString(R.string.monCompte), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getActivity().getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.monCompte), response, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


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
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.monCompte), response, "error");
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
        LocalNotification(getString(R.string.monCompte), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getActivity().getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.monCompte), response, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }


    @SuppressLint("SetTextI18n")
    @OnClick(R.id.btnDetails)
    void detail_Card(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        (LinearLayout)bottomSheetDialog.findViewById(R.id.bottomSheetContainer)
                );
        TextView updateDateDialog = bottomSheetView.findViewById(R.id.updateDateDialog);
        TextView soldeCompte = bottomSheetView.findViewById(R.id.soldeCompte);
        TextView soldeUnite = bottomSheetView.findViewById(R.id.soldeUnite);
        TextView soldeDepot = bottomSheetView.findViewById(R.id.soldeDepot);

        TextView account_number = bottomSheetView.findViewById(R.id.account_number);
        TextView state_account = bottomSheetView.findViewById(R.id.state_account);
        TextView card_number = bottomSheetView.findViewById(R.id.card_number);
        TextView serial_numbers = bottomSheetView.findViewById(R.id.serial_number);
        TextView card_state = bottomSheetView.findViewById(R.id.card_state);
        TextView card_type = bottomSheetView.findViewById(R.id.card_type);

        String current = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss", Locale.getDefault()).format(new Date());
        updateDateDialog.setText(getString(R.string.updateTo) + " " + current);

        soldeCompte.setText(getString(R.string.compte) +": " + df.format(Float.parseFloat(myPersonalAccountAmount)) + " Fcfa");
        soldeUnite.setText(getString(R.string.unite) +": " + df.format(Float.parseFloat(unity)) + " Fcfa");
        soldeDepot.setText(getString(R.string.depot) +": " + df.format(Float.parseFloat(deposit)) + " Fcfa");

        //compte
        account_number.setText(myPersonalAccountNumber);
        state_account.setText(myPersonalAccountState);
        TextView subAccount = bottomSheetView.findViewById(R.id.proprietaire);
        subAccount.setText(profil_complet);

        //card
        card_number.setText(card_id);
        serial_numbers.setText(serial_number);
        card_state.setText(cardState);
        card_type.setText(getString(R.string.carteMag) + " " + typeCard);


        bottomSheetView.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.fermeture), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TextView tt = bottomSheetView.findViewById(R.id.showDetail);
            if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
                tt.setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));
                tt.setTextColor(getResources().getColor(R.color.white));
            }
        }

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnackBar(isConnected);

        if(isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            snackbar = Snackbar.make(getView().findViewById(R.id.detailCard), message, Snackbar.LENGTH_LONG);
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
            snackbar = Snackbar.make(getView().findViewById(R.id.detailCard), message, Snackbar.LENGTH_INDEFINITE);
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

        //progressDialog.dismiss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
        if(cardCall != null){
            cardCall.cancel();
            cardCall = null;
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
                                            Toast.makeText(getContext(), getString(R.string.echoue), Toast.LENGTH_SHORT).show();
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
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity().getApplicationContext());

        RemoteViews collapsedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notif_collapsed);
        RemoteViews expandedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notif_expanded);

        Intent clickIntent = new Intent(getActivity().getApplicationContext(), NotifReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                0, clickIntent, 0);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1, titles);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, subtitles);

        expandedView.setImageViewResource(R.id.image_view_expanded, R.mipmap.logo_official);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo_official)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
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
