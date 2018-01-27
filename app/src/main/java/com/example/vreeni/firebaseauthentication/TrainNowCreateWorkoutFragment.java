package com.example.vreeni.firebaseauthentication; /**
 * Created by vreee on 19/12/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class TrainNowCreateWorkoutFragment extends Fragment implements View.OnClickListener {
    private Button btnTrainNow;
    private Button btnCreateWorkout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTrainNow = (Button) view.findViewById(R.id.btn_train_now);
        btnTrainNow.setOnClickListener(this);

        btnCreateWorkout = (Button) view.findViewById(R.id.btn_create_workout);
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

