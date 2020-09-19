package com.tolgahankurtdere.extrait;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class MyTripsRecyclerAdapter extends RecyclerView.Adapter<MyTripsRecyclerAdapter.TripHolder> {


    class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView fromText,toText,dateText,timeText,carModelText;

        public TripHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            fromText = itemView.findViewById(R.id.myTripsRecyclerTextViewFrom);
            toText = itemView.findViewById(R.id.myTripsRecyclerTextViewTo);
            dateText = itemView.findViewById(R.id.myTripsRecyclerTextViewDate);
            timeText = itemView.findViewById(R.id.myTripsRecyclerTextViewTime);
            carModelText = itemView.findViewById(R.id.myTripsRecyclerTextViewCarModel);

        }

        @Override
        public void onClick(View v) {
            // get the position on recyclerview.
            int position = getLayoutPosition();
            Intent intent = new Intent(context,TicketActivity.class);
            intent.putExtra("tripData", tripArrayList.get(position));
            context.startActivity(intent);
        }
    }

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayList<Trip> tripArrayList;
    private Context context;

    public MyTripsRecyclerAdapter(final Context context, ArrayList<Trip> tripArrayList) {
        this.context = context;
        this.tripArrayList = tripArrayList;

    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.my_trips_recycler_row,parent,false);

        return new TripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripHolder holder, int position) {
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

        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
        holder.dateText.setText(date);

        holder.fromText.setText(tripArrayList.get(position).getFrom());
        holder.toText.setText(tripArrayList.get(position).getTo());
        holder.carModelText.setText(tripArrayList.get(position).getCarModel());

    }

    @Override
    public int getItemCount() {
        return tripArrayList.size();
    }

}
