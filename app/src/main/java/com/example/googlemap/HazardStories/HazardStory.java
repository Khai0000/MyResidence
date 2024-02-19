package com.example.googlemap.HazardStories;

public class HazardStory {

    private String postId,userUID,postUrl,userProfileUrl,title,description,username;


    public HazardStory(String postId, String userUID, String postUrl, String userProfileUrl, String title, String description, String username) {
        this.postId = postId;
        this.userUID = userUID;
        this.postUrl = postUrl;
        this.userProfileUrl = userProfileUrl;
        this.title = title;
        this.description = description;
        this.username = username;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public void setUserProfileUrl(String userProfileUrl) {
        this.userProfileUrl = userProfileUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
