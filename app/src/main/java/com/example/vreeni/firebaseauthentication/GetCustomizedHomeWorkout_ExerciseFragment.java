package com.example.vreeni.firebaseauthentication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.firebaseauthentication.User.NICKNAME;
import static com.example.vreeni.firebaseauthentication.User.TAG;
import static com.example.vreeni.firebaseauthentication.User.WORKOUTS;

/**
 * Created by vreee on 24/01/2018.
 */

public class GetCustomizedHomeWorkout_ExerciseFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    //load view for this segment, load textViews whose text will be set based on which exercise is being displayed
    private Bundle workoutBundle;

    //all the information in here will be set in the workout object and then uploaded ot the database
    private Workout myWorkout;

    private Button btn_startWorkout;
    private TextView timer;
    private int time;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        workoutBundle = getArguments();

        time = 60;
        timer = (TextView) view.findViewById(R.id.workoutTimer);
        btn_startWorkout = (Button) view.findViewById(R.id.btn_workout_startWk);
        btn_startWorkout.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_exercise, container, false);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_workout_startWk) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //start workout after 5 seconds = just for testing to implement a pause for 30 sec
                    startTimer();
                }
            }, 5000);
        }

    }

    public void startTimer() {
        //get workout duration from bundle information
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
//                if (isCancelled) {
//                    cancel();
//                    isCancelled = false;
//                } else {
                    timer.setText("0:" + checkDigit(time));
                    time--;
//                }
            }
            public void onFinish() {
                timer.setText("Warm-up completed!");
                time = 60;
                addWorkouttoUserDocument();
//                //Enable the start button
//                btn_startWarmup.setEnabled(false);
////                //Disable the pause, resume and cancel button
//                btn_cancelWarmup.setEnabled(false);
//                btn_startWarmup.setVisibility(View.GONE);
//                btn_cancelWarmup.setVisibility(View.GONE);
//                btn_continueToExercises.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    public void addWorkouttoUserDocument() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userDocRef = db.collection("Users").document(currUser.getDisplayName());
        if (userDocRef != null) {
            //add workout as a reference?
            Map<String, Object> dataUpdate = new HashMap<String, Object>();
            int nrOfWorkouts = (int) dataUpdate.get(WORKOUTS);
            dataUpdate.put(WORKOUTS, nrOfWorkouts+1);
            userDocRef
                    .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Document has been saved");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Document could not be saved");
                }
            });
        } else {
            //throw exception Username not Found
        }

    }
}
