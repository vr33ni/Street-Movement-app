package com.example.vreeni.firebaseauthentication;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vreee on 4/01/2018.
 */

public class Exercise {
    private String description; //name of the exercise, eg Pullups
    private HashMap<String, Object> Description;
    private int repetition; //5x 5 pullups
    private int duration; //30sec hanging (duration = optional)
    private File img;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference uploadRef = storageRef.child("HomeWorkout_Beginner");


    public Exercise() {
    }


    public Exercise(String description) {
        this.description = description;
    }


    public int getRepetition() {
        return repetition;
    }

    public int getDuration() {
        return duration;
    }


    private void addToWorkout() {

    }

    // Create a storage reference from our app

    public void addImg() {

        Uri file = Uri.fromFile(new File("D:/Programming/FirebaseAuthentication/app/src/main/res/drawable/img_railheaven.jpg"));
        StorageReference wkStorRef = storageRef.child("HomeWorkout_Beginner/" + file.getLastPathSegment());
        UploadTask uploadTask = wkStorRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }





}
