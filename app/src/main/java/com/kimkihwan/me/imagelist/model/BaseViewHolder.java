package com.kimkihwan.me.imagelist.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jamie on 1/14/17.
 */

public abstract class BaseViewHolder<I>
        extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(I item);
}
