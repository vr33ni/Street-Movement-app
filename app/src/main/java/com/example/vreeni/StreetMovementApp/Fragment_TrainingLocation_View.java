package com.example.vreeni.StreetMovementApp;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by vreee on 13/03/2018.
 */

/**
 * displays more information on a specific training location after clicking on a marker's infowindow on the map
 */
public class Fragment_TrainingLocation_View extends Fragment implements View.OnClickListener {
    private String TAG = "PkParkView_Fragment: ";

    private RecyclerView mRecyclerView;
    private ItemList_Ratings_Adapter mAdapter;
    private ImageView iv;
    private EditText et_comment;
    private RatingBar ratingBar;
    private Button btnSubmitRating;
    private long mLastClickTime = 0;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private String activity;
    private String setting;
    private ParkourPark pk;
    private Location mLastKnownLocation;
    private HashMap<String, Object> rt;

    private final ArrayList<HashMap<String, Object>> ratinglist = new ArrayList<>();

    private Tab_FragmentPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static Fragment_TrainingLocation_View newInstance(String act, String set, ParkourPark spot, Location mLastKnownLocation) {
        final Bundle bundle = new Bundle();
        Fragment_TrainingLocation_View fragment = new Fragment_TrainingLocation_View();
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
            setting = getArguments().getString("Setting");
            pk = getArguments().getParcelable("TrainingLocation");
            mLastKnownLocation = getArguments().getParcelable("UserLocation");
            Log.d(TAG, "bundle info: " + getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parkourpark_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv = (ImageView) view.findViewById(R.id.iv_parkview);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        }

    @Override
    public void onStart() {
        super.onStart();
        //set image on top of the fragment
        adapter = new Tab_FragmentPagerAdapter(this.getChildFragmentManager(), this.getActivity());

        if (pk.getPhoto_0() != null) {
            HashMap<String, Object> photo = pk.getPhoto_0();
            String photoURL = (String) photo.get("url");
            loadImgWithGlide(photoURL, iv);
        } else {
            //default photo, but prevent that by having pictures of all spots in the map view fragment already
            iv.setImageResource(R.drawable.noimgavailable);
        }
        //pass info to the respective tabs containing further fragments that are being displayed within this fragment
        Tab_Ratings_Fragment ratingsTab = Tab_Ratings_Fragment.newInstance(activity, setting, pk, mLastKnownLocation);
        Tab_Overview_Fragment overviewTab = Tab_Overview_Fragment.newInstance(activity, setting, pk, mLastKnownLocation);
        Tab_ActiveUsers_Fragment activeUsersTab = Tab_ActiveUsers_Fragment.newInstance(activity, setting, pk, mLastKnownLocation);
        adapter.addFragment(overviewTab, "Overview");
        adapter.addFragment(ratingsTab, "Ratings");
        adapter.addFragment(activeUsersTab, "Active users");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

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
