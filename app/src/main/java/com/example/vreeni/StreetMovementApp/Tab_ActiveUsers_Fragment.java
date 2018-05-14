package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tab_ActiveUsers_Fragment extends Fragment implements View.OnClickListener {
    private String TAG = "Active Users Tab ";
    private long mLastClickTime = 0;
    private ArrayList<GeoPoint> activeUsers;
    private RecyclerView mRecyclerView;
    private ItemListAdapter mAdapter;
    private final ArrayList<HashMap<String, Object>> activeUserList = new ArrayList<>();
    private String activity;
    private String setting;
    private ParkourPark pk;


    public static Tab_ActiveUsers_Fragment newInstance(String act, String set, ParkourPark spot) {
        final Bundle bundle = new Bundle();
        Tab_ActiveUsers_Fragment fragment = new Tab_ActiveUsers_Fragment();
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
            setting = "Outdoors";
            pk = getArguments().getParcelable("TrainingLocation");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_active_users, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView nrOfActUsers = (TextView) view.findViewById(R.id.tv_parkourpark_active_users);

        // Get a handle to the RecyclerView.
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        Log.d(TAG, "passed - parkourpark object - parkDescription " + pk.getName());
    }


    @Override
    public void onStart() {
        super.onStart();
        // Create an adapter and supply the data to be displayed.
        mAdapter = new ItemListAdapter(this.getActivity(), activeUserList);
        int height = 400; //get height
        ViewGroup.LayoutParams params_new = mRecyclerView.getLayoutParams();
        params_new.height = height;
        mRecyclerView.setLayoutParams(params_new);

        getActiveUsers();

//        backButton.setOnClickListener(click -> {
//            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
//        });
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


    public void getActiveUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference pkRef = db.collection("ParkourParks").document(pk.getName());
        pkRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //access all the information in the document
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //try to get user as an object (containing an image)
                    ParkourPark thisPark = task.getResult().toObject(ParkourPark.class);
                    int latestActiveUser;
                    if (thisPark.getListOfActiveUsers() != null) {
                        for (HashMap<String, Object> map : thisPark.getListOfActiveUsers())
                            activeUserList.add(map);
                        latestActiveUser = activeUserList.lastIndexOf(activeUserList.get(activeUserList.size() - 1));

                        Log.d(TAG, "position of last active user in list: " + latestActiveUser);
                        Log.d(TAG, "activeUserList: " + activeUserList);

                        // Connect the adapter with the RecyclerView.
                        mRecyclerView.setAdapter(mAdapter);
                        // Give the RecyclerView a default layout manager.
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                        mRecyclerView.smoothScrollToPosition(latestActiveUser);
                    } else Log.d(TAG, "No active users");
                    //add textview saying no one is training here at the moment
                }
            }
        });
    }


    public List<GeoPoint> checkForActiveUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference activeUsersQuery = db.collection("Users");
        //define based on which parameters the query should be done
        GeoPoint lastPos;
        String posLastUpdate;
        long radius;
        // assign active users to parkour par? and then just get list? or actively track location
//        if (lastPos);
        //query to get all documents that both home workouts and suited for beginners
        Query query = activeUsersQuery.whereEqualTo("position", "position + - radius")
                .whereEqualTo("positionLastUpdate", "active within the last hour?");
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
                        DocumentReference queriedWkRef = activeUsersQuery.document(ranWk.getName());
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


        return activeUsers;
    }

    @Override
    public void onClick(View v) {
        //make sure button is not clicked accidentally 2 times in a row
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
//        if (v.getId() == R.id.btn_submitRating) {

//        }
    }

}
