package com.compscitutorials.basigarcia.webalbum;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Leo-Yang on 15/12/12.
 */
public class PicturesName {
    public static ArrayList<String> picture_name = new ArrayList<String>();
    public static ArrayList<Bitmap> pictures = new ArrayList<Bitmap>();
    public static boolean tag;
    public static int length;
    public static boolean upload = false;

    public static void resetPicturesCache(){
        picture_name = new ArrayList<String>();
        pictures = new ArrayList<Bitmap>();
    }
}
