package com.example.vreeni.StreetMovementApp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.getDefaultSize;
import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.WORKOUTSCOMPLETED;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;


public class Tab_Overview_Fragment extends Fragment implements View.OnClickListener {
    private String TAG = "Overview Tab ";
    private long mLastClickTime = 0;
    private TextView tv_name;
    private TextView tv_description;
    private Button btn_inSpotViewTabTrainHere;

    private String activity;
    private String setting;
    private ParkourPark pk;
    private Location mLastKnownLocation;


    public static Tab_Overview_Fragment newInstance(String act, String set, ParkourPark spot, Location mLastKnownLocation) {
        final Bundle bundle = new Bundle();
        Tab_Overview_Fragment fragment = new Tab_Overview_Fragment();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        bundle.putParcelable("TrainingLocation", spot);
        bundle.putParcelable("UserLocation", mLastKnownLocation);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
            setting = getArguments().getString("Setting");
            pk = getArguments().getParcelable("TrainingLocation");
            mLastKnownLocation = getArguments().getParcelable("UserLocation");
            Log.d(TAG, "bundle info: " + getArguments());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_description, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_name = (TextView) view.findViewById(R.id.tv_parkourpark_name);
        tv_description = (TextView) view.findViewById(R.id.tv_parkourpark_description);
        btn_inSpotViewTabTrainHere = (Button) view.findViewById(R.id.btn_inSpotViewTabTrainHere);
    }

    @Override
    public void onStart() {
        super.onStart();
        tv_name.setText(pk.getName());
        tv_description.setText("test");
        btn_inSpotViewTabTrainHere.setOnClickListener(this);
        Log.d(TAG, "passed parkourpark - parkDescription " + pk.getName());

//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });
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
        //make sure button is not clicked accidentally 2 times in a row
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (v.getId() == R.id.btn_inSpotViewTabTrainHere) {
            //convert the parkour park's geolocation to a location object
            Location trainingLocation = new Location("provider");
            trainingLocation.setLatitude(pk.getCoordinates().getLatitude());
            trainingLocation.setLongitude(pk.getCoordinates().getLongitude());

            //if user is in a radius of 250m from the spot, then he can start a training
            //start outdoor training cycle => setting first, then type of activity, then details on that, level etc.
            if (activity != null) { //training flow already started from nav. drawer => training => choooseactivity etc
                // continue the normal training flow
                //if user is in a radius of 250m from the spot, then he can start a training
                if (mLastKnownLocation.distanceTo(trainingLocation) < 750) {
                    if (activity.equals("Street Movement Challenge")) {
                        //see the street movement challenge connected to the specific spot

                    } else {
                        //get a workout or movement specific challenge
                        Fragment_Training_TrainNowORCreateTraining trainNowOrCreate = Fragment_Training_TrainNowORCreateTraining.newInstance(activity, setting, pk);
                        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, trainNowOrCreate, "trainNowOrCreate")
                                .addToBackStack("trainNowOrCreate")
                                .commit();
                    }
                } else {
                    //else: navigate to the spot
                    Toast.makeText(getActivity().getApplicationContext(), "Go to the spot to start training", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "go to the spot first" + mLastKnownLocation + ", " + mLastKnownLocation.distanceTo(trainingLocation));
                }

            } else {
                //no training flow initiated yet
                Log.d(TAG, "location infos " + mLastKnownLocation + ", " + trainingLocation);
                //if user is in a radius of 500m from the spot, then he can start a training
                if (mLastKnownLocation.distanceTo(trainingLocation) < 1000) {
                    Fragment_Training_ChooseActivity chooseAct = Fragment_Training_ChooseActivity.newInstance(setting, pk);
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, chooseAct, "chooseAct")
                            .addToBackStack("chooseAct")
                            .commit();
                }
                //else: navigate to the spot
                else {
                    //Do you want to use navigation services to get there?
//                    POPUP? DIAGLOG?
                    Toast.makeText(getActivity().getApplicationContext(), "Go to the spot to start training", Toast.LENGTH_LONG).show();

                    Log.d(TAG, "go to the spot first" + mLastKnownLocation + ", " + mLastKnownLocation.distanceTo(trainingLocation));
                }
            }

        }
    }
}