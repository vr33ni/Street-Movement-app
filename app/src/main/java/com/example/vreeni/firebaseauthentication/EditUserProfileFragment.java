package com.example.vreeni.firebaseauthentication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.firebaseauthentication.User.AGE;
import static com.example.vreeni.firebaseauthentication.User.NATIONALITY;
import static com.example.vreeni.firebaseauthentication.User.NICKNAME;

public class EditUserProfileFragment extends Fragment implements View.OnClickListener {
    private String TAG = "Edit User Profile ";

    private DatabaseReference mDatabase;

    private Button btnSaveProfile;

    private EditText editUsername;
    private EditText editAge;
    private EditText editNationality;

    private String username_input;
    private String age_input;
    private String nationality_input;




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Button to save the profile
        btnSaveProfile = (Button) view.findViewById(R.id.save_user_info);
        btnSaveProfile.setOnClickListener(this);

        //field that allows changes on the nick name
        editUsername = (EditText) view.findViewById(R.id.profile_section_edit_nickname);

        //field that allows you to enter the correct age
        editAge = (EditText) view.findViewById(R.id.profile_section_edit_age);

        //field that allows you to enter your nationality
        editNationality = (EditText) view.findViewById(R.id.profile_section_edit_nationality);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile_edit, container, false);
    }


    @Override
    public void onClick(View v) {
        //save information and return to userProfileFraagment

        username_input = editUsername.getText().toString().trim();
        age_input = editAge.getText().toString().trim();
        nationality_input = editNationality.getText().toString().trim();

        //update Firestore data
        updateFireStoreData(username_input, age_input, nationality_input);
        //update realtime database
        //updateUserExtras(username_input, age_input, nationality_input);

        Fragment fragment = null;
        //if the button representing the "train now or create workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.save_user_info) {
            fragment = new UserProfileFragment();
        }
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    //update the user entered information to the database, if the strings arent empty
    public void updateFireStoreData(String nicknameUpdate, String ageUpdate, String nationalityUpdate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        //NOTE: Both .set(.., SetOptions.merge()) and .update perform the same action
        if (!nicknameUpdate.matches("")) {
            Map<String, Object> dataUpdate = new HashMap<String, Object>();
            dataUpdate.put(NICKNAME, nicknameUpdate);
            userDocRef
                    .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        if (!ageUpdate.matches("")) {
            userDocRef
                    .update(AGE, ageUpdate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        if (!nationalityUpdate.matches("")) {
            userDocRef
                    .update(NATIONALITY, nationalityUpdate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

        }
    }

}


/*
REALTIME DATABASE
    public void updateUserExtras(String username, String age, String nationality) {
        //get information from the two fields that the user can edit: nickname and age
        String nickname = username;
        String user_age = age;
        String user_nationality = nationality;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        //if user has entered a new value, update information
        if (!nickname.matches("")) {
            Map<String, Object> nicknameVal = new HashMap<String, Object>();
            nicknameVal.put("Nickname", nickname);
            mDatabase.child("Users").child(currUser.getDisplayName())
                    .updateChildren(nicknameVal);
        }
        //if user has entered a new value, update information
        if (!user_age.matches("")) {
            Map<String, Object> ageVal = new HashMap<String, Object>();
            ageVal.put("Age", user_age);
            mDatabase.child("Users").child(currUser.getDisplayName())
                    .updateChildren(ageVal);
        }
        //if user has entered a new value, update information
        if(!user_nationality.matches("")) {
            Map<String, Object> nationalityVal = new HashMap<String, Object>();
            nationalityVal.put("Nationality", user_nationality);
            mDatabase.child("Users").child(currUser.getDisplayName())
                    .updateChildren(nationalityVal);
        }
    }
*/