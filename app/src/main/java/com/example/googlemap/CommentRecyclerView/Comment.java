package com.example.googlemap.CommentRecyclerView;

public class Comment {

    private String userUID,profileUrl,username,comment, postId, commentId;

    public Comment(String userUID,String profileUrl, String username, String comment) {
        this.profileUrl = profileUrl;
        this.username = username;
        this.comment = comment;
        this.userUID=userUID;
    }

    public Comment(String userUID, String profileUrl, String username, String comment, String postId, String commentId) {
        this.userUID = userUID;
        this.profileUrl = profileUrl;
        this.username = username;
        this.comment = comment;
        this.postId = postId;
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
