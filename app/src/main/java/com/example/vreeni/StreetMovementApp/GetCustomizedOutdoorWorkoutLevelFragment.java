package com.example.vreeni.StreetMovementApp;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
 * Created by vreeni on 26/02/2018.
 */

/**
 * Fragment displaying options for different levels of Outdoor Workouts (Beginner, Intermediate, Advanced)
 * calls workout from the database
 * puts workout as parcelable object to a bundle and passes it on to the next fragment
 */
public class GetCustomizedOutdoorWorkoutLevelFragment extends Fragment implements View.OnClickListener {

    private String TAG = "Outdoor_Choose Level: ";

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    boolean beginner;
    boolean intermediate;
    boolean advanced;

    private Bundle outdoorBundle; //to pass arguments to the next fragment


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnPredefHomeWorkoutBeginner = (Button) view.findViewById(R.id.btn_predef_outdoorworkout_beginner);
        btnPredefHomeWorkoutBeginner.setOnClickListener(this);

        Button btnPredefHomeWorkoutIntermediate = (Button) view.findViewById(R.id.btn_predef_outdoorworkout_intermediate);
        btnPredefHomeWorkoutIntermediate.setOnClickListener(this);

        Button btnPredefHomeWorkoutAdvanced = (Button) view.findViewById(R.id.btn_predef_outdoorworkout_advanced);
        btnPredefHomeWorkoutAdvanced.setOnClickListener(this);

        ImageView iv = (ImageView) view.findViewById(R.id.outdoorWkChooseLvl);

        outdoorBundle = getArguments();
        if (outdoorBundle != null) {
            ParkourPark pk = outdoorBundle.getParcelable("OutdoorWorkout");
            Log.d(TAG, "outdoor bundle " + pk.getDescription());

            if (pk.getPhoto_0() != null) {
                HashMap<String, Object> photo = pk.getPhoto_0();
                String photoURL = (String) photo.get("url");
                loadImgWithGlide(photoURL, iv);
            } else {
                //default photo, but prevent that by having pictures of all spots in the map view fragment already
                iv.setImageResource(R.drawable.img_railheaven);
            }
        }


        beginner = false;
        intermediate = false;
        advanced = false;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_get_customized_outdoorworkout_level, container, false);

    }

    @Override
    public void onClick(View v) {

        Fragment fragment = null;
        if (v.getId() == R.id.btn_predef_outdoorworkout_beginner) {
            beginner = true;
//            queryLevel1HomeWorkout();
            fragment = new WebViewFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
        } else if (v.getId() == R.id.btn_predef_outdoorworkout_intermediate) {
            intermediate = true;
            //new fragment for choosing your focus of a predefined home workout for intermediate

        } else if (v.getId() == R.id.btn_predef_outdoorworkout_advanced) {
            advanced = true;
            //new fragment for choosing your level of a predefined home workout for advanced
        }

        //create new fragment displaying the result of either of the choices
//        GetCustomizedHomeWorkoutSelectionFragment result = new GetCustomizedHomeWorkoutSelectionFragment();
//        //check which bundle obj exists, beginner, intermed, advanced? use boolean? level1= true?
//        if (outdoorBundle != null) {
//            result.setArguments(outdoorBundle);
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, result)
//                    .addToBackStack(null)
//                    .commit();
//        }
    }


    public void queryLevel1HomeWorkout() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference wkquery = db.collection("PredefinedWorkouts");
        //query to get all documents that both home workouts and suited for beginners
        Query query = wkquery.whereEqualTo("setting", "Outdoor")
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
//                                        int time = task.getResult().getLong("duration").intValue();

                                        //access the object exercise 1 as hashmap and get its key-values
                                        HashMap<String, Object> exI = workout.getExerciseI();
                                        String descriptionEx1 = (String) exI.get("description");
//                                        String urlImgEx1 = (String) exI.get("image");

//                                        //access the object exercise 2 as hashmap and get its key-values
//                                        HashMap<String, Object> exII = workout.getExerciseII();
//                                        String descriptionEx2 = (String) exII.get("description");
//                                        String urlImgEx2 = (String) exII.get("image");

//                                        //access the object exercise 3 as hashmap and get its key-values
//                                        HashMap<String, Object> exIII = workout.getExerciseIII();
//                                        String descriptionEx3 = (String) exIII.get("description");
//                                        String urlImgEx3 = (String) exIII.get("image");
//
//                                        //access the object exercise 4 as hashmap and get its key-values
//                                        HashMap<String, Object> exIV = workout.getExerciseIV();
//                                        String descriptionEx4 = (String) exIII.get("description");
//                                        String urlImgEx4 = (String) exIII.get("image");
//
//                                        //access the object exercise 5 as hashmap and get its key-values
//                                        HashMap<String, Object> exV = workout.getExerciseV();
//                                        String descriptionE53 = (String) exIII.get("description");
//                                        String urlImgEx5 = (String) exIII.get("image");

                                        String workoutID = document.getId();
                                        //here no object is created, but simply the string from the database accessed
                                        //String descriptionEx2 = task.getResult().getString("Exercise II");

                                        // yes put this info to the bundle right here, working!
                                        outdoorBundle = new Bundle();
                                        //passing object as parcelable
                                        outdoorBundle.putParcelable("Workout", workout);
                                        outdoorBundle.putString("Exercise1", descriptionEx1);
//                                        beginnerBundle.putString("Exercise2", descriptionEx2);
//                                        beginnerBundle.putString("Image", urlImgEx1);
//                                        beginnerBundle.putInt("Time", (int) time);
                                        outdoorBundle.putString("WorkoutID", workoutID);
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
                    } else {
                        Log.d("Query Data", "Data is not valid");
                    }
                }
            }
        });
    }

    public void loadImgWithGlide(String url, ImageView iv) {
        Glide
                .with(this)
                .load(url)
                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(iv);
    }

}





