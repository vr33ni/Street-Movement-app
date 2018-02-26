package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vreee on 17/02/2018.
 */

public class JasonHandler {
    private static final String LOG_TAG = "JsonParser";

    private Context context = getApplicationContext();

    private GeoPoint gp;

    private List<ParkourPark> listOfParkLocations;

    private List<HashMap> listOfPhotos;


    public JasonHandler() {

    }


    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = context.getAssets().open("gadeidraetPins.json");
            Log.d(LOG_TAG, "found json file");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void retrieveFileFromResource() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject arr = obj.getJSONObject("array");
            JSONObject obj1 = arr.getJSONObject("data");
            JSONArray arr2 = obj1.getJSONArray("pins");

            listOfParkLocations = new ArrayList<>();


            //loop through the array of pin locations
            for (int i = 0; i < arr2.length(); i++) {
                JSONObject pin = arr2.getJSONObject(i);
                JSONArray pinCats = pin.getJSONArray("pin_categories");


                //loop through the array of pin categories that are listed within each pin location
                for (int j = 0; j < pinCats.length(); j++) {
                    //if parkour || calisthenics = true
                    if ((pinCats.getString(j).equals("3")) || (pinCats.getString(j).equals("13"))) {
                        ParkourPark parkLoc = new ParkourPark();
                        JSONArray photos = pin.getJSONArray("photos");

                        /*
                          set the category for the park
                        */
                        //if category = 3, set parkLoc category parkour true
                        if (pinCats.getString(j).equals("3")) {
                            parkLoc.setParkour(true);
                        }
                        //if category = 13, set parkLoc category calisthenics true
                        if (pinCats.getString(j).equals("13")) {
                            parkLoc.setCalisthenics(true);
                        }

                        //get and set photos for all the parks that offer pk or calisthenics
                        String url;
                        String fileName;
                        String id;

                        ArrayList<PhotoData> spotPhotos = new ArrayList<>();

                        Log.d(LOG_TAG, "list of photos " + photos.length());

                        //loop through the json array photos
                        for (int k = 0; k < photos.length(); k++) {
//                            //create photo object for each object in the json array "photos"
                            JSONObject individualPhoto = photos.getJSONObject(k);
//                            //create new photoData object for each object of the array
                            PhotoData photo = new PhotoData();
                            url = individualPhoto.getString("url");
                            fileName = individualPhoto.getString("filename");
                            id = individualPhoto.getString("id");
                            photo.setPhotoURL(url);
                            photo.setId(id);
                            photo.setFileName(fileName);
                            spotPhotos.add(photo);

                            Log.d(LOG_TAG, "list of spot photos " + spotPhotos);
                        }

                        parkLoc.setListOfPhotoData(spotPhotos);
//


                        /*
                          get and set coordinates for parkour and calisthenics locations
                        */
                        //get latitude and longitude
                        String lat = pin.getString("latitude");
                        String lon = pin.getString("longitude");


                        //validate format of latitude and longitude: Hardcode the string that is in the wrong format
                        double dlat;
                        double dlon;
                        if (lat.equals("55.474.649")) {
                            String validLat = lat.replace("55.474.649", "55.474649");
                            //parse validated latitude and longitude string values to doubles
                            dlat = Double.parseDouble(validLat);
                            dlon = Double.parseDouble(lon);
                        } else {
                            //parse validated latitude and longitude string values to doubles
                            dlat = Double.parseDouble(lat);
                            dlon = Double.parseDouble(lon);
                        }
                        //set coordinates in the parkLoc object
                        parkLoc.setCoordinates(calculateGeoPoint(dlat, dlon));
                        parkLoc.setLati(dlat);
                        parkLoc.setLongi(dlon);

                        //set source
                        String link = pin.getString("permalink");
                        parkLoc.setSource(pin.getString("permalink"));


                        //set name/title
                        if (pin.getString("title") != null) {
                            parkLoc.setName(pin.getString("title"));
                        } else {
                            parkLoc.setName(pin.getString("address"));
                        }

                        //set Description
                        String description = pin.getString("description");
                        parkLoc.setDescription(formatDescription(description));


                        //set photos to use as snippets


                        if (listOfParkLocations.contains(parkLoc)) {
                            //dont add duplicates
                        } else {
                            listOfParkLocations.add(parkLoc);
                        }


//                        Log.d(LOG_TAG, "lati: " + lat + " longi: " + lon + " nrofparks: " + listOfParkLocations.size());

                    } else {
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateFirestore() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (ParkourPark pkPark : listOfParkLocations) {
            DocumentReference pkRef = db.collection("ParkourParks").document(pkPark.getName());

            HashMap<String, Object> data = new HashMap<>();

/*
   if wana change anything in this terms, close the following loop before the data.update
*/
//                data.put("name", pkPark.getName());
//                data.put("parkour", pkPark.isParkour());
//                data.put("calisthenics", pkPark.isCalisthenics());
//                data.put("coordinates", pkPark.getCoordinates());
                data.put("source", pkPark.getSource());
                data.put("description (in Danish)", pkPark.getDescription());


//            if (pkPark.getListOfPhotoData() != null) {
//                for (int z = 0; z < pkPark.getListOfPhotoData().size(); z++) {
//                    HashMap<String, Object> nestedNestedData = new HashMap<>();
//                    nestedNestedData.put("url", pkPark.getListOfPhotoData().get(z).getPhotoURL());
//                    nestedNestedData.put("id", pkPark.getListOfPhotoData().get(z).getId());
//                    nestedNestedData.put("filename", pkPark.getListOfPhotoData().get(z).getFileName());
//                    String name = "photo_" + z;
//                    data.put(name, nestedNestedData);


            pkRef
                    .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(LOG_TAG, "New Document has been saved: " + pkRef.getId());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(LOG_TAG, "New Document could not be saved");
                }
            });
//        }
//    }
}

    }


    public static GeoPoint calculateGeoPoint(double latitude, double longitude) {
        return new GeoPoint(latitude, longitude);
    }

    public String formatDescription(String description) {
        //formatting the description strings for irregularities
        String item1 = "<p class=\"p1\">";
        String item2 = "</a>";
        String item3 = "</p>";
        String item4 = "<a target=";
        if (description.contains(item1)) {
            description = description.replace(item1, "");
        }
        if (description.contains(item2)) {
            description = description.replace(item2, "");
        }
        if (description.contains(item3)) {
            description = description.replace(item3, "");
        }
        if (description.contains(item4)) {
            description = description.split(item4)[0];
        }
        return description;
    }

}
