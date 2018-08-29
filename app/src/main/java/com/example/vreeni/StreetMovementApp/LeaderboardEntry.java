package com.example.vreeni.StreetMovementApp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

/**
 * class storing all information regarding one leaderboard entry
 * corresponding to a single "Scores" document on the database
 * parcelable so after querying a document from the "Scores" collection, this document can be converted into a leaderboardEntry object
 */
public class LeaderboardEntry implements Parcelable {
    private int week;
    private String username;

    private DocumentReference userReference;

    private int nrOfActivities_total;
    private int nrOfActivities_weekly;

    private int nrOfWorkouts_total;
    private int nrOfWorkouts_weekly;

    private int nrOfMovementSpecificChallenges_total;
    private int nrOfMovementSpecificChallenges_weekly;

    private int nrOfStreetMovementChallenges_total;
    private int nrOfStreetMovementChallenges_weekly;

    private int nrOfPlaces_total;
    private int nrOfPlaces_weekly;

    private ArrayList<Object> listOfPlaces_total;
    private ArrayList<Object> listOfPlaces_weekly;


    public LeaderboardEntry() {
//        nrOfWorkouts_total = wk_total;
//        nrOfWorkouts_weekly = wk_weekly;
//
//        nrOfMovementSpecificChallenges_total = muv_total;
//        nrOfMovementSpecificChallenges_weekly = muv_weekly;
//
//        nrOfStreetMovementChallenges_total = sm_total;
//        nrOfStreetMovementChallenges_weekly = sm_weekly;
//
//        nrOfActivities_total = nrOfWorkouts_total + nrOfStreetMovementChallenges_total + nrOfStreetMovementChallenges_total;
//        nrOfActivities_weekly = nrOfWorkouts_weekly + nrOfMovementSpecificChallenges_weekly + nrOfStreetMovementChallenges_weekly;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        this.userReference = userReference;
    }

    public ArrayList<Object> getListOfPlaces_total() {
        return listOfPlaces_total;
    }

    public void setListOfPlaces_total(ArrayList<Object> listOfPlaces_total) {
        this.listOfPlaces_total = listOfPlaces_total;
    }

    public ArrayList<Object> getListOfPlaces_weekly() {
        return listOfPlaces_weekly;
    }

    public void setListOfPlaces_weekly(ArrayList<Object> listOfPlaces_weekly) {
        this.listOfPlaces_weekly = listOfPlaces_weekly;
    }

    public int getNrOfPlaces_total() {
        return nrOfPlaces_total;
    }

    public void setNrOfPlaces_total(int nrOfPlaces_total) {
        this.nrOfPlaces_total = nrOfPlaces_total;
    }

    public int getNrOfPlaces_weekly() {
        return nrOfPlaces_weekly;
    }

    public void setNrOfPlaces_weekly(int nrOfPlaces_weekly) {
        this.nrOfPlaces_weekly = nrOfPlaces_weekly;
    }

    public int getNrOfActivities_total() {
        return nrOfActivities_total;
    }

    public void setNrOfActivities_total(int nrOfActivities_total) {
        this.nrOfActivities_total = nrOfActivities_total;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getNrOfActivities_weekly() {
        return nrOfActivities_weekly;
    }

    public void setNrOfActivities_weekly(int nrOfActivities_weekly) {
        this.nrOfActivities_weekly = nrOfActivities_weekly;
    }

    public int getNrOfWorkouts_total() {
        return nrOfWorkouts_total;
    }

    public void setNrOfWorkouts_total(int nrOfWorkouts_total) {
        this.nrOfWorkouts_total = nrOfWorkouts_total;
    }

    public int getNrOfWorkouts_weekly() {
        return nrOfWorkouts_weekly;
    }

    public void setNrOfWorkouts_weekly(int nrOfWorkouts_weekly) {
        this.nrOfWorkouts_weekly = nrOfWorkouts_weekly;
    }

    public int getNrOfMovementSpecificChallenges_total() {
        return nrOfMovementSpecificChallenges_total;
    }

    public void setNrOfMovementSpecificChallenges_total(int nrOfMovementSpecificChallenges_total) {
        this.nrOfMovementSpecificChallenges_total = nrOfMovementSpecificChallenges_total;
    }

    public int getNrOfMovementSpecificChallenges_weekly() {
        return nrOfMovementSpecificChallenges_weekly;
    }

    public void setNrOfMovementSpecificChallenges_weekly(int nrOfMovementSpecificChallenges_weekly) {
        this.nrOfMovementSpecificChallenges_weekly = nrOfMovementSpecificChallenges_weekly;
    }

    public int getNrOfStreetMovementChallenges_total() {
        return nrOfStreetMovementChallenges_total;
    }

    public void setNrOfStreetMovementChallenges_total(int nrOfStreetMovementChallenges_total) {
        this.nrOfStreetMovementChallenges_total = nrOfStreetMovementChallenges_total;
    }

    public int getNrOfStreetMovementChallenges_weekly() {
        return nrOfStreetMovementChallenges_weekly;
    }

    public void setNrOfStreetMovementChallenges_weekly(int nrOfStreetMovementChallenges_weekly) {
        this.nrOfStreetMovementChallenges_weekly = nrOfStreetMovementChallenges_weekly;
    }


    protected LeaderboardEntry(Parcel in) {
        week = in.readInt();
        username = in.readString();
//        userReference = in.readString();
        nrOfActivities_total = in.readInt();
        nrOfActivities_weekly = in.readInt();
        nrOfWorkouts_total = in.readInt();
        nrOfWorkouts_weekly = in.readInt();
        nrOfMovementSpecificChallenges_total = in.readInt();
        nrOfMovementSpecificChallenges_weekly = in.readInt();
        nrOfStreetMovementChallenges_total = in.readInt();
        nrOfStreetMovementChallenges_weekly = in.readInt();
        nrOfPlaces_total = in.readInt();
        nrOfPlaces_weekly = in.readInt();
        if (in.readByte() == 0x01) {
            listOfPlaces_total = new ArrayList<Object>();
            in.readList(listOfPlaces_total, Object.class.getClassLoader());
        } else {
            listOfPlaces_total = null;
        }
        if (in.readByte() == 0x01) {
            listOfPlaces_weekly = new ArrayList<Object>();
            in.readList(listOfPlaces_weekly, Object.class.getClassLoader());
        } else {
            listOfPlaces_weekly = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(week);
        dest.writeString(username);
//        dest.writeDoc(userReference);
        dest.writeInt(nrOfActivities_total);
        dest.writeInt(nrOfActivities_weekly);
        dest.writeInt(nrOfWorkouts_total);
        dest.writeInt(nrOfWorkouts_weekly);
        dest.writeInt(nrOfMovementSpecificChallenges_total);
        dest.writeInt(nrOfMovementSpecificChallenges_weekly);
        dest.writeInt(nrOfStreetMovementChallenges_total);
        dest.writeInt(nrOfStreetMovementChallenges_weekly);
        dest.writeInt(nrOfPlaces_total);
        dest.writeInt(nrOfPlaces_weekly);
        if (listOfPlaces_total == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listOfPlaces_total);
        }
        if (listOfPlaces_weekly == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(listOfPlaces_weekly);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LeaderboardEntry> CREATOR = new Parcelable.Creator<LeaderboardEntry>() {
        @Override
        public LeaderboardEntry createFromParcel(Parcel in) {
            return new LeaderboardEntry(in);
        }

        @Override
        public LeaderboardEntry[] newArray(int size) {
            return new LeaderboardEntry[size];
        }
    };
}
