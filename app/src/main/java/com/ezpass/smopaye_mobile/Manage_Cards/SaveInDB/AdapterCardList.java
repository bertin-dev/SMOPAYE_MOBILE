package com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.R;

import java.util.List;

public class AdapterCardList extends ArrayAdapter<Model_Card> {

    private Context context;
    private List<Model_Card> myCard;

    public AdapterCardList(Context context, List<Model_Card> myCard){
        super(context, R.layout.list_all_cards, myCard);
        this.context = context;
        this.myCard = myCard;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.list_all_cards, parent, false);

        Model_Card card = myCard.get(position);
        TextView txtV_create_at = (TextView) convertView.findViewById(R.id.created_at);
        txtV_create_at.setText(card.getStarting_date());

        TextView txtV_exp_at = (TextView) convertView.findViewById(R.id.exp_date);
        txtV_exp_at.setText(card.getEnd_date());


        TextView txtV_codeNumber = (TextView) convertView.findViewById(R.id.code_number);
        txtV_codeNumber.setText(card.getCode_number());


        TextView txtV_cardType = (TextView) convertView.findViewById(R.id.cardType);
        txtV_cardType.setText(card.getType());


        TextView txtV_serialNumber = (TextView) convertView.findViewById(R.id.serial_number);
        txtV_serialNumber.setText(card.getSerial_number());

        TextView txtV_cardState = (TextView) convertView.findViewById(R.id.cardState);
        txtV_cardState.setText(card.getCard_state());

        return convertView;
    }
}
