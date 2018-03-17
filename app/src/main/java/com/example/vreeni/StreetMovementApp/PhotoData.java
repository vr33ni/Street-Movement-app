package com.example.vreeni.StreetMovementApp;


/**
 * Created by vreeni on 23/02/2018.
 */

/**
 * Class storing information on each photo object
 * used for an easier handling of the JSON file with parkour park information
 * in particular used to store information like url, title, description of photos in photo objects and add them to a list of photos; this list of photos can then be set as list in the ParkourPark object and its objects can then be uploaded to the database
 * ! precise handling of data yet to be improved !
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
