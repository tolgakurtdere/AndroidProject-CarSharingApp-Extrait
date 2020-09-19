package com.tolgahankurtdere.extrait;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.Striped;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyTripsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    MyTripsRecyclerAdapter myTripsRecyclerAdapter;
    RecyclerView recyclerView;

    ArrayList<Trip> tripArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        this.setTitle("My Trips"); //set activity title

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewMyTrips);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tripArrayList = new ArrayList<>();

        myTripsRecyclerAdapter = new MyTripsRecyclerAdapter(MyTripsActivity.this, tripArrayList);
        recyclerView.setAdapter(myTripsRecyclerAdapter);

        getMyTripsFromFirestore();

        /* Bottom Navigation View Start */
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_view);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_myTrips);
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

    public void getMyTripsFromFirestore(){
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    final User user = documentSnapshot.toObject(User.class);
                    firebaseFirestore.collection("Trips").orderBy("departTime", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(!queryDocumentSnapshots.isEmpty()){
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot snapshot : list){
                                    if(user.getTrips().contains(snapshot.getId())){
                                        Trip trip = snapshot.toObject(Trip.class);
                                        tripArrayList.add(trip);
                                        myTripsRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }

                            }
                            else{
                                //System.out.println("There is no trip on fireStore");
                            }
                            if(tripArrayList.isEmpty()){ //if user do not have any trip
                                AlertDialog.Builder builder = new AlertDialog.Builder(MyTripsActivity.this);
                                builder.setMessage("You do not have any trip plan!");
                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        finish();
                                    }
                                });
                            }

                        }
                    });
                }
                else{
                    //System.out.println("ELSE");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyTripsActivity.this, e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
        /*firebaseFirestore.collection("Users").whereEqualTo("email",firebaseAuth.getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(MyTripsActivity.this, error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
                if(value != null){
                    for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                        User user = documentSnapshot.toObject(User.class);

                        userTrips = user.getTrips();

                    }
                }
            }
        });*/
    }

}
