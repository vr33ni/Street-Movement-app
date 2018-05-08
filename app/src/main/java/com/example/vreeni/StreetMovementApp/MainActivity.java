package com.example.vreeni.StreetMovementApp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Main Activity of this application
 * After successful login, everything is handled from within this activity
 * - fragments
 * - navigation drawer
 * - toolbar
 * - back navigation
 * - logout
 * (- location updates every few seconds)
 * TO BE DEFINED: special button in toolbar enabling an admin view page, only accessible by specific pre-defined users
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnCompleteListener<Void> {

    private static final String TAG = MainActivity.class.getSimpleName();

    //Toolbar navigation/backbutton handling
    ActionBarDrawerToggle mDrawerToggle;
    private boolean mToolBarNavigationListenerIsRegistered = false;


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String loginMethod;

    //GEOFENCING
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }


    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;
    private GeofenceMaxNrHandler geofenceMax;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    private Bundle startedFromGeofenceNotification;
    //END OF GEOFENCING


    /**
     * setup the Main Activity
     * check if it was started from a Geofence-Notification (in which case there is a redirection to the MapViewFragment) or by newly starting the application
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * Geofencing setup
         */
        geofenceMax = new GeofenceMaxNrHandler(this, this.getBaseContext(), null);
        // Get the UI widgets.
        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;
        setButtonsEnabledState();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        /*
         * end of Geofencing on create
         */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        // Get DrawerLayout ref from layout
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Initialize ActionBarDrawerToggle, which will control toggle of hamburger.
        // You set the values of R.string.open and R.string.close accordingly.
        // Also, you can implement drawer toggle listener if you want.
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
        // Calling sync state is necessary to show your hamburger icon...
        // or so I hear. Doesn't hurt including it even if you find it works
        // without it on your test device(s)
        mDrawerToggle.syncState();
        showBackButton(false);

        //show the start fragment after login
        if (savedInstanceState == null) {
            Fragment newFragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, newFragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set username in navigation drawer header and make it clickable, linking to the user profile
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profile_section);
        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            Log.d(TAG, "profilename " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            String profileName = firebaseAuth.getCurrentUser().getDisplayName();
            txtProfileName.setText(profileName);
        } else {
            String profileName = "J. Doe";
            txtProfileName.setText(profileName);
        }
        txtProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                if (v.getId() == R.id.profile_section) {
                    fragment = new UserProfile_Account();
                }
                if (fragment != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.commit();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        checkLoginMethod();

        //check if mainActivity was started from Notification
        Intent intent = this.getIntent();
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("GeofenceDetails") && (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            String geofenceID = this.getIntent().getExtras().getString("GeofenceDetails");
            Log.d(TAG, "get extras - Geofence IDs: " + geofenceID);
            //open map view fragment
            Fragment fragment = new MapView_Fragment();
            if (fragment != null) {
                //add bundle with last location, so new callback isnt neccessary when clicking on the map fragment?
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment).replace(R.id.fragment_container, fragment).addToBackStack(null);
                ft.commit();
                ft.addToBackStack(null);
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
            Log.d(TAG, "on start - requesting permssions");
        } else {
            geofenceMax = new GeofenceMaxNrHandler(this, this.getBaseContext(), null);
            geofenceMax.getTrainingLocationsFromFirebaseAndTrack100Closest();
            performPendingGeofenceTask();
            Log.d(TAG, "performing pending geofence task");
        }
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        Log.d(TAG, "set trigger");

        // Add the geofences to be monitored by geofencing service. somehow link geofences to the original parkour park object?
        builder.addGeofences(geofenceMax.getmGeofenceList());
        Log.d(TAG, "geofence list added to builder" + mGeofenceList);


        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
        Log.d(TAG, "adding geofences button clicked");
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            Log.d(TAG, "insufficient permissions");
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
        Log.d(TAG, "geofences added, geofncrqest: " + getGeofencingRequest() + ", geofncnpendingintent: " + getGeofencePendingIntent());

    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     *
     * @param task the resulting Task, containing either a result or error.
     */
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            Log.d(TAG, "mGeofencePendingIntent not null - " + mGeofencePendingIntent);
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "mGeofencePendingIntent: " + mGeofencePendingIntent);
        return mGeofencePendingIntent;
    }


    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        if (getGeofencesAdded()) {
            if (mAddGeofencesButton != null) mAddGeofencesButton.setEnabled(false);
            if (mRemoveGeofencesButton != null) mRemoveGeofencesButton.setEnabled(true);

        } else {
            if (mAddGeofencesButton != null) mAddGeofencesButton.setEnabled(true);
            if (mRemoveGeofencesButton != null) mRemoveGeofencesButton.setEnabled(false);
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
//        Log.d(TAG, "Geofences added " + getGeofencesAdded());
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                GeofenceConstants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(GeofenceConstants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
            Log.d(TAG, "adding geofences");
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
            Log.d(TAG, "removing geofences");
        } else Log.d(TAG, "mPendingGeofenceTask: " + mPendingGeofenceTask);

    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                geofenceMax = new GeofenceMaxNrHandler(this, this.getBaseContext(), null);
                geofenceMax.getTrainingLocationsFromFirebaseAndTrack100Closest();
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }


    //enabling the options menu in the appbar / toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        //if navigation drawer is open => close it
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //if is only one fragment on the back stack, check if its the home fragment or not
        // if so: logout; if not: move the task to the background
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
//                logout();
                moveTaskToBack(false);

            } else {
                moveTaskToBack(false);
            }
        }
        //if there are more than 1 fragments on the back stack, check if its the home fragment or not
        // if so: logout; if not: implement the super() method of onBackPressed
        else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof HomeFragment) {
                logout();
            } else {
                super.onBackPressed();
            }
        }
    }


    //handling the actions from the navigation drawer menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int groupId = item.getGroupId();
        int id = item.getItemId();
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (groupId == R.id.first_group) {
            //add new if statement here and in activity_main_drawer.xml to create a new navigation menu item
            if (id == R.id.home) {
                fragment = new HomeFragment();
//            getSupportActionBar().setSubtitle("Home"); //underneath the app name, there will appear a subtitle
            } else if (id == R.id.training) {
                fragment = new Fragment_Training_ChooseActivity();
            }
        } else if (groupId == R.id.second_group) {
            if (id == R.id.fb) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/streetmovementdk")));
            } else if (id == R.id.insta) {
                Uri uri = Uri.parse("http://instagram.com/_u/streetmovementdk");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                i.setPackage("com.instagram.android");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            } else if (id == R.id.vimeo) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vimeo.com/streetmovement")));
            } else if (id == R.id.youtube) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/StreetmovementDK")));
            }
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment).replace(R.id.fragment_container, fragment).addToBackStack(null);
            ft.commit();
            ft.addToBackStack(null);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //handling clicks on 3 dot options menu in the app bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.showMap:
                Fragment fragment = new MapView_Fragment();
                if (fragment != null) {
                    //add bundle with last location, so new callback isnt neccessary when clicking on the map fragment?
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment).replace(R.id.fragment_container, fragment).addToBackStack(null);
                    ft.commit();
                    ft.addToBackStack(null);
                }
                break;
            case R.id.log_out_button:
                logout();
                break;
            case R.id.action_settings:
                System.out.print("SETTINGS");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }


    //possibly create new class called AppStateHandler handling login, signin and logout
    private void logout() {
        if (loginMethod != null) {
            if (loginMethod.equals("Facebook")) {
                //signout via firebase + facebook
                Log.d(TAG, "google sign out successful");
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                updateUI(null);
            } else if (loginMethod.equals("Google")) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //redirect to signout page, login page, etc
                                Log.d(TAG, "google sign out successful");
                                updateUI(null);
                            }
                        });

            } else {
                Log.d(TAG, "email password sign out successful");
                FirebaseAuth.getInstance().signOut();
                updateUI(null);
            }
        } else {
            Log.d(TAG, "login method not found.");
        }
//        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
    }

    public void checkLoginMethod() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userDocRef = db.collection("Users").document(currUser.getEmail());
        Log.d(TAG, "userDocRef: " + userDocRef);
        //access current values saved under this user
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    Log.d(TAG, "checking loginmethod - userDocRef: " + currentUser);
//
                    Map<String, Object> data = new HashMap<>();
                    data = documentSnapshot.getData();
                    loginMethod = (String) data.get("loginMethod");
                } else {
                    //sometimes if a user has just been created, it might not yet have been fully saved as a document to the database
                    checkLoginMethod();
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            //check if user exists as a user document in the database
//            checkIfExists();
            //start main activity with nav. drawer and fragments
//            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//            startActivity(intent);
        } else {
            Log.d(TAG, "not logged in");
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void showBackButton(boolean enable) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {
            // Remove hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            mDrawerToggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }


}
