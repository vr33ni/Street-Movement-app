package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFOUTDOORWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFPLACES;
import static com.example.vreeni.StreetMovementApp.User.WORKOUTSCOMPLETED;


/**
 * Created by vreeni on 24/01/2018.
 */

/**
 * Fragment displaying the current Home Exercise that is being done as well as the timer with the remaining Home Workout time
 * contains webview with an embedded vimeo video
 * contains reference to the database => after timer has run out, adds Home Workout as a DocumentReference to a list of completed Home Workouts to the user profile
 */
public class Fragment_Training_Workout_Exercises extends Fragment implements View.OnClickListener {
    private String TAG = "Workout: ";

    private String exerciseI;
    private String exerciseII;
    private String imgEx1;
    private String vidEx1;
    private ImageView imageEx1;
    private ArrayList<Object> listOfHomeWks;
    private ArrayList<Object> listOfOutdoorWks;
    private ArrayList<Object> listOfPlaces;
    private final ArrayList<HashMap<String, Object>> listOfActiveUsers = new ArrayList<>();


    //all the information in here will be updated in the user object and then uploaded ot the database
    private Workout myWorkout;
    private ParkourPark pk;
    private String wkReference;
    private long nrOfWorkouts;
    String descriptionEx1;

    private TextView ex1;
    private TextView timer;
    private WebView webView;
    private int time;

    private boolean timerIsRunning;

    public static Fragment_Training_Workout_Exercises newInstance(Workout wk, ParkourPark pk) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Workout_Exercises fragment = new Fragment_Training_Workout_Exercises();
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
            descriptionEx1 = myWorkout.getExerciseI().get("description").toString();
            wkReference = myWorkout.getName();
            vidEx1 = (String) myWorkout.getExerciseI().get("video");
            if (getArguments().containsKey("TrainingLocation")) {
                pk = getArguments().getParcelable("TrainingLocation");

            }
            Log.d(TAG, "bundle info:" + getArguments());
            Log.d(TAG, "bundle info - video:" + vidEx1);
            Log.d(TAG, "bundle info - wkRef:" + wkReference);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_exercise, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//            pk = bundle.getParcelable("TrainingLocation");
//            myWorkout = bundle.getParcelable("Workout");
//            exerciseI = bundle.getString("Exercise1");
//            exerciseII = bundle.getString("Exercise2");
//            vidEx1 = (String) myWorkout.getExerciseI().get("video");
//            wkReference = bundle.getString("WorkoutID");
//            time = bundle.getInt("Time");
        //maybe here create exercise objects and set the fields?? (exerciseI = new Exercise(); exerciseI.setDescription, setIsCompleted....)
        //then add them to a list of exercise objects?

        ex1 = (TextView) view.findViewById(R.id.exercise_description);

        //including the webview - vimeo vide
        webView = (WebView) view.findViewById(R.id.webView);

        time = 10;
        timer = (TextView) view.findViewById(R.id.workoutTimer);
        timerIsRunning = false;
        Button btn_startWorkout = (Button) view.findViewById(R.id.btn_workout_startWk);
        btn_startWorkout.setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        ex1.setText(descriptionEx1);

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


        Log.d(TAG, "training info" + myWorkout);
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
        if (v.getId() == R.id.btn_workout_startWk) {
            if (!timerIsRunning) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //start workout after 5 seconds = just for testing to implement a pause for 30 sec
                        startTimer();
                        //add to trainingLocation document as a reference in the list of active users
                        //if allowed , add as active user to the list of active users
                        addAsActiveUser();

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
                removeAsActiveUser();
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
                //initialize the field Nr Of Workouts and the list of Home Workouts
                nrOfWorkouts = currentUser.getWorkoutsCompleted() + 1;
                listOfHomeWks = currentUser.getListOfHomeWorkouts();
                listOfOutdoorWks = currentUser.getListOfOutdoorWorkouts();
                listOfPlaces = currentUser.getListOfPlaces();


                Map<String, Object> update = new HashMap<>();
                //put the updated nr of workouts in the map that is to be uploaded to the database
                update.put(WORKOUTSCOMPLETED, nrOfWorkouts);

                //put a reference to the workout just completed in the map that is to be uploaded to the database
                HashMap<String, Object> wkdetails = new HashMap<>();
                wkdetails.put("activity", "Workout");
                wkdetails.put("activityReference", db.collection("PredefinedWorkouts").document(wkReference));
                Calendar calender = Calendar.getInstance();
                int weekNr = calender.get(Calendar.WEEK_OF_YEAR);
                wkdetails.put("week", weekNr);

                if (pk == null) {
                    //meaning that this is a home workout with no specific training location selected
                    wkdetails.put("place", "Home");
                    listOfHomeWks.add(wkdetails);
                    update.put(LISTOFHOMEWORKOUTS, listOfHomeWks);

                    //update the user document
                    userDocRef
                            .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "HomeWorkout has been saved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "HomeWorkout could not be saved" + e.toString());
                        }
                    });
                    updateScore();
                    Log.d(TAG, "updating score");

                } else {
                    //training location received in bundle was not null => workout was done at a specific training location
                    DocumentReference spotref = db.collection("ParkourParks").document(pk.getName());
                    wkdetails.put("place", spotref);
                    listOfOutdoorWks.add(wkdetails);

                    //update list of places the user has been to
                    HashMap<String, Object> places = new HashMap<>();
                    places.put("place", spotref);
                    places.put("coordinates", pk.getCoordinates());
                    places.put("week", calender.get(Calendar.WEEK_OF_YEAR));
                    listOfPlaces.add(places);

                    update.put(LISTOFOUTDOORWORKOUTS, listOfOutdoorWks);
                    update.put(LISTOFPLACES, listOfPlaces);

                    //update the user document
                    userDocRef
                            .set(update, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Outdoor Workout has been saved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Outdoor Workout could not be saved" + e.toString());
                        }
                    });
                    updateScore();
                    Log.d(TAG, "updating leaderboard entry - score");
