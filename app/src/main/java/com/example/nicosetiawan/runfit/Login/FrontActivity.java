package com.example.nicosetiawan.runfit.Login;


import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.nicosetiawan.runfit.R;



public class FrontActivity extends AppCompatActivity {

    private TextView login;
    private Button signup;
    TextView txt;
    Typeface tp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        txt = findViewById(R.id.logo1);

        tp = Typeface.createFromAsset(this.getAssets(), "fonts/airstrikehalf.ttf");
        txt.setTypeface(tp);

        login = findViewById(R.id.txtlogin);
        signup =  findViewById(R.id.btn_signup) ;

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrontActivity.this,RegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrontActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });



    }



}
