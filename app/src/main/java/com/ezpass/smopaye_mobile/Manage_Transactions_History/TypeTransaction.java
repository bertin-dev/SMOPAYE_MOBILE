package com.ezpass.smopaye_mobile.Manage_Transactions_History;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ezpass.smopaye_mobile.Constant;
import com.ezpass.smopaye_mobile.Manage_Apropos.Apropos;
import com.ezpass.smopaye_mobile.Manage_Tutoriel.TutorielUtilise;
import com.ezpass.smopaye_mobile.Manage_Update_ProfilUser.UpdatePassword;
import com.ezpass.smopaye_mobile.R;

public class TypeTransaction extends AppCompatActivity {

    //changement de couleur du theme
    private Constant constant;
    private SharedPreferences.Editor editor;
    private SharedPreferences app_preferences;
    private Toolbar toolbar;
    int appTheme;
    int themeColor;
    int appColor;

    private static final String TAG = "TypeTransaction";
    private Intent intent;
    private String typeHistoriqueTransaction;
    private String typeHistoriqueTransaction2;
    private RelativeLayout typeOperation1;
    private RelativeLayout typeOperation2;
    private TextView titleOperation1;
    private TextView titleOperation2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeTheme();
        setContentView(R.layout.activity_type_transaction);

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.historique));
        toolbar.setSubtitle(getString(R.string.ezpass));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        typeOperation1 = findViewById(R.id.typeOperation1);
        typeOperation2 = findViewById(R.id.typeOperation2);
        titleOperation1 = findViewById(R.id.titleOperation1);
        titleOperation2 = findViewById(R.id.titleOperation2);

        intent = getIntent();
        typeHistoriqueTransaction = intent.getStringExtra("typeHistoriqueTransaction");
        typeHistoriqueTransaction2 = intent.getStringExtra("typeHistoriqueTransaction2");

        titleOperation1.setText(typeHistoriqueTransaction.toLowerCase());
        titleOperation2.setText(typeHistoriqueTransaction2.toLowerCase());


        typeOperation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", typeHistoriqueTransaction);
                intent.putExtra("typeHistoriqueTransaction2", "");
                startActivity(intent);
            }
        });

        typeOperation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoriqueTransactions.class);
                intent.putExtra("typeHistoriqueTransaction", typeHistoriqueTransaction2);
                intent.putExtra("typeHistoriqueTransaction2", "");
                startActivity(intent);
            }
        });

    }

    private void changeTheme() {
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0) {
            setTheme(Constant.theme);
        } else if (appTheme == 0) {
            setTheme(Constant.theme);
        } else {
            setTheme(appTheme);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    private void changeColorWidget() {

        if (Constant.color == getResources().getColor(R.color.colorPrimaryRed)) {
            toolbar.setBackground(ContextCompat.getDrawable(this, R.color.colorPrimaryDarkRed));

            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkRed));
        } else {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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
        }

        if (id == R.id.tuto) {
            Intent intent = new Intent(getApplicationContext(), TutorielUtilise.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}