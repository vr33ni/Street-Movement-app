package com.example.vreeni.firebaseauthentication;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //everything for firebase
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //user already signed in
            Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());
            //start main activity with nav. drawer and fragments
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //String name = firebaseAuth.getCurrentUser().getDisplayName();
            //intent.putExtra("display-name", name); //Put your id to your next Intent
            startActivity(intent);

        } else {
            //"register"
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppThemeFirebaseAuth) // somehow change color of app bar

                            .setAvailableProviders(
                                    //this leads to the list of login options. possible work on the layout of this display
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
        }
        // add please login to continue button here
        findViewById(R.id.pleaseLoginToContinue).setOnClickListener(this); //maybe not necessary at this point any more, as after login, the user is redirected directly to the street movement app view
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                //login user
                Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());

                //start main activity including the navigation drawer and fragments
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                //user not authenticated, do not log in
                Log.d("AUTH", "NOT AUTHENTICATED");
            }
        }
    }


    //not really needed anymore, cause logout is not handled from the login activity, but from main activity without View.OnClickListener
    // interface method required by interface "View.OnClickListener"

    @Override
    public void onClick(View v) {
        //create an extra class called App state handling log in and log out
        if (v.getId() == R.id.pleaseLoginToContinue) {
            firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                //user already signed in
                Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());

                String name = firebaseAuth.getCurrentUser().getDisplayName();
                final TextView profile = (TextView) findViewById(R.id.profile_section);
                profile.setText(name);
                //start main activity with nav. drawer and fragments
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            } else {
                //"register"
                startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setTheme(R.style.AppThemeFirebaseAuth) // somehow change color of app bar
                                .setAvailableProviders(
                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))

                                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                                .build(),
                        RC_SIGN_IN);
            }
        }
    }

}

