package com.ezpass.smopaye_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;

import java.io.FileInputStream;

public class MenuHistoriqueTransaction extends AppCompatActivity {

    private LinearLayout historiqueRecharge, historiqueTelecollecte, historiqueDebit, historiqueTransfert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_historique_transaction);


        getSupportActionBar().setTitle(getString(R.string.historiqueTransaction));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        /*Intent intent = getIntent();
        telephone = intent.getStringExtra("telephone");

        Toast.makeText(this, telephone, Toast.LENGTH_SHORT).show();*/




        historiqueRecharge = (LinearLayout) findViewById(R.id.historiqueRecharge);
        historiqueRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "Recharge");
                startActivity(intent);
            }
        });

        historiqueTelecollecte = (LinearLayout) findViewById(R.id.historiqueTelecollecte);
        historiqueTelecollecte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "Telecollecte");
                startActivity(intent);
            }
        });

        historiqueDebit = (LinearLayout) findViewById(R.id.historiqueDebit);
        historiqueDebit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "debitcarte");
                startActivity(intent);
            }
        });

        historiqueTransfert = (LinearLayout) findViewById(R.id.historiqueTransfert);
        historiqueTransfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", "transfert");
                startActivity(intent);
            }
        });
    }


    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.apropos) {
            Intent intent = new Intent(getApplicationContext(), Apropos.class);
            startActivity(intent);
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            startActivity(intent);
        }

        if(id == R.id.modifierCompte){
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            startActivity(intent);
        }

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
