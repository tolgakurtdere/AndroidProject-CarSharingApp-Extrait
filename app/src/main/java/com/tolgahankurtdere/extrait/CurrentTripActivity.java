package com.tolgahankurtdere.extrait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CurrentTripActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    boolean isTravelingNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip);

        this.setTitle("Current Trip Data"); //set activity title

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                isTravelingNow = user.isTravelingNow();
                if(!user.isTravelingNow()){ //if user is not traveling now
                    AlertDialog.Builder builder = new AlertDialog.Builder(CurrentTripActivity.this);
                    builder.setMessage("You are not currently traveling, so you won't be able to use some of the features. ");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        /* Bottom Navigation View Start */
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_view);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_currentTripData);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(bottomNavigationView.getSelectedItemId() != item.getItemId()){ //if same item is not selected
                    switch (item.getItemId()){
                        case R.id.bottomNavigation_search:
                            Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.bottomNavigation_profile:
                            Intent intent2 = new Intent(getApplicationContext(),ProfileActivity.class);
                            startActivity(intent2);
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.bottomNavigation_myTrips:
                            Intent intent3 = new Intent(getApplicationContext(),MyTripsActivity.class);
                            startActivity(intent3);
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.bottomNavigation_currentTripData:
                            Intent intent4 = new Intent(getApplicationContext(),CurrentTripActivity.class);
                            startActivity(intent4);
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                    }
                }

                return false;
            }
        });
        /* Bottom Navigation View End */
    }

    public void showLocationClicked(View view){
        Intent intent = new Intent(CurrentTripActivity.this,CurrentLocationActivity.class);
        startActivity(intent);
    }

    public void panicButtonClicked(View view){
        System.out.println("Panic button clicked");
    }
}