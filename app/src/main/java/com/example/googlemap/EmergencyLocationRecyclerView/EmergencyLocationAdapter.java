package com.example.googlemap.EmergencyLocationRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EmergencyLocationAdapter extends RecyclerView.Adapter<EmergencyLocationViewHolder> {

    private Context context;
    private ArrayList<EmergencyLocation> emergencyLocationList;

    public EmergencyLocationAdapter(Context context, ArrayList<EmergencyLocation> emergencyLocationList) {
        this.context = context;
        this.emergencyLocationList = emergencyLocationList;
    }

    @NonNull
    @Override
    public EmergencyLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.emergency_location_view_holder,parent,false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new EmergencyLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyLocationViewHolder holder, int position) {

        EmergencyLocation location = emergencyLocationList.get(position);
        holder.locationNameTextView.setText(location.getLocationName());
        holder.distanceTextView.setText(String.format("%.2f",location.getDistance())+" km away");
        Picasso.get().load(location.getIconUrl()).fit().into(holder.locationIcon);
        holder.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EmergencyLocationDetails.class);
                intent.putExtra("locationName",location.getLocationName());
                intent.putExtra("address",location.getAddress());
                intent.putExtra("rating",location.getRating());
                intent.putExtra("totalUserReview",location.getTotalUserReview());
                intent.putExtra("destinationDistance",location.getDistance());
                intent.putExtra("locationImage",location.getLocationImage());
                intent.putExtra("openNow",location.getOpenNow());
                intent.putExtra("destinationLatitude",location.getLatitude());
                intent.putExtra("destinationLongitude",location.getLongitude());
                intent.putExtra("userLatitude",location.getUserLatitude());
                intent.putExtra("userLongitude",location.getUserLongitude());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emergencyLocationList.size();
    }
}
