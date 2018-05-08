package com.example.vreeni.StreetMovementApp;

/**
 * Created by vreee on 1/04/2018.
 */

import android.media.midi.MidiDeviceService;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Constants used in this sample.
 */

final class GeofenceConstants {

    private GeofenceConstants() {
    }

    private static final String PACKAGE_NAME = "com.example.vreeni.StreetMovementApp";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km

    /**
     * radius in which is being searched for geofences based on user's current location
     */
    static final float GEOFENCE_TRACKING_RADIUS_IN_METERS = 10000; // 1 mile, 1.6 km


    /**
     * Map for storing information about the locations of parkour and fitness parsk
     */
    static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();
    static final HashMap<String, ParkourPark> TRAINING_LOCATIONS = new HashMap<>();
    static HashMap<String, ParkourPark> MAX_100_TRAINING_LOCATIONS = new HashMap<>();



    static {
        // San Francisco International Airport. OSU
        BAY_AREA_LANDMARKS.put("Home", new LatLng(48.612457,  11.950368));
        // Googleplex. OSU
        BAY_AREA_LANDMARKS.put("Streetmekka", new LatLng(55.66208270000001, 12.540357099999937));
    }
}

