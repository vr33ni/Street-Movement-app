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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by vreeni on 20/12/2017.
 */

/**
 * Fragment displaying the randomly selected Home Workout from the database
 * gets arguments from bundle and displays a summary of the workout
 * passes on the bundle to the next fragment in the workout flow
 */
public class Fragment_Training_Workout_Selection extends Fragment {
    private static final String LOG_TAG = "Result";

    private Button btn_Continue;

    private String exerciseI;
    private String exerciseII;
    private String imgEx1;
//    private WebView vidEx1;

    private int time;
    private ImageView imageEx1;

    private String activity;
    private String setting;
    private String level;
    private ParkourPark pk;



    private TextView ex1;
    private TextView ex2;


    public static Fragment_Training_Workout_Selection newInstance(String act, String set, ParkourPark spot, String lvl) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Workout_Selection fragment = new Fragment_Training_Workout_Selection();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        bundle.putString("Level", lvl);
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
            level = getArguments().getString("Level");
            pk = getArguments().getParcelable("TrainingLocation");
            Log.d(LOG_TAG, "bundle information: " + getArguments());
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_get_customized_homeworkout_selection, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_Continue = (Button) view.findViewById(R.id.btn_predef_homeworkout_SelectionContinue);

            //maybe here create exercise objects and set the fields?? (exerciseI = new Exercise(); exerciseI.setDescription, setIsCompleted....)
            //then add them to a list of exercise objects?

        ex1 = (TextView) view.findViewById(R.id.ex1);
        ex2 = (TextView) view.findViewById(R.id.ex2);
//
//        TextView timeView = (TextView) view.findViewById(R.id.time);
//        timeView.setText(time + " minutes");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "training info" + activity + setting + level);

        FirebaseQuery query = new FirebaseQuery(activity, setting, level);
        query.query(new FirebaseCallback() {
            @Override
            public void onQuerySuccess(Workout workout) {
                String descriptionEx1 = workout.getExerciseI().get("description").toString();
                String descriptionEx2 = workout.getExerciseII().get("description").toString();
                Log.d(LOG_TAG, "query details: " + descriptionEx1 + ", " + descriptionEx2);

                ex1.setText(descriptionEx1);
                ex2.setText(descriptionEx2);

                btn_Continue.setOnClickListener(click -> {
                    Fragment_Training_Warmup warmup = Fragment_Training_Warmup.newInstance(workout, pk);
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, warmup, "warmup")
                            .addToBackStack("warmup")
                            .commit();
                });
                Log.d(LOG_TAG, "warmup");

            }

            @Override
            public void onFailure() {

            }
        });
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
}






