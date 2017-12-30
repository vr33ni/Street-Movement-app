package com.example.vreeni.firebaseauthentication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vreee on 20/12/2017.
 */

public class CreateWorkoutFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_workout, container, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_out_button:

            case R.id.action_settings:
                System.out.print("SETTINGS");
        }
    }

}





