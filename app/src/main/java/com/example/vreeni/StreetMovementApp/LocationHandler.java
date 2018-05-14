package com.example.vreeni.StreetMovementApp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.Manifest;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by vreee on 20/03/2018.
 */

public class LocationHandler extends MainActivity implements  ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String LOG_TAG = "LocationHandler";

    private boolean firstRun;

    //Constants used in the location settings dialog.
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    //The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //Callback for Location events. Initializes the location update circle
    private LocationCallback mLocationCallback;

    /*
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons. */
    private boolean mTrackingLocation = false;

    /*
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    /* A default location (Streetmekka, Copenhagen) and default zoom to use when location permission is
     * not granted. */
    private final LatLng mDefaultLocation = new LatLng(55.66208270000001, 12.540357099999937);
    private static final int DEFAULT_ZOOM = 12;

    /*
     * The geographical location where the device is currently located. That is, the last-known
     * location retrieved by the Fused Location Provider.
     */
    private Location mLastKnownLocation;
    private List<Location> listOfLocationUpdates;
    private String time;


    private Context mContext;
    private Activity mActivity;
    private GoogleMap mGoogleMap;

    private HashMap<String, ParkourPark> allSpots;



    public LocationHandler(Activity activity, Context context, GoogleMap map) {
        mActivity = activity;
        mContext = context;
        mGoogleMap = map;
        listOfLocationUpdates = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback();
        mTrackingLocation = true;
        firstRun = true;
        allSpots = new HashMap<>();


    }

    public FusedLocationProviderClient getmFusedLocationProviderClient() {
        return mFusedLocationProviderClient;
    }

    public void setmFusedLocationProviderClient(FusedLocationProviderClient mFusedLocationProviderClient) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
    }

    public LocationCallback getmLocationCallback() {
        return mLocationCallback;
    }


    public void setmLocationCallback(LocationCallback mLocationCallback) {
        this.mLocationCallback = mLocationCallback;
    }


    public boolean ismTrackingLocation() {
        return mTrackingLocation;
    }

    public void setmTrackingLocation(boolean mTrackingLocation) {
        this.mTrackingLocation = mTrackingLocation;
    }

    public String getmLastUpdateTime() {
        return mLastUpdateTime;
    }

    public void setmLastUpdateTime(String mLastUpdateTime) {
        this.mLastUpdateTime = mLastUpdateTime;
    }

    public LatLng getmDefaultLocation() {
        return mDefaultLocation;
    }

    public Location getmLastKnownLocation() {
        return mLastKnownLocation;
    }

    public void setmLastKnownLocation(Location mLastKnownLocation) {
        this.mLastKnownLocation = mLastKnownLocation;
    }


    public List<Location> getListOfLocationUpdates() {
        return listOfLocationUpdates;
    }

    public void setListOfLocationUpdates(List<Location> listOfLocationUpdates) {
        this.listOfLocationUpdates = listOfLocationUpdates;
    }

    /**
     * show Dialog asking the user to give permission to access his or her location
     */
    public void requestPermissions() {
        ActivityCompat.requestPermissions(mActivity, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }



    /**
     * initializing the regular location tracking
     * if permission is not granted, request it
     * if permission is granted, request location updates from the FusedLocationProviderclient,
     * based on what is declared in the locationRequest and the locationCallback, looper = null so the dialog window only pops up once and not repeatedly due to a permission check denied
     */
    public void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "checking permission: requesting permission");
            requestPermissions();

        } else {
            mTrackingLocation = true;
            createLocationCallback();
            Log.d(LOG_TAG, "checking permission: permission granted");
            //somehow put pending intent in here
            mFusedLocationProviderClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback, Looper.myLooper() /* Looper */);
            Log.d(LOG_TAG, "requesting location updates");
        }
    }




    /**
     * stop location tracking
     */
    public void stopTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
            mLastKnownLocation.setLatitude(0);
            mLastKnownLocation.setLongitude(0);
            updateLocationUI(mLastKnownLocation); //updating the location UI based on a null value, which leads to the recentering around a defaultLocation (StreetMekka)
            Log.d(LOG_TAG, "location tracking disabled. Location set to 0.");
//            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }


    /**
     * sets up the initial location request, defines the interval of how often updates are being retrieved, the priority, etc.
     *
     * @return
     */
    public LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(100000);
        locationRequest.setFastestInterval(25000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return locationRequest;
    }


    /**
     * creating a location callback regularly updating the user's position
     * calling the method updateLocationUI if the current view is a mapView
     * calling the method updateLocationOnFirebase to regularly save the user's last position to the database
     */
    public void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    Log.i(LOG_TAG, "Location: " + location.getLatitude() + " " + location.getLongitude() + " " + location.getTime());
                    mLastKnownLocation = location;
                    listOfLocationUpdates.add(location);
                    Log.i(LOG_TAG, "Location List: " + listOfLocationUpdates.size());

                    updateLocationUI(mLastKnownLocation);
                    updateLocationOnFirebase();
//                    displayLocationData();

                }
            }
        };
    }


//    public static final double R = 6372.8; // In kilometers
//
//    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        lat1 = Math.toRadians(lat1);
//        lat2 = Math.toRadians(lat2);
//
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
//        double c = 2 * Math.asin(Math.sqrt(a));
//        return R * c;
//    }


