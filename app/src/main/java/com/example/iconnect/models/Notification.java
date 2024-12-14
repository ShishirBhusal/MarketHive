package com.example.iconnect.models;

public class Notification {
    private String notificationBy;
    private String type;
    private String postId;
    private String notificationId;
    private String postedBy;
    private long notificationAt;
    private boolean checkOpen;


    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }


    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public long getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(long notificationAt) {
        this.notificationAt = notificationAt;
    }

    public boolean isCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(boolean checkOpen) {
        this.checkOpen = checkOpen;
    }

    public Notification() {
    }

    public Notification(String notificationBy, String type, String postId, String postedBy, long notificationAt, boolean checkOpen) {
        this.notificationBy = notificationBy;
        this.type = type;
        this.postId = postId;
        this.postedBy = postedBy;
        this.notificationAt = notificationAt;
        this.checkOpen = checkOpen;
    }

}
