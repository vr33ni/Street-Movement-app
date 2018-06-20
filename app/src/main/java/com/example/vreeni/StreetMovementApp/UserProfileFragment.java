package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.example.vreeni.StreetMovementApp.User.AGE;
import static com.example.vreeni.StreetMovementApp.User.EMAIL;
import static com.example.vreeni.StreetMovementApp.User.FULLNAME;
import static com.example.vreeni.StreetMovementApp.User.NATIONALITY;
import static com.example.vreeni.StreetMovementApp.User.NICKNAME;
import static com.example.vreeni.StreetMovementApp.User.PROFILE_PICTURE;
import static com.example.vreeni.StreetMovementApp.User.STATUS;

/**
 * Fragment displaying the user profile
 * => offering the possiblity to change certain user information and redirect to the EditUserProfile Fragment
 *
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private String TAG = "User Profile ";
    private PopupWindow popupWindow_editProfile;

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
    private ImageView iv_currentProfilePicture;
    private Button btn_my_activities;

    private PopupWindow popupWindow;
    private PopupWindow popupWindowSelectUploadSource;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private Uri photoURI;
    private Uri downloadUrl;
    private String mCurrentPhotoPath;

    private Button btn_submit;
    private Button btn_cancel;
    private ImageView iv_popup_currentProfilePicture;

    //for preventing multiple clicks on buttons
    private long mLastClickTime = 0;


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

        Button btnEditProfile = (Button) view.findViewById(R.id.edit_user_info);
        btnEditProfile.setOnClickListener(this);

        txtProfileName = (TextView) view.findViewById(R.id.profile_section_fullname);
        txtProfileEmail = (TextView) getView().findViewById(R.id.profile_section_email);
        txtProfileNickname = (TextView) getView().findViewById(R.id.profile_section_nickname);
        txtProfileAge = (TextView) getView().findViewById(R.id.profile_section_age);
        txtProfileNationality = (TextView) getView().findViewById(R.id.profile_section_nationality);
        txtProfileStatus = (TextView) getView().findViewById(R.id.profile_section_status);

        iv_currentProfilePicture = (ImageView) view.findViewById(R.id.iv_currentProfilePicture);

        btn_my_activities = (Button) view.findViewById(R.id.btn_myactivities);
    }

    @Override
    public void onStart() {
        super.onStart();
        //DATA FROM FIRESTORE
        displayFirestoreData();

        iv_currentProfilePicture.setOnClickListener(this);
        btn_my_activities.setOnClickListener(this);
    }

    public void getUserProfilePictureFromDB() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail());


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
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

//        Fragment fragment = null;
        //if the button representing the "train now or create workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.edit_user_info) {
//            fragment = new EditUserProfileFragment();
            openEditProfilePopupWindow();

        }
        if (v.getId() == R.id.iv_currentProfilePicture) {
            //open edit / add image popup window
            changeProfilePicture();
        }
        if (v.getId() == R.id.btn_cancelProfilePictureChanges_step1) {
            //exit popup window
            popupWindow.dismiss();
        }
        if (v.getId() == R.id.btn_upload_picture) {
            //open 2nd popup window to select img source
            openPopUpWindowSelectUploadSource();
        }
        if (v.getId() == R.id.btn_chooseFromGallery) {
            //opening image gallery on phone to select picture
            retrieveFromGallery();
        }
        if (v.getId() == R.id.btn_takePicture) {
            //open camera
            takePicture();
        }
        if (v.getId() == R.id.btn_cancelProfilePictureChanges_step2) {
            //return to first popup window
            popupWindowSelectUploadSource.dismiss();
            //display uploaded image in preview of 1st popup window
        }
        if (v.getId() == R.id.btn_submitProfilePictureChanges) {
            //upload to firebase storage
            uploadPictureToFirestore();
            changeFirebaseUserPicture();
        }
        if (v.getId() == R.id.btn_myactivities) {
            UserProfile_Account fragment_setting = UserProfile_Account.newInstance();
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_setting, "Personal Stats")
                    .addToBackStack("personalStats")
                    .commit();
        }

//        if (fragment != null) {
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .addToBackStack(null)
//                    .commit();
//        }
    }

    public void changeFirebaseUserPicture() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoURI)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }

    public void changeProfilePicture() {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_upload_profile_picture, null);
        popupWindow = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        iv_popup_currentProfilePicture = (ImageView) layout.findViewById(R.id.iv_popupWindow_currentProfilePicture);
        Button btn_uploadPicture = (Button) layout.findViewById(R.id.btn_upload_picture);
        btn_uploadPicture.setOnClickListener(this);
//            Button btn_chooseAvatar = (Button) layout.findViewById(R.id.btn_select_avatar);
//            btn_uploadPicture.setOnClickListener(this);
        btn_submit = (Button) layout.findViewById(R.id.btn_submitProfilePictureChanges);
        btn_submit.setOnClickListener(this);
        btn_submit.setEnabled(true);

        btn_cancel = (Button) layout.findViewById(R.id.btn_cancelProfilePictureChanges_step1);
        btn_cancel.setOnClickListener(this);
        btn_cancel.setEnabled(true);
//        int x = Resources.getSystem().getDisplayMetrics().widthPixels/2-150;
//        int y = Resources.getSystem().getDisplayMetrics().heightPixels/2-100;
        popupWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);
        Log.d(TAG, "opening a popup window");
    }

    /**
     * opens a second popup window where the user can choose if he wants to take a picture using the camera or upload one from the gallery
     * once one button is clicked, the other one is set to disabled
     */
    public void openPopUpWindowSelectUploadSource() {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_select_upload_source, null);
        popupWindowSelectUploadSource = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        Button btn_takePicture = (Button) layout.findViewById(R.id.btn_takePicture);
        btn_takePicture.setOnClickListener(this);
        btn_takePicture.setEnabled(true);
        Button btn_chooseFromGallery = (Button) layout.findViewById(R.id.btn_chooseFromGallery);
        btn_chooseFromGallery.setOnClickListener(this);
        btn_chooseFromGallery.setEnabled(true);
        Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancelProfilePictureChanges_step2);
        btn_cancel.setOnClickListener(this);
        popupWindowSelectUploadSource.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindowSelectUploadSource);
        Log.d(TAG, "opening the popup window to select upload source");
    }

    /**
     * access the user's image gallery to select a photo and load into the imageview in the popup window
     */
    public void retrieveFromGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * access the user's camera to take a photo and load into the imageview in the popup window
     */
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d(TAG, "creating photofile: " + photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "error creating the file" + ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                File testFile = new File(mCurrentPhotoPath);
                Log.d(TAG, "test file length: " + testFile.getAbsolutePath().length());
                Log.d(TAG, "test file Path: " + mCurrentPhotoPath);

                photoURI = FileProvider.getUriForFile(this.getActivity(),
                        "com.example.vreeni.StreetMovementApp", photoFile);
                Log.d(TAG, "uri: " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.d(TAG, "intent: " + takePictureIntent + " , " + REQUEST_TAKE_PHOTO);
            }
        }
    }


    /**
     * callback for camera intents, handling both retrieveFromGallyer() and takePicture()
     *
     * @param requestCode either pickImage from Gallery or takePicture to access the camera
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
            photoURI = data.getData();
            Log.d(TAG, "uri " + photoURI);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), photoURI);
                popupWindowSelectUploadSource.dismiss();
//                iv_popup_currentProfilePicture.setVisibility(View.VISIBLE);
                iv_popup_currentProfilePicture.setImageBitmap(bitmap);
                Log.d(TAG, "img path" + bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            popupWindowSelectUploadSource.dismiss();
            iv_popup_currentProfilePicture.setVisibility(View.VISIBLE);
            iv_popup_currentProfilePicture.setImageURI(photoURI);
            Log.d(TAG, "file path" + iv_popup_currentProfilePicture);
//                Glide.with(this)
//                        .load(getBitmapFromAssets(this.getActivity(), mCurrentPhotoPath, 100, 100))
//                        .error(R.drawable.img_railheaven)
//                        .into(selectedImage);
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * validate user input and upload to the collection of "trainingLocationsToBeApproved" on firestore for further data processing
     */
    public void uploadPictureToFirestore() {
        //upload image to storage
        Date currentTime = Calendar.getInstance().getTime();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userProfilePicturesRef = storageRef.child("userProfilePictures/" + photoURI.getLastPathSegment());
        Log.d(TAG, "Uri: " + photoURI);

        UploadTask uploadTask = userProfilePicturesRef.putFile(photoURI);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {
//                btn_submit.setEnabled(false);
            // once file is uploading successfully, button is set to disabled to prevent multiple upload
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            downloadUrl = taskSnapshot.getDownloadUrl();
            Log.d(TAG, "download url " + downloadUrl);

            //upload text information to database + create reference to storage
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            HashMap<String, Object> data = new HashMap<>();
            data.put("profilePicture", downloadUrl.toString());
            userDocRef
                    .set(data, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "New Profile picture has been saved: " + userDocRef.getId());
                //close popup window after upload
                popupWindow.dismiss();
                //display newly updated image in profile picture imageview
                popupWindowSelectUploadSource.dismiss();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), photoURI);
