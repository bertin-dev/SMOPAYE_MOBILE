package com.ezpass.smopaye_mobile.vuesUtilisateur;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TutorielUtilise;

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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SouscriptionUploadIMGidCard extends AppCompatActivity {

    Button GetImageFromGalleryButton, UploadImageOnServerButton;
    ImageView ShowSelectedImageRecto, ShowSelectedImageVerso;
    EditText imageName;
    Bitmap FixBitmap, FixBitmap2;
    String ImageTag = "image_tag" ;
    String ImageName = "image_data" ;
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    String GetImageNameFromEditText;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;

    String nom, prenom, genre, tel, cni, sessioncompte, adresse, idcarte, idcategorie, typeabon, uniquser;
    LinearLayout imgCardVerso;
    RelativeLayout dividerBarUpload;
    TextView infoNom, infoPrenom, infoCni;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souscription_upload_imgid_card);


        getSupportActionBar().setTitle("Souscription Etape 2");
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




        GetImageFromGalleryButton = (Button)findViewById(R.id.buttonSelect);
        UploadImageOnServerButton = (Button)findViewById(R.id.buttonUpload);
        ShowSelectedImageRecto = (ImageView)findViewById(R.id.imageViewRecto);
        ShowSelectedImageVerso = (ImageView)findViewById(R.id.imageViewVerso);
        imgCardVerso = (LinearLayout) findViewById(R.id.imgCardVerso);
        infoNom = (TextView) findViewById(R.id.infoNom);
        infoPrenom = (TextView) findViewById(R.id.infoPrenom);
        infoCni = (TextView) findViewById(R.id.infoCni);
        dividerBarUpload = (RelativeLayout) findViewById(R.id.dividerBarUpload);
        //imageName=(EditText)findViewById(R.id.imageName);


        String[] parts = cni.split("-");
        if(parts[0].toLowerCase().equalsIgnoreCase("cni"))
            infoCni.setText(getString(R.string.infoIdCard));
        else
            infoCni.setText(getString(R.string.pays) +", " + parts[0]);

        infoNom.setText(nom);
        infoPrenom.setText(prenom);

        byteArrayOutputStream = new ByteArrayOutputStream();
        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();


            }
        });


        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = imageName.getText().toString();

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
                ShowSelectedImageRecto.setImageBitmap(FixBitmap);
                imgCardVerso.setVisibility(View.VISIBLE);
                dividerBarUpload.setVisibility(View.VISIBLE);
                return;
            }


            if(ShowSelectedImageRecto.getDrawable() != null){
                FixBitmap2 = (Bitmap) data.getExtras().get("data");
                ShowSelectedImageVerso.setImageBitmap(FixBitmap2);
                UploadImageOnServerButton.setVisibility(View.VISIBLE);
                imgCardVerso.setVisibility(View.VISIBLE);
                dividerBarUpload.setVisibility(View.VISIBLE);
                GetImageFromGalleryButton.setVisibility(View.GONE);
            }

        }
    }


    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(SouscriptionUploadIMGidCard.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(SouscriptionUploadIMGidCard.this,string1,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageName, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest("http://192.168.43.86/projects/AndroidUpload/upload-image-to-server.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

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
}
