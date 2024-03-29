package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModalDialogSendONENotifMsg extends AppCompatDialogFragment {

    private TextInputLayout editTextmsgNotif, edit_titre_notif;
    private ExampleDialogListener1 listener;
    private String carte;


    private Spinner spinner;
    private DatabaseReference mDatabase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_msg_for_notif, null);

        spinner = (Spinner) view.findViewById(R.id.spinnerUserGoogle);
        editTextmsgNotif = view.findViewById(R.id.edit_msg_notif);
        edit_titre_notif = view.findViewById(R.id.edit_titre_notif);


        spinner.setVisibility(view.VISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mDatabase.orderByChild("nom");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final List<String> titleList = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    String nom = dataSnapshot1.child("nom").getValue(String.class);
                    String prenom = dataSnapshot1.child("prenom").getValue(String.class);
                    String id_carte = dataSnapshot1.child("id_carte").getValue(String.class);
                    titleList.add(nom + " " + prenom + "-" + id_carte);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, titleList);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        //RECUPERATION DE L ID DANS LE SPINNER
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = spinner.getSelectedItem().toString();
                String[] part = item.split("-");
                carte = part[1];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        builder.setView(view)
                .setTitle(getString(R.string.envoiOneUser))
                .setNegativeButton(getString(R.string.sortir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String contentTitre = edit_titre_notif.getEditText().getText().toString();
                        String contentMsg = editTextmsgNotif.getEditText().getText().toString();

                        SendData(contentTitre, contentMsg);

                    }
                });

        editTextmsgNotif.setErrorTextColor(ColorStateList.valueOf(Color.BLUE));
        edit_titre_notif.setErrorTextColor(ColorStateList.valueOf(Color.BLUE));

        return builder.create();
    }


    private void SendData(String titre, String message){

        if(!validateTitre() | !validateMessage()){
            return;
        }
        listener.applyTexts(carte, titre, message);
    }


    private boolean validateTitre(){
        String titre = edit_titre_notif.getEditText().getText().toString().trim();

        if(titre.isEmpty()){
            edit_titre_notif.setError(getString(R.string.insererTitre));
            Toast.makeText(getActivity(), getString(R.string.insererTitre), Toast.LENGTH_SHORT).show();
            return false;
        }  else {
            edit_titre_notif.setError(null);
            return true;
        }
    }


    private Boolean validateMessage(){
        String numero = editTextmsgNotif.getEditText().getText().toString().trim();
        if(numero.isEmpty()){
            editTextmsgNotif.setError(getString(R.string.insererMsg));
            Toast.makeText(getActivity(), getString(R.string.insererMsg), Toast.LENGTH_SHORT).show();
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
            listener = (ExampleDialogListener1) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ExampleDialogListener1");
        }
    }

    public interface ExampleDialogListener1 {
        void applyTexts(String carte, String titreNotif, String msgNotif);
    }

}
