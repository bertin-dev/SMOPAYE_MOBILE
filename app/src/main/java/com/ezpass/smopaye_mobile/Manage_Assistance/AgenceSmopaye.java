package com.ezpass.smopaye_mobile.Manage_Assistance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ezpass.smopaye_mobile.Manage_HomePage.ExpandableListAdapter;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AgenceSmopaye extends AppCompatActivity {

    ExpandableListAdapter listAdapter1;
    ListView listView1;
    List<String> listDataHeader1;
    HashMap<String, List<String>> listHashMap1;
    String[] quartier = new String[] {"Essos", "Essos", "Essos", "Essos",
                                      "Essos", "Essos", "Essos", "Essos",
                                      "Essos", "Essos", "Essos", "Essos",
                                      "Essos", "Essos", "Essos", "Essos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agence_smopaye);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.pointDeVenteSmopaye));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listView1 = (ListView) findViewById(R.id.exp_list_view1);
        /*intializedData1();
        listAdapter1 = new ExpandableListAdapter(this, listDataHeader1, listHashMap1, quartier);
        listView1.setAdapter(listAdapter1);*/


        ArrayList<Object> list = new ArrayList<>();
        list.add(new String("CENTRE"));
        list.add(new AgenceSmopayeModel(getString(R.string.yaounde), getString(R.string.camair)));
        list.add(new AgenceSmopayeModel(getString(R.string.yaounde), getString(R.string.omnisport)));
        list.add(new AgenceSmopayeModel(getString(R.string.yaounde), getString(R.string.soa)));
       // list.add(new AgenceSmopayeModel("YAOUNDE", "Bastos"));

        /*list.add(new String("LITTORAL"));
        list.add(new AgenceSmopayeModel("DOUALA", "Akwa"));
        list.add(new AgenceSmopayeModel("DOUALA", "Bonanjo"));
        list.add(new AgenceSmopayeModel("DOUALA", "Makepe"));
        list.add(new AgenceSmopayeModel("DOUALA", "Ndokoti"));*/

        listView1.setAdapter(new AgenceSmopayeAdapter(this, list));

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }


    private void intializedData1() {

        //First Add Header List
        listDataHeader1 = new ArrayList<>();
        listHashMap1 = new HashMap<>();

        listDataHeader1.add("YAOUNDE");
        listDataHeader1.add("DOUALA");
        listDataHeader1.add("BAFFOUSSAM");
        listDataHeader1.add("NGAOUNDERE");

        //After that add childs list
        List<String> mobileDev = new ArrayList<>();
        mobileDev.add("Avenue Kennedy");
        mobileDev.add("Biyem-assi");
        mobileDev.add("Essos");
        mobileDev.add("Bastos");


        List<String> webDev = new ArrayList<>();
        webDev.add("Akwa");
        webDev.add("Bonanjo");
        webDev.add("Makepe");
        webDev.add("Ndokoti");

        List<String> deskDev = new ArrayList<>();
        deskDev.add("Babadjou");
        deskDev.add("Babete");
        deskDev.add("Bana");

        List<String> databaseDev = new ArrayList<>();
        databaseDev.add("Tongo Texaco");
        databaseDev.add("Baladji");
        databaseDev.add("");
        databaseDev.add("");

        listHashMap1.put(listDataHeader1.get(0), mobileDev);
        listHashMap1.put(listDataHeader1.get(1), webDev);
        listHashMap1.put(listDataHeader1.get(2), deskDev);
        listHashMap1.put(listDataHeader1.get(3), databaseDev);
    }

    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     * @param newBase
     * @since 2020
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
