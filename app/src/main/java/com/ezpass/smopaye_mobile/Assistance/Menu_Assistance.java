package com.ezpass.smopaye_mobile.Assistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.ServicesIndisponible;

public class Menu_Assistance extends AppCompatActivity {

    private Button assistanceEnLigne, contactezNous, boutiqueSmopaye;
    private String retourBD, telephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_assistance);

        getSupportActionBar().setTitle(getString(R.string.assistance));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        retourBD = intent.getStringExtra("resultatBD");
        telephone = intent.getStringExtra("telephone");


        assistanceEnLigne = (Button) findViewById(R.id.btn_assistanceEnLigne);
        contactezNous = (Button) findViewById(R.id.btn_contactezNous);
        boutiqueSmopaye = (Button) findViewById(R.id.btn_boutiqueSmopaye);

        assistanceEnLigne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu_Assistance.this, HomeAssistanceOnline.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("resultatBD", retourBD);
                //intent.putExtra("telephone", telephone);
                startActivity(intent);
                //finish();
               // Toast.makeText(Menu_Assistance.this, "Service Encours de Finalisation", Toast.LENGTH_LONG).show();

                /*Intent intent = new Intent(Menu_Assistance.this, ServicesIndisponible.class);
                startActivity(intent);*/
                   }
        });

        contactezNous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ServiceClientSmopaye.class));
            }
        });


        boutiqueSmopaye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BoutiqueSmopaye.class));
            }
        });




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}
