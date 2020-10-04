package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Recharge;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB.AdapterCardList;
import com.ezpass.smopaye_mobile.Manage_Cards.SaveInDB.Model_Card;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.Profil_user.DataUser;
import com.ezpass.smopaye_mobile.Profil_user.DataUserCard;
import com.ezpass.smopaye_mobile.Profil_user.Particulier;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.DataAllUserCard;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.ListAllUserCardResponse;
import com.ezpass.smopaye_mobile.web_service_response.Recharge.MessageRechargeCardByAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressPie;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCartes extends Fragment {

    private static final String TAG = "FragmentCartes";
    private ListView listView;
    private ProgressBar progressBar;
    private ListAllUserCardResponse myResponse;
    private ACProgressPie dialog2;

    private ApiService service;
    private TokenManager tokenManager;
    private Call<ListAllUserCardResponse> cardCall;
    private LinearLayout listCards;
    private AdapterUserCardList adapterUserCardList;
    private AlertDialog.Builder build_error;
    private Call<MessageRechargeCardByAccount> call;

    //SERVICES GOOGLE FIREBASE
    private APIService apiService;
    private FirebaseUser fuser;

    private String file2 = "tmp_account";
    private String temp_account = "";
    private int c;
    private String idUser;
    private String myCardNumber;


    //BD LOCALE
    private DbHandler dbHandler;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DataAllUserCard> allUserCard;
    private ListView listViewOwnerCard;
    private Call<DataUser> CallOwnerCard;
    private List<DataUserCard> ownerCardResponse;
    private AdapterOwnerCard adapterOwnerCard;
    private LinearLayout listOwnerCard;
    private String file = "tmp_number";
    private String temp_number = "";


    public FragmentCartes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changeTheme();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cartes, container, false);

        listView = (ListView) view.findViewById(R.id.listViewContent);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        listCards = (LinearLayout) view.findViewById(R.id.listCards);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        listViewOwnerCard = (ListView) view.findViewById(R.id.listViewOwnerCard);
        listOwnerCard = (LinearLayout) view.findViewById(R.id.listOwnerCard);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

        apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        build_error = new AlertDialog.Builder(getContext());
        idUser = getArguments().getString("idUser", "");
        myCardNumber =  getArguments().getString("compte", "");
        readTempAccountInFile();
        readTempNumberInFile();

        showOwnerCard(temp_number);

        //Toast.makeText(getContext(), idUser, Toast.LENGTH_SHORT).show();
        showAllUserCards(Integer.parseInt(idUser));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget(view);
        }

        //module d'actualisation
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showAllUserCards(Integer.parseInt(idUser));

                if(!allUserCard.isEmpty()){
                    adapterUserCardList.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
                        allUserCard = myResponse.getData();

                        if(allUserCard.isEmpty()){
                            listCards.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else{
                            listCards.setVisibility(View.GONE);
                            adapterUserCardList = new AdapterUserCardList(getContext(), allUserCard);
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

    private void showOwnerCard(String telephone) {

        CallOwnerCard = service.profil(telephone);
        CallOwnerCard.enqueue(new Callback<DataUser>() {
            @Override
            public void onResponse(Call<DataUser> call, Response<DataUser> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;
                    ownerCardResponse = response.body().getCards();

                    if(ownerCardResponse.isEmpty()){
                        listOwnerCard.setVisibility(View.VISIBLE);
                        listViewOwnerCard.setVisibility(View.GONE);
                    } else{
                        listOwnerCard.setVisibility(View.GONE);
                        adapterOwnerCard = new AdapterOwnerCard(getContext(), ownerCardResponse);
                        listViewOwnerCard.setAdapter(adapterOwnerCard);
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

        if(call != null){
            call.cancel();
            call = null;
        }

        if(CallOwnerCard != null){
            CallOwnerCard.cancel();
            CallOwnerCard = null;
        }
    }

    public Fragment newInstance(String idUsers, String card_number) {
        FragmentCartes myFragment = new FragmentCartes();
        Bundle args = new Bundle();
        args.putString("idUser", idUsers);
        args.putString("compte", card_number);
        myFragment.setArguments(args);
        return myFragment;
    }


    /**************************************CLASSE EXTEND ARRAYADAPTER*************************************/
    private class AdapterUserCardList extends BaseAdapter {

        private Context context;
        private List<DataAllUserCard> myAllUserCard;
        private LayoutInflater inflater;
        private ArrayList<DataAllUserCard> arrayList;

        public AdapterUserCardList(Context context, List<DataAllUserCard> myAllUserCard){
            this.context = context;
            this.myAllUserCard = myAllUserCard;
            inflater = LayoutInflater.from(context);
            this.arrayList = new ArrayList<>();
            this.arrayList.addAll(myAllUserCard);
        }


        @Override
        public int getCount() {
            return myAllUserCard.size();
        }

        @Override
        public Object getItem(int position) {
            return myAllUserCard.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            MyViewHolder myViewHolder;
            if(convertView==null) {
                myViewHolder = new MyViewHolder();
                convertView = inflater.inflate(R.layout.list_all_user_cards, null);

                myViewHolder.txtV_telUserCard = convertView.findViewById(R.id.telUserCard);
                myViewHolder.txtV_create_at = convertView.findViewById(R.id.created_at);
                myViewHolder.txtV_codeNumber = convertView.findViewById(R.id.code_number);
                myViewHolder.txtV_serialNumber = convertView.findViewById(R.id.serial_number);
                myViewHolder.txtV_cardState = convertView.findViewById(R.id.cardState);
                myViewHolder.chk_State = convertView.findViewById(R.id.cardSelected);
                myViewHolder.txtV_username = convertView.findViewById(R.id.username);
                convertView.setTag(myViewHolder);


            } else {
                myViewHolder = (MyViewHolder) convertView.getTag();
            }



            //info sur la carte
            List<DataUserCard> card = myAllUserCard.get(position).getCards();

                //telephone du détenteur de carte
                myViewHolder.txtV_telUserCard.setText("Tel: " + myAllUserCard.get(position).getPhone());

                //date de creation de la carte
            myViewHolder.txtV_create_at.setText(getString(R.string.create) + card.get(position).getCreated_at().substring(0,10));
                //numéro de carte
            myViewHolder.txtV_codeNumber.setText("N°"+card.get(position).getCode_number());
                //numéro de série
            myViewHolder.txtV_serialNumber.setText(card.get(position).getSerial_number());
                //etat de la carte
            myViewHolder.txtV_cardState.setText(getString(R.string.etat) + card.get(position).getCard_state());
                //nom et prenom
                //TextView txtV_exp_at = (TextView) convertView.findViewById(R.id.username);
                //txtV_exp_at.setText(card.getEnd_date());
            myViewHolder.chk_State.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            myViewHolder.chk_State.setChecked(false);
                            //Toast.makeText(context, card.get(finalI).getId(), Toast.LENGTH_SHORT).show();

                            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_recharge_carte, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            statutOperation.setText(getString(R.string.rechargeCard) + card.get(position).getCode_number() + " " + getString(R.string.amountPlease));
                            EditText montant = (EditText) view.findViewById(R.id.edit_montant);
                            title.setText(getString(R.string.recharge));
                            build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(!montant.getText().toString().equalsIgnoreCase("")){
                                        rechargeYourCarte(card.get(position).getCode_number(), Float.parseFloat(montant.getText().toString()), temp_account);
                                    } else{
                                        Toast.makeText(context, getString(R.string.veuillezInsererMontant), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            build_error.setNeutralButton(getString(R.string.annuler), new DialogInterface.OnClickListener() { // define the 'Cancel' button
                                public void onClick(DialogInterface dialog, int which) {
                                    //Either of the following two lines should work.
                                    dialog.cancel();
                                    //dialog.dismiss();
                                }
                            });
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();

                        }
                    }
                });




            /**********************************************************************/
            List<Particulier> particuliers = myAllUserCard.get(position).getParticulier();
            for(int j=0; j<particuliers.size();j++){
                //nom et prenom
                myViewHolder.txtV_username.setText(particuliers.get(j).getFirstname() + " " + particuliers.get(j).getLastname());
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

        public void filter(String charText){
            charText = charText.toLowerCase(Locale.getDefault());
            myAllUserCard.clear();
            if(charText.length()==0){
                myAllUserCard.addAll(arrayList);
            }else {
                for (DataAllUserCard dataAllUserCard : arrayList){


                    for (int i = 0; i < dataAllUserCard.getCards().size();i++){

                        for(int j = 0; j < dataAllUserCard.getParticulier().size(); j++){

                            if(dataAllUserCard.getPhone().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getCards().get(i).getCode_number().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getCards().get(i).getSerial_number().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getCards().get(i).getCard_state().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getCards().get(i).getType().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getCards().get(i).getCompany().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getCreated_at().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getParticulier().get(j).getFirstname().toLowerCase(Locale.getDefault()).contains(charText) ||
                                    dataAllUserCard.getParticulier().get(j).getLastname().toLowerCase(Locale.getDefault()).contains(charText)) {

                                myAllUserCard.add(dataAllUserCard);
                            }

                        }



                    }





                }
            }
            notifyDataSetChanged();
        }



        private void rechargeYourCarte(String card_id, Float amount, String account_number){

            //********************DEBUT***********
            dialog2 = new ACProgressPie.Builder(getContext())
                    .ringColor(Color.WHITE)
                    .pieColor(Color.WHITE)
                    .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                    .build();
            dialog2.show();
            //*******************FIN*****

            call = service.rechargeCards(card_id, amount, account_number);
            call.enqueue(new Callback<MessageRechargeCardByAccount>() {
                @Override
                public void onResponse(Call<MessageRechargeCardByAccount> call, Response<MessageRechargeCardByAccount> response) {
                    Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                    dialog2.dismiss();
                    if(response.isSuccessful()){

                        assert response.body() != null;

                        String card_sender = myCardNumber;
                        String msg_sender = response.body().getMessage().getNotif();

                        String card_receiver = response.body().getMessage().getCode_number();
                        String msg_receiver = getString(R.string.notifRechargeCard) + " " + amount + " FCFA " + getString(R.string.notif2RechargeCard) + response.body().getMessage().getDeposit() + " FCFA";

                        successResponse(card_receiver, msg_receiver, card_sender, msg_sender);

                    }
                    else{
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        errorResponse(myCardNumber, apiError.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<MessageRechargeCardByAccount> call, Throwable t) {
                    dialog2.dismiss();
                    Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
                }
            });
        }



        public class MyViewHolder {
            TextView txtV_telUserCard;
            TextView txtV_create_at;
            TextView txtV_codeNumber;
            TextView txtV_serialNumber;
            TextView txtV_cardState;
            CheckBox chk_State;
            TextView txtV_username;
        }
    }

    private class AdapterOwnerCard extends ArrayAdapter<DataUserCard> {

        private Context context;
        private List<DataUserCard> myOwnerCard;

        public AdapterOwnerCard(Context context, List<DataUserCard> myOwnerCard){
            super(context, R.layout.list_all_user_cards, myOwnerCard);
            this.context = context;
            this.myOwnerCard = myOwnerCard;
        }


        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_all_user_cards, parent, false);

            //info sur la carte
            List<DataUserCard> card = myOwnerCard;
            for(int i=0; i<card.size();i++) {

                //telephone du détenteur de carte
                TextView txtV_telUserCard = (TextView) convertView.findViewById(R.id.telUserCard);
                txtV_telUserCard.setText("Tel: " + temp_number);

                //date de creation de la carte
                //TextView txtV_create_at = (TextView) convertView.findViewById(R.id.created_at);
                //txtV_create_at.setText(getString(R.string.create) + card.get(i).getCreated_at().substring(0,10));
                //numéro de carte
                TextView txtV_codeNumber = (TextView) convertView.findViewById(R.id.code_number);
                txtV_codeNumber.setText("N°"+card.get(i).getCode_number());
                //numéro de série
                TextView txtV_serialNumber = (TextView) convertView.findViewById(R.id.serial_number);
                txtV_serialNumber.setText(card.get(i).getSerial_number());
                //etat de la carte
                TextView txtV_cardState = (TextView) convertView.findViewById(R.id.cardState);
                txtV_cardState.setText(getString(R.string.etat) + card.get(i).getCard_state());
                //nom et prenom
                //TextView txtV_exp_at = (TextView) convertView.findViewById(R.id.username);
                //txtV_exp_at.setText(card.getEnd_date());

                CheckBox chk_State = (CheckBox) convertView.findViewById(R.id.cardSelected);
                int finalI = i;
                chk_State.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            chk_State.setChecked(false);
                            //Toast.makeText(context, card.get(finalI).getId(), Toast.LENGTH_SHORT).show();

                            View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_recharge_carte, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            statutOperation.setText(getString(R.string.rechargeCard) + card.get(finalI).getCode_number() + " " + getString(R.string.amountPlease));
                            EditText montant = (EditText) view.findViewById(R.id.edit_montant);
                            title.setText(getString(R.string.recharge));
                            build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(!montant.getText().toString().equalsIgnoreCase("")){
                                        rechargeYourCarte(card.get(finalI).getCode_number(), Float.parseFloat(montant.getText().toString()), temp_account);
                                    } else{
                                        Toast.makeText(context, getString(R.string.veuillezInsererMontant), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            build_error.setNeutralButton(getString(R.string.annuler), new DialogInterface.OnClickListener() { // define the 'Cancel' button
                                public void onClick(DialogInterface dialog, int which) {
                                    //Either of the following two lines should work.
                                    dialog.cancel();
                                    //dialog.dismiss();
                                }
                            });
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();

                        }
                    }
                });
            }



            /**********************************************************************/
            /*List<Particulier> particuliers = myAllUserCard.get(position).getParticulier();
            for(int j=0; j<particuliers.size();j++){
                //nom et prenom
                TextView txtV_username = (TextView) convertView.findViewById(R.id.username);
                txtV_username.setText(particuliers.get(j).getFirstname() + " " + particuliers.get(j).getLastname());
            }*/
            return convertView;
        }




        private void rechargeYourCarte(String card_id, Float amount, String account_number){

            //********************DEBUT***********
            dialog2 = new ACProgressPie.Builder(getContext())
                    .ringColor(Color.WHITE)
                    .pieColor(Color.WHITE)
                    .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                    .build();
            dialog2.show();
            //*******************FIN*****

            call = service.rechargeCards(card_id, amount, account_number);
            call.enqueue(new Callback<MessageRechargeCardByAccount>() {
                @Override
                public void onResponse(Call<MessageRechargeCardByAccount> call, Response<MessageRechargeCardByAccount> response) {
                    Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                    dialog2.dismiss();
                    if(response.isSuccessful()){

                        assert response.body() != null;

                        String card_sender = myCardNumber;
                        String msg_sender = response.body().getMessage().getNotif();

                        String card_receiver = response.body().getMessage().getCode_number();
                        String msg_receiver = getString(R.string.notifRechargeCard) + " " + amount + " FCFA " + getString(R.string.notif2RechargeCard) + response.body().getMessage().getDeposit() + " FCFA";

                        successResponse(card_receiver, msg_receiver, card_sender, msg_sender);

                    }
                    else{
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                        errorResponse(myCardNumber, apiError.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<MessageRechargeCardByAccount> call, Throwable t) {
                    dialog2.dismiss();
                    Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
                }
            });
        }



    }

    private void errorResponse(String id_card, String response){

        /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
        //SERVICE GOOGLE FIREBASE
        final String id_carte_sm = id_card;

        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("id_carte")
                .equalTo(id_carte_sm);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals(id_carte_sm)) {
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.rechargeCarte), response, "error");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.rechargeCarte), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.rechargeCarte), response, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    private void successResponse(String id_cardReceiver, String msgReceiver, String id_cardSender, String msgSender) {

        /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////
        //SERVICE GOOGLE FIREBASE

        /*****************************************************RECEIVER MESSAGE******************************/
        Query queryReceiver = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("id_carte")
                .equalTo(id_cardReceiver);

        queryReceiver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals(id_cardReceiver)) {
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.rechargeCarte), msgReceiver, "success");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


/*****************************************************SENDER MESSAGE******************************/
        Query querySender = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("id_carte")
                .equalTo(id_cardSender);

        querySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user.getId_carte().equals(id_cardSender)) {
                            RemoteNotification(user.getId(), user.getPrenom(), getString(R.string.rechargeCarte), id_cardSender, "success");
                            //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.numeroInexistant), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(getContext(), getString(R.string.impossibleSendNotification), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.rechargeCarte), msgSender);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.rechargeCarte), msgSender, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(msgSender);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }


    private void readTempAccountInFile() {
        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().getApplication().openFileInput(file2);
            while ((c = fIn.read()) != -1){
                temp_account = temp_account + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void RemoteNotification(final String receiver, final String username, final String title, final String message, final String statut_notif){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(fuser.getUid(), R.mipmap.logo_official, username + ": " + message, title, receiver, statut_notif);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(getActivity(), getString(R.string.echoue), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void LocalNotification(String titles, String subtitles){

        ///////////////DEBUT NOTIFICATIONS///////////////////////////////
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        RemoteViews collapsedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notif_collapsed);
        RemoteViews expandedView = new RemoteViews(getActivity().getPackageName(),
                R.layout.notif_expanded);

        Intent clickIntent = new Intent(getContext(), NotifReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(getContext(),
                0, clickIntent, 0);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1, titles);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, subtitles);

        expandedView.setImageViewResource(R.id.image_view_expanded, R.mipmap.logo_official);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo_official)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget(View view) {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            //description du module
            LinearLayout desc = (LinearLayout) view.findViewById(R.id.descModule);
            TextView myTitle = (TextView) view.findViewById(R.id.descContent);
            myTitle.setTextColor(getResources().getColor(R.color.colorPrimaryRed));
            desc.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.edittextborder_red));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            getActivity().setTheme(Constant.theme);
        }else if (appTheme == 0){
            getActivity().setTheme(Constant.theme);
        }else{
            getActivity().setTheme(appTheme);
        }
    }

    private void readTempNumberInFile() {
        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//utilisé dans le cas des fragments uniquement pour afficher l'option menu
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_search_menu, menu);

        //search listener
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(TextUtils.isEmpty(query)){
                    adapterUserCardList.filter("");
                    listView.clearTextFilter();
                } else {
                    adapterUserCardList.filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(TextUtils.isEmpty(query)){
                    adapterUserCardList.filter("");
                    listView.clearTextFilter();
                } else {
                    adapterUserCardList.filter(query);
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.apropos) {
            Intent intent = new Intent(getContext(), Apropos.class);
            startActivity(intent);
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getContext(), TutorielUtilise.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
