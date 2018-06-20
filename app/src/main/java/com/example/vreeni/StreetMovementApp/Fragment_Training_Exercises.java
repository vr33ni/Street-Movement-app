package com.example.vreeni.StreetMovementApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFMOVEMENTSPECIFICCHALLENGES;
import static com.example.vreeni.StreetMovementApp.User.LISTOFOUTDOORWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFPLACES;
import static com.example.vreeni.StreetMovementApp.User.LISTOFSTREETMOVEMENTCHALLENGES;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by vreeni on 24/01/2018.
 */

/**
 * Fragment displaying the current Home Exercise that is being done as well as the timer with the remaining Home Workout time
 * contains webview with an embedded vimeo video
 * contains reference to the database => after timer has run out, adds Home Workout as a DocumentReference to a list of completed Home Workouts to the user profile
 */
public class Fragment_Training_Exercises extends Fragment implements View.OnClickListener {
    private String TAG = "Workout: ";

    //popup window views
    private PopupWindow popupWindow_activityCompleted;
    private PopupWindow popupWindow_cancelTimer;
    private ImageView iv_activityCompleted;
    //extra awards based on nr of activities/places etc.
//    private ImageView iv_explorerStatus; //after having trained at 10 + different locations
//    private ImageView iv_ninja; //after having completed 5+ movement specific challenges


    private ArrayList<Object> listOfHomeWks;
    private ArrayList<Object> listOfOutdoorWks;
    private ArrayList<Object> listOfPlaces;


    private final ArrayList<HashMap<String, Object>> listOfActiveUsers = new ArrayList<>();


    //all the information in here will be updated in the user object and then uploaded ot the database
    private Workout myWorkout;
    private ParkourPark pk;
    private Exercise exercise;
    private String wkReference;
    private String activityType;

    private TextView ex1;
    private WebView webView;
    private int time;

    private boolean timerIsRunning;
    private PrefUtilsActivityTimer prefUtilsActivityTimer;
    private TextView timer;
    private TextView noticeText;
    private Button btn_startWorkout;
    private Button btn_stopWorkout;
    private CountDownTimer countDownTimer;
    private int timeToStart;
    private TimerState timerState;
    private static final int MAX_TIME = 12; //Time length is 12 seconds




    public static Fragment_Training_Exercises newInstance(Workout wk, ParkourPark pk) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Exercises fragment = new Fragment_Training_Exercises();
        bundle.putParcelable("Workout", wk);
        bundle.putParcelable("TrainingLocation", pk);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myWorkout = getArguments().getParcelable("Workout");
            wkReference = myWorkout.getName();
            if (getArguments().containsKey("TrainingLocation")) {
                pk = getArguments().getParcelable("TrainingLocation");
            }
            activityType = myWorkout.getActivity();
            Log.d(TAG, "bundle info:" + activityType);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_exercise, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ex1 = (TextView) view.findViewById(R.id.exercise_description);

        //including the webview - vimeo vide
        webView = (WebView) view.findViewById(R.id.webView);

