package com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteBD extends AppCompatActivity {

    private static final String TAG = "DeleteBD";
    private ListView listView;
    private ProgressBar progressBar;
    private EditText search;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<Response_Status> cardCall;

    private Response_Status myResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_bd);


        getSupportActionBar().setTitle(getString(R.string.suppression));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.listViewContent);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        search = (EditText)findViewById(R.id.search);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(DeleteBD.this, Login.class));
            finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);


        showAllCards();
    }


    private void showAllCards() {
        cardCall = service.findAllCards();
        cardCall.enqueue(new Callback<Response_Status>() {
            @Override
            public void onResponse(Call<Response_Status> call, Response<Response_Status> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    myResponse = response.body();
                    if(myResponse.isSuccess()){
                        //Toast.makeText(ListAllCardSaved.this, myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        List<Model_Card> myCardModel = myResponse.getData();
                        listView.setAdapter(new AdapterCardList(getApplicationContext(), myCardModel));
                    } else{
                        Toast.makeText(DeleteBD.this, myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(DeleteBD.this, Login.class));
                    finish();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Response_Status> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cardCall != null){
            cardCall.cancel();
            cardCall = null;
        }
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