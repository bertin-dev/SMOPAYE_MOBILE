package com.ezpass.smopaye_mobile.Manage_Transactions_History;

import android.annotation.SuppressLint;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.web_service_historique_trans.Users;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Custom_Layout_Historique extends ArrayAdapter<Users> {


    private String[] title_montant_valeur;
    private String[] title_operation;
    private String[] title_frais;
    private String[] title_temps;
    private Activity context;
    private List<Users> userList;
    private static final String TAG = "Custom_Layout_Historiqu";
    //DecimalFormat df = new DecimalFormat("0.00"); // import java.text.DecimalFormat;

    public Custom_Layout_Historique(Activity context, String[] title_montant_valeur, String[] title_operation, String[] title_frais, String[] title_temps, List<Users> userList) {
        super(context, R.layout.layout_historique, userList);

        this.title_montant_valeur = title_montant_valeur;
        this.title_operation = title_operation;
        this.title_frais = title_frais;
        this.title_temps = title_temps;
        this.context = context;
        this.userList = userList;
    }

    @SuppressLint("SetTextI18n")
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

        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy à HH:mm:ss");
        Date date = null;
        String startDateStrNewFormat = null;

        try {
            date = inputFormat.parse(title_temps[position]);
            assert date != null;
            startDateStrNewFormat = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.tvw1.setText(title_montant_valeur[position] + " Fcfa");
        viewHolder.tvw4.setText(startDateStrNewFormat);

        if(title_frais[position] != null)
            viewHolder.tvw6.setText(title_frais[position] + " Fcfa");
        else
            viewHolder.tvw6.setText(0 + " Fcfa");


        //interaction entre particulier uniquement
        if(userList.get(position).getParticulier() != null && userList.get(position).getEntreprise() == null){
            Log.w(TAG, "getView particulier uniquement:----------------------------------- " + userList.size() );
            if(userList.get(position).getParticulier().getType().toLowerCase().equalsIgnoreCase("emetteur")) {
                viewHolder.tvw2.setText(userList.get(position).getParticulier().getFirstname().toLowerCase());
                if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                    viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Emis un " + title_operation[position].toLowerCase());
                }else{
                    viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname().toLowerCase()+ " vous avez Emis un " + title_operation[position].toLowerCase());
                }
            } else{
                viewHolder.tvw3.setText(userList.get(position).getParticulier().getFirstname().toLowerCase());
                if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                    viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Réçu un " + title_operation[position].toLowerCase());
                }else{
                    viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Réçu un " + title_operation[position].toLowerCase());
                }
            }
        }

        //interaction entre entreprise uniquement
        if(userList.get(position).getEntreprise() != null && userList.get(position).getParticulier() == null){
            Log.w(TAG, "getView entreprise uniquement:----------------------------------- " + userList.size() );
            if(userList.get(position).getEntreprise().getType().toLowerCase().equalsIgnoreCase("emetteur")){
                viewHolder.tvw2.setText(userList.get(position).getEntreprise().getRaison_social().toLowerCase());
                viewHolder.tvw5.setText("Le " + userList.get(position).getEntreprise().getEntite().toLowerCase() + " a Emis un " + title_operation[position].toLowerCase());
            } else {

                if(userList.get(position).getEntreprise().getRaison_social() != null) {
                    viewHolder.tvw3.setText(userList.get(position).getEntreprise().getRaison_social().toLowerCase());
                    viewHolder.tvw5.setText("Le. " + userList.get(position).getEntreprise().getEntite().toLowerCase() + " a Réçu un " + title_operation[position].toLowerCase());
                }
            }
        }


        //interaction entre entreprise et particulier
        else if(userList.get(position).getEntreprise() != null && userList.get(position).getParticulier() != null){
            Log.w(TAG, "getView entreprise et particulier:----------------------------------- " + userList.size() );
            if(userList.get(position).getParticulier().getType().toLowerCase().equalsIgnoreCase("emetteur")) {
                viewHolder.tvw2.setText(userList.get(position).getParticulier().getFirstname().toLowerCase());
                if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                    viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Emis un " + title_operation[position].toLowerCase());
                }else{
                    viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname().toLowerCase()+ " vous avez Emis un " + title_operation[position].toLowerCase());
                }
            } else{
                viewHolder.tvw3.setText(userList.get(position).getParticulier().getFirstname().toLowerCase());
                if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                    viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Réçu un " + title_operation[position].toLowerCase());
                }else{
                    viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Réçu un " + title_operation[position].toLowerCase());
                }
            }


            if(userList.get(position).getEntreprise().getType().toLowerCase().equalsIgnoreCase("emetteur")){
                viewHolder.tvw2.setText(userList.get(position).getEntreprise().getRaison_social().toLowerCase());
                viewHolder.tvw5.setText("Le " + userList.get(position).getEntreprise().getEntite().toLowerCase() + " a Emis un " + title_operation[position].toLowerCase());
            } else {

                if(userList.get(position).getEntreprise().getRaison_social() != null) {
                    viewHolder.tvw3.setText(userList.get(position).getEntreprise().getRaison_social().toLowerCase());
                    viewHolder.tvw5.setText("Le. " + userList.get(position).getEntreprise().getEntite().toLowerCase() + " a Réçu un " + title_operation[position].toLowerCase());
                }
            }

        }

        //VERIFIER S'IL S'AGIT D'UNE ENTREPRISE OU UN PARTICULIER QUI A FAIT UNE OPERATION
        /*if(userList.get(position).getEntreprise() == null){

            if(userList.get(position).getEntreprise().getType().toLowerCase().equalsIgnoreCase("emetteur")) {
                viewHolder.tvw2.setText(userList.get(position).getParticulier().getFirstname().toLowerCase());
                if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                    viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Emis un " + title_operation[position].toLowerCase());
                }else{
                    viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname().toLowerCase()+ " vous avez Emis un " + title_operation[position].toLowerCase());
                }
            } else{
                viewHolder.tvw3.setText(userList.get(position).getParticulier().getFirstname().toLowerCase());
                if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                    viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Réçu un " + title_operation[position].toLowerCase());
                }else{
                    viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname().toLowerCase() + " vous avez Réçu un " + title_operation[position].toLowerCase());
                }
            }


        } else {

            if(userList.get(position).getParticulier().getType().toLowerCase().equalsIgnoreCase("emetteur")){
                viewHolder.tvw2.setText(userList.get(position).getEntreprise().getRaison_social().toLowerCase());
                viewHolder.tvw5.setText("Le " + userList.get(position).getEntreprise().getEntite().toLowerCase() + " a Emis un " + title_operation[position].toLowerCase());
            } else {
                viewHolder.tvw3.setText(userList.get(position).getEntreprise().getRaison_social().toLowerCase());
                viewHolder.tvw5.setText("Le. " + userList.get(position).getEntreprise().getEntite().toLowerCase() + " a Réçu un " + title_operation[position].toLowerCase());
            }
        }*/













        /*if(userList.get(position).getEntreprise().getType().toLowerCase().equalsIgnoreCase("emetteur")) {
            viewHolder.tvw2.setText(userList.get(position).getParticulier().getFirstname().toLowerCase() + " " + userList.get(position).getParticulier().getLastname().toLowerCase());
            if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname() + " vous avez Emis un paiement par " + title_operation[position]);
            }else{
                viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname() + " vous avez Emis un paiement par " + title_operation[position]);
            }
        } else{
            viewHolder.tvw3.setText(userList.get(position).getParticulier().getLastname() + " " + userList.get(position).getParticulier().getLastname());
            if(userList.get(position).getParticulier().getGender().toLowerCase().equalsIgnoreCase("masculin")){
                viewHolder.tvw5.setText("M. " + userList.get(position).getParticulier().getFirstname() + " vous avez Réçu un paiement par " + title_operation[position]);
            }else{
                viewHolder.tvw5.setText("Mme. " + userList.get(position).getParticulier().getFirstname() + " vous avez Réçu un paiement par " + title_operation[position]);
            }
        }*/


        return r;
    }

    class ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        TextView tvw4;
        TextView tvw5;
        TextView tvw6;

        ViewHolder(View v){
            tvw1 = (TextView)v.findViewById(R.id.title_montant_valeur);
            tvw2 = (TextView)v.findViewById(R.id.title_donataire_valeur);
            tvw3 = (TextView)v.findViewById(R.id.title_beneficiaire_valeur);
            tvw4 = (TextView)v.findViewById(R.id.date_transaction);
            tvw5 = (TextView)v.findViewById(R.id.typePaiement);
            tvw6 = (TextView)v.findViewById(R.id.title_frais_valeur);
        }
    }

}
