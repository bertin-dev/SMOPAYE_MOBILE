package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge.adapter.AdapterUserCardList;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.DataAllUserCard;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.ListAllUserCardResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCartes extends Fragment {

    private static final String TAG = "FragmentCartes";
    private ListView listView;
    private ProgressBar progressBar;
    private EditText search;
    private ListAllUserCardResponse myResponse;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<ListAllUserCardResponse> cardCall;

    public FragmentCartes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cartes, container, false);

        listView = (ListView) view.findViewById(R.id.listViewContent);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        search = (EditText)view.findViewById(R.id.search);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        showAllUserCards(14);

        return view;
    }

    private void showAllUserCards(int id) {

        cardCall = service.findAllUserCards(id);
        cardCall.enqueue(new Callback<ListAllUserCardResponse>() {
            @Override
            public void onResponse(Call<ListAllUserCardResponse> call, Response<ListAllUserCardResponse> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    myResponse = response.body();
                    if(myResponse.isSuccess()){
                        //Toast.makeText(ListAllCardSaved.this, myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        List<DataAllUserCard> allUserCard = myResponse.getData();
                        listView.setAdapter(new AdapterUserCardList(getContext(), allUserCard));
                    } else{
                        Toast.makeText(getActivity(), myResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ListAllUserCardResponse> call, Throwable t) {
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
}
