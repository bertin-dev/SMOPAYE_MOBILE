package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.R;

import java.io.FileInputStream;


public class QRCodeModalDialog extends AppCompatDialogFragment {

    private EditText editTextMontant, edit_num_carte;
    private QRCodeModalDialog.ExampleDialogListener listener;
    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    private int c;
    private String file2 = "tmp_card_number";
    private String temp_card = "";
    private TextView statutOperation;


    public QRCodeModalDialog newInstanceCode(String numCarteBeneficiaire, String message) {
        Bundle args = new Bundle();
        args.putString("numCarteBeneficiaire", numCarteBeneficiaire);
        args.putString("message", message);
        QRCodeModalDialog frag = new QRCodeModalDialog();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_qr_code, null);


        editTextMontant = view.findViewById(R.id.edit_montant);
        edit_num_carte = view.findViewById(R.id.edit_num_carte);
        statutOperation = view.findViewById(R.id.statutOperation);
        statutOperation.setText((String) getArguments().getString("message"));

        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        readTempCardInFile();

        edit_num_carte.setText(temp_card);

        builder.setView(view)
                //.setTitle(getString(R.string.insererMontant))
                .setNegativeButton(getString(R.string.sortir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String beneficiaireCard = (String) getArguments().getString("numCarteBeneficiaire");
                        String donataireCard = edit_num_carte.getText().toString().trim();
                        String montant = editTextMontant.getText().toString().trim();

                        if(donataireCard.equalsIgnoreCase("")){
                            Toast.makeText(getContext(), getString(R.string.veuillezInsererCompte), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(montant.equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), getString(R.string.veuillezInsererMontant), Toast.LENGTH_SHORT).show();
                            return;
                        }

                            listener.applyTexts(beneficiaireCard, donataireCard, montant);
                    }
                });

        return builder.create();
    }

    private void readTempCardInFile() {
        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file2);
            while ((c = fIn.read()) != -1){
                temp_card = temp_card + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (QRCodeModalDialog.ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String beneficiaireCard, String donataireCard, String montant);
    }
}
