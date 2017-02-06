package com.kimkihwan.me.imagelist.model;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.kimkihwan.me.imagelist.api.ImageDownloader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jamie on 1/14/17.
 */

public class ImageDataAdapter
        extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ImageData data;

    private OnLoadMoreListener listener;

    private int lastVisibleItem;
    private int nextPageFirstPosition;

    private boolean isLoading;

    private final static int NUM_ITEMS_PER_PAGE = 10;
    private final int maxPage;
    private int loadedPage = -1;

    private ImageLoader loader;
    private final static long LATCH_TIMEOUT_SECONDS = 60 * 10;
    private Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;

    public ImageDataAdapter(Context context, ImageData data, RecyclerView view) {
        this.context = context;
        this.loader = new ImageLoader(context);
        this.data = data;
        this.maxPage = (data.size() - 1) / NUM_ITEMS_PER_PAGE;
        final LinearLayoutManager manager = (LinearLayoutManager) view.getLayoutManager();
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItem = manager.findLastVisibleItemPosition();

                nextPageFirstPosition = (loadedPage + 1) * NUM_ITEMS_PER_PAGE;
                if (!isLoading && nextPageFirstPosition == (lastVisibleItem + 1)) {
                    if (listener != null) {
                        listener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ImageViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ImageData.Tuple tuple = data.getTupleAt(position);
        ImageViewData imageViewData =
                new ImageViewData(loader.getBitmap(tuple.imageUrlIndex),
                        tuple.color);
        holder.onBindView(imageViewData);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (loadedPage == maxPage) {
            count = data.size();
        } else if (loadedPage < 0) {
            count = 0;
        } else {
            count = (loadedPage + 1)* NUM_ITEMS_PER_PAGE;
        }
        return count;
    }

    public int getLoadedPage() {
        return loadedPage;
    }

    public void loadNextPage() {
        if (loadedPage >= maxPage)
            return;

        int firstItem = (loadedPage + 1) * NUM_ITEMS_PER_PAGE;
        int lastItem = firstItem + NUM_ITEMS_PER_PAGE;

        Map<String, Integer> urls = new HashMap<>();
        for (int i = firstItem; i < lastItem; i++) {
            int index = data.getImageUrlIndex(i);
            if (loader.isCached(index))
                continue;
            urls.put(data.getUrl(index), index);
        }

        if (!urls.isEmpty()) {
            // barrier pattern
            CountDownLatch latch = new CountDownLatch(urls.size());
            for (String key : urls.keySet()) {
                File out = loader.create(urls.get(key));
                new ImageDownloader(out, latch)
                        .executeOnExecutor(executor, key);
            }

            try {
                if (latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    loadedPage++;
                }
            } catch (InterruptedException e) {

            }
        } else {
            loadedPage++;
        }
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.listener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
