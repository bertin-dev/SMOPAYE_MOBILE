package com.ezpass.smopaye_mobile.Assistance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ezpass.smopaye_mobile.R;

public class DetailsAgenceSmopaye extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_agence_smopaye);


        getSupportActionBar().setTitle(getString(R.string.details));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
