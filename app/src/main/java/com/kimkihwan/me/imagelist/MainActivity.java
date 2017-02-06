package com.kimkihwan.me.imagelist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.kimkihwan.me.imagelist.api.ImageDataFetcher;
import com.kimkihwan.me.imagelist.logger.Log;

import java.io.File;

public class MainActivity extends Activity {

    private ImageDataFetcher request;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        for (File child : getCacheDir().listFiles()) {
            Log.d(this, "file to delete: " + child.getAbsolutePath() + ", deleted: " + child.delete());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        request = new ImageDataFetcher(this);
        request.execute(recyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (request != null && !request.isCancelled()) {
            request.cancel(true);
        }
    }

}
