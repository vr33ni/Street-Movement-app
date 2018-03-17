package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vreeni on 20/12/2017.
 */

/**
 * Fragment displaying the possiblity to select from a list of previously created workouts, challenges, etc.
 * ! precise function yet to be defined !
 */
public class TrainNowFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_train_now, container, false);
    }
    @Override
    public void onClick(View v) {
//        Fragment fragment = null;
//        switch (v.getId()) {
//            //IF CASE = HOME =>
//            case R.id.workout_selected:
//                fragment = new TrainNowCreateWorkoutFragment();
//
//        }
//        //OR IF CASE = OUTDOORS =>
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .addToBackStack(null)
//                .commit();
    }

}



