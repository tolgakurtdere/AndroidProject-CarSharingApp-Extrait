package com.tolgahankurtdere.extrait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class TicketActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    TextView nameSurnameText,idNoText,fromText,toText,dateText,timeText,peopleNumberText,breakNumberText,carModelText;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        this.setTitle("Ticket"); //set activity title

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        nameSurnameText = findViewById(R.id.textViewTicketNameSurname);
        idNoText = findViewById(R.id.textViewTicketIdNo);
        fromText = findViewById(R.id.textViewTicketFrom);
        toText = findViewById(R.id.textViewTicketTo);
        dateText = findViewById(R.id.textViewTicketDate);
        timeText = findViewById(R.id.textViewTicketTime);
        peopleNumberText = findViewById(R.id.textViewTicketPeople);
        breakNumberText = findViewById(R.id.textViewTicketBreak);
        carModelText = findViewById(R.id.textViewTicketCarModel);

        trip = getIntent().getParcelableExtra("tripData");
        setTexts();
    }

    private void setTexts(){

        String time;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(trip.getDepartTime().toDate().getTime());
        calendar.add(Calendar.HOUR_OF_DAY,3); //because of UTC+3

        if(calendar.get(Calendar.MINUTE) == 0) {
            time = calendar.get(Calendar.HOUR_OF_DAY) + ":00";
        }
        else{
            time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        }
        timeText.setText(time);

        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
        dateText.setText(date);


        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                nameSurnameText.setText(user.getName() + " " + user.getSurname());
                idNoText.setText(user.getId());
            }
        });

        fromText.setText(trip.getFrom());
        toText.setText(trip.getTo());
        peopleNumberText.setText(trip.getFullSeatNumber() + "/" + trip.getPeopleNumber() + "(" + trip.getDriverNumber() + ")");
        breakNumberText.setText(String.valueOf(trip.getBreakNumber()));
        carModelText.setText(trip.getCarModel());
    }

    public void showCarDetailsClicked(View view){
        Intent intent = new Intent(TicketActivity.this, CarDetailsActivity.class);
        intent.putExtra("tripData", trip);
        startActivity(intent);
    }
    public void QRClicked(View view){

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if(!user.isReady()){
                    user.setReady(true);
                    firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).set(user, SetOptions.merge()); //merge user data

                    firebaseFirestore.collection("Trips").document(trip.getTripID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Trip trip = documentSnapshot.toObject(Trip.class);
                            trip.setReadyUserNumber(trip.getReadyUserNumber() + 1);

                            if(trip.getFullSeatNumber() == trip.getReadyUserNumber() || trip.getDepartTime().toDate().compareTo(Calendar.getInstance().getTime()) < 0){ //if all users are ready or time is up
                                trip.setActive(true); //set trip active
                                //make users travelling now if user is enrolled this trip and ready
                                firebaseFirestore.collection("Users")
                                        .whereArrayContains("trips",trip.getTripID())
                                        .whereEqualTo("ready",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                            User user = queryDocumentSnapshot.toObject(User.class);
                                            user.setTravelingNow(true);
                                            firebaseFirestore.collection("Users").document(user.getEmail()).set(user, SetOptions.merge()); //merge user data
                                        }
                                    }
                                });
                            }
                            firebaseFirestore.collection("Trips").document(trip.getTripID()).set(trip, SetOptions.merge()); //merge trip data
                        }
                    });

                }
                else{
                    Toast.makeText(TicketActivity.this,"Your trip is done!",Toast.LENGTH_LONG).show();
                    user.setReady(false);
                    user.setTravelingNow(false);
                    firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).set(user, SetOptions.merge()); //merge user data

                    firebaseFirestore.collection("Trips").document(trip.getTripID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Trip trip = documentSnapshot.toObject(Trip.class);
                            trip.setActive(false); //set trip inactive
                            firebaseFirestore.collection("Trips").document(trip.getTripID()).set(trip, SetOptions.merge()); //merge trip data
                        }
                    });
                    //Intent intent = new Intent(TicketActivity.this,)
                }

            }
        });

    }

}