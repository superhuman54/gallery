package com.kimkihwan.me.imagelist.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jamie on 1/13/17.
 */

public class ImageData
        implements Iterator<ImageData.Tuple>{

    public static class Tuple {
        final int color;
        final int imageUrlIndex;

        public Tuple(int color, int imageUrlIndex) {
            this.color = color;
            this.imageUrlIndex = imageUrlIndex;
        }
    }

    private final int[] colors;
    private final int[] urlIndices;
    private final String[] urlLookupTable;

    private boolean iterStarted;
    private int iterIndex;

    public ImageData(int[] colors, int[] urlIndices, String[] urlLookupTable) {
        this.colors = colors;
        this.urlIndices = urlIndices;
        this.urlLookupTable = urlLookupTable;
    }

    public int size() {
        return colors.length;
    }

    public String getUrl(int index){
        return urlLookupTable[index];
    }

    public int getImageUrlIndex(int position) {
        return urlIndices[position];
    }

    @Override
    public boolean hasNext() {
        if(iterStarted){
            iterIndex++;
        } else {
            iterStarted = true;
            iterIndex = 0;
        }

        return colors.length > iterIndex;
    }

    public Tuple getTupleAt(int index) {
        if (index < 0)
            throw new IllegalArgumentException("index must be equal to or greater than 0");
        return new Tuple(colors[index], urlIndices[index]);
    }

    @Override
    public Tuple next() {
        if(iterStarted)
            return new Tuple(colors[iterIndex], urlIndices[iterIndex]);
        throw new IllegalStateException("Iterator not started");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "size : "  + size() +", lookup url table size : " + urlLookupTable.length;
    }
}
