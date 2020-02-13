package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.ChaineConnexion;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.QRCodeShow;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.RemoteFragments.APIService;
import com.ezpass.smopaye_mobile.RemoteModel.User;
import com.ezpass.smopaye_mobile.RemoteNotifications.Client;
import com.ezpass.smopaye_mobile.RemoteNotifications.Data;
import com.ezpass.smopaye_mobile.RemoteNotifications.MyResponse;
import com.ezpass.smopaye_mobile.RemoteNotifications.Sender;
import com.ezpass.smopaye_mobile.RemoteNotifications.Token;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpass.smopaye_mobile.ChaineConnexion.getsecurity_keys;
import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class SouscriptionUploadIMGidCard extends AppCompatActivity {

    Button GetImageFromGalleryButton, UploadImageOnServerButton;
    ImageView ShowSelectedImageRecto, ShowSelectedImageVerso;
    Bitmap FixBitmap, FixBitmap2;
    String ImageTagRecto = "nom_piece_recto" ;
    String ImageNameRecto = "piece_recto" ;
    String ImageTagVerso = "nom_piece_verso" ;
    String ImageNameVerso = "piece_verso" ;
    ProgressDialog progressDialog, progressDialog1, progressDialog2;
    ByteArrayOutputStream byteArrayOutputStream1, byteArrayOutputStream2;
    byte[] byteArray1, byteArray2;
    String ConvertImageRecto, ConvertImageVerso ;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;

    String nom, prenom, genre, tel, cni, sessioncompte, adresse, idcarte, idcategorie, typeabon, uniquser, GetImageNameFromRectoIdCard, GetImageNameFromVersoIdCard, sessioncompteValue, idcategorieValue, register;
    LinearLayout imgCardVerso;
    RelativeLayout dividerBarUpload;
    TextView infoNom, infoPrenom, infoCni;
    AlertDialog.Builder build_error;


    //BD LOCALE
    private DbHandler dbHandler;
    private DbUser dbUser;
    private Date aujourdhui;
    private DateFormat shortDateFormat;



    //SERVICES GOOGLE FIREBASE
    FirebaseAuth auth;
    DatabaseReference reference;
    APIService apiService;
    FirebaseUser fuser;

    String resultFromBD = "";


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souscription_upload_imgid_card);


        getSupportActionBar().setTitle("Souscription - Etape Finale");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        nom = intent.getStringExtra("NOM");
        prenom = intent.getStringExtra("PRENOM");
        genre = intent.getStringExtra("GENRE");
        tel = intent.getStringExtra("TELEPHONE");
        cni = intent.getStringExtra("CNI");
        sessioncompte = intent.getStringExtra("sessioncompte");
        adresse = intent.getStringExtra("Adresse");
        idcarte = intent.getStringExtra("IDCARTE");
        idcategorie = intent.getStringExtra("IDCathegorie");
        typeabon = intent.getStringExtra("typeAbon");
        uniquser = intent.getStringExtra("uniquser");
        sessioncompteValue = intent.getStringExtra("sessioncompteValue");
        idcategorieValue = intent.getStringExtra("IDCathegorieValue");
        register = intent.getStringExtra("register");




        GetImageFromGalleryButton = (Button)findViewById(R.id.buttonSelect);
        UploadImageOnServerButton = (Button)findViewById(R.id.buttonUpload);
        ShowSelectedImageRecto = (ImageView)findViewById(R.id.imageViewRecto);
        ShowSelectedImageVerso = (ImageView)findViewById(R.id.imageViewVerso);
        imgCardVerso = (LinearLayout) findViewById(R.id.imgCardVerso);
        infoNom = (TextView) findViewById(R.id.infoNom);
        infoPrenom = (TextView) findViewById(R.id.infoPrenom);
        infoCni = (TextView) findViewById(R.id.infoCni);
        dividerBarUpload = (RelativeLayout) findViewById(R.id.dividerBarUpload);




        String[] parts = cni.split("-");
        if(parts[0].toLowerCase().equalsIgnoreCase("cni"))
            infoCni.setText(getString(R.string.infoIdCard));
        else
            infoCni.setText(getString(R.string.pays) +", " + parts[0]);

        infoNom.setText(nom);
        infoPrenom.setText(prenom);

        byteArrayOutputStream1 = new ByteArrayOutputStream();
        byteArrayOutputStream2 = new ByteArrayOutputStream();

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();


            }
        });


        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromRectoIdCard = nom + "_" + prenom + "_Recto_" + parts[0];
                GetImageNameFromVersoIdCard = nom + "_" + prenom + "_Verso_" + parts[0];

                new AsyncTaskUploadImg().execute();
                //UploadImageToServer();

            }
        });

        if (ContextCompat.checkSelfPermission(SouscriptionUploadIMGidCard.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

    }



    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.selectAction));
        String[] pictureDialogItems = {
                getString(R.string.tofGallery),
                getString(R.string.camera) };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    if(ShowSelectedImageRecto.getDrawable() == null) {
                        FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        //debut compression
                        //FixBitmap = Bitmap.createScaledBitmap(FixBitmap, 60, 60, true);
                        //fin compression
                        ShowSelectedImageRecto.setImageBitmap(FixBitmap);
                        imgCardVerso.setVisibility(View.VISIBLE);
                        dividerBarUpload.setVisibility(View.VISIBLE);
                        return;
                    }


                    if(ShowSelectedImageRecto.getDrawable() != null){
                        FixBitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        ShowSelectedImageVerso.setImageBitmap(FixBitmap2);
                        UploadImageOnServerButton.setVisibility(View.VISIBLE);
                        imgCardVerso.setVisibility(View.VISIBLE);
                        dividerBarUpload.setVisibility(View.VISIBLE);
                        GetImageFromGalleryButton.setVisibility(View.GONE);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SouscriptionUploadIMGidCard.this, getString(R.string.operation_fail), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            /*FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImageRecto.setImageBitmap(FixBitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);*/

            if(ShowSelectedImageRecto.getDrawable() == null) {
                FixBitmap = (Bitmap) data.getExtras().get("data");
                //debut compression
                //FixBitmap = Bitmap.createScaledBitmap(FixBitmap, 60, 60, true);
                //fin compression
                ShowSelectedImageRecto.setImageBitmap(FixBitmap);
                imgCardVerso.setVisibility(View.VISIBLE);
                dividerBarUpload.setVisibility(View.VISIBLE);
                return;
            }


            if(ShowSelectedImageRecto.getDrawable() != null){
                FixBitmap2 = (Bitmap) data.getExtras().get("data");
                //debut compression
                //FixBitmap2 = Bitmap.createScaledBitmap(FixBitmap2, 60, 60, true);
                //fin compression
                ShowSelectedImageVerso.setImageBitmap(FixBitmap2);
                UploadImageOnServerButton.setVisibility(View.VISIBLE);
                imgCardVerso.setVisibility(View.VISIBLE);
                dividerBarUpload.setVisibility(View.VISIBLE);
                GetImageFromGalleryButton.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //progressDialog.dismiss();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);

                httpURLConnection.setConnectTimeout(5000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                //bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        /*private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(SouscriptionUploadIMGidCard.this, getString(R.string.impossibleUsingCamera), Toast.LENGTH_LONG).show();

            }
        }
    }


    //initialisation du FixBitmap avec les images chargés de la camera ou gallerie
    private void InitImage(){
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream1);
        FixBitmap2.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream2);

        byteArray1 = byteArrayOutputStream1.toByteArray();
        byteArray2 = byteArrayOutputStream2.toByteArray();

        ConvertImageRecto = Base64.encodeToString(byteArray1, Base64.DEFAULT);
        ConvertImageVerso = Base64.encodeToString(byteArray2, Base64.DEFAULT);
    }


    //ETAPE 1: Envoi unique des Images vers le serveur
    private class AsyncTaskUploadImg extends AsyncTask<Void,String,String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            InitImage();

            progressDialog2 = new ProgressDialog(SouscriptionUploadIMGidCard.this);
            progressDialog2.setMessage("Chargement Encours...");
            progressDialog2.setTitle("Etape 1: Envoi des Images");
            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog2.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog2.setMessage(values[0] + "Opération Encours...");
            //progressDialog2.setMessage("Encore un petit instant");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog2.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {

            ImageProcess1 imageProcessClass = new ImageProcess1();
            HashMap<String,String> HashMapParams = new HashMap<String,String>();

            HashMapParams.put(ImageTagRecto, GetImageNameFromRectoIdCard);
            HashMapParams.put(ImageNameRecto, ConvertImageRecto);
            HashMapParams.put(ImageTagVerso, GetImageNameFromVersoIdCard);
            HashMapParams.put(ImageNameVerso, ConvertImageVerso);

            //String FinalData = imageProcessClass.ImageHttpRequest1("http://bertin-mounok.com/upload-image-to-server.php", HashMapParams);
            String FinalData = imageProcessClass.ImageHttpRequest1("https://management.device.domaineteste.space.smopaye.fr/upload.php", HashMapParams);
            return FinalData;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String f = s.toLowerCase().trim();

            progressDialog2.dismiss();

            int pos = f.indexOf("success");
            if (pos >= 0) {
                // Envoi des données du formulaire vers le serveur
                UploadImageToServer();
            } else{
                Toast.makeText(getApplicationContext(), "Une erreur est survenue lors de l'upload de l'image", Toast.LENGTH_LONG).show();
                View view = LayoutInflater.from(SouscriptionUploadIMGidCard.this).inflate(R.layout.alert_dialog_success, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                title.setText(getString(R.string.information));
                imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                statutOperation.setText(getString(R.string.erreurSurvenue));
                build_error.setPositiveButton("OK", null);
                build_error.setCancelable(false);
                build_error.setView(view);
                build_error.show();
            }
        }
    }

    //Traitement des images uploadées
    public class ImageProcess1{

        public String ImageHttpRequest1(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }


    //ETAPE 2: Upload des données vers le serveur
    public void UploadImageToServer(){

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(SouscriptionUploadIMGidCard.this,"Etape 2: Envoi des Données","Connexion au serveur Smopaye",true,true);
                build_error = new AlertDialog.Builder(SouscriptionUploadIMGidCard.this);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                progressDialog.dismiss();
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                String f = string1.toLowerCase().trim();

                progressDialog.dismiss();

                int pos = f.indexOf("success");
                if (pos >= 0) {

                    // Calling Method to Parese JSON data into listView.
                    new AsyncTaskGoogleFirebase(f).execute();

                } else{

                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                    dbHandler = new DbHandler(getApplicationContext());
                    aujourdhui = new Date();
                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                    //////////////////////////////////NOTIFICATIONS////////////////////////////////
                    LocalNotification("Souscription", f);
                    dbHandler.insertUserDetails("Souscription","carte non enregistré", "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

                    //progressDialog.dismiss();


                    build_error = new AlertDialog.Builder(SouscriptionUploadIMGidCard.this);
                    View view = LayoutInflater.from(SouscriptionUploadIMGidCard.this).inflate(R.layout.alert_dialog_success, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                    title.setText(getString(R.string.information));
                    imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                    statutOperation.setText(f);
                    build_error.setPositiveButton("OK", null);
                    build_error.setCancelable(false);
                    build_error.setView(view);
                    build_error.show();

                    Toast.makeText(getApplicationContext(), f, Toast.LENGTH_LONG).show();



                }

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                /*HashMap<String,String> HashMapParams = new HashMap<String,String>();


                HashMapParams.put("enregUser","users");
                HashMapParams.put("enregReg", "register");

                HashMapParams.put("NOM", nom);
                HashMapParams.put("PRENOM", prenom);
                HashMapParams.put("GENRE", genre.toUpperCase());
                HashMapParams.put("TELEPHONE", tel);
                HashMapParams.put("CNI", cni);
                HashMapParams.put("sessioncompte", sessioncompte);
                HashMapParams.put("Adresse", adresse);
                HashMapParams.put("IDCARTE", idcarte);
                HashMapParams.put("IDCathegorie", idcategorie);
                HashMapParams.put("typeAbon", typeabon);
                HashMapParams.put("uniquser", uniquser);
                HashMapParams.put(ImageTagRecto, GetImageNameFromRectoIdCard);
                HashMapParams.put(ImageNameRecto, ConvertImageRecto);
                HashMapParams.put(ImageTagVerso, GetImageNameFromVersoIdCard);
                HashMapParams.put(ImageNameVerso, ConvertImageVerso);

                HashMapParams.put("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                HashMapParams.put("uhtdgG18",ChaineConnexion.getSalt());*/

                Uri.Builder builder = new Uri.Builder();

                if(register.equalsIgnoreCase("autoEnreg")){
                    builder.appendQueryParameter("auth","Users");
                    builder.appendQueryParameter("login", "autoregister");
                    builder.appendQueryParameter("nom", nom);
                    builder.appendQueryParameter("prenom", prenom);
                    builder.appendQueryParameter("genre", genre.toUpperCase());
                    builder.appendQueryParameter("telephone", tel);
                    builder.appendQueryParameter("cni", cni);
                    //builder.appendQueryParameter("sessioncompte", sessioncompte);
                    builder.appendQueryParameter("adresse", adresse);
                    //builder.appendQueryParameter("IDCARTE", idcarte);
                    builder.appendQueryParameter("cathegorie", idcategorie);
                    builder.appendQueryParameter("cryptverso", GetImageNameFromVersoIdCard);
                    builder.appendQueryParameter("cryptrecto", GetImageNameFromRectoIdCard);
                    builder.appendQueryParameter("typeAbon", typeabon);
                    builder.appendQueryParameter("uniquser", uniquser);
                } else {
                    builder.appendQueryParameter("enregUser", "users");
                    builder.appendQueryParameter("enregReg", "register");
                    builder.appendQueryParameter("NOM", nom);
                    builder.appendQueryParameter("PRENOM", prenom);
                    builder.appendQueryParameter("GENRE", genre.toUpperCase());
                    builder.appendQueryParameter("TELEPHONE", tel);
                    builder.appendQueryParameter("CNI", cni);
                    builder.appendQueryParameter("sessioncompte", sessioncompte);
                    builder.appendQueryParameter("Adresse", adresse);
                    builder.appendQueryParameter("IDCARTE", idcarte);
                    builder.appendQueryParameter("IDCathegorie", idcategorie);
                    /*builder.appendQueryParameter("cryptverso", GetImageNameFromVersoIdCard);
                    builder.appendQueryParameter("cryptrecto", GetImageNameFromRectoIdCard);*/
                    builder.appendQueryParameter("typeAbon", typeabon);
                    builder.appendQueryParameter("uniquser", uniquser);
                }

                builder.appendQueryParameter("fgfggergJHGS", ChaineConnexion.getEncrypted_password());
                builder.appendQueryParameter("uhtdgG18",ChaineConnexion.getSalt());

                //URL url = new URL("http://192.168.20.6:1234/index.php"+builder.build().toString());
                //String FinalData = imageProcessClass.ImageHttpRequest(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

                String FinalData = imageProcessClass.ImageHttpRequest(ChaineConnexion.getAdresseURLsmopayeServer() + builder.build().toString());

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }



    //ETAPE 3: Envoi des données vers le serveur Google
    private class AsyncTaskGoogleFirebase extends  AsyncTask<Void,Void,String>{

        private String result;

        public AsyncTaskGoogleFirebase(String result) {
            this.result = result;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            progressDialog1 = new ProgressDialog(SouscriptionUploadIMGidCard.this);
            progressDialog1.setMessage("Connexion au Serveur Google");
            progressDialog1.setTitle("Etape 3 - Finale");
            progressDialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog1.show();

            //SERVICE GOOGLE FIREBASE
            auth = FirebaseAuth.getInstance();
            apiService = Client.getClient(ChaineConnexion.getAdresseURLGoogleAPI()).create(APIService.class);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog1.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            registerGoogleFirebase(nom, prenom, genre,
                    tel,  cni, sessioncompteValue,
                    adresse, idcarte, idcategorieValue,
                    "sm" + tel + "@smopaye.cm", tel, "default", "offline", typeabon, result);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog1.dismiss();
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
            finish();
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            startActivity(intent);
            finish();
            return true;
        }

        if(id == R.id.modifierCompte){
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            intent.putExtra("telephone", uniquser);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private void registerGoogleFirebase(final String nom1, final String prenom1, final String sexe1,
                                        final String tel1, final String cni1, final String session1,
                                        final String adresse1, final String id_carte1, final String typeUser1,
                                        String email1, String password1, final String imageURL1, final String status1, final String abonnement1, final String f1)
    {

        auth.createUserWithEmailAndPassword(email1, password1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("nom", nom1);
                            hashMap.put("prenom", prenom1);
                            hashMap.put("sexe", sexe1);
                            hashMap.put("tel", tel1);
                            hashMap.put("cni", cni1);
                            hashMap.put("session", session1);
                            hashMap.put("adresse", adresse1);
                            hashMap.put("id_carte", id_carte1);
                            hashMap.put("typeUser", typeUser1);
                            hashMap.put("imageURL", imageURL1);
                            hashMap.put("status", status1);
                            hashMap.put("search", nom1.toLowerCase());
                            hashMap.put("abonnement", abonnement1);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SouscriptionUploadIMGidCard.this, "Opération Réussie", Toast.LENGTH_SHORT).show();

                                    /////////////////////SERVICE GOOGLE FIREBASE CLOUD MESSAGING///////////////////////////

                                    //SERVICE GOOGLE FIREBASE

                                    Query query = FirebaseDatabase.getInstance().getReference("Users")
                                            .orderByChild("id_carte")
                                            .equalTo(id_carte1);

                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.exists()){
                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                    User user = userSnapshot.getValue(User.class);
                                                    if (user.getId_carte().equals(id_carte1)) {
                                                        RemoteNotification(user.getId(), user.getPrenom(), "Souscription", f1, "success");
                                                        //Toast.makeText(RetraitAccepteur.this, "CARTE TROUVE", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(SouscriptionUploadIMGidCard.this, "Ce numéro de carte n'existe pas", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                            else{
                                                Toast.makeText(SouscriptionUploadIMGidCard.this, "Impossible d'envoyer votre Notification", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                    ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                                    dbHandler = new DbHandler(getApplicationContext());
                                    dbUser = new DbUser(getApplicationContext());
                                    aujourdhui = new Date();
                                    shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                                    //////////////////////////////////NOTIFICATIONS////////////////////////////////
                                    LocalNotification("Souscription", f1);
                                    dbHandler.insertUserDetails("Souscription",f1, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


                                    ////////////////////INSERTION DES DONNEES UTILISATEURS DANS LA BD LOCALE/////////////////////////
                                    dbUser.insertInfoUser(nom1, prenom1, sexe1,
                                            tel1, cni1, session1,
                                            adresse1, id_carte1, typeUser1,
                                            "default", "offline" , abonnement1, shortDateFormat.format(aujourdhui));


                                    String num_carte = id_carte1;

                                    View view = LayoutInflater.from(SouscriptionUploadIMGidCard.this).inflate(R.layout.alert_dialog_success, null);
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                                    ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                                    title.setText(getString(R.string.information));
                                    imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                    statutOperation.setText(f1);
                                    build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(SouscriptionUploadIMGidCard.this, QRCodeShow.class);
                                            intent.putExtra("id_carte", "E-ZPASS" +num_carte + getsecurity_keys());
                                            intent.putExtra("nom_prenom", nom1 + " " + prenom1);
                                            startActivity(intent);
                                        }
                                    });
                                    build_error.setCancelable(false);
                                    build_error.setView(view);
                                    build_error.show();

                                    /*nom.setText("");
                                    prenom.setText("");
                                    telephone.setText("");
                                    cni.setText("");
                                    adresse.setText("");
                                    numCarte.setText("");*/
                                }
                            });
                        }
                        else{

                            ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
                            dbHandler = new DbHandler(getApplicationContext());
                            aujourdhui = new Date();
                            shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

                            //////////////////////////////////NOTIFICATIONS////////////////////////////////
                            LocalNotification("Souscription", getString(R.string.impossibleRegister));
                            dbHandler.insertUserDetails("Souscription",getString(R.string.impossibleRegister), "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

                            Toast.makeText(SouscriptionUploadIMGidCard.this, getString(R.string.impossibleRegister), Toast.LENGTH_SHORT).show();
                            View view = LayoutInflater.from(SouscriptionUploadIMGidCard.this).inflate(R.layout.alert_dialog_success, null);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
                            ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                            title.setText(getString(R.string.information));
                            imageButton.setImageResource(R.drawable.ic_cancel_black_24dp);
                            statutOperation.setText(getString(R.string.impossibleRegister));
                            build_error.setPositiveButton("OK", null);
                            build_error.setCancelable(false);
                            build_error.setView(view);
                            build_error.show();
                        }
                    }
                });
    }

    private void RemoteNotification(final String receiver, final String username, final String title, final String message, final String statut_notif){

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(fuser.getUid(), R.drawable.logo2, username + ": " + message, title, receiver, statut_notif);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(SouscriptionUploadIMGidCard.this, "Failed", Toast.LENGTH_SHORT).show();
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


    public void LocalNotification(String titles, String subtitles){

        ///////////////DEBUT NOTIFICATIONS///////////////////////////////
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notif_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notif_expanded);

        Intent clickIntent = new Intent(getApplicationContext(), NotifReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, clickIntent, 0);

        collapsedView.setTextViewText(R.id.text_view_collapsed_1, titles);
        collapsedView.setTextViewText(R.id.text_view_collapsed_2, subtitles);

        expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.logo2);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo2)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
    }




}
