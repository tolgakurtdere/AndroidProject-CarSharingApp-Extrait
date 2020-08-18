package com.tolgahankurtdere.extrait;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminMainRecyclerAdapter extends RecyclerView.Adapter<AdminMainRecyclerAdapter.TripHolder> {

    class TripHolder extends RecyclerView.ViewHolder{

        TextView dateText,timeText,fromText,toText,carIdText,tripIdText;
        CheckBox activeCheck;

        public TripHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.adminMainRecyclerTextViewDate);
            timeText = itemView.findViewById(R.id.adminMainRecyclerTextViewTime);
            fromText = itemView.findViewById(R.id.adminMainRecyclerTextViewFrom);
            toText = itemView.findViewById(R.id.adminMainRecyclerTextViewTo);
            carIdText = itemView.findViewById(R.id.adminMainRecyclerTextViewCarID);
            tripIdText = itemView.findViewById(R.id.adminMainRecyclerTextViewTripID);
            activeCheck = itemView.findViewById(R.id.adminMainCheckBoxActive);

        }
    }

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ArrayList<Trip> tripArrayList;

    public AdminMainRecyclerAdapter(ArrayList<Trip> tripArrayList) {
        this.tripArrayList = tripArrayList;
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.admin_main_recycler_row,parent,false);

        return new AdminMainRecyclerAdapter.TripHolder(view);
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
        holder.carIdText.setText(tripArrayList.get(position).getCarID());
        holder.tripIdText.setText(tripArrayList.get(position).getTripID());

        holder.activeCheck.setChecked(tripArrayList.get(position).isActive());
    }

    @Override
    public int getItemCount() {
        return tripArrayList.size();
    }

}
