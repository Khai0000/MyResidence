package com.example.googlemap.RecentChatRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.R;

public class RecentChatViewHolder extends RecyclerView.ViewHolder {

    LinearLayout recentChatHolder;
    TextView timeStampTextView, usernameTextView, lastMessageTextView;
    ImageView imageView;

    public RecentChatViewHolder(@NonNull View itemView) {
        super(itemView);
        recentChatHolder = itemView.findViewById(R.id.recent_chat_holder);
        timeStampTextView = itemView.findViewById(R.id.time_stamp_text_holder);
        usernameTextView = itemView.findViewById(R.id.username_text_holder);
        lastMessageTextView = itemView.findViewById(R.id.last_message_text_holder);
        imageView = itemView.findViewById(R.id.user_image_holder);
    }
}
