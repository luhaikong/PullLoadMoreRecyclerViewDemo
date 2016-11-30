package com.lhk.library;

import android.support.v4.widget.SwipeRefreshLayout;


public class PullRefreshOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    public PullRefreshOnRefreshListener(PullLoadMoreRecyclerView pullLoadMoreRecyclerView) {
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