//                        iv_currentProfilePicture.setVisibility(View.VISIBLE);
                    iv_currentProfilePicture.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "New Profile picture could not be saved");
                }
            });
        });
    }


    /**
     * once popup window is open, dim everything behind it for the time it is opened
     * @param popupWindow based on which popup window is set as a parameter, it is taken as reference point and everything behind is dimmed
     */
    private void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    /**
     * if user documentReference exists, this method adds a SnapshotListener to the documentReference
     * if the snapshot isn't empty, Strings are created and assigned the desired information from the document snapshot
     * textViews are then set with the newly retrieved Strings
     */
    public void displayFirestoreData() {
        //get firestore database data
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usersDocRef = db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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
                    if (documentSnapshot.getString(PROFILE_PICTURE) != null) {
//                        String profilePic = ;
                        loadImageWithGlide(documentSnapshot.getString(PROFILE_PICTURE), iv_currentProfilePicture);
                    } else {
                        //if no profile picture is available in the user document, get facebook profile picture
                        getFacebookData();
                    }
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
                    //profile picture


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
                    loadImageWithGlide((getImageUrl(response)), iv_currentProfilePicture);
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
                .placeholder(R.drawable.ic_gallery1)
                .override(700, 400)
                .into(iv);
    }


    public void openEditProfilePopupWindow() {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_edit_profile, null);
        popupWindow_editProfile = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        EditText editUsername = (EditText) layout.findViewById(R.id.profile_section_edit_nickname);
        EditText editAge = (EditText) layout.findViewById(R.id.profile_section_edit_age);
        EditText editNationality = (EditText) layout.findViewById(R.id.profile_section_edit_nationality);

        Button btn_cancelEditProfile = (Button) layout.findViewById(R.id.btn_cancelEditProfile);
        btn_cancelEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow_editProfile.dismiss();
            }
        });

        Button btn_submitEditProfile;
        btn_submitEditProfile = (Button) layout.findViewById(R.id.btn_submitEditProfile);
        btn_submitEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_input;
                String age_input;
                String nationality_input;
                if (editUsername.getText() != null) {
                    username_input = editUsername.getText().toString().trim();
                } else username_input = null;
                if (editAge.getText() != null) {
                    age_input = editAge.getText().toString().trim();
                } else age_input = null;
                if (editNationality.getText() != null) {
                    nationality_input = editNationality.getText().toString().trim();
                } else nationality_input = null;

                //update Firestore data
                updateFireStoreData(username_input, age_input, nationality_input);

            }
        });

        popupWindow_editProfile.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindow_editProfile);
        Log.d(TAG, "opening a popup window");
    }


    /**
     * check if the parameters do not equal an empty string
     * => if not empty: create new hashmap that will contain the key value pair that is sent to the database
     *
     * @param nicknameUpdate    user input for the nickname
     * @param ageUpdate         user input for the age
     * @param nationalityUpdate user input for the nationality
     */
    //update the user entered information to the database, if the strings arent empty
    public void updateFireStoreData(String nicknameUpdate, String ageUpdate, String nationalityUpdate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                    popupWindow_editProfile.dismiss();
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

