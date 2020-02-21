package com.ezpass.smopaye_mobile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Assistance.HomeAssistanceOnline;
import com.ezpass.smopaye_mobile.vuesAccepteur.ConsulterRecette;
import com.ezpass.smopaye_mobile.vuesAccepteur.ConsulterSolde;
import com.ezpass.smopaye_mobile.vuesAccepteur.DebitCarte;
import com.ezpass.smopaye_mobile.vuesUtilisateur.MenuRechargeCompte;
import com.ezpass.smopaye_mobile.vuesAccepteur.MenuRetraitTelecollecte;
import com.ezpass.smopaye_mobile.vuesAccepteur.VerifierNumCarte;
import com.ezpass.smopaye_mobile.vuesUtilisateur.RechargePropreCompte;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AccueilFragment extends Fragment {

    private TextView jour, jourSemaine, moisAnnee;
    private LinearLayout debitCarte, MenuRechargeTelecollecte, ConsultationRecetteChauffeur, RechargeAvecCashChauffeur, VerifierNumCarteChauffeur, ConsultationSoldeChauffeur, btnPayerFacture, btnQrCode;
    private Button consulterHistoriqueAccepteur;
    FloatingActionButton assistanceOnline;

    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";
    //String code, numCarte, montant;

    public AccueilFragment() {
        // Required empty public constructor
    }

    /*public static AccueilFragment newInstanceQRCode(String codeQR, String numCarte, String MontantQR) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CODE, codeQR);
        args.putString("carte", numCarte);
        args.putString("montant", MontantQR);
        AccueilFragment fragment = new AccueilFragment();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*code = (String) getArguments().getSerializable(EXTRA_CODE);
        numCarte = (String) getArguments().getString("carte");
        montant = (String) getArguments().getString("montant");*/
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_accueil, container, false);

        assistanceOnline = view.findViewById(R.id.btnAssistanceOnline);

        jour = (TextView) view.findViewById(R.id.jour);
        jourSemaine = (TextView) view.findViewById(R.id.jourSemaine);
        moisAnnee   = (TextView) view.findViewById(R.id.moisAnnee);


/////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
            //Toast.makeText(getActivity(), temp, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
       /* assert getArguments() != null;
        String retour = getArguments().getString("result_BD");
        String telephone = getArguments().getString("telephone");
        assert retour != null;
        String[] parts = retour.split("-");

        tel = Objects.requireNonNull(getArguments()).getString("telephone");*/



       //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")){

            Calendar calendar = Calendar.getInstance();
            // String currentDate = DateFormat.getDateInstance().format(calendar.getTime());// 31.07.2019
            String currentDate2 = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            String[] part =currentDate2.split(" ");
            if(part[0].equalsIgnoreCase(currentDate2)){
                Toast.makeText(getActivity(), getString(R.string.dateSystemePosePB), Toast.LENGTH_SHORT).show();
            }
            else {
                jourSemaine.setText(part[0]);
                String Day = "0" + part[1];
                if(Integer.parseInt(part[1]) < 10)
                    jour.setText(Day);
                else
                    jour.setText(part[1]);
                moisAnnee.setText(String.format("%s %s", part[2], part[3]));
            }
        }




        //DEBITER UNE CARTE
        debitCarte = (LinearLayout) view.findViewById(R.id.btnDebitCarteChauffeur);
        debitCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), DebitCarte.class);
                intent2.putExtra("telephone", temp_number);
                startActivity(intent2);
            }
        });


        //MENU TELECOLLECTE
        MenuRechargeTelecollecte = (LinearLayout) view.findViewById(R.id.btnMenuRechargeTelecollecte);
        MenuRechargeTelecollecte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuRetraitTelecollecte.class));
            }
        });


        //CONSULTER RECETTE CHAUFFEUR
        ConsultationRecetteChauffeur = (LinearLayout) view.findViewById(R.id.btnConsultationRecetteChauffeur);
        ConsultationRecetteChauffeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ConsulterRecette.class));
            }
        });

        //MENU RECHARGE MA CARTE ET UNE AUTRE CARTE
        RechargeAvecCashChauffeur = (LinearLayout) view.findViewById(R.id.btnRechargeAvecCashChauffeur);
        RechargeAvecCashChauffeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RechargePropreCompte.class));
            }
        });

        //VERIFIER NUMERO DE LA CARTE
        VerifierNumCarteChauffeur = (LinearLayout) view.findViewById(R.id.btnVerifierNumCarteChauffeur);
        VerifierNumCarteChauffeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VerifierNumCarte.class);
                startActivity(intent);
            }
        });

        //CONSULTER SOLDE
        ConsultationSoldeChauffeur = (LinearLayout) view.findViewById(R.id.btnConsultationSoldeChauffeur);
        ConsultationSoldeChauffeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsulterSolde.class);
                startActivity(intent);
            }
        });



        consulterHistoriqueAccepteur = (Button) view.findViewById(R.id.consulterHistoriqueAccepteur);
        consulterHistoriqueAccepteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuHistoriqueTransaction.class);
                startActivity(intent);
                /*Intent intent = new Intent(getActivity(), ServicesIndisponible.class);
                startActivity(intent);*/
            }
        });


        //GESTION DES EVENEMENTS SUR LES BOUTONS DU MENU
        assistanceOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeAssistanceOnline.class);
                //intent.putExtra("resultatBD", resultat_bd);
                //intent.putExtra("telephone", telephone);
                startActivity(intent);
            }
        });


        //paiement des factures
        btnPayerFacture = (LinearLayout) view.findViewById(R.id.btnPayerFacture);
        btnPayerFacture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PayerFacture.class);
                intent.putExtra("telephone", temp_number);
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

        return view;
    }

}


