package com.schintha.nytimessearch.listeners;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by sc043016 on 7/28/16.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = EndlessRecyclerViewScrollListener.class.getSimpleName();
    public StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int visibleThreshold = 5;
    private int lastVisibleItemPosition = 0;
    private boolean loading = true;
    private int currentPage = 0;

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.staggeredGridLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * staggeredGridLayoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int[] lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
        lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = staggeredGridLayoutManager.getItemCount();

        if (totalItemCount < lastVisibleItemPosition) {
            lastVisibleItemPosition = totalItemCount;
            if (totalItemCount == 0) loading = true;
        }

        if (loading && (totalItemCount > lastVisibleItemPosition)) {
            loading = false;
            currentPage++;
        }

        if (!loading && (lastVisibleItemPosition + visibleItemCount + visibleThreshold >= totalItemCount)) {
            //Log.i(TAG, "LOADPAGE last " + lastVisibleItemPosition + " visible " + visibleItemCount + " total " + totalItemCount);
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }
    }

    public abstract boolean onLoadMore(int page, int totalItemsCount);
}