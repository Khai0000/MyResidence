package com.example.googlemap.ChatMessageViewHolder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.R;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    LinearLayout leftChatLinearLayout, rightChatLinearLayout;
    TextView leftChatTextView, rightChatTextView, leftTimeStampTextView, rightTimeStampTextView, timeStampDivider;

    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        leftChatLinearLayout = itemView.findViewById(R.id.left_chat_layout);
        leftChatTextView = itemView.findViewById(R.id.left_chat_text_holder);
        rightChatLinearLayout = itemView.findViewById(R.id.right_chat_layout);
        rightChatTextView = itemView.findViewById(R.id.right_chat_text_holder);
        leftTimeStampTextView = itemView.findViewById(R.id.left_chat_timestamp_holder);
        rightTimeStampTextView = itemView.findViewById(R.id.right_chat_timestamp_holder);
        timeStampDivider = itemView.findViewById(R.id.timestamp_divider);
    }
}
