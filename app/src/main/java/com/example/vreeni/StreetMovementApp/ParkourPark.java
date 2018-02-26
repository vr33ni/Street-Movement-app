package com.example.vreeni.StreetMovementApp;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by vreee on 17/02/2018.
 */

public class ParkourPark {


    private GeoPoint coordinates;
    private double lati;
    private double longi;
    private String name; // = title, if unnamed, name = address?
    private boolean parkour;
    private boolean calisthenics;
    private String source;
    private String description;

    private ArrayList<PhotoData> listOfPhotoData;

    private HashMap<String, Object> photo_0;
    private HashMap<String, Object> photo_1;
    private HashMap<String, Object> photo_2;
    private HashMap<String, Object> photo_3;
    private HashMap<String, Object> photo_4;
    private HashMap<String, Object> photo_5;


    public ParkourPark() {}


    public ArrayList<PhotoData> getListOfPhotoData() {
        return listOfPhotoData;
    }

    public void setListOfPhotoData(ArrayList<PhotoData> listOfPhotoData) {
        this.listOfPhotoData = listOfPhotoData;
    }


    public HashMap<String, Object> getPhoto_0() {
        return photo_0;
    }

    public void setPhoto_0(HashMap<String, Object> photo_0) {
        this.photo_0 = photo_0;
    }

    public HashMap<String, Object> getPhoto_1() {
        return photo_1;
    }

    public void setPhoto_1(HashMap<String, Object> photo_1) {
        this.photo_1 = photo_1;
    }

    public HashMap<String, Object> getPhoto_2() {
        return photo_2;
    }

    public void setPhoto_2(HashMap<String, Object> photo_2) {
        this.photo_2 = photo_2;
    }

    public HashMap<String, Object> getPhoto_3() {
        return photo_3;
    }

    public void setPhoto_3(HashMap<String, Object> photo_3) {
        this.photo_3 = photo_3;
    }

    public HashMap<String, Object> getPhoto_4() {
        return photo_4;
    }

    public void setPhoto_4(HashMap<String, Object> photo_4) {
        this.photo_4 = photo_4;
    }

    public HashMap<String, Object> getPhoto_5() {
        return photo_5;
    }

    public void setPhoto_5(HashMap<String, Object> photo_5) {
        this.photo_5 = photo_5;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(coordinates, source, lati, longi);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof ParkourPark)) {
            return false;
        }
        ParkourPark pp = (ParkourPark) o;
        return  lati == pp.lati &&
                longi == pp.longi &&
                coordinates.equals(pp.coordinates) &&
                source.equals(pp.source);

//                Objects.equals(coordinates, pp.coordinates) &&
//                Objects.equals(permalink, pp.permalink);
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public boolean isParkour() {
        return parkour;
    }

    public void setParkour(boolean parkour) {
        this.parkour = parkour;
    }

    public boolean isCalisthenics() {
        return calisthenics;
    }

    public void setCalisthenics(boolean calisthenics) {
        this.calisthenics = calisthenics;
    }


    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
