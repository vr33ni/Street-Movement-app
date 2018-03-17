package com.example.vreeni.StreetMovementApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//make this launcher activity
public class StartScreenActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //add listener to loginToContinue button
        findViewById(R.id.pleaseLoginToContinue).setOnClickListener(this); //maybe not necessary at this point any more, as after login, the user is redirected directly to the street movement app view
    }


    @Override
    public void onClick(View v) {
        //create an extra class called App state handling log in and log out
        if (v.getId() == R.id.signin) {
            Intent intent = new Intent(StartScreenActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}

