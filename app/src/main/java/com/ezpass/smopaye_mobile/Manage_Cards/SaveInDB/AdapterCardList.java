package com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ezpass.smopaye_mobile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterCardList extends BaseAdapter {

    private Context context;
    private List<Model_Card> myCard;
    private LayoutInflater inflater;
    ArrayList<Model_Card> arrayList;

    public AdapterCardList(Context context, List<Model_Card> myCard){
        this.context = context;
        this.myCard = myCard;
        inflater = LayoutInflater.from(context);
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(myCard);
    }


    @Override
    public int getCount() {
        return myCard.size();
    }

    @Override
    public Object getItem(int position) {
        return myCard.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        MyViewHolder myViewHolder;
        if(convertView==null) {
            myViewHolder = new MyViewHolder();
            convertView = inflater.inflate(R.layout.list_all_cards, null);

            myViewHolder.txtV_create_at = convertView.findViewById(R.id.created_at);
            myViewHolder.txtV_exp_at = convertView.findViewById(R.id.exp_date);
            myViewHolder.txtV_codeNumber = convertView.findViewById(R.id.code_number);
            myViewHolder.txtV_cardType = convertView.findViewById(R.id.cardType);
            myViewHolder.txtV_serialNumber = convertView.findViewById(R.id.serial_number);
            myViewHolder.txtV_cardState = convertView.findViewById(R.id.cardState);
            convertView.setTag(myViewHolder);

        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }


        Model_Card card = myCard.get(position);

        myViewHolder.txtV_create_at.setText(context.getString(R.string.create) + " " + card.getStarting_date().substring(0,10));
        myViewHolder.txtV_exp_at.setText(context.getString(R.string.expire) + " " +card.getEnd_date().substring(0,10));
        myViewHolder.txtV_codeNumber.setText(card.getCode_number());
        myViewHolder.txtV_cardType.setText(card.getType());
        myViewHolder.txtV_serialNumber.setText(card.getSerial_number());
        myViewHolder.txtV_cardState.setText(context.getString(R.string.etat) + " " + card.getCard_state());


        //clique sur un élément de la listView
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start Activity Card Form
                Intent intent = new Intent(context, UpdateBD.class);
                intent.putExtra("card_id", card.getId());
                intent.putExtra("card_number", card.getCode_number());
                intent.putExtra("serial_number", card.getSerial_number());
                intent.putExtra("exp_date", card.getEnd_date());
                context.startActivity(intent);

            }
        });

        return convertView;
    }


    public class MyViewHolder {
        TextView txtV_create_at;
        TextView txtV_exp_at;
        TextView txtV_codeNumber;
        TextView txtV_cardType;
        TextView txtV_serialNumber;
        TextView txtV_cardState;
    }


    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        myCard.clear();
        if(charText.length()==0){
            myCard.addAll(arrayList);
        }else {
            for (Model_Card modelCard : arrayList){

                if(modelCard.getCode_number().toLowerCase(Locale.getDefault()).contains(charText) ||
                        modelCard.getSerial_number().toLowerCase(Locale.getDefault()).contains(charText) ||
                        modelCard.getCard_state().toLowerCase(Locale.getDefault()).contains(charText) ||
                        modelCard.getType().toLowerCase(Locale.getDefault()).contains(charText) ||
                        modelCard.getCompany().toLowerCase(Locale.getDefault()).contains(charText) ||
                        modelCard.getCreated_at().toLowerCase(Locale.getDefault()).contains(charText)){

                    myCard.add(modelCard);
                }



            }
        }
        notifyDataSetChanged();
    }
}
