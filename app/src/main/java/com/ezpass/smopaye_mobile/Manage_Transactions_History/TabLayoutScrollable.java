package com.ezpass.smopaye_mobile.Manage_Transactions_History;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.Login;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_historique_trans.Home_Historique;
import com.ezpass.smopaye_mobile.web_service_historique_trans.Operations;
import com.ezpass.smopaye_mobile.web_service_historique_trans.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabLayoutScrollable extends Fragment {

    private String urladress = "https://cm.secure-ws-api-smp-excecute.smopaye.fr/index.php?auth=Card&login=historyChange&dateDeal=2019-11-04&TelDeal=681797318&TypeDeal=debitcarte&fgfggergJHGS=Iyz4BVU2Hlt0cIeIPBlB7Wq15kMDI4NGRmOTNi&uhtdgG18=d0284df93b";
    private String getUrladress1 = "https://cm.secure-ws-api-smp-excecute.smopaye.fr/index.php?auth=Card&login=historyChange&dateDeal=2019-11-14&TelDeal=681797318&TypeDeal=debitcarte&fgfggergJHGS=Iyz4BVU2Hlt0cIeIPBlB7Wq15kMDI4NGRmOTNi&uhtdgG18=d0284df93b";
    private String[] montant_valeur;
    private String[] donataire_valeur;
    private String[] beneficiaire_valeur;
    private String[] date_HistoriqueTransaction;
    private String[] operation;
    private String[] frais;
    private ListView listView;
    private BufferedInputStream is;
    private String line = null;
    private String result = null;
    private static final SimpleDateFormat sdfNew = new SimpleDateFormat("EEEE, MMM d, yyyy HH:mm:ss a");
    private static final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    private String dateString = "2019-05-23 00:00:00.0";
    private Date date;

    private String rechargeMaCarte = "RECHARGE";
    int position;
    int jour, mois, annee;
    String fullDate = "";
    /////////////////////////////////LIRE CONTENU DES FICHIERS////////////////////
    String file = "tmp_number";
    int c;
    String temp_number = "";

    private LinearLayout historiqueVide;
    private ProgressBar progressHistorique;
    private Custom_Layout_Historique customListView = null;
    private String typHistTransaction;


    private static final String TAG = "TabLayoutScrollable";
    private ApiService service;
    private TokenManager tokenManager;
    private Call<Home_Historique> cardCall;
    private List<Operations> myResponse;
    private List<User> user;

   /* public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabLayoutScrollable tabFragment = new TabLayoutScrollable();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }*/


    public TabLayoutScrollable() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");
        jour = getArguments().getInt("jour", 0);
        mois = getArguments().getInt("mois", 0);
        annee = getArguments().getInt("annee", 0);
        //mois = (mois<10) ?  0 + mois : mois;
        //fullDate = annee + (mois < 10) ? mois: mois + annee;
        fullDate = annee+"-"+mois+"-"+jour;
        typHistTransaction = getArguments().getString("typeHistTrans", "");

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


        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        }
        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView =  inflater.inflate(R.layout.fragment_tab_layout_scrollable, container, false);

        listView = (ListView) convertView.findViewById(R.id.lviewHistorique);
        historiqueVide = (LinearLayout) convertView.findViewById(R.id.historiqueVide);
        progressHistorique = (ProgressBar) convertView.findViewById(R.id.progressHistorique);

     /*   try {
            // string to date
             date = defaultFormat.parse(dateString);
            int hours = date.getHours();


            String numberAsString = String.valueOf(hours);
            Toast.makeText(getActivity(), numberAsString, Toast.LENGTH_SHORT).show();



            Toast.makeText(getActivity(), defaultFormat.format(date), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), sdfNew.format(date), Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }*/


        //getArguments().getInt("jour", 0);
        /*Toast.makeText(getActivity(), String.valueOf(getArguments().getInt("jour", 0)), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), String.valueOf(getArguments().getInt("mois", 0)), Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), String.valueOf(getArguments().getInt("annee", 0)), Toast.LENGTH_SHORT).show();*/

        /*listView = (ListView) convertView.findViewById(R.id.lviewHistorique);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        collectData();
        Custom_Layout_Historique customListView = new Custom_Layout_Historique(getActivity(), montant_valeur, donataire_valeur, beneficiaire_valeur, date_HistoriqueTransaction);
        listView.setAdapter(customListView);*/

        return convertView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //new GetHttpResponse().execute();
        showAllHistoriqueTransactions(fullDate, typHistTransaction);
        //showAllHistoriqueTransactions("2020-07-27", "TRANSFERT");
    }


    public class GetHttpResponse extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
            progressHistorique.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            LoadHistoriqueTransactionData();
            //customListView = new Custom_Layout_Historique(getActivity(), montant_valeur, donataire_valeur, beneficiaire_valeur, date_HistoriqueTransaction);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressHistorique.setVisibility(View.GONE);
            if(customListView.isEmpty()){
                listView.setVisibility(View.GONE);
                historiqueVide.setVisibility(View.VISIBLE);
            } else {
                listView.setAdapter(customListView);
            }

        }
    }



    private void LoadHistoriqueTransactionData(){
        //connection
        try{

            final Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("auth","Card");
            builder.appendQueryParameter("login", "historyChange");
            builder.appendQueryParameter("dateDeal", fullDate);
            builder.appendQueryParameter("TelDeal", temp_number);
            builder.appendQueryParameter("TypeDeal", typHistTransaction);
            builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
            builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

            //Connexion au serveur
            //URL url = new URL("http://192.168.20.11:1234/listing.php"+builder.build().toString());
            URL url = new URL(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

            // URL url = new URL(urlAddress+"?auth="+param1+"&login="+param2+"&dateDeal="+param3+"&TelDeal="+param4+"&TypeDeal="+param5+"&fgfggergJHGS="+param6+"&uhtdgG18="+param7);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //content
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //JSON
        try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;
            montant_valeur = new String[ja.length()];
            donataire_valeur = new String[ja.length()];
            beneficiaire_valeur = new String[ja.length()];
            date_HistoriqueTransaction = new String[ja.length()];

            for(int i=0; i<=ja.length();i++){
                jo = ja.getJSONObject(i);
                donataire_valeur[i] = jo.getString("donateur");
                beneficiaire_valeur[i] = jo.getString("beneficier");
                montant_valeur[i] = jo.getString("montant") + "F";
                date = defaultFormat.parse(jo.getString("temps") + ".0");
                date_HistoriqueTransaction[i] = date.getHours() + "H " + date.getMinutes() + "min";
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static Fragment newInstance(int jour, int mois, int annee, String typeHistoriqueTransaction)
    {
        TabLayoutScrollable myFragment = new TabLayoutScrollable();
        Bundle args = new Bundle();
        args.putInt("jour", jour);
        args.putInt("mois", mois);
        args.putInt("annee", annee);
        args.putString("typeHistTrans", typeHistoriqueTransaction);
        myFragment.setArguments(args);
        return myFragment;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cardCall != null){
            cardCall.cancel();
            cardCall = null;
        }
    }


    private void showAllHistoriqueTransactions(String date, String type_operation) {

        cardCall = service.historiqueTransactions(date, type_operation);
        cardCall.enqueue(new Callback<Home_Historique>() {
            @Override
            public void onResponse(Call<Home_Historique> call, Response<Home_Historique> response) {
                Log.w(TAG, "SMOPAYE SERVER onResponse: "+ response);
                if(response.isSuccessful()){
                    assert response.body() != null;

                    myResponse = response.body().getData();
                    if(myResponse.isEmpty()){
                        historiqueVide.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else{
                        initArray(myResponse.size());
                        for(int i=0; i<myResponse.size();i++){
                            montant_valeur[i] = myResponse.get(i).getMontant();
                            operation[i] = myResponse.get(i).getOperation();
                            frais[i] = myResponse.get(i).getFrais();
                            date_HistoriqueTransaction[i] = myResponse.get(i).getDate();
                            user = myResponse.get(i).getUser();
                        }
                        customListView = new Custom_Layout_Historique(getActivity(), montant_valeur, operation, frais, date_HistoriqueTransaction, user);
                        historiqueVide.setVisibility(View.GONE);
                        listView.setAdapter(customListView);
                    }
                } else{
                    tokenManager.deleteToken();
                    startActivity(new Intent(getActivity(), Login.class));
                    getActivity().finish();
                }
                progressHistorique.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Home_Historique> call, Throwable t) {
                progressHistorique.setVisibility(View.GONE);
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
            }
        });
    }



    private void initArray(int size){
        montant_valeur = new String[size];
        operation = new String[size];
        frais = new String[size];
        date_HistoriqueTransaction = new String[size];
    }
}


