package com.kimkihwan.me.imagelist.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kimkihwan.me.imagelist.R;

/**
 * Created by jamie on 1/14/17.
 */

public class ProgressViewHolder
        extends BaseViewHolder<Void> {

    private ProgressBar bar;

    public ProgressViewHolder(View itemView) {
        super(itemView);

        bar = (ProgressBar) itemView.findViewById(R.id.progressBar);
    }

    @Override
    public void onBindView(Void item) {
        bar.setIndeterminate(true);
    }

    public static ProgressViewHolder newInstance(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress, parent, false);
        return new ProgressViewHolder(v);
    }
}
