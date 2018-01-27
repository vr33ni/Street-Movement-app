package com.example.vreeni.firebaseauthentication;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vreee on 4/01/2018.
 */

public class Workout {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String name;
    private String setting;
    private String level;
    private int duration;

    //each pre-defined workout consists of 5 exercises that can be defined using HashMaps
    //==> workout.getExerciseI() will be the set as a new HashMap<String, Object> exI
    private HashMap<String, Object> exerciseI;
    private HashMap<String, Object> exerciseII;
    private HashMap<String, Object> exerciseIII;
    private HashMap<String, Object> exerciseIV;
    private HashMap<String, Object> exerciseV;



    public Workout() {
    }

    public Workout(String name) {
        this.name = name;

    }

    public HashMap<String, Object> getExerciseI() {
        return exerciseI;
    }

    public void setExerciseI(HashMap<String, Object> exerciseI) {
        this.exerciseI = exerciseI;
    }

    public HashMap<String, Object> getExerciseII() {
        return exerciseII;
    }

    public void setExerciseII(HashMap<String, Object> exerciseII) {
        this.exerciseII = exerciseII;
    }

    public HashMap<String, Object> getExerciseIII() {
        return exerciseIII;
    }

    public void setExerciseIII(HashMap<String, Object> exerciseIII) {
        this.exerciseIII = exerciseIII;
    }

    public HashMap<String, Object> getExerciseIV() {
        return exerciseIV;
    }

    public void setExerciseIV(HashMap<String, Object> exerciseIV) {
        this.exerciseIV = exerciseIV;
    }

    public HashMap<String, Object> getExerciseV() {
        return exerciseV;
    }

    public void setExerciseV(HashMap<String, Object> exerciseV) {
        this.exerciseV = exerciseV;
    }

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







}
