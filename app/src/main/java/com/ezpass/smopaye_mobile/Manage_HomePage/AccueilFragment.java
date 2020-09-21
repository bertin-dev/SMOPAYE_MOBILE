package com.ezpass.smopaye_mobile.Manage_HomePage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.ezpass.smopaye_mobile.Manage_Assistance.HomeAssistanceOnline;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Factures.PayerFacture;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode.MenuQRCode;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_Withdrawal.MenuRetraitOperateur;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge.HomeRecharge;
import com.ezpass.smopaye_mobile.Manage_Transactions_History.MenuHistoriqueTransaction;
import com.ezpass.smopaye_mobile.Manage_Consult.ConsulterRecette;
import com.ezpass.smopaye_mobile.Manage_Consult.ConsulterSolde;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_DebitCards.DebitCarte;
import com.ezpass.smopaye_mobile.Manage_Consult.VerifierNumCarte;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccueilFragment extends Fragment {

    private TextView jour, jourSemaine, moisAnnee, myRole, myCategorie;
    private LinearLayout debitCarte, MenuRechargeTelecollecte, ConsultationRecetteChauffeur, RechargeAvecCashChauffeur, VerifierNumCarteChauffeur, ConsultationSoldeChauffeur, btnPayerFacture, btnQrCode;
    private Button consulterHistoriqueAccepteur;
    private FloatingActionButton assistanceOnline;

    private String code_number_sender, telephone, role, categorie, idUser;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    /*private Dialog epicDialog;
    private ImageView closePopupPositionImg;
    private TextView titleTv;
    private TextView messageTv;
    private RelativeLayout myBonus;*/

    private TextView points;
    private TextView bonus;
    private int nbreBonus;
    private int nbrePoints;
    private ImageView close;
    private RelativeLayout reportDashboad;

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

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        changeTheme();
        View view =  inflater.inflate(R.layout.fragment_accueil, container, false);

        assistanceOnline = view.findViewById(R.id.btnAssistanceOnline);

        jour = (TextView) view.findViewById(R.id.jour);
        jourSemaine = (TextView) view.findViewById(R.id.jourSemaine);
        moisAnnee   = (TextView) view.findViewById(R.id.moisAnnee);
        myRole   = (TextView) view.findViewById(R.id.myRole);
        myCategorie   = (TextView) view.findViewById(R.id.myCategorie);

        //epicDialog = new Dialog(getActivity());
        //myBonus = (RelativeLayout) view.findViewById(R.id.myBonus);
        //showPopupBonus();
        points = (TextView) view.findViewById(R.id.points);
        bonus   = (TextView) view.findViewById(R.id.bonus);
        close = (ImageView) view.findViewById(R.id.close);
        reportDashboad = (RelativeLayout) view.findViewById(R.id.reportDashboad);



        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        assert getArguments() != null;
         code_number_sender = getArguments().getString("compte");
         telephone = getArguments().getString("telephone");
         categorie = getArguments().getString("categorie");
         role = getArguments().getString("role");
         idUser = getArguments().getString("idUser");
         nbrePoints = getArguments().getInt("points", 0);
         nbreBonus = getArguments().getInt("bonus", 0);

        myRole.setText(role);
        myCategorie.setText(categorie);
        points.setText(getString(R.string.points) + " " + nbrePoints);
        bonus.setText(getString(R.string.bonus) + " " +nbreBonus);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animatoo.animateInAndOut(getContext());
                reportDashboad.setVisibility(View.GONE);
            }
        });

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
                intent2.putExtra("telephone", telephone);
                intent2.putExtra("compte", code_number_sender);
                startActivity(intent2);
            }
        });


        //MENU TELECOLLECTE
        MenuRechargeTelecollecte = (LinearLayout) view.findViewById(R.id.btnMenuRechargeTelecollecte);
        MenuRechargeTelecollecte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), MenuRetraitTelecollecte.class));
                Intent intent = new Intent(getActivity(), MenuRetraitOperateur.class);
                startActivity(intent);
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
                Intent intent = new Intent(getActivity(), HomeRecharge.class);
                intent.putExtra("idUser", idUser);
                intent.putExtra("compte", code_number_sender);
                startActivity(intent);
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
            }
        });


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
        return view;
    }

    /*private void showPopupBonus() {
        epicDialog.setContentView(R.layout.bonus_modal_dialog);
        closePopupPositionImg = (ImageView) epicDialog.findViewById(R.id.closePopupPositionImg);
        titleTv = (TextView) epicDialog.findViewById(R.id.titleTv);
        messageTv = (TextView) epicDialog.findViewById(R.id.messageTv);

        closePopupPositionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });

        epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicDialog.show();
    }*/


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            consulterHistoriqueAccepteur.setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));
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


