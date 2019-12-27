package com.ezpass.smopaye_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

public class HideShowAndroidEditTextPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_show_android_edit_text_password);

        ShowHidePasswordEditText showHidePasswordEditText = (ShowHidePasswordEditText) findViewById(R.id.android_hide_show_edittext_password);
        ShowHidePasswordEditText showHidePasswordEditText1 = (ShowHidePasswordEditText) findViewById(R.id.hide_show_edittext_password);
        ShowHidePasswordEditText showHidePasswordEditText3 = (ShowHidePasswordEditText) findViewById(R.id.hide_show_android_edittext_password);
    }
}
