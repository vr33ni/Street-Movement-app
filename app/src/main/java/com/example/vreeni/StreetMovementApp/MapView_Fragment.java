package com.example.vreeni.StreetMovementApp;

import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * Created by vreeni on 8/02/2018.
 */

/**
 * Fragment containing a google map with user location + locations to train, which the user can select as his/her training location by clicking on it
 * The user can further add new training locations that will then have to be approved by Street Movement
 */
public class MapView_Fragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener, View.OnClickListener {

    private static final String LOG_TAG = "SpotMap";

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private View mView;

    //to set the value based on whether its the first run of location callbacks or not
    private boolean firstRun;

    //checkboxes to select whether there should be displayed pk parks, regular fitness parks, both or none
    private CheckBox chckBxPk;
    private CheckBox chckBxCali;

    //textview in the bottom of the window displaying the location details and timestamp of last location update
    private TextView tvMyLocation;

    //textfield acting as a searchbar
    private EditText searchBar;
    private ImageButton btn_search;

    private List<Marker> listOfPkMarkers;
    private List<Marker> listOfCaliMarkers;
    private Map<Marker, ParkourPark> mapMarkerToPark;
//    private List<Location> listOfLocationUpdates;

    //popup window variables
    private PopupWindow popupWindow;
    private PopupWindow popupWindowSelectUploadSource;
    private ImageView selectedImage;
    //first popup window
    private EditText et_name;
    private EditText et_desc;
    private Button btn_submit;
    private long mLastClickTime = 0;


    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private Uri photoURI;
    private Uri downloadUrl;
    private String mCurrentPhotoPath;
    private LatLng lating;

    //Constants used in the location settings dialog.
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    //The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //Callback for Location events. Initializes the location update circle
    private LocationCallback mLocationCallback;

    private LocationHandler locationHandler;

    /*
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons. */
    private boolean mTrackingLocation;

    /*
     * Checks whether the user wants to share his location with others
     * If true, regular location updates will be enabled and shared with other users
     */
    private boolean locationSharing = false;

    /*
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;
    private String time;

    /* A default location (Streetmekka, Copenhagen) and default zoom to use when location permission is
     * not granted. */
    private final LatLng mDefaultLocation = new LatLng(55.66208270000001, 12.540357099999937);
    private static final int DEFAULT_ZOOM = 12;

    /*
     * The geographical location where the device is currently located. That is, the last-known
     * location retrieved by the Fused Location Provider.
     */
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    private static final int PLACE_PICKER_REQUEST = 1;



    /**
     * inflating the fragment layout + the mapview object
     * initializing the FusedlocationProviderClient and the LocationCallback
     * starting to retrieve the mapView (asynchronously)
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_google_maps, container, false);

        mMapView = (MapView) mView.findViewById(R.id.mapview);
        mMapView.onCreate(null);

        updateValuesFromBundle(savedInstanceState);

        locationHandler = new LocationHandler(this.getActivity(), this.getActivity(), mGoogleMap);
        mLastKnownLocation = locationHandler.getmLastKnownLocation();
        mTrackingLocation = locationHandler.ismTrackingLocation();


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        mLocationCallback = new LocationCallback();

        mMapView.getMapAsync(this); //this is important

        return mView;
    }

    /**
     * Once the fragment and mapview object are loaded, set up additional views contained in it
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //display the checkboxes show parkour parks / show calisthenics park
        chckBxPk = (CheckBox) mView.findViewById(R.id.showPkParks);
        chckBxCali = (CheckBox) mView.findViewById(R.id.showCaliParks);

        //display a textview with current location data
//        tvMyLocation = (TextView) mView.findViewById(R.id.tv_mylocation);

        //display the refresh button
        searchBar = (EditText) mView.findViewById(R.id.et_searchbar);
        btn_search = (ImageButton) mView.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
    }

    /**
     * create new arrayLists for listOfPkMarkers and listOfCaliMarkers and a new HashMap to identify marker and the respective parkour park object
     * set googleMap properties (mapType, ClickListeners)
     * get location permission from user
     * update the locationUI
     * get the device's location or set a default one if permission is denied
     * commented out: new JSON Handler loading location data from JSON files to database
     * getting all the locations from the database to display on map
     * handling the checkboxes parkour spots / calisthenics spots to display only the specific markers
     * initializing the locationCallback and the startTracking methods to start regular location updates
     *
     * @param googleMap
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getContext());

        listOfPkMarkers = new ArrayList<>();
        listOfCaliMarkers = new ArrayList<>();
        mapMarkerToPark = new HashMap<>();
//        listOfLocationUpdates = new ArrayList<>();

        firstRun = true;

        //setup map
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);

//        //asking user if they wana share their location with others
//        Dialog dialog = new Dialog(getActivity());
//        dialog.setCancelable(true);
//        View view  = getActivity().getLayoutInflater().inflate(R.layout.custom_alert_dialog_share_location, null);
//        dialog.setContentView(view);
//        Button btnYes = (Button) view.findViewById(R.id.btn_yes);
//        Button btnNo = (Button) view.findViewById(R.id.btn_no);
//        dialog.show();

/*
  update information on firebase
*/
//        JasonHandler jh = new JasonHandler();
//        jh.retrieveFileFromResource();
//        //making changes to the park locations via this method
//        jh.updateFirestore();

