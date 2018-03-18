package com.example.vreeni.StreetMovementApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean isCallingLocationUpdates = false;

    //firebase analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    private HomeFragment hf;
    private WebViewFragment wvf;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String loginMethod;

    //SEND LOCATION UPDATES
    // private boolean isOutdoorTraining => if true, update location every few seconds, if false, do nothing


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.showMap) {
//                    Fragment fragment = new MapView_Fragment();
//                    if (fragment != null) {
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.fragment_container, fragment).replace(R.id.fragment_container, fragment).addToBackStack(null);
//                        ft.commit();
//                        ft.addToBackStack(null);
//                    }
//                }
//            }
//        });

        //show the start fragment after login
        if (savedInstanceState == null) {
            Fragment newFragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, newFragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        //define the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
                    fragment = new UserProfileFragment();
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
                logout();
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
        int id = item.getItemId();
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        //add new if statement here and in activity_main_drawer.xml to create a new navigation menu item
        if (id == R.id.home) {
            fragment = new HomeFragment();
//            getSupportActionBar().setSubtitle("Home"); //underneath the app name, there will appear a subtitle
        } else if (id == R.id.training) {
            fragment = new Training_ChooseWorkout_Fragment();
        } else if (id == R.id.fb) {
            bundle.putString("url", "https://www.facebook.com/StreetMovement.dk");
            fragment = new WebViewFragment_SocialMediaChannels();
            fragment.setArguments(bundle);
        } else if (id == R.id.insta) {
            bundle.putString("url", "https://www.instagram.com/streetmovementdk");
            fragment = new WebViewFragment_SocialMediaChannels();
            fragment.setArguments(bundle);
        } else if (id == R.id.vimeo) {
            bundle.putString("url", "https://vimeo.com/streetmovement");
            fragment = new WebViewFragment_SocialMediaChannels();
            fragment.setArguments(bundle);
        } else if (id == R.id.youtube) {
            bundle.putString("url", "https://www.youtube.com/user/StreetmovementDK");
            fragment = new WebViewFragment_SocialMediaChannels();
            fragment.setArguments(bundle);
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
//                            updateUI(null);
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
}


