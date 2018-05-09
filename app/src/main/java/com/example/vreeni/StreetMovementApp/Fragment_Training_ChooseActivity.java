package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


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

    Button btnWorkout;


    public static Fragment_Training_ChooseActivity newInstance(String string) {
        final Bundle bundle = new Bundle(); //to pass arguments to the next fragment
        Fragment_Training_ChooseActivity fragment = new Fragment_Training_ChooseActivity();
        bundle.putString("Setting", string);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setting = getArguments().getString("Setting");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_choose_workout, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnWorkout = (Button) view.findViewById(R.id.workout_selected);
        btnWorkout.setOnClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();
        btnWorkout.setOnClickListener(this);
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
                Fragment_Training_Workout_Setting fragment_setting = Fragment_Training_Workout_Setting.newInstance(activity);
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment_setting, "SettingsFragment")
                        .addToBackStack("setting")
                        .commit();
            } else {
                // started training flow from map - setting already defined
                Log.d(LOG_TAG, "started training flow from map - bundle: " + setting);
//                Fragment_Training_Workout_Level fragment_level = new Fragment_Training_Workout_Level();
//                bundle.putString("Activity", "Workout");
//                bundle.putString("Setting", "Outdoors");

            }
        }
        //if the button representing the "Movement Specific Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.movementChallenge_selected) {
//          fragment = new Fragment_Training_TrainNowORCreateTraining();
        }
        //if the button representing the "Street Movement Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.smChallenge_selected) {
//          fragment = new Fragment_Training_TrainNowORCreateTraining();
        }

        //how to handle clicks that leave the training flow? bakc buttons?
    }
}





