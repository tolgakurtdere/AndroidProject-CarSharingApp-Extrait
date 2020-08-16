package com.tolgahankurtdere.extrait;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CarDetailsActivity extends AppCompatActivity {

    Trip trip;

    FirebaseFirestore firebaseFirestore;

    TextView modelText,yearText,kmText,fuelTypeText,locationText,lastUsedTimeText;
    CheckBox automaticBox,laneAssistBox,parkingGuidanceBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        this.setTitle("Car Details"); //set activity title

        firebaseFirestore = FirebaseFirestore.getInstance();

        modelText = findViewById(R.id.textViewCarDetailsModel);
        yearText = findViewById(R.id.textViewCarDetailsYear);
        kmText = findViewById(R.id.textViewCarDetailsKm);
        fuelTypeText = findViewById(R.id.textViewCarDetailsFuelType);
        locationText = findViewById(R.id.textViewCarDetailsLocation);
        lastUsedTimeText = findViewById(R.id.textViewCarDetailsLastUsedTime);
        automaticBox = findViewById(R.id.checkBoxCarDetailsAutomatic);
        laneAssistBox = findViewById(R.id.checkBoxCarDetailsLaneAssist);
        parkingGuidanceBox = findViewById(R.id.checkBoxCarDetailsParkingGuidance);

        trip = getIntent().getParcelableExtra("tripData");

        firebaseFirestore.collection("Cars").document(trip.getCarID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Car car = documentSnapshot.toObject(Car.class);
                modelText.setText("Model : " + car.getModel());
                yearText.setText("Year : " + car.getYear());
                kmText.setText("Km : " + car.getKm());
                fuelTypeText.setText("Fuel Type : " + car.getFuelType());
                locationText.setText("Location : " + car.getLocation());
                lastUsedTimeText.setText("Last Used Time : " + car.getLastUsedTime());
                automaticBox.setChecked(car.isAutomatic());
                laneAssistBox.setChecked(car.isHasLaneAssist());
                parkingGuidanceBox.setChecked(car.isHasParkingGuidance());
            }
        });
    }
}