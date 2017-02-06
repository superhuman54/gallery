package com.kimkihwan.me.imagelist.model;

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Bytes;
import com.kimkihwan.me.imagelist.BuildConfig;
import com.kimkihwan.me.imagelist.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by jamie on 1/13/17.
 */

public class ImageDataJsonParser {

    enum State {
        ARRAY_START,
        ARRAY_END,
        OBJECT_START,
        OBJECT_END,
        KEY_STRING_START,
        KEY_STRING_END,
        VALUE_STRING_START,
        VALUE_STRING_END
    }
    private final static int SIZE_LIMIT = 100000;

    private final static byte[] KEY_COLOR = new byte[]{'c', 'o', 'l', 'o', 'r'};
    private final static byte[] KEY_IMAGE_URL = new byte[]{'i', 'm', 'a', 'g', 'e', 'U', 'r', 'l'};

    private byte[] buffer = new byte[1024 * 128];
    private byte[] data = new byte[1024];
    private int dataIndex = 0;

    private int[] colors = new int[SIZE_LIMIT];
    private int[] urlIndices = new int[SIZE_LIMIT];
    private int index = 0;
    private TreeMap<byte[], Integer> urlLookupTable;
    private int urlLookupTableIndex = 0;

    public ImageDataJsonParser() {
        urlLookupTable = new TreeMap<>(new LexicographicalComparator());
    }

    private class LexicographicalComparator
            implements Comparator<byte[]> {

        @Override
        public int compare(byte[] o1, byte[] o2) {
            if (o1 == data) {
                // for containsKey method
                return compare(o1, KEY_IMAGE_URL.length, dataIndex - KEY_IMAGE_URL.length, o2, 0, o2.length);
            } else if (o2 == data) {
                // for containsKey method
                return compare(o1, 0, o1.length, o2, KEY_IMAGE_URL.length, dataIndex - KEY_IMAGE_URL.length);
            } else {
                // for put method
                return compare(o1, 0, o1.length, o2, 0, o2.length);
            }
        }

        public int compare(byte[] left, int leftstart, int leftlen, byte[] right, int rightstart, int rightlen) {
            int min = Math.min(leftlen, rightlen);
            for (int i = 0; i < min; i++) {
                int result = left[leftstart + i] - right[rightstart + i];
                if (result != 0) {
                    return result;
                }
            }
            return leftlen - rightlen;
        }
    }

    public ImageData parse(InputStream in) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        int len = 0;
        State state = null;

        Log.d(this, "start");
        while ((len = in.read(buffer)) > 0) {
            for (int i = 0; i < len; i++) {
                if (Character.isWhitespace(buffer[i]))
                    continue;

                switch (buffer[i]) {
                    case '[':
                        state = State.ARRAY_START;
                        break;
                    case ']':
                        state = State.ARRAY_END;
                        break;
                    case '{':
                        state = State.OBJECT_START;
                        break;
                    case '}':
                        state = State.OBJECT_END;
                        parseKeyValue();
                        break;
                    case '"':
                        switch (state) {
                            case OBJECT_START:
                                state = State.KEY_STRING_START;
                                break;
                            case KEY_STRING_START:
                                state = State.KEY_STRING_END;
                                break;
                            case KEY_STRING_END:
                                state = State.VALUE_STRING_START;
                                break;
                            case VALUE_STRING_START:
                                state = State.VALUE_STRING_END;
                                break;
                            case VALUE_STRING_END:
                                state = State.KEY_STRING_START;
                                break;
                        }
                        break;
                    case ':':
                        switch (state) {
                            case KEY_STRING_START:
                            case VALUE_STRING_START:
                                data[dataIndex++] = buffer[i];
                                break;
                        }
                        break;
                    case ',':
                        if (state == State.VALUE_STRING_END)
                            parseKeyValue();
                        dataIndex = 0;
                        break;
                    default:
                        data[dataIndex++] = buffer[i];
                        break;
                }
            }
        }
        stopwatch.stop();

        Log.d(this, "finished. elapsed time : " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

        Set<Map.Entry<byte[], Integer>> entrySet = urlLookupTable.entrySet();
        String[] arrayUrlLookupTable = new String[entrySet.size()];

        for (Map.Entry<byte[], Integer> entry : entrySet) {
            arrayUrlLookupTable[entry.getValue()] = new String(entry.getKey());
        }

        if (BuildConfig.DEBUG) {
            for (int i = 0; i < 10; i++) {
                int color = colors[i];
                int urlIndex = urlIndices[i];
                String url = arrayUrlLookupTable[urlIndex];

                Log.d(this, "color : #" + Long.toHexString(color) + ", urlIndex: " + urlIndex + ", url : " + url);
            }
        }

        return new ImageData(colors, urlIndices, arrayUrlLookupTable);
    }

    private void parseKeyValue() {
        //over 100,000
        if (index >= colors.length)
            return;

        if (Bytes.indexOf(data, KEY_COLOR) == 0) {
            int color = parseColor(data, KEY_COLOR.length + 1, dataIndex - KEY_COLOR.length - 1);
            colors[index] = color;
        } else if (Bytes.indexOf(data, KEY_IMAGE_URL) == 0) {

            int urlIndex;
            if (urlLookupTable.containsKey(data)) {
                urlIndex = urlLookupTable.get(data);
            } else {
                /** 이미지 주소 종류의 개수만큼 배열 복사가 일어난다.
                 *  예를 들어, 이미지 주소 종류가 200개면 200번의 byte 배열 인스턴스와 배열 복사가 일어난다.
                  */
                byte[] url = new byte[dataIndex - KEY_IMAGE_URL.length];
                System.arraycopy(data, KEY_IMAGE_URL.length, url, 0, dataIndex - KEY_IMAGE_URL.length);
                urlLookupTable.put(url, urlLookupTableIndex);
                Log.d(this, new String(url) + ", " + urlLookupTableIndex);
                urlIndex = urlLookupTableIndex;
                urlLookupTableIndex++;
            }

            urlIndices[index++] = urlIndex;
        }
    }

    private static int parseColor(byte[] s, int start, int len)
            throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        int radix = 16;
        long result = 0;
        int i = 0;
        int digit;

        if (len > 0) {
            while (i < len) {
                digit = Character.digit(s[start + i], radix);
                i++;
                if (digit < 0) {
                    throw new NumberFormatException();
                }
                result *= radix;
                result -= digit;
            }
        } else {
            throw new NumberFormatException();
        }
        return (int) -result;
    }

}
