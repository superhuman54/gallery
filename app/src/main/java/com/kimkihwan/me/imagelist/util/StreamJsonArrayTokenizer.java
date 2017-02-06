package com.kimkihwan.me.imagelist.util;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.kimkihwan.me.imagelist.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by jamie on 1/13/17.
 */

/**
 * Jackson tokenizer 파싱 결과 300,000개의 객체를 생성하여 오랜 시간이 걸림. (약 40초)
 *
 *  #USAGE
 *
 *  StreamJsonArrayTokenizer tokenizer = new StreamJsonArrayTokenizer.Builder()
        .setSource(in)
        .build();

 if (tokenizer.isExpectedStartArrayToken()) {
    while (tokenizer.hasNext()) {
        StreamJsonArrayTokenizer.Element e = tokenizer.next();
    }
 */
@Deprecated
public class StreamJsonArrayTokenizer
        implements Iterator<StreamJsonArrayTokenizer.Element> {

    private JsonParser parser;
    private Element element = null;

    private StreamJsonArrayTokenizer(Builder builder) {
        this.parser = builder.parser;

        this.element = new Element();
    }

    public JsonToken nextToken() throws IOException {
        return parser.nextToken();
    }

    public boolean isExpectedStartArrayToken() throws IOException {
        return nextToken() == JsonToken.START_ARRAY;
    }

    @Override
    public boolean hasNext() {
        try {
            return nextToken() != JsonToken.END_ARRAY;
        } catch (IOException ignored) {

        }
        return false;
    }

    @Override
    public Element next() {
        if (element == null) {
            element = new Element();
        }
        try {
            element.node = parser.readValueAsTree();
        } catch (IOException e) {
            Log.e(this, "Failed to parse", e);
        }
        return element;
    }

    @Deprecated
    public static class Element {
        JsonNode node;

        public String getValue(String key) {
            return node.get(key).toString();
        }
    }

    public static class Builder<S extends InputStream> {
        private JsonFactory factory;
        private JsonParser parser;
        private S src;

        public Builder() {
            this.factory = new MappingJsonFactory();
        }

        public Builder setSource(S src) throws IOException {
            this.src = src;
            this.parser = factory.createParser(src);
            return this;
        }

        public StreamJsonArrayTokenizer build() {
            return new StreamJsonArrayTokenizer(this);
        }
    }
}
