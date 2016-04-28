package com.veggies.android.custom;

/**
 * Created by JerryCheung on 4/20/16.
 */
public class DropdownMenuItem {
    private int imageId;
    private String text;

    public DropdownMenuItem(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
    }

    public int getImageId() {
        return this.imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
