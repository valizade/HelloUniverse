package com.valizade.hellouniverse.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

  private int mPreviousTotal = 0;
  private boolean mLoading = true;

  @Override
  public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    int visibleItemCount = recyclerView.getChildCount();
    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
        .findFirstVisibleItemPosition();

    if (mLoading) {
      if (totalItemCount > mPreviousTotal) {
        mLoading = false;
        mPreviousTotal = totalItemCount;
      }
    }
    int visibleThreshold = 5;
    if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
      // End has been reached
      onLoadMore();
      mLoading = true;
    }
  }

  public abstract void onLoadMore();
}