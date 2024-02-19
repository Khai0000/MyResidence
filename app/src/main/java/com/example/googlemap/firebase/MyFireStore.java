package com.example.googlemap.firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.googlemap.Chatroom.ChatRoomCallBack;
import com.example.googlemap.CommentRecyclerView.CommentCallBack;
import com.example.googlemap.CommentRecyclerView.Comment;
import com.example.googlemap.RecentChatRecyclerView.RecentChatCallBack;
import com.example.googlemap.dialog.MyCustomDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyFireStore {

    public static final FirebaseFirestore FIRESTORE = FirebaseFirestore.getInstance();

    public static void uploadData(String collection, String uuid, Map<String, Object> documentData, Activity activity) {
        FIRESTORE.collection(collection).document(uuid).set(documentData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    MyCustomDialog.showDialog("Uploaded successfully", "Please check for updates", activity, true);
                } else {
                    MyCustomDialog.showDialog("Failed to upload data", "Please try again later", activity, true);

                }
            }
        });
    }

    public static void updateData(String collection, String uuid, Map<String, Object> documentData, Activity activity) {
        FIRESTORE.collection(collection).document(uuid).update(documentData);
    }

    public static void uploadComment(String postId, Map<String, Object> commentData) {
        FIRESTORE.collection("posts").document(postId).update("comments", FieldValue.arrayUnion(commentData));
    }

    public static void getComments(String postId, ArrayList<Comment> commentList, CommentCallBack commentCallBack) {
        FIRESTORE.collection("posts")
                .document(postId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<Map<String, Object>> comments = (ArrayList<Map<String, Object>>) document.get("comments");
                                Collections.reverse(comments);
                                if (comments != null) {
                                    for (Map<String, Object> commentData : comments) {
                                        String profileUrl = (String) commentData.get("profileUrl");
                                        String username = (String) commentData.get("username");
                                        String comment = (String) commentData.get("comment");
                                        String userUid = (String) commentData.get("userUid");
                                        commentList.add(new Comment(userUid, profileUrl, username, comment));
                                    }
                                    commentCallBack.onCommentGet();
                                } else {
                                    Log.e("Firestore Error", "No comments found in the document.");
                                }
                            } else {
                                Log.e("Firestore Error", "Document does not exist.");
                            }
                        } else {
                            Log.e("Firestore Error", "Error getting document: " + task.getException());
                        }
                    }
                });
    }

    public static void getUserData(String userUid, CommentCallBack callBack, Activity activity) {

        if (userUid == null) {
            MyCustomDialog.showDialog("User not found", "Please try again later", activity);
            return;
        }
        FIRESTORE.collection("users").document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult();
                    callBack.onGetProfile(task.getResult());
                }
            }

        });
    }

    public static void checkAndCreateChatRoom(String chatRoomId, String userUid1, String userUid2) {
        DocumentReference reference = FIRESTORE.collection("chatrooms").document(chatRoomId);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        Map<String, Object> chatRoomData = new HashMap<>();
                        chatRoomData.put("chatUsers", Arrays.asList(userUid1, userUid2));
                        chatRoomData.put("lastMessageTimeStamp", Timestamp.now());
                        chatRoomData.put("lastMessage", "");
                        chatRoomData.put("lastMessageSenderUid", "");

                        reference.set(chatRoomData);
                    }
                }
            }
        });
    }

    public static void uploadChatMessage(String chatroomUid, String message, String senderUid, ChatRoomCallBack chatRoomCallBack) {

        Map<String, Object> chatroomData = new HashMap<>();
        chatroomData.put("lastMessage", message);
        chatroomData.put("lastMessageSenderUid", senderUid);
        chatroomData.put("lastMessageTimeStamp", Timestamp.now());
        FIRESTORE.collection("chatrooms").document(chatroomUid).update(chatroomData);

        Map<String, Object> chatMessageData = new HashMap<>();
        chatMessageData.put("senderUid", senderUid);
        chatMessageData.put("message", message);
        chatMessageData.put("timestamp", Timestamp.now());
        FIRESTORE.collection("chatrooms").document(chatroomUid).collection("chats").add(chatMessageData).addOnCompleteListener(
                new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            chatRoomCallBack.uploadSuccess();
                        } else {
                            chatRoomCallBack.uploadFailed();
                        }
                    }
                }
        );
    }

    public static void getAllChatMessages(String chatRoomId, ChatRoomCallBack chatRoomCallBack) {
        FIRESTORE.collection("chatrooms").document(chatRoomId).collection("chats")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            chatRoomCallBack.getAllMessagesSuccess(task.getResult());
                        }
                    }
                });
    }

    public static void listenForRecentChats(String userUid, RecentChatCallBack callBack) {
        FIRESTORE.collection("chatrooms")
                .whereArrayContains("chatUsers", userUid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    if (value != null) {
                        callBack.onRecentChatGet(value);
                    }
                });
    }
}
