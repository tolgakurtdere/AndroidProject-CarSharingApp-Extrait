package com.tolgahankurtdere.extrait;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.TripHolder> {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayList<String> tripIDs;
    private ArrayList<Trip> tripArrayList;

    public MainRecyclerAdapter(ArrayList<Trip> tripArrayList, ArrayList<String> tripIDs) {
        this.tripIDs = tripIDs;

        this.tripArrayList = tripArrayList;
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.main_recycler_row,parent,false);

        return new TripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TripHolder holder, final int position) {
        String time;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tripArrayList.get(position).getDepartTime().toDate().getTime());
        calendar.add(Calendar.HOUR_OF_DAY,3); //because of UTC+3

        if(calendar.get(Calendar.MINUTE) == 0) {
            time = calendar.get(Calendar.HOUR_OF_DAY) + ":00";
        }
        else{
            time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        }
        holder.timeText.setText(time);

        holder.peopleNumberText.setText(tripArrayList.get(position).getFullSeatNumber() + "/" + tripArrayList.get(position).getPeopleNumber() + "(" + tripArrayList.get(position).getDriverNumber() + ")");
        holder.breakNumberText.setText(String.valueOf(tripArrayList.get(position).getBreakNumber()));
        holder.carModelText.setText(tripArrayList.get(position).getCarModel());

        if(tripArrayList.get(position).getFullSeatNumber() >= tripArrayList.get(position).getPeopleNumber()){
            holder.selectButton.setEnabled(false);
        }
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if(user.getTrips().contains(tripIDs.get(position))){ //if current user already has this trip, set button enabled false
                    holder.selectButton.setEnabled(false);
                }
            }
        });

        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTrip(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripArrayList.size();
    }

    class TripHolder extends RecyclerView.ViewHolder{

        TextView timeText,peopleNumberText,breakNumberText,carModelText;
        Button selectButton;

        public TripHolder(@NonNull View itemView) {
            super(itemView);

            timeText = itemView.findViewById(R.id.recyclerTextViewTime);
            peopleNumberText = itemView.findViewById(R.id.recyclerTextViewPeopleNumber);
            breakNumberText = itemView.findViewById(R.id.recyclerTextViewBreakNumber);
            carModelText = itemView.findViewById(R.id.recyclerTextViewCarModel);

            selectButton = itemView.findViewById(R.id.recyclerButtonSelect);

        }

    }

    private void joinTrip(final int position){
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.addTriptoUser(tripIDs.get(position));
                firebaseFirestore.collection("Users").document(user.getEmail()).set(user, SetOptions.merge()); //update firestore data
            }
        });
        firebaseFirestore.collection("Trips").document(tripIDs.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Trip trip = documentSnapshot.toObject(Trip.class);
                trip.setFullSeatNumber(trip.getFullSeatNumber()+1);
                firebaseFirestore.collection("Trips").document(tripIDs.get(position)).set(trip, SetOptions.merge()); //update firestore data
            }
        });
    }

}
