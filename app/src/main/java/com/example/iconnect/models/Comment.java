package com.example.iconnect.models;

public class Comment {
    private String commentBody,commentedBy;
    private long commentedAt;

    public Comment() {
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public long getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(long commentedAt) {
        this.commentedAt = commentedAt;
    }

    public Comment(String commentBody, String commentedBy, long commentedAt) {
        this.commentBody = commentBody;
        this.commentedBy = commentedBy;
        this.commentedAt = commentedAt;
    }
}
