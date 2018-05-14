package com.example.vreeni.StreetMovementApp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.Games;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PlayGamesAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONObject;

import java.util.Calendar;

import static com.example.vreeni.StreetMovementApp.User.AGE;
import static com.example.vreeni.StreetMovementApp.User.EMAIL;
import static com.example.vreeni.StreetMovementApp.User.FULLNAME;
import static com.example.vreeni.StreetMovementApp.User.NATIONALITY;
import static com.example.vreeni.StreetMovementApp.User.NICKNAME;
import static com.example.vreeni.StreetMovementApp.User.STATUS;

/**
 * Fragment displaying the user profile
 * => offering the possiblity to change certain user information and redirect to the EditUserProfile Fragment
 *
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private String TAG = "User Profile ";

    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtProfileNickname;
    private TextView txtProfileAge;
    private TextView txtProfileNationality;
    private TextView txtProfileStatus;

    private static String FACEBOOK_FIELD_PROFILE_IMAGE = "picture.type(large)";
    private static String FACEBOOK_FIELDS = "fields";
    private static String FACEBOOK_FIELD_PICTURE = "picture";
    private static String FACEBOOK_FIELD_DATA = "data";
    private static String FACEBOOK_FIELD_URL = "url";

    private ProfilePictureView profilePictureView;
    private ImageView iv;

    //get firestore database data
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());


    public static UserProfileFragment newInstance() {
        final Bundle bundle = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //DATA FROM FIRESTORE
        displayFirestoreData();

        Button btnEditProfile = (Button) view.findViewById(R.id.edit_user_info);
        btnEditProfile.setOnClickListener(this);

        txtProfileName = (TextView) view.findViewById(R.id.profile_section_fullname);
        txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
        txtProfileNickname = (TextView) getView().findViewById(R.id.profile_section_nickname);
        txtProfileAge = (TextView) getView().findViewById(R.id.profile_section_age);
        txtProfileNationality = (TextView) getView().findViewById(R.id.profile_section_nationality);
        txtProfileStatus = (TextView) getView().findViewById(R.id.profile_section_status);

//        profilePictureView = (ProfilePictureView) view.findViewById(R.id.friendProfilePicture);
        iv = (ImageView) view.findViewById(R.id.IMG_UploadProfilePicture);
    }

    @Override
    public void onStart() {
        super.onStart();

        getFacebookData();

//        Games.getLeaderboardsClient(this.getActivity(), FirebaseAuth.getInstance().getCurrentUser().);
//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });
    }

    public void askForAccount() {
        // initializeGoogleAccount
        Calendar calender = Calendar.getInstance();
        Log.d(TAG, "Current Week:" + calender.get(Calendar.WEEK_OF_YEAR));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        //if the button representing the "train now or create workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.edit_user_info) {
            fragment = new EditUserProfileFragment();
        }
        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    /**
     * if user documentReference exists, this method adds a SnapshotListener to the documentReference
     * if the snapshot isn't empty, Strings are created and assigned the desired information from the document snapshot
     * textViews are then set with the newly retrieved Strings
     */
    public void displayFirestoreData() {
        if (usersDocRef != null) {
        }
        //this.getActivity makes sure the listener only works when in this FragmentActivity
        usersDocRef.addSnapshotListener(this.getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString(FULLNAME);
                    String email = documentSnapshot.getString(EMAIL);
                    String nickname = documentSnapshot.getString(NICKNAME);
                    String age = documentSnapshot.getString(AGE);
                    String nationality = documentSnapshot.getString(NATIONALITY);
                    String status = documentSnapshot.getString(STATUS);

                    //setting all the text views in the user profile
                    //TextView txtProfileName = (TextView) getView().findViewById(R.id.profile_section_fullname);
                    txtProfileName.setText(name);
                    // TextView txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
                    txtProfileEmail.setText(email);
                    //TextView txtProfileNickname = (TextView) getView().findViewById(R.id.profile_section_nickname);
                    txtProfileNickname.setText(nickname);
                    //TextView txtProfileAge = (TextView) getView().findViewById(R.id.profile_section_age);
                    txtProfileAge.setText(age);
                    //TextView txtProfileNationality = (TextView) getView().findViewById(R.id.profile_section_nationality);
                    txtProfileNationality.setText(nationality);
                    //TextView txtProfileStatus = (TextView) getView().findViewById(R.id.profile_section_status);
                    txtProfileStatus.setText(status);
                } else if (e != null) {
                    Log.w(TAG, "An exception occured", e);
                }
            }
        });

    }


    private void getFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (JSONObject object, GraphResponse response) -> {
//                    profilePictureView.setProfileId(getImageUrl(response));
                    loadImageWithGlide((getImageUrl(response)), iv);
                });
        Bundle parameters = new Bundle();
        parameters.putString(FACEBOOK_FIELDS, FACEBOOK_FIELD_PROFILE_IMAGE);
        request.setParameters(parameters);
        request.executeAsync();
    }

    private String getImageUrl(GraphResponse response) {
        String url = null;
        try {
            url = response.getJSONObject()
                    .getJSONObject(FACEBOOK_FIELD_PICTURE)
                    .getJSONObject(FACEBOOK_FIELD_DATA)
                    .getString(FACEBOOK_FIELD_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public void loadImageWithGlide(final String id, ImageView iv) {

        Glide.with(this)
                .load(id)
                .override(700, 400)
                .into(iv);
    }

}
