package com.kimkihwan.me.imagelist.model;

import android.support.v7.widget.RecyclerView;

/**
 * Created by jamie on 1/14/17.
 */

public class ImageFetcherParam {

    ImageDataAdapter adapter;
    RecyclerView view;

    public ImageFetcherParam(ImageDataAdapter adapter, RecyclerView view) {
        this.adapter = adapter;
        this.view = view;
    }

}
