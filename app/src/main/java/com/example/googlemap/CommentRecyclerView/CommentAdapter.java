package com.example.googlemap.CommentRecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.Pages.ViewProfilePage;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.firebase.MyFireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

    Activity context;
    ArrayList<Comment> list;

    public CommentAdapter(Activity context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_section_holder,parent,false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = list.get(position);
        Picasso.get().load(comment.getProfileUrl()).fit().into(holder.imageView);
        holder.nameTextView.setText(comment.getUsername());
        holder.commentTextView.setText(comment.getComment());

        String userUid = list.get(position).getUserUID();

        if (userUid.equals(UserProvider.UID)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String postId = comment.getPostId();
                    String commentId = comment.getCommentId();

                    SpannableString spannableTitle = new SpannableString("Are you sure you want to delete this comment?");
                    spannableTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(spannableTitle)
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked Delete button
                                    FirebaseFirestore.getInstance().collection("posts").document(postId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            List<Map<String, Object>> comments = (List<Map<String, Object>>) document.get("comments");
                                                            if (comments != null) {
                                                                for (Map<String, Object> commentMap : comments) {
                                                                    if (commentId.equals(commentMap.get("commentId"))) {
                                                                        comments.remove(commentMap);
                                                                        break;
                                                                    }
                                                                }

                                                                FirebaseFirestore.getInstance().collection("posts").document(postId)
                                                                        .update("comments", comments)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    int itemPosition = holder.getAdapterPosition();
                                                                                    list.remove(itemPosition);
                                                                                    notifyItemRemoved(itemPosition);
                                                                                    notifyItemRangeChanged(itemPosition, list.size());
                                                                                    dialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            })
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked Cancel button
                                    dialog.dismiss();
                                }
                            });

                    // Set the dialog background color
                    AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2d2d2d")));

                    // Show the AlertDialog
                    alertDialog.show();

                    Button cancelButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    cancelButton.setTextColor(Color.RED);
                }
            });
        }


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFireStore.getUserData(userUid,new CommentCallBack(){
                    @Override
                    public void onGetProfile(DocumentSnapshot snapshot) {
                        Intent intent = new Intent(context, ViewProfilePage.class);
                        intent.putExtra("username",snapshot.getString("username"));
                        intent.putExtra("address",snapshot.getString("address"));
                        intent.putExtra("bloodType",snapshot.getString("bloodType"));
                        intent.putExtra("contact",snapshot.getString("contact"));
                        intent.putExtra("email",snapshot.getString("email"));
                        intent.putExtra("profileUrl",snapshot.getString("profileUrl"));
                        intent.putExtra("postAuthorUid",userUid);

                        context.startActivity(intent);
                    }
                },context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
