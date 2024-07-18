package com.letta.entities;

public class Comment {
    private int commentId;
    private int eventId;
    private int userId;
    private String commentText;

    public Comment(int commentId, int eventId, int userId, String commentText) {
        this.commentId = commentId;
        this.eventId = eventId;
        this.userId = userId;
        this.commentText = commentText;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    // Override toString() method for easy printing
    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}
