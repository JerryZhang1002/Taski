package com.veggies.android.custom;

/**
 * Created by JerryCheung on 4/20/16.
 */
public class DrawerItem {
    private int imageId;
    private String text;

    public DrawerItem(int imageId, String text) {
        this.imageId = imageId;
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int newId) {
        this.imageId = newId;
    }

    public String getText() {
        return text;
    }

    public void setText(String newText) {
        this.text = newText;
    }
}
