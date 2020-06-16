package com.ezpass.smopaye_mobile.Manage_Assistance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.R;

import java.util.ArrayList;

public class AgenceSmopayeAdapter extends BaseAdapter {

    ArrayList<Object> list;
    private static final int AGENCE_SMOPAYE_MODEL = 0;
    private static final int HEADER = 1;
    private LayoutInflater inflater;


    public AgenceSmopayeAdapter(Context context, ArrayList<Object> list){
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position) instanceof AgenceSmopayeModel)
            return AGENCE_SMOPAYE_MODEL;
        else
            return HEADER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            switch (getItemViewType(position)){
                case AGENCE_SMOPAYE_MODEL:
                    view = inflater.inflate(R.layout.listitem, null);
                    break;

                case HEADER:
                    view = inflater.inflate(R.layout.list_group, null);
                    break;
            }
        }

        switch (getItemViewType(position)){
            case AGENCE_SMOPAYE_MODEL:
                TextView ville = (TextView) view.findViewById(R.id.lv_list_item);
                TextView quartier = (TextView) view.findViewById(R.id.lv_list_item_sub);
                TextView current_distance = (TextView) view.findViewById(R.id.current_distance);

                ville.setText(((AgenceSmopayeModel)list.get(position)).getVille());
                quartier.setText(((AgenceSmopayeModel)list.get(position)).getQuartier());
                current_distance.setText(((AgenceSmopayeModel)list.get(position)).getDistance());
                break;

            case HEADER:
                TextView title = (TextView) view.findViewById(R.id.lv_list_group);
                title.setText(((String)list.get(position)));
                break;
        }

        return view;
    }
}
