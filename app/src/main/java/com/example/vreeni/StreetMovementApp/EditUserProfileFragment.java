package com.example.vreeni.StreetMovementApp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.AGE;
import static com.example.vreeni.StreetMovementApp.User.NATIONALITY;
import static com.example.vreeni.StreetMovementApp.User.NICKNAME;

/**
 * Fragment handling edits to the user profile:
 * ==> possibility to change username, age and nationality and send the updates to the database
 */
public class EditUserProfileFragment extends Fragment implements View.OnClickListener {
    private String TAG = "Edit User Profile ";

    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText editUsername;
    private EditText editAge;
    private EditText editNationality;
    private EditText editDisplayName;


    public static EditUserProfileFragment newInstance() {
        final Bundle bundle = new Bundle();
        EditUserProfileFragment fragment = new EditUserProfileFragment();
//        bundle.putParcelable("UserData", user);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Button to save the profile
        Button btnSaveProfile = (Button) view.findViewById(R.id.save_user_info);
        btnSaveProfile.setOnClickListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName()==null) {
            view.findViewById(R.id.profile_section_setDisplayname).setVisibility(View.VISIBLE);
            editDisplayName = (EditText) view.findViewById(R.id.profile_section_edit_displayname);
        }

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


    /**
     * handling save button clicks
     * => once clicked, EditText input is trimmed and set as parameter in the updateFireStoreData method that is called
     * finally, the next fragment is loaded
     *
     * @param v referring to the Save-Button View that is being clicked
     */
    @Override
    public void onClick(View v) {
        //save information and return to userProfileFraagment

        String displayname_input = editDisplayName.getText().toString().trim();
        String username_input = editUsername.getText().toString().trim();
        String age_input = editAge.getText().toString().trim();
        String nationality_input = editNationality.getText().toString().trim();

        Log.d(TAG, "displayname " + displayname_input);
        //update Firestore data

        updateFireStoreData(displayname_input, username_input, age_input, nationality_input);
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


    /**
     * check if the parameters do not equal an empty string
     * => if not empty: create new hashmap that will contain the key value pair that is sent to the database
     *
     * @param displayname
     * @param nicknameUpdate    user input for the nickname
     * @param ageUpdate         user input for the age
     * @param nationalityUpdate user input for the nationality
     */
    //update the user entered information to the database, if the strings arent empty
    public void updateFireStoreData(String displayname, String nicknameUpdate, String ageUpdate, String nationalityUpdate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if ((displayname != null || !displayname.matches(""))) {
            Log.d(TAG, "updating displayname... " + displayname);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayname)
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete (@NonNull Task < Void > task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        }

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
        if (!nationalityUpdate.matches(""))

        {
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

//        public void updateDisplayname(String displayname) {
//            Map<String, Object> dataUpdate = new HashMap<String, Object>();
//            dataUpdate.put(NICKNAME, displayname);
//            userDocRef
//                    .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d(TAG, "Document has been saved");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG, "Document could not be saved");
//                }
//            });
//
//        }
