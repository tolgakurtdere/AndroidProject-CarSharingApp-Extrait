package com.tolgahankurtdere.extrait;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Trip implements Parcelable {
    private String from,to,carModel,carID,tripID;
    private int driverNumber,peopleNumber,fullSeatNumber = 1,breakNumber,readyUserNumber = 0;
    private Timestamp departTime;
    private boolean canDrive,isActive = false;
    private ArrayList<String> users = new ArrayList<>();

    public Trip(){}

    public Trip(String from, String to, String carModel, boolean canDrive, int peopleNumber, int breakNumber, Timestamp departTime) {
        this.from = from;
        this.to = to;
        this.carModel = carModel;
        this.canDrive = canDrive;
        this.peopleNumber = peopleNumber;
        this.breakNumber = breakNumber;
        this.departTime = departTime;

        if(canDrive) driverNumber = 1;
        else driverNumber = 0;
    }


    protected Trip(Parcel in) {
        from = in.readString();
        to = in.readString();
        carModel = in.readString();
        carID = in.readString();
        tripID = in.readString();
        driverNumber = in.readInt();
        peopleNumber = in.readInt();
        fullSeatNumber = in.readInt();
        breakNumber = in.readInt();
        readyUserNumber = in.readInt();
        departTime = in.readParcelable(Timestamp.class.getClassLoader());
        canDrive = in.readByte() != 0;
        isActive = in.readByte() != 0;
        users = in.createStringArrayList();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(int driverNumber) {
        this.driverNumber = driverNumber;
    }

    public int getPeopleNumber() {
        return peopleNumber;
    }

    public void setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public int getFullSeatNumber() {
        return fullSeatNumber;
    }

    public void setFullSeatNumber(int fullSeatNumber) {
        this.fullSeatNumber = fullSeatNumber;
    }

    public int getBreakNumber() {
        return breakNumber;
    }

    public void setBreakNumber(int breakNumber) {
        this.breakNumber = breakNumber;
    }

    public Timestamp getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Timestamp departTime) {
        this.departTime = departTime;
    }

    public boolean isCanDrive() {
        return canDrive;
    }

    public void setCanDrive(boolean canDrive) {
        this.canDrive = canDrive;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public int getReadyUserNumber() {
        return readyUserNumber;
    }

    public void setReadyUserNumber(int readyUserNumber) {
        this.readyUserNumber = readyUserNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void addUsertoTrip(String userID){
        users.add(userID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(carModel);
        dest.writeString(carID);
        dest.writeString(tripID);
        dest.writeInt(driverNumber);
        dest.writeInt(peopleNumber);
        dest.writeInt(fullSeatNumber);
        dest.writeInt(breakNumber);
        dest.writeInt(readyUserNumber);
        dest.writeParcelable(departTime, flags);
        dest.writeByte((byte) (canDrive ? 1 : 0));
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeStringList(users);
    }
}