//                    updatePlaces(spotref);
                    Log.d(TAG, "updating leaderboard entry - places");

                }
            }
        });
    }

    public void updateScore() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference scoreRef = db.collection("Scores").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        scoreRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    LeaderboardEntry score = documentSnapshot.toObject(LeaderboardEntry.class);
                    //initialize the field Nr Of Workouts and the list of Home Workouts
                    int nrOfWorkouts_total = score.getNrOfWorkouts_total() + 1;
                    int nrOfWorkouts_weekly = score.getNrOfWorkouts_weekly() + 1;
                    int nrOfActivities_total = score.getNrOfWorkouts_total()
                            + score.getNrOfMovementSpecificChallenges_total()
                            + score.getNrOfStreetMovementChallenges_total()
                            + 1;
                    int nrOfActivities_weekly = score.getNrOfWorkouts_weekly()
                            + score.getNrOfMovementSpecificChallenges_weekly()
                            + score.getNrOfStreetMovementChallenges_weekly()
                            + 1;

                    Map<String, Object> update = new HashMap<>();
                    //put the updated nr of workouts in the map that is to be uploaded to the database
                    update.put("total activities", nrOfActivities_total);
                    update.put("total workouts", nrOfWorkouts_total);
                    update.put("weekly activities", nrOfActivities_weekly);
                    update.put("weekly workouts", nrOfWorkouts_weekly);

                    scoreRef.set(update, SetOptions.merge()).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Leaderboard entry has been saved");
                                }
                            }).

                            addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Leaderboard Entry could not be saved");
                                }
                            });
                } else {

                    int nrOfWorkouts_total = listOfHomeWks.size() + listOfOutdoorWks.size();
                    int nrOfWorkouts_weekly = listOfHomeWks.size() + listOfOutdoorWks.size();
                    int nrOfActivities_total = listOfHomeWks.size() + listOfOutdoorWks.size(); //add other lists
                    int nrOfActivities_weekly = listOfHomeWks.size() + listOfOutdoorWks.size();


                    Map<String, Object> update = new HashMap<>();
                    //put the updated nr of workouts in the map that is to be uploaded to the database
                    update.put("total activities", nrOfActivities_total);
                    update.put("total workouts", nrOfWorkouts_total);
                    update.put("weekly activities", nrOfActivities_weekly);
                    update.put("weekly workouts", nrOfWorkouts_weekly);

                    scoreRef.set(update, SetOptions.merge()).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Leaderboard Entry has been saved");
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


//        db.collection("Scores")
//                .whereEqualTo("listOfPlaces.place", "Amager Strandpark")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "TEST 1: " + task.getResult().size());
//
//
//                        }
//
//                    }
//
//
//                });
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
                        (score.getNrOfDifferentSpots_total() != 0) && (score.getNrOfDifferentSpots_weekly() != 0)) {
                    listOfPlaces_total = score.getListOfPlaces_total();
                    listOfPlaces_total.add(spotref);
                    listOfPlaces_weekly = score.getListOfPlaces_weekly();
                    listOfPlaces_weekly.add(spotref);
                    nrOfPlaces_total = score.getNrOfDifferentSpots_total() + 1;
                    nrOfPlaces_weekly = score.getNrOfDifferentSpots_weekly() + 1;
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
}


