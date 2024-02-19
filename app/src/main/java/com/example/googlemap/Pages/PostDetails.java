package com.example.googlemap.Pages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.CommentRecyclerView.CommentCallBack;
import com.example.googlemap.CommentRecyclerView.Comment;
import com.example.googlemap.CommentRecyclerView.CommentAdapter;
import com.example.googlemap.CommentRecyclerView.VerticalMarginItemDecoration;
import com.example.googlemap.Fragments.MyMapFragment;
import com.example.googlemap.HazardStories.HazardStory;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.firebase.MyFireStore;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostDetails extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private Button goBackButton, descriptionButton;
    private TextView title, postAuthor, description, resultTextView, openCommentTextView;
    private ImageView postImage;

    private GestureDetector gestureDetector;

    private String postId;

    private ArrayList<Comment> commentList = new ArrayList<>();
    private RecyclerView commentRecyclerView;

    private Button deleteButton;

    private boolean isDescriptionButtonClicked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details_page);

        goBackButton = findViewById(R.id.go_back_button);
        title = findViewById(R.id.post_title);
        description = findViewById(R.id.post_description);
        postAuthor = findViewById(R.id.post_author);
        postImage = findViewById(R.id.post_image);
        openCommentTextView = findViewById(R.id.open_comment_button);

        Intent intent = getIntent();

        String authorText = intent.getStringExtra("postAuthor");

        postAuthor.setText("by " + authorText);
        postAuthor.setPaintFlags(postAuthor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        postAuthor.getPaint().setUnderlineText(true);

        postId = intent.getStringExtra("postId");
        title.setText(intent.getStringExtra("title"));
        description.setText(intent.getStringExtra("description"));
        description.setMovementMethod(new ScrollingMovementMethod());
        Picasso.get().load(intent.getStringExtra("postImageUrl")).fit().into(postImage);

        descriptionButton = findViewById(R.id.description_button);

        openCommentTextView.setPaintFlags(postAuthor.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        openCommentTextView.getPaint().setUnderlineText(true);

        openCommentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComment();
            }
        });

        if(intent.getStringExtra("authorUid").equals(UserProvider.UID))
        {
            deleteButton= findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SpannableString spannableTitle = new SpannableString("Are you sure you want to delete this post?");
                    spannableTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(PostDetails.this);
                    builder.setMessage(spannableTitle)
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseFirestore.getInstance().collection("posts").document(postId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            removeMarkerFromMap(postId);

                                            // 2. Remove the HazardStory from storyList
                                            removeHazardStory(postId);

                                            // 3. Notify the adapter
                                            MyMapFragment.storyAdapter.notifyDataSetChanged();
                                            finish();
                                        }
                                    });
                                    dialog.dismiss();
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




        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gestureDetector = new GestureDetector(this, this);

        setUpPostAuthorTextView(intent.getStringExtra("authorUid"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        hidePostDescription();
        return true;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        double swipeDistance = e1.getY() - e2.getY();
        if (swipeDistance > 250 && swipeDistance > 0) {
            showPostDescription();
            return true;
        } else if (swipeDistance < -250 && swipeDistance < 0) {
            hidePostDescription();
            return true;
        }
        return false;
    }


    private void showComment() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.comment_dialog);
        commentList.clear();

        ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        resultTextView = dialog.findViewById(R.id.result_text_holder);

        MyFireStore.getComments(postId, commentList, new CommentCallBack() {
            @Override
            public void onCommentGet() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        commentRecyclerView.getAdapter().notifyDataSetChanged();
                        if (commentList.size() == 0) {
                            resultTextView.setVisibility(View.VISIBLE);
                        } else {
                            resultTextView.setVisibility(View.GONE);
                        }
                    }
                }, 200);
            }
        });


        setUpCommentButton(dialog.findViewById(R.id.open_upload_comment_button));

        commentRecyclerView = dialog.findViewById(R.id.comment_recycler_view_holder);
        commentRecyclerView.setAdapter(new CommentAdapter(PostDetails.this, commentList));
        LinearLayoutManager layoutManager = new LinearLayoutManager(dialog.getContext());
        commentRecyclerView.setLayoutManager(layoutManager);
        commentRecyclerView.addItemDecoration(new VerticalMarginItemDecoration(18));

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setUpCommentButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadCommentDialog();
            }
        });
    }

    private void showUploadCommentDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.upload_comment_dialog);

        TextInputEditText commentInputField = dialog.findViewById(R.id.comment_input_field);
        setUpCommentInputField(commentInputField);
        setUpCloseButton(dialog.findViewById(R.id.close_button), dialog);
        setUpUploadCommentButton(dialog.findViewById(R.id.upload_comment_button), dialog, commentInputField);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setUpCommentInputField(TextInputEditText commentInputField) {
        InputFilter filters[] = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(400);
        commentInputField.setFilters(filters);
    }

    private void setUpCloseButton(Button button, final Dialog dialog) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setUpUploadCommentButton(Button button, Dialog dialog, TextInputEditText commentInputField) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commentInputField.getText().toString().trim().equals(""))
                    commentInputField.setError("Comment can't be empty");
                else {
                    button.setEnabled(false);
                    Map<String, Object> commentData = new HashMap<>();
                    String commentId = UUID.randomUUID().toString();
                    commentData.put("profileUrl", UserProvider.profileUrl);
                    commentData.put("username", UserProvider.username);
                    commentData.put("comment", commentInputField.getText().toString().trim());
                    commentData.put("userUid", UserProvider.UID);
                    commentData.put("commentId", commentId);
                    commentList.add(0, new Comment(UserProvider.UID, UserProvider.profileUrl, UserProvider.username, commentInputField.getText().toString().trim(),postId,commentId));
                    commentRecyclerView.getAdapter().notifyDataSetChanged();
                    MyFireStore.uploadComment(postId, commentData);
                    resultTextView.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });
    }

    private void setUpPostAuthorTextView(String authorUid) {
        postAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFireStore.getUserData(authorUid, new CommentCallBack() {
                    @Override
                    public void onGetProfile(DocumentSnapshot snapshot) {
                        Intent intent = new Intent(PostDetails.this, ViewProfilePage.class);
                        intent.putExtra("username", snapshot.getString("username"));
                        intent.putExtra("address", snapshot.getString("address"));
                        intent.putExtra("bloodType", snapshot.getString("bloodType"));
                        intent.putExtra("contact", snapshot.getString("contact"));
                        intent.putExtra("email", snapshot.getString("email"));
                        intent.putExtra("profileUrl", snapshot.getString("profileUrl"));
                        intent.putExtra("postAuthorUid", authorUid);

                        startActivity(intent);
                    }
                }, PostDetails.this);
            }
        });
    }

    private void showPostDescription() {
        if (!isDescriptionButtonClicked) {
            int maxHeightInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    400, // height in dp
                    getResources().getDisplayMetrics()
            );

            description.setMaxHeight(maxHeightInPixels);
            description.setHeight(maxHeightInPixels);
            isDescriptionButtonClicked = true;
        } else {
            int maxHeightInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    80, // height in dp
                    getResources().getDisplayMetrics()
            );
            description.setHeight(maxHeightInPixels);
            description.setMaxHeight(maxHeightInPixels);
            isDescriptionButtonClicked = false;
        }
    }

    private void hidePostDescription() {
        int maxHeightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                80, // height in dp
                getResources().getDisplayMetrics()
        );

        description.setHeight(maxHeightInPixels);
        description.setMaxHeight(maxHeightInPixels);
        isDescriptionButtonClicked = false;
    }

    // Helper method to remove the marker from the map
    private void removeMarkerFromMap(String postId) {
        for (Marker marker : MyMapFragment.markerMap.keySet()) {
            Map<String, Object> postData = MyMapFragment.markerMap.get(marker);
            if (postData.get("postId").equals(postId)) {
                marker.remove();
                MyMapFragment.markerMap.remove(marker);
                break;
            }
        }
    }

    // Helper method to remove the HazardStory from storyList
    private void removeHazardStory(String postId) {
        for (HazardStory story : MyMapFragment.storyList) {
            if (story.getPostId().equals(postId)) {
                MyMapFragment.storyList.remove(story);
                break;
            }
        }
    }


}