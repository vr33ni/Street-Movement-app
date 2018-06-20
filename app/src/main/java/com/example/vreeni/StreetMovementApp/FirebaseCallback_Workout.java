package com.example.vreeni.StreetMovementApp;

/**
 * interface used in queries to the database
 * returns a workout* object from the database
 * *workout hereby also inludes movement specific challenges, as they are stored in the same format (setting, level, list of exercises...)
 */
public interface FirebaseCallback_Workout {

    void onQuerySuccess(Workout workout);

    void onFailure();
}
