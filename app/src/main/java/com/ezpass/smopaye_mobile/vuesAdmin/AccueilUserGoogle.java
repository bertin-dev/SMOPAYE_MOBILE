package com.ezpass.smopaye_mobile.vuesAdmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccueilUserGoogle extends AppCompatActivity {

    Button totalAdmin, totalAccepteur, totalUser, totalAgent;
    LinearLayout lnListUserGoogle, lnSouscriptionGoogleUser, lnEnvoiNotifOneGoogleUser;


    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil_user_google);

        getSupportActionBar().setTitle(getString(R.string.gesUserGoogle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        totalAdmin = (Button) findViewById(R.id.totalAdmin);
        totalUser = (Button) findViewById(R.id.totalUser);
        totalAccepteur = (Button) findViewById(R.id.totalAccepteur);
        totalAgent = (Button) findViewById(R.id.totalAgent);

        lnListUserGoogle = (LinearLayout) findViewById(R.id.lnListUserGoogle);
        lnSouscriptionGoogleUser = (LinearLayout) findViewById(R.id.lnSouscriptionGoogleUser);
        lnEnvoiNotifOneGoogleUser = (LinearLayout) findViewById(R.id.lnEnvoiNotifOneGoogleUser);



        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("Users");


        mReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int utilisateur = 0, admin = 0, accepteur = 0, agent = 0;
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){

                    User user1 = keyNode.getValue(User.class);
                    if(user1.getSession().toLowerCase().equalsIgnoreCase("administrateur")){
                        admin++;
                    } else if(user1.getSession().toLowerCase().equalsIgnoreCase("utilisateur")){
                        utilisateur++;
                    } else if(user1.getSession().toLowerCase().equalsIgnoreCase("accepteur")){
                        accepteur++;
                    } else if(user1.getSession().toLowerCase().equalsIgnoreCase("agent")){
                        agent++;
                    }
                }

                totalAdmin.setText(String.valueOf(admin));
                totalUser.setText(String.valueOf(utilisateur));
                totalAccepteur.setText(String.valueOf(accepteur));
                totalAgent.setText(String.valueOf(agent));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        lnListUserGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListUserGoogle.class);
                startActivity(intent);
            }
        });

        lnSouscriptionGoogleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SouscriptionGoogleOnly.class);
                startActivity(intent);
            }
        });


        lnEnvoiNotifOneGoogleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccueilEnvoiNotifGoogleUser.class);
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

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
