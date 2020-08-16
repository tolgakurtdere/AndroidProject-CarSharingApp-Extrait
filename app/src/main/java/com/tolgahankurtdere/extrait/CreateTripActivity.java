package com.tolgahankurtdere.extrait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTripActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView departDateText, fromText, toText, peopleNumberText, timeText, breakNumberText, carModelText;
    private CheckBox canDriveCheck;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String[] locations;

    int selectedItemFrom = -1, selectedItemTo = -1, selectedItemPeopleNum = -1 ,selectedItemBreakNum = -1, selectedItemCarModel = -1;
    Timestamp departTimestamp;

    int chosenYear,chosenMonth,chosenDay,chosenHour,chosenMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        this.setTitle("Create a new trip"); //set activity title

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        departDateText = findViewById(R.id.textViewCreateDepartDate);
        fromText = findViewById(R.id.textViewCreateFrom);
        toText = findViewById(R.id.textViewCreateTo);
        peopleNumberText = findViewById(R.id.textViewCreatePeopleNumber);
        canDriveCheck = findViewById(R.id.checkBoxCanDrive);
        timeText = findViewById(R.id.textViewCreateTime);
        breakNumberText = findViewById(R.id.textViewCreateBreakNumber);
        carModelText = findViewById(R.id.textViewCreateCarModel);

        /* Get data from intent Start */
        fromText.setText(getIntent().getStringExtra("fromText"));
        toText.setText(getIntent().getStringExtra("toText"));
        departDateText.setText(getIntent().getStringExtra("departDateText"));
        locations = getIntent().getStringArrayExtra("locations");
        chosenYear = getIntent().getIntExtra("chosenYear",-1);
        chosenMonth = getIntent().getIntExtra("chosenMonth",-1);
        chosenDay = getIntent().getIntExtra("chosenDay",-1);
        selectedItemFrom = getIntent().getIntExtra("selectedItemFrom",-1);
        selectedItemTo = getIntent().getIntExtra("selectedItemTo",-1);
        departTimestamp = getIntent().getParcelableExtra("departTimestamp");
        /* Get data from intent End */

        /* PopUp Calendar Start */
        departDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = chosenYear; //calendar will start with this date
                int month = chosenMonth;
                int day = chosenDay;

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTripActivity.this,R.style.ThemeOverlay_AppCompat_Dialog,dateSetListener,year,month,day);
                //datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //disable old dates
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
            }
        };
        /* PopUp Calendar End */

        /* Alert Dialog Single Choice Start */
        fromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
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

        /* Alert Dialog Single Choice Start */
        peopleNumberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
                builder.setTitle("How many people do you want to travel with?");
                final String[] peopleNumbers = new String[]{"2","3","4","5"};
                builder.setSingleChoiceItems(peopleNumbers, selectedItemPeopleNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemPeopleNum = which;
                        peopleNumberText.setText(peopleNumbers[selectedItemPeopleNum]);
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
        breakNumberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
                builder.setTitle("Please select break number");
                final String[] breakNumbers = new String[]{"0","1","2","3+"};
                builder.setSingleChoiceItems(breakNumbers, selectedItemBreakNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemBreakNum = which;
                        breakNumberText.setText(breakNumbers[selectedItemBreakNum]);
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
        carModelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
                builder.setTitle("Please select a car model");
                final String[] carModels = new String[]{"Renault Clio 3","Renault Clio 4","Renault Clio 5"};
                builder.setSingleChoiceItems(carModels, selectedItemCarModel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedItemCarModel = which;
                        carModelText.setText(carModels[selectedItemCarModel]);
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

        /* TimePickerDialog Start */
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time;
                        if(hourOfDay == 0) {
                            if(minute == 0) time = "00:00";
                            else time = "00:" + minute;
                        }
                        else{
                            if(minute == 0) time = hourOfDay + ":00";
                            else time = hourOfDay + ":" + minute;
                        }
                        timeText.setText(time);
                        chosenHour = hourOfDay;
                        chosenMinute = minute;
                    }
                },chosenHour,chosenMinute,true);
                timePickerDialog.show();
            }
        });
        /* TimePickerDialog End */
    }

    public void createTripClicked(View view){
        Calendar calendar = Calendar.getInstance();
        calendar.set(chosenYear,chosenMonth,chosenDay,chosenHour-3,chosenMinute,0);
        departTimestamp = new Timestamp(calendar.getTimeInMillis()/1000 , 0);

        final String from = fromText.getText().toString();
        final String to = toText.getText().toString();
        final Timestamp time = departTimestamp;
        final int peopleNumber = Integer.parseInt(peopleNumberText.getText().toString()); //or selectedItemPeopleNum+2
        final int breakNumber = selectedItemBreakNum;
        final String carModel = carModelText.getText().toString();
        final boolean canDrive = canDriveCheck.isChecked();


        firebaseFirestore.collection("Cars")
                .whereEqualTo("model",carModel)
                .whereEqualTo("available",true)
                .whereEqualTo("location",from)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final Trip newTrip = new Trip(from,to,carModel,canDrive,peopleNumber,breakNumber,time); //create new trip from Trip class then push it to fireStore
                            newTrip.addUsertoTrip(firebaseAuth.getCurrentUser().getEmail()); //add userID to trip user arrayList
                            if(!task.getResult().isEmpty()){ //if there is available car

                                //get an available car and fix fireStore data
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    Car car = document.toObject(Car.class);
                                    car.setAvailable(false);
                                    newTrip.setCarID(document.getId());
                                    firebaseFirestore.collection("Cars").document(document.getId()).set(car, SetOptions.merge()); //update car data
                                    break;
                                }

                                //save newTrip to fireStore
                                firebaseFirestore.collection("Trips").add(newTrip).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(final DocumentReference documentReference) {
                                        Toast.makeText(CreateTripActivity.this,"Trip is created!",Toast.LENGTH_LONG).show();
                                        //add tripID to trip and merge
                                        newTrip.setTripID(documentReference.getId());
                                        documentReference.set(newTrip,SetOptions.merge());

                                        //add tripID to user trips arrayList
                                        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                User user = documentSnapshot.toObject(User.class);
                                                user.addTriptoUser(documentReference.getId());
                                                firebaseFirestore.collection("Users").document(user.getEmail()).set(user,SetOptions.merge()); //update fireStore data
                                            }
                                        });

                                        //intent to main activity
                                        Intent intent = new Intent(CreateTripActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateTripActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else{ //if there is no available car
                                AlertDialog.Builder builder = new AlertDialog.Builder(CreateTripActivity.this);
                                builder.setMessage("There is no available car. Please try other car models!");
                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                        else {
                            Toast.makeText(CreateTripActivity.this, task.getException().getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}