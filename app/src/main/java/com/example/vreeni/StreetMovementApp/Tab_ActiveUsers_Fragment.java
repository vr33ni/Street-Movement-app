package com.example.vreeni.StreetMovementApp;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class Tab_ActiveUsers_Fragment extends Fragment implements View.OnClickListener {
    private String TAG = "Active Users Tab ";
    private long mLastClickTime = 0;
    private ArrayList<GeoPoint> activeUsers;
    private RecyclerView mRecyclerView;
    private ItemList_Ratings_Adapter mAdapter;
    private final ArrayList<HashMap<String, Object>> activeUserList = new ArrayList<>();
    private String activity;
    private String setting;
    private ParkourPark pk;
    private Location mLastKnownLocation;


    public static Tab_ActiveUsers_Fragment newInstance(String act, String set, ParkourPark spot, Location mLastKnownLocation) {
        final Bundle bundle = new Bundle();
        Tab_ActiveUsers_Fragment fragment = new Tab_ActiveUsers_Fragment();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        bundle.putParcelable("TrainingLocation", spot);
        bundle.putParcelable("UserLocation", mLastKnownLocation);
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
            mLastKnownLocation = getArguments().getParcelable("UserLocation");
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
        mAdapter = new ItemList_Ratings_Adapter(this.getActivity(), activeUserList);
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
                        if (activeUserList.size() != 0) {
                            latestActiveUser = activeUserList.lastIndexOf(activeUserList.get(activeUserList.size() - 1));
                        } else latestActiveUser = 0;

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
