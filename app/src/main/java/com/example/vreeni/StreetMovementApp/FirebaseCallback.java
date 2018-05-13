package com.example.vreeni.StreetMovementApp;

public interface FirebaseCallback {

    void onQuerySuccess(Workout workout);

    void onFailure();
}
