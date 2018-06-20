package com.example.vreeni.StreetMovementApp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class representing a workout document in the database
 * => containing all the fields that are also listed in the database, so data from a database query can be converted to a workout object
 * Implementing Parcelable Interface allowing for Workout objects to be put as parcelables in bundles
 */
public class Workout implements Parcelable {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String name;
    private String activity;
    private String setting;
    private String level;
    private int duration;

    private ArrayList<Object> listOfExercises = new ArrayList<>();




    public Workout() {
    }

    public ArrayList<Object> getListOfExercises() {
        return listOfExercises;
    }

    public void setListOfExercises(ArrayList<Object> listOfExercises) {
        this.listOfExercises = listOfExercises;
    }

    public Workout(String name) {
        this.name = name;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

//    public HashMap<String, Object> getExerciseI() {
//        return exerciseI;
//    }
//
//    public void setExerciseI(HashMap<String, Object> exerciseI) {
//        this.exerciseI = exerciseI;
//    }
//
//    public HashMap<String, Object> getExerciseII() {
//        return exerciseII;
//    }
//
//    public void setExerciseII(HashMap<String, Object> exerciseII) {
//        this.exerciseII = exerciseII;
//    }
//
//    public HashMap<String, Object> getExerciseIII() {
//        return exerciseIII;
//    }
//
//    public void setExerciseIII(HashMap<String, Object> exerciseIII) {
//        this.exerciseIII = exerciseIII;
//    }
//
//    public HashMap<String, Object> getExerciseIV() {
//        return exerciseIV;
//    }
//
//    public void setExerciseIV(HashMap<String, Object> exerciseIV) {
//        this.exerciseIV = exerciseIV;
//    }
//
//    public HashMap<String, Object> getExerciseV() {
//        return exerciseV;
//    }
//
//    public void setExerciseV(HashMap<String, Object> exerciseV) {
//        this.exerciseV = exerciseV;
//    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() { return duration; }

    public void setDuration(int duration) { this.duration = duration; }

    private void addExerciseByCategory() {
    }


    //implementing the parcelable interface
    protected Workout(Parcel in) {
        name = in.readString();
        setting = in.readString();
        level = in.readString();
        duration = in.readInt();
//        exerciseI = (HashMap) in.readValue(HashMap.class.getClassLoader());
//        exerciseII = (HashMap) in.readValue(HashMap.class.getClassLoader());
//        exerciseIII = (HashMap) in.readValue(HashMap.class.getClassLoader());
//        exerciseIV = (HashMap) in.readValue(HashMap.class.getClassLoader());
//        exerciseV = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(setting);
        dest.writeString(level);
        dest.writeInt(duration);
//        dest.writeValue(exerciseI);
//        dest.writeValue(exerciseII);
//        dest.writeValue(exerciseIII);
//        dest.writeValue(exerciseIV);
//        dest.writeValue(exerciseV);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Workout> CREATOR = new Parcelable.Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };
}