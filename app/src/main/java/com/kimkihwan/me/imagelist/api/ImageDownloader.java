package com.kimkihwan.me.imagelist.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.common.base.Stopwatch;
import com.kimkihwan.me.imagelist.logger.Log;
import com.kimkihwan.me.imagelist.util.Utils;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by jamie on 1/14/17.
 */

public class ImageDownloader
        extends AsyncTask<String, Void, Bitmap>{

    private final static String TEMP_FILENAME = "TEMP";

    private File out;
    private CountDownLatch latch;

    public ImageDownloader(File out, CountDownLatch latch) {
        this.out = out;
        this.latch = latch;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = params[0];
        Bitmap scaled = null;
        BufferedOutputStream os = null;
        BufferedInputStream bis = null;
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            HttpURLConnection connection = WebServiceClient.connect(url);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 16;
            bis = new BufferedInputStream(connection.getInputStream(), 1024 * 16);
            Bitmap bitmap = BitmapFactory.decodeStream(bis, null, opts);

            os = new BufferedOutputStream(new FileOutputStream(out));
            boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            scaled = bitmap;

            Log.d(this, "success compressed: " + result +
                    ", saved file: " + out.getAbsolutePath() +
                    ", time: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));

        } catch (IOException e) {
            Log.e(this, "Failed to download image", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {

                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {

                }
            }
            latch.countDown();
        }
        return scaled;
    }

    public Bitmap decodeSampledBitmapFromResource(File file,
                                                         int reqWidth, int reqHeight) {

        Bitmap bitmap = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BufferedInputStream in;

        try {
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            Log.d(this, "file(" + file.getAbsolutePath() + ") sample size: " + options.inSampleSize);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            in = new BufferedInputStream(new FileInputStream(file));
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } catch (FileNotFoundException e) {
            Log.e(this, "No file found", e);
        }


        return bitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static int computeSampleSize(InputStream in, int maxResolutionX,
                                         int maxResolutionY) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        int maxNumOfPixels = maxResolutionX * maxResolutionY;
        int minSideLength = Math.min(maxResolutionX, maxResolutionY) / 2;
        return Utils.computeSampleSize(options, minSideLength, maxNumOfPixels);
    }

    private File createTempFile(File ori) throws IOException {
        return File.createTempFile(FilenameUtils.getBaseName(ori.getName()) + TEMP_FILENAME,
                FilenameUtils.EXTENSION_SEPARATOR_STR.concat(FilenameUtils.getExtension(ori.getName())),
                ori.getParentFile());
    }

}
