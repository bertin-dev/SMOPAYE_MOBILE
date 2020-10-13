package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteModel.User;

import java.util.List;

public class RecyclerAdapter_Google {

    private Context mContext;
    private UserAdapterGoogle mUserAdapterGoogle;



    public void setConfig(RecyclerView recyclerView, Context context, List<User> user2, List<String> keys2){
        mContext = context;
        mUserAdapterGoogle = new UserAdapterGoogle(user2, keys2);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //Collections.reverse(user2);
        recyclerView.setAdapter(mUserAdapterGoogle);
    }


    public void setConfig2(RecyclerView recyclerView, Context context, List<User> user2, List<String> keys2){
        mContext = context;
        mUserAdapterGoogle = new UserAdapterGoogle(user2, keys2);
        Log.w("PIPO", user2 + "+33333333333333+: ");
        //refresh adapter
        mUserAdapterGoogle.notifyDataSetChanged();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //Collections.reverse(user2);
        recyclerView.setAdapter(mUserAdapterGoogle);
    }



    class UserItemView extends RecyclerView.ViewHolder{

        private TextView mName;
        private TextView mIdCarte;
        private TextView mNumber;
        private TextView mSession;

        private String key;


        private String prenom;
        private String sexe;
        private String cni;
        private String adresse;
        private String typeUser;
        private String abonnement;


        public UserItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.list_item_user_google, parent,false));

            mName = (TextView) itemView.findViewById(R.id.nomPrenomUser);
            mIdCarte = (TextView) itemView.findViewById(R.id.id_carteUser);
            mNumber = (TextView) itemView.findViewById(R.id.telUser);
            mSession = (TextView) itemView.findViewById(R.id.sessionUser);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ModifSuppGoogleUser.class);
                    intent.putExtra("key", key);
                    intent.putExtra("nom", mName.getText().toString());
                    intent.putExtra("id_carte", mIdCarte.getText().toString());
                    intent.putExtra("telephone", mNumber.getText().toString());
                    intent.putExtra("session", mSession.getText().toString());

                    intent.putExtra("prenom", prenom);
                    intent.putExtra("sexe", sexe);
                    intent.putExtra("cni", cni);
                    intent.putExtra("adresse", adresse);
                    intent.putExtra("typeUser", typeUser);
                    intent.putExtra("abonnement", abonnement);

                    mContext.startActivity(intent);
                }
            });

        }


        public void bind (User user, String key){
            mName.setText(user.getNom());
            mIdCarte.setText(user.getId_carte());
            mNumber.setText(user.getTel());
            mSession.setText(user.getSession());
            this.key = key;



            prenom = user.getPrenom();
            sexe = user.getSexe();
            cni = user.getCni();
            adresse = user.getAdresse();
            typeUser = user.getTypeUser();
            abonnement = user.getAbonnement();
        }

    }

    class UserAdapterGoogle extends RecyclerView.Adapter<UserItemView>{

        private List<User> mUserList;
        private List<String> mKeys;

        public UserAdapterGoogle(List<User> mUserList, List<String> mKeys) {
            this.mUserList = mUserList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public UserItemView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new UserItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserItemView holder, int i) {
            holder.bind(mUserList.get(i), mKeys.get(i));
        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }
    }
}
