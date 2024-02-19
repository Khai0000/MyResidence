package com.example.googlemap.Guide;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.HazardStories.StoryViewHolder;
import com.example.googlemap.R;

import java.util.List;


public class GuideViewAdapter extends RecyclerView.Adapter<GuideViewHolder>{
    private List<GuideModel> itemList;
    private OnItemClickListener listener;
    public GuideViewAdapter(List<GuideModel> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guide_row, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        GuideModel currentItem = itemList.get(position);
        holder.textViewItem.setText(currentItem.getText());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(GuideModel guidemodel);
    }

}
