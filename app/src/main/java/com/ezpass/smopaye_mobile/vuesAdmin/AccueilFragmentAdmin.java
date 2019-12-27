package com.ezpass.smopaye_mobile.vuesAdmin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.MenuHistoriqueTransaction;
import com.ezpass.smopaye_mobile.PayerFacture;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.ServicesIndisponible;
import com.ezpass.smopaye_mobile.vuesAccepteur.ConsulterSolde;
import com.ezpass.smopaye_mobile.vuesAccepteur.VerifierNumCarte;
import com.ezpass.smopaye_mobile.vuesUtilisateur.RechargePropreCompte;
import com.ezpass.smopaye_mobile.vuesUtilisateur.Souscription;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Calendar;

import static android.content.Context.CLIPBOARD_SERVICE;

public class AccueilFragmentAdmin  extends Fragment {

    TextView jour, jourSemaine, moisAnnee;
    LinearLayout GesCompt, CheckCardNumber, ConsulterSolde, RechargeAvecCashAdmin, btnPayerFacture, btnQrCode;
    FloatingActionButton Register;
    private Button consulterHistoriqueAdmin;
    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_accueil_admin, container, false);

        Register = view.findViewById(R.id.btnRegister);
        GesCompt = (LinearLayout) view.findViewById(R.id.btnGesCompt);
        CheckCardNumber = (LinearLayout) view.findViewById(R.id.btnCheckCardNumber);
        ConsulterSolde = (LinearLayout) view.findViewById(R.id.btnConsulterSolde);
        RechargeAvecCashAdmin = (LinearLayout) view.findViewById(R.id.btnRechargeAvecCashAdmin);

        jour = (TextView) view.findViewById(R.id.jour);
        jourSemaine = (TextView) view.findViewById(R.id.jourSemaine);
        moisAnnee   = (TextView) view.findViewById(R.id.moisAnnee);

        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        //GESTION DES DATE DU MENU
        Calendar calendar = Calendar.getInstance();
        // String currentDate = DateFormat.getDateInstance().format(calendar.getTime());// 31.07.2019
        String currentDate2 = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        String[] part =currentDate2.split(" ");
        if(part[0].equalsIgnoreCase(currentDate2)){
            Toast.makeText(getActivity(), "la date est en Anglais", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(getActivity(), RechargePropreCompte.class);
                startActivity(intent);
            }
        });

        consulterHistoriqueAdmin = (Button) view.findViewById(R.id.consulterHistoriqueAdmin);
        consulterHistoriqueAdmin.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("telephone", temp_number);
                startActivity(intent);
            }
        });

        //paiement par Code QR
        btnQrCode = (LinearLayout) view.findViewById(R.id.btnQrCode);
        btnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setPrompt("Scan Encours...");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
            }
        });

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null && result.getContents() != null){

            new AlertDialog.Builder(getActivity())
                    .setTitle("Scan Result")
                    .setMessage(result.getContents())
                    .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData data = ClipData.newPlainText("result", result.getContents());
                            manager.setPrimaryClip(data);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