        time = 10;
        timer = (TextView) view.findViewById(R.id.workoutTimer);
        timerIsRunning = false;
        noticeText = (TextView) view.findViewById(R.id.tv_timer_noticetext);
        btn_startWorkout = (Button) view.findViewById(R.id.btn_workout_startWk);
        btn_stopWorkout = (Button) view.findViewById(R.id.btn_workout_stopWk);
    }


    @Override
    public void onStart() {
        super.onStart();

        getExerciseReferencesFromFirebase(myWorkout);


        prefUtilsActivityTimer = new PrefUtilsActivityTimer(getApplicationContext());
        btn_startWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerState == TimerState.STOPPED) {
                    prefUtilsActivityTimer.setStartedTime((int) getNow());
                    startTimer();
                    timerState = TimerState.RUNNING;
                }
            }
        });
        btn_stopWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelTimerYesNoPopupWindow();

            }
        });
    }

    public void cancelTimer() {
        countDownTimer.cancel();
        timerState = TimerState.STOPPED;
        prefUtilsActivityTimer.setStartedTime(0);
        timeToStart = MAX_TIME;
    }

    public void showTimerStoppedToast() {
        Toast.makeText(this.getActivity(), "Activity cancelled!", Toast.LENGTH_SHORT).show();
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
        if (timerState == TimerState.RUNNING) {
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
        if (timerState == TimerState.RUNNING) {
            cancelTimer();
            updatingUI();
        } else {

        }
//        cancelTimer();
//        updatingUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void addAsActiveUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference parkRef;
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if (pk != null) {
            parkRef = db.collection("ParkourParks").document(pk.getName());
            HashMap<String, Object> activeUsers = new HashMap<>();
            activeUsers.put("user", userDocRef);
            activeUsers.put("activeSince", "add start time");
            listOfActiveUsers.add(activeUsers);
            pk.setListOfActiveUsers(listOfActiveUsers);

            Map<String, Object> dataUpdate = new HashMap<String, Object>();
            dataUpdate.put("listOfActiveUsers", pk.getListOfActiveUsers());
            parkRef
                    .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "active user has been added ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "active user could not be added");
                }
            });
        }

    }


    public void removeAsActiveUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference parkRef;
        if (pk != null) {
            parkRef = db.collection("ParkourParks").document(pk.getName());
            listOfActiveUsers.clear();
            pk.setListOfActiveUsers(listOfActiveUsers);
            Map<String, Object> dataUpdate = new HashMap<String, Object>();
            dataUpdate.put("listOfActiveUsers", pk.getListOfActiveUsers());
            parkRef
                    .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "active user has been added ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "active user could not be added");
                }
            });
        }
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_cancel_timer_cancel) {
            popupWindow_cancelTimer.dismiss();
        }
        if (v.getId() == R.id.btn_cancel_timer_yes) {
            cancelTimer();
            showTimerStoppedToast();
//            updatingUI();
            //redirect to home screen
            HomeFragment home = HomeFragment.newInstance();
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, home, "home")
                    .addToBackStack(null)
                    .commit();
            popupWindow_cancelTimer.dismiss();
        }
        if (v.getId() == R.id.btn_cancel_timer_no) {
            popupWindow_cancelTimer.dismiss();
        }


        if (v.getId() == R.id.btn_activityCompleted_continue) {
            //continue to home screen
            HomeFragment home = HomeFragment.newInstance();
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, home, "home")
                    .addToBackStack(null)
                    .commit();
            popupWindow_activityCompleted.dismiss();
        }
        if (v.getId() == R.id.btn_activityCompleted_goToProfile) {
            //go to personal stats section
            UserProfile_Account fragment_setting = UserProfile_Account.newInstance();
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment_setting, "Personal Stats")
                    .addToBackStack("personalStats")
                    .commit();
            popupWindow_activityCompleted.dismiss();
        }
        if (v.getId() == R.id.btn_activityCompleted_leaderboard) {
            //go to leaderboard
            Fragment_Leaderboard fragment_leaderboard = Fragment_Leaderboard.newInstance(this.getContext());
            (this.getActivity().getSupportFragmentManager().beginTransaction())
                    .replace(R.id.fragment_container, fragment_leaderboard, "Leaderboard")
                    .addToBackStack("leaderboard")
                    .commit();
            popupWindow_activityCompleted.dismiss();
        }

    }

    /**
     * workout completion is measured using a timer
     */
