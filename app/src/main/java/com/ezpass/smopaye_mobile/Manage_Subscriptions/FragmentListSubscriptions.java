package com.ezpass.smopaye_mobile.Manage_Subscriptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Manage_Subscriptions.Adapter.AdapterUserSubscriptionList;
import com.ezpass.smopaye_mobile.Profil_user.Abonnement;
import com.ezpass.smopaye_mobile.Profil_user.Compte;
import com.ezpass.smopaye_mobile.Profil_user.DataUser;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentListSubscriptions extends Fragment {

    private static final String TAG = "FragmentListSubscriptio";
    private ListView listView;
    private ProgressBar progressBar;
    private DataUser myResponse;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<DataUser> cardCall;
    private String myPhone;
    private LinearLayout historiqueVide;

    public FragmentListSubscriptions() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        showAllUserSubscriptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_subscriptions, container, false);

        myPhone = getArguments().getString("myPhone", "");

        listView = (ListView) view.findViewById(R.id.listViewContent);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        historiqueVide = (LinearLayout)view.findViewById(R.id.historiqueVide);


        return view;
    }


    private void showAllUserSubscriptions() {

        cardCall = service.profil(myPhone);
        cardCall.enqueue(new Callback<DataUser>() {
            @Override
            public void onResponse(Call<DataUser> call, Response<DataUser> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    myResponse = response.body();

                    Compte compte = myResponse.getCompte();
                    List<Abonnement> abonnement = compte.getCompte_subscriptions();

                        //Toast.makeText(ListAllCardSaved.this, myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        List<Abonnement> allUserSubscription = abonnement;

                        if(allUserSubscription.isEmpty()){
                            historiqueVide.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            historiqueVide.setVisibility(View.GONE);
                            listView.setAdapter(new AdapterUserSubscriptionList(getContext(), allUserSubscription));
                        }

                    }
                else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(getActivity(), Login.class));
                    getActivity().finish();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DataUser> call, Throwable t) {
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

    public Fragment newInstance(String myPhone) {
        FragmentListSubscriptions myFragment = new FragmentListSubscriptions();
        Bundle args = new Bundle();
        args.putString("myPhone", myPhone);
        myFragment.setArguments(args);
        return myFragment;
    }
}
