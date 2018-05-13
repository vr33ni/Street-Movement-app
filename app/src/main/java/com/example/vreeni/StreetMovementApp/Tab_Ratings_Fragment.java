package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tab_Ratings_Fragment extends Fragment implements View.OnClickListener {
    private String TAG = "Rating Tab ";

    private RecyclerView mRecyclerView;
    private ItemListAdapter mAdapter;
    private EditText et_comment;
    private RatingBar ratingBar;
    private Button btnSubmitRating;
    private long mLastClickTime = 0;

    private String activity;
    private String setting;
    private ParkourPark pk;
    private HashMap<String, Object> rt;
    private final ArrayList<HashMap<String, Object>> ratinglist = new ArrayList<>();
    private ScrollView scrollview;


    public static Tab_Ratings_Fragment newInstance(String act, String set, ParkourPark spot) {
        final Bundle bundle = new Bundle();
        Tab_Ratings_Fragment fragment = new Tab_Ratings_Fragment();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_ratings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSubmitRating = (Button) view.findViewById(R.id.btn_submitRating);
        btnSubmitRating.setOnClickListener(this);
        scrollview = ((ScrollView) view.findViewById(R.id.scrollView));

        Log.d(TAG, "bundle - parkourpark object - parkDescription " + pk.getName());
//            tv_name.setText(pk.getName());
//            tv_description.setText(pk.getDescription());

        if (pk.getPhoto_0() != null) {
            HashMap<String, Object> photo = pk.getPhoto_0();
            String photoURL = (String) photo.get("url");
//                Uri photoURI = Uri.parse(photoURL);
//                iv.setImageURI(photoURI);
//                loadImgWithGlide(photoURL, iv);
        } else {
            //default photo, but prevent that by having pictures of all spots in the map view fragment already
//                iv.setImageResource(R.drawable.img_railheaven);
        }

        et_comment = (EditText) view.findViewById(R.id.et_new_userratings_comment);
        ratingBar = (RatingBar) view.findViewById(R.id.new_userrating_stars);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Log.d(TAG, String.valueOf(rating));
            }
        });


        // Get a handle to the RecyclerView.
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new ItemListAdapter(this.getActivity(), ratinglist);
//        // Connect the adapter with the RecyclerView.
//        mRecyclerView.setAdapter(mAdapter);
//        // Give the RecyclerView a default layout manager.
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        int height = 400; //get height
        ViewGroup.LayoutParams params_new = mRecyclerView.getLayoutParams();
        params_new.height = height;
        mRecyclerView.setLayoutParams(params_new);

        getRatingsFromDatabase();

        //create method calling the current ratings from database
//        for (int i = 0; i < 20; i++) {
//            ratinglist.addLast("Rating " + mCount++);
//            Log.d("RatingList", ratinglist.getLast());
//        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        TextView textView = new TextView(getContext());
//
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


    public void loadImgWithGlide(String url, ImageView iv) {
        Glide
                .with(this)
                .load(url)
                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(iv);
    }


    @Override
    public void onClick(View v) {
        //make sure button is not clicked accidentally 2 times in a row
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        if (v.getId() == R.id.btn_submitRating) {
            String comment = et_comment.getText().toString().trim();
            long rating = (long) ratingBar.getRating();
            if (!validateUserInput()) {
            } else {
                rt = new HashMap<>();
                rt.put("comment", comment);
                rt.put("rating", rating);
                connectUserToRating(); //from here: start the writing process of the rating to the database
                //refresh fragment to display comment
            }
        }
    }

    /**
     * if email/password fields are empty, show an error message
     *
     * @return
     */
    private boolean validateUserInput() {
        boolean valid = true;
        String comment = et_comment.getText().toString();
        int rating = (int) ratingBar.getRating();

        if ((TextUtils.isEmpty(comment)) || (rating == 0)) {
            //Snackbar showing user has to submit a rating first
            Snackbar.make(getActivity().getWindow().getDecorView().getRootView().findViewById(R.id.fragment_container),
                    "Please specify your rating", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
        }
        Log.d(TAG, "validateUserInput " + valid);
        return valid;
    }

    public void getRatingsFromDatabase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference pkRef = db.collection("ParkourParks").document(pk.getName());
        pkRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //access all the information in the document
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    //try to get user as an object (containing an image)
                    ParkourPark thisPark = task.getResult().toObject(ParkourPark.class);
                    int positionOfLatestRating;
                    if (thisPark.getListOfRatings() != null) {
                        for (HashMap<String, Object> map : thisPark.getListOfRatings())
                            ratinglist.add(map);
                        positionOfLatestRating = ratinglist.lastIndexOf(ratinglist.get(ratinglist.size() - 1));

                        Log.d(TAG, "position of last rating map in rating list: " + positionOfLatestRating);
                        Log.d(TAG, "ratinglist: " + ratinglist);

                        // Connect the adapter with the RecyclerView.
                        mRecyclerView.setAdapter(mAdapter);
                        // Give the RecyclerView a default layout manager.
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                        mRecyclerView.smoothScrollToPosition(positionOfLatestRating);
                    } else Log.d(TAG, "No ratings yet");
                }
            }
        });
    }


    public void connectUserToRating() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //access all the information in the document
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    //try to get user as an object (containing an image)
                    User user = task.getResult().toObject(User.class);
                    String userDocId = document.getId();
                    DocumentReference referenceToUser = db.collection("Users").document(userDocId);
                    Log.d(TAG, "getUsernameWhoPerformedRating - nickname: " + referenceToUser);
                    String referenceToUser_nickname;
                    if (user.getName() != null) {
                        referenceToUser_nickname = user.getName();
                        Log.d(TAG, "getUsernameWhoPerformedRating - nickname: " + referenceToUser_nickname);

                    } else {
                        referenceToUser_nickname = user.getNickname();
                        Log.d(TAG, "getUsernameWhoPerformedRating - displayname: " + referenceToUser_nickname);
                    }
                    rt.put("userRef", referenceToUser);
                    rt.put("username", referenceToUser_nickname);
                    // SET RATING FROM HERE so it only gets set once this task is completed (querying from database takes time and might result in a null value if another method relying on its updates starts before its completion)
                    setRating();

                    Log.d(TAG, "connected user to rating, writing rating to database now... ");
                }
            }
        });
    }


    public void setRating() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference parkRef = db.collection("ParkourParks").document(pk.getName());
        Map<String, Object> dataUpdate = new HashMap<String, Object>();
        HashMap<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("referenceToUserDocument", rt.get("userRef"));
        ratingMap.put("username", rt.get("username"));
        ratingMap.put("comment", rt.get("comment"));
        ratingMap.put("rating", rt.get("rating"));
        ratinglist.add(ratingMap);
//            ratinglist.add(ratingMap);

        pk.setListOfRatings(ratinglist);
        dataUpdate.put("listOfRatings", pk.getListOfRatings());
        parkRef
                .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "rating has been saved");
                Snackbar.make(getActivity().getWindow().getDecorView().getRootView().findViewById(R.id.fragment_container),
                        "Your rating has been saved successfully", Snackbar.LENGTH_SHORT).show();
                getRatingsFromDatabase();

////                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
//                scrollview.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollview.scrollTo(0, scrollview.getBottom());
//                        Log.d(TAG, "scrolling to end");
//                    }
//                });//                    mRecyclerView.smoothScrollToPosition(this);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "rating could not be saved");
            }
        });
    }
}
