package com.kimkihwan.me.imagelist.model;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kimkihwan.me.imagelist.R;

/**
 * Created by jamie on 1/14/17.
 */

public class ImageViewHolder
        extends BaseViewHolder<ImageViewData> {

    private ImageView image;

    public ImageViewHolder(View itemView) {
        super(itemView);

        image = (ImageView) itemView.findViewById(R.id.imageUrl);
    }

    @Override
    public void onBindView(ImageViewData item) {
        image.setImageBitmap(item.bitmap);
        GradientDrawable background = (GradientDrawable) image.getBackground();
        String hex = String.format("#%06X", (0xFFFFFF & item.color));
        background.setColor(Color.parseColor(hex));
    }

    public static ImageViewHolder newInstance(ViewGroup parent) {
        View v =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(v);
    }

}
