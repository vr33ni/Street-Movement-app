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
 * Fragment containing two buttons offering the user to choose between a customized workout and creating his/her own
 */
public class CreateWorkoutFragment extends Fragment implements View.OnClickListener {
    private Button btnPredefWorkout;
    private Button btnCreateYourOwnWorkout;



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPredefWorkout = (Button) view.findViewById(R.id.btn_get_predef_workout);
        btnPredefWorkout.setOnClickListener(this);
        btnCreateYourOwnWorkout = (Button) view.findViewById(R.id.btn_create_your_own_workout);
        btnCreateYourOwnWorkout.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_workout, container, false);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v.getId() == R.id.btn_get_predef_workout) {
            fragment = new GetCustomizedWorkoutSettingFragment();
        } else if (v.getId() == R.id.btn_create_your_own_workout) {
        }
        //OR IF CASE = OUTDOORS =>
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}



