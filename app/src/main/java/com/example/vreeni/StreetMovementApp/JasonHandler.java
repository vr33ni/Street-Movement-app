package com.example.vreeni.StreetMovementApp;

import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by vreeni on 17/02/2018.
 */

/**
 * Class handling the reading of JSON files and the writing of the desired data to the database
 */
public class JasonHandler {
    private static final String LOG_TAG = "JsonParser";

    private Context context = getApplicationContext();

    private GeoPoint gp;
    private List<ParkourPark> listOfParkLocations;
    private List<HashMap> listOfPhotos;


    public JasonHandler() {}


    /**
     * JSON file is saved in the assets folder of the application and read into a String
     * @return file content is returned as a String
     */
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


    /**
     * handling the extraction of the desired information from the file
     * file consists of JSONObjects and JSONArrays that can both be understood and accessed as key value pairs
     * try:
     * 1. desired data (= parkour park locations) is part of the array "pins"
     * 2. desired data to be saved in a list of type ParkourPark
     * 3. looping through the array of pins and creating of a new JSONObject for each pin as well as a new JSONArray for each pin containing the pin categories on which this pin can be categorized based on
     * 4. looping through the array of pinCats and creating a new ParkourPark object whenever the categories for the respective pin object matches either "parkour" or "calisthenics" => set boolean values of ParkourPark objects for Parkour / Calisthenics
     * 5. creating JSONArray "photos" for each pin and a new list of type PhotoData to store the information of each photo in a list of photo objects for each pin
     * 6. get information on latitude and longitude ("latitude", "longitude") from the JSON String; format values (one value contains 2 commas) before parsing Strings to doubles; set double values for the ParkourPark object
     * 7. get information on source ("permalink") and set value for the ParkourPark object
     * 8. get information on name/title ("title", if null: "address") and set value for ParkourPark object
     * 9. get information on description ("description"9 and set value for ParkourPark object
     * 10. add ParkourPark object with newly set fields to the list of ParkourPark objects, if list doesnt contain this object already (.contains based on overriden .equals and hashcode methods in ParkourPark class)
     * catch: exceptions
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void retrieveFileFromResource() {
        //1.
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject arr = obj.getJSONObject("array");
            JSONObject obj1 = arr.getJSONObject("data");
            JSONArray arr2 = obj1.getJSONArray("pins");

            //2.
            listOfParkLocations = new ArrayList<>();


            //3. loop through the array of pin locations
            for (int i = 0; i < arr2.length(); i++) {
                JSONObject pin = arr2.getJSONObject(i);
                JSONArray pinCats = pin.getJSONArray("pin_categories");


                //4. loop through the array of pin categories that are listed within each pin location
                for (int j = 0; j < pinCats.length(); j++) {
                    //if parkour || calisthenics = true
                    if ((pinCats.getString(j).equals("3")) || (pinCats.getString(j).equals("13"))) {
                        ParkourPark parkLoc = new ParkourPark();

                        //if category = 3, set parkLoc category parkour true
                        if (pinCats.getString(j).equals("3")) {
                            parkLoc.setParkour(true);
                        }
                        //if category = 13, set parkLoc category calisthenics true
                        if (pinCats.getString(j).equals("13")) {
                            parkLoc.setCalisthenics(true);
                        }

                        //5.
                        JSONArray photos = pin.getJSONArray("photos");
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
                        }
//                        parkLoc.setListOfPhotoData(spotPhotos);
                        Log.d(LOG_TAG, "list of spot photos " + parkLoc.getListOfPhotoData());


                        /*
                         6. get and set coordinates for parkour and calisthenics locations
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
//                        parkLoc.setCoordinates(calculateGeoPoint(dlat, dlon));
                        parkLoc.setLati(dlat);
                        parkLoc.setLongi(dlon);

//                        7.
                        //set source
                        parkLoc.setSource(pin.getString("permalink"));


//                        8.
                        //set name/title
                        if (pin.getString("title") != null) {
                            parkLoc.setName(pin.getString("title"));
                        } else {
                            parkLoc.setName(pin.getString("address"));
                        }

//                        9.
                        //set Description
                        String description = pin.getString("description");
                        parkLoc.setDescription(formatDescription(description));


                        //set photos to use as snippets


//                        10.
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

    /**
     * updates to firestore database always require a reference to the database and a hashmap containing the key value pairs that are supposed to be written to the database
     * db = Reference to database
     * pkRef = DocumentReference created for each ParkourPark object in the list of park locations (if the reference doesnt exist already, it will be created based on the name of the park)
     * creating HashMap and adding key value pairs (key names should be the same as the fields of the object for easier later retrieval)
     * => here, information is added as String-String, String-Geopoint or String-Object
     * update Documents and Document fields in database based on data in hashmap
     */
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

    /**
     * formatting the description strings by removing unwanted characters
     * @param description unformatted input String with potentially unwanted characters
     * @return formatted String
     */
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