//    /**
//     * display location data in a textview at the bottom of the fragment
//     */
//    public void displayLocationData() {
//        if (mLastKnownLocation != null) {
//            if (mContext!=null) {
//                TextView tvMyLocation = (TextView) ((Activity) mContext).findViewById(R.id.tv_mylocation);
//                if (tvMyLocation!=null) {
//                    tvMyLocation.setText(mContext.getString(R.string.location_text,
//                            mLastKnownLocation.getLatitude(),
//                            mLastKnownLocation.getLongitude(),
//                            mLastKnownLocation.getTime()));
//                }
//            }
//        }
//    }

    /**
     * Return the current state of the permissions needed.
     */
    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }



    /**
     * Callback received when a permissions request has been completed. If permission is granted, get the location.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "location permission granted");
                    startTrackingLocation(); //includes the update of the location UI
                } else {
                    Log.d(LOG_TAG, "location permission denied");
                    mLastKnownLocation.setLatitude(0);
                    mLastKnownLocation.setLongitude(0);
                    updateLocationUI(mLastKnownLocation); //updating the location UI based on a null value, which leads to the recentering around a defaultLocation (StreetMekka)
                }
                break;
        }
    }


    /**
     * updates the user interface for the current location
     * If the map is loaded for the first time, the boolean value firstRun is true and the UI is setup as defined in the method setInitialLocationUI
     * For all the following times (when firstRun is false) the UI is setup the same way apart from the fact that the camera isn't recentered around the current location,
     * => enabling the user to freely explore the map without constant recentering around his location
     * Recentering is possible using google's myLocationButton in the top right corner of the mapView
     *
     * @param location current location received from the locationCallback as many times as defined in the interval
     */
    public void updateLocationUI(Location location) {
        if (firstRun) {
            setInitialLocationUI(location);
            firstRun = false;
            Log.d(LOG_TAG, "first run " + firstRun);
        } else {
            updateLocationUInoCamRecentering(location);
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     * Responsible for whether or not the map contains the blue dot, can be recentered, etc.
     */
    private void updateLocationUInoCamRecentering(Location location) {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (location != null) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                Location updatedLocation = new Location("");
                updatedLocation.setLatitude(location.getLatitude());
                updatedLocation.setLongitude(location.getLongitude());
//                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        new LatLng(mLastKnownLocation.getLatitude(),
//                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                if (mContext != null) {
                    Snackbar.make(((Activity)mContext).findViewById(R.id.fragment_container),
                            "Retrieving location update", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                Log.d(LOG_TAG, "Current location null. Using defaults.");
//                        mGoogleMap.moveCamera(CameraUpdateFactory
//                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
//                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * called if boolean firstRun = true; just makes one call to the last known location retrieved from other location based services
     * moves the camera to the user's location ONCE.
     * camera movement to the user's location is disabled in the locationRequest, cause it would not allow the user to explore the map without the camera constantly moving back to the current location
     * user can focus on his location by pressing google map's own myLocation button in the top right corner
     *
     * @param location
     */
    private void setInitialLocationUI(Location location) {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (location != null) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(),
                                location.getLongitude()), DEFAULT_ZOOM));
//                Snackbar.make(getActivity().getWindow().getDecorView().getRootView().findViewById(android.R.id.content),
//                        "First location update", Snackbar.LENGTH_SHORT).show(); !!!keeps crashing when screen rotation
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                Log.d(LOG_TAG, "Current location null. Using defaults.");
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public Date getDateFromString(String datetoSaved) {

        try {
            Date date = format.parse(datetoSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }

    }

    /**
     * updating the user's last position and the last time of update to the database
     */
    public void updateLocationOnFirebase() {
        time = String.format(mActivity.getString(R.string.location_text),
                mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude(),
                mLastKnownLocation.getTime());
//        Date date = Calendar.getInstance().getTime(); also get date?
        final String[] timeOnly = time.split("Timestamp: ");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        User user = new User();
        GeoPoint currPos = calculateGeoPoint(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        user.setPosition(currPos);
        if (mLastKnownLocation != null) {
            Log.d(LOG_TAG, "writing new location to database");
            Map<String, Object> dataUpdate = new HashMap<String, Object>();
            dataUpdate.put("position", currPos);
            dataUpdate.put("positionLastUpdate", timeOnly[1]);
            userDocRef
                    .set(dataUpdate, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(LOG_TAG, "New position has been registered for: " + userDocRef.getId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(LOG_TAG, "New position could not be saved");
                }
            });

        }

    }

    public static GeoPoint calculateGeoPoint(double latitude, double longitude) {
        return new GeoPoint(latitude, longitude);
    }


    public void getTrainingLocationsFromFirebaseAndTrack100Closest() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        GeofenceConstants.TRAINING_LOCATIONS.clear();
        allSpots = new HashMap<>();
        Log.d(LOG_TAG, "Training location Constants size: " + allSpots.size());
        db.collection("ParkourParks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Training locations: " + task.getResult().size());
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null) {
                                    ParkourPark queriedLocation = document.toObject(ParkourPark.class);
                                    queriedLocation.setDescription(document.getString("description (in Danish)"));
                                    allSpots.put(queriedLocation.getName(), queriedLocation);

                                }
                            }
                            Log.d(LOG_TAG, "all spots: " + allSpots.size());
                            startTracking100ClosestGeofences();

                        } else {
                        }
                    }
                });
    }


    @SuppressLint("MissingPermission")
    public void startTracking100ClosestGeofences() {
        createLocationCallback();
        //somehow put pending intent in here
        mFusedLocationProviderClient.requestLocationUpdates
                (getLocationRequest(), mLocationCallback, Looper.myLooper() /* Looper */);
        Log.d(LOG_TAG, "requesting location updates, callback for geofences: " + mLocationCallback);
    }

}
