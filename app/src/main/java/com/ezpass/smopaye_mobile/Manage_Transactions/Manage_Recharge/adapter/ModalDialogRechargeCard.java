package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge.adapter;

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

import com.ezpass.smopaye_mobile.R;

public class ModalDialogRechargeCard extends AppCompatDialogFragment {

    private EditText editTextMontant;
    private ExampleDialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_retrait_accepteur, null);

        builder.setView(view)
                .setTitle(getString(R.string.insererMontant))
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String montantRetrait = editTextMontant.getText().toString();
                        if(montantRetrait.equalsIgnoreCase("")) {
                            /*Snackbar.make(, "Replace with your own action", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();*/
                            Toast.makeText(getContext(), getString(R.string.veuillezInsererMontant), Toast.LENGTH_SHORT).show();
                        }
                        else
                        listener.applyTexts(montantRetrait);
                    }
                });


        editTextMontant = view.findViewById(R.id.edit_montant);

        return builder.create();
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
        void applyTexts(String montant);
    }

}
