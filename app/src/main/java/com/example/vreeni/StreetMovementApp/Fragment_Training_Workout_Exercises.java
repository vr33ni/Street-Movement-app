package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import static android.content.Context.MODE_PRIVATE;
import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.WORKOUTSCOMPLETED;


/**
 * Created by vreeni on 24/01/2018.
 */

/**
 * Fragment displaying the current Home Exercise that is being done as well as the timer with the remaining Home Workout time
 * contains webview with an embedded vimeo video
 * contains reference to the database => after timer has run out, adds Home Workout as a DocumentReference to a list of completed Home Workouts to the user profile
 */
public class Fragment_Training_Workout_Exercises extends android.support.v4.app.Fragment implements View.OnClickListener {
    private String TAG = "Workout in process: ";

    private Bundle bundle;
    private String exerciseI;
    private String exerciseII;
    private String imgEx1;
    private String vidEx1;
    private ImageView imageEx1;
    private ArrayList<Object> listOfHomeWks;
    private ParkourPark pk;


    //all the information in here will be updated in the user object and then uploaded ot the database
    private Workout myWorkout;
    private String wkReference;
    private long nrOfWorkouts;

    private TextView timer;
    private WebView webView;
    private int time;

    private boolean timerIsRunning;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = getArguments();
        if (null != bundle) {
            pk = bundle.getParcelable("TrainingLocation");
            myWorkout = bundle.getParcelable("Workout");
            exerciseI = bundle.getString("Exercise1");
            exerciseII = bundle.getString("Exercise2");
            vidEx1 = (String) myWorkout.getExerciseI().get("video");
            wkReference = bundle.getString("WorkoutID");
//            time = bundle.getInt("Time");
            //maybe here create exercise objects and set the fields?? (exerciseI = new Exercise(); exerciseI.setDescription, setIsCompleted....)
            //then add them to a list of exercise objects?

        }
        TextView ex1 = (TextView) view.findViewById(R.id.exercise_description);
        ex1.setText(exerciseI);

        //including the webview - vimeo vide
        webView = (WebView) view.findViewById(R.id.webView);
        webView.setInitialScale(1);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Log.e("WebView Log", width + "-" + height);
        String data_html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:white;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe style=\"background:white;\" width=' " + width + "' height='" + height / 2 + "' src=\"" + vidEx1 + "\" frameborder=\"0\"></iframe> </body> </html> ";
        webView.loadDataWithBaseURL("http://vimeo.com", data_html, "text/html", "UTF-8", null);

        //end of webview


        time = 10;

        timer = (TextView) view.findViewById(R.id.workoutTimer);
        timerIsRunning = false;
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
            if (!timerIsRunning) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //start workout after 5 seconds = just for testing to implement a pause for 30 sec
                        startTimer();
                    }
                }, 1000);
            }
        }

    }

    /**
     * workout completion is measured using a timer
     */
    public void startTimer() {
        //get workout duration from bundle information
        timerIsRunning = true;
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
                timer.setText("Workout completed!");
                time = 10;
                timerIsRunning = false;
                addWorkouttoUserDocument();
                //show the navigation drawer hamburger icon instead of back button to easily navigate to another section after the workout
                ((MainActivity) getActivity()).showBackButton(false);


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
                HashMap<String, Object> wkdetails = new HashMap<>();
                wkdetails.put("Activity", db.collection("PredefinedWorkouts").document(wkReference));
                String spotRef = db.collection("ParkourParks").document().getId();
                wkdetails.put("Place", db.collection("ParkourParks").document(spotRef));
                listOfHomeWks.add(wkdetails);
                update.put(LISTOFHOMEWORKOUTS, listOfHomeWks);

                //update the user document
                userDocRef
                        .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document has been saved");
                        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("Training", MODE_PRIVATE);
                        sharedPrefs.edit().remove("Activity").apply();
                        sharedPrefs.edit().remove("Setting").apply();
                        sharedPrefs.edit().remove("Level").apply();
                        sharedPrefs.edit().remove("TrainingFlowStarted").apply();

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
