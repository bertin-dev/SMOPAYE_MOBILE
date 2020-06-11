package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.R;
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
    private ListAllUserCardResponse myResponse;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<ListAllUserCardResponse> cardCall;
    private LinearLayout listCards;
    private AdapterUserCardList1 adapterUserCardList;
    private AlertDialog.Builder build_error;

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
        listCards = (LinearLayout) view.findViewById(R.id.listCards);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        build_error = new AlertDialog.Builder(getContext());

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

                        if(allUserCard.isEmpty()){
                            listCards.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else{
                            listCards.setVisibility(View.GONE);
                            adapterUserCardList = new AdapterUserCardList1(getContext(), allUserCard);
                            listView.setAdapter(adapterUserCardList);
                        }
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


    /***************************************************************************/
    public class AdapterUserCardList1 extends ArrayAdapter<DataAllUserCard> {

        private Context context;
        private List<DataAllUserCard> myAllUserCard;
        private static final String TAG = "AdapterUserCardList";

        public AdapterUserCardList1(Context context, List<DataAllUserCard> myAllUserCard){
            super(context, R.layout.list_all_user_cards, myAllUserCard);
            this.context = context;
            this.myAllUserCard = myAllUserCard;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_all_user_cards, parent, false);


            //telephone du détenteur de carte
            TextView txtV_cardType = (TextView) convertView.findViewById(R.id.telUserCard);
            txtV_cardType.setText(myAllUserCard.get(0).getPhone());

            //info sur la carte
            List<DataUserCard> card = myAllUserCard.get(position).getCards();

            for(int i=0; i<card.size();i++) {
                //date de creation de la carte
                TextView txtV_create_at = (TextView) convertView.findViewById(R.id.created_at);
                txtV_create_at.setText(card.get(i).getCreated_at());
                //numéro de carte
                TextView txtV_codeNumber = (TextView) convertView.findViewById(R.id.code_number);
                txtV_codeNumber.setText(card.get(i).getCode_number());
                //numéro de série
                TextView txtV_serialNumber = (TextView) convertView.findViewById(R.id.serial_number);
                txtV_serialNumber.setText(card.get(i).getSerial_number());
                //etat de la carte
                TextView txtV_cardState = (TextView) convertView.findViewById(R.id.cardState);
                txtV_cardState.setText(card.get(i).getCard_state());
                //nom et prenom
                //TextView txtV_exp_at = (TextView) convertView.findViewById(R.id.username);
                //txtV_exp_at.setText(card.getEnd_date());

                CheckBox chk_State = (CheckBox) convertView.findViewById(R.id.cardSelected);
                int finalI = i;
                chk_State.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            chk_State.setChecked(false);
                            Toast.makeText(context, card.get(finalI).getId(), Toast.LENGTH_SHORT).show();
                            //openDialog();

                            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                            title.setText(getString(R.string.information));
                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            statutOperation.setText()card.get(finalI).getId();
                            build_error.setNeutralButton("CANCEL", Compo)
                            build_error.setPositiveButton("OK", null);
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();

                        }
                    }
                });
            }



            //clique sur un élément de la listView
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "onClick: " +  card.get(position).getId());
            }
        });*/

            return convertView;
        }

    }

}
