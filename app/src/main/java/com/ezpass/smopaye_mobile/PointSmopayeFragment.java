package com.ezpass.smopaye_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ezpass.smopaye_mobile.Assistance.AgenceSmopayeAdapter;
import com.ezpass.smopaye_mobile.Assistance.AgenceSmopayeModel;
import com.ezpass.smopaye_mobile.Assistance.DetailsAgenceSmopaye;

import java.util.ArrayList;

public class PointSmopayeFragment extends Fragment {

    ListView listView;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_point_smopaye, container, false);

        getActivity().setTitle("Points de Vente Smopaye");


        listView = view.findViewById(R.id.exp_list_view);

        ArrayList<Object> list = new ArrayList<>();
        list.add(new String("CENTRE"));
        list.add(new AgenceSmopayeModel("YAOUNDE", "CAMAIR"));
        list.add(new AgenceSmopayeModel("YAOUNDE", "OMNISPORT"));
        list.add(new AgenceSmopayeModel("YAOUNDE", "SOA"));

      /*  list.add(new String("LITTORAL"));
        list.add(new AgenceSmopayeModel("DOUALA", "Akwa"));
        list.add(new AgenceSmopayeModel("DOUALA", "Bonanjo"));
        list.add(new AgenceSmopayeModel("DOUALA", "Makepe"));
        list.add(new AgenceSmopayeModel("DOUALA", "Ndokoti"));*/

        listView.setAdapter(new AgenceSmopayeAdapter(getActivity().getApplicationContext(), list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               /* if (listView1.getItemAtPosition(position).toString().equalsIgnoreCase("YAOUNDE")){
                    Intent intent = new Intent(getApplicationContext(), DetailsAgenceSmopaye.class);
                    startActivity(intent);
                }*/

                if (position == 1) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 3) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 4) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 6) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 7) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 8) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 9) {
                    Intent myIntent = new Intent(view.getContext(), DetailsAgenceSmopaye.class);
                    startActivityForResult(myIntent, 0);
                }


            }
        });

        return view;
    }



}