//    public void startTimer() {
//        //get workout duration from bundle information
//        timerIsRunning = true;
//        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
////                if (isCancelled) {
////                    cancel();
////                    isCancelled = false;
////                } else {
//                timer.setText("0:" + checkDigit(time));
//                time--;
////                }
//            }
//
//            public void onFinish() {
//                timer.setText("Activity completed!");
//                time = 10;
//                timerIsRunning = false;
//                addWorkouttoUserDocument();
//                removeAsActiveUser();
//                //show the navigation drawer hamburger icon instead of back button to easily navigate to another section after the workout
//                ((MainActivity) getActivity()).showBackButton(false);
//
//
////                //Enable the start button
////                btn_startWarmup.setEnabled(false);
//////                //Disable the pause, resume and cancel button
////                btn_cancelWarmup.setEnabled(false);
////                btn_startWarmup.setVisibility(View.GONE);
////                btn_cancelWarmup.setVisibility(View.GONE);
////                btn_continueToExercises.setVisibility(View.VISIBLE);
//            }
//        }.start();
//    }


    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    /**
     * add the workout to the respective list of home or outdoorworkouts on the database and
     * update the respective leaderboard entry
     */
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

                String fieldname_list;
                ArrayList<Object> fieldValue_list;
                Map<String, Object> update = new HashMap<>();

                //check what to query by checking what type of activity
                if (activityType.equals("Workout")) {
                    Log.d(TAG, "activity = workout");
                    if (pk == null) {
                        fieldname_list = LISTOFHOMEWORKOUTS;
                        fieldValue_list = currentUser.getListOfHomeWorkouts();
                        listOfPlaces = currentUser.getListOfPlaces();
                    } else {
                        fieldname_list = LISTOFOUTDOORWORKOUTS;
                        fieldValue_list = currentUser.getListOfOutdoorWorkouts();
                        listOfPlaces = currentUser.getListOfPlaces();
                    }
                } else if (activityType.equals("Movement specific challenge")) {
                    Log.d(TAG, "activity = move specific");

                    fieldname_list = LISTOFMOVEMENTSPECIFICCHALLENGES;
                    fieldValue_list = currentUser.getListOfStreetMovementChallenges();
                    listOfPlaces = currentUser.getListOfPlaces();
                } else if (activityType.equals("Street Movement challenge")) {
                    Log.d(TAG, "activity = sm challenge");

                    fieldname_list = LISTOFSTREETMOVEMENTCHALLENGES;
                    fieldValue_list = currentUser.getListOfStreetMovementChallenges();
                    listOfPlaces = currentUser.getListOfPlaces();
                } else {
                    fieldname_list = "";
                    fieldValue_list = new ArrayList<>();
                    listOfPlaces = currentUser.getListOfPlaces();
                }

                //put a reference to the workout just completed in the map that is to be uploaded to the database
                HashMap<String, Object> wkdetails = new HashMap<>();
                wkdetails.put("activity", activityType);
                wkdetails.put("activityReference", db.collection("PredefinedWorkouts").document(wkReference));
                Calendar calender = Calendar.getInstance();
                int weekNr = calender.get(Calendar.WEEK_OF_YEAR);
                wkdetails.put("week", weekNr);

                if (pk == null) {
                    //meaning that this is a home workout with no specific training location selected
                    wkdetails.put("place", "Home");
                    fieldValue_list.add(wkdetails);
                    update.put(fieldname_list, fieldValue_list);

                    //update the user document
                    userDocRef
                            .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "HomeWorkout has been saved");
                            //do this asynchronously??
                            updateScore(db, userDocRef);
                            Log.d(TAG, "updating score");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "HomeWorkout could not be saved" + e.toString());
                        }
                    });


                } else {
                    //training location received in bundle was not null => workout was done at a specific training location
                    DocumentReference spotref = db.collection("ParkourParks").document(pk.getName());
                    wkdetails.put("place", spotref);
                    fieldValue_list.add(wkdetails);

                    //update list of places the user has been to
                    HashMap<String, Object> places = new HashMap<>();
                    places.put("place", spotref);
                    places.put("coordinates", pk.getCoordinates());
                    places.put("week", calender.get(Calendar.WEEK_OF_YEAR));
                    listOfPlaces.add(places);

                    Log.d(TAG, "fieldname list" + fieldname_list + " fieldvalue list: " + fieldValue_list);

                    update.put(fieldname_list, fieldValue_list);
                    update.put(LISTOFPLACES, listOfPlaces);

                    //update the user document
                    userDocRef
                            .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do these tasks asynchronously??
                            updateScore(db, userDocRef);
                            Log.d(TAG, "updating leaderboard entry - score");
                            updatePlaces(spotref);
                            Log.d(TAG, "updating leaderboard entry - places");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Outdoor Activity could not be saved" + e.toString());
                        }
                    });

                }
            }
        });
    }

    //include parameter specifying the type of activity to update
    public void updateScore(FirebaseFirestore db, DocumentReference userdocRef) {
        DocumentReference scoreRef = db.collection("Scores").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        scoreRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    LeaderboardEntry score = documentSnapshot.toObject(LeaderboardEntry.class);
                    Log.d(TAG, "leaderboard entry exists already, updating it...");

                    Map<String, Object> update = new HashMap<>();

                    if (activityType.equals("Workout")) {
                        //initialize the field Nr Of Workouts and the list of Home Workouts
                        //put the updated nr of workouts in the map that is to be uploaded to the database
                        update.put("nrOfActivities_total", score.getNrOfWorkouts_total()
                                + score.getNrOfMovementSpecificChallenges_total()
                                + score.getNrOfStreetMovementChallenges_total()
                                + 1);
                        update.put("nrOfWorkouts_total", score.getNrOfWorkouts_total() + 1);
                        update.put("nrOfActivities_weekly", score.getNrOfWorkouts_weekly()
                                + score.getNrOfMovementSpecificChallenges_weekly()
                                + score.getNrOfStreetMovementChallenges_weekly()
                                + 1);
                        update.put("nrOfWorkouts_weekly", score.getNrOfWorkouts_weekly() + 1);

                    } else if (activityType.equals("Movement specific challenge")) {
                        //put the updated nr of workouts in the map that is to be uploaded to the database
                        update.put("nrOfActivities_total", score.getNrOfWorkouts_total()
                                + score.getNrOfMovementSpecificChallenges_total()
                                + score.getNrOfStreetMovementChallenges_total()
                                + 1);
                        update.put("nrOfMovementSpecificChallenges_total", score.getNrOfMovementSpecificChallenges_total() + 1);
                        update.put("nrOfActivities_weekly",
                                score.getNrOfWorkouts_weekly()
                                        + score.getNrOfMovementSpecificChallenges_weekly()
                                        + score.getNrOfStreetMovementChallenges_weekly()
                                        + 1);
                        update.put("nrOfMovementSpecificChallenges_weekly", score.getNrOfMovementSpecificChallenges_weekly() + 1);
                    }
//                    else if (activityType.equals("Street Movement challenge")) {
                    else {
                        update.put("nrOfActivities_total", score.getNrOfWorkouts_total()
                                + score.getNrOfMovementSpecificChallenges_total()
                                + score.getNrOfStreetMovementChallenges_total()
                                + 1);
                        update.put("nrOfStreetMovementChallenges_total", score.getNrOfStreetMovementChallenges_total() + 1 + 1);
                        update.put("nrOfActivities_weekly",
                                score.getNrOfWorkouts_weekly()
                                        + score.getNrOfMovementSpecificChallenges_weekly()
                                        + score.getNrOfStreetMovementChallenges_weekly()
                                        + 1);
                        update.put("nrOfStreetMovementChallenges_weekly", score.getNrOfStreetMovementChallenges_weekly() + 1 + 1);
                    }
                    scoreRef.set(update, SetOptions.merge()).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Leaderboard entry has been saved");
                                    openActivityCompletedPopupWindow();
                                }
                            }).

                            addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Leaderboard Entry could not be saved");
                                }
                            });
                } else {
                    //creating new leader board entry

                    Map<String, Object> update = new HashMap<>();

                    if (activityType.equals("Workout")) {
                        update.put("nrOfWorkouts_total", 1);
                        update.put("nrOfWorkouts_weekly", 1);
                    } else if (activityType.equals("Movement specific challenge")) {
                        update.put("nrOfMovementSpecificChallenges_total", 1);
                        update.put("nrOfMovementSpecificChallenges_weekly", 1);
                    } else {
                        update.put("nrOfStreetMovementChallenges_total", 1);
                        update.put("nrOfStreetMovementChallenges_weekly", 1);
                    }

                    //add empty fields for outdoor activities as well, so the leaderboard object can be parcelable
                    int nrOfPlaces_total = 0;
                    int nrOfPlaces_weekly = 0;
                    ArrayList<Object> listOfPlaces_total = new ArrayList<>();
                    ArrayList<Object> listOfPlaces_weekly = new ArrayList<>();


                    //put the updated nr of workouts in the map that is to be uploaded to the database
                    update.put("nrOfActivities_total", 1);
                    update.put("nrOfActivities_weekly", 1);
                    update.put("userReference", userdocRef);
                    //add empty fields for outdoor activities as well, so the leaderboard object can be parcelable
                    update.put("total places", nrOfPlaces_total);
                    update.put("weekly places", nrOfPlaces_weekly);
                    update.put("total listOfPlaces", listOfPlaces_total);
                    update.put("weekly listOfPlaces", listOfPlaces_weekly);


                    scoreRef.set(update, SetOptions.merge()).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Leaderboard Entry has been saved");
                                    openActivityCompletedPopupWindow();
                                }
                            }).

                            addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Leaderboard Entry could not be saved");
                                }
                            });
                }
            }
        });
    }

    /**
     * after each completed activity - in case an object of a training location was passed -
     * add the nr of places and list of places total and weekly to the user's leaderboard entry in the Scores collection
     *
     * @param spotref
     */
    public void updatePlaces(DocumentReference spotref) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference scoreRef = db.collection("Scores").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        scoreRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                LeaderboardEntry score = documentSnapshot.toObject(LeaderboardEntry.class);
                //initialize the field Nr Of Workouts and the list of places
                int nrOfPlaces_total;
                int nrOfPlaces_weekly;
                ArrayList<Object> listOfPlaces_total;
                ArrayList<Object> listOfPlaces_weekly;
                if ((score.getListOfPlaces_total() != null) && (score.getListOfPlaces_weekly() != null) &&
                        (score.getNrOfPlaces_total() != 0) && (score.getNrOfPlaces_weekly() != 0)) {
                    listOfPlaces_total = score.getListOfPlaces_total();
                    listOfPlaces_total.add(spotref);
                    listOfPlaces_weekly = score.getListOfPlaces_weekly();
                    listOfPlaces_weekly.add(spotref);
                    nrOfPlaces_total = score.getNrOfPlaces_total() + 1;
                    nrOfPlaces_weekly = score.getNrOfPlaces_weekly() + 1;
                } else {
                    listOfPlaces_total = new ArrayList<>();
                    listOfPlaces_total.add(spotref);
                    listOfPlaces_weekly = new ArrayList<>();
                    listOfPlaces_weekly.add(spotref);
                    nrOfPlaces_total = 1;
                    nrOfPlaces_weekly = 1;
                }

                Map<String, Object> update = new HashMap<>();
                //put the updated nr of workouts in the map that is to be uploaded to the database
                update.put("nrOfPlaces_total", nrOfPlaces_total);
                update.put("nrOfPlaces_weekly", nrOfPlaces_weekly);
                update.put("listOfPlaces_total", listOfPlaces_total);
                update.put("listOfPlaces_weekly", listOfPlaces_weekly);

                scoreRef.set(update, SetOptions.merge()).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "New User Document has been saved");
                            }
                        }).

                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "New User Document could not be saved");
                            }
                        });
            } else {

                int nrOfPlaces_total = 1;
                int nrOfPlaces_weekly = 1;
                ArrayList<Object> listOfPlaces_total = new ArrayList<>();
                listOfPlaces_total.add(spotref);
                ArrayList<Object> listOfPlaces_weekly = new ArrayList<>();
                listOfPlaces_weekly.add(spotref);

                Map<String, Object> update = new HashMap<>();
                //put the updated nr of workouts in the map that is to be uploaded to the database
                update.put("total places", nrOfPlaces_total);
                update.put("weekly places", nrOfPlaces_weekly);
                update.put("total listOfPlaces", listOfPlaces_total);
                update.put("weekly listOfPlaces", listOfPlaces_weekly);

                scoreRef.set(update, SetOptions.merge()).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "New User Document has been saved");
                            }
                        }).

                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "New User Document could not be saved");
                            }
                        });
            }
        });

    }

    public void openCancelTimerYesNoPopupWindow() {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_workout_cancel_timer, null);
        popupWindow_cancelTimer = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancel_timer_cancel);
        btn_cancel.setOnClickListener(this);

        Button btn_yes = (Button) layout.findViewById(R.id.btn_cancel_timer_yes);
        btn_yes.setOnClickListener(this);
        btn_yes.setEnabled(true);

        Button btn_no;
        btn_no = (Button) layout.findViewById(R.id.btn_cancel_timer_no);
        btn_no.setOnClickListener(this);
        btn_no.setEnabled(true);
        popupWindow_cancelTimer.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindow_cancelTimer);
    }


    public void openActivityCompletedPopupWindow() {
        View layout = getLayoutInflater().inflate(R.layout.popup_window_activity_completed, null);
        popupWindow_activityCompleted = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        iv_activityCompleted = (ImageView) layout.findViewById(R.id.iv_activityCompleted_badge);

        Button btn_continue = (Button) layout.findViewById(R.id.btn_activityCompleted_continue);
        btn_continue.setOnClickListener(this);

        Button btn_goToLeaderboard = (Button) layout.findViewById(R.id.btn_activityCompleted_leaderboard);
        btn_goToLeaderboard.setOnClickListener(this);
        btn_goToLeaderboard.setEnabled(true);

        Button btn_goToPersonalStats;
        btn_goToPersonalStats = (Button) layout.findViewById(R.id.btn_activityCompleted_goToProfile);
        btn_goToPersonalStats.setOnClickListener(this);
        btn_goToPersonalStats.setEnabled(true);
//        int x = Resources.getSystem().getDisplayMetrics().widthPixels/2-150;
//        int y = Resources.getSystem().getDisplayMetrics().heightPixels/2-100;
        popupWindow_activityCompleted.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindow_activityCompleted);
        Log.d(TAG, "opening a popup window");
    }


    /**
     * once popup window is open, dim everything behind it for the time it is opened
     *
     * @param popupWindow based on which popup window is set as a parameter, it is taken as reference point and everything behind is dimmed
     */
    private void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    public void getExerciseReferencesFromFirebase(Workout workout) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference wkquery = db.collection("Exercises");
        DocumentReference eRef = (DocumentReference) workout.getListOfExercises().get(0);
        eRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    exercise = documentSnapshot.toObject(Exercise.class);
                    Log.d(TAG, "recyclerview_item_exercise: " + exercise.toString());

                    String nameEx1 = exercise.getName();
                    ex1.setText(nameEx1);

                    if (exercise.getVideo() != null) {
                        String vidEx1 = exercise.getVideo();
                        setupVideo(vidEx1);
                    }


                } else {
                }
            }
        });
    }

    public void setupVideo(String vidEx1) {
        //set up the webview - vimeo vide
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
    }

    private long getNow() {
        Calendar rightNow = Calendar.getInstance();
        return rightNow.getTimeInMillis() / 1000;
    }

    private void initTimer() {
        long startTime = prefUtilsActivityTimer.getStartedTime();
        if (startTime > 0) {
            timeToStart = (int) (MAX_TIME - (getNow() - startTime));
            if (timeToStart <= 0) {
                // TIMER EXPIRED
                timeToStart = MAX_TIME;
                timerState = TimerState.STOPPED;
                onTimerFinish();
            } else {
                startTimer();
                timerState = TimerState.RUNNING;
            }
        } else {
            timeToStart = MAX_TIME;
            timerState = TimerState.STOPPED;
        }
    }

    private void onTimerFinish() {
        Toast.makeText(this.getActivity(), "Countdown timer finished!", Toast.LENGTH_SHORT).show();
        prefUtilsActivityTimer.setStartedTime(0);
        timeToStart = MAX_TIME;
        updatingUI();
        addWorkouttoUserDocument();
        removeAsActiveUser();
    }


    private void updatingUI() {
        if (timerState == TimerState.RUNNING) {
            btn_startWorkout.setEnabled(false);
            noticeText.setText("Countdown Timer is running...");
        } else {
//            btn_startWorkout.setEnabled(true);
            noticeText.setText("Countdown Timer stopped!");
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
                timerState = TimerState.STOPPED;
                onTimerFinish();
                updatingUI();
            }
        }.start();
    }

    public void setAlarmManager() {
        int wakeUpTime = (prefUtilsActivityTimer.getStartedTime() + MAX_TIME) * 1000;
        AlarmManager am = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getActivity(), TimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this.getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(wakeUpTime, sender), sender);
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

    private enum TimerState {
        STOPPED,
        RUNNING
    }
}