        //download the locations of the parks from firestore and display them on the map
        getTrainingLocationsFromFirestoreToMap();

        //handle checkbox clicks
        if (chckBxPk != null) {
            chckBxPk.setChecked(true);
            chckBxPk.setOnClickListener(v -> {
                if (chckBxPk.isChecked()) showPkSpots();
                else hidePkSpots();
            });
        }
        if (chckBxCali != null) {
            chckBxCali.setChecked(true);
            chckBxCali.setOnClickListener(v -> {
                if (chckBxCali.isChecked()) showCaliSpots();
                else hideCaliSpots();
            });
        }

        //retrieve regular location updates
        //possibly separate comman for when map is first loaded? to only move camera once
        //get last location => once retrieved, start regular updates?
        locationHandler = new LocationHandler(this.getActivity(), this.getActivity(), mGoogleMap);
        locationHandler.startTrackingLocation();

//        updateLocationUI(locationHandler.getmLastKnownLocation());
//        displayLocationData();

    }


    /**
     * show Dialog asking the user to give permission to access his or her location
     */
    public void requestPermissions() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
    }

    /**
     * initializing the regular location tracking
     * if permission is not granted, request it
     * if permission is granted, request location updates from the FusedLocationProviderclient,
     * based on what is declared in the locationRequest and the locationCallback, looper = null so the dialog window only pops up once and not repeatedly due to a permission check denied
     */
    public void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(LOG_TAG, "checking permission: requesting permission");
            requestPermissions();

        } else {
            mTrackingLocation = true;
            Log.d(LOG_TAG, "checking permission: permission granted");
            mFusedLocationProviderClient.requestLocationUpdates
                    (getLocationRequest(), mLocationCallback, null /* Looper */);
            Log.d(LOG_TAG, "requesting location updates");
        }
    }


    /**
     * sets up the initial location request, defines the interval of how often updates are being retrieved, the priority, etc.
     *
     * @return
     */
    public LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(150000);
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    /**
     * Creates a callback for receiving location events.
     */


    public void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    Log.i(LOG_TAG, "Location: " + location.getLatitude() + " " + location.getLongitude() + " " + location.getTime());
                    mLastKnownLocation = location;
//                    listOfLocationUpdates.add(location);
                    updateLocationUI(location);
                    updateLocationOnFirebase();
//                    displayLocationData();
                }
            }
        };
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

