package com.lhk.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressWarnings("unused")
public class PullLoadMoreRecyclerView extends LinearLayout {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PullActionListener mPullActionListener;
    private boolean hasMore = true;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean pullRefreshEnable = true;//是否支持下拉刷新
    private boolean pullLoadMoreEnable = true;//是否支持上划加载

    private View mFooterView;
    private FrameLayout mEmptyViewContainer;
    private Context mContext;
    private TextView loadMoreText;
    private LinearLayout loadMoreLayout;
    private EmptyAdapterDataObserver mEmptyDataObserver;

    public PullLoadMoreRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public PullLoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.pull_loadmore_layout, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark
                , android.R.color.holo_blue_dark
                , android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(new PullRefreshOnRefreshListener(this));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setVerticalScrollBarEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new PullLoadOnScrollListener(this));

        mRecyclerView.setOnTouchListener(new onTouchRecyclerView());

        mFooterView = view.findViewById(R.id.footerView);
        mEmptyViewContainer = (FrameLayout) view.findViewById(R.id.emptyView);

        loadMoreLayout = (LinearLayout) view.findViewById(R.id.loadMoreLayout);
        loadMoreText = (TextView) view.findViewById(R.id.loadMoreText);

        mFooterView.setVisibility(View.GONE);
        mEmptyViewContainer.setVisibility(View.GONE);

        this.addView(view);
    }

    /**
     * LinearLayoutManager
     */
    public void setLinearLayout() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /**
     * GridLayoutManager
     */
    public void setGridLayout(int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, spanCount);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }


    /**
     * StaggeredGridLayoutManager
     */
    public void setStaggeredGridLayout(int spanCount) {
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecyclerView.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        mRecyclerView.addItemDecoration(decor, index);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        mRecyclerView.addItemDecoration(decor);
    }

    public void scrollToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    public void setEmptyView(View emptyView) {
        mEmptyViewContainer.removeAllViews();
        mEmptyViewContainer.addView(emptyView);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mEmptyDataObserver == null) {
            mEmptyDataObserver = new EmptyAdapterDataObserver();
        }
        RecyclerView.Adapter oldAdapter = mRecyclerView.getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mEmptyDataObserver);
        }
        if (adapter != null) {
            mRecyclerView.setAdapter(adapter);
            showEmptyView();
            adapter.registerAdapterDataObserver(mEmptyDataObserver);
        }
    }

    public void showEmptyView() {
        RecyclerView.Adapter oldAdapter = mRecyclerView.getAdapter();
        if (oldAdapter != null && mEmptyViewContainer.getChildCount() != 0) {
            if (oldAdapter.getItemCount() == 0) {
                mFooterView.setVisibility(View.GONE);
                mEmptyViewContainer.setVisibility(VISIBLE);
            } else {
                mEmptyViewContainer.setVisibility(GONE);
            }
        }
    }

    /**
     * When view detached from window , unregister adapter data observer, avoid momery leak.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RecyclerView.Adapter oldAdapter = mRecyclerView.getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mEmptyDataObserver);
        }
    }

    /**
     * This Observer receives adapter data change.
     * When adapter's item count greater than 0 and empty view has been set,then show the empty view.
     * when adapter's item count is 0 ,then empty view hide.
     */
    private class EmptyAdapterDataObserver extends android.support.v7.widget.RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            showEmptyView();
        }
    }

    public void setPullRefreshEnable(boolean enable) {
        this.pullRefreshEnable = enable;
        setSwipeRefreshEnable(enable);
    }

    public boolean getPullRefreshEnable() {
        return pullRefreshEnable;
    }

    private void setSwipeRefreshEnable(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    private boolean getSwipeRefreshEnable() {
        return mSwipeRefreshLayout.isEnabled();
    }

    public void setColorSchemeResources(int... colorResIds) {
        mSwipeRefreshLayout.setColorSchemeResources(colorResIds);
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public void setRefreshing(final boolean isRefreshing) {
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                if (pullRefreshEnable)
                    mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });

    }

    /**
     * Solve IndexOutOfBoundsException exception
     */
    public class onTouchRecyclerView implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return isRefresh || isLoadMore;
        }
    }

    public boolean isPullLoadMoreEnable() {
        return pullLoadMoreEnable;
    }

    public void setPullLoadMoreEnable(boolean pullLoadMoreEnable) {
        this.pullLoadMoreEnable = pullLoadMoreEnable;
    }

    public LinearLayout getFooterViewLayout() {
        return loadMoreLayout;
    }

    public void setFooterViewBackgroundColor(int color) {
        loadMoreLayout.setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    public void setFooterViewText(CharSequence text) {
        loadMoreText.setText(text);
    }

    public void setFooterViewText(int resid) {
        loadMoreText.setText(resid);
    }

    public void setFooterViewTextColor(int color) {
        loadMoreText.setTextColor(ContextCompat.getColor(mContext, color));
    }

    public void refresh() {
        if (mPullActionListener != null && isRefresh && !isLoadMore) {
            mPullActionListener.onRefresh();
        }
    }

    public void loadMore() {
        if (mPullActionListener != null && hasMore && isLoadMore && !isRefresh) {
            mFooterView.animate()
                    .translationY(0)
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mFooterView.setVisibility(View.VISIBLE);
                        }
                    })
                    .start();
            invalidate();
            mPullActionListener.onLoadMore();
        }
    }

    public void setPullActionCompleted() {
        if (isRefresh){
            isRefresh = false;
            setRefreshing(false);
        }

        if (isLoadMore){
            isLoadMore = false;
            mFooterView.animate()
                    .translationY(mFooterView.getHeight())
                    .setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    public void setOnPullActionListener(PullActionListener listener) {
        this.mPullActionListener = listener;
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
        setRefreshing(isRefresh);
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public interface PullActionListener {
        void onRefresh();

        void onLoadMore();
    }
}
