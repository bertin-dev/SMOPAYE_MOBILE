package com.ezpass.smopaye_mobile.Manage_HomePage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Assistance.HomeAssistanceOnline;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Factures.PayerFacture;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode.MenuQRCode;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.MenuRetraitOperateur;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge.HomeRecharge;
import com.ezpass.smopaye_mobile.Manage_Transactions_History.MenuHistoriqueTransaction;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.Manage_Consult.ConsulterSolde;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccueilFragmentUser extends Fragment {

    private TextView jour, jourSemaine, moisAnnee, myRole, myCategorie;
    private LinearLayout btnRecharge, btnConsulter, btnPayerFacture, btnQrCode, btnRetraitOperateur;
    //private LinearLayout CheckCardNumber;
    private FloatingActionButton assistanceOnline;
    private Button consulterHistoriqueUser;
    private String role;
    private String telephone;
    private String categorie;
    private String myState;
    private String code_number_sender;
    private String idUser;
    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        changeTheme();
        View view =  inflater.inflate(R.layout.fragment_accueil_user, container, false);

        btnRecharge = (LinearLayout) view.findViewById(R.id.btnRecharger);
        btnConsulter = (LinearLayout) view.findViewById(R.id.btnConsultSolde);
        //CheckCardNumber = (LinearLayout) view.findViewById(R.id.btnCheckCardNumber);
        assistanceOnline = view.findViewById(R.id.btnAssistanceOnline);



        jour = (TextView) view.findViewById(R.id.jour);
        jourSemaine = (TextView) view.findViewById(R.id.jourSemaine);
        moisAnnee   = (TextView) view.findViewById(R.id.moisAnnee);

        myRole   = (TextView) view.findViewById(R.id.myRole);
        myCategorie   = (TextView) view.findViewById(R.id.myCategorie);


        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        assert getArguments() != null;
        role = getArguments().getString("role");
        telephone = getArguments().getString("telephone");
        categorie = getArguments().getString("categorie");
        myState = getArguments().getString("etat");
        code_number_sender = getArguments().getString("compte");
        idUser = getArguments().getString("idUser");



        myCategorie.setText(categorie);
        myRole.setText(role);

        Toast.makeText(getContext(), role, Toast.LENGTH_SHORT).show();
        if(!"actif".equalsIgnoreCase(myState)) {
            myRole.setText(getString(R.string.user));
        }


        //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            //GESTION DES DATE DU MENU
            Calendar calendar = Calendar.getInstance();
            // String currentDate = DateFormat.getDateInstance().format(calendar.getTime());// 31.07.2019
            String currentDate2 = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            String[] part = currentDate2.split(" ");
            if (part[0].equalsIgnoreCase(currentDate2)) {
                Toast.makeText(getActivity(), getString(R.string.dateSystemePosePB), Toast.LENGTH_SHORT).show();
            } else {
                jourSemaine.setText(part[0]);
                String Day = "0" + part[1];
                if (Integer.parseInt(part[1]) < 10 )
                    jour.setText(Day);
                else
                    jour.setText(part[1]);
                moisAnnee.setText(String.format("%s %s", part[2], part[3]));
            }
        }


        //GESTION DES EVENEMENTS SUR LES BOUTONS DU MENU

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeRecharge.class);
                intent.putExtra("idUser", idUser);
                intent.putExtra("compte", code_number_sender);
                startActivity(intent);
            }
        });

        btnConsulter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsulterSolde.class);
                startActivity(intent);
            }
        });

        /*CheckCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifierNumCarte.class);
                startActivity(intent);
            }
        });*/


        //GESTION DES EVENEMENTS SUR LES BOUTONS DU MENU
        assistanceOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeAssistanceOnline.class);
                intent.putExtra("role", role);
                intent.putExtra("telephone", telephone);
                startActivity(intent);
            }
        });

        consulterHistoriqueUser = (Button) view.findViewById(R.id.consulterHistoriqueUser);
        consulterHistoriqueUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuHistoriqueTransaction.class);
                startActivity(intent);
                /*Intent intent = new Intent(getActivity(), ServicesIndisponible.class);
                startActivity(intent);*/
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
            consulterHistoriqueUser.setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));
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