//    /**
//     * Callback received when a permissions request has been completed. If permission is granted, get the location.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_LOCATION_PERMISSION:
//                // If the permission is granted, get the location,
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(LOG_TAG, "location permission granted");
//                    locationHandler.startTrackingLocation(); //includes the update of the location UI
//                } else {
//                    Log.d(LOG_TAG, "location permission denied");
//                    mLastKnownLocation.setLatitude(0);
//                    mLastKnownLocation.setLongitude(0);
//                    updateLocationUI(mLastKnownLocation); //updating the location UI based on a null value, which leads to the recentering around a defaultLocation (StreetMekka)
//                }
//                break;
////            case REQUEST_CAMERA_PERMISSION:
////                if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
////                    ErrorDialog.newInstance(("Requesting permission"))
////                            .show(getChildFragmentManager(), FRAGMENT_DIALOG);
////                } else {
////                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////                }
//        }
//    }


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
        Log.d(LOG_TAG, "mlastknown set initial " + locationHandler.getmLastKnownLocation());

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
                if (getActivity() != null) {
                    Snackbar.make(getActivity().getWindow().getDecorView().getRootView().findViewById(R.id.fragment_container),
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
     * gets all the parkour park documents from the database;
     * then creates a parkour park object for each document, sets its description (Field description is slightly different to database and has to be set manually);
     * then calls the method addMarker to create a marker on the map based on the properties of this parkour park object such as coordinates, name, etc.
     */
    public void getTrainingLocationsFromFirestoreToMap() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        delete specific document from the database => apparently only works form within the code, database deleltion alone doesnt do it
//        db.collection("ParkourParks").document("PLUG N PLAY").delete();
        db.collection("ParkourParks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null) {
//                                    Log.d(LOG_TAG, document.getId() + " => " + document.getData());
                                    ParkourPark queriedLocation = document.toObject(ParkourPark.class);
                                    queriedLocation.setDescription(document.getString("description (in Danish)"));
                                    addApprovedLocationMarkersOnMap(queriedLocation);
                                }
                            }
                            if (getActivity() != null) {
                                Snackbar.make(getActivity().getWindow().getDecorView().getRootView().findViewById(android.R.id.content),
                                        "Spotmap up to date", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * handles click events on the info windows => selection of the respective parkour park to start a training;
     * passing the parkour park object as parcelable in a bundle to the next fragment
     *
     * @param marker infoWindow is specific for each marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Fragment fragment = null;
        Log.d(LOG_TAG, "InfoWindow clicked: " + marker.getTitle());
        //passing object as parcelable
        //get hashmap marker - park object to get the location object
        Bundle outdoorBundle = new Bundle();
        outdoorBundle.putParcelable("TrainingLocation", mapMarkerToPark.get(marker));
        //create new fragment displaying the result of either of the choices
        fragment = new Fragment_TrainingLocation_View();
        Log.d(LOG_TAG, "outdoor bundle content " + outdoorBundle.getParcelable("TrainingLocation"));
        if (outdoorBundle != null) {
            fragment.setArguments(outdoorBundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();


//        Fragment fragment = null;
//        Log.d(LOG_TAG, "InfoWindow clicked: " + marker.getTitle());
//        //passing object as parcelable
//        //get hashmap marker - park object to get the location object
//        outdoorBundle = new Bundle();
//        outdoorBundle.putParcelable("OutdoorWorkout", mapMarkerToPark.get(marker));
//        //create new fragment displaying the result of either of the choices
//        fragment = new Fragment_Training_Workout_Level_Outdoor();
//        Log.d(LOG_TAG, "outdoor bundle content " + outdoorBundle.getParcelable("OutdoorWorkout"));
//        if (outdoorBundle != null) {
//            fragment.setArguments(outdoorBundle);
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, fragment)
//                    .addToBackStack(null)
//                    .commit();
//        }

        }
    }


    /**
     * handles click events on markers
     * => creation of a custom info window after clicking on each marker and then displaying it incl. name, image, description
     *
     * @param marker the respective marker that is clicked on
     * @return has to return true
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        GoogleMapsCustomInfoWindow adapter = new GoogleMapsCustomInfoWindow(getActivity());
        mGoogleMap.setInfoWindowAdapter(adapter);
        marker.showInfoWindow();
        return true;
    }

    /**
     * is called after checkbox parkour is checked => shows all the markers based on parkour parks with the property parkour = true
     */
    public void showPkSpots() {
        //show parkour spots only
        for (Marker m : listOfPkMarkers) {
            m.setVisible(true);
        }
    }

    /**
     * is called after checkbox calisthenics is checked => shows all the markers based on parkour parks with the property calisthenics = true
     */
    public void showCaliSpots() {
        //show cali spots only
        for (Marker m : listOfCaliMarkers) {
            m.setVisible(true);
        }
    }

    /**
     * is called after checkbox parkour is unchecked => hides all the markers based on parkour parks with the property parkour = false
     */
    public void hidePkSpots() {
        for (Marker m : listOfPkMarkers) {
            m.setVisible(false);
        }
    }

    /**
     * is called after checkbox calisthenics is unchecked => shows all the markers based on parkour parks with the property calisthenics = false
     */
    public void hideCaliSpots() {
        for (Marker m : listOfCaliMarkers) {
            m.setVisible(false);
        }
    }

    /**
     * adds markers on the map based on the parkour park objects and their properties
     * => adding parkour parks to listOfPkParks, calisthenics to listOfCalisthenics
     * => adding each marker to a hashmap of type marker, object to get a park object for a marker key later on
     * does not create custom info windows yet (performance issues)
     *
     * @param queriedLocation = the parkour park objects from the database incl all their properties defined in the database
     */
    public void addApprovedLocationMarkersOnMap(ParkourPark queriedLocation) {
        GeoPoint gp = queriedLocation.getCoordinates();
        String name = queriedLocation.getName();
        String shortDescription = queriedLocation.getDescription();
        HashMap<String, Object> photoI = queriedLocation.getPhoto_0();
        String photoURL;
        if (photoI != null) {
            photoURL = (String) photoI.get("url");
        } else {
            //default photo
            photoURL = "http://map.gadeidraet.dk/content/uploads/2016/06/068eb118452253193acfc9a00cb5b8f9_frederiksbergroskildevej300x300.jpg";
        }
        Log.d(LOG_TAG, "photo url: " + photoURL);
        //show pk spots so that everything is loaded in in on map ready
        //define marker options and set the custom info window
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(new LatLng(gp.getLatitude(), gp.getLongitude()))
                .title(name + "_" +
                        "Description: " + shortDescription)
                .snippet(photoURL)
                .icon(BitmapDescriptorFactory.defaultMarker());

        Marker m = mGoogleMap.addMarker(markerOpt);
        if (queriedLocation.isParkour()) {
            listOfPkMarkers.add(m);
            mapMarkerToPark.put(m, queriedLocation);
        }
        if (queriedLocation.isCalisthenics()) {
            listOfCaliMarkers.add(m);
            mapMarkerToPark.put(m, queriedLocation);
        }
        Log.d("marker", markerOpt.getTitle());
        m.hideInfoWindow();


        //pass along the picture URL to the customInfoWindow => everything is being downloaded onMapReady
//            GoogleMapsCustomInfoWindow adapter = new GoogleMapsCustomInfoWindow(getActivity(), photoURL);
//            mGoogleMap.setInfoWindowAdapter(adapter);
//            Marker m = mGoogleMap.addMarker(markerOpt);
//            if (queriedLocation.isParkour()) {
//                listOfPkMarkers.add(m);
//                mapMarkerToPark.put(m, queriedLocation);
//            }
//            if (queriedLocation.isCalisthenics()) {
//                listOfCaliMarkers.add(m);
//                mapMarkerToPark.put(m, queriedLocation);
//            }
//            m.showInfoWindow();
    }


    /**
     * enables the user to add a new training location to the map that will then be uploaded to firebase for approval
     * opens a popup window
     */
    public void addToBeApprovedLocationMarkersOnMap() {
        openPopupWindow();
        Log.d(LOG_TAG, "adding new to be approved marker on map");
    }

    /**
     * prompts the opening of a popup window requesting further information to the just added marker on the map
     * is focusable meaning it can be closed by clicking anywhere outside the window
     * fields in the popup window are required (name, description, image) for approval as a new training location
     */
    public void openPopupWindow() {
        View layout = getLayoutInflater().inflate(R.layout.custom_popup_window_addmarker, null);
        popupWindow = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        et_name = (EditText) layout.findViewById(R.id.et_toBeApprovedLocation_name);
        et_desc = (EditText) layout.findViewById(R.id.et_toBeApprovedLocation_description);
        selectedImage = (ImageView) layout.findViewById(R.id.iv_toBeApprovedLocation_image);
        Button btn_selectImg = (Button) layout.findViewById(R.id.btn_selectImage);
        btn_selectImg.setOnClickListener(this);
        btn_submit = (Button) layout.findViewById(R.id.btn_submitForApproval);
        btn_submit.setOnClickListener(this);
        btn_submit.setEnabled(true);
//        int x = Resources.getSystem().getDisplayMetrics().widthPixels/2-150;
//        int y = Resources.getSystem().getDisplayMetrics().heightPixels/2-100;
        popupWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);
        Log.d(LOG_TAG, "opening a popup window");
    }


    /**
     * opens a second popup window where the user can choose if he wants to take a picture using the camera or upload one from the gallery
     * once one button is clicked, the other one is set to disabled
     */
    public void openPopUpWindowSelectUploadSource() {
        View layout = getLayoutInflater().inflate(R.layout.custom_popup_window_select_upload_source, null);
        popupWindowSelectUploadSource = new PopupWindow(
                layout,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        Button btn_takePicture = (Button) layout.findViewById(R.id.btn_takePicture);
        btn_takePicture.setOnClickListener(this);
        btn_takePicture.setEnabled(true);
        Button btn_chooseFromGallery = (Button) layout.findViewById(R.id.btn_chooseFromGallery);
        btn_chooseFromGallery.setOnClickListener(this);
        btn_chooseFromGallery.setEnabled(true);
        Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancel_selectImg_process);
        btn_cancel.setOnClickListener(this);
        popupWindowSelectUploadSource.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
        dimBehind(popupWindowSelectUploadSource);
        Log.d(LOG_TAG, "opening the popup window to select upload source");
    }

    /**
     * once popup window is open, dim everything behind it for the time it is opened
     * @param popupWindow based on which popup window is set as a parameter, it is taken as reference point and everything behind is dimmed
     */
    private void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    /**
     * validate user input and upload to the collection of "trainingLocationsToBeApproved" on firestore for further data processing
     */
    public void submitNewLocationForApprovalToFirestore() {
        if (validateUserInput()) {
            //upload image to storage
            Date currentTime = Calendar.getInstance().getTime();
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference toBeApprovedTrainingLocationsRef = storageRef.child("toBeApprovedTrainingLocations/" + photoURI.getLastPathSegment());
            Log.d(LOG_TAG, "Uri: " + photoURI);

            UploadTask uploadTask = toBeApprovedTrainingLocationsRef.putFile(photoURI);
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
            }).addOnSuccessListener(taskSnapshot -> {
//                btn_submit.setEnabled(false);
                // once file is uploading successfully, button is set to disabled to prevent multiple upload
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(LOG_TAG, "download url " + downloadUrl);

                //upload text information to database + create reference to storage
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference spotToBeApproved = db.collection("TrainingLocationsToBeApproved").document(/*marker.getId()*/);
                DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                HashMap<String, Object> data = new HashMap<>();
                data.put("name", et_name.getText().toString().trim());
                data.put("description", et_desc.getText().toString().trim());
                data.put("suggestedBy", userDocRef);
                data.put("image", downloadUrl.toString());
                data.put("coordinates", calculateGeoPoint(lating.latitude, lating.longitude));
                data.put("date", currentTime);
                spotToBeApproved
                        .set(data, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                    Log.d(LOG_TAG, "New Location has been submitted for approval: " + spotToBeApproved.getId());
                    //close popup window after upload
                    popupWindow.dismiss();
                    //mark marker as diff color or transparent? to see its only a suggestion.
                    Marker m = mGoogleMap.addMarker((new MarkerOptions()
                            .position(lating)
                            .title(et_name.getText().toString().trim() + "_" +
                                    "Description: " + et_desc.getText().toString().trim())
                            .snippet(downloadUrl.toString())
                            .alpha(0.3f)));
                    m.hideInfoWindow();
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOG_TAG, "New Location could not be saved");
                    }
                });
            });
        } else {
            return;
        }
    }


    /**
     * checking user input and marking fields as required if submit button is pressed and some fields are empty
     *
     * @return
     */
    private boolean validateUserInput() {
        boolean valid = true;
        String name = et_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            et_name.setError("Required.");
            valid = false;
        } else {
            et_name.setError(null);
        }

        String description = et_desc.getText().toString();
        if (TextUtils.isEmpty(description)) {
            et_desc.setError("Required.");
            valid = false;
        } else {
            et_desc.setError(null);
        }
        return valid;
    }


    /**
     * access the user's image gallery to select a photo and load into the imageview in the popup window
     */
    public void retrieveFromGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    /**
     * access the user's camera to take a photo and load into the imageview in the popup window
     */
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d(LOG_TAG, "creating photofile: " + photoFile);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(LOG_TAG, "error creating the file" + ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                File testFile = new File(mCurrentPhotoPath);
                Log.d(LOG_TAG, "test file length: " + testFile.getAbsolutePath().length());
                Log.d(LOG_TAG, "test file Path: " + mCurrentPhotoPath);

                photoURI = FileProvider.getUriForFile(this.getActivity(),
                        "com.example.vreeni.StreetMovementApp", photoFile);
                Log.d(LOG_TAG, "uri: " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.d(LOG_TAG, "intent: " + takePictureIntent + " , " + REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * callback for camera intents, handling both retrieveFromGallyer() and takePicture()
     *
     * @param requestCode either pickImage from Gallery or takePicture to access the camera
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
            photoURI = data.getData();
            Log.d(LOG_TAG, "uri " + photoURI);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), photoURI);
                popupWindowSelectUploadSource.dismiss();
                selectedImage.setVisibility(View.VISIBLE);
                selectedImage.setImageBitmap(bitmap);
                Log.d(LOG_TAG, "img path" + bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            popupWindowSelectUploadSource.dismiss();
            selectedImage.setVisibility(View.VISIBLE);
            selectedImage.setImageURI(photoURI);
            Log.d(LOG_TAG, "file path" + selectedImage);
//                Glide.with(this)
//                        .load(getBitmapFromAssets(this.getActivity(), mCurrentPhotoPath, 100, 100))
//                        .error(R.drawable.img_railheaven)
//                        .into(selectedImage);
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * getting last known location. This happens when location tracking is disabled.
     * Currently, location tracking is always enabled, so this method is not accessed.
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(LOG_TAG, "Location permissions granted - Retrieving last known location");
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                Log.d(LOG_TAG, "location: " + mLastKnownLocation.getLatitude() + " "
                                        + mLastKnownLocation.getLongitude() + " " + mLastKnownLocation.getTime());

                            } else {
                                Log.d(LOG_TAG, "Current location null. Using defaults.");
                                mGoogleMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    });
        }
    }


