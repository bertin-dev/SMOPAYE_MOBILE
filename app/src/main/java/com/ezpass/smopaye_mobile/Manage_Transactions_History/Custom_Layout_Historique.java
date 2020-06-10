package com.ezpass.smopaye_mobile.Manage_Transactions_History;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.R;

public class Custom_Layout_Historique extends ArrayAdapter<String> {


    private String[] title_montant_valeur;
    private String[] title_donataire_valeur;
    private String[] title_beneficiaire_valeur;
    private String[] title_temps;
    private Activity context;

    public Custom_Layout_Historique(Activity context, String[] title_montant_valeur, String[] title_donataire_valeur, String[] title_beneficiaire_valeur, String[] title_temps) {
        super(context, R.layout.layout_historique, title_montant_valeur);

        this.title_montant_valeur = title_montant_valeur;
        this.title_donataire_valeur = title_donataire_valeur;
        this.title_beneficiaire_valeur = title_beneficiaire_valeur;
        this.title_temps = title_temps;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        ViewHolder viewHolder = null;

        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.layout_historique, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)r.getTag();
        }
        viewHolder.tvw1.setText(title_montant_valeur[position]);
        viewHolder.tvw2.setText(title_donataire_valeur[position]);
        viewHolder.tvw3.setText(title_beneficiaire_valeur[position]);
        viewHolder.tvw4.setText(title_temps[position]);
        return r;
    }

    class ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        TextView tvw4;

        ViewHolder(View v){
            tvw1 = (TextView)v.findViewById(R.id.title_montant_valeur);
            tvw2 = (TextView)v.findViewById(R.id.title_donataire_valeur);
            tvw3 = (TextView)v.findViewById(R.id.title_beneficiaire_valeur);
            tvw4 = (TextView)v.findViewById(R.id.date_transaction);
        }
    }

}
