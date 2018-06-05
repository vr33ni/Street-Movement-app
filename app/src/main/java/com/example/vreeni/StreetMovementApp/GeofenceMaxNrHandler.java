package com.example.vreeni.StreetMovementApp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.location.Geofence;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vreee on 20/03/2018.
 */

public class GeofenceMaxNrHandler extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String LOG_TAG = "GeofenceMaxNrHandler";

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

    private Context mContext;
    private Activity mActivity;
    private GoogleMap mGoogleMap;

    private HashMap<String, ParkourPark> allSpots;
    private HashMap<String, ParkourPark> max100TrainingLocations = new HashMap<>();
    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;


    public GeofenceMaxNrHandler(Activity activity, Context context, GoogleMap map) {
        mActivity = activity;
        mContext = context;
        mGoogleMap = map;
        listOfLocationUpdates = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback();
        mTrackingLocation = true;
        firstRun = true;
        allSpots = new HashMap<>();
        mGeofenceList = new ArrayList<>();

    }


    public ArrayList<Geofence> getmGeofenceList() {
        return mGeofenceList;
    }


    public HashMap<String, ParkourPark> getMax100TrainingLocations() {
        return max100TrainingLocations;
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
        Log.d(LOG_TAG, "starting startTrackingLocation");
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "checking permission: requesting permission");
            requestPermissions();

        } else {
            mTrackingLocation = true;
            Log.d(LOG_TAG, "creating location callback");

            createLocationCallback();
            Log.d(LOG_TAG, "checking permission: permission granted");
            //somehow put pending intent in here
            mFusedLocationProviderClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback, Looper.myLooper() /* Looper */);
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


    public void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.i(LOG_TAG, "Location: " + location.getLatitude() + " " + location.getLongitude() + " " + location.getTime());
                    mLastKnownLocation = location;
                    listOfLocationUpdates.add(location);

                    updateLocationUI(mLastKnownLocation);
//                    updateLocationOnFirebase();
//                    displayLocationData();
                    filterSpotsBasedOnRadius();
                }

            }
        };


    }


    /**
     * only take the spots within a certain radius and include them in the geofence list that are to be tracked
     */
    public void filterSpotsBasedOnRadius() {
        // GeofenceConstants.MAX_100_TRAINING_LOCATIONS = new HashMap<>();
        Log.d(LOG_TAG, "all spots: " + allSpots);
        max100TrainingLocations = new HashMap<>();
        ParkourPark parkLoc = new ParkourPark();
        parkLoc.setName("Default - Home");
        parkLoc.setCoordinates(calculateGeoPoint(55.647531, 12.527550));
        max100TrainingLocations.put("Home", parkLoc);
        Log.d("max100", "max100 before adding stuff: " + max100TrainingLocations);

        for (Map.Entry<String, ParkourPark> pkPark : allSpots.entrySet()) {
            Log.d("pkpark", "coordinates: " + pkPark.getValue().getCoordinates().getLatitude());
            Location trainingLocation = new Location("locationprovider");//provider name is unnecessary
            trainingLocation.setLatitude(pkPark.getValue().getCoordinates().getLatitude());
            trainingLocation.setLongitude(pkPark.getValue().getCoordinates().getLongitude());
            Log.d(LOG_TAG, "allSpots map: " + pkPark.getValue().getCoordinates());

            if ((mLastKnownLocation != null)) {
                if (mLastKnownLocation.distanceTo(trainingLocation) < GeofenceConstants.GEOFENCE_TRACKING_RADIUS_IN_METERS) {
                    Log.d(LOG_TAG, "in predef radius around user location: " + mLastKnownLocation + ", training loc: " + trainingLocation);
                    max100TrainingLocations.put(pkPark.getValue().getName(), pkPark.getValue());
//                  GeofenceConstants.MAX_100_TRAINING_LOCATIONS.put(pkPark.getKey(), pkPark.getValue());
                    Log.d(LOG_TAG, "updated map of locations to a max of 100: " + max100TrainingLocations.size());
                } else
                    Log.d(LOG_TAG, "distance: " + mLastKnownLocation.distanceTo(trainingLocation));
            }
        }
        GeofenceConstants.MAX_100_TRAINING_LOCATIONS = max100TrainingLocations;
        populateGeofenceList();

    }


    /**
     * This function dynamically creates geofences based on the user's location.
     */
    private void populateGeofenceList() {
        Log.d(LOG_TAG, "training locations max 100 map: " + GeofenceConstants.MAX_100_TRAINING_LOCATIONS);
        //checking ALL the retrieved locations
//        GeofenceConstants.MAX_100_TRAINING_LOCATIONS.entrySet()
        for (Map.Entry<String, ParkourPark> entry : getMax100TrainingLocations().entrySet()) {
            Log.d(LOG_TAG, "geofence max list" + getMax100TrainingLocations().size());
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence. If the user is within this radius around the training location, an event will be triggered
                    .setCircularRegion(
                            entry.getValue().getCoordinates().getLatitude(),
                            entry.getValue().getCoordinates().getLongitude(),
                            GeofenceConstants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(GeofenceConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
//                    .setLoiteringDelay(30000)
                    .build());
        }
    }


//    /**
//     * display location data in a textview at the bottom of the fragment
//     */
//    public void displayLocationData() {
//        if (mLastKnownLocation != null) {
//            if (mContext != null) {
//                TextView tvMyLocation = (TextView) ((Activity) mContext).findViewById(R.id.tv_mylocation);
//                if (tvMyLocation != null) {
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
//            Log.d(LOG_TAG, "first run " + firstRun);
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
                    Snackbar.make(((Activity) mContext).findViewById(R.id.fragment_container),
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

    public void updateLocationOnFirebase() {
        String time = this.getApplicationContext().getString(R.string.location_text);
        time = String.format(time, mLastKnownLocation.getLatitude(),
                mLastKnownLocation.getLongitude(),
                mLastKnownLocation.getTime());
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
            dataUpdate.put("positionUpdate", timeOnly[1]);
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


    @SuppressLint("MissingPermission")
    public void startTracking100ClosestGeofences() {
        createLocationCallback();
        //somehow put pending intent in here
        mFusedLocationProviderClient.requestLocationUpdates
                (getLocationRequest(), mLocationCallback, Looper.myLooper() /* Looper */);
        Log.d(LOG_TAG, "requesting location updates, callback for geofences: " + mLocationCallback);
    }


    //how to find object that relates to geofence again later after notification click
    public void saveTrainingLocationsInInternalMemory(HashMap<String, ParkourPark> map) {
        try {
            File file = new File(getDir("data", MODE_PRIVATE), "map");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(map);
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getTrainingLocationsFromFirebaseAndTrack100Closest() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        allSpots = new HashMap<>();

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


    /**
     * sets up the initial location request, defines the interval of how often updates are being retrieved, the priority, etc.
     *
     * @return
     */
    public LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(100000);
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        return locationRequest;
    }
}