package com.ezpass.smopaye_mobile.Manage_Administrator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Manage_Administrator.Adapter.AllUsersAdapter;
import com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response.AllParticuliersList;
import com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response.HomeUsersList;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.NotifFragmentHelper.Common;
import com.ezpass.smopaye_mobile.NotifFragmentRemote.IMenuRequest;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionAdministrateur extends AppCompatActivity {

    private static final String TAG = "GestionAdministrateur";
    private Button totalAccepteur, totalAccepteurActifs, totalAccepteurInactifs;
    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private Toolbar toolbar;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HomeUsersList responseUser;
    private List<AllParticuliersList> usersLists;
    private AllUsersAdapter adapter;
    private ProgressBar progressBar;

    private ApiService service;
    private TokenManager tokenManager;
    private LinearLayout emptyListUsers;
    private Call<HomeUsersList> call;
    private int nbrTotalAdministrateur;
    private int nbrTotalAdministrateurActif;
    private int nbrTotalAdministrateurInactif;
    private HomeUsersList responseTotalAdmin;
    private List<AllParticuliersList> adminTotalLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_gestion_administrateur);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.GesAdmin));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalAccepteur = (Button) findViewById(R.id.totalAccepteur);
        totalAccepteurActifs = (Button) findViewById(R.id.totalAccepteurActifs);
        totalAccepteurInactifs = (Button) findViewById(R.id.totalAccepteurInactifs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyListUsers = (LinearLayout) findViewById(R.id.emptyListUsers);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);


        //module d'actualisation
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showCompteurTotalUsers();
                showAllSmopayeUsers();

                if(!usersLists.isEmpty()){
                    adapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        showCompteurTotalUsers();
        showAllSmopayeUsers();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }
    }


    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);

        //search listener
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                adapter.getFilter().filter(query);

                return true;
            }
        });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }



    private void showAllSmopayeUsers() {

        call = service.findAllSmopayeUsers();
        call.enqueue(new Callback<HomeUsersList>() {
            @Override
            public void onResponse(Call<HomeUsersList> call, Response<HomeUsersList> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    responseUser = response.body();
                    if(responseUser.isSuccess()){
                        //Toast.makeText(ListAllCardSaved.this, myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        usersLists = responseUser.getData();
                        List<AllParticuliersList> result = new ArrayList<>();

                        if(usersLists.isEmpty()){
                            emptyListUsers.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else{

                            for(int i=0; i<usersLists.size();i++){
                                if(usersLists.get(i).getUser().getRole().getName().toLowerCase().trim().equalsIgnoreCase("administrateur")){
                                    result.add(usersLists.get(i));
                                    Log.w(TAG, "LISTE ADMINISTRATEURS: " + usersLists.get(i));
                                }
                            }

                            emptyListUsers.setVisibility(View.GONE);
                            adapter = new AllUsersAdapter(GestionAdministrateur.this, result, result);
                            recyclerView.setAdapter(adapter);
                        }
                    } else{
                        Toast.makeText(GestionAdministrateur.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(GestionAdministrateur.this, Login.class));
                    finish();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<HomeUsersList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
            }
        });
    }



    private void showCompteurTotalUsers() {
        call = service.findAllSmopayeUsers();
        call.enqueue(new Callback<HomeUsersList>() {
            @Override
            public void onResponse(Call<HomeUsersList> call, Response<HomeUsersList> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    responseTotalAdmin = response.body();
                    if(responseTotalAdmin.isSuccess()){
                        //Toast.makeText(ListAllCardSaved.this, myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        adminTotalLists = responseTotalAdmin.getData();

                        nbrTotalAdministrateur = 0;
                        nbrTotalAdministrateurActif = 0;
                        nbrTotalAdministrateurInactif = 0;

                        if(adminTotalLists.isEmpty()){
                            Toast.makeText(GestionAdministrateur.this, "Liste des administrateurs Vide", Toast.LENGTH_SHORT).show();
                        } else{

                            for(int i = 0; i<adminTotalLists.size(); i++){

                                if(adminTotalLists.get(i).getUser().getRole().getName().toLowerCase().equalsIgnoreCase("administrateur")){
                                    nbrTotalAdministrateur++;
                                    if(adminTotalLists.get(i).getUser().getState().toLowerCase().equalsIgnoreCase("activer")){
                                        nbrTotalAdministrateurActif++;
                                    }else {
                                        nbrTotalAdministrateurInactif++;
                                    }
                                }
                            }

                            totalAccepteur.setText(String.valueOf(nbrTotalAdministrateur));
                            totalAccepteurActifs.setText(String.valueOf(nbrTotalAdministrateurActif));
                            totalAccepteurInactifs.setText(String.valueOf(nbrTotalAdministrateurInactif));

                        }
                    } else{
                        Toast.makeText(GestionAdministrateur.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(GestionAdministrateur.this, Login.class));
                    finish();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<HomeUsersList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
            }
        });
    }
}