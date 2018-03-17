package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * Created by vreeni on 20/12/2017.
 */

/**
 * Fragment displaying options for different levels of Home Workouts (Beginner, Intermediate, Advanced)
 * calls workout from the database
 * puts workout as parcelable object to a bundle and passes it on to the next fragment
 */
public class GetCustomizedHomeWorkoutLevelFragment extends Fragment implements View.OnClickListener {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    boolean beginner;
    boolean intermediate;
    boolean advanced;

    Button btnPredefHomeWorkoutBeginner;
    Button btnPredefHomeWorkoutIntermediate;
    Button btnPredefHomeWorkoutAdvanced;

    private  Bundle bundle; //to pass arguments to the next fragment

    private String TAG = "Choose Level: ";


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPredefHomeWorkoutBeginner = (Button) view.findViewById(R.id.btn_predef_homeworkout_beginner);
        btnPredefHomeWorkoutBeginner.setOnClickListener(this);
        btnPredefHomeWorkoutBeginner.setEnabled(true);

        btnPredefHomeWorkoutIntermediate = (Button) view.findViewById(R.id.btn_predef_homeworkout_intermediate);
        btnPredefHomeWorkoutIntermediate.setOnClickListener(this);
        btnPredefHomeWorkoutIntermediate.setEnabled(true);

        btnPredefHomeWorkoutAdvanced = (Button) view.findViewById(R.id.btn_predef_homeworkout_advanced);
        btnPredefHomeWorkoutAdvanced.setOnClickListener(this);
        btnPredefHomeWorkoutAdvanced.setEnabled(true);


        beginner=false;
        intermediate=false;
        advanced=false;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_level, container, false);

    }


    /**
     * handling of the button clicks
     * => button click disables the other buttons,
     * => starts a query to the database querying the respective Home Workout,
     * => and redirects to the next fragment in the workout flow
     * @param v representing the buttons "Beginner", "Intermediate" or "Advanced"
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_predef_homeworkout_beginner) {
            beginner=true;
            btnPredefHomeWorkoutIntermediate.setEnabled(false);
            btnPredefHomeWorkoutAdvanced.setEnabled(false);
            queryHomeWorkout();

        } else if (v.getId() == R.id.btn_predef_homeworkout_intermediate) {
            intermediate=true;
            btnPredefHomeWorkoutBeginner.setEnabled(false);
            btnPredefHomeWorkoutAdvanced.setEnabled(false);
            queryHomeWorkout();

        } else if (v.getId() == R.id.btn_predef_homeworkout_advanced) {
            advanced=true;
            btnPredefHomeWorkoutIntermediate.setEnabled(false);
            btnPredefHomeWorkoutBeginner.setEnabled(false);
            queryHomeWorkout();
        }

        //create new fragment displaying the result of either of the choices
//        GetCustomizedHomeWorkoutSelectionFragment result = new GetCustomizedHomeWorkoutSelectionFragment();
        //check which bundle obj exists, beginner, intermed, advanced? use boolean? level1= true?
//        if (beginnerBundle!=null) {
//            fragment.setArguments(beginnerBundle);
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .addToBackStack(null)
//                    .commit();
//        }
    }


    public void queryHomeWorkout() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference wkquery = db.collection("PredefinedWorkouts");
        //define which level is to be queried
        String levelSelected;
        if (beginner) levelSelected ="Beginner";
        else if (intermediate) levelSelected ="Intermediate";
        else levelSelected="Advanced";
        //query to get all documents that both home workouts and suited for beginners
        Query query = wkquery.whereEqualTo("setting", "Home")
                .whereEqualTo("level", levelSelected);
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
                                        String urlVidEx1 = (String) exI.get("video");


                                        //access the object exercise 2 as hashmap and get its key-values
                                        HashMap<String, Object> exII = workout.getExerciseII();
                                        String descriptionEx2 = (String) exII.get("description");
                                        String urlImgEx2 = (String) exII.get("image");
                                        String urlVidEx2 = (String) exII.get("video");


                                        //access the object exercise 3 as hashmap and get its key-values
                                        HashMap<String, Object> exIII = workout.getExerciseIII();
                                        String descriptionEx3 = (String) exIII.get("description");
                                        String urlImgEx3 = (String) exIII.get("image");
                                        String urlVidEx3 = (String) exIII.get("video");

                                        //access the object exercise 4 as hashmap and get its key-values
                                        HashMap<String, Object> exIV = workout.getExerciseIV();
                                        String descriptionEx4 = (String) exIV.get("description");
                                        String urlImgEx4 = (String) exIV.get("image");
                                        String urlVidEx4 = (String) exIV.get("video");


                                        //access the object exercise 5 as hashmap and get its key-values
                                        HashMap<String, Object> exV = workout.getExerciseV();
                                        String descriptionE53 = (String) exV.get("description");
                                        String urlImgEx5 = (String) exV.get("image");
                                        String urlVidEx5 = (String) exV.get("video");


                                        String workoutID = document.getId();
                                        //here no object is created, but simply the string from the database accessed
                                        //String descriptionEx2 = task.getResult().getString("Exercise II");

                                        // yes put this info to the bundle right here, working!
                                        bundle = new Bundle();
                                        //passing object as parcelable
                                        bundle.putParcelable("Workout", workout);
                                        bundle.putString("Exercise1", descriptionEx1);
                                        bundle.putString("Exercise2", descriptionEx2);
                                        bundle.putString("Image", urlImgEx1);
                                        bundle.putInt("Time", (int)time);
                                        bundle.putString("WorkoutID", workoutID);

                                            //beginnerBundle.putBoolean("ExerciseCompleted", exerciseCompleted);

                                        GetCustomizedHomeWorkoutSelectionFragment result = new GetCustomizedHomeWorkoutSelectionFragment();
                                        if (bundle!=null) {
                                            result.setArguments(bundle);
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.fragment_container, result)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }

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





