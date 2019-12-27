package com.ezpass.smopaye_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Slide3_offre_smopaye extends AppCompatActivity {


    private TextView btnCmdTPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide3_offre_smopaye);



        btnCmdTPE = (TextView) findViewById(R.id.btnCmdTPE);
        btnCmdTPE.setMovementMethod(LinkMovementMethod.getInstance());

        /*btnCmdTPE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Slide3_offre_smopaye.this, "bonjour", Toast.LENGTH_LONG).show();
            }
        });*/
    }

}
