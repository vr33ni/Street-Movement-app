package com.example.vreeni.StreetMovementApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.AGE;
import static com.example.vreeni.StreetMovementApp.User.EMAIL;
import static com.example.vreeni.StreetMovementApp.User.FULLNAME;
import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFOUTDOORWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.NATIONALITY;
import static com.example.vreeni.StreetMovementApp.User.NICKNAME;
import static com.example.vreeni.StreetMovementApp.User.STATUS;
import static com.example.vreeni.StreetMovementApp.User.WARMUPSCOMPLETED;
import static com.example.vreeni.StreetMovementApp.User.WARMUPSSKIPPED;
import static com.example.vreeni.StreetMovementApp.User.WORKOUTSCOMPLETED;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "LoginActivity: ";

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    //CLOUD FIRESTORE
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //user already signed in
            Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());

            //if user is authenticated, check if he is also stored in the database
            checkIfExists();

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
                //registration successful > login user
                Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());

                //if registration worked, handle the login process

                checkIfExists();

                //start main activity including the navigation drawer and fragments
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                //registration failed > user not authenticated, do not log in
                Log.d("AUTH", "NOT AUTHENTICATED");
            }
        }
    }


    @Override
    public void onClick(View v) {
        //not automatically login >
        if (v.getId() == R.id.pleaseLoginToContinue) {
            firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                //user already signed in
                Log.d("AUTH", firebaseAuth.getCurrentUser().getEmail());

                //start main activity with nav. drawer and fragments
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //to prevent problems with the back button when going from main to login activity
                startActivity(intent);

            } else {
                //"start registration process"
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

    public void checkIfExists() {
        DocumentReference docRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "User already exists in database");
                        //perform updates on fields
                    } else {
                        Log.d(TAG, "Creating user in database...");
                        handleLogin();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public void handleLogin() {
        final String loginEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //Google Mail exception: Name cannot be retrieved from firebaseAuth
        final String loginName;
                    if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()!=null){
                        //set name to the name retrieved from firebaseAuth
                        loginName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    }
                    else {
                        //if name cannot be retrieved from firebaseAuth = Google Accounts => make name changeable?
                        loginName = "User";
                    }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                Map<String, Object> newEntry = new HashMap<String, Object>();
                newEntry.put(FULLNAME, loginName);
                newEntry.put(EMAIL, loginEmail);
                newEntry.put(NICKNAME, "-");
                newEntry.put(AGE, "-");
                newEntry.put(NATIONALITY, "-");
                newEntry.put(STATUS, "Baby monkey");
                newEntry.put(WORKOUTSCOMPLETED, 0);
                newEntry.put(WARMUPSSKIPPED, 0);
                newEntry.put(WARMUPSCOMPLETED, 0);
                newEntry.put(LISTOFHOMEWORKOUTS, new ArrayList<Object>());
                newEntry.put(LISTOFOUTDOORWORKOUTS, new ArrayList<Object>());
                userDocRef
                        .set(newEntry, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "New User Document has been saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "New User Document could not be saved");
                    }
                });

            }
}




