package com.ezpass.smopaye_mobile.Manage_Subscriptions.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Profil_user.Abonnement;
import com.ezpass.smopaye_mobile.R;

import java.util.List;

public class AdapterUserSubscriptionList extends ArrayAdapter<Abonnement> {

    private Context context;
    private List<Abonnement> myAllUserCard;

    public AdapterUserSubscriptionList(Context context, List<Abonnement> myAllUserSubscription){
        super(context, R.layout.list_all_user_cards, myAllUserSubscription);
        this.context = context;
        this.myAllUserCard = myAllUserSubscription;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.list_all_subscriptions_users, parent, false);


        TextView txtV_subscription_create= (TextView) convertView.findViewById(R.id.subscription_create);
        TextView txtV_subscription_expiration= (TextView) convertView.findViewById(R.id.subscription_expiration);
        TextView txtV_subscription_type= (TextView) convertView.findViewById(R.id.txtV_subscription_type);
        TextView txtV_montant_subscription= (TextView) convertView.findViewById(R.id.txtV_montant_subscription);
        TextView cardState= (TextView) convertView.findViewById(R.id.cardState);


        for(int i=0; i<myAllUserCard.size();i++) {

            //telephone du détenteur de carte
            txtV_subscription_create.setText(myAllUserCard.get(i).getStarting_date());

            txtV_subscription_expiration.setText(myAllUserCard.get(i).getEnd_date());

            txtV_subscription_type.setText(myAllUserCard.get(i).getSubscription_type());

            txtV_montant_subscription.setText(myAllUserCard.get(i).getSubscriptionCharge() + " FCFA");

            cardState.setText(myAllUserCard.get(i).getValidate());
        }


        //clique sur un élément de la listView
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, card.get(0).getId(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return convertView;
    }
}
