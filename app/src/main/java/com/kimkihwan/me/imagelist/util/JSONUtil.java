package com.kimkihwan.me.imagelist.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by jamie on 1/12/17.
 */

public class JSONUtil {

    public static <T> List<T> toObjectList(String src, Class<T> clazz) {
        try {
            return new ObjectMapper()
                    .readValue(src, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException ignored) {

        }
        return null;
    }

    public static <T> List<T> toObjectList(InputStream in, Class<T> clazz) {
        try {
            return new ObjectMapper()
                    .readValue(in, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException ignored) {

        }
        return null;
    }
}
