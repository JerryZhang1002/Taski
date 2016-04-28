package com.veggies.android.custom;

/**
 * Created by JerryCheung on 2/23/16.
 */


public class ToDoItem {
    //keys for passing bundle
    public static final String TODO_TITLE = "Title";
    public static final String TODO_DESCRIPTION = "Description";
    public static final String TODO_DATE = "Date";
    public static final String TODO_COMPLETE = "Complete";
    public static final String TODO_POSITION = "Position";
    public static final String TODO_TIME_MILLIS = "Time";
    public static final String TODO_ID = "Id";
    public static final String TODO_TYPE = "Type";
    public static final String TODO_AUDIO = "audio_path";
    public static final String[] TYPE_STRING = {"Default", "Personal", "Shopping", "Wishlist", "Work"};

    private String title;
    private String description;
    private String date;
    private int complete;
    private long timeMillis;
    private int id;
    private int type;
    private String audioPath;

    public ToDoItem(int id, String title, String description, String date, long timeMillis, int complete, int type, String audioPath) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.complete = complete;
        this.timeMillis = timeMillis;
        this.type = type;
        this.audioPath = audioPath;
    }

    public ToDoItem() {
        super();
    }

    public int getId() { return id; }
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getComplete() {
        return complete;
    }

    public long getTimeMillis() { return timeMillis; }

    public int getType() {
        return type;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setId(int id) { this.id = id; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeMillis(long timeMillis){ this.timeMillis = timeMillis; }

    public void setType(int type) {
        this.type = type;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
