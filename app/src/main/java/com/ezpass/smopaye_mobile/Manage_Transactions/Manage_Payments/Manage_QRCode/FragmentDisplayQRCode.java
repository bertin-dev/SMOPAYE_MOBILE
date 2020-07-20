package com.ezpass.smopaye_mobile.Manage_Transactions.Manage_Payments.Manage_QRCode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;
import com.ezpass.smopaye_mobile.TranslateItem.LocaleHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static com.ezpass.smopaye_mobile.ChaineConnexion.getsecurity_keys;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDisplayQRCode extends Fragment {

    private static final String TAG = "FragmentDisplayQRCode";
    private ImageView qrcode;
    private TextView card_number;


    int c;
    private String file2 = "tmp_card_number";
    private String temp_card = "";

    private Bitmap bitmap;
    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    private int appTheme;
    private int themeColor;
    private int appColor;
    private Button btn_shared;

    public FragmentDisplayQRCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        changeTheme();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_q_r_code, container, false);

        card_number = (TextView) view.findViewById(R.id.card_number);
        qrcode = (ImageView) view.findViewById(R.id.qrcode);
        btn_shared = (Button) view.findViewById(R.id.btn_shared);


        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////

        readTempCardInFile();
        String cardNumber = temp_card; // 12345678


        String carteCrypte = "E-ZPASS" + cardNumber.toLowerCase() + getsecurity_keys();

        if(!carteCrypte.isEmpty()){
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(carteCrypte, BarcodeFormat.QR_CODE, 500, 500);
                /*BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);*/
                bitmap = createBitmapCustomized(bitMatrix);
                //qrcode.setImageBitmap(bitmap);
            } catch (WriterException e){
                e.printStackTrace();
            }
        } else{
            Toast.makeText(getContext(), getString(R.string.erreurSurvenue1), Toast.LENGTH_SHORT).show();
        }

       /* //cryptage du numero de carte
        byte[] bytes = cardNumber.getBytes();
        String mdp = "E-ZPASS by " + getString(R.string.app_name) +  "/" + getPackageName() + "/123456789";
        HashMap<String, byte[]> carteCrypte = encryptBytes(bytes, mdp);
        Toast.makeText(this, carteCrypte.toString(), Toast.LENGTH_SHORT).show();

        if(carteCrypte != null && !carteCrypte.isEmpty()){
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(carteCrypte.toString(), BarcodeFormat.QR_CODE, 500, 500);
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
        card_number.setText(String.valueOf(carteCrypte));*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeColorWidget();
        }


        btn_shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    shareBitmap(viewToBitmap(qrcode, qrcode.getWidth(), qrcode.getHeight()));
                } else {
                    startShare();
                }

            }
        });
        return view;
    }


    private void readTempCardInFile() {
        /////////////////////////////////LECTURE DES CONTENUS DES FICHIERS////////////////////
        try{
            FileInputStream fIn = getActivity().getApplication().openFileInput(file2);
            while ((c = fIn.read()) != -1){
                temp_card = temp_card + Character.toString((char)c);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap createBitmapCustomized(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                //pixels[offset + x] = matrix.get(x, y) ?
                //ResourcesCompat.getColor(getResources(),R.color.bgColorStandard,null) :WHITE;

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.ezpass_codeqr);
        qrcode.setImageBitmap(mergeBitmaps(overlay,bitmap));

        return bitmap;
    }


    private Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth  - overlay.getWidth()) /2;
        int centreY = (canvasHeight - overlay.getHeight()) /2 ;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
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
        startActivity(Intent.createChooser(shareIntent, getString(R.string.smopayePartarge)));
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
            File file = new File(getActivity().getExternalCacheDir(), "share.png");
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
            startActivity(Intent.createChooser(intent, getString(R.string.smopayePartargeEZPASS)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {
        if(Constant.color == getResources().getColor(R.color.colorPrimaryRed)){
            qrcode.setBackground(ContextCompat.getDrawable(getContext(), R.color.colorPrimaryRed));
        }
    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

}
