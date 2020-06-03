package com.ezpass.smopaye_mobile.vuesUtilisateur.Recharge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.R;

public class ModalDialog_ValidateRecharge extends AppCompatDialogFragment {

    private static final String TAG = "ModalDialog_ValidateRec";
    private ModalDialog_ValidateRecharge.ExampleDialogListener listener;
    private TextView msgValidateRecharge;
    private TextView timerRecharge;
    private Button btnValidateRecharge;
    private FragmentActivity myContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_validate_recharge, null);


        btnValidateRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = 1;
                listener.applyTexts(number);

            }
        });
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        myContext=(FragmentActivity) context;
        super.onAttach(context);

        try {
            listener = (ModalDialog_ValidateRecharge.ExampleDialogListener) myContext;
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: " + e.getMessage());
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(int number);
    }
}