package com.tolgahankurtdere.extrait;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
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
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView departDateText, fromText, toText;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String[] locations;
    int selectedItemFrom = -1, selectedItemTo = -1;
    Timestamp departTimestamp, departTimestampEnd;

    int chosenYear,chosenMonth,chosenDay;

    ArrayList<String> tripIDs;
    ArrayList<Trip> tripArrayList;

    MainRecyclerAdapter mainRecyclerAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        departDateText = findViewById(R.id.textViewDepartDate);
        fromText = findViewById(R.id.textViewFrom);
        toText = findViewById(R.id.textViewTo);

        locations = new String[]{"Istanbul","Ankara","Izmir","Bursa","Antalya","Aydin","Mugla","Sivas","Samsun","Adana","Mersin","Konya","Artvin","Kastamonu","Balikesir"};

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,3); //UTC+3
        chosenYear = calendar.get(Calendar.YEAR);
        chosenMonth = calendar.get(Calendar.MONTH);
        chosenDay = calendar.get(Calendar.DAY_OF_MONTH);

        tripIDs = new ArrayList<>();
        tripArrayList = new ArrayList<>();

        /* PopUp Calendar Start */
        departDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar calendar = Calendar.getInstance();
                int year = chosenYear; //calendar will start with this date
                int month = chosenMonth;
                int day = chosenDay;
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,R.style.ThemeOverlay_AppCompat_Dialog,dateSetListener,year,month,day);
                //datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000 + 10800000); //disable old dates
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
                departDateText.setText(date);
                chosenYear = year;
                chosenMonth = month;
                chosenDay = dayOfMonth;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth-1);

                calendar.set(Calendar.HOUR_OF_DAY,20); //because Turkey use GMT+03:00
                calendar.set(Calendar.MINUTE,59);
                calendar.set(Calendar.SECOND,59);
                calendar.set(Calendar.MILLISECOND,0);
                departTimestamp = new Timestamp(calendar.getTimeInMillis()/1000 , 0);
                calendar.add(Calendar.DAY_OF_MONTH,1);
                calendar.set(Calendar.HOUR_OF_DAY,20); //because Turkey use GMT+03:00
                calendar.set(Calendar.MINUTE,59);
                calendar.set(Calendar.SECOND,59);
                calendar.set(Calendar.MILLISECOND,0);
                departTimestampEnd = new Timestamp(calendar.getTimeInMillis()/1000,0);
                //System.out.println("depart: " + departTimestamp.toString());
                //System.out.println("departEnd: " + departTimestampEnd.toString());
            }
        };
        /* PopUp Calendar End */

        /* Alert Dialog Single Choice Start */
        fromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose depart location");
                //builder.setIcon()
                builder.setSingleChoiceItems(locations, selectedItemFrom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemFrom = which;
                        fromText.setText(locations[selectedItemFrom]);
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        /* Alert Dialog Single Choice End */

        /* Alert Dialog Single Choice Start */
        toText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose arrival location");
                //builder.setIcon()
                builder.setSingleChoiceItems(locations, selectedItemTo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemTo = which;
                        toText.setText(locations[selectedItemTo]);
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        /* Alert Dialog Single Choice End */

        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* Bottom Navigation View Start */
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation_view);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigation_search);
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

    public void searchClicked(View view){
        /*tripIDs.clear();
        tripArrayList.clear();*/

        getSuitableTrips();
        mainRecyclerAdapter = new MainRecyclerAdapter(tripArrayList,tripIDs);
        recyclerView.setAdapter(mainRecyclerAdapter);
    }

    public void createTripClicked(View view){
        Intent intent = new Intent(MainActivity.this,CreateTripActivity.class);
        intent.putExtra("fromText", fromText.getText().toString());
        intent.putExtra("toText", toText.getText().toString());
        intent.putExtra("departDateText", departDateText.getText().toString());
        intent.putExtra("locations", locations);
        intent.putExtra("chosenYear",chosenYear);
        intent.putExtra("chosenMonth",chosenMonth);
        intent.putExtra("chosenDay",chosenDay);
        intent.putExtra("selectedItemFrom",selectedItemFrom);
        intent.putExtra("selectedItemTo",selectedItemTo);
        intent.putExtra("departTimestamp",departTimestamp);
        startActivity(intent);
    }

    private void getSuitableTrips(){
        CollectionReference collectionReference = (CollectionReference) firebaseFirestore.collection("Trips");
        collectionReference.whereEqualTo("from",fromText.getText().toString())
                .whereEqualTo("to",toText.getText().toString())
                .whereGreaterThan("departTime",departTimestamp)
                .whereLessThan("departTime",departTimestampEnd)
                .orderBy("departTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MainActivity.this, error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(value != null){
                            tripArrayList.clear(); //clear ArrayList before the getting data
                            tripIDs.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                Trip trip = doc.toObject(Trip.class); //get Trip Data from fireStore as a Trip Object
                                tripIDs.add(doc.getId());
                                tripArrayList.add(trip);

                                mainRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        List<DocumentSnapshot> document = value.getDocuments();
                        if(document.isEmpty()){
                            //if there is no result
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("There is no suitable trip. You should try different date or create a trip!");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.preferences){
            Intent intent = new Intent(MainActivity.this,PreferencesActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.signOut){
            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}