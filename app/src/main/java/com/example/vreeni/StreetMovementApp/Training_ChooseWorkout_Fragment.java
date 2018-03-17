package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class Training_ChooseWorkout_Fragment extends Fragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnWorkout = (Button) view.findViewById(R.id.workout_selected);
        btnWorkout.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_choose_workout, container, false);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        //if the button representing the "Workout" fragment is clicked, create this fragment
        if (v.getId() == R.id.workout_selected) {
            fragment = new TrainNowCreateWorkoutFragment();
        }
        //if the button representing the "Movement Specific Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.movementChallenge_selected) {
//            fragment = new TrainNowCreateWorkoutFragment();
        }
        //if the button representing the "Street Movement Challenge" fragment is clicked, create this fragment
        if (v.getId() == R.id.smChallenge_selected) {
//            fragment = new TrainNowCreateWorkoutFragment();
        }

        if (fragment != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}





