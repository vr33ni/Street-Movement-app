package com.example.vreeni.StreetMovementApp; /**
 * Created by vreeni on 19/12/2017.
 */

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
 * Fragment containing two buttons offering the possibility to choose between creating a workout or starting a training based on a selection of already created workouts / workout plans connected to the user profile
 */
public class Fragment_Training_TrainNowORCreateTraining extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "TrainOrCreate";
    private String activity;
    private String setting;


    public static Fragment_Training_TrainNowORCreateTraining newInstance(String act, String set) {
        final Bundle bundle = new Bundle();
        Fragment_Training_TrainNowORCreateTraining fragment = new Fragment_Training_TrainNowORCreateTraining();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
            setting = getArguments().getString("Setting");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_train_now_create_training, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnTrainNow = (Button) view.findViewById(R.id.btn_train_now);
//        btnTrainNow.setOnClickListener(this);

        Button btnCreateWorkout = (Button) view.findViewById(R.id.btn_create_training);
        btnCreateWorkout.setOnClickListener(this);

        ((MainActivity) getActivity()).showBackButton(true);

    }

    @Override
    public void onStart() {
        super.onStart();
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
        if (v.getId() == R.id.btn_create_training) {
            Log.d(LOG_TAG, "training information: " + activity + ", " + setting);

            Fragment_Training_CreateTraining fragment_createTraining = Fragment_Training_CreateTraining.newInstance(activity, setting);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_createTraining, "CreateTraining")
                    .addToBackStack("Create")
                    .commit();
        }
//        else if (v.getId()==R.id.btn_train_now) {
//                fragment = new TrainNowFragment();


    }
}

