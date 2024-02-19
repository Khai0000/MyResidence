package com.example.googlemap.EmergencyLocationRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.R;

public class EmergencyLocationViewHolder extends RecyclerView.ViewHolder {

    TextView locationNameTextView,distanceTextView;
    ImageView locationIcon;
    Button nextButton;
    public EmergencyLocationViewHolder(@NonNull View itemView) {
        super(itemView);
        locationIcon= itemView.findViewById(R.id.location_icon_holder);
        locationNameTextView= itemView.findViewById(R.id.location_name_text_holder);
        nextButton = itemView.findViewById(R.id.next_button);
        distanceTextView = itemView.findViewById(R.id.distance_text_holder);
    }
}
