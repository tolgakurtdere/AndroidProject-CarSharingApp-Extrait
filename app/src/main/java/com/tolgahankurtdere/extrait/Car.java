package com.tolgahankurtdere.extrait;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Car implements Parcelable {
    private String model, fuelType, location;
    private int year, km;
    private ArrayList<String> lastUsers;
    private boolean isAutomatic, hasLaneAssist, hasParkingGuidance, isAvailable = true;
    Timestamp lastUsedTime;

    public Car() {}

    public Car(String model, String fuelType, String location, int year, int km, boolean isAutomatic, boolean hasLaneAssist, boolean hasParkingGuidance) {
        this.model = model;
        this.fuelType = fuelType;
        this.location = location;
        this.year = year;
        this.km = km;
        this.isAutomatic = isAutomatic;
        this.hasLaneAssist = hasLaneAssist;
        this.hasParkingGuidance = hasParkingGuidance;
    }

    protected Car(Parcel in) {
        model = in.readString();
        fuelType = in.readString();
        location = in.readString();
        year = in.readInt();
        km = in.readInt();
        lastUsers = in.createStringArrayList();
        isAutomatic = in.readByte() != 0;
        hasLaneAssist = in.readByte() != 0;
        hasParkingGuidance = in.readByte() != 0;
        isAvailable = in.readByte() != 0;
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public ArrayList<String> getLastUsers() {
        return lastUsers;
    }

    public void setLastUsers(ArrayList<String> lastUsers) {
        this.lastUsers = lastUsers;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public void setAutomatic(boolean automatic) {
        isAutomatic = automatic;
    }

    public boolean isHasLaneAssist() {
        return hasLaneAssist;
    }

    public void setHasLaneAssist(boolean hasLaneAssist) {
        this.hasLaneAssist = hasLaneAssist;
    }

    public boolean isHasParkingGuidance() {
        return hasParkingGuidance;
    }

    public void setHasParkingGuidance(boolean hasParkingGuidance) {
        this.hasParkingGuidance = hasParkingGuidance;
    }

    public Timestamp getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(Timestamp lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(model);
        dest.writeString(fuelType);
        dest.writeString(location);
        dest.writeInt(year);
        dest.writeInt(km);
        dest.writeStringList(lastUsers);
        dest.writeByte((byte) (isAutomatic ? 1 : 0));
        dest.writeByte((byte) (hasLaneAssist ? 1 : 0));
        dest.writeByte((byte) (hasParkingGuidance ? 1 : 0));
        dest.writeByte((byte) (isAvailable ? 1 : 0));
    }
}
