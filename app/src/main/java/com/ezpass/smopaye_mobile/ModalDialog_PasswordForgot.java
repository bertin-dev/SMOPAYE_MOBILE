package com.ezpass.smopaye_mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class ModalDialog_PasswordForgot extends AppCompatDialogFragment {

    private ModalDialog_PasswordForgot.ExampleDialogListener listener;
    private TextInputLayout telRetrieve, cniRetrieve;
    private Spinner typePjustificative;
    private String[] pieceJ;
    private TextView title;
    private Button btnForgetPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_forgot_password, null);


        telRetrieve = view.findViewById(R.id.telRetrieve);
        cniRetrieve = view.findViewById(R.id.cniRetrieve);
        typePjustificative = view.findViewById(R.id.typePjustificative);
        title = view.findViewById(R.id.title);
        btnForgetPassword = view.findViewById(R.id.btnForgetPassword);


        //Vérification si la langue du telephone est en Francais
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {

            // Initializing a String Array
            pieceJ = new String[]{
                    "CNI",
                    "passeport",
                    "recipissé",
                    "carte de séjour",
                    "carte d'étudiant"
            };
        } else {
            // Initializing a String Array
            pieceJ = new String[]{
                    "CNI",
                    "passport",
                    "receipt",
                    "residence permit",
                    "student card"
            };
        }
        // Initializing an ArrayAdapter justificatives
        ArrayAdapter<String> spinnerArrayAdapter7 = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_item, pieceJ);
        spinnerArrayAdapter7.setDropDownViewResource(R.layout.spinner_item);
        typePjustificative.setAdapter(spinnerArrayAdapter7);


        typePjustificative.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cniRetrieve.setHint("N° CNI");
                        } else {
                            cniRetrieve.setHint("N° CNI");
                        }
                        break;
                    case 1:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cniRetrieve.setHint("N° Passeport");
                        } else {
                            cniRetrieve.setHint("N° Passport");
                        }
                        break;
                    case 2:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cniRetrieve.setHint("N° Recipissé");
                        } else {
                            cniRetrieve.setHint("N° Receipt");
                        }
                        break;
                    case 3:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cniRetrieve.setHint("N° Carte de séjour");
                        } else {
                            cniRetrieve.setHint("N° Residence permit");
                        }
                        break;
                    case 4:
                        if ((Locale.getDefault().getLanguage().contentEquals("fr"))) {
                            cniRetrieve.setHint("N° Carte d'étudiant");
                        } else {
                            cniRetrieve.setHint("N° Student card");
                        }
                        break;
                    default:
                        cniRetrieve.setHint("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        telRetrieve.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));
        cniRetrieve.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135,206,250)));

        title.setText(R.string.getPasswordForgot);
        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String telephone = telRetrieve.getEditText().getText().toString().trim();
                String pieceJustificative = typePjustificative.getSelectedItem().toString().trim() + "-" +cniRetrieve.getEditText().getText().toString().trim();

                if(!validateTelephone()){
                    return;
                }

                if(!validatePJ()){
                    return;
                }

                listener.applyTexts(telephone.trim(), pieceJustificative.trim());

            }
        });
        /*builder.setView(view)
                //.setTitle(getString(R.string.information))
                .setNegativeButton(getString(R.string.annuler), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(getString(R.string.confirmer), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String telephone = telRetrieve.getEditText().getText().toString().trim();
                        String pieceJustificative = cniRetrieve.getEditText().getText().toString().trim();

                        if(!validateTelephone()){
                            return;
                        }

                        if(!validatePJ()){
                            return;
                        }

                        listener.applyTexts(telephone.trim(), pieceJustificative.trim());
                    }
                });*/
        builder.setView(view);
        return builder.create();
    }


    private Boolean validateTelephone(){
        String numero = telRetrieve.getEditText().getText().toString().trim();
        if(numero.isEmpty()){
            telRetrieve.setError(getString(R.string.insererTelephone));
            return false;
        } else if(numero.length() < 9){
            telRetrieve.setError(getString(R.string.telephoneCourt));
            return false;
        } else {
            telRetrieve.setError(null);
            return true;
        }
    }



    private Boolean validatePJ(){
        String numero = cniRetrieve.getEditText().getText().toString().trim();
        if(numero.isEmpty()){
            cniRetrieve.setError(getString(R.string.insererNumPJ));
            return false;
        } else if(numero.length() < 3){
            cniRetrieve.setError(getString(R.string.numPJCourt));
            return false;
        } else {
            cniRetrieve.setError(null);
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ModalDialog_PasswordForgot.ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String tel, String pj);
    }
}