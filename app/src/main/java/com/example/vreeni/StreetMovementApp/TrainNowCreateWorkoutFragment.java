package com.example.vreeni.StreetMovementApp; /**
 * Created by vreeni on 19/12/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Fragment containing two buttons offering the possibility to choose between creating a workout or starting a training based on a selection of already created workouts / workout plans connected to the user profile
 */
public class TrainNowCreateWorkoutFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnTrainNow = (Button) view.findViewById(R.id.btn_train_now);
        btnTrainNow.setOnClickListener(this);

        Button btnCreateWorkout = (Button) view.findViewById(R.id.btn_create_workout);
        btnCreateWorkout.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_train_now_create_workout, container, false);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v.getId()==R.id.btn_train_now) {
                fragment = new TrainNowFragment();
        } else if (v.getId()==R.id.btn_create_workout){
            fragment = new CreateWorkoutFragment();
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

