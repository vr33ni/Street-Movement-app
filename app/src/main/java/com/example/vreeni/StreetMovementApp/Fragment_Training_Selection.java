package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Fragment displaying a randomly selected training activity such as a workout from the database
 * gets arguments from bundle and - after the query succeeds - displays a summary of the workout
 * passes on the bundle to the next fragment in the workout flow
 */
public class Fragment_Training_Selection extends Fragment {
    private static final String LOG_TAG = "Result";

    private RecyclerView recycler;
    private RecyclerView.LayoutManager manager;
    private ItemList_Exercises_Adapter adapter;
    private ArrayList<Exercise> list = new ArrayList<>();
    private ArrayList<Object> listofexercisereferences;

    private Context context;

    private Button btn_Continue;

    private String activity;
    private String setting;
    private String level;
    private ParkourPark pk;

    private TextView tv_duration;

    public static Fragment_Training_Selection newInstance(String act, String set, ParkourPark spot, String lvl) {
        final Bundle bundle = new Bundle();
        Fragment_Training_Selection fragment = new Fragment_Training_Selection();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        bundle.putString("Level", lvl);
        bundle.putParcelable("TrainingLocation", spot);
        fragment.setArguments(bundle);
        return fragment;
//        context = this.getActivity().getContext();
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
        return inflater.inflate(R.layout.fragment_training_selection, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.exercises_selection_recyclerView);
        recycler.setHasFixedSize(true);
        manager = new GridLayoutManager(this.getContext(), 1, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);

        btn_Continue = (Button) view.findViewById(R.id.btn_predef_homeworkout_SelectionContinue);

        tv_duration = (TextView) view.findViewById(R.id.time);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "training info" + activity + setting + level);

        context = this.getContext();

        FirebaseQuery_Workout query = new FirebaseQuery_Workout(activity, setting, level);
        query.query(new FirebaseCallback_Workout() {
            @Override
            public void onQuerySuccess(Workout workout) {
                if (workout.getListOfExercises() != null) {

                    //getting and setting the exercises that are part of the workout in a recycler view
                    listofexercisereferences = workout.getListOfExercises();
                    list = getExercises(context);

                    //setting the duration of the workout
                    tv_duration.setText(workout.getDuration() + " minutes");

                } else Log.d(LOG_TAG, "workout has no list of exercises");

                btn_Continue.setOnClickListener(click -> {
                    Fragment_Training_Warmup warmup = Fragment_Training_Warmup.newInstance(workout, pk);
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, warmup, "selection")
                            .addToBackStack("selection")
                            .commit();
                });
                Log.d(LOG_TAG, "workout selected");
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

    public ArrayList<Exercise> getExercises(Context c) {
        Log.d(LOG_TAG, "getting the single exercises of a workout");
        FirebaseQuery_Exercises query = new FirebaseQuery_Exercises(listofexercisereferences);
        query.query(new FirebaseCallback_Exercises() {
            @Override
            public void onQuerySuccess(ArrayList<Exercise> exercises) {
                list = exercises;

                RecyclerViewClickListener listener = (view, position) -> {
                };

                adapter = new ItemList_Exercises_Adapter(list, c, listener);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onFailure() {
            }
        });
        return list;
    }
}


