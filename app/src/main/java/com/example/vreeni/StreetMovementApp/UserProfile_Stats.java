package com.example.vreeni.StreetMovementApp;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import static com.example.vreeni.StreetMovementApp.User.LISTOFHOMEWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFMOVEMENTSPECIFICCHALLENGES;
import static com.example.vreeni.StreetMovementApp.User.LISTOFOUTDOORWORKOUTS;
import static com.example.vreeni.StreetMovementApp.User.LISTOFSTREETMOVEMENTCHALLENGES;


/**
 * User profile section displaying the user's stats and personal bests
 */
public class UserProfile_Stats extends Fragment implements OnChartGestureListener, View.OnTouchListener, View.OnClickListener {

    private static final String TAG = UserProfile_Stats.class.getSimpleName();

    private BarChart mChart;

    int nrOfHomeWorkouts;
    int nrOfOutdoorWorkouts;
    int nrOfMovSpecChallenges;
    int nrOfStreetMovementChallenges;


    public static UserProfile_Stats newInstance() {
        final Bundle bundle = new Bundle();
        UserProfile_Stats fragment = new UserProfile_Stats();
//        bundle.putString("Activity", act);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserActivityStats(view);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile_stats, container, false);
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
        mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }


    private String[] mLabels = new String[]{"Activities completed"};
    private String[] mXVals = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"};

    private String getLabel(int i) {
        return mLabels[i];
    }

    public void getUserActivityStats(View view) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        final DocumentReference userDocRef = db.collection("Users").document(currUser.getEmail());
        userDocRef.addSnapshotListener(this.getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    ArrayList listOfHomeWorkouts;
                    ArrayList listofOutdoorWorkouts;
                    ArrayList listOfMovSpecChallenges;
                    ArrayList listOfSMChallenges;

                    if (documentSnapshot.get(LISTOFHOMEWORKOUTS) != null) {
                        listOfHomeWorkouts = (ArrayList) documentSnapshot.get(LISTOFHOMEWORKOUTS);
                    } else {
                        listOfHomeWorkouts = new ArrayList();
                    }

                    if (documentSnapshot.get(LISTOFOUTDOORWORKOUTS) != null) {
                        listofOutdoorWorkouts = (ArrayList) documentSnapshot.get(LISTOFOUTDOORWORKOUTS);
                    } else {
                        listofOutdoorWorkouts = new ArrayList();
                    }
                    if (documentSnapshot.get(LISTOFMOVEMENTSPECIFICCHALLENGES) != null) {
                        listOfMovSpecChallenges = (ArrayList) documentSnapshot.get(LISTOFMOVEMENTSPECIFICCHALLENGES);
                    } else {
                        listOfMovSpecChallenges = new ArrayList();
                    }
                    if (documentSnapshot.get(LISTOFSTREETMOVEMENTCHALLENGES) != null) {
                        listOfSMChallenges = (ArrayList) documentSnapshot.get(LISTOFSTREETMOVEMENTCHALLENGES);
                    } else {
                        listOfSMChallenges = new ArrayList();
                    }
                    nrOfHomeWorkouts = listOfHomeWorkouts.size();
                    nrOfOutdoorWorkouts = listofOutdoorWorkouts.size();
                    nrOfMovSpecChallenges = listOfMovSpecChallenges.size();
                    nrOfStreetMovementChallenges = listOfSMChallenges.size();

                    Log.d(TAG, "nr of home workouts " + nrOfHomeWorkouts +
                            " + nr of outdoor workouts: " + nrOfOutdoorWorkouts +
                            " nr of mov spec challenges: " + nrOfMovSpecChallenges +
                            " nr of street movement challenges: " + nrOfStreetMovementChallenges);

                    if ((nrOfHomeWorkouts > 0) || (nrOfOutdoorWorkouts > 0) || (nrOfStreetMovementChallenges > 0) || (nrOfMovSpecChallenges > 0)) {
                        makeChart(view);
                    } else {
                        TextView tv = (TextView) view.findViewById(R.id.tv_noActivities);
                        tv.setVisibility(View.VISIBLE);
                    }


                } else if (e != null) {
                    Log.w("An exception occured", e);
                }
            }
        });

    }

    public void makeChart(View view) {
        // create a new chart object
        mChart = new BarChart(getActivity());
        // mChart = (BarChart) view.findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setOnChartGestureListener(this);

        Charts_MyMarkerView mv = new Charts_MyMarkerView(getActivity(), R.layout.charts_custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv);
//        getUserActivityStats(view);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(12f);

        mChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(false);

        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        Button btn_activities = (Button) view.findViewById(R.id.btn_activities);
        btn_activities.setOnTouchListener(this);
        Button btn_places = (Button) view.findViewById(R.id.btn_places);
        btn_places.setOnTouchListener(this);
        Button btn_goals = (Button) view.findViewById(R.id.btn_goals);
        btn_goals.setOnTouchListener(this);
        Button btn_challenges = (Button) view.findViewById(R.id.btn_challenges);
        btn_challenges.setOnTouchListener(this);

        BarDataSet set1, set2, set3;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

        yVals1.add(new BarEntry(0, nrOfHomeWorkouts + nrOfOutdoorWorkouts));
        yVals2.add(new BarEntry(1, nrOfMovSpecChallenges));
        yVals3.add(new BarEntry(2, nrOfStreetMovementChallenges));

        //mChart.setData(generateBarData(3, 20000, 3));
        set1 = new BarDataSet(yVals1, "Workout");
        set1.setColor(Color.rgb(104, 241, 175));

        set2 = new BarDataSet(yVals2, "Movement-specific challenge");
        set2.setColor(Color.rgb(164, 228, 251));

        set3 = new BarDataSet(yVals3, "Street Movement challenge");
        set3.setColor(Color.rgb(242, 247, 158));

        //all the data to be displayed in the chart
        BarData data = new BarData(set1, set2, set3);

        //create new value formatter adjusting the value labes inside the chart
        data.setValueFormatter(new Charts_InsideValueFormatter());
        mChart.setData(data);

        // programatically add the chart
        FrameLayout parent = (FrameLayout) view.findViewById(R.id.parentLayout);
        parent.addView(mChart);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 0x6D6D6D sets how much to darken - tweak as desired
                setColorFilter(v, 0x6D6D6D);
                Log.d(TAG, "button touched");
                break;
            // remove the filter when moving off the button
            // the same way a selector implementation would
            case MotionEvent.ACTION_MOVE:
                Rect r = new Rect();
                v.getLocalVisibleRect(r);
                if (!r.contains((int) event.getX(), (int) event.getY())) {
                    setColorFilter(v, null);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setColorFilter(v, null);
                break;
        }
        return false;
    }

    private void setColorFilter(View v, Integer filter) {
        if (filter == null) v.getBackground().clearColorFilter();
        else {
            // To lighten instead of darken, try this:
            // LightingColorFilter lighten = new LightingColorFilter(0xFFFFFF, filter);
            LightingColorFilter darken = new LightingColorFilter(filter, 0x000000);
            v.getBackground().setColorFilter(darken);
        }
        // required on Android 2.3.7 for filter change to take effect (but not on 4.0.4)
        v.getBackground().invalidateSelf();
    }

    @Override
    public void onClick(View v) {

    }
}







