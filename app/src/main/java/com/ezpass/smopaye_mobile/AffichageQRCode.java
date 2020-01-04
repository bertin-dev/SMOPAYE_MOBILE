package com.ezpass.smopaye_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Apropos.Apropos;
import com.ezpass.smopaye_mobile.vuesUtilisateur.ModifierCompte;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AffichageQRCode extends AppCompatActivity {

    private ImageView qrcode;
    private TextView card_number;

    String fil = "tmp_data_user";
    int c;
    String temp_ = "";

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affichage_qrcode);

        getSupportActionBar().setTitle("Lecteur QR Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        card_number = (TextView) findViewById(R.id.card_number);
        qrcode = (ImageView) findViewById(R.id.qrcode);


        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = openFileInput(fil);
            while ((c = fIn.read()) != -1){
                temp_ = temp_ + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        String[] parts = temp_.split("-");
        String cardNumber = parts[10]; // 12345678


        if(cardNumber != null && !cardNumber.isEmpty()){
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(cardNumber, BarcodeFormat.QR_CODE, 500, 500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrcode.setImageBitmap(bitmap);
            } catch (WriterException e){
                e.printStackTrace();
            }
        } else{
            Toast.makeText(this, "Une Erreur est survenue", Toast.LENGTH_SHORT).show();
        }

        // Display saved image uri to TextView
        card_number.setText(cardNumber);

    }

    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_qrcode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.partage){
            //startShare();
            shareBitmap(viewToBitmap(qrcode, qrcode.getWidth(), qrcode.getHeight()));
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.apropos) {
            Intent intent = new Intent(getApplicationContext(), Apropos.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if(id == R.id.modifierCompte){
            Intent intent = new Intent(getApplicationContext(), ModifierCompte.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    //methode qui fonctionnement uniquement avec les versions inférieur à Android 7.0
    public void startShare() {
        Bitmap bitmap = viewToBitmap(qrcode, qrcode.getWidth(), qrcode.getHeight());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "ImageDemo.jpg");
        try{
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/ImageDemo.jpg"));
        startActivity(Intent.createChooser(shareIntent, "Smopaye Partage QR Code"));
    }

    private static Bitmap viewToBitmap(View view, int width, int height){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    private void shareBitmap(Bitmap bitmap) {

        /*final String shareText = " E-ZPASS by" + " "
                + getString(R.string.app_name) + " developed by "
                + "https://play.google.com/store/apps/details?id=" + getPackageName() + ": \n\n";*/


        final String shareText = " E-ZPASS by" + " "
                + getString(R.string.app_name) + ": \n\n";

        try {
            File file = new File(this.getExternalCacheDir(), "share.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "E-ZPASS Partage via"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
