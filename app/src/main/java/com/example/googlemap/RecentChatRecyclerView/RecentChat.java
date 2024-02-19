package com.example.googlemap.RecentChatRecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecentChat implements Comparable<RecentChat>{

    private String message, senderUid, lastSenderUid,formattedTimestamp,anotherUsername, anotherUserProfileUrl;
    private Timestamp timestamp;


    public RecentChat(String message, String senderUid, Timestamp timeStamp, String lastSenderUid) {
        this.message = message;
        this.senderUid = senderUid;
        this.timestamp = timeStamp;
        this.lastSenderUid = lastSenderUid;
        this.formattedTimestamp = formatTimestamp(timeStamp);
    }

    public void setFormattedTimestamp(String formattedTimestamp) {
        this.formattedTimestamp = formattedTimestamp;
    }

    public String getAnotherUsername() {
        return anotherUsername;
    }

    public void setAnotherUsername(String anotherUsername) {
        this.anotherUsername = anotherUsername;
    }

    public String getAnotherUserProfileUrl() {
        return anotherUserProfileUrl;
    }

    public void setAnotherUserProfileUrl(String anotherUserProfileUrl) {
        this.anotherUserProfileUrl = anotherUserProfileUrl;
    }

    public String getLastSenderUid() {
        return lastSenderUid;
    }

    public void setLastSenderUid(String lastSenderUid) {
        this.lastSenderUid = lastSenderUid;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }

    private String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    public int compareTo(RecentChat other) {
        if (other == null || other.getTimestamp() == null || this.getTimestamp() == null) {
            return 0;
        }
        return other.getTimestamp().compareTo(this.getTimestamp());
    }

}