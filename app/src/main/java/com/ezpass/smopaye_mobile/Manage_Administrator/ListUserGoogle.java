package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteAdapter.UserAdapter;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListUserGoogle extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_list_user_google);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.userGoogle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_utilisateurGoogle);

        new GoogleUtilisateur().readUser(new GoogleUtilisateur.DataStatus() {
            @Override
            public void DataIsLoaded(List<User> users, List<String> keys) {
                findViewById(R.id.pd_listUserGoogle).setVisibility(View.GONE);
                new RecyclerAdapter_Google().setConfig(mRecyclerView, ListUserGoogle.this, users, keys);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }


    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);


        //MenuItem item = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) item.getActionView();
        //search listener
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.searchByName));

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUsers2(query);
                }else {
                    getAllUsers();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(!TextUtils.isEmpty(query.trim())){
                    searchUsers2(query);
                }else {
                    getAllUsers();
                }

                return true;
            }
        });

        return true;
    }

    private void getAllUsers() {

        //get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> keys = new ArrayList<>();
                List<User> userList = new ArrayList<>();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User modelUser = ds.getValue(User.class);
                    //get all users except currently signed in user

                    Log.w("Dadi", "AAA" + fUser.getUid() + "=" +  "AAA" + modelUser.getId());
                   /* if(!fUser.getUid().equals(modelUser.getId())){
                        keys.add(ds.getKey());
                        userList.add(modelUser);

                    }*/

                    keys.add(ds.getKey());
                    userList.add(modelUser);
                    //Log.w("PIPO", modelUser + "++++++++++++++++++++++++: " + ds.getKey() );

                    new RecyclerAdapter_Google().setConfig(mRecyclerView, ListUserGoogle.this, userList, keys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers2(String s) {
        List<String> keys = new ArrayList<>();
        List<User> userList = new ArrayList<>();

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert fuser != null;
                   /* if(!fuser.getUid().equals(user.getId())){
                        userList.add(user);
                        keys.add(snapshot.getKey());
                    }*/
                    userList.add(user);
                    keys.add(snapshot.getKey());
                }
                new RecyclerAdapter_Google().setConfig2(mRecyclerView, ListUserGoogle.this, userList, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

        if(id == android.R.id.home){
            finish();
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
        } else{
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }
    }

}
