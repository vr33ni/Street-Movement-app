package com.example.vreeni.firebaseauthentication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * Created by vreee on 20/12/2017.
 */

public class GetCustomizedHomeWorkoutLevelFragment extends Fragment implements View.OnClickListener {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    boolean beginner;
    boolean intermediate;
    boolean advanced;

    private  Bundle beginnerBundle; //to pass arguments to the next fragment

    private String TAG = "Choose Level: ";


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnPredefHomeWorkoutBeginner = (Button) view.findViewById(R.id.btn_predef_homeworkout_beginner);
        btnPredefHomeWorkoutBeginner.setOnClickListener(this);

        Button btnPredefHomeWorkoutIntermediate = (Button) view.findViewById(R.id.btn_predef_homeworkout_intermediate);
        btnPredefHomeWorkoutIntermediate.setOnClickListener(this);

        Button btnPredefHomeWorkoutAdvanced = (Button) view.findViewById(R.id.btn_predef_homeworkout_advanced);
        btnPredefHomeWorkoutAdvanced.setOnClickListener(this);

        beginner=false;
        intermediate=false;
        advanced=false;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_level, container, false);

    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        if (v.getId() == R.id.btn_predef_homeworkout_beginner) {
            beginner=true;
            queryLevel1HomeWorkout();

        } else if (v.getId() == R.id.btn_predef_homeworkout_intermediate) {
            intermediate=true;
            //new fragment for choosing your focus of a predefined home workout for intermediate

        } else if (v.getId() == R.id.btn_predef_homeworkout_advanced) {
            advanced=true;
            //new fragment for choosing your level of a predefined home workout for advanced
        }

        //create new fragment displaying the result of either of the choices
        GetCustomizedHomeWorkoutSelectionFragment result = new GetCustomizedHomeWorkoutSelectionFragment();
        //check which bundle obj exists, beginner, intermed, advanced? use boolean? level1= true?
        if (beginnerBundle!=null) {
            result.setArguments(beginnerBundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, result)
                    .addToBackStack(null)
                    .commit();
        }
    }


    public void queryLevel1HomeWorkout() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference wkquery = db.collection("PredefinedWorkouts");
        //query to get all documents that both home workouts and suited for beginners
        Query query = wkquery.whereEqualTo("setting", "Home")
                .whereEqualTo("level", "Beginner");
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
                                        Workout workout = task.getResult().toObject(Workout.class);
                                        String wkCategory = task.getResult().getString("setting");
                                        String ex1 = workout.getLevel();
                                        int time = task.getResult().getLong("duration").intValue();

                                        //access the object exercise 1 as hashmap and get its key-values
                                        HashMap<String, Object> exI = workout.getExerciseI();
                                        String descriptionEx1 = (String) exI.get("description");
                                        String urlImgEx1 = (String) exI.get("image");

                                        //access the object exercise 2 as hashmap and get its key-values
                                        HashMap<String, Object> exII = workout.getExerciseII();
                                        String descriptionEx2 = (String) exII.get("description");
                                        String urlImgEx2 = (String) exII.get("image");

                                        //access the object exercise 3 as hashmap and get its key-values
                                        HashMap<String, Object> exIII = workout.getExerciseIII();
                                        String descriptionEx3 = (String) exIII.get("description");
                                        String urlImgEx3 = (String) exIII.get("image");

                                        //access the object exercise 4 as hashmap and get its key-values
                                        HashMap<String, Object> exIV = workout.getExerciseIV();
                                        String descriptionEx4 = (String) exIII.get("description");
                                        String urlImgEx4 = (String) exIII.get("image");

                                        //access the object exercise 5 as hashmap and get its key-values
                                        HashMap<String, Object> exV = workout.getExerciseV();
                                        String descriptionE53 = (String) exIII.get("description");
                                        String urlImgEx5 = (String) exIII.get("image");

                                        String workoutID = document.getId();
                                        //here no object is created, but simply the string from the database accessed
                                        //String descriptionEx2 = task.getResult().getString("Exercise II");

                                        // yes put this info to the bundle right here, working!
                                        beginnerBundle = new Bundle();
                                        //passing object as parcelable
                                        beginnerBundle.putParcelable("Workout", workout);
                                        beginnerBundle.putString("Exercise1", descriptionEx1);
                                        beginnerBundle.putString("Exercise2", descriptionEx2);
                                        beginnerBundle.putString("Image", urlImgEx1);
                                        beginnerBundle.putInt("Time", (int)time);
                                        beginnerBundle.putString("WorkoutID", workoutID);
                                        //beginnerBundle.putBoolean("ExerciseCompleted", exerciseCompleted);

//                                        GetCustomizedHomeWorkoutSelectionFragment result = new GetCustomizedHomeWorkoutSelectionFragment();
//                                        if (beginnerBundle!=null) {
//                                            result.setArguments(beginnerBundle);
//                                            getActivity().getSupportFragmentManager().beginTransaction()
//                                                    .replace(R.id.fragment_container, result)
//                                                    .addToBackStack(null)
//                                                    .commit();
//                                        }

                                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData().get("exerciseI"));
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                     else {
                        Log.d("Query Data", "Data is not valid");
                    }
                }
            }
        });
    }




}





