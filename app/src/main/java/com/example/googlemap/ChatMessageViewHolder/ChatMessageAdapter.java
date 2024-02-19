package com.example.googlemap.ChatMessageViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageViewHolder> {

    Context context;
    ArrayList<ChatMessage> chatMessageList;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_view_holder, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);

        if (chatMessage.getSenderUid().equals(UserProvider.UID)) {
            holder.leftChatLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            holder.leftChatTextView.setVisibility(View.GONE);

            holder.rightChatLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
            holder.rightChatTextView.setText(chatMessage.getMessage());
            holder.rightTimeStampTextView.setText(chatMessage.getFormattedTime());

            holder.rightChatLinearLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setVisibility(View.VISIBLE);
            holder.rightTimeStampTextView.setVisibility(View.VISIBLE);

            holder.leftChatLinearLayout.setVisibility(View.GONE);
            holder.leftChatTextView.setVisibility(View.GONE);
            holder.leftTimeStampTextView.setVisibility(View.GONE);


        } else {
            holder.leftChatLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f));
            holder.leftChatTextView.setText(chatMessage.getMessage());
            holder.leftTimeStampTextView.setText(chatMessage.getFormattedTime());

            holder.rightChatLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));

            holder.leftChatLinearLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextView.setVisibility(View.VISIBLE);
            holder.leftTimeStampTextView.setVisibility(View.VISIBLE);

            holder.rightChatLinearLayout.setVisibility(View.GONE);
            holder.rightChatTextView.setVisibility(View.GONE);
            holder.rightTimeStampTextView.setVisibility(View.GONE);

        }

        setTimeStamp(holder.timeStampDivider, chatMessage.getTimestamp().toDate(), position);
    }


    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    private void setTimeStamp(TextView timeStampTextView, Date timestamp, int position) {
        if (position > 0) {
            ChatMessage previousMessage = chatMessageList.get(position - 1);
            if (isSameDay(previousMessage.getTimestamp().toDate(), timestamp)) {
                timeStampTextView.setVisibility(View.GONE);
            } else {
                timeStampTextView.setText(formatTimeStampDivider(timestamp));
                timeStampTextView.setVisibility(View.VISIBLE);
            }
        } else {
            timeStampTextView.setText(formatTimeStampDivider(timestamp));
            timeStampTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    private String formatTimeStampDivider(Date timestamp) {
        SimpleDateFormat dateFormatDivider = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        return dateFormatDivider.format(timestamp);
    }
}
