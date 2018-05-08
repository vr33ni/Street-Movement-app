package com.example.vreeni.StreetMovementApp;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.WARMUPSSKIPPED;

/**
 * Created by vreeni on 27/01/2018.
 */

/**
 * Fragment displaying information on Home Warmups before training
 * contains different Image-, TextViews and Buttons
 * contains reference to the database updating the number of warmups completed or skipped (no differentiation between home/outdoor)
 */
public class Fragment_Training_Warmup extends Fragment implements View.OnClickListener {
    private String TAG = "Warmup: ";

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
    private User currentUser;
    private long warmups_skipped;
    private long warmups_completed;

    private Workout wk;


    public static Fragment_Training_Warmup newInstance(Workout workout) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Warmup fragment = new Fragment_Training_Warmup();
        bundle.putParcelable("Workout", workout);
        fragment.setArguments(bundle);
        return fragment;
    }


//    public static Fragment_Training_Warmup newInstance(MovementSpecificActivity movspec){
//        final Bundle bundle = new Bundle();
//        Fragment_Training_Warmup fragment = new Fragment_Training_Warmup();
//        bundle.putParcelable("MovementSpecificActivity", movspec);
//        fragment.setArguments(bundle);
//        return fragment;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            wk = getArguments().getParcelable("Workout");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_warmup, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //silently transfer the bundle via this argument without displaying
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


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "warmup for: " + wk);
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
        updateSkippedWarmups();
            //bundle.putInt("Warm-ups skipped", warmups_skipped);
            exercises = new Fragment_Training_Workout_Exercises();
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
            exercises = new Fragment_Training_Workout_Exercises();
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

    public void updateSkippedWarmups() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userDocRef = db.collection("Users").document(currUser.getEmail());
        //access current values saved under this user
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                warmups_skipped = currentUser.getWarmupsSkipped()+1;
                Map<String, Object> update = new HashMap<>();
                //warmups_skipped=getWarmups_skipped();
                update.put(WARMUPSSKIPPED, warmups_skipped);
                userDocRef
                        .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document has been saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Document could not be saved" +e.toString());
                    }
                });
                Log.d(TAG, "DocumentSnapshot successfully retrieved! " + warmups_skipped);
            }
        });
    }


}

