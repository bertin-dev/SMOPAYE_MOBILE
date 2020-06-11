package com.ezpass.smopaye_mobile.Manage_Subscriptions.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Profil_user.Abonnement;
import com.ezpass.smopaye_mobile.R;

import java.util.List;

public class AdapterUserSubscriptionList extends ArrayAdapter<Abonnement> {

    private Context context;
    private List<Abonnement> myAllUserCard;
    private static final String TAG = "AdapterUserSubscription";

    public AdapterUserSubscriptionList(Context context, List<Abonnement> myAllUserCard){
        super(context, R.layout.list_all_subscriptions_users, myAllUserCard);
        this.context = context;
        this.myAllUserCard = myAllUserCard;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.list_all_subscriptions_users, parent, false);


        TextView txtV_subscription_create= (TextView) convertView.findViewById(R.id.subscription_create);
        txtV_subscription_create.setText(myAllUserCard.get(position).getStarting_date());

        TextView txtV_subscription_expiration= (TextView) convertView.findViewById(R.id.subscription_expiration);
        txtV_subscription_expiration.setText(myAllUserCard.get(position).getEnd_date());

        TextView txtV_subscription_type= (TextView) convertView.findViewById(R.id.txtV_subscription_type);
        txtV_subscription_type.setText(myAllUserCard.get(position).getSubscription_type());

        TextView txtV_montant_subscription= (TextView) convertView.findViewById(R.id.txtV_montant_subscription);
        txtV_montant_subscription.setText(myAllUserCard.get(position).getSubscriptionCharge() + " FCFA");

        TextView cardState= (TextView) convertView.findViewById(R.id.cardState);
        cardState.setText(myAllUserCard.get(position).getValidate());


        /*for(int i=0; i<myAllUserCard.size();i++) {
            Log.w(TAG, "getView-1: " +  myAllUserCard.get(i).getStarting_date());
            Log.w(TAG, "GETVIEW-2: " +  myAllUserCard.get(i).getEnd_date());

            //telephone du détenteur de carte
            TextView txtV_subscription_create= (TextView) convertView.findViewById(R.id.subscription_create);
            txtV_subscription_create.setText(myAllUserCard.get(i).getStarting_date());

            TextView txtV_subscription_expiration= (TextView) convertView.findViewById(R.id.subscription_expiration);
            txtV_subscription_expiration.setText(myAllUserCard.get(i).getEnd_date());

            TextView txtV_subscription_type= (TextView) convertView.findViewById(R.id.txtV_subscription_type);
            txtV_subscription_type.setText(myAllUserCard.get(i).getSubscription_type());

            TextView txtV_montant_subscription= (TextView) convertView.findViewById(R.id.txtV_montant_subscription);
            txtV_montant_subscription.setText(myAllUserCard.get(i).getSubscriptionCharge() + " FCFA");

            TextView cardState= (TextView) convertView.findViewById(R.id.cardState);
            cardState.setText(myAllUserCard.get(i).getValidate());
        }*/


        //clique sur un élément de la listView
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, myAllUserCard.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return convertView;
    }
}
