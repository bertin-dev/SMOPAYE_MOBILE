package com.ezpass.smopaye_mobile.Manage_HomePage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Factures.PayerFacture;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode.MenuQRCode;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.MenuRetraitOperateur;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge.HomeRecharge;
import com.ezpass.smopaye_mobile.Manage_Transactions_History.MenuHistoriqueTransaction;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.Manage_Consult.ConsulterSolde;
import com.ezpass.smopaye_mobile.Manage_Consult.VerifierNumCarte;
import com.ezpass.smopaye_mobile.Manage_Register.Souscription;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccueilFragmentAgent extends Fragment {

    private TextView jour, jourSemaine, moisAnnee, categorie, session;
    private LinearLayout RechargeAvecCashAgent, ConsultSoldeAgent, CheckCardNumberAgent, btnPayerFacture, btnQrCode, btnRetraitOperateur;
    private FloatingActionButton InscriptionUserByAgent;
    private Button consulterHistoriqueAgent;
    private String statut1, telephone, code_number_sender, mycategorie, idUser;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    private TextView points;
    private TextView bonus;
    private int nbreBonus;
    private int nbrePoints;
    private ImageView close;
    private RelativeLayout reportDashboad;

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        changeTheme();
        View view =  inflater.inflate(R.layout.fragment_accueil_agent, container, false);

        getActivity().setTitle(getString(R.string.accueil));

        InscriptionUserByAgent = view.findViewById(R.id.btnInscriptionUserByAgent);
        RechargeAvecCashAgent = (LinearLayout) view.findViewById(R.id.btnRechargeAvecCashAgent);
        ConsultSoldeAgent = (LinearLayout) view.findViewById(R.id.btnConsultSoldeAgent);
        CheckCardNumberAgent = (LinearLayout) view.findViewById(R.id.btnCheckCardNumberAgent);
        categorie = (TextView) view.findViewById(R.id.categorieAgent);
        session = (TextView) view.findViewById(R.id.sessionAgent);
        points = (TextView) view.findViewById(R.id.points);
        bonus   = (TextView) view.findViewById(R.id.bonus);
        close = (ImageView) view.findViewById(R.id.close);
        reportDashboad = (RelativeLayout) view.findViewById(R.id.reportDashboad);


        jour = (TextView) view.findViewById(R.id.jour);
        jourSemaine = (TextView) view.findViewById(R.id.jourSemaine);
        moisAnnee   = (TextView) view.findViewById(R.id.moisAnnee);




        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        assert getArguments() != null;
        telephone = getArguments().getString("telephone");
        statut1 = getArguments().getString("role");
        code_number_sender = getArguments().getString("compte");
        mycategorie = getArguments().getString("categorie");
        idUser = getArguments().getString("idUser");
        nbrePoints = getArguments().getInt("points", 0);
        nbreBonus = getArguments().getInt("bonus", 0);

        categorie.setText(mycategorie); // chauffeur, moto_taxi, bus, cargo   A GERER DANS LE NOUVEAU WEB SERVICE
        session.setText(statut1); //Administrateur, Utilisateur etc...
        points.setText(getString(R.string.points) + " " + nbrePoints);
        bonus.setText(getString(R.string.bonus) + " " +nbreBonus);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDashboad.setVisibility(View.GONE);
            }
        });



        //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            //GESTION DES DATE DU MENU
            Calendar calendar = Calendar.getInstance();
            // String currentDate = DateFormat.getDateInstance().format(calendar.getTime());// 31.07.2019
            String currentDate2 = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            String[] part = currentDate2.split(" ");
            if (part[0].equalsIgnoreCase(currentDate2)) {
                Toast.makeText(getActivity(), getString(R.string.dateSystemePosePB) + " ", Toast.LENGTH_SHORT).show();
            } else {
                jourSemaine.setText(part[0]);
                String Day = "0" + part[1];
                if (Integer.parseInt(part[1]) < 10)
                    jour.setText(Day);
                else
                    jour.setText(part[1]);
                moisAnnee.setText(String.format("%s %s", part[2], part[3]));
            }
        }


        //GESTION DES EVENEMENTS SUR LES BOUTONS DU MENU
        InscriptionUserByAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Souscription.class);
                startActivity(intent);
            }
        });

        RechargeAvecCashAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeRecharge.class);
                intent.putExtra("idUser", idUser);
                intent.putExtra("compte", code_number_sender);
                startActivity(intent);
            }
        });
        ConsultSoldeAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsulterSolde.class);
                startActivity(intent);
            }
        });

        CheckCardNumberAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifierNumCarte.class);
                startActivity(intent);
            }
        });

        consulterHistoriqueAgent = (Button) view.findViewById(R.id.consulterHistoriqueAgent);
        consulterHistoriqueAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuHistoriqueTransaction.class);
                startActivity(intent);
            }
        });



        //paiement des factures
        btnPayerFacture = (LinearLayout) view.findViewById(R.id.btnPayerFacture);
        btnPayerFacture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PayerFacture.class);
                intent.putExtra("compte", code_number_sender);
                intent.putExtra("telephone", telephone);
                startActivity(intent);
            }
        });

        //paiement par Code QR
        btnQrCode = (LinearLayout) view.findViewById(R.id.btnQrCode);
        btnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MenuQRCode.class);
                startActivity(intent);

                /*IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Scan Encours...");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();*/
            }
        });

        //paiement des factures
        btnRetraitOperateur = (LinearLayout) view.findViewById(R.id.btnRetraitOperateur);
        btnRetraitOperateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuRetraitOperateur.class);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            consulterHistoriqueAgent.setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));
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
