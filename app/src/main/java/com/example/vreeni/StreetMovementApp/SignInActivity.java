package com.example.vreeni.StreetMovementApp;

/**
 * Created by vreee on 11/03/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.AGE;
import static com.example.vreeni.StreetMovementApp.User.EMAIL;
import static com.example.vreeni.StreetMovementApp.User.FULLNAME;
import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFOUTDOORWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LOGINMETHOD;
import static com.example.vreeni.StreetMovementApp.User.NATIONALITY;
import static com.example.vreeni.StreetMovementApp.User.NICKNAME;
import static com.example.vreeni.StreetMovementApp.User.STATUS;
import static com.example.vreeni.StreetMovementApp.User.WARMUPSCOMPLETED;
import static com.example.vreeni.StreetMovementApp.User.WARMUPSSKIPPED;
import static com.example.vreeni.StreetMovementApp.User.WORKOUTSCOMPLETED;

/**
 * Firebase Authentication using a Google ID Token, Facebook SignIn or Email Password Registration
 */
public class SignInActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "SigninActivity";
    private static final int RC_SIGN_IN = 9001;

    /**
     * checking whether user has logged on via facebook
     * necessary later on due to different logout processes for Facebook, Google and Email/Password
     */
    private boolean loginWithFacebook;
    /**
     * checking whether user has logged on via Google
     * necessary later on due to different logout processes for Facebook, Google and Email/Password
     */
    private boolean loginWithGoogle;
    /**
     * checking whether user has logged on via Email/Password
     * necessary later on due to different logout processes for Facebook, Google and Email/Password
     */
    private boolean loginWithEmailPassword;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    //Facebook
    private CallbackManager mCallbackManager;

    //Google
    private GoogleSignInClient mGoogleSignInClient;

    //Email Password
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;

    /**
     * defines the actions taken when this activity is started
     * initializes and sets up the different sign in options
     * @param savedInstanceState savedInstanceState can be a previous session, where a user is already logged in
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.button_facebook_login).setOnClickListener(this);

        //shared pref?
        loginWithFacebook = false;
        loginWithGoogle = false;
        loginWithEmailPassword = false;

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                loginWithFacebook = true;
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]


    // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]


        //START setup Email Password SignIn
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mNameField = findViewById(R.id.field_name);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.email_go_to_create_account).setOnClickListener(this);
        findViewById(R.id.email_go_to_sign_in).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
        // [END setup email Passowrd

        // [START Firebase initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END Firebaseinitialize_auth]



    }


    /**
     * once the activity is created, it is checked for authenticated users
     * the UI is being updated accordingly
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    /**
     * method is called in the sign up process of Facebook and Google to verify the user has an account
     * here the boolean values (of which login option has been chosen) come into play the first time, leading to different actions
     * @param requestCode
     * @param resultCode
     * @param data
     */
    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //START OF FACEBOOK login handling
        if (loginWithFacebook) {
            Log.d(TAG, "login with facebook: " + loginWithFacebook);
//            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
//        //END OF FACEBOOK login handling
//
//        //START OF GOOGLE login handling
        else {
            Log.d(TAG, "login with google: " + loginWithGoogle);
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // [START_EXCLUDE]
                    updateUI(null);
                    // [END_EXCLUDE]
                }
            }
        } //END OF GOOGLE login handling
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOutWithGoogle() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    /**
     * responsible for the updates of the user interface following a login attempt
     * if user authenitication worked, check if the user already exists in the database (updates in the user fields can be done here)
     * if user also exists in database, go to MainActivity
     * @param user
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            //check if user exists as a user document in the database
            checkIfExists();
            //start main activity with nav. drawer and fragments
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            Log.d(TAG, "not logged in");
            //check if user exists as a user document in the database
        }
    }


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]


    //START Email Password handling
    private void createAccount(String name, String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) { return; }
        showProgressDialog();

        //create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if ((name != null || !name.matches(""))) {
                                Log.d(TAG, "updating displayname... " + name);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete (@NonNull Task < Void > task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Firebase user displayname updated." + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                                    updateUI(user);

                                                }
                                            }
                                        });
                            }
                        } else {
                            // If sign in fails, display a message to the user giving an explanation
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            String exception = task.getException().toString();
                            if (exception.contains("The email address is already in use by another account")) {
                                View rootView = findViewById(android.R.id.content);
                                Snackbar.make(rootView, "This email address is already in used.", Snackbar.LENGTH_LONG)
                                        .show();
                            } else if (exception.contains("FirebaseAuthWeakPasswordException:")) {
                                View rootView = findViewById(android.R.id.content);
                                Snackbar.make(rootView, "Invalid password. Password should be at least 6 characters", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            String exception = task.getException().toString();
                            if (exception.contains("FirebaseAuthInvalidCredentialsException:")) {
                                View rootView = findViewById(android.R.id.content);
                                Snackbar.make(rootView, "Invalid password. Please try again.", Snackbar.LENGTH_LONG)
                                        .show();
                            } else if (exception.contains("FirebaseAuthInvalidUserException")) {
                                View rootView = findViewById(android.R.id.content);
                                Snackbar.make(rootView, "No such user in database. Please try again", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "authentization failed");
//                            String exception = task.getException().toString();
//                            if (exception.contains("FirebaseAuthInvalidCredentialsException:")) {
//                                View rootView = findViewById(android.R.id.content);
//                                Snackbar.make(rootView, "Invalid password. Try again.", Snackbar.LENGTH_LONG)
//                                        .show();
//                            }
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email verification sent to " + user.getEmail());
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    /**
     * if email/password fields are empty, show an error message
     * @return
     */
    private boolean validateForm() {
        boolean valid = true;
        if (mNameField.getVisibility()==View.VISIBLE) {
            String name = mNameField.getText().toString();
            if (TextUtils.isEmpty(name)) {
                valid = false;
            } else {
                mNameField.setError(null);
            }
        }

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
    //END OF EMAIL PASSWORD HANDLINg


    /**
     * check if user already exists in the Firestore Database
     * if user exists, do nothing or perform updates on user data
     * if user doesnt exist, create new user document in database
     */
    public void checkIfExists() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                    Log.d(TAG, "operation failed with ", task.getException());
                }
            }
        });
    }

    /**
     * creates new user document in the Firestore Database
     */
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
        String loginMethod;
        if (loginWithFacebook) loginMethod = "Facebook";
        else if (loginWithEmailPassword) loginMethod = "EmailPassword";
        else loginMethod = "Google";
        newEntry.put(LOGINMETHOD, loginMethod);
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


    /**
     * checks if user has entered something into the email address field
     * is called in the forgotPassword? process
     * @return
     */
    private boolean emailEntered() {
        boolean emailEntered = true;
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            emailEntered = false;
        }
        return emailEntered;
    }


    /**
     * handling of forgotten passwords calls
     */
    public void handleForgottenPasswords() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (emailEntered()) {
            String emailAddress = mEmailField.getText().toString();
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                View rootView1 = findViewById(android.R.id.content);
                                Snackbar.make(rootView1, "Please check your email to reset your password.", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });

        } else {

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //Sign In Processes
        if (i == R.id.sign_in_button) {
            loginWithGoogle = true;
            signInWithGoogle();
        } else if (i== R.id.button_facebook_login) {
            loginWithFacebook = true;
        }
        //no account yet? create account => set create account button visible and sign in button gone
        else if (i == R.id.email_go_to_create_account) {
            findViewById(R.id.field_name).setVisibility(View.VISIBLE);
            findViewById(R.id.email_sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.email_create_account_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_go_to_create_account).setVisibility(View.GONE);
            findViewById(R.id.email_go_to_sign_in).setVisibility(View.VISIBLE);
            findViewById(R.id.forgot_password).setVisibility(View.GONE);
        }
        //already go an account? sign in => set create account button gone and sign in button visible
        else if (i==R.id.email_go_to_sign_in) {
            findViewById(R.id.field_name).setVisibility(View.GONE);
            findViewById(R.id.email_create_account_button).setVisibility(View.GONE);
            findViewById(R.id.email_sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_go_to_sign_in).setVisibility(View.GONE);
            findViewById(R.id.email_go_to_create_account).setVisibility(View.VISIBLE);
            findViewById(R.id.forgot_password).setVisibility(View.VISIBLE);
        }
        //create account using email/password
        else if (i == R.id.email_create_account_button) {
            loginWithEmailPassword = true;
            createAccount(mNameField.getText().toString(), mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
        //sign in using email/password
        else if (i == R.id.email_sign_in_button) {
            loginWithEmailPassword = true;
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.forgot_password) {
            //handle forgotten passwords
            handleForgottenPasswords();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
        else {}
//        else if (i == R.id.sign_out_button) {
//            signOutWithGoogle();
//        } else if (i == R.id.disconnect_button) {
//            revokeAccess();
//        }
    }
}