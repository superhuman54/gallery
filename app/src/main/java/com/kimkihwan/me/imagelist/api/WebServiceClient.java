package com.kimkihwan.me.imagelist.api;

import android.content.Context;

import com.kimkihwan.me.imagelist.logger.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jamie on 1/13/17.
 */

public class WebServiceClient {

    public static HttpURLConnection connect(String stringUrl) throws IOException {
        HttpURLConnection connection = null;
        URL url = new URL(stringUrl);
        connection = (HttpURLConnection) url.openConnection();

        if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
            return connection;
        }
        return connection;
    }
}
