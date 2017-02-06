package com.kimkihwan.me.imagelist.model;

import android.graphics.Bitmap;

/**
 * Created by jamie on 1/15/17.
 */

public class ImageViewData {

    Bitmap bitmap;
    int color;

    public ImageViewData(Bitmap bitmap, int color) {
        this.bitmap = bitmap;
        this.color = color;
    }
}
