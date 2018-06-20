package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



/**
 * Created by vreeni on 20/12/2017.
 */

/**
 * Fragment displaying options for different levels of Home Workouts (Beginner, Intermediate, Advanced)
 * calls workout from the database
 * puts workout as parcelable object to a bundle and passes it on to the next fragment
 */
public class Fragment_Training_Level extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "Level";

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    Button btnPredefHomeWorkoutBeginner;
    Button btnPredefHomeWorkoutIntermediate;
    Button btnPredefHomeWorkoutAdvanced;

    private String activity;
    private String setting;
    private ParkourPark pk;


    public static Fragment_Training_Level newInstance(String act, String set, ParkourPark spot) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Level fragment = new Fragment_Training_Level();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        bundle.putParcelable("TrainingLocation", spot);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
            setting = getArguments().getString("Setting");
            pk = getArguments().getParcelable("TrainingLocation");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_level, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPredefHomeWorkoutBeginner = (Button) view.findViewById(R.id.btn_predef_homeworkout_beginner);
        btnPredefHomeWorkoutIntermediate = (Button) view.findViewById(R.id.btn_predef_homeworkout_intermediate);
        btnPredefHomeWorkoutAdvanced = (Button) view.findViewById(R.id.btn_predef_homeworkout_advanced);
    }

    @Override
    public void onStart() {
        super.onStart();
        btnPredefHomeWorkoutBeginner.setOnClickListener(this);
        btnPredefHomeWorkoutBeginner.setEnabled(true);

        btnPredefHomeWorkoutIntermediate.setOnClickListener(this);
        btnPredefHomeWorkoutIntermediate.setEnabled(true);

        btnPredefHomeWorkoutAdvanced.setOnClickListener(this);
        btnPredefHomeWorkoutAdvanced.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * handling of the button clicks
     * => button click disables the other buttons,
     * => starts a query to the database querying the respective Home Workout,
     * => and redirects to the next fragment in the workout flow
     *
     * @param v representing the buttons "Beginner", "Intermediate" or "Advanced"
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_predef_homeworkout_beginner) {
            Log.d(LOG_TAG, "training info: " + activity + setting);
            btnPredefHomeWorkoutIntermediate.setEnabled(false);
            btnPredefHomeWorkoutAdvanced.setEnabled(false);
            String level = "Beginner";
            Fragment_Training_Selection result = Fragment_Training_Selection.newInstance(activity, setting, pk, level);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, result, "Result")
                    .addToBackStack("result")
                    .commit();

        } else if (v.getId() == R.id.btn_predef_homeworkout_intermediate) {
            Log.d(LOG_TAG, "training info" + activity + setting);
            btnPredefHomeWorkoutBeginner.setEnabled(false);
            btnPredefHomeWorkoutAdvanced.setEnabled(false);
            String level = "Intermediate";
            Fragment_Training_Selection result = Fragment_Training_Selection.newInstance(activity, setting, pk, level);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, result, "Result")
                    .addToBackStack("result")
                    .commit();


        } else if (v.getId() == R.id.btn_predef_homeworkout_advanced) {
            Log.d(LOG_TAG, "training info: " + activity + setting);
            btnPredefHomeWorkoutIntermediate.setEnabled(false);
            btnPredefHomeWorkoutBeginner.setEnabled(false);
            String level = "Advanced";
            Fragment_Training_Selection result = Fragment_Training_Selection.newInstance(activity, setting, pk, level);
            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, result, "Result")
                    .addToBackStack("result")
                    .commit();

        }

    }

}





