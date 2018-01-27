package com.example.vreeni.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HomeFragment hf;
    private WebViewFragment wvf;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


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
        String username = firebaseAuth.getCurrentUser().getDisplayName();
        txtProfileName.setText(username);
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
        if(getFragmentManager().getBackStackEntryCount() == 1) {
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
            }
            else {
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
            getSupportActionBar().setSubtitle("Home");
        } else if (id == R.id.training) {
            fragment = new TrainingFragment();
        } else if (id == R.id.fb) {
            bundle.putString("url", "https://www.facebook.com/StreetMovement.dk");
            fragment = new WebViewFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.insta) {
            bundle.putString("url", "https://www.instagram.com/streetmovementdk");
            fragment = new WebViewFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.youtube) {
            bundle.putString("url", "https://www.youtube.com/user/StreetmovementDK");
            fragment = new WebViewFragment();
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
            case R.id.log_out_button:
                logout();
            case R.id.action_settings:
                System.out.print("SETTINGS");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //possibly create new class called AppStateHandler handling login, signin and logout
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, StartScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}


