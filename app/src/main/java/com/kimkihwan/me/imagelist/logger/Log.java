package com.kimkihwan.me.imagelist.logger;

import com.kimkihwan.me.imagelist.BuildConfig;

/**
 * Created by jamie on 1/12/17.
 */

public class Log {
    public static void v(Object obj, String message) {
        if (BuildConfig.DEBUG)
            android.util.Log.v(obj.getClass().getSimpleName(), message);
    }

    public static void i(Object obj, String message) {
        if (BuildConfig.DEBUG)
            android.util.Log.i(obj.getClass().getSimpleName(), message);
    }

    public static void d(Object obj, String message) {
        if (BuildConfig.DEBUG)
            android.util.Log.d(obj.getClass().getSimpleName(), message);
    }

    public static void e(Object obj, String message, Throwable tr) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(obj.getClass().getSimpleName(), message, tr);
    }

    public static void e(Object obj, String message) {
        if (BuildConfig.DEBUG)
            android.util.Log.e(obj.getClass().getSimpleName(), message);
    }

    public static void wtf(Object obj, String message, Throwable tr) {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(obj.getClass().getSimpleName(), message, tr);
    }

    public static void wtf(Object obj, String message) {
        if (BuildConfig.DEBUG)
            android.util.Log.wtf(obj.getClass().getSimpleName(), message);
    }
}
