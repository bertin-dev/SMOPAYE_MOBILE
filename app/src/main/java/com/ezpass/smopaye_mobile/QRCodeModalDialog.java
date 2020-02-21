package com.ezpass.smopaye_mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;


public class QRCodeModalDialog extends AppCompatDialogFragment {

    private EditText editTextMontant, edit_num_carte;
    private QRCodeModalDialog.ExampleDialogListener listener;
    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_data_user";
    int c;
    String tmp_data = "";


    public QRCodeModalDialog newInstanceCode(String numCarteAccepteur) {
        Bundle args = new Bundle();
        args.putString("contenuCode", numCarteAccepteur);
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

        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file);
            while ((c = fIn.read()) != -1){
                tmp_data = tmp_data + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] parts = tmp_data.split("-");
        String cardNumber = parts[10]; // 12345678
        edit_num_carte.setText(cardNumber);

        builder.setView(view)
                .setTitle(getString(R.string.insererMontant))
                .setNegativeButton(getString(R.string.sortir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String numCarteAccepteur = (String) getArguments().getString("contenuCode");
                        String numCarteUser = edit_num_carte.getText().toString().trim();
                        String montant = editTextMontant.getText().toString().trim();

                        if(numCarteUser.equalsIgnoreCase("")){
                            Toast.makeText(getContext(), getString(R.string.veuillezInsererCompte), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(montant.equalsIgnoreCase("")) {
                            Toast.makeText(getContext(), getString(R.string.veuillezInsererMontant), Toast.LENGTH_SHORT).show();
                            return;
                        }

                            listener.applyTexts(numCarteAccepteur, numCarteUser, montant);
                    }
                });

        return builder.create();
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
        void applyTexts(String numCarteAccepteur, String numCarteUtilisateur, String montantUtilisateur);
    }
}
