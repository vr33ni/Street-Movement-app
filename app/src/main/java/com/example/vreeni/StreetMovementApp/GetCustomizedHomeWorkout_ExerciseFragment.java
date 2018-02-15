package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.WORKOUTSCOMPLETED;


/**
 * Created by vreee on 24/01/2018.
 */

public class GetCustomizedHomeWorkout_ExerciseFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private String TAG = "Workout in process: ";

    private Bundle workoutBundle;
    private String exerciseI;
    private String exerciseII;
    private String imgEx1;
    private ImageView imageEx1;
    private ArrayList<Object> listOfHomeWks;


    //all the information in here will be updated in the user object and then uploaded ot the database
    private Workout myWorkout;
    private String wkReference;
    private long nrOfWorkouts;

    private TextView timer;
    private int time;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle workoutBundle = getArguments();
        if (null != workoutBundle) {
            exerciseI = workoutBundle.getString("Exercise1");
            exerciseII = workoutBundle.getString("Exercise2");
            imgEx1 = workoutBundle.getString("Image");
            myWorkout = workoutBundle.getParcelable("Workout");
            wkReference = workoutBundle.getString("WorkoutID");
//            time = workoutBundle.getInt("Time");
            //maybe here create exercise objects and set the fields?? (exerciseI = new Exercise(); exerciseI.setDescription, setIsCompleted....)
            //then add them to a list of exercise objects?

        }
        TextView ex1 = (TextView) view.findViewById(R.id.exercise_description);
        ex1.setText(exerciseI);

        time = 10;

        timer = (TextView) view.findViewById(R.id.workoutTimer);
        Button btn_startWorkout = (Button) view.findViewById(R.id.btn_workout_startWk);
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
        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {

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
                time = 10;
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
        //create reference?
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userDocRef = db.collection("Users").document(currUser.getEmail());
        //access current values saved under this user
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                //initialize the field Nr Of Workouts and the list of Home Workouts
                nrOfWorkouts = currentUser.getWorkoutsCompleted() + 1;
                listOfHomeWks = currentUser.getListOfHomeWorkouts();
                listOfHomeWks.add(db.collection("PredefinedWorkouts").document(wkReference));

                Map<String, Object> update = new HashMap<>();
                //put the updated nr of workouts in the map that is to be uploaded to the database
                update.put(WORKOUTSCOMPLETED, nrOfWorkouts);
                //put a reference to the workout just completed in the map that is to be uploaded to the database
                update.put(LISTOFHOMEWORKOUTS, listOfHomeWks);

                //update the user document
                userDocRef
                        .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document has been saved");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Document could not be saved" + e.toString());
                    }
                });
                Log.d(TAG, "DocumentSnapshot successfully retrieved! " + nrOfWorkouts);
            }
        });
    }
 }
