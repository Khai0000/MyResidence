package com.example.googlemap.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.CommentRecyclerView.CommentCallBack;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.RecentChatRecyclerView.RecentChat;
import com.example.googlemap.RecentChatRecyclerView.RecentChatAdapter;
import com.example.googlemap.RecentChatRecyclerView.RecentChatCallBack;
import com.example.googlemap.firebase.MyFireStore;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class RecentChatFragment extends Fragment {

    private RecyclerView recentChatRecyclerView;
    private TextView resultTextView;
    private RecentChatAdapter recentChatAdapter;
    private ArrayList<RecentChat> recentChatArrayList;

    private EditText recentChatSearchInput;
    private Button refreshRecentChatButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_chat_fragment, container, false);
        recentChatRecyclerView = view.findViewById(R.id.recent_chat_recycler_view);
        resultTextView = view.findViewById(R.id.result_text_holder);
        refreshRecentChatButton = view.findViewById(R.id.refresh_chat_button);
        recentChatSearchInput = view.findViewById(R.id.recent_chat_search_input);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpRecyclerView();
        setUpRecentChatSearchInput();

        refreshRecentChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the setupRecyclerView method to refresh the recent chats
                setUpRecyclerView();
            }
        });
    }

    private void setUpRecyclerView() {
        recentChatArrayList = new ArrayList<>();
        recentChatAdapter = new RecentChatAdapter(getActivity(), recentChatArrayList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recentChatRecyclerView.setLayoutManager(manager);
        recentChatRecyclerView.setAdapter(recentChatAdapter);

        MyFireStore.listenForRecentChats(UserProvider.UID, new RecentChatCallBack() {
            @Override
            public void onRecentChatGet(QuerySnapshot result) {
                recentChatArrayList.clear();
                for (DocumentSnapshot snapshot : result.getDocuments()) {
                    String lastMessage = snapshot.getString("lastMessage");
                    Timestamp timestamp = snapshot.getTimestamp("lastMessageTimeStamp");
                    String lastSenderUid = snapshot.getString("lastMessageSenderUid");
                    String anotherSenderUid = "";

                    ArrayList<String> chatUsers = (ArrayList<String>) snapshot.get("chatUsers");

                    for (String userUid : chatUsers) {
                        if (!userUid.equals(UserProvider.UID)) {
                            anotherSenderUid = userUid;
                        }
                    }
                    if (!lastMessage.isEmpty())
                    {
                        RecentChat recentChat = new RecentChat(lastMessage,anotherSenderUid,timestamp,lastSenderUid);
                        MyFireStore.getUserData(anotherSenderUid,new CommentCallBack(){
                            @Override
                            public void onGetProfile(DocumentSnapshot snapshot) {
                                super.onGetProfile(snapshot);
                                String username = snapshot.getString("username");
                                String profileUrl = snapshot.getString("profileUrl");
                                recentChat.setAnotherUsername(username);
                                recentChat.setAnotherUserProfileUrl(profileUrl);
                                recentChatArrayList.add(recentChat);
                                recentChatAdapter.notifyDataSetChanged();
                                updateUI();
                            }
                        },getActivity());
                    }
                }

            }
        });
    }

    private void updateUI() {
        if (recentChatArrayList.isEmpty()) {
            resultTextView.setVisibility(View.VISIBLE);
            recentChatRecyclerView.setVisibility(View.GONE);
        } else {
            resultTextView.setVisibility(View.GONE);
            recentChatRecyclerView.setVisibility(View.VISIBLE);
            Collections.sort(recentChatArrayList);
            recentChatAdapter.notifyDataSetChanged();
        }
    }

    private void updateUI(ArrayList<RecentChat> recentChatArrayList) {
        if (recentChatArrayList.isEmpty()) {
            resultTextView.setVisibility(View.VISIBLE);
            recentChatRecyclerView.setVisibility(View.GONE);
        } else {
            resultTextView.setVisibility(View.GONE);
            recentChatRecyclerView.setVisibility(View.VISIBLE);
            Collections.sort(recentChatArrayList);
            recentChatAdapter.notifyDataSetChanged();
        }
    }

    private void setUpRecentChatSearchInput() {
        recentChatSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for your implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchQuery = charSequence.toString().trim().toLowerCase();
                filterRecentChats(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for your implementation
            }
        });
    }

    private void filterRecentChats(String searchQuery) {
        resultTextView.setVisibility(View.GONE);
        recentChatRecyclerView.setVisibility(View.VISIBLE);

        ArrayList<RecentChat> filteredList = new ArrayList<>();
        for (RecentChat recentChat : recentChatArrayList) {
            // Modify this condition based on your search criteria
            if (recentChat.getAnotherUsername().toLowerCase().contains(searchQuery)) {
                filteredList.add(recentChat);
            }
        }

        if (searchQuery.isEmpty()) {
            recentChatAdapter.setRecentChats(recentChatArrayList);
        }

        recentChatAdapter.setRecentChats(filteredList);
        recentChatAdapter.notifyDataSetChanged();
        updateUI(filteredList);
    }

}
