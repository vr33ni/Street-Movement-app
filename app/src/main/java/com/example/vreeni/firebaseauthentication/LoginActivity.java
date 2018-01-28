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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //REALTIME DATABASE
    private DatabaseReference mDatabase;
    private DatabaseReference users;
    private DatabaseReference user;
    private DatabaseReference userID;

    //CLOUD FIRESTORE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //everything for firebase
        //dont need: mDatabase = FirebaseDatabase.getInstance().getReference();
        //

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //user already signed in
            Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());
            //if user is authenticated, check if he is also stored in the database
            //User currUser = new... now, User declared as a class variable
            User currUser = new User();
            //currUser.updateRealTimeDatabase();
            currUser.checkFireStoreDatabase();

            //start main activity with nav. drawer and fragments
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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

                User currUser = new User();
               // currUser.updateRealTimeDatabase();
                currUser.checkFireStoreDatabase();



                //start main activity including the navigation drawer and fragments
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                //user not authenticated, do not log in
                Log.d("AUTH", "NOT AUTHENTICATED");
            }
        }
    }


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
                    //to prevent problems with the back button when going from main to login activity
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
                                //.setPrivacyPolicyUrl()
                                .build(),
                        RC_SIGN_IN);
            }
        }


    }

}

