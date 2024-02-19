package com.example.googlemap.RecentChatRecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.Chatroom.ChatRoom;
import com.example.googlemap.CommentRecyclerView.CommentCallBack;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.firebase.MyFireStore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatViewHolder> {

    Activity context;
    ArrayList<RecentChat> recentChatArrayList;

    public RecentChatAdapter(Activity activity, ArrayList<RecentChat> recentChatArrayList) {
        this.context = activity;
        this.recentChatArrayList = recentChatArrayList;
    }

    @NonNull
    @Override
    public RecentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentChatViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_chat_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatViewHolder holder, int position) {
        RecentChat chat = recentChatArrayList.get(position);

        if (chat.getLastSenderUid().equals(UserProvider.UID))
            holder.lastMessageTextView.setText("(Me) " + chat.getMessage());
        else
            holder.lastMessageTextView.setText(chat.getMessage());

        holder.timeStampTextView.setText(chat.getFormattedTimestamp());

        holder.usernameTextView.setText(chat.getAnotherUsername());
        Picasso.get().load(chat.getAnotherUserProfileUrl()).fit().into(holder.imageView);


        holder.recentChatHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoom.class);
                intent.putExtra("profileUrl", chat.getAnotherUserProfileUrl());
                intent.putExtra("username", chat.getAnotherUsername());
                intent.putExtra("userUid", chat.getSenderUid());
                v.getContext().startActivity(intent);
            }
        });

    }

    public void setRecentChats(ArrayList<RecentChat> newChatList)
    {
        this.recentChatArrayList=newChatList;
    }

    @Override
    public int getItemCount() {
        return recentChatArrayList.size();
    }
}
