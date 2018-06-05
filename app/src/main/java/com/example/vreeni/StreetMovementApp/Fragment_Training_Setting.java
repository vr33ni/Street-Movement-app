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
 * Fragment displaying two buttons offering the possibility to choose the setting of the training
 * => home, redirecting to the next fragment (Selection of level) and initiating the Home Workout flow
 * => outdoors, redirecting to the next fragment (Selection of level) and initiating the Outdoor Workout flow
 */
public class Fragment_Training_Setting extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "TrainingWorkoutSetting";
    private Bundle bundle;
    private String activity;


    public static Fragment_Training_Setting newInstance(String string) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Setting fragment = new Fragment_Training_Setting();
        bundle.putString("Activity", string);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_setting, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnPredefHomeWorkout = (Button) view.findViewById(R.id.btn_training_workout_home);
        btnPredefHomeWorkout.setOnClickListener(this);

        Button btnPredefOutdoorWorkout = (Button) view.findViewById(R.id.btn_training_workout_outdoors);
        btnPredefOutdoorWorkout.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
//        TextView textView = new TextView(getContext());
//
//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });


    }


    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "training info: " + activity);


        if (v.getId() == R.id.btn_training_workout_home) {
            String setting = "Home";
            ParkourPark pk = null;
            Fragment_Training_TrainNowORCreateTraining fragment_trainNowOrCreateTraining = Fragment_Training_TrainNowORCreateTraining.newInstance(activity, setting, pk);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_trainNowOrCreateTraining, "TrainOrCreateWk")
                    .addToBackStack(null)
                    .commit();

        } else if (v.getId() == R.id.btn_training_workout_outdoors) {
            //write setting to bundle
            String setting = "Outdoors";
            //new fragment for choosing where you wana train
            Fragment_OutdoorActivity_MapView fragment_mapView = Fragment_OutdoorActivity_MapView.newInstance(activity, setting);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_mapView, "MapView")
                    .addToBackStack(null)
                    .commit();
        }

    }

}



