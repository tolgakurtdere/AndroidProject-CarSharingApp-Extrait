package com.tolgahankurtdere.extrait;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CarDetailsActivity extends AppCompatActivity {

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        this.setTitle("Car Details"); //set activity title

        trip = getIntent().getParcelableExtra("tripData");
        System.out.println(trip.getCarID());
    }
}