//    /**
//     * display location data in a textview at the bottom of the fragment
//     */
//    public void displayLocationData() {
//        if (locationHandler.getmLastKnownLocation() != null) {
//            tvMyLocation.setText(getString(R.string.location_text,
//                    locationHandler.getmLastKnownLocation().getLatitude(),
//                    locationHandler.getmLastKnownLocation().getLongitude(),
//                    locationHandler.getmLastKnownLocation().getTime()));
//        }
//    }

    /**
     * implemented method from View.OnClickListener interface
     * => possibly set up a button that shows other users' locations
     * => handles click events on the submit button in the popup window
     *
     * @param v defining the different views that can be clicked on
     */
    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        //opening a second popup window asking to choose the upload source
        if (v.getId() == R.id.btn_selectImage) {
            openPopUpWindowSelectUploadSource();
        }
        //opening image gallery on phone to select picture
        if (v.getId() == R.id.btn_chooseFromGallery) {
            retrieveFromGallery();
        }
        //open camera
        if (v.getId() == R.id.btn_takePicture) {
            takePicture();
        }
        if (v.getId() == R.id.btn_cancel_selectImg_process) {
            //return to first popup window
        }
        if (v.getId() == R.id.btn_submitForApproval) {
            submitNewLocationForApprovalToFirestore();
        }
        if (v.getId() == R.id.btn_search) {
            //search for entered text
            search();
        }

    }


    public void search() {
        for (Map.Entry entry : mapMarkerToPark.entrySet()) {
            //check if the term entered equals one of the marker names from the map
            if (searchBar.getText().toString().trim().equals(entry.getKey())) {

            }
        }
    }


    /**
     * defining which values from existing bundle are being assigned to fields;
     * sually done in the beginning
     *
     * @param savedInstanceState
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(TRACKING_LOCATION_KEY)) {
                mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
            }
            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
        }
    }

    /**
     * determines what is being saved in a bundle
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mMapView.onSaveInstanceState(outState);
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
//            outState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
            outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
//            outState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
            super.onSaveInstanceState(outState);
        }
    }


    /**
     * this gets called everytime after a pause.
     * The RequestingPermission DialogField is causing the fragment to pause, so all actions that are to continue based on granting or denying permission, are handled here
     * if boolean is true and permission is granted, location tracking is started/continued
     * if boolean is false, the location UI will be updated accordingly (location is set to 0, camera is centered around DefaultLocation)
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.d(LOG_TAG, "on resume");
        if (mTrackingLocation && locationHandler.checkPermissions()) {
            Log.d(LOG_TAG, "permission checked " + locationHandler.checkPermissions());
            locationHandler.startTrackingLocation(); //includes updating the location UI

        } else
            updateLocationUI(locationHandler.getmLastKnownLocation()); //updating the location UI based on a null value, which leads to the recentering around a defaultLocation (StreetMekka)
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * fragment is set to pause, whenever a dialog pops up
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
//        if (mTrackingLocation) {
//            stopTrackingLocation();
//            mTrackingLocation = true;
//        }
        Log.d(LOG_TAG, "on Pause");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        locationHandler.stopTrackingLocation();
        Log.d(LOG_TAG, "location updates stopped");
    }

    public static GeoPoint calculateGeoPoint(double latitude, double longitude) {
        return new GeoPoint(latitude, longitude);
    }

    /**
     * update the user's current location on firebase
     */
    public void updateLocationOnFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        User user = new User();
        GeoPoint currPos = calculateGeoPoint(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        user.setPosition(currPos);
        if (mLastKnownLocation != null) {
            Log.d(LOG_TAG, "writing new location to database");
            time = String.format(this.getActivity().getString(R.string.location_text),
                    locationHandler.getmLastKnownLocation().getLatitude(),
                    locationHandler.getmLastKnownLocation().getLongitude(),
                    locationHandler.getmLastKnownLocation().getTime());
            final String[] timeOnly = time.split("Timestamp: ");
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

    /**
     * possibility for user to add a yet to be approved marker on the map
     * add differently colored marker without uploading it to the list of approved locations AND
     * show popup window with edittext for user to enter REQUIRED information regarding the new training location
     *
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        //set class variable lating, so it can be accessed from other methods and used to finally create the new (transparent) marker with the yet to be approved location
        lating = latLng;
        addToBeApprovedLocationMarkersOnMap();
    }
}