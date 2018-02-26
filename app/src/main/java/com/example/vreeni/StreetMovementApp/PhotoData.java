package com.example.vreeni.StreetMovementApp;

import android.animation.PropertyValuesHolder;

/**
 * Created by vreee on 23/02/2018.
 */

public class PhotoData {
    private String photoURL;
    private String fileName;
    private String id;

    public PhotoData() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
