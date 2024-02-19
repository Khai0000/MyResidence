package com.example.googlemap.Chatroom;

import static com.example.googlemap.firebase.MyFireStore.FIRESTORE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemap.ChatMessageViewHolder.ChatMessage;
import com.example.googlemap.ChatMessageViewHolder.ChatMessageAdapter;
import com.example.googlemap.Provider.UserProvider;
import com.example.googlemap.R;
import com.example.googlemap.firebase.MyFireStore;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatRoom extends AppCompatActivity {

    private TextView usernameTextView;
    private ImageView usernameProfileIcon;

    private EditText messageInput;

    private Button goBackButton;

    private String anotherUserUid, chatRoomUid;

    private ProgressBar progressBar;

    private RecyclerView chatMessageRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        Intent intent = getIntent();
        setUpUsernameTextView(intent.getStringExtra("username").toString());
        setUpUserProfileIcon(intent.getStringExtra("profileUrl"));

        if(UserProvider.UID==null)
        {
            UserProvider.UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        anotherUserUid = intent.getStringExtra("userUid");
        chatRoomUid = getChatRoomId(UserProvider.UID, anotherUserUid);
        progressBar = findViewById(R.id.progress_indicator);
        chatMessageRecyclerView = findViewById(R.id.chat_message_recycler_view);

        MyFireStore.checkAndCreateChatRoom(chatRoomUid, UserProvider.UID, anotherUserUid);

        setUpGoBackButton();
        setUpMessageInput();
        setUpRecyclerView();

    }

    private void setUpUsernameTextView(String name) {
        usernameTextView = findViewById(R.id.username_text_holder);
        usernameTextView.setText(name);
    }

    private void setUpUserProfileIcon(String profileUrl) {
        usernameProfileIcon = findViewById(R.id.user_image_holder);
        Picasso.get().load(profileUrl).into(usernameProfileIcon);
    }

    private void setUpGoBackButton() {
        goBackButton = findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpMessageInput() {
        EditText messageInput = findViewById(R.id.message_input);

        messageInput.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int drawableEnd = messageInput.getCompoundDrawables()[2].getBounds().width();
                if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= drawableEnd) {
                    if (messageInput.getText().toString().trim().equals(""))
                        return false;
                    else {
                        progressBar.setVisibility(View.VISIBLE);
                        messageInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                        MyFireStore.uploadChatMessage(chatRoomUid, messageInput.getText().toString().trim(), UserProvider.UID, new ChatRoomCallBack() {
                            @Override
                            public void uploadSuccess() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageInput.setText("");
                                        messageInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.send_icon, 0);
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }, 300);

                            }

                            @Override
                            public void uploadFailed() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageInput.setError("Failed to sent message, try again");
                                        messageInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.send_icon, 0);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }, 300);
                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        ArrayList<ChatMessage> chatList = new ArrayList<>();

        listenForChatMessages(chatRoomUid, new ChatRoomCallBack() {
            @Override
            public void getAllMessagesSuccess(QuerySnapshot snapshot) {
                chatList.clear();

                for (QueryDocumentSnapshot document : snapshot) {
                    String messageText = document.getString("message");
                    String senderUid = document.getString("senderUid");
                    Timestamp timestamp = document.getTimestamp("timestamp");

                    ChatMessage chatMessage = new ChatMessage(messageText, senderUid, timestamp);
                    chatList.add(chatMessage);
                }

                ChatMessageAdapter adapter = new ChatMessageAdapter(ChatRoom.this, chatList);
                chatMessageRecyclerView.setAdapter(adapter);

                if (!chatList.isEmpty()) {
                    chatMessageRecyclerView.scrollToPosition(chatList.size() - 1);
                }
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatMessageRecyclerView.setLayoutManager(manager);
    }

    public static void listenForChatMessages(String chatRoomId, ChatRoomCallBack callBack) {
        FIRESTORE.collection("chatrooms").document(chatRoomId).collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null) {
                        callBack.getAllMessagesSuccess(value);
                    }
                });
    }


    private String getChatRoomId(String userUid1, String userUid2) {
        if (userUid1.hashCode() < userUid2.hashCode())
            return userUid1 + "_" + userUid2;
        else
            return userUid2 + "_" + userUid1;
    }


}
