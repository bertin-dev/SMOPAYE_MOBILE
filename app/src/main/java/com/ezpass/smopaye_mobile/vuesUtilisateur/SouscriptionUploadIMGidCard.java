package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbHandler;
import com.ezpass.smopaye_mobile.DBLocale_Notifications.DbUser;
import com.ezpass.smopaye_mobile.NotifApp;
import com.ezpass.smopaye_mobile.NotifReceiver;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.ezpass.smopaye_mobile.TutorielUtilise;
import com.ezpass.smopaye_mobile.checkInternetDynamically.ConnectivityReceiver;
import com.ezpass.smopaye_mobile.checkInternetDynamically.OfflineActivity;
import com.ezpass.smopaye_mobile.web_service.ApiService;
import com.ezpass.smopaye_mobile.web_service.RetrofitBuilder;
import com.ezpass.smopaye_mobile.web_service_access.ApiError;
import com.ezpass.smopaye_mobile.web_service_access.TokenManager;
import com.ezpass.smopaye_mobile.web_service_access.Utils_manageError;
import com.ezpass.smopaye_mobile.web_service_response.AllMyResponse;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpass.smopaye_mobile.NotifApp.CHANNEL_ID;

public class SouscriptionUploadIMGidCard extends AppCompatActivity
                                         implements ConnectivityReceiver.ConnectivityReceiverListener{

    private static final String TAG = "SouscriptionUploadIMGid";
    private Button GetImageFromGalleryButton, UploadImageOnServerButton;
    private ImageView ShowSelectedImageRecto, ShowSelectedImageVerso;
    private Bitmap FixBitmap, FixBitmap2;
    private String ImageTagRecto = "nom_piece_recto" ;
    private String ImageNameRecto = "piece_recto" ;
    private String ImageTagVerso = "nom_piece_verso" ;
    private String ImageNameVerso = "piece_verso" ;
    private ProgressDialog progressDialog, progressDialog2;
    private ByteArrayOutputStream byteArrayOutputStream1, byteArrayOutputStream2;
    private byte[] byteArray1, byteArray2;
    private String ConvertImageRecto, ConvertImageVerso ;
    private HttpURLConnection httpURLConnection ;
    private URL url;
    private OutputStream outputStream;
    private BufferedWriter bufferedWriter ;
    private int RC ;
    private BufferedReader bufferedReader ;
    private StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;

    private String nom, prenom, genre, tel, cni, sessioncompte, adresse, idcarte, idcategorie, typeabon, uniquser, GetImageNameFromRectoIdCard, GetImageNameFromVersoIdCard, sessioncompteValue, idcategorieValue;
    private LinearLayout imgCardVerso;
    private RelativeLayout dividerBarUpload;
    private TextView infoNom, infoPrenom, infoCni;
    private AlertDialog.Builder build_error;
    private String typePieceJusti = "";

    //BD LOCALE
    private DbHandler dbHandler;
    private DbUser dbUser;
    private Date aujourdhui;
    private DateFormat shortDateFormat;

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private TokenManager tokenManager;
    private Call<AllMyResponse> call;



    @BindView(R.id.authWindows)
    LinearLayout authWindows;
    @BindView(R.id.internetIndisponible)
    LinearLayout internetIndisponible;
    @BindView(R.id.conStatusIv)
    ImageView conStatusIv;
    @BindView(R.id.titleNetworkLimited)
    TextView titleNetworkLimited;
    @BindView(R.id.msgNetworkLimited)
    TextView msgNetworkLimited;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souscription_upload_imgid_card);


        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.SouscriptionEtapeFinale));
        toolbar.setSubtitle(getString(R.string.pjValid));
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
        idcategorie = intent.getStringExtra("IDCathegorie");
        uniquser = intent.getStringExtra("uniquser");
        sessioncompteValue = intent.getStringExtra("sessioncompteValue");
        idcategorieValue = intent.getStringExtra("IDCathegorieValue");


        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));




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
        typePieceJusti = parts[0].toLowerCase();
        if(typePieceJusti.equalsIgnoreCase("cni"))
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

                    if(typePieceJusti.equalsIgnoreCase("passeport")){

                        FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        ShowSelectedImageRecto.setImageBitmap(FixBitmap);

                        UploadImageOnServerButton.setVisibility(View.VISIBLE);
                        GetImageFromGalleryButton.setVisibility(View.GONE);

                    } else{
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

            if(typePieceJusti.equalsIgnoreCase("passeport")){

                FixBitmap = (Bitmap) data.getExtras().get("data");
                ShowSelectedImageRecto.setImageBitmap(FixBitmap);

                UploadImageOnServerButton.setVisibility(View.VISIBLE);
                GetImageFromGalleryButton.setVisibility(View.GONE);
            } else {

                if (ShowSelectedImageRecto.getDrawable() == null) {
                    FixBitmap = (Bitmap) data.getExtras().get("data");
                    //debut compression
                    //FixBitmap = Bitmap.createScaledBitmap(FixBitmap, 60, 60, true);
                    //fin compression
                    ShowSelectedImageRecto.setImageBitmap(FixBitmap);
                    imgCardVerso.setVisibility(View.VISIBLE);
                    dividerBarUpload.setVisibility(View.VISIBLE);
                    return;
                }


                if (ShowSelectedImageRecto.getDrawable() != null) {
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
            progressDialog2.setMessage(getString(R.string.chargement));
            progressDialog2.setTitle(getString(R.string.etape1EnvoiDimages));
            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog2.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog2.setMessage(values[0] + getString(R.string.operationEncours));
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
            //String FinalData = imageProcessClass.ImageHttpRequest1("https://management.device.domaineteste.space.smopaye.fr/upload.php", HashMapParams);
            String FinalData = imageProcessClass.ImageHttpRequest1("https://ms.smp.net.smopaye.fr/upload.php", HashMapParams);
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
                Toast.makeText(getApplicationContext(), getString(R.string.ErreurSurvenuePendantLupload), Toast.LENGTH_LONG).show();
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
    private class ImageProcess1{

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
                progressDialog = ProgressDialog.show(SouscriptionUploadIMGidCard.this, getString(R.string.etape2EnvoiDesDonnees), getString(R.string.connexionServeurSmopaye),true,true);
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
                Toast.makeText(SouscriptionUploadIMGidCard.this, getString(R.string.operationDone), Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                auto_registerInSmopayeServer(prenom, nom, genre.toUpperCase(), adresse, idcategorie, uniquser, sessioncompte, cni, tel, GetImageNameFromRectoIdCard + ".jpg", GetImageNameFromVersoIdCard + ".jpg" );
                return null;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    private void auto_registerInSmopayeServer(String prenom, String nom, String genre, String adresse, String idcategorie, String created_by, String sessioncompte, String cni, String tel, String imgName_recto, String imgName_verso) {

        call = service.autoregister(prenom, nom, genre, adresse, idcategorie, created_by, sessioncompte, cni.toLowerCase(), tel, imgName_recto, imgName_verso);
        call.enqueue(new Callback<AllMyResponse>() {
            @Override
            public void onResponse(Call<AllMyResponse> call, Response<AllMyResponse> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()){

                    if(response.body().isSuccess()){
                        //tokenManager.saveToken(response.body());
                        successResponse("");
                    } else {
                        errorResponse("");
                    }

                } else{

                    if(response.code() == 401){
                        ApiError apiError = Utils_manageError.convertErrors(response.errorBody());
                        Toast.makeText(SouscriptionUploadIMGidCard.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        errorResponse("");
                    }
                }

            }

            @Override
            public void onFailure(Call<AllMyResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());

                /*Vérification si la connexion internet accessible*/
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                if(!(activeInfo != null && activeInfo.isConnected())){
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    Toast.makeText(SouscriptionUploadIMGidCard.this, getString(R.string.pasDeConnexionInternet), Toast.LENGTH_SHORT).show();
                }
                /*Vérification si le serveur est inaccessible*/
                else{
                    authWindows.setVisibility(View.GONE);
                    internetIndisponible.setVisibility(View.VISIBLE);
                    conStatusIv.setImageResource(R.drawable.ic_action_limited_network);
                    titleNetworkLimited.setText(getString(R.string.connexionLimite));
                    //msgNetworkLimited.setText();
                    Toast.makeText(SouscriptionUploadIMGidCard.this, getString(R.string.connexionLimite), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void successResponse(String response) {
        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.souscription), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.souscription), response, "0", R.drawable.ic_notifications_black_48dp, shortDateFormat.format(aujourdhui));


        View view = LayoutInflater.from(SouscriptionUploadIMGidCard.this).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_check_circle_black_24dp);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();


    }

    private void errorResponse(String response){

        //////////////////////////////////NOTIFICATIONS LOCALE////////////////////////////////
        LocalNotification(getString(R.string.souscription), response);

        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        dbHandler = new DbHandler(getApplicationContext());
        aujourdhui = new Date();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(getString(R.string.souscription), response, "0", R.drawable.ic_notifications_red_48dp, shortDateFormat.format(aujourdhui));

        View view = LayoutInflater.from(SouscriptionUploadIMGidCard.this).inflate(R.layout.alert_dialog_success, null);
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

        expandedView.setImageViewResource(R.id.image_view_expanded, R.mipmap.logo_official);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo_official)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(new Random().nextInt(), notification);
        ////////////////////////////////////FIN NOTIFICATIONS/////////////////////
    }




    /**
     * checkNetworkConnectionStatus() méthode permettant de verifier si la connexion existe ou si le serveur est accessible
     * @since 2019
     * */
    @OnClick(R.id.btnReessayer)
    void checkNetworkConnectionStatus(){
        /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if(activeInfo != null && activeInfo.isConnected()){
            dialog = ProgressDialog.show(this, getString(R.string.connexion), getString(R.string.encours), true);
            dialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                    recreate();
                    //finish();
                    //startActivity(getIntent());
                }
            }, 3000); // 3000 milliseconds delay

        } else{
            progressDialog.dismiss();
            authWindows.setVisibility(View.GONE);
            internetIndisponible.setVisibility(View.VISIBLE);
            Toast.makeText(Login.this, getString(R.string.connexionIntrouvable), Toast.LENGTH_SHORT).show();
        }*/

        boolean isConnected = ConnectivityReceiver.isConnected();

        showSnackBar(isConnected);

        if(!isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {
        Intent intent = new Intent(this, OfflineActivity.class);
        startActivity(intent);
    }

    private void showSnackBar(boolean isConnected) {
        String message;
        int color = Color.WHITE;
        Snackbar snackbar;
        View view;

        if(isConnected){
            message = getString(R.string.networkOnline);
            snackbar = Snackbar.make(findViewById(R.id.souscription_upload_img), message, Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setBackgroundColor(Color.parseColor("#039BE5"));
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        } else{
            message = getString(R.string.networkOffline);
            snackbar = Snackbar.make(findViewById(R.id.souscription_upload_img), message, Snackbar.LENGTH_INDEFINITE);
            view = snackbar.getView();
            TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            textView.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            }
        }
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        /*if(!isConnected){
            changeActivity();
        }*/
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        //register connection status listener
        NotifApp.getInstance().setConnectivityListener(this);
    }



    /**
     * onDestroy() methode Callback qui permet de détruire une activity et libérer l'espace mémoire
     * @since 2020
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
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

}
