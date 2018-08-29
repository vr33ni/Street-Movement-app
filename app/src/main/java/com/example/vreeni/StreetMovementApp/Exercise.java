package com.example.vreeni.StreetMovementApp;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private String exerciseID;
    private String name;
    private String description;
    private String category;
    private String image;
    private String video;
    private String repetitions;
    private int xp;
    private int sets;
    private boolean workout;
    private boolean movementSpecificChallenge;
    private boolean streetMovementChallenge;
    private boolean singlePlayer;
    private boolean multiPlayer;


    public Exercise() {
    }

    public String getExerciseID() {
        return exerciseID;
    }

    public boolean isWorkout() {
        return workout;
    }

    public void setWorkout(boolean workout) {
        this.workout = workout;
    }

    public boolean isMovementSpecificChallenge() {
        return movementSpecificChallenge;
    }

    public void setMovementSpecificChallenge(boolean movementSpecificChallenge) {
        this.movementSpecificChallenge = movementSpecificChallenge;
    }

    public boolean isStreetMovementChallenge() {
        return streetMovementChallenge;
    }

    public void setStreetMovementChallenge(boolean streetMovementChallenge) {
        this.streetMovementChallenge = streetMovementChallenge;
    }

    public boolean isSinglePlayer() {
        return singlePlayer;
    }

    public void setSinglePlayer(boolean singlePlayer) {
        this.singlePlayer = singlePlayer;
    }

    public boolean isMultiPlayer() {
        return multiPlayer;
    }

    public void setMultiPlayer(boolean multiPlayer) {
        this.multiPlayer = multiPlayer;
    }

    public void setExerciseID(String exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(String reptitions) {
        this.repetitions = reptitions;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }


    protected Exercise(Parcel in) {
        exerciseID = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        image = in.readString();
        video = in.readString();
        repetitions = in.readString();
        xp = in.readInt();
        sets = in.readInt();
        workout = in.readByte() != 0x00;
        movementSpecificChallenge = in.readByte() != 0x00;
        streetMovementChallenge = in.readByte() != 0x00;
        singlePlayer = in.readByte() != 0x00;
        multiPlayer = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exerciseID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeString(image);
        dest.writeString(video);
        dest.writeString(repetitions);
        dest.writeInt(xp);
        dest.writeInt(sets);
        dest.writeByte((byte) (workout ? 0x01 : 0x00));
        dest.writeByte((byte) (movementSpecificChallenge ? 0x01 : 0x00));
        dest.writeByte((byte) (streetMovementChallenge ? 0x01 : 0x00));
        dest.writeByte((byte) (singlePlayer ? 0x01 : 0x00));
        dest.writeByte((byte) (multiPlayer ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}