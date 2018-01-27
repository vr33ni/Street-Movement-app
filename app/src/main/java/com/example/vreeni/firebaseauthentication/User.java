package com.example.vreeni.firebaseauthentication;

import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vreee on 30/12/2017.
 */

//this class handles the creation of a user in the firebase Database
public class User extends AppCompatActivity { //change this to extends FIREBASEUser !!!

    //FIRESTORE
    public static final String AGE = "Age";
    public static final String EMAIL = "Email";
    public static final String FULLNAME = "Full name";
    public static final String NATIONALITY = "Nationality";
    public static final String NICKNAME = "Nickname";
    public static final String STATUS = "Status";

    private String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String userFullName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    public static final String TAG = User.class.getSimpleName();


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    protected void checkFireStoreDatabase() {
        // Create a new user with a first and last name
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usersDocRef = db.collection("Users").document(userFullName);

        if (usersDocRef != null) {
        } else {
            createNewEntry();
        }
    }

    public void createNewEntry() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usersDocRef = db.collection("Users").document(userFullName);
        Map<String, Object> userEntry;

        userEntry = new HashMap<>();
        userEntry.put(FULLNAME, userFullName);
        userEntry.put(EMAIL, userEmail);
        userEntry.put(NICKNAME, "-");
        userEntry.put(AGE, "-");
        userEntry.put(NATIONALITY, "-");
        userEntry.put(STATUS, "Baby monkey");
        db.document(userFullName).set(userEntry, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Document has been saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Document could not be saved");
            }
        });
    }

}


/*
    protected void updateRealTimeDatabase() {
        //whenever a new user object is created, it signifies the usage of the app by the current user
        //the creation of a user object also prompts the program to check if the current user already exists in the database
        //if not, create a new Database entry
        //OR: only create user object when it is actually a new user that will be stored in database? would be more logical
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersRef = mDatabase.getRef();
        if (usersRef != null) {
        } else {
            users = mDatabase.child("Users");
        }
        updateDatabaseEntry();
    }

    //CHECK IF USER EXISTS
    private void updateDatabaseEntry() {
        final DatabaseReference nameRef = usersRef.getRef();
        nameRef.addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.equals(getUsername())) {
                            //username already exists
                        } else {
                            //user doesnt exist yet
                            addNewUser();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }


    //method for saving user data in database if the entry doesnt exist yet

    private void addNewUser() {
        users = mDatabase.child("Users");
        if (getUserID() != null) {
            //create new child with the user name
            DatabaseReference newUser = users.child(getUsername());
            //create new child called user id and its value
            DatabaseReference userID = newUser.child("UserID");
            userID.setValue(getUserID());

            DatabaseReference displayname = newUser.child("User name");
            displayname.setValue(getUsername());

            DatabaseReference emailaddress = newUser.child(EMAIL);
            emailaddress.setValue(getEmail());

        }
    }

    */


