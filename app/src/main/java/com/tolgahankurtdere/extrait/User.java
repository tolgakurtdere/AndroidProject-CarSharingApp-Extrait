package com.tolgahankurtdere.extrait;

import java.util.ArrayList;

public class User {
    private String id,name,surname,email;
    private boolean driverLicense,sleep,smoke;
    private ArrayList<Boolean> musics, topics;
    private ArrayList<String> trips = new ArrayList<>();

    public User(){}

    public User(String id, String name, String surname, String email, boolean driverLicense) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.driverLicense = driverLicense;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(boolean driverLicense) {
        this.driverLicense = driverLicense;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }

    public ArrayList<Boolean> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Boolean> musics) {
        this.musics = musics;
    }

    public ArrayList<Boolean> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Boolean> topics) {
        this.topics = topics;
    }

    public ArrayList<String> getTrips() {
        return trips;
    }

    public void setTrips(ArrayList<String> trips) {
        this.trips = trips;
    }

    public void addTriptoUser(String tripID){
        trips.add(tripID);
    }
}
