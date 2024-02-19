package com.example.googlemap.HazardStories;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.R;

public class StoryViewHolder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView usernameHolder;

    public StoryViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.story_image);
        usernameHolder = itemView.findViewById(R.id.story_username);
    }

}
