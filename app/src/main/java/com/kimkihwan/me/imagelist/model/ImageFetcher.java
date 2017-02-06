package com.kimkihwan.me.imagelist.model;

import android.os.AsyncTask;

/**
 * Created by jamie on 1/14/17.
 */

public class ImageFetcher
        extends AsyncTask<ImageFetcherParam, Void, ImageFetcherParam> {

    @Override
    protected ImageFetcherParam doInBackground(ImageFetcherParam... params) {
        ImageFetcherParam param = params[0];
        param.adapter.loadNextPage();
        return param;
    }

    @Override
    protected void onPostExecute(final ImageFetcherParam param) {
        param.adapter.setLoading(false);
        param.adapter.notifyDataSetChanged();
    }

}
