package com.example.vreeni.firebaseauthentication;

import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by vreee on 27/01/2018.
 */

public class GetCustomizedHomeWorkout_WarmupFragment extends Fragment implements View.OnClickListener {
    //silently transfer the bundle via this argument without displaying
    private Bundle bundle;
    private TextView timer;
    private Button btn_skipWarmup;
    private Button btn_continueToExercises;
    private Button btn_startWarmup;
    private Button btn_cancelWarmup;
    private boolean isCancelled;
    private int time;

    //put these to as fields in the workout class later on
    private int warmups_skipped=0;
    private int warmups_completed=0;



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //silently transfer the bundle via this argument without displaying
        bundle = getArguments();

        time = 60;
        timer = (TextView) view.findViewById(R.id.warmuptimer);

        btn_startWarmup = (Button) view.findViewById(R.id.btn_workout_startWarmup);
        btn_startWarmup.setOnClickListener(this);

        btn_cancelWarmup = (Button) view.findViewById(R.id.btn_workout_stopWarmup);
        btn_cancelWarmup.setOnClickListener(this);

        btn_continueToExercises = (Button) view.findViewById(R.id.btn_workout_continueToExercises);
        btn_continueToExercises.setOnClickListener(this);

        btn_skipWarmup = (Button) view.findViewById(R.id.btn_workout_skipWarmup);
        btn_skipWarmup.setOnClickListener(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_warmup, container, false);
    }


    @Override
    public void onClick(View v) {
        //clickable actions involving the timer
        if (v.getId() == R.id.btn_workout_startWarmup) {
            startTimer();
            btn_skipWarmup.setEnabled(false);
            btn_startWarmup.setEnabled(false);
            //Enabled the pause and cancel button
            btn_cancelWarmup.setEnabled(true);
        } else if (v.getId() == R.id.btn_workout_stopWarmup) {
            isCancelled = true;
            onCancel();
            btn_skipWarmup.setEnabled(true);
        }
        //continue to exercises (skipping warm-up or after finishing the timer)
        Fragment exercises = null;
        if (v.getId() == R.id.btn_workout_skipWarmup) {
            warmups_skipped+=1;
            //bundle.putInt("Warm-ups skipped", warmups_skipped);
            exercises = new GetCustomizedHomeWorkout_ExerciseFragment();
            //check which bundle obj exists, beginner, intermed, advanced? use boolean? level1= true?
            if (bundle != null) {
                exercises.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, exercises)
                        .addToBackStack(null)
                        .commit();
            }
        }
        if (v.getId() == R.id.btn_workout_continueToExercises) {
            exercises = new GetCustomizedHomeWorkout_ExerciseFragment();
            //check which bundle obj exists, beginner, intermed, advanced? use boolean? level1= true?
            if (bundle != null) {
                exercises.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, exercises)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void startTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (isCancelled) {
                    cancel();
                    isCancelled = false;
                } else {
                    timer.setText("0:" + checkDigit(time));
                    time--;
                }
            }

            public void onFinish() {
                timer.setText("Warm-up completed!");
                time = 60;
                //Enable the start button
                btn_startWarmup.setEnabled(false);
//                //Disable the pause, resume and cancel button
                btn_cancelWarmup.setEnabled(false);
                btn_startWarmup.setVisibility(View.GONE);
                btn_cancelWarmup.setVisibility(View.GONE);
                btn_continueToExercises.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void onCancel() {
        //When user request to cancel the CountDownTimer
        time = 60;
        isCancelled = true;
        //Disable the cancel, pause and resume button
        btn_cancelWarmup.setEnabled(false);
        //Enable the start button
        btn_startWarmup.setEnabled(true);
        //Notify the user that CountDownTimer is canceled/stopped
        timer.setText("Warm-up stopped.");
    }
}
