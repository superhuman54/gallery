package com.kimkihwan.me.imagelist.model;

/**
 * Created by jamie on 1/14/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kimkihwan.me.imagelist.logger.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ImageLoader {

    private final static String IMAGE_EXTENSION_FORMAT = "%d.jpg";
    private File cacheDir;

    private Map<Integer, String> cachedFileMap;

    private AtomicLong totalSize = new AtomicLong();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLocek = lock.writeLock();

    public ImageLoader(Context context) {
        this.cacheDir = context.getCacheDir();
        cachedFileMap = Collections.synchronizedMap(new LinkedHashMap<Integer, String>(1024));

        init();
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writeLocek.lock();
                    for (File file : cacheDir.listFiles()) {

                    }

                } finally {
                    writeLocek.unlock();
                }
            }
        }).start();
    }

    private void mapCacheFile(File file) {
//        cachedFileMap.put(file.getName(), );

    }

    public boolean isCached(int index) {
        if (cachedFileMap.containsKey(index)) {
            return true;
        }
        String filename = String.format(IMAGE_EXTENSION_FORMAT, index);
        File file = new File(cacheDir, filename);
        if (file.exists()) {
            cachedFileMap.put(index, filename);
            return true;
        }
        return false;
    }

    public File create(int index) {
        String filename = String.format(IMAGE_EXTENSION_FORMAT, index);
        return new File(cacheDir, filename);
    }

    public Bitmap getBitmap(int index) {
        String filename = String.format(IMAGE_EXTENSION_FORMAT, index);
        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(new File(cacheDir, filename));
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            Log.e(this, "file not found", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return bitmap;
    }
}
