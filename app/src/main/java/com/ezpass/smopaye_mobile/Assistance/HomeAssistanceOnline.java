package com.ezpass.smopaye_mobile.Assistance;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.ChatsFragment;
import com.ezpass.smopaye_mobile.RemoteFragments.ProfileFragment;
import com.ezpass.smopaye_mobile.RemoteFragments.UsersFragment;
import com.ezpass.smopaye_mobile.RemoteModel.Chat;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAssistanceOnline extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private CircleImageView profil_image;
    private TextView username;
    private String retourBD, telephone, sessionUser;

    private String file = "tmp_number";
    private String file2 = "tmp_data_user";
    int c;
    private String temp_number = "";
    private  String temp_databD = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_assistance_online);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
       // getSupportActionBar().hide();

        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }

            FileInputStream fIn2 = openFileInput(file2);
            while ((c = fIn2.read()) != -1){
                temp_databD = temp_databD + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        telephone = temp_number;
        retourBD = temp_databD;
       /* Intent intent = getIntent();
        retourBD = intent.getStringExtra("resultatBD");
        telephone = intent.getStringExtra("telephone");*/

        String[] parts = retourBD.split("-");
        sessionUser = parts[2]; // Accepteur, Administrateur, Utilisateur, Agent


        profil_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getPrenom() + " " + user.getNom() );
                if(user.getImageURL().equals("default"))
                {
                    profil_image.setImageResource(R.drawable.ic_user_google_blank);
                } else
                {
                    //Change this
                    //Glide.with(MainActivity.this).load(user.getImageURL()).into(profil_image);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profil_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final TabLayout tabLayout = findViewById(R.id.tab_layoutChat);
        final ViewPager viewPager = findViewById(R.id.view_pagerChat);

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }
                if(unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(), getString(R.string.chats));
                }
                else{
                    viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ")" + getString(R.string.chats));
                }

                if(sessionUser.toLowerCase().trim().equalsIgnoreCase("administrateur") || sessionUser.toLowerCase().trim().equalsIgnoreCase("agent")) {
                    viewPagerAdapter.addFragment(new UsersFragment().newInstance2(sessionUser), getString(R.string.utilisateur));
                    viewPagerAdapter.addFragment(new ProfileFragment(), getString(R.string.profile));
                    viewPager.setAdapter(viewPagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                } else{
                    viewPagerAdapter.addFragment(new UsersFragment().newInstance2(sessionUser), getString(R.string.AgentCommercial));
                    viewPager.setAdapter(viewPagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                }

                /*viewPagerAdapter.addFragment(new UsersFragment(), "Utilisateurs");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }




    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

    }


    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
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
