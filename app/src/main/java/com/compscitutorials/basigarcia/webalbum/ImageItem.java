package com.compscitutorials.basigarcia.webalbum;

import android.graphics.Bitmap;

/**
 * Created by luxy on 12/12/15.
 */
public class ImageItem {
    private Bitmap image;
    private String text;

    public ImageItem(Bitmap image, String text){
        super();
        this.image = image;
        this.text = text;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }
}
