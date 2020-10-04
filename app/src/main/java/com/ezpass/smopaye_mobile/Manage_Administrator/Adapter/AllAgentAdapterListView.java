package com.ezpass.smopaye_mobile.Manage_Administrator.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response.AllParticuliersList;
import com.ezpass.smopaye_mobile.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllAgentAdapterListView extends BaseAdapter {

    private static final String TAG = "AllAgentAdapterListView";
    private Context context;
    private List<AllParticuliersList> modelUsersLists;
    private DecimalFormat df = new DecimalFormat("0.00");
    private LayoutInflater inflater;
    ArrayList<AllParticuliersList> arrayList;

    public AllAgentAdapterListView(Context context, List<AllParticuliersList> modelUsersLists) {
        this.context = context;
        this.modelUsersLists = modelUsersLists;
        inflater = LayoutInflater.from(context);
        this.arrayList = new ArrayList<AllParticuliersList>();
        this.arrayList.addAll(modelUsersLists);
    }

    @Override
    public int getCount() {
        return modelUsersLists.size();
    }

    @Override
    public Object getItem(int position) {
        return modelUsersLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        MyViewHolder myViewHolder;
        if(convertView==null){
            myViewHolder = new MyViewHolder();
            convertView = inflater.inflate(R.layout.all_smopaye_users, null);


            //creation d'une animation
            //myViewHolder.profil.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
            //myViewHolder.view_foreground.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

            myViewHolder.profil = convertView.findViewById(R.id.profil);
            myViewHolder.nom = convertView.findViewById(R.id.nom);
            myViewHolder.prenom = convertView.findViewById(R.id.prenom);
            myViewHolder.cni = convertView.findViewById(R.id.cni);
            myViewHolder.genre = convertView.findViewById(R.id.genre);
            myViewHolder.telephone = convertView.findViewById(R.id.telephone);
            myViewHolder.adresse = convertView.findViewById(R.id.adresse);
            myViewHolder.etat_user = convertView.findViewById(R.id.etat_user);
            myViewHolder.date_creation = convertView.findViewById(R.id.date_creation);
            myViewHolder.role = convertView.findViewById(R.id.role);
            myViewHolder.type_role = convertView.findViewById(R.id.type_role);
            myViewHolder.numCompte = convertView.findViewById(R.id.numCompte);
            myViewHolder.company = convertView.findViewById(R.id.company);
            myViewHolder.etat_compte = convertView.findViewById(R.id.etat_compte);
            myViewHolder.montant_compte = convertView.findViewById(R.id.montant_compte);
            myViewHolder.num_carte = convertView.findViewById(R.id.num_carte);
            myViewHolder.num_serie = convertView.findViewById(R.id.num_serie);
            myViewHolder.type_carte = convertView.findViewById(R.id.type_carte);
            myViewHolder.etat_carte = convertView.findViewById(R.id.etat_carte);
            myViewHolder.unity = convertView.findViewById(R.id.unity);
            myViewHolder.deposit = convertView.findViewById(R.id.deposit);
            myViewHolder.view_foreground = convertView.findViewById(R.id.view_foreground);

            convertView.setTag(myViewHolder);

        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }



        List<AllParticuliersList> usersList = modelUsersLists;


        myViewHolder.profil.setImageResource(R.drawable.ic_account);
        myViewHolder.nom.setText(usersList.get(position).getLastname().toUpperCase());
        myViewHolder.prenom.setText(usersList.get(position).getFirstname());
        myViewHolder.cni.setText(context.getString(R.string.cni1) + ": " + usersList.get(position).getCni());
        myViewHolder.genre.setText(context.getString(R.string.genre) + ": " + usersList.get(position).getGender());
        myViewHolder.telephone.setText(context.getString(R.string.telephone) + ": " + usersList.get(position).getUser().getPhone());
        myViewHolder.adresse.setText(context.getString(R.string.adresse) + ": " + usersList.get(position).getUser().getAddress());

                if(usersList.get(position).getUser().getState().toLowerCase().equalsIgnoreCase("activer"))
                    myViewHolder.etat_user.setText(context.getString(R.string.etat) + " Actif");
                else
                    myViewHolder.etat_user.setText(context.getString(R.string.etat) + " Inactif");

        myViewHolder.date_creation.setText(context.getString(R.string.create) + "" + usersList.get(position).getCreated_at().substring(0,10));

                //Role
        myViewHolder.role.setText(context.getString(R.string.role) + " :" + usersList.get(position).getUser().getRole().getName());
        myViewHolder.type_role.setText(context.getString(R.string.typeRole) + " :" +usersList.get(position).getUser().getRole().getType());

                //Compte
        myViewHolder.numCompte.setText(context.getString(R.string.numCompte) + " :" +usersList.get(position).getUser().getCompte().getAccount_number());
        myViewHolder.company.setText(context.getString(R.string.compagnie) + " :" +usersList.get(position).getUser().getCompte().getCompany());

                if(usersList.get(position).getUser().getCompte().getAccount_state().toLowerCase().equalsIgnoreCase("activer"))
                    myViewHolder.etat_compte.setText(context.getString(R.string.etatCompte) + ": Activé");
                else
                    myViewHolder.etat_compte.setText(context.getString(R.string.etatCompte) + ": Désactivé");

        myViewHolder.montant_compte.setText(context.getString(R.string.montant) + " :" + df.format(Float.parseFloat(usersList.get(position).getUser().getCompte().getAmount())) + " Fcfa");

                //Carte
                for(int j =0; j<usersList.get(position).getUser().getCards().size(); j++){
                    myViewHolder.num_carte.setText(context.getString(R.string.numCarte) + " :" + usersList.get(position).getUser().getCards().get(j).getCode_number());
                    myViewHolder.num_serie.setText(context.getString(R.string.serial) + " :" + usersList.get(position).getUser().getCards().get(j).getSerial_number());
                    myViewHolder.type_carte.setText(context.getString(R.string.typeCarte) + " :" + usersList.get(position).getUser().getCards().get(j).getType());

                    if(usersList.get(position).getUser().getCards().get(j).getCard_state().toLowerCase().equalsIgnoreCase("activer"))
                        myViewHolder.etat_carte.setText(context.getString(R.string.etatCarte) + ": Activé");
                    else
                        myViewHolder.etat_carte.setText(context.getString(R.string.etatCarte) + ": Désactivé");

                    myViewHolder.unity.setText(context.getString(R.string.unite) + " :" + df.format(Float.parseFloat(usersList.get(position).getUser().getCards().get(j).getUnity())) + " Fcfa");
                    myViewHolder.deposit.setText(context.getString(R.string.depot) + " :" + df.format(Float.parseFloat(usersList.get(position).getUser().getCards().get(j).getDeposit())) + " Fcfa");
                }





        return convertView;
    }


    public class MyViewHolder {

        ImageView profil;
        TextView nom;
        TextView prenom;
        TextView cni;
        TextView genre;
        TextView telephone;
        TextView adresse;
        TextView etat_user;
        TextView date_creation;
        TextView role;
        TextView type_role;
        TextView numCompte;
        TextView company;
        TextView etat_compte;
        TextView montant_compte;
        TextView num_carte;
        TextView num_serie;
        TextView type_carte;
        TextView etat_carte;
        TextView unity;
        TextView deposit;
        RelativeLayout view_foreground;

    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        modelUsersLists.clear();
        if(charText.length()==0){
            modelUsersLists.addAll(arrayList);
        }else {
            for (AllParticuliersList particuliersList : arrayList){

                if(particuliersList.getLastname().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getCni().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getGender().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getPhone().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getAddress().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getState().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getRole().getName().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getRole().getType().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getCompte().getAccount_number().toLowerCase(Locale.getDefault()).contains(charText) ||
                        particuliersList.getUser().getCompte().getCompany().toLowerCase(Locale.getDefault()).contains(charText)){

                    modelUsersLists.add(particuliersList);
                }



            }
        }
        notifyDataSetChanged();
    }
}
