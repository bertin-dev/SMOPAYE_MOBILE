package com.ezpass.smopaye_mobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ezpass.smopaye_mobile.Config.Global;
import com.ezpass.smopaye_mobile.Profil_user.Entreprise;
import com.ezpass.smopaye_mobile.Provider.PrefManager;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Assistance.HomeAssistanceOnline;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Manage_HomePage.CarteFragment;
import com.ezpass.smopaye_mobile.Manage_HomePage.NotificationsFragment;
import com.ezpass.smopaye_mobile.Manage_HomePage.PointSmopayeFragment;
import com.ezpass.smopaye_mobile.Manage_Register.SubSouscription;
import com.ezpass.smopaye_mobile.Manage_Subscriptions.Home_Subscriptions;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Transfer.HomeTransfer;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.Profil_user.Abonnement;
import com.ezpass.smopaye_mobile.Profil_user.CategoryUser;
import com.ezpass.smopaye_mobile.Profil_user.Compte;
import com.ezpass.smopaye_mobile.Profil_user.DataUser;
import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.Profil_user.Particulier;
import com.ezpass.smopaye_mobile.Profil_user.Role;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.Manage_Settings.Setting;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.Manage_Url_Website.Accueil_OffreSmopaye;
import com.ezpass.smopaye_mobile.Manage_Url_Website.WebSite;
import com.ezpass.smopaye_mobile.Manage_HomePage.AccueilFragment;
import com.ezpass.smopaye_mobile.Manage_Administrator.AccueilFragmentAdmin;
import com.ezpass.smopaye_mobile.Manage_HomePage.AccueilFragmentAgent;
import com.ezpass.smopaye_mobile.Manage_HomePage.AccueilFragmentUser;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import hotchemi.android.rate.AppRate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 *   <b>MainActivity est la classe représentant l'activity d'accueil.</b>
 *   <p>
 *   Un utilisateur y accède après s'être authentifié. cette activity contient plusieurs fragments parmi lesquels nous avons:
 *   <ul>
 *   <li>AccueilFragmentAdmin pour le role administrateur</li>
 *   <li>AccueilFragment pour le role accepteur</li>
 *   <li>AccueilFragmentAgent pour le role Agent</li>
 *   <li>AccueilFragmentUser pour le role utilisateur</li>
 *   </ul>
 *   </p>
 *   <p>
 *   Tous ces rôles sont générés par la web service et donc en fonction du role, je fais appel au fragment correspondant
 *   </p>
 *
 *
 * Classe MainActivity qui hérite AppCompatActivity et implemente NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener
 *
 * @see MainActivity
 * @see AppCompatActivity
 *
 * @author Bertin-dev
 * @since 2019
 * @version 1.4.0
 * @powered by smopaye sarl
 * {@link https://smopaye.cm/}
 * {@link https://play.google.com/store/apps/details?id=com.ezpass.smopaye_mobile&showAllReviews=true}
 * @Copyright 2019-2021
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {


    /**
     * Déclaration de toutes les variables et objects utilisés pour la mise en place de la page d'accueil
     */
    private static final String TAG = "MainActivity";
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
    Constant constant;
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    private ProgressDialog progressDialog;
    private BottomBar bottomBar;
    private DbHandler db;
    private BottomBarTab nearby;
    private Dialog myDialog;
    private ACProgressFlower dialog;
    private TextView txt_role, txt_profile, txtclose, txt_telephone;
    private String profil_complet = "";
    private String session = ""; // Accepteur, Administrateur, Utilisateur, Agent
    private String etat = ""; // Actif, Inactif, Offline
    private String myPhone = ""; //694048925
    private String myAbon = ""; //hebdomadaire, service, mensuel
    private String myCompte = ""; // BDAADA51
    private String myId_card = ""; //1;
    private String myCategorie = "";
    private String myPersonalAccountNumber = "";
    //DatabaseReference reference;
    private String myPersonalAccountState = "";
    private String myPersonalAccountAmount = "";
    private String myPersonalAccountId = "";
    /*Déclaration des objets qui permettent d'écrire sur le fichier*/
    private String tmp_card_number = "tmp_card_number";
    private String telephone1 = "";
    private String tmp_card_id = "tmp_card_id";
    private String tmp_account = "tmp_account";
    //web service
    private ApiService service;
    private TokenManager tokenManager;
    private Call<DataUser> call;
    //service google firebase
    private FirebaseUser fuser;
    //update account
    private String nom;
    private String prenom;
    private String numeroTel;
    private String cni;
    private String adresse;
    private String numero_card;
    private String idUser;
    private int bonus;
    private int points;
    private PrefManager prf;
    private Toolbar toolbar;

    /**
     * Méthode callback de java android (onPostCreate) qui permet d'éxécuter une fonction ACProgressFlower qui est chargé de faire appel à un loading DIRECT_CLOCKWISE
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //********************DEBUT***********
        /*runOnUiThread(new Runnable() {
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

        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        dialog.show();

    }

    /**
     * Methode onCreate appelé en premier lors du chargement de l'activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_main);

        /**
         * toolbar permet d'insérer la toolbar sur la page d'accueil
         */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(Constant.color);
        prf = new PrefManager(getApplicationContext());
        /**
         * FloatingActionButton Méthode inutilisé pour cette activity
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        txt_role = (TextView) header.findViewById(R.id.txt_role);
        txt_profile = (TextView) header.findViewById(R.id.txt_profile);
        txt_telephone = (TextView) header.findViewById(R.id.txt_number);

        if (Constant.color == getResources().getColor(R.color.colorPrimaryRed))
            header.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryRed));
        else
            header.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));


        /**
         *  Instanciation de la base de données locale
         */
        db = new DbHandler(getApplicationContext());
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        /**
         * Initialisation de la Bottom bar et les icons de notifications
         */
        nearby = bottomBar.getTabWithId(R.id.nav_notifications);
        nearby.setBadgeCount(Integer.parseInt(db.GetNumNotifications()));

        progressDialog = new ProgressDialog(this);


        /**
         * vérifie si l'activity précédente possède un numéro de téléphone si oui alors le recupère et le stock dans la variable telephone1
         *
         * @see Intent
         */
        Intent intent = getIntent();
        telephone1 = intent.getStringExtra("telephone");


        /**
         * Integration de la rest API
         * initialisation des service retrofit 2 et des ressources avec le Binding du code java et xml via la methode ButterKnife
         * vérification et recupération de l'access token dans les sharedpreferences si cela existe si non alors redirection dans la page login de l'utilisateur
         */
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        //verification si on a pas le token dans les sharepreferences alors on retourne vers le login activity
        if (tokenManager.getToken() == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        //google firebase
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        //reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());


        //web service
        call = service.profil(telephone1);
        call.enqueue(new Callback<DataUser>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<DataUser> call, Response<DataUser> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse " + response);


                if (response.isSuccessful()) {

                    assert response.body() != null;
                    Fragment selectedFragment1 = null;
                    Bundle bundle1 = new Bundle();

                    //initialisation des objets et listes
                    CategoryUser categorie = response.body().getCategorie();
                    List<Particulier> particulier = response.body().getParticulier();
                    List<Entreprise> entreprise = response.body().getEnterprise();
                    Role role = response.body().getRole();
                    Compte compte = response.body().getCompte();
                    List<Abonnement> abonnement = compte.getCompte_subscriptions();
                    List<DataUserCard> dataUserCards = response.body().getCards();

                    //utilisateur courant
                    idUser = response.body().getId();
                    etat = response.body().getState();
                    myPhone = response.body().getPhone();
                    adresse = response.body().getAddress();

                    //categorie courante de l'utilisateur
                    myCategorie = categorie.getName();


                    //utilisateur ou entreprise courante
                    if (particulier.isEmpty()) {
                        profil_complet = (entreprise.get(0).getRaison_social()).toUpperCase();
                        nom = entreprise.get(0).getRaison_social();
                        prenom = "";
                        cni = "";
                    } else {
                        profil_complet = (particulier.get(0).getFirstname() + " " + particulier.get(0).getLastname()).toUpperCase();
                        nom = particulier.get(0).getLastname();
                        prenom = particulier.get(0).getFirstname();
                        cni = particulier.get(0).getCni();
                    }

                    //Role de l'utilisateur courant
                    session = role.getName();

                    //Compte de l'utilisateur courant
                    myPersonalAccountNumber = compte.getAccount_number();
                    myPersonalAccountState = compte.getAccount_state();
                    myPersonalAccountAmount = compte.getAmount();
                    myPersonalAccountId = compte.getId();


                    //Listes des Cartes de l'utilisateur courant
                    if (!dataUserCards.isEmpty()) {
                        myCompte = dataUserCards.get(0).getCode_number();
                        myId_card = dataUserCards.get(0).getId();
                    }


                    //Liste des abonnements de l'utilisateur courant
                    for (int i = 0; i < abonnement.size(); i++) {
                        myAbon = abonnement.get(abonnement.size() - 1).getSubscription_type();
                        //myAbon = abonnement.get(i).getSubscription_type();
                    }


                    //calcul des bonnus et points cumulés par l'utilisateur courant
                    if (response.body().getBonus() != null)
                        points = Integer.parseInt(response.body().getBonus());
                    if (response.body().getBonus_valider() != null && response.body().getBonus_non_valider() != null)
                        bonus = Integer.parseInt(response.body().getBonus_valider()) + Integer.parseInt(response.body().getBonus_non_valider());

                    //update account
                    numeroTel = myPhone;
                    numero_card = myCompte;

                    writeTempCardNumberInFile(myCompte);
                    writeTempCardIDInFile(myId_card);
                    writeTempAccountInFile(myPersonalAccountNumber);

                    //store data
                    if((prf.getString("key_myPhoneUser")).equalsIgnoreCase("")){
                        //Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        prf.setString("key_idUser", idUser);
                        prf.setString("key_etatUser", etat);
                        prf.setString("key_myPhoneUser", myPhone);
                        prf.setString("key_adresseUser", adresse);
                        prf.setString("key_myCategorieUser", myCategorie);
                        prf.setString("key_profil_completUser", profil_complet);
                        prf.setString("key_cniUser", cni);
                        prf.setString("key_RoleUser", session);
                        prf.setString("key_myPersonalAccountNumberUser", myPersonalAccountNumber);
                        prf.setString("key_myPersonalAccountStateUser", myPersonalAccountState);
                        prf.setString("key_myPersonalAccountAmountUser", myPersonalAccountAmount);
                        prf.setString("key_myPersonalAccountIdUser", myPersonalAccountId);
                        prf.setString("key_myCompteUser", myCompte);
                        prf.setString("key_myId_cardUser", myId_card);
                        prf.setString("key_myAbonUser", myAbon);
                        prf.setInt("key_points", points);
                        prf.setInt("key_bonus", bonus);
                    }


                    /************************************************DEBUT**************/

                    txt_role.setText(session); // Accepteur, Administrateur, Utilisateur, Agent
                    txt_profile.setText(profil_complet); //Bertin Mounok
                    if (particulier.isEmpty()) {
                        txt_telephone.setText(role.getType().toUpperCase() + " Tel: +237 " + myPhone); //+237 694048925
                    } else {
                        txt_telephone.setText("+237 " + myPhone); //+237 694048925
                    }

                    if (session.toLowerCase().equalsIgnoreCase("administrateur")) {
                        /*--------------------------------AJOUT DES ELEMENTS DANS LE HEADER DU  nav_header_main.xml---------------*/

                        if (etat.toLowerCase().equalsIgnoreCase("activer")) {
                            bundle1.putString("telephone", myPhone);
                            bundle1.putString("compte", myCompte);
                            bundle1.putString("role", session);
                            bundle1.putString("categorie", myCategorie);
                            bundle1.putString("idUser", idUser);
                            bundle1.putInt("points", points);
                            bundle1.putInt("bonus", bonus);
                            selectedFragment1 = new AccueilFragmentAdmin();
                            selectedFragment1.setArguments(bundle1);
                        } else {
                            bundle1.putString("telephone", myPhone);
                            bundle1.putString("compte", myCompte);
                            bundle1.putString("role", session);
                            bundle1.putString("categorie", myCategorie);
                            bundle1.putString("etat", etat);
                            bundle1.putString("idUser", idUser);
                            bundle1.putInt("points", points);
                            bundle1.putInt("bonus", bonus);
                            selectedFragment1 = new AccueilFragmentUser();
                            selectedFragment1.setArguments(bundle1);
                            txt_role.setText(getString(R.string.utilisateur));
                        }
                    } else if (session.toLowerCase().equalsIgnoreCase("utilisateur")) {
                        bundle1.putString("telephone", myPhone);
                        bundle1.putString("compte", myCompte);
                        bundle1.putString("role", session);
                        bundle1.putString("categorie", myCategorie);
                        bundle1.putString("etat", etat);
                        bundle1.putString("idUser", idUser);
                        bundle1.putInt("points", points);
                        bundle1.putInt("bonus", bonus);
                        selectedFragment1 = new AccueilFragmentUser();
                        selectedFragment1.setArguments(bundle1);
                    } else if (session.toLowerCase().equalsIgnoreCase("agent")) {
                        if (etat.toLowerCase().equalsIgnoreCase("activer")) {
                            bundle1.putString("telephone", myPhone);
                            bundle1.putString("compte", myCompte);
                            bundle1.putString("role", session);
                            bundle1.putString("categorie", myCategorie);
                            bundle1.putString("idUser", idUser);
                            bundle1.putInt("points", points);
                            bundle1.putInt("bonus", bonus);
                            selectedFragment1 = new AccueilFragmentAgent();
                            selectedFragment1.setArguments(bundle1);
                        } else {
                            bundle1.putString("telephone", myPhone);
                            bundle1.putString("compte", myCompte);
                            bundle1.putString("role", session);
                            bundle1.putString("categorie", myCategorie);
                            bundle1.putString("etat", etat);
                            bundle1.putString("idUser", idUser);
                            bundle1.putInt("points", points);
                            bundle1.putInt("bonus", bonus);
                            selectedFragment1 = new AccueilFragmentUser();
                            selectedFragment1.setArguments(bundle1);
                            txt_role.setText(getString(R.string.utilisateur));
                        }
                    } else if (session.toLowerCase().equalsIgnoreCase("accepteur"))  //il s 'agit d'un accepteur
                    {
                        if (etat.toLowerCase().equalsIgnoreCase("activer")) {
                            bundle1.putString("telephone", myPhone);
                            bundle1.putString("compte", myCompte);
                            bundle1.putString("role", session);
                            bundle1.putString("categorie", myCategorie);
                            bundle1.putString("idUser", idUser);
                            bundle1.putInt("points", points);
                            bundle1.putInt("bonus", bonus);
                            selectedFragment1 = new AccueilFragment();
                            selectedFragment1.setArguments(bundle1);
                        } else {
                            bundle1.putString("telephone", myPhone);
                            bundle1.putString("compte", myCompte);
                            bundle1.putString("role", session);
                            bundle1.putString("categorie", myCategorie);
                            bundle1.putString("etat", etat);
                            bundle1.putString("idUser", idUser);
                            bundle1.putInt("points", points);
                            bundle1.putInt("bonus", bonus);
                            selectedFragment1 = new AccueilFragmentUser();
                            selectedFragment1.setArguments(bundle1);
                            txt_role.setText(getString(R.string.utilisateur));
                        }
                    }
                    /*else{
                        selectedFragment1 = new ServiceIndispinibleFragment();
                        selectedFragment1.setArguments(bundle1);
                    }*/


                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment1).commit();
                    getSupportActionBar().setTitle(getString(R.string.accueil));
                    toolbar.setSubtitle(getString(R.string.ezpass));

                    /************************************************FIN**********************/


                    /**************************************************BOTTOM BAR**************************/

                    bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                        @Override
                        public void onTabSelected(int tabId) {

                            Fragment selectedFragment = null;
                            Bundle bundle = new Bundle();

                            switch (tabId) {
                                case R.id.nav_Accueil:
                                    getSupportActionBar().setTitle(getString(R.string.accueil));
                                    toolbar.setSubtitle(getString(R.string.ezpass));
                                    //selectedFragment = new AccueilFragment();
                                    if (session.toLowerCase().equalsIgnoreCase("administrateur")) {
                                        if (etat.toLowerCase().equalsIgnoreCase("activer")) {
                                            bundle.putString("telephone", myPhone);
                                            bundle.putString("compte", myCompte);
                                            bundle.putString("role", session);
                                            bundle.putString("categorie", myCategorie);
                                            bundle.putString("idUser", idUser);
                                            bundle.putInt("points", points);
                                            bundle.putInt("bonus", bonus);
                                            selectedFragment = new AccueilFragmentAdmin();
                                            selectedFragment.setArguments(bundle);
                                        } else {
                                            bundle.putString("telephone", myPhone);
                                            bundle.putString("compte", myCompte);
                                            bundle.putString("role", session);
                                            bundle.putString("categorie", myCategorie);
                                            bundle.putString("etat", etat);
                                            bundle.putString("idUser", idUser);
                                            bundle.putInt("points", points);
                                            bundle.putInt("bonus", bonus);
                                            selectedFragment = new AccueilFragmentUser();
                                            selectedFragment.setArguments(bundle);
                                        }
                                    } else if (session.toLowerCase().equalsIgnoreCase("utilisateur")) {

                                        bundle.putString("telephone", myPhone);
                                        bundle.putString("compte", myCompte);
                                        bundle.putString("role", session);
                                        bundle.putString("categorie", myCategorie); //mini-bus cargo, petit-commerce
                                        bundle.putString("etat", etat);
                                        bundle.putString("idUser", idUser);
                                        bundle.putInt("points", points);
                                        bundle.putInt("bonus", bonus);
                                        selectedFragment = new AccueilFragmentUser();
                                        selectedFragment.setArguments(bundle);
                                    } else if (session.toLowerCase().equalsIgnoreCase("agent")) {
                                        bundle.putString("telephone", myPhone);
                                        bundle.putString("compte", myCompte);
                                        bundle.putString("role", session);
                                        bundle.putString("categorie", myCategorie);
                                        bundle.putString("idUser", idUser);
                                        bundle.putInt("points", points);
                                        bundle.putInt("bonus", bonus);
                                        selectedFragment = new AccueilFragmentAgent();
                                        selectedFragment.setArguments(bundle);
                                    } else //accepteur
                                    {
                                        if (etat.toLowerCase().equalsIgnoreCase("activer")) {
                                            bundle.putString("telephone", myPhone);
                                            bundle.putString("compte", myCompte);
                                            bundle.putString("role", session);
                                            bundle.putString("categorie", myCategorie);
                                            bundle.putString("idUser", idUser);
                                            bundle.putInt("points", points);
                                            bundle.putInt("bonus", bonus);
                                            selectedFragment = new AccueilFragment();
                                            selectedFragment.setArguments(bundle);
                                        } else {
                                            bundle.putString("telephone", myPhone);
                                            bundle.putString("compte", myCompte);
                                            bundle.putString("role", session);
                                            bundle.putString("categorie", myCategorie);
                                            bundle.putString("etat", etat);
                                            bundle.putString("idUser", idUser);
                                            bundle.putInt("points", points);
                                            bundle.putInt("bonus", bonus);
                                            selectedFragment = new AccueilFragmentUser();
                                            selectedFragment.setArguments(bundle);
                                        }
                                    }
                                    break;

                                case R.id.nav_maCarte:
                                    getSupportActionBar().setTitle(getString(R.string.maCarteSmopaye));
                                    toolbar.setSubtitle(getString(R.string.ezpass));
                                    bundle.putString("abonnement", myAbon);
                                    bundle.putString("myId_card", myId_card);
                                    bundle.putString("telephone", myPhone);
                                    bundle.putString("categorie", myCategorie);
                                    bundle.putString("myPersonalAccountNumber", myPersonalAccountNumber);
                                    bundle.putString("myPersonalAccountState", myPersonalAccountState);
                                    bundle.putString("myPersonalAccountAmount", myPersonalAccountAmount);
                                    bundle.putString("profil_complet", profil_complet);
                                    selectedFragment = new CarteFragment();
                                    selectedFragment.setArguments(bundle);
                                    break;
                                case R.id.nav_pointSmopaye:
                                    getSupportActionBar().setTitle(getString(R.string.pointDeVenteSmopaye));
                                    toolbar.setSubtitle(getString(R.string.ezpass));
                                    selectedFragment = new PointSmopayeFragment();
                                    break;
                                case R.id.nav_notifications:
                                    getSupportActionBar().setTitle(getString(R.string.notifications));
                                    toolbar.setSubtitle(getString(R.string.ezpass));
                                    nearby.setBadgeCount(Integer.parseInt(db.GetNumNotifications()));

                                    DbHandler db1 = new DbHandler(getApplicationContext());
                                    db1.UpdateNumNotification("1");

                                    selectedFragment = new NotificationsFragment();
                                    break;
                            }
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                            //return true;
                        }
                    });


                    updateToken(FirebaseInstanceId.getInstance().getToken());

                } else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
                progressDialog.dismiss();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<DataUser> call, Throwable t) {

                progressDialog.dismiss();
                dialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());


                //Vérification si la connexion internet accessible
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if (!(activeInfo != null && activeInfo.isConnected())) {
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                //Vérification si le serveur est inaccessible
                else {
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(MainActivity.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }

        if (prf.getBoolean("tutorial")) {
            prf.setBoolean("tutorial",false);
            tutorial();
        }
    }

    /**
     * Méthode privé changeColorWidget qui permet de changer la couleur des widget que nous avons défini
     *
     * elle ne prend aucun paramètre. elle est accessible à partir de la version LOLLIPOP d'Android
     *
     * @see changeColorWidget
     *
     * @return ne rerourne rien du tout
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeColorWidget() {
        if (Constant.color == getResources().getColor(R.color.colorPrimaryRed)) {
            //network not available ic_wifi_red
            conStatusIv.setImageResource(R.drawable.ic_wifi_red);
            titleNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            msgNetworkLimited.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryRed));
            findViewById(R.id.btnReessayer).setBackground(ContextCompat.getDrawable(this, R.drawable.btn_rounded_red));

            //setTheme(R.style.colorPrimaryDark);
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    /**
     * Méthode privé changeTheme qui permet de changer la couleur des widget que nous avons défini
     *
     * elle ne prend aucun paramètre.
     *
     * @see changeTheme()
     *
     * @return null
     */
    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0) {
            setTheme(Constant.theme);
        } else if (appTheme == 0) {
            setTheme(Constant.theme);
        } else {
            setTheme(appTheme);
        }
    }


    /**
     * Methode callback onPostResume() il permet d'afficher le nombre de notification contenu dans la base de données locale de l'utilisateur
     *
     * elle ne prend aucun paramètre
     *
     * @see onPostResume
     *
     * @exception e
     */
    @Override
    protected void onPostResume() {
        nearby.setBadgeCount(Integer.parseInt(db.GetNumNotifications()));
        //status("online");
        try {
            Badges.setBadge(getApplicationContext(), Integer.parseInt(db.GetNumNotifications()));
        } catch (BadgesNotSupportedException e) {
            e.printStackTrace();
            Log.i("Notification Badge", e.getMessage());
        }
        super.onPostResume();
    }

    /**
     * Methode callback onBackPressed() permet d'ouvrir ou fermé le DrawerLayout et deconnecter l'utilisateur au cas ou
     *
     * elle ne prend aucun paramètre
     *
     * @see onBackPressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Deconnexion(false);
        }
        progressDialog.dismiss();

    }

    /**
     * Affiche List des éléments contenu dans le menu
     * @see onCreateOptionsMenu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * permet d'ouvrir une activity en fonction des éléments cliqué dans le menu
     * @see onOptionsItemSelected
     * @param item
     * @return super.onOptionsItemSelected(item)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.apropos) {
            Intent intent = new Intent(getApplicationContext(), Apropos.class);
            intent.putExtra("role", session);
            intent.putExtra("telephone", myPhone);
            startActivity(intent);
            Animatoo.animateZoom(this);  //fire the zoom animation
        }

        if (id == R.id.tuto) {
            prf.setBoolean("tutorial",true);
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            intent.putExtra("role", session);
            intent.putExtra("telephone", myPhone);
            startActivity(intent);
            Animatoo.animateZoom(this);  //fire the zoom animation
        }

        if (id == R.id.modifierCompte) {
            //Toast.makeText(this, telephone, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), UpdatePassword.class);
            intent.putExtra("telephone", myPhone);
            startActivity(intent);
            Animatoo.animateZoom(this);  //fire the zoom animation
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * permet de lister et ouvrir chaque élément contenu dans le DrawerLayout de gauche
     * @see onNavigationItemSelected
     * @param item
     * @return true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addSubAccount) {
            Intent intent = new Intent(getApplicationContext(), SubSouscription.class);
            startActivity(intent);
            Animatoo.animateFade(this);  //fire the zoom animation
        }

        if (id == R.id.nav_offreSmopaye) {
            // Handle the camera action
            startActivity(new Intent(getApplicationContext(), Accueil_OffreSmopaye.class));
            Animatoo.animateFade(this);  //fire the zoom animation
        } else if (id == R.id.nav_siteWeb) {
            // Toast.makeText(this, "Site Web", Toast.LENGTH_SHORT).show();
            //Methode permettant l'ouverture d'une page web via un navigateur
            /*String url="http://www.google.fr";
            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);*/

            Intent intent = new Intent(getApplicationContext(), WebSite.class);
            startActivity(intent);
            Animatoo.animateFade(this);  //fire the zoom animation

            //startActivity(new Intent(getApplicationContext(), WebSite.class));

        } else if (id == R.id.nav_clients) {

            Uri uri = Uri.parse(Global.espace_clients);
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Global.espace_clients)));
            }

            /*Intent intent = new Intent(getApplicationContext(), EspaceClients.class);
            startActivity(intent);
            Animatoo.animateFade(this);*/

            /*try {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse(ChaineConnexion.getEspace_clients()));
                startActivity(viewIntent);
            }catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to Connect Try Again...",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }*/


        } else if (id == R.id.nav_transfert) {
            Intent intent = new Intent(getApplicationContext(), HomeTransfer.class);
            intent.putExtra("myId_card", myId_card);
            intent.putExtra("telephone", myPhone);
            intent.putExtra("compte", myCompte);
            intent.putExtra("myPersonalAccountNumber", myPersonalAccountNumber);
            startActivity(intent);
            Animatoo.animateFade(this);  //fire the zoom animation
        } else if (id == R.id.nav_abonnement) {
            Intent intent = new Intent(getApplicationContext(), Home_Subscriptions.class);
            intent.putExtra("myId_card", myId_card);
            intent.putExtra("myAbon", myAbon);
            intent.putExtra("myPhone", myPhone);
            intent.putExtra("myPersonalAccountId", myPersonalAccountId);
            startActivity(intent);
            Animatoo.animateFade(this);  //fire the zoom animation
        }

        /*else if (id == R.id.nav_Payer_Facture){
            Intent intent = new Intent(getApplicationContext(), PayerFacture.class);
            intent.putExtra("telephone", telephone);
            startActivity(intent);
        }*/

        else if (id == R.id.nav_Assistance_EnLigne) {
            Intent intent = new Intent(getApplicationContext(), HomeAssistanceOnline.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("telephone", myPhone);
            intent.putExtra("role", session);
            startActivity(intent);
            Animatoo.animateFade(this);  //fire the zoom animation
        } else if (id == R.id.nav_share) {
            //partager votre lien avec unique ID
            myDialog = new Dialog(this);
            myDialog.setContentView(R.layout.layout_dialog_invite_friends);
            txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
            txtclose.setText("X");
            txtclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

        } else if (id == R.id.nav_notation) {
            //Toast.makeText(this, "noter nous sur playstore", Toast.LENGTH_SHORT).show();
           /* try {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://play.google.com/store/apps/details?id=com.ezpass.smopaye_mobile&hl=fr"));
                startActivity(viewIntent);
            }catch(Exception e) {
                Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }*/


            AppRate.with(this)
                    .setInstallDays(1)
                    .setLaunchTimes(3)
                    .setRemindInterval(2)
                    .monitor();
            AppRate.showRateDialogIfMeetsConditions(this);
            //AppRate.with(this).clearAgreeShowDialog();
            AppRate.with(this).showRateDialog(this);

            /*Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }*/
        } else if (id == R.id.nav_deconnexion) {
            Deconnexion(true);
        } else if (id == R.id.nav_QuestionFrequentes) {
            Toast.makeText(this, getString(R.string.questionFrequentes), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_parametres) {
            Intent intent = new Intent(getApplicationContext(), Setting.class);
            intent.putExtra("nom", nom);
            intent.putExtra("prenom", prenom);
            intent.putExtra("numeroTel", numeroTel);
            intent.putExtra("cni", cni);
            intent.putExtra("adresse", adresse);
            intent.putExtra("numero_card", numero_card);
            intent.putExtra("idUser", idUser);
            intent.putExtra("role", session);
            intent.putExtra("categorie", myCategorie);
            startActivity(intent);
            Animatoo.animateFade(this);  //fire the zoom animation
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * fonction permettant d'ouvrir une boite de dialogue et demande à l'utilisateur s'il souhaite sortir de l'application.
     * si oui alors il est deconnecté de tous les services ensuite l'application se ferme
     * @see Deconnexion
     *
     * @param exitGoogleFirebase
     */
    private void Deconnexion(boolean exitGoogleFirebase) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.confirmExit));
        alertDialogBuilder.setIcon(R.drawable.ic_exit_to_app_black_24dp);
        alertDialogBuilder.setMessage(getString(R.string.confirmExitMsg));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
                if (exitGoogleFirebase) {
                    dialog.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    finishAffinity();
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.non), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

   /* private void Exit() {
        ProgressDialog dialog2 = ProgressDialog.show(MainActivity.this, getString(R.string.deconnexion), getString(R.string.encours), true);
        dialog2.show();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                FirebaseAuth.getInstance().signOut();
                dialog2.dismiss();
                finish();
            }
        }, 2000); // 2000 milliseconds delay
    }*/

    /*private void status(String status){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }



    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }*/


    /**
     * methode permettant de recupérer une instance de l'utilisateur connecté et met à jour la base de données realtime database de firebase
     * @param token
     * @see updateToken
     * @return cette fonction ne renvoi rien
     */
    private void updateToken(String token) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference1.child(fuser.getUid()).setValue(token1);
    }


    /**
     * methode callback qui permet à la base de detruire une activity et vide egalement la variable call
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (call != null) {
            call.cancel();
            call = null;
        }
    }


    /**
     * writeTempCardNumberInFile() methodes permettant l'écriture du numéro de téléphone dans le fichier tmp_number
     * et insertion de celui-ci dans le tie_telephone
     *
     * @param fileContents
     * @throws e
     * @since 2019
     */
    private void writeTempCardNumberInFile(String fileContents) {
        try {
            //ecrire du numero de telephone
            FileOutputStream fOut = openFileOutput(tmp_card_number, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(), tmp_card_number);
            //Toast.makeText(getBaseContext(), "File Saved at " + fileDir, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * writeTempCardIDInFile() methodes permettant l'écriture de l'ID CARTE dans le fichier tmp_card
     *
     * @param fileContents
     * @throws e
     * @since 2019
     */
    private void writeTempCardIDInFile(String fileContents) {
        try {
            //ecrire de l'ID CARD
            FileOutputStream fOut = openFileOutput(tmp_card_id, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(), tmp_card_id);
            //Toast.makeText(getBaseContext(), "File Saved at " + fileDir, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * writeTempAccountInFile() methodes permettant l'écriture du numéro de compte dans le fichier tmp_account
     *
     * @see writeTempAccountInFile
     * @param fileContents
     * @throws e
     * @since 2019
     */
    private void writeTempAccountInFile(String fileContents) {
        try {
            //ecrire de l'ID CARD
            FileOutputStream fOut = openFileOutput(tmp_account, MODE_PRIVATE);
            fOut.write(fileContents.getBytes());
            fOut.close();
            File fileDir = new File(getFilesDir(), tmp_account);
            //Toast.makeText(getBaseContext(), "File Saved at " + fileDir, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * checkNetworkConnectionStatus() méthode permettant de verifier si la connexion existe ou si le serveur est accessible
     *
     * @see checkNetworkConnectionStatus
     * @return ne retourne rien
     * @since 2019
     */
    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnackBar(isConnected);
        if (isConnected) {
            changeActivity();
        }
    }


    /**
     * permet de recreer une activity  lorque la connexion etait inaccessible la premiere fois
     * @return ne retourne rien du tout
     */
    private void changeActivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            progressDialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
            progressDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                    recreate();
                }
            }, 2000); // 2000 milliseconds delay

        } else {
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * permet d'afficher un snackbar avec un message en fonction de l'etat de la connexion internet
     * @see showSnackBar
     * @param isConnected
     * @return ne retourne rien du tout
     */
    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if (isConnected) {
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(color);
            if (Constant.color == getResources().getColor(R.color.colorPrimaryRed))
                textView.setBackgroundResource(R.color.colorPrimaryRed);
            else
                textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else {
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_INDEFINITE);
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

    /**
     * verifi l'etat de la connexion en temps réel
     * @param isConnected
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        /*if(!isConnected){
            changeActivity();
        }*/
        showSnackBar(isConnected);
    }

    /**
     * Methode appelé apres que l'activity a été affiché de manière complète
     */
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

    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page MainActivity
     *
     * @see attachBaseContext
     * @param newBase
     * @since 2019
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    /**
     * permet d'afficher un tutoriel descriptif des fonctionnalités disponible avec animation
     */
    private void tutorial() {
        toolbar.inflateMenu(R.menu.main);
        //toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp));
        // We load a drawable and create a location to show a tap target here
        // We need the display to get the width and height at this point in time
        final Display display = getWindowManager().getDefaultDisplay();
        // Load our little droid guy
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ezpass);
        final Drawable droid2 = ContextCompat.getDrawable(this, R.drawable.tpe11);
        final Drawable droid3 = ContextCompat.getDrawable(this, R.drawable.ic_baseline_credit_card_48);
        // Tell our droid buddy where we want him to appear
        // final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth(), droid.getIntrinsicHeight());
        final Rect droidTarget2 = new Rect(0, 0, 0, droid2.getIntrinsicHeight());
        final Rect droidTarget3 = new Rect(0, 0, 0, droid2.getIntrinsicHeight());
        // Using deprecated methods makes you look way cool
        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);
        droidTarget2.offset(display.getWidth() / 2, display.getHeight() / 2);
        droidTarget3.offset(display.getWidth() / 2, display.getHeight() / 2);

        final TapTargetSequence seq = new TapTargetSequence(this)
                .targets(

                        TapTarget.forBounds(droidTarget, getString(R.string.ezpassBySmopaye), getString(R.string.sloganEzpass))
                                .cancelable(false)
                                .icon(droid)
                                .tintTarget(false)
                                .id(1),

                        TapTarget.forBounds(droidTarget2, getString(R.string.tpeMobile), getString(R.string.sloganEzpassTpe))
                                .cancelable(false)
                                .icon(droid2)
                                .tintTarget(false)
                                .id(2),

                        TapTarget.forBounds(droidTarget3, getString(R.string.cardEzpass), getString(R.string.sloganEzpassCard))
                                .cancelable(false)
                                .icon(droid3)
                                .tintTarget(false)
                                .id(3),



                        /*TapTarget.forToolbarNavigationIcon(toolbar, "This is the back button", sassyDesc).id(2),
                        TapTarget.forToolbarOverflow(toolbar, "This will show more options", "But they're not useful :(").id(3),
                        TapTarget.forToolbarMenuItem(toolbar, R.id.search, "This is a search icon", "As you can see, it has gotten pretty dark around here...")
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.secondary_color)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .textColor(android.R.color.black)
                                .id(4),*/
                        TapTarget.forView(findViewById(R.id.nav_Accueil), getString(R.string.accueil), getString(R.string.homeDescription))
                                .dimColor(android.R.color.darker_gray)
                                .outerCircleColor(R.color.bgColorStandard)
                                .targetCircleColor(R.color.white)
                                .cancelable(false)
                                .drawShadow(true)
                                .titleTextDimen(R.dimen.title_text_size)
                                .textColor(android.R.color.white)
                                .id(4),
                        TapTarget.forView(findViewById(R.id.nav_maCarte), getString(R.string.maCarteBottomNav), getString(R.string.maCarteDesc)).id(6),
                        TapTarget.forView(findViewById(R.id.nav_pointSmopaye), getString(R.string.pointSmopayeBottomNav), getString(R.string.pointSmopayeDes)).id(7),
                        TapTarget.forView(findViewById(R.id.nav_notifications), getString(R.string.notificationBottomNav), getString(R.string.notificationsBottom)).id(5)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });





        // You don't always need a sequence, and for that there's a single time tap target
        final SpannableString spannedDesc = new SpannableString(getString(R.string.chez) + " " + getString(R.string.ezpassBySmopaye));
        spannedDesc.setSpan(new UnderlineSpan(), spannedDesc.length() - getString(R.string.ezpassBySmopaye).length(), spannedDesc.length(), 0);
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.fab), getString(R.string.welcome), spannedDesc)
                .cancelable(false)
                .drawShadow(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                // .. which evidently starts the sequence we defined earlier
                seq.start();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                Toast.makeText(view.getContext(), getString(R.string.cliked), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                Log.d("TapTargetViewSample", "You dismissed me :(");
            }
        });

    }
}
