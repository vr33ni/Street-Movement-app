package com.example.vreeni.firebaseauthentication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by vreee on 20/12/2017.
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
        } else if (v.getId() == R.id.btn_predef_workout_outdoors) {
            //new fragment for choosing your level of a predefined outdoor workout
            fragment = new GetCustomizedOutdoorWorkoutStep2Fragment();

        }
        //OR IF CASE = OUTDOORS =>
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}



