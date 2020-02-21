package com.ezpass.smopaye_mobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class CarteFragment extends Fragment {

    private TextView dateExpirationCarte, typeCarte, etatCarteUser, CarteNumber, tAbonnement;
    private Switch verouillerCarte;

    private BufferedInputStream is;
    private String line = null;
    private String result = null;

    private String  expiration[];
    private String  carte[];


    private ProgressDialog progressDialog1;
    private AlertDialog.Builder build, build_error;


    private String i = "0";
    private String file = "tmp_etat_carte";
    int c;
    String temp_number = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_carte, container, false);
        getActivity().setTitle(getString(R.string.maCarteSmopaye));

        dateExpirationCarte = (TextView) view.findViewById(R.id.tVdateExpirationCarte);
        typeCarte = (TextView) view.findViewById(R.id.tVtypeCarte);
        verouillerCarte = (Switch) view.findViewById(R.id.sWtVerouillerCarte);
        etatCarteUser = (TextView) view.findViewById(R.id.etatDelaCarte);
        CarteNumber = (TextView) view.findViewById(R.id.CarteNumber);
        tAbonnement = (TextView) view.findViewById(R.id.tAbonnement);

        //checkStateCarte("http://192.168.20.11:1234/recharge.php", "694048925", "Actif");


        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        assert getArguments() != null;
        String retour = getArguments().getString("result_BD");
        assert retour != null;
        String[] parts = retour.split("-");

        //stockage des données
        String nom = parts[0]; //Nom
        String prenom = parts[1]; //Prenom
        String session = parts[2]; // Accepteur, Administrateur
        String etat = parts[3]; // Actif, Inactif, Offline
        String typeAccepteur = parts[4]; // chauffeur, bus, restaurant

        String annee = parts[5]; // 2019
        String mois = parts[6]; // Septembre
        String jour = parts[7]; // 09

        String etatCart = parts[8]; // actif=deverouillé, inactif=verouillé
        String typeCart = parts[9]; // A M1

        String cardNumber = parts[10]; // 12345678
        String typeAbonnement = parts[11]; // hebdomadaire, mensuel


        typeCarte.setText( getString(R.string.carteMag)+ " " + typeCart);
        CarteNumber.setText(cardNumber);
        tAbonnement.setText(typeAbonnement);





        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().openFileInput(file);
            while ((c = fIn.read()) != -1){
                temp_number = temp_number + Character.toString((char)c);
            }
            //Toast.makeText(getActivity(), temp_number, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(etatCart.equalsIgnoreCase("inactif") && temp_number.equalsIgnoreCase("")) {
            verouillerCarte.setChecked(true);
            //verouillerCarte.setEnabled(false);
            etatCarteUser.setText(getString(R.string.votreCarteVerouille));
        }
        else if (etatCart.equalsIgnoreCase("actif") && temp_number.equalsIgnoreCase("") ){
            verouillerCarte.setChecked(false);
            //verouillerCarte.setEnabled(true);
        }
        else if (temp_number.equalsIgnoreCase("1")) {
            verouillerCarte.setChecked(true);
            etatCarteUser.setText(getString(R.string.votreCarteVerouille));
        }
        else if(temp_number.equalsIgnoreCase("2")){
            verouillerCarte.setChecked(false);
        }


        //TRAITEMENT DE LA DATE D'EXPIRATION
        String times = jour + "/" + mois + "/" + annee;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date d = simpleDate.parse(times);
            String currentDate1 = DateFormat.getDateInstance(DateFormat.FULL).format(d.getTime());
            dateExpirationCarte.setText(currentDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //RECUPERATION DU TELEPHONE
        assert getArguments() != null;
        final String number = getArguments().getString("telephone");
        assert number != null;
        //Toast.makeText(getActivity(), number, Toast.LENGTH_SHORT).show();





       /* verouillerCarte.setOnClickListener(new View.OnClickListener() {

                                               @Override
                                               public void onClick(View v) {
                                                   final Switch btn = (Switch) v;
                                                   final boolean switchChecked = btn.isChecked();

                                                   if (btn.isChecked()) {
                                                       btn.setChecked(false);
                                                   } else {
                                                       btn.setChecked(true);
                                                   }

                                                   String message = getString(R.string.deverouillerCarte);
                                                   if (!btn.isChecked()) {
                                                       message = getString(R.string.verouillerCarte);
                                                   }


                                                   build_error = new AlertDialog.Builder(getActivity());
                                                   View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_success, null);
                                                   TextView title = (TextView) view.findViewById(R.id.title);
                                                   TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                                   ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                                   title.setText(getString(R.string.information));
                                                   imageButton.setImageResource(R.drawable.ic_warning_black_24dp);
                                                   statutOperation.setText(message);

                                                   build_error.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {

                                                           if (switchChecked) {
                                                               btn.setChecked(true);
                                                               Toast.makeText(getActivity(), "Votre Carte a été Vérouillé", Toast.LENGTH_SHORT).show();
                                                               checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", number, "inactif");
                                                               verouillerCarte.setEnabled(false);
                                                           } else {
                                                               btn.setChecked(false);
                                                               Toast.makeText(getActivity(), "Votre Carte a été Devérouillé", Toast.LENGTH_SHORT).show();
                                                           }

                                                       }
                                                   });
                                                   build_error.setNegativeButton("Non", null);

                                                   build_error.setView(view);
                                                   build_error.show();




                                               }
                                       });*/




        verouillerCarte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                final Switch btn = (Switch) v;
                final boolean switchChecked = btn.isChecked();

                /*if (btn.isChecked()) {
                    btn.setChecked(true);
                } else {
                    btn.setChecked(false);
                }

                String message = getString(R.string.verouillerCarte);
                if (!btn.isChecked()) {
                    message = getString(R.string.deverouillerCarte);
                }

                if (btn.isChecked()) {
                    build_error = new AlertDialog.Builder(getActivity());
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_warning_black_24dp);
                    statutOperation.setText(message);

                    build_error.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (switchChecked) {
                                btn.setChecked(true);
                                Toast.makeText(getActivity(), "Votre Carte a été Vérouillé", Toast.LENGTH_SHORT).show();
                                checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", number, "inactif");
                                //verouillerCarte.setEnabled(false);
                            } else {
                                btn.setChecked(false);
                                Toast.makeText(getActivity(), "Votre Carte a été Devérouillé", Toast.LENGTH_SHORT).show();
                                checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", number, "actif");
                            }

                        }
                    });
                    build_error.setNegativeButton("Non", null);

                    build_error.setView(view);
                    build_error.show();

                } else {
                    btn.setChecked(false);
                }*/


                if(switchChecked){
                    Toast.makeText(getActivity(), getString(R.string.votreCarteVerouille), Toast.LENGTH_SHORT).show();
                    checkStateCarte(ChaineConnexion.getAdresseURLsmopayeServer(), number, "inactif");
                    i = "1";
                    etatCarteUser.setText(getString(R.string.votreCarteVerouille));
                } else{
                    Toast.makeText(getActivity(), getString(R.string.votreCarteDeverouille), Toast.LENGTH_SHORT).show();
                    checkStateCarte(ChaineConnexion.getAdresseURLsmopayeServer(), number, "actif");
                    i = "2";
                    etatCarteUser.setText(getString(R.string.balacerBouton));
                }

                /////////////////////////ECRIRE DANS LES FICHIERS/////////////////////////////////
                try{
                    //ecrire donnée renvoyées par le web service lors de la connexion
                    FileOutputStream fOut = getActivity().openFileOutput(file, MODE_PRIVATE);
                    fOut.write(i.getBytes());
                    fOut.close();
                } catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

       /* verouillerCarte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    Toast.makeText(getActivity(), verouillerCarte.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                     checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", number, "actif");
                }
                else {
                    Toast.makeText(getActivity(), verouillerCarte.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                    checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", number, "inactif");
                }


            }
        });*/


       /* verouillerCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verouillerCarte.isChecked()){
                    Toast.makeText(getActivity(), verouillerCarte.getTextOn().toString(), Toast.LENGTH_SHORT).show();
                   // checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", "694048925", "actif");
                }
                else
                {

                    build_error = new AlertDialog.Builder(getActivity());
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_warning_black_24dp);
                    statutOperation.setText(getString(R.string.verouillerCarte));

                    build_error.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), verouillerCarte.getTextOff().toString(), Toast.LENGTH_SHORT).show();
                            //checkStateCarte("https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php", "694048925", "inactif");
                        }
                    });
                    build_error.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    build_error.setView(view);
                    build_error.show();


                }
            }
        });*/


        return view;
    }




    private void checkStateCarte(final String urladress, final String telephone, final String etat){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //********************DEBUT***********
                    /*getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // On ajoute un message à notre progress dialog
                            progressDialog1.setMessage(getString(R.string.connexionserver));
                            // On donne un titre à notre progress dialog
                            progressDialog1.setTitle(getString(R.string.attenteReponseServer));
                            // On spécifie le style
                            //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            // On affiche notre message
                            progressDialog1.show();
                            //build.setPositiveButton("ok", new View.OnClickListener()
                        }
                    });*/
                    //*******************FIN*****
                    Uri.Builder builder = new Uri.Builder();
                    builder.appendQueryParameter("enregUser","Card");
                    builder.appendQueryParameter("enregReg", "EtatCarte");
                    builder.appendQueryParameter("telephone", telephone);
                    builder.appendQueryParameter("EtatCarte", etat);
                    builder.appendQueryParameter("fgfggergJHGS",ChaineConnexion.getEncrypted_password());
                    builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());


                    URL url = new URL(urladress+builder.build().toString());//"http://192.168.20.11:1234/recharge.php"
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.connect();


                    InputStream inputStream = httpURLConnection.getInputStream();

                    final BufferedReader bufferedReader  =  new BufferedReader(new InputStreamReader(inputStream));

                    String string="";
                    String data="";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(), "ooooo", Toast.LENGTH_SHORT).show();
                        }
                    });

                    while (bufferedReader.ready() || data==""){
                        data+=bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    inputStream.close();


                    final String f = data.trim();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // progressDialog1.dismiss();
                           /* build.setMessage(f);
                            build.setCancelable(true);
                            AlertDialog dialog = build.create();
                            dialog.show();*/

                            Toast.makeText(getActivity(), f, Toast.LENGTH_SHORT).show();

                            if(f.indexOf("direction") > 0)
                                etatCarteUser.setText(f);



                            /*
                            ATTENTE DU STATUS RENVOYE PAR MONETBIL
                               notifications("Télécollecte", "Votre compte a bien été crédite d'un montant de  " + score.getMontant() + " FCFA");
                               dbHandler.insertUserDetails("Télécollecte","Votre compte a bien été crédite d'un montant de  " + score.getMontant() + " FCFA", shortDateFormat.format(aujourdhui));

                            */

                            /*View view = LayoutInflater.from(RechargePropreCompte.this).inflate(R.layout.alert_dialog_success, null);
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
                            notifications("Recharge Propre Compte", f);*/
                            //Toast.makeText(getApplicationContext(), "login, mot de passe ou numéro de carte incorrect !!!", Toast.LENGTH_LONG).show();
                        }
                    });


                    //    JSONObject jsonObject = new JSONObject(data);
                    //  jsonObject.getString("status");
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i=0;i<jsonArray.length();i++){
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(getActivity(), jsonObject.getString("telephone"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (final IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    try {
                        Thread.sleep(2000);
                        //Toast.makeText(RechargePropreCompte.this, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                   // progressDialog1.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }





}
