package com.ezpass.smopaye_mobile.Manage_HomePage;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.NotifFragmentAdapter.CardListAdapter;
import com.ezpass.smopaye_mobile.NotifFragmentHelper.Common;
import com.ezpass.smopaye_mobile.NotifFragmentHelper.RecyclerItemTouchHelper;
import com.ezpass.smopaye_mobile.NotifFragmentHelper.RecyclerItemTouchHelperListener;
import com.ezpass.smopaye_mobile.NotifFragmentModel.Item;
import com.ezpass.smopaye_mobile.NotifFragmentRemote.IMenuRequest;
import com.ezpass.smopaye_mobile.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationsFragment extends Fragment implements RecyclerItemTouchHelperListener {


    // Array of strings...
    ListView simpleList;
    String  Item[] = {"Télécollecte", "Carte smopaye", "Manage_Recharge", "Retrait", "Retrait", "Manage_Recharge"};
    String  SubItem[] = {"Une Télécollecte sur un montant de 10 000 fcfa a été éffectué.",
            "Votre carte s'expire le 29/09/19 veuillez passer mettre à jour.",
            "Votre recharge a échoué carte invalid. Veuillez vous rapprocher.",
            "Votre retrait aupres de votre opérateur orange a reussit.",
            "Votre retrait aupres de votre opérateur orange a reussit.",
            "Votre retrait aupres de votre opérateur orange a reussit."};
    int flags[] = {R.drawable.ic_notifications_black_48dp, R.drawable.ic_notifications_red_48dp, R.drawable.ic_notifications_red_48dp, R.drawable.ic_notifications_black_48dp, R.drawable.ic_notifications_red_48dp, R.drawable.ic_notifications_black_48dp};
    String  ItemDate[] = {"01/08/2019", "02/08/2019", "03/08/2019", "04/08/2019", "05/08/2019", "06/08/2019"};

    private FloatingActionButton btnSuppNotif;
    private LinearLayout notificationsVides;


//https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php
    String urladress = "http://bertin-mounok.com/soccer.php";
    BufferedInputStream is;
    String line = null;
    String result = null;



    //AJOUT DU RECYCLER AVEC SUPPRESSION DES NOTIFICATIONS MANUELLE
    /*************************************/
    private final String URL_API = "https://api.androidhive.info/json/menu.json";
    private RecyclerView recyclerView;
    private List<Item> list;
    private ArrayList<Item> list2;
    private CardListAdapter adapter;
    private CoordinatorLayout rootLayout;
    private IMenuRequest mService;
    private EditText inputSearch;
    private RelativeLayout notif_search;
    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        //permet de rendre une activity en plein écran
        //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);

        getActivity().setTitle(getString(R.string.notifications));


        btnSuppNotif = (FloatingActionButton) view.findViewById(R.id.btnSuppNotif);
        //simpleList = (ListView) view.findViewById(R.id.ListViewNotification);
        notificationsVides = (LinearLayout) view.findViewById(R.id.notificationsVides);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        notif_search = (RelativeLayout) view.findViewById(R.id.notif_search);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);



        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        /*assert getArguments() != null;
        String retour = getArguments().getString("result_BD");
        assert retour != null;
        String[] parts = retour.split("-");*/

        //numTel = getArguments().getString("telephone");


        //BASE DE DONNEES DISTANTE
        /*******************************DEBUT******************************/
        /*simpleList = (ListView) view.findViewById(R.id.ListViewNotification);
        //cela fontionne bien. faut juste enlever les commentaires en dessous
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        //collectData();
        CustomAdapterNotification customAdapter = new CustomAdapterNotification(getActivity(), Item,SubItem, flags, ItemDate);
        simpleList.setAdapter(customAdapter);*/
        /********************************FIN*******************************/



        //BASE DE DONNEES LOCALE
        /*******************************DEBUT******************************/
        /*DbHandler db = new DbHandler(getActivity());
        ArrayList<HashMap<String, String>> userList = db.GetUsers();

        if(userList.isEmpty()){
            notificationsVides.setVisibility(View.VISIBLE);
            simpleList.setVisibility(View.GONE);

        } else {
            notificationsVides.setVisibility(View.GONE);
            simpleList.setVisibility(View.VISIBLE);
            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.listitem_notification, new String[]{"title","description", "image_notification", "dateEnreg"}, new int[]{R.id.item, R.id.subitem, R.id.image, R.id.itemDate});
            simpleList.setAdapter(adapter);
        }*/
        /********************************FIN*******************************/





        //SUPRESSION DES NOTIFICATIONS APRES GLISSEMENT
        /*******************************DEBUT******************************/
        DbHandler db = new DbHandler(getActivity());
        list2 = db.listAllNotification();

        mService = Common.getMenuRequest();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        rootLayout = (CoordinatorLayout) view.findViewById(R.id.rootlayout);

        if(list2.isEmpty()){
            notificationsVides.setVisibility(View.VISIBLE);
            notif_search.setVisibility(View.GONE);

        } else {
            adapter = new CardListAdapter(getContext(), list2, list2);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0,    ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT|ItemTouchHelper.UP|ItemTouchHelper.DOWN, this);
            new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        }
        //request API
       // addItemTocart();

        /********************************FIN*******************************/



        btnSuppNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list2.isEmpty()){
                    notificationsVides.setVisibility(View.VISIBLE);
                    notif_search.setVisibility(View.GONE);
                } else {
                    db.DeleteAllNotifications();

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);

                    Toast.makeText(getContext(), getString(R.string.notifVide), Toast.LENGTH_SHORT).show();
                    notificationsVides.setVisibility(View.VISIBLE);
                    notif_search.setVisibility(View.GONE);
                }

            }
        });

        //module de recherche
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //module d'actualisation
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(list2.isEmpty()){
                    notificationsVides.setVisibility(View.VISIBLE);
                    notif_search.setVisibility(View.GONE);

                } else {
                    adapter = new CardListAdapter(getContext(), list2, list2);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }




    //SUPRESSION DES NOTIFICATIONS APRES GLISSEMENT
    /*******************************DEBUT******************************/
    private void addItemTocart() {
        mService.getMenuList(URL_API)
                .enqueue(new Callback<List<Item>>() {
                    @Override
                    public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                        list.clear(); //remove old item
                        list.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Item>> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof CardListAdapter.MyViewHolder){
            String name = list2.get(viewHolder.getAdapterPosition()).getName();
            final int deteteIndex = viewHolder.getAdapterPosition();
            final String id_bd = list2.get(viewHolder.getAdapterPosition()).getId();
            final Item deletedItem = list2.get(viewHolder.getAdapterPosition());

            adapter.removeItem(deteteIndex, Integer.parseInt(id_bd), getActivity());

            Snackbar snackbar = Snackbar.make(rootLayout, name + " " + getString(R.string.encoursSuppression), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.supprimer), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedItem, deteteIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
    /********************************FIN*******************************/




    private void collectData(){
        //connection
        try{
            //parametres
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("nom","Vini");

            URL url = new URL(urladress + builder.build().toString());
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");

            /*con.setConnectTimeout(5000);
            con.setRequestMethod("POST");
            con.connect();*/

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

            /*String data = "";
            while (br.ready() || data=="")
            {
                data+= br.readLine();
            }*/

            is.close();
            result=sb.toString();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //JSON
        /*try{
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;
            Item = new String[ja.length()];
            SubItem = new String[ja.length()];
           // flags = new String[ja.length()];
            ItemDate = new String[ja.length()];

            for(int i=0; i<=ja.length();i++){
                jo = ja.getJSONObject(i);
                Item[i] = jo.getString("name");
                SubItem[i] = jo.getString("email");
                ItemDate[i] = jo.getString("dates");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }*/
    }

    /*private  void recharg(final String adresse){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //********************DEBUT***********
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // On ajoute un message à notre progress dialog
                            progressDialog.setMessage("Connexion au serveur");
                            // On donne un titre à notre progress dialog
                            progressDialog.setTitle("Attente d'une réponse");
                            // On spécifie le style
                            //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            // On affiche notre message
                            progressDialog.show();
                            //build.setPositiveButton("ok", new View.OnClickListener()
                        }
                    });
                    //*******************FIN*****
                    Uri.Builder builder = new Uri.Builder();
                    builder.appendQueryParameter("montant",montant.getText().toString().trim());
                    builder.appendQueryParameter("numcarte",numCarte.getText().toString().trim());


                    URL url = new URL(adresse+builder.build().toString());//"http://192.168.20.11:1234/recharge.php"
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();


                    InputStream inputStream = httpURLConnection.getInputStream();

                    final BufferedReader bufferedReader  =  new BufferedReader(new InputStreamReader(inputStream));

                    String string="";
                    String data="";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RechargePropreCompte.this, "ooooo", Toast.LENGTH_SHORT).show();
                        }
                    });

                    while (bufferedReader.ready() || data==""){
                        data+=bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();


                    final String f = data;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();


                            View view = LayoutInflater.from(RechargePropreCompte.this).inflate(R.layout.alert_dialog_success, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                            title.setText("Etat de votre Compte");
                            imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            statutOperation.setText(f);
                            build_error.setPositiveButton("OK", null);
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();
                            notifications("Manage_Recharge Propre Compte", f);
                            //Toast.makeText(getApplicationContext(), "login, mot de passe ou numéro de carte incorrect !!!", Toast.LENGTH_LONG).show();
                        }
                    });


                    //    JSONObject jsonObject = new JSONObject(data);
                    //  jsonObject.getString("status");
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i=0;i<jsonArray.length();i++){
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(RechargePropreCompte.this, jsonObject.getString("telephone"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RechargePropreCompte.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    try {
                        Thread.sleep(2000);
                        //Toast.makeText(RechargePropreCompte.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
}
