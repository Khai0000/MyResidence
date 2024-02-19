package com.example.googlemap.HazardStories;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.Pages.PostDetails;
import com.example.googlemap.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolder> {

    Context context;
    List<HazardStory> stories;

    public StoryAdapter(Context context, List<HazardStory> stories) {
        this.context = context;
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_view, parent, false);

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new StoryViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {

        HazardStory story = stories.get(position);
        holder.imageView.setBackgroundResource(R.drawable.circular_image);
        Picasso.get().load(stories.get(position).getUserProfileUrl()).into(holder.imageView);
        holder.usernameHolder.setText(stories.get(position).getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),PostDetails.class);
                intent.putExtra("postAuthor",story.getUsername());
                intent.putExtra("postId",story.getPostId());
                intent.putExtra("title",story.getTitle());
                intent.putExtra("description",story.getDescription());
                intent.putExtra("postImageUrl",story.getPostUrl());
                intent.putExtra("authorUid",story.getUserUID());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }
}
