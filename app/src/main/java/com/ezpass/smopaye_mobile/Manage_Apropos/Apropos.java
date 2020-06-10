package com.ezpass.smopaye_mobile.Manage_Apropos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Manage_Assistance.Menu_Assistance;
import com.ezpass.smopaye_mobile.BuildConfig;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;

public class Apropos extends AppCompatActivity {

    private ListView listView;
    //String [] typeAide = {"Informations légales", "Logiciels tiers", "Badge de confiance"};
    String [] typeAide = {"Informations légales"};
    private String retourBD, telephone;
    private TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apropos);


        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.AproposSmopaye));
        toolbar.setSubtitle(getString(R.string.ezpass));
        //getSupportParentActivityIntent().putExtra("resultatBD", "Administrateur");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        retourBD = intent.getStringExtra("resultatBD");
        telephone = intent.getStringExtra("telephone");

        listView=(ListView)findViewById(R.id.listAide);
        //version Name de l'application
        versionName = (TextView) findViewById(R.id.versionName);
        versionName.setText(BuildConfig.VERSION_NAME);

       /* ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeAide);
        listView.setAdapter(adapter);*/




        // Initialize an array adapter
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeAide){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position,convertView,parent);

                // Set the typeface/font for the current item
                //item.setTypeface(mTypeface);

                // Set the list view item's text color
                item.setTextColor(Color.parseColor("#000000"));

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                // return the view
                return item;
            }
        };

        // Data bind the list view with array adapter items
        listView.setAdapter(adapter);




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listView.getItemAtPosition(position).toString().equalsIgnoreCase("Informations légales")){
                    Intent intent = new Intent(Apropos.this, InformationLegales.class);
                    startActivity(intent);
                }

                /*else if (listView.getItemAtPosition(position).toString().equalsIgnoreCase("Logiciels tiers")){
                    Intent intent = new Intent(Apropos.this, LogicielsTiers.class);
                    startActivity(intent);
                }*/

                /*else if (listView.getItemAtPosition(position).toString().equalsIgnoreCase("Badge de confiance")){
                    Intent intent = new Intent(this, AideAdmin.class);
                    startActivity(intent);
                }*/


            }
        });

    }




    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help_assistance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            Intent intent = new Intent(getApplicationContext(), Menu_Assistance.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("resultatBD", retourBD);
            intent.putExtra("telephone", telephone);
            startActivity(intent);
            finish();
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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
