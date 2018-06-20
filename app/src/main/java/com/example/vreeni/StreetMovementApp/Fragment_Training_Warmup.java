package com.example.vreeni.StreetMovementApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.WARMUPSSKIPPED;
import static com.facebook.FacebookSdk.getApplicationContext;

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

    //timer
    private boolean timerIsRunning;
    private PrefUtilsWarmupTimer prefUtilsWarmupTimer;
    //    private TextView timer;
    private TextView noticeText;
    private CountDownTimer countDownTimer;
    private int timeToStart;
    private WarmupTimerState warmupTimerState;
    private static final int MAX_TIME = 12; //Time length is 12 seconds


    //put these to as fields in the workout class later on
    private long warmups_skipped;
    private long warmups_completed;

    private Workout wk;
    private ParkourPark pk;


    public static Fragment_Training_Warmup newInstance(Workout workout, ParkourPark spot) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Warmup fragment = new Fragment_Training_Warmup();
        bundle.putParcelable("Workout", workout);
        bundle.putParcelable("TrainingLocation", spot);
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
            pk = getArguments().getParcelable("TrainingLocation");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_warmup, container, false);
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

        prefUtilsWarmupTimer = new PrefUtilsWarmupTimer(getApplicationContext());


        Log.d(TAG, "warmup for: " + wk + " at " + pk);
    }


    @Override
    public void onResume() {
        super.onResume();
        //initializing a countdown timer
        initTimer();
        updatingUI();
        removeAlarmManager();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (warmupTimerState == WarmupTimerState.RUNNING) {
            countDownTimer.cancel();
            setAlarmManager();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (warmupTimerState == WarmupTimerState.RUNNING) {
//            cancelTimer();
//            updatingUI();
//        } else {
//
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        //clickable actions involving the timer
        if (v.getId() == R.id.btn_workout_startWarmup) {
            if (warmupTimerState == WarmupTimerState.STOPPED) {
                prefUtilsWarmupTimer.setStartedTime((int) getNow());
                startTimer();
                warmupTimerState = WarmupTimerState.RUNNING;
            }
            btn_skipWarmup.setEnabled(false);
            btn_startWarmup.setEnabled(false);
            //Enabled the pause and cancel button
            btn_cancelWarmup.setEnabled(true);
        } else if (v.getId() == R.id.btn_workout_stopWarmup) {
            if (warmupTimerState == WarmupTimerState.RUNNING) {
                btn_startWarmup.setEnabled(false);
                cancelTimer();
                btn_skipWarmup.setEnabled(true);
//                noticeText.setText("Countdown Timer is running...");
            } else {
                btn_startWarmup.setEnabled(true);
//                noticeText.setText("Countdown Timer stopped!");
            }
            timer.setText(String.valueOf(timeToStart));
            isCancelled = true;
//            onCancel();
            updatingUI();
        }
        //continue to exercises (skipping warm-up or after finishing the timer)
        if (v.getId() == R.id.btn_workout_skipWarmup) {
//            btn_startWarmup.setEnabled(false);
//            btn_cancelWarmup.setEnabled(false);
        updateSkippedWarmups();
        }
        if (v.getId() == R.id.btn_workout_continueToExercises) {
            //check which bundle obj exists, beginner, intermed, advanced? use boolean? level1= true?
//                exercises.setArguments(bundle);
            Fragment_Training_Exercises exercises = Fragment_Training_Exercises.newInstance(wk, pk);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, exercises, "exercises")
                    .addToBackStack("exercises")
                        .commit();

        }
    }

//    public void startTimer() {
//        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                if (isCancelled) {
//                    cancel();
//                    isCancelled = false;
//                } else {
//                    timer.setText("0:" + checkDigit(time));
//                    time--;
//                }
//            }
//
//            public void onFinish() {
//                timer.setText("Warm-up completed!");
//                time = 60;
//                //Enable the start button
//                btn_startWarmup.setEnabled(false);
////                //Disable the pause, resume and cancel button
//                btn_cancelWarmup.setEnabled(false);
//                btn_startWarmup.setVisibility(View.GONE);
//                btn_cancelWarmup.setVisibility(View.GONE);
//                btn_continueToExercises.setVisibility(View.VISIBLE);
//            }
//        }.start();
//    }


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
                        Log.d(TAG, "Document has been saved, warmup skipped, continuing to workout " + wk + " at " + pk);
                        Fragment_Training_Exercises exercises = Fragment_Training_Exercises.newInstance(wk, pk);
                        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, exercises, "exercises")
                                .addToBackStack("exercises")
                                .commit();
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


    private long getNow() {
        Calendar rightNow = Calendar.getInstance();
        return rightNow.getTimeInMillis() / 1000;
    }

    private void initTimer() {
        long startTime = prefUtilsWarmupTimer.getStartedTime();
        if (startTime > 0) {
            timeToStart = (int) (MAX_TIME - (getNow() - startTime));
            if (timeToStart <= 0) {
                // TIMER EXPIRED
                timeToStart = MAX_TIME;
                warmupTimerState = WarmupTimerState.STOPPED;
                onTimerFinish();
            } else {
                startTimer();
                warmupTimerState = WarmupTimerState.RUNNING;
            }
        } else {
            timeToStart = MAX_TIME;
            warmupTimerState = WarmupTimerState.STOPPED;
        }
    }

    public void cancelTimer() {
        countDownTimer.cancel();
        warmupTimerState = WarmupTimerState.STOPPED;
        prefUtilsWarmupTimer.setStartedTime(0);
        timeToStart = MAX_TIME;
    }

    private void onTimerFinish() {
        Toast.makeText(this.getActivity(), "Countdown timer finished!", Toast.LENGTH_SHORT).show();
        prefUtilsWarmupTimer.setStartedTime(0);
        timeToStart = MAX_TIME;
        updatingUI();
        btn_continueToExercises.setVisibility(View.VISIBLE);


    }


    private void updatingUI() {
        if (warmupTimerState == WarmupTimerState.RUNNING) {
            btn_startWarmup.setEnabled(false);
//            noticeText.setText("Countdown Timer is running...");
        } else {
            btn_startWarmup.setEnabled(true);
//            noticeText.setText("Countdown Timer stopped!");
        }
        timer.setText(String.valueOf(timeToStart));
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeToStart * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeToStart -= 1;
                updatingUI();
            }

            @Override
            public void onFinish() {
                warmupTimerState = WarmupTimerState.STOPPED;
                onTimerFinish();
                updatingUI();
            }
        }.start();
    }

    public void setAlarmManager() {
        Log.d(TAG, "setting alarm manager");
        int wakeUpTime = (prefUtilsWarmupTimer.getStartedTime() + MAX_TIME) * 1000;
        AlarmManager am = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), TimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, wakeUpTime, sender);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, wakeUpTime, sender);
        }
    }

    public void removeAlarmManager() {
        Intent intent = new Intent(this.getActivity(), TimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this.getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    private enum WarmupTimerState {
        STOPPED,
        RUNNING
    }

}

