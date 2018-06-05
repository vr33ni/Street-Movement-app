package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by vreeni on 20/12/2017.
 */

/**
 * Fragment initiating the Training Flow
 * => containing three buttons offering the possiblity to choose between workout, movement-specific challenge and Street Movement Challenge
 */
public class Fragment_Training_ChooseActivity extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "ChooseActivity";
    private String setting;
    private ParkourPark pk;

    private Button btnWorkout;
    private Button btnMovSpecChallenge;
    private Button btnSMChallenge;


    /**
     * Constructor that can hold an activity's setting and specific training location, when the training flow was initiated from the map
     * setting and specific location will be null if started from the training function in the navigation drawer
     *
     * @param setting
     * @param pk
     * @return
     */
    public static Fragment_Training_ChooseActivity newInstance(String setting, ParkourPark pk) {
        final Bundle bundle = new Bundle(); //to pass arguments to the next fragment
        Fragment_Training_ChooseActivity fragment = new Fragment_Training_ChooseActivity();
        bundle.putString("Setting", setting);
        bundle.putParcelable("TrainingLocation", pk);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setting = getArguments().getString("Setting");
            pk = getArguments().getParcelable("TrainingLocation");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_choose_activity, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnWorkout = (Button) view.findViewById(R.id.workout_selected);

        btnMovSpecChallenge = (Button) view.findViewById(R.id.movementChallenge_selected);

        btnSMChallenge = (Button) view.findViewById(R.id.smChallenge_selected);

    }


    @Override
    public void onStart() {
        super.onStart();
        btnWorkout.setOnClickListener(this);
        btnMovSpecChallenge.setOnClickListener(this);
        btnSMChallenge.setOnClickListener(this);

//        TextView textView = new TextView(getContext());
//        ((MainActivity)getActivity()).showBackButton(true);
//        OR
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
        //if the button representing the "Workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.workout_selected) {
            if (setting == null) {
                String activity = "Workout";
                Fragment_Training_Setting fragment_setting = Fragment_Training_Setting.newInstance(activity);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment_setting, "SettingsFragment")
                        .addToBackStack("setting")
                        .commit();
            } else {
                // started training flow from map - setting already defined
                Log.d(LOG_TAG, "started training flow from map - bundle: " + setting);
                String activity = "Workout";
                Fragment_Training_Level fragment_setting = Fragment_Training_Level.newInstance(activity, setting, pk);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment_setting, "LevelFragment")
                        .addToBackStack("lvl")
                        .commit();

            }
        }
        //if the button representing the "Movement Specific Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.movementChallenge_selected) {
            if (setting == null) {
                String activity = "Movement specific challenge";
                String setting = "Outdoors";
                Fragment_OutdoorActivity_MapView fragment_setting = Fragment_OutdoorActivity_MapView.newInstance(activity, setting);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment_setting, "SettingsFragment")
                        .addToBackStack("setting")
                        .commit();
            } else {
                // started training flow from map - setting already defined
                Log.d(LOG_TAG, "started training flow from map - bundle: " + setting);
                String activity = "Movement specific challenge";
                Fragment_Training_Level fragment_setting = Fragment_Training_Level.newInstance(activity, setting, pk);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment_setting, "LevelFragment")
                        .addToBackStack("lvl")
                        .commit();

            }
        }
        //if the button representing the "Street Movement Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.smChallenge_selected) {
//            if (setting == null) {
//                String activity = "Street Movement challenge";
//                String setting = "Outdoors";
//                Fragment_OutdoorActivity_MapView fragment_setting = Fragment_OutdoorActivity_MapView.newInstance(activity,setting);
//                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, fragment_setting, "SettingsFragment")
//                        .addToBackStack("setting")
//                        .commit();
//            } else {
            // started training flow from map - setting already defined
            Log.d(LOG_TAG, "Street mvmnt challenge + setting: " + setting);
            String activity = "Street Movement challenge";
            Fragment_Training_StreetMovementChallenge fragment_setting = Fragment_Training_StreetMovementChallenge.newInstance(activity, setting);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_setting, "smChallenge")
                    .addToBackStack("smChallenge")
                    .commit();

//            }
        }

        //how to handle clicks that leave the training flow? bakc buttons?
    }
}





