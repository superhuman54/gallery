package com.kimkihwan.me.imagelist.api;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kimkihwan.me.imagelist.logger.Log;
import com.kimkihwan.me.imagelist.model.ImageData;
import com.kimkihwan.me.imagelist.model.ImageDataAdapter;
import com.kimkihwan.me.imagelist.model.ImageDataJsonParser;
import com.kimkihwan.me.imagelist.model.ImageFetcher;
import com.kimkihwan.me.imagelist.model.ImageFetcherParam;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by jamie on 1/12/17.
 */

public class ImageDataFetcher
        extends AsyncTask<RecyclerView, Void, ImageData> {

    private final static String URL = "http://ticketlink.dn.toastoven.net/mobile/etc/frameImages8.json";

    private Context context;

    private RecyclerView recyclerView;

    public ImageDataFetcher(Context context) {
        this.context = context;
    }

    @Override
    protected ImageData doInBackground(RecyclerView... params) {
        recyclerView = params[0];

        HttpURLConnection connection = null;
        BufferedInputStream in = null;
        ImageData imageData = null;
        try {
            connection = WebServiceClient.connect(URL);
            in = new BufferedInputStream(connection.getInputStream(), 1024 * 128);

            imageData = new ImageDataJsonParser().parse(in);

            Log.d(this, imageData.toString());

        } catch (IOException e) {
            Log.e(this, "IOException", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                    // do something meaningless
                }
            }
        }
        return imageData;
    }

    @Override
    protected void onPostExecute(final ImageData data) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        final ImageDataAdapter adapter = new ImageDataAdapter(context, data, recyclerView);
        adapter.setOnLoadMoreListener(new ImageDataAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ImageFetcherParam newParam =
                        new ImageFetcherParam(adapter,
                                recyclerView);

                ImageFetcher fetcher = new ImageFetcher();
                fetcher.execute(newParam);
            }
        });
        recyclerView.setAdapter(adapter);
        ImageFetcherParam param = new ImageFetcherParam(
                adapter,
                recyclerView);

        adapter.setLoading(true);
        final ImageFetcher fetcher = new ImageFetcher();
        fetcher.execute(param);
    }
}
