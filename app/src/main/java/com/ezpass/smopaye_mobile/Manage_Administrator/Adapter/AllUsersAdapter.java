package com.ezpass.smopaye_mobile.Manage_Administrator.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response.AllParticuliersList;
import com.ezpass.smopaye_mobile.NotifFragmentModel.Item;
import com.ezpass.smopaye_mobile.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.MyViewHolder> implements Filterable {

    private static final String TAG = "AllUsersAdapter";
    private Context context;
    private List<AllParticuliersList> modelUsersLists;
    private List<AllParticuliersList> listFilter;
    private DecimalFormat df = new DecimalFormat("0.00");


    public AllUsersAdapter(Context context, List<AllParticuliersList> modelUsersLists, List<AllParticuliersList> listFilter) {
        this.context = context;
        this.modelUsersLists = modelUsersLists;
        this.listFilter = listFilter;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String key = constraint.toString();
                if(key.isEmpty()){
                    listFilter = modelUsersLists;
                } else {
                    ArrayList<AllParticuliersList> firstFilter = new ArrayList<>();
                    for(AllParticuliersList row : modelUsersLists){


                        if(row.getLastname().toLowerCase().contains(key.toLowerCase()) ||
                                row.getCni().toLowerCase().contains(key.toLowerCase()) ||
                                row.getGender().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getPhone().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getAddress().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getState().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getRole().getName().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getRole().getType().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getCompte().getAccount_number().toLowerCase().contains(key.toLowerCase()) ||
                                row.getUser().getCompte().getCompany().toLowerCase().contains(key.toLowerCase())){

                            firstFilter.add(row);
                        }


                        /*if(row.getUser().getPhone().toLowerCase().contains(key.toLowerCase()) || row.getLastname().toLowerCase().contains(key.toLowerCase())){
                            firstFilter.add(row);
                        }*/
                    }
                    listFilter = firstFilter;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                listFilter = (ArrayList<AllParticuliersList>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_smopaye_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


            AllParticuliersList usersList = listFilter.get(position);
            //creation d'une animation
            holder.profil.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
            holder.view_foreground.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

            Log.w(TAG, "AllUsersAdapter//////////////////////////////: " + position);
            Log.w(TAG, "onBindViewHolder: ************************" + usersList.getUser().getRole().getName().trim().toLowerCase());
            //chargement des données en fonction du rôle des utilisateurs

                holder.profil.setImageResource(R.drawable.ic_account);
                holder.nom.setText(usersList.getLastname().toUpperCase());
                holder.prenom.setText(usersList.getFirstname());
                holder.cni.setText(context.getString(R.string.cni1) + ": " + usersList.getCni());
                holder.genre.setText(context.getString(R.string.genre) + ": " + usersList.getGender());
                holder.telephone.setText(context.getString(R.string.telephone) + ": " + usersList.getUser().getPhone());
                holder.adresse.setText(context.getString(R.string.adresse) + ": " + usersList.getUser().getAddress());

                if(usersList.getUser().getState().toLowerCase().equalsIgnoreCase("activer"))
                    holder.etat_user.setText(context.getString(R.string.etat) + " Actif");
                else
                    holder.etat_user.setText(context.getString(R.string.etat) + " Inactif");

                holder.date_creation.setText(context.getString(R.string.create) + "" + usersList.getCreated_at().substring(0,10));

                //Role
                holder.role.setText(context.getString(R.string.role) + " :" + usersList.getUser().getRole().getName());
                holder.type_role.setText(context.getString(R.string.typeRole) + " :" +usersList.getUser().getRole().getType());

                //Compte
                holder.numCompte.setText(context.getString(R.string.numCompte) + " :" +usersList.getUser().getCompte().getAccount_number());
                holder.company.setText(context.getString(R.string.compagnie) + " :" +usersList.getUser().getCompte().getCompany());

                if(usersList.getUser().getCompte().getAccount_state().toLowerCase().equalsIgnoreCase("activer"))
                    holder.etat_compte.setText(context.getString(R.string.etatCompte) + ": Activé");
                else
                    holder.etat_compte.setText(context.getString(R.string.etatCompte) + ": Désactivé");

                holder.montant_compte.setText(context.getString(R.string.montant) + " :" + df.format(Float.parseFloat(usersList.getUser().getCompte().getAmount())) + " Fcfa");

                //Carte
                for(int i =0; i<usersList.getUser().getCards().size(); i++){
                    holder.num_carte.setText(context.getString(R.string.numCarte) + " :" + usersList.getUser().getCards().get(i).getCode_number());
                    holder.num_serie.setText(context.getString(R.string.serial) + " :" + usersList.getUser().getCards().get(i).getSerial_number());
                    holder.type_carte.setText(context.getString(R.string.typeCarte) + " :" + usersList.getUser().getCards().get(i).getType());

                    if(usersList.getUser().getCards().get(i).getCard_state().toLowerCase().equalsIgnoreCase("activer"))
                        holder.etat_carte.setText(context.getString(R.string.etatCarte) + ": Activé");
                    else
                        holder.etat_carte.setText(context.getString(R.string.etatCarte) + ": Désactivé");

                    holder.unity.setText(context.getString(R.string.unite) + " :" + df.format(Float.parseFloat(usersList.getUser().getCards().get(i).getUnity())) + " Fcfa");
                    holder.deposit.setText(context.getString(R.string.depot) + " :" + df.format(Float.parseFloat(usersList.getUser().getCards().get(i).getDeposit())) + " Fcfa");
                }






        //Show or collapse
        //boolean isExpandable = models.get(position).isExpandable();
        //holder.view_foreground.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView profil;
        public TextView nom;
        public TextView prenom;
        public TextView cni;
        public TextView genre;
        public TextView telephone;
        public TextView adresse;
        public TextView etat_user;
        public TextView date_creation;
        public TextView role;
        public TextView type_role;
        public TextView numCompte;
        public TextView company;
        public TextView etat_compte;
        public TextView montant_compte;
        public TextView num_carte;
        public TextView num_serie;
        public TextView type_carte;
        public TextView etat_carte;
        public TextView unity;
        public TextView deposit;
        public RelativeLayout view_foreground;
        public ImageButton expandArrow;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profil = itemView.findViewById(R.id.profil);
            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            cni = itemView.findViewById(R.id.cni);
            genre = itemView.findViewById(R.id.genre);
            telephone = itemView.findViewById(R.id.telephone);
            adresse = itemView.findViewById(R.id.adresse);
            etat_user = itemView.findViewById(R.id.etat_user);
            date_creation = itemView.findViewById(R.id.date_creation);
            role = itemView.findViewById(R.id.role);
            type_role = itemView.findViewById(R.id.type_role);
            numCompte = itemView.findViewById(R.id.numCompte);
            company = itemView.findViewById(R.id.company);
            etat_compte = itemView.findViewById(R.id.etat_compte);
            montant_compte = itemView.findViewById(R.id.montant_compte);
            num_carte = itemView.findViewById(R.id.num_carte);
            num_serie = itemView.findViewById(R.id.num_serie);
            type_carte = itemView.findViewById(R.id.type_carte);
            etat_carte = itemView.findViewById(R.id.etat_carte);
            unity = itemView.findViewById(R.id.unity);
            deposit = itemView.findViewById(R.id.deposit);
            view_foreground = itemView.findViewById(R.id.view_foreground);
            expandArrow = itemView.findViewById(R.id.expandArrow);

            /*expandArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModelUsersList list = models.get(getAdapterPosition());
                    list.setExpandable(!list.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });*/
        }
    }
}
