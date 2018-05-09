package com.example.vreeni.StreetMovementApp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by vreeni on 8/02/2018.
 */


/**
 * Fragment containing a google map with user location + locations to train, which the user can select as his/her training location by clicking on it
 */
public class GetCustomizedOutdoorWorkoutMapView extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private static final String LOG_TAG = "OutdoorWorkout MapView";

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private View mView;


    private CheckBox chckBxPk;
    private CheckBox chckBxCali;

    private List<Marker> listOfPkMarkers;
    private List<Marker> listOfCaliMarkers;
    private Map<Marker, ParkourPark> mapMarkerToPark;

//    private GoogleMaps_LocationHandler locationHandler;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private boolean mTrackingLocation;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //location callback for requesting location updates
    private LocationCallback mLocationCallback;

    // A default location (Streetmekka, Copenhagen) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(55.66208270000001, 12.540357099999937);
    private static final int DEFAULT_ZOOM = 12;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;


    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private String activity;
    private String setting;


    public static GetCustomizedOutdoorWorkoutMapView newInstance(String act, String set) {
        final Bundle bundle = new Bundle();
        GetCustomizedOutdoorWorkoutMapView fragment = new GetCustomizedOutdoorWorkoutMapView();
        bundle.putString("Activity", act);
        bundle.putString("Setting", set);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = getArguments().getString("Activity");
            setting = "Outdoors";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_google_maps, container, false);

        mMapView = (MapView) mView.findViewById(R.id.mapview);
        //mapview.oncreate(savedInstance) led to the app crashing when screen was rotated
        mMapView.onCreate(null);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        //update current location
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        mLocationCallback = new LocationCallback();

        mMapView.getMapAsync(this); //this is important

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //display the checkboxes show parkour parks / show calisthenics park
        chckBxPk = (CheckBox) mView.findViewById(R.id.showPkParks);
        chckBxCali = (CheckBox) mView.findViewById(R.id.showCaliParks);
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

    /**
     * create new arrayLists for listOfPkMarkers and listOfCaliMarkers and a new HashMap to identify marker and the respective parkour park object
     * set googleMap properties (mapType, ClickListeners)
     * get location permission from user
     * update the locationUI
     * get the device's location or set a default one if permission is denied
     * commented out: new JSON Handler loading location data from JSON files to database
     * getting all the locations from the database to display on map
     * handling the checkboxes parkour spots / calisthenics spots to display only the specific markers
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

        //set marker
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);

        // Prompt the user for permission.
        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
/*
  update information on firebase
*/
//        JasonHandler jh = new JasonHandler();
//        jh.retrieveFileFromResource();
//        //making changes to the park locations via this method
//        jh.updateFirestore();

        //download the locations of the parks from firestore and display them on the map
        getLocationsFromFirestoreToMap();

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

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

//        mMapView.getMapAsync(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mMapView.onSaveInstanceState(outState);
        if (mGoogleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mGoogleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(LOG_TAG, "Current location null. Using defaults.");
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            } else {
                //permission to show location denied
                Log.e(LOG_TAG, "Permission denied");
                mGoogleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //check if permission is granted
            mLocationPermissionGranted = true;
        } else {
            //if not granted, request it
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
////        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_TAG, "Permission denied");
                    mLocationPermissionGranted = true;
                } else {
                    Log.e(LOG_TAG, "Permission denied");
                    mLocationPermissionGranted = false;
                }
                break;
                // other 'case' lines to check for other
                // permissions this app might request.
            }
        }
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
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
    public void getLocationsFromFirestoreToMap() {
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
        Log.d(LOG_TAG, "InfoWindow clicked: " + marker.getTitle());
        //get hashmap marker - park object to get the location object and pass it to the detail view of the spot
        Log.d(LOG_TAG, "passing training location: " + mapMarkerToPark.get(marker));
        Fragment_TrainingLocation_View spot = Fragment_TrainingLocation_View.newInstance(activity, setting, mapMarkerToPark.get(marker));
        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, spot, "training location")
                .addToBackStack("training location")
                .commit();
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
//        possibly milliseconds faster, but backButton pressed events are not handled correclty at the moment, so crash
//        getActivity().runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                loading everything here and creating info windows at once => bad performance, slwo
//                GoogleMapsCustomInfoWindow adapter = new GoogleMapsCustomInfoWindow(getActivity(), photoURL);
//                mGoogleMap.setInfoWindowAdapter(adapter);
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
//            }
//        });

        //pass along the picture URL to the customInfoWindow
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
}