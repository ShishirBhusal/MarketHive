package com.example.iconnect.models;

import java.util.ArrayList;

public class Story {
    private String storyBy;
    private long storyAt;
    ArrayList<Userstory> userstories;

    public Story() {
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<Userstory> getUserstories() {
        return userstories;
    }

    public void setUserstories(ArrayList<Userstory> userstories) {
        this.userstories = userstories;
    }

    public Story(String storyBy, long storyAt, ArrayList<Userstory> userstories) {
        this.storyBy = storyBy;
        this.storyAt = storyAt;
        this.userstories = userstories;
    }
}
