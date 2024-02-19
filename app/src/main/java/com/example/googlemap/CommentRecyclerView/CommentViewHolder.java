package com.example.googlemap.CommentRecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameTextView,commentTextView;

    Button deleteButton;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView=itemView.findViewById(R.id.user_image_holder);
        nameTextView=itemView.findViewById(R.id.username_text_holder);
        commentTextView=itemView.findViewById(R.id.comment_text_holder);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }
}
