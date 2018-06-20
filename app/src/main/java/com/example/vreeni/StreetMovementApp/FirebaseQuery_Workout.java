package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * class handing all queries to the database querying a certain activity
 * defined for queries of Workouts and Movement specific challenges
 * a randum generator selects one random activity to be returned
 */
public class FirebaseQuery_Workout {
    private static final String LOG_TAG = "FbQuery_Workout";

    private String activity;
    private String setting;
    private String level;
    private Context context;
    private Workout workout;

    public FirebaseQuery_Workout(String act, String set, String lvl) {
        this.activity = act;
        this.setting = set;
        this.level = lvl;
    }

    public FirebaseQuery_Workout(String act, String set, String lvl, Context context) {
        this.activity = act;
        this.setting = set;
        this.level = lvl;
        this.context = context;
    }

    public void query(FirebaseCallback_Workout callback) {

        if (activity.equals("Workout")) {
            //data comes back
            try {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final CollectionReference wkquery = db.collection("PredefinedWorkouts");
                //query to get all documents that both home workouts and suited for beginners
                com.google.firebase.firestore.Query query = wkquery.whereEqualTo("setting", setting)
                        .whereEqualTo("level", level);

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot qSnap = task.getResult();
                            if (!qSnap.isEmpty()) {
                                //get random document name out of list of all matching documents
                                List<Workout> listOfWorkouts = new ArrayList<>();
                                for (DocumentSnapshot doc : task.getResult()) {
                                    Workout queriedWk = doc.toObject(Workout.class);
                                    queriedWk.setName(doc.getId());
                                    listOfWorkouts.add(queriedWk);
                                }
                                Random ranGen = new Random();
                                int index = ranGen.nextInt(listOfWorkouts.size());
                                Log.d(LOG_TAG, "randome wk generator based on: " + listOfWorkouts);
                                final Workout ranWk = listOfWorkouts.get(index);
                                //random workout object selected and its ID is set as its name

                                //after that, a document reference is created by calling the randomly selected doc name
                                DocumentReference queriedWkRef = wkquery.document(ranWk.getName());
                                queriedWkRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            //access all the information in the document
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                //try to get workout as an object (containing an image)
                                                workout = task.getResult().toObject(Workout.class);
                                                String id = document.getId();
                                                workout.setName(id);
                                                callback.onQuerySuccess(workout);

                                                Log.d(LOG_TAG, "DocumentSnapshot data: " + task.getResult().getData());

                                            } else {
                                                Log.d(LOG_TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(LOG_TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            } else {
                                Log.d("Query Data", "Data is not valid");
                            }
                        }
                    }
                });
                if (workout == null) {
                    throw new FirebaseException("Query could not be executed");
                }
            } catch (FirebaseException ex) {
                callback.onFailure();
            }
            Log.d(activity, "executed");
        } else if (activity.equals("Movement specific challenge")) {
            //query movement specific challenges
            Log.d(LOG_TAG, "movement specific challenge selected");
            queryMovementSpecificChallenge(callback);
        } else {
            //show list of streetmovement locations
        }
    }

//    void querySecond(){}


    public void queryMovementSpecificChallenge(FirebaseCallback_Workout callback) {
        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference wkquery = db.collection("MovementSpecificChallenge");
            //query to get all documents that both home workouts and suited for beginners
            com.google.firebase.firestore.Query query = wkquery.whereEqualTo("setting", setting)
                    .whereEqualTo("level", level);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot qSnap = task.getResult();
                        if (!qSnap.isEmpty()) {
                            //get random document name out of list of all matching documents
                            List<Workout> listOfWorkouts = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Workout queriedWk = doc.toObject(Workout.class);
                                queriedWk.setName(doc.getId());
                                listOfWorkouts.add(queriedWk);
                            }
                            Random ranGen = new Random();
                            int index = ranGen.nextInt(listOfWorkouts.size());
                            Log.d(LOG_TAG, "randome wk generator based on: " + listOfWorkouts);
                            final Workout ranWk = listOfWorkouts.get(index);
                            //random workout object selected and its ID is set as its name

                            //after that, a document reference is created by calling the randomly selected doc name
                            DocumentReference queriedWkRef = wkquery.document(ranWk.getName());
                            queriedWkRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        //access all the information in the document
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            //try to get workout as an object (containing an image)
                                            workout = task.getResult().toObject(Workout.class);
                                            String id = document.getId();
                                            workout.setName(id);
                                            callback.onQuerySuccess(workout);

                                            Log.d(LOG_TAG, "DocumentSnapshot data: " + task.getResult().getData().get("exerciseI"));

                                        } else {
                                            Log.d(LOG_TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(LOG_TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.d("Query Data", "Data is not valid");
                        }
                    }
                }
            });
            if (workout == null) {
                throw new FirebaseException("Query could not be executed");
            }
        } catch (FirebaseException ex) {
            callback.onFailure();
        }
        Log.d(activity, "executed");
    }
}