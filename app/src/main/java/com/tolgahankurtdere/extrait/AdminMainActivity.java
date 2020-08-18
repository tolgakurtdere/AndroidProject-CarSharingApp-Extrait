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

public class AdminMainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView startDateText, endDateText;
    private DatePickerDialog.OnDateSetListener dateSetListener1, dateSetListener2;
    Timestamp startTimestamp, endTimestamp;

    int chosenYear1,chosenMonth1,chosenDay1;
    int chosenYear2,chosenMonth2,chosenDay2;

    ArrayList<Trip> tripArrayList;

    AdminMainRecyclerAdapter adminMainRecyclerAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        startDateText = findViewById(R.id.textViewAdminStartDate);
        endDateText = findViewById(R.id.textViewAdminEndDate);

        Calendar calendar = Calendar.getInstance();
        chosenDay1 = calendar.get(Calendar.DAY_OF_MONTH);
        chosenDay2 = calendar.get(Calendar.DAY_OF_MONTH);
        chosenMonth1 = calendar.get(Calendar.MONTH);
        chosenMonth2 = calendar.get(Calendar.MONTH);
        chosenYear1 = calendar.get(Calendar.YEAR);
        chosenYear2 = calendar.get(Calendar.YEAR);

        tripArrayList = new ArrayList<>();

        /* PopUp Calendar Start */
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar calendar = Calendar.getInstance();
                int year = chosenYear1; //calendar will start with this date
                int month = chosenMonth1;
                int day = chosenDay1;
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminMainActivity.this,R.style.ThemeOverlay_AppCompat_Dialog,dateSetListener1,year,month,day);
                datePickerDialog.show();
            }
        });

        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
                startDateText.setText(date);
                chosenYear1 = year;
                chosenMonth1 = month;
                chosenDay1 = dayOfMonth;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth-1);

                calendar.set(Calendar.HOUR_OF_DAY,20); //because Turkey use GMT+03:00
                calendar.set(Calendar.MINUTE,59);
                calendar.set(Calendar.SECOND,59);
                calendar.set(Calendar.MILLISECOND,0);
                startTimestamp = new Timestamp(calendar.getTimeInMillis()/1000 , 0);
                //System.out.println(startTimestamp);
            }
        };
        /* PopUp Calendar End */

        /* PopUp Calendar Start */
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar calendar = Calendar.getInstance();
                int year = chosenYear2; //calendar will start with this date
                int month = chosenMonth2;
                int day = chosenDay2;
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminMainActivity.this,R.style.ThemeOverlay_AppCompat_Dialog,dateSetListener2,year,month,day);
                datePickerDialog.show();
            }
        });

        dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month+1) + "/" + year;
                endDateText.setText(date);
                chosenYear2 = year;
                chosenMonth2 = month;
                chosenDay2 = dayOfMonth;

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,dayOfMonth-1);

                calendar.set(Calendar.HOUR_OF_DAY,20); //because Turkey use GMT+03:00
                calendar.set(Calendar.MINUTE,59);
                calendar.set(Calendar.SECOND,59);
                calendar.set(Calendar.MILLISECOND,0);
                endTimestamp = new Timestamp(calendar.getTimeInMillis()/1000 , 0);
                //System.out.println(endTimestamp);
            }
        };
        /* PopUp Calendar End */

        recyclerView = findViewById(R.id.recyclerViewAdminMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void GetTripsClicked(View view){
        getSuitableTrips();
        adminMainRecyclerAdapter = new AdminMainRecyclerAdapter(tripArrayList);
        recyclerView.setAdapter(adminMainRecyclerAdapter);
    }

    private void getSuitableTrips(){
        firebaseFirestore.collection("Trips")
                .whereGreaterThan("departTime",startTimestamp)
                .whereLessThan("departTime",endTimestamp)
                .orderBy("departTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(AdminMainActivity.this, error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                            return;
                        }

                        tripArrayList.clear(); //clear ArrayList before the getting data
                        adminMainRecyclerAdapter.notifyDataSetChanged();
                        if(value != null){
                            for (QueryDocumentSnapshot doc : value) {
                                Trip trip = doc.toObject(Trip.class); //get Trip Data from fireStore as a Trip Object
                                tripArrayList.add(trip);

                                adminMainRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        List<DocumentSnapshot> document = value.getDocuments();
                        if(document.isEmpty()){
                            //if there is no result
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminMainActivity.this);
                            builder.setMessage("There is no trip!");
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
        menuInflater.inflate(R.menu.admin_options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.carData){
            Intent intent = new Intent(AdminMainActivity.this, CarDataActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.signOut){
            firebaseAuth.signOut();
            Intent intent = new Intent(AdminMainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}