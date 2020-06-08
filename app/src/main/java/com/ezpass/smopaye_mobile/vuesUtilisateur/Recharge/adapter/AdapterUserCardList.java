package com.ezpass.smopaye_mobile.vuesUtilisateur.Recharge.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.DataAllUserCard;

import java.util.List;

public class AdapterUserCardList extends ArrayAdapter<DataAllUserCard> {

    private Context context;
    private List<DataAllUserCard> myAllUserCard;

    public AdapterUserCardList(Context context, List<DataAllUserCard> myAllUserCard){
        super(context, R.layout.list_all_user_cards, myAllUserCard);
        this.context = context;
        this.myAllUserCard = myAllUserCard;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.list_all_user_cards, parent, false);


        //telephone du détenteur de carte
        TextView txtV_cardType = (TextView) convertView.findViewById(R.id.telUserCard);
        txtV_cardType.setText(myAllUserCard.get(0).getPhone());

        //info sur la carte
        List<DataUserCard> card = myAllUserCard.get(position).getCards();

        for(int i=0; i<card.size();i++) {
            //date de creation de la carte
            TextView txtV_create_at = (TextView) convertView.findViewById(R.id.created_at);
            txtV_create_at.setText(card.get(i).getCreated_at());
            //numéro de carte
            TextView txtV_codeNumber = (TextView) convertView.findViewById(R.id.code_number);
            txtV_codeNumber.setText(card.get(i).getCode_number());
            //numéro de série
            TextView txtV_serialNumber = (TextView) convertView.findViewById(R.id.serial_number);
            txtV_serialNumber.setText(card.get(i).getSerial_number());
            //etat de la carte
            TextView txtV_cardState = (TextView) convertView.findViewById(R.id.cardState);
            txtV_cardState.setText(card.get(i).getCard_state());
            //nom et prenom
            //TextView txtV_exp_at = (TextView) convertView.findViewById(R.id.username);
            //txtV_exp_at.setText(card.getEnd_date());
        }

        CheckBox chk_State = (CheckBox) convertView.findViewById(R.id.cardSelected);


        //clique sur un élément de la listView
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, card.get(0).getId(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
