package com.ezpass.smopaye_mobile.Manage_Administrator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Manage_Administrator.Adapter.AllAgentAdapterListView;
import com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response.AllParticuliersList;
import com.ezpass.smopaye_mobile.Manage_Administrator.WebService_Response.HomeUsersList;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionAgents extends AppCompatActivity {

    private static final String TAG = "GestionUtilisateursAgen";
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
    private ListView listView;
    private HomeUsersList responseUser;
    private List<AllParticuliersList> usersLists;
    private AllAgentAdapterListView adapter;
    private ProgressBar progressBar;

    private ApiService service;
    private TokenManager tokenManager;
    private LinearLayout emptyListUsers;
    private Call<HomeUsersList> call;
    private int nbrTotalAgents;
    private int nbrTotalAgentsActif;
    private int nbrTotalAgentsInactif;
    private HomeUsersList responseTotalAgents;
    private List<AllParticuliersList> usersTotalAgents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_gestion_agents);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.gesUserAgent));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        totalAccepteur = (Button) findViewById(R.id.totalAccepteur);
        totalAccepteurActifs = (Button) findViewById(R.id.totalAccepteurActifs);
        totalAccepteurInactifs = (Button) findViewById(R.id.totalAccepteurInactifs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) findViewById(R.id.list_view);
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
                showCompteurTotalAgents();
                showAllSmopayeAgents();

                if(!usersLists.isEmpty()){
                    adapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        showCompteurTotalAgents();
        showAllSmopayeAgents();

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

                if(TextUtils.isEmpty(query)){
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(TextUtils.isEmpty(query)){
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(query);
                }
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


    private void showAllSmopayeAgents() {

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
                            listView.setVisibility(View.GONE);
                        } else{

                            for(int i=0; i<usersLists.size();i++){
                                if(usersLists.get(i).getUser().getRole().getName().toLowerCase().trim().equalsIgnoreCase("agent")){
                                    result.add(usersLists.get(i));
                                    Log.w(TAG, "LISTE AGENTS: " + usersLists.get(i));
                                }
                            }

                            emptyListUsers.setVisibility(View.GONE);
                            adapter = new AllAgentAdapterListView(GestionAgents.this, result);
                            listView.setAdapter(adapter);
                        }
                    } else{
                        Toast.makeText(GestionAgents.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(GestionAgents.this, Login.class));
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



    private void showCompteurTotalAgents() {
        call = service.findAllSmopayeUsers();
        call.enqueue(new Callback<HomeUsersList>() {
            @Override
            public void onResponse(Call<HomeUsersList> call, Response<HomeUsersList> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    responseTotalAgents = response.body();
                    if(responseTotalAgents.isSuccess()){

                        usersTotalAgents = responseTotalAgents.getData();

                        nbrTotalAgents = 0;
                        nbrTotalAgentsActif = 0;
                        nbrTotalAgentsInactif = 0;

                        if(usersTotalAgents.isEmpty()){
                            Toast.makeText(GestionAgents.this, "Liste des Agents Vide", Toast.LENGTH_SHORT).show();
                        } else{

                            for(int i = 0; i<usersTotalAgents.size(); i++){

                                if(usersTotalAgents.get(i).getUser().getRole().getName().toLowerCase().equalsIgnoreCase("agent")){
                                    nbrTotalAgents++;
                                    if(usersTotalAgents.get(i).getUser().getState().toLowerCase().equalsIgnoreCase("activer")){
                                        nbrTotalAgentsActif++;
                                    }else {
                                        nbrTotalAgentsInactif++;
                                    }
                                }
                            }

                            totalAccepteur.setText(String.valueOf(nbrTotalAgents));
                            totalAccepteurActifs.setText(String.valueOf(nbrTotalAgentsActif));
                            totalAccepteurInactifs.setText(String.valueOf(nbrTotalAgentsInactif));

                        }
                    } else{
                        Toast.makeText(GestionAgents.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(GestionAgents.this, Login.class));
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
