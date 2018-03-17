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
 * Fragment displaying two buttons offering the possibility to choose the setting of the training
 * => home, redirecting to the next fragment (Selection of level) and initiating the Home Workout flow
 * => outdoors, redirecting to the next fragment (Selection of level) and initiating the Outdoor Workout flow
 */
public class GetCustomizedWorkoutSettingFragment extends Fragment implements View.OnClickListener {
    private Button btnPredefHomeWorkout;
    private Button btnPredefOutdoorWorkout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPredefHomeWorkout = (Button) view.findViewById(R.id.btn_predef_workout_home);
        btnPredefHomeWorkout.setOnClickListener(this);

        btnPredefOutdoorWorkout = (Button) view.findViewById(R.id.btn_predef_workout_outdoors);
        btnPredefOutdoorWorkout.setOnClickListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_workout_setting, container, false);

    }
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v.getId() == R.id.btn_predef_workout_home) {
            //new fragment for choosing your level of a predefined home workout
            fragment = new GetCustomizedHomeWorkoutLevelFragment();
            //OR IF CASE = OUTDOORS =>
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "GetCustomizedHomeWorkoutLevel")
                    .addToBackStack(null)
                    .commit();
        } else if (v.getId() == R.id.btn_predef_workout_outdoors) {
            //new fragment for choosing your level of a predefined outdoor workout
            fragment = new GetCustomizedOutdoorWorkoutMapView();
            //OR IF CASE = OUTDOORS =>
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, "GetCustomizedOutdoorWorkoutMapView")
                    .addToBackStack(null)
                    .commit();
        }

    }

}



