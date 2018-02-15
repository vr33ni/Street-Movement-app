package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vreee on 20/12/2017.
 */

public class GetCustomizedOutdoorWorkoutStep2Fragment extends Fragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_outdoorworkout_step2, container, false);

    }
    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if (v.getId() == R.id.btn_predef_outdoorworkout_beginner) {

            //new fragment for choosing your focus of a predefined home workout
            //fragment = new TrainNowCreateWorkoutFragment();
        } else if (v.getId() == R.id.btn_predef_outdoorworkout_intermediate) {
            //new fragment for choosing your focus of a predefined home workout
        } else if (v.getId() == R.id.btn_predef_outdoorworkout_advanced) {
            //new fragment for choosing your level of a predefined home workout
        }
        //OR IF CASE = OUTDOORS =>
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}



