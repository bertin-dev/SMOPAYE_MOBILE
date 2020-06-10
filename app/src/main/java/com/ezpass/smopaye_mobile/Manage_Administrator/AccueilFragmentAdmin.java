package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Manage_Cards.Accueil_Carte;
import com.ezpass.smopaye_mobile.Manage_Transactions_Story.MenuHistoriqueTransaction;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode.MenuQRCode;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Factures.PayerFacture;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.Manage_Consult.ConsulterSolde;
import com.ezpass.smopaye_mobile.Manage_Consult.VerifierNumCarte;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge.HomeRecharge;
import com.ezpass.smopaye_mobile.Manage_Register.Souscription;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccueilFragmentAdmin  extends Fragment {

    private TextView jour, jourSemaine, moisAnnee, myCategorie, myRole;
    private LinearLayout GesCompt, CheckCardNumber, ConsulterSolde, RechargeAvecCashAdmin, btnPayerFacture, btnQrCode, btn_gesCartes;
    private FloatingActionButton Register;
    private Button consulterHistoriqueAdmin;
    private String code_number_sender, telephone, role, categorie;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_accueil_admin, container, false);

        Register = view.findViewById(R.id.btnRegister);
        GesCompt = (LinearLayout) view.findViewById(R.id.btnGesCompt);
        CheckCardNumber = (LinearLayout) view.findViewById(R.id.btnCheckCardNumber);
        ConsulterSolde = (LinearLayout) view.findViewById(R.id.btnConsulterSolde);
        RechargeAvecCashAdmin = (LinearLayout) view.findViewById(R.id.btnRechargeAvecCashAdmin);
        btn_gesCartes = (LinearLayout) view.findViewById(R.id.btn_gesCartes);

        jour = (TextView) view.findViewById(R.id.jour);
        jourSemaine = (TextView) view.findViewById(R.id.jourSemaine);
        moisAnnee   = (TextView) view.findViewById(R.id.moisAnnee);

        myRole   = (TextView) view.findViewById(R.id.myRole);
        myCategorie   = (TextView) view.findViewById(R.id.myCategorie);

        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        assert getArguments() != null;
        code_number_sender = getArguments().getString("compte");
        telephone = getArguments().getString("telephone");
        categorie = getArguments().getString("categorie");
        role = getArguments().getString("role");

        myCategorie.setText(categorie);
        myRole.setText(role);



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
                if (Integer.parseInt(part[1]) < 10)
                    jour.setText(Day);
                else
                    jour.setText(part[1]);
                moisAnnee.setText(String.format("%s %s", part[2], part[3]));
            }
        }


        //GESTION DES EVENEMENTS SUR LES BOUTONS DU MENU
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Souscription.class);
               // intent.putExtra("Accepteur",numTelephone.getText().toString().trim());
                startActivity(intent);
            }
        });

        GesCompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Menu_GestionComptes.class);
                startActivity(intent);
            }
        });

        CheckCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifierNumCarte.class);
                startActivity(intent);
            }
        });

        ConsulterSolde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsulterSolde.class);
                startActivity(intent);
            }
        });


        RechargeAvecCashAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HomeRecharge.class);
                startActivity(intent);
            }
        });

        consulterHistoriqueAdmin = (Button) view.findViewById(R.id.consulterHistoriqueAdmin);
        consulterHistoriqueAdmin.setOnClickListener(new View.OnClickListener() {
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

        btn_gesCartes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Accueil_Carte.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
