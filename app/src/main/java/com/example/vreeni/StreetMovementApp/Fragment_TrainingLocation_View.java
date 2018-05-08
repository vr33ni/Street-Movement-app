package com.example.vreeni.StreetMovementApp;

import android.media.JetPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Created by vreee on 13/03/2018.
 */

/**
 * displays more information on a specific training location after clicking on a marker's infowindow on the map
 */
public class Fragment_TrainingLocation_View extends Fragment implements View.OnClickListener {
    private String TAG = "PkParkView_Fragment: ";

    private RecyclerView mRecyclerView;
    private ItemListAdapter mAdapter;
    private EditText et_comment;
    private RatingBar ratingBar;
    private Button btnSubmitRating;
    private long mLastClickTime = 0;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private Bundle outdoorBundle; //to pass arguments to the next fragment
    private ParkourPark pk;
    private HashMap<String, Object> rt;

    private final ArrayList<HashMap<String, Object>> ratinglist = new ArrayList<>();

    private Tab_FragmentPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView iv = (ImageView) view.findViewById(R.id.iv_parkview);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
//        adapter = new Tab_FragmentPagerAdapter(this.getActivity().getSupportFragmentManager(), this.getActivity());
        adapter = new Tab_FragmentPagerAdapter(this.getChildFragmentManager(), this.getActivity());
        outdoorBundle = getArguments();
        if (outdoorBundle != null) {
            //set image on top of the fragment
            pk = outdoorBundle.getParcelable("TrainingLocation");
            if (pk.getPhoto_0() != null) {
                HashMap<String, Object> photo = pk.getPhoto_0();
                String photoURL = (String) photo.get("url");
//                Uri photoURI = Uri.parse(photoURL);
//                iv.setImageURI(photoURI);
                loadImgWithGlide(photoURL, iv);
            } else {
                //default photo, but prevent that by having pictures of all spots in the map view fragment already
                iv.setImageResource(R.drawable.img_railheaven);
            }

            //pass info to the respective tabs containing further fragments that are being displayed within this fragment
            Tab_Ratings_Fragment ratingsTab = new Tab_Ratings_Fragment();
            ratingsTab.setArguments(outdoorBundle);
            Tab_Overview_Fragment overviewTab = new Tab_Overview_Fragment();
            overviewTab.setArguments(outdoorBundle);
            Tab_ActiveUsers_Fragment activeUsersTab = new Tab_ActiveUsers_Fragment();
            activeUsersTab.setArguments(outdoorBundle);

            adapter.addFragment(overviewTab, "Overview");
            adapter.addFragment(ratingsTab, "Ratings");
            adapter.addFragment(new TrainNowFragment(), "Active users");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_parkourpark_view, container, false);

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


    public void loadImgWithGlide(String url, ImageView iv) {
        Glide
                .with(this)
                .load(url)
                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(iv);
    }


}
