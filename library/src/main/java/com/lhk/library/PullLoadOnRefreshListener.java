package com.lhk.library;

import android.support.v4.widget.SwipeRefreshLayout;


public class PullLoadOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    public PullLoadOnRefreshListener(PullLoadMoreRecyclerView pullLoadMoreRecyclerView) {
        this.mPullLoadMoreRecyclerView = pullLoadMoreRecyclerView;
    }

    @Override
    public void onRefresh() {
        if (!mPullLoadMoreRecyclerView.isRefresh()) {
            mPullLoadMoreRecyclerView.setIsRefresh(true);
            mPullLoadMoreRecyclerView.refresh();
        }
    }
}
