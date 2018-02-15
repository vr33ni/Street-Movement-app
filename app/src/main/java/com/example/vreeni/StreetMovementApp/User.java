package com.example.vreeni.StreetMovementApp;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Created by vreee on 30/12/2017.
 */

//this class handles the creation of a user in the firebase Database
public class User { //change this to extends FIREBASEUser !!!

    //FIRESTORE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String name;
    private String email;
    private String nationality;
    private String nickname;
    private String status;
    private String age;

    private long workoutsCompleted;
    private long warmupsSkipped;
    private long warmupsCompleted;


    static final String AGE = "age";
    static final String EMAIL = "email";
    static final String FULLNAME = "name";
    static final String NATIONALITY = "nationality";
    static final String NICKNAME = "nickname";
    static final String STATUS = "status";
    static final String WORKOUTSCOMPLETED = "workoutsCompleted";
    static final String WARMUPSSKIPPED = "warmupsSkipped";
    static final String WARMUPSCOMPLETED = "warmupsSkipped";
    static final String LISTOFHOMEWORKOUTS = "listOfHomeWorkouts";
    static final String LISTOFOUTDOORWORKOUTS = "listOfOutdoorWorkouts";

    private ArrayList<Object> listOfHomeWorkouts = new ArrayList<>();
    private ArrayList<Object> listOfOutdoorWorkouts = new ArrayList<>();


    public User() {}

    public ArrayList<Object> getListOfHomeWorkouts() {
        return listOfHomeWorkouts;
    }

    public void setListOfHomeWorkouts(ArrayList<Object> listOfHomeWorkouts) {
        this.listOfHomeWorkouts = listOfHomeWorkouts;
    }

    public ArrayList<Object> getListOfOutdoorWorkouts() {
        return listOfOutdoorWorkouts;
    }

    public void setListOfOutdoorWorkouts(ArrayList<Object> listOfOutdoorWorkouts) {
        this.listOfOutdoorWorkouts = listOfOutdoorWorkouts;
    }

    //    protected void checkFireStoreDatabase() {
//        // Create a new user with a first and last name
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference usersDocRef = db.collection("Users").document(userFullName);
//
//        if (usersDocRef != null) {
//        } else {
//            createNewEntry();
//        }
//    }


    public long getWarmupsCompleted() {
        return warmupsCompleted;
    }

    public void setWarmupsCompleted(long warmupsCompleted) {
        this.warmupsCompleted = warmupsCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getWorkoutsCompleted() {
        return workoutsCompleted;
    }

    public void setWorkoutsCompleted(long workoutsCompleted) {
        this.workoutsCompleted = workoutsCompleted;
    }

    public long getWarmupsSkipped() {
        return warmupsSkipped;
    }

    public void setWarmupsSkipped(long warmupsSkipped) {
        this.warmupsSkipped = warmupsSkipped;
    }

    //    public void createNewEntry() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference usersDocRef = db.collection("Users").document(userFullName);
//        Map<String, Object> userEntry;
//
//        userEntry = new HashMap<>();
//        userEntry.put(FULLNAME, userFullName);
//        userEntry.put(EMAIL, userEmail);
//        userEntry.put(NICKNAME, "-");
//        userEntry.put(AGE, "-");
//        userEntry.put(NATIONALITY, "-");
//        userEntry.put(STATUS, "Baby monkey");
//        db.document(userFullName).set(userEntry, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "Document has been saved");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "Document could not be saved");
//            }
//        });
//    }

}


/*
    protected void updateRealTimeDatabase() {
        //whenever a new user object is created, it signifies the usage of the app by the current user
        //the creation of a user object also prompts the program to check if the current user already exists in the database
        //if not, create a new Database entry
        //OR: only create user object when it is actually a new user that will be stored in database? would be more logical
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersRef = mDatabase.getRef();
        if (usersRef != null) {
        } else {
            users = mDatabase.child("Users");
        }
        updateDatabaseEntry();
    }

    //CHECK IF USER EXISTS
    private void updateDatabaseEntry() {
        final DatabaseReference nameRef = usersRef.getRef();
        nameRef.addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.equals(getUsername())) {
                            //username already exists
                        } else {
                            //user doesnt exist yet
                            addNewUser();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }


    //method for saving user data in database if the entry doesnt exist yet

    private void addNewUser() {
        users = mDatabase.child("Users");
        if (getUserID() != null) {
            //create new child with the user name
            DatabaseReference newUser = users.child(getUsername());
            //create new child called user id and its value
            DatabaseReference userID = newUser.child("UserID");
            userID.setValue(getUserID());

            DatabaseReference displayname = newUser.child("User name");
            displayname.setValue(getUsername());

            DatabaseReference emailaddress = newUser.child(EMAIL);
            emailaddress.setValue(getEmail());

        }
    }

    */


