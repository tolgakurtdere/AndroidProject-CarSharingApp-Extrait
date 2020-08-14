package com.tolgahankurtdere.extrait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

}