package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class storing the current timer information in the shared preferences, when the application is paused or stopped
 */
public class PrefUtilsWarmupTimer {
    private static final String START_TIME = "countdown_timer_warmup";
    private SharedPreferences mPreferences;

    public PrefUtilsWarmupTimer(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getStartedTime() {
        return mPreferences.getInt(START_TIME, 0);
    }

    public void setStartedTime(int startedTime) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(START_TIME, startedTime);
        editor.apply();
    }

}
