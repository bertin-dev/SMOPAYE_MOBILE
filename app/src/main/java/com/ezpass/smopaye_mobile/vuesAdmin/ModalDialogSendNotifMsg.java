package com.ezpass.smopaye_mobile.vuesAdmin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;

public class ModalDialogSendNotifMsg extends AppCompatDialogFragment {

    private TextInputLayout editTextmsgNotif, edit_titre_notif;
    private ExampleDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_msg_for_notif, null);

        builder.setView(view)
                .setTitle("Envoi à Tous les Utilisateurs")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String contentTitre = edit_titre_notif.getEditText().getText().toString();
                        String contentMsg = editTextmsgNotif.getEditText().getText().toString();

                        SendData(contentTitre, contentMsg);

                    }
                });


        editTextmsgNotif = view.findViewById(R.id.edit_msg_notif);
        edit_titre_notif = view.findViewById(R.id.edit_titre_notif);


        editTextmsgNotif.setErrorTextColor(ColorStateList.valueOf(Color.BLUE));
        edit_titre_notif.setErrorTextColor(ColorStateList.valueOf(Color.BLUE));

        return builder.create();
    }


    private void SendData(String titre, String message){

        if(!validateTitre() | !validateMessage()){
            return;
        }
        listener.applyTexts("", titre, message);
    }


    private boolean validateTitre(){
        String titre = edit_titre_notif.getEditText().getText().toString().trim();

        if(titre.isEmpty()){
            edit_titre_notif.setError("Veuillez inserer un titre.");
            Toast.makeText(getActivity(), "Veuillez inserer un message.", Toast.LENGTH_SHORT).show();
            return false;
        }  else {
            edit_titre_notif.setError(null);
            return true;
        }
    }


    private Boolean validateMessage(){
        String numero = editTextmsgNotif.getEditText().getText().toString().trim();
        if(numero.isEmpty()){
            editTextmsgNotif.setError("Veuillez inserer un message.");
            Toast.makeText(getActivity(), "Veuillez inserer un message.", Toast.LENGTH_SHORT).show();
            return false;
        }  else {
            editTextmsgNotif.setError(null);
            return true;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String carte, String titreNotif, String msgNotif);
    }

}
