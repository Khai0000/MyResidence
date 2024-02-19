package com.example.googlemap.Guide;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.example.googlemap.R;
public class GuideViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewItem;

    public GuideViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewItem = itemView.findViewById(R.id.guideString);
    }
}
