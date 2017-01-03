package com.lhk.pullloadmorerecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lhk.library.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment implements PullLoadMoreRecyclerView.PullActionListener {

    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private int mCount = 1;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    //private RecyclerView mRecyclerView;
    List<String> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) view.findViewById(R.id.pullLoadMoreRecyclerView);
        //获取mRecyclerView对象
        //mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        //mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置是否可以下拉刷新
        //mPullLoadMoreRecyclerView.setPullRefreshEnable(false);
        //设置是否可以上拉刷新
        //mPullLoadMoreRecyclerView.setPullLoadMoreEnable(false);

        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText("loading");
        //设置上拉刷新文字颜色
        mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();

        mPullLoadMoreRecyclerView.setOnPullActionListener(this);
        //setEmptyView，演示空数据，可以提示“数据加载中”
        mPullLoadMoreRecyclerView.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.empty_view, null));
        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity());
        mPullLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
        getData();
        setPullActionCompleted();
    }

    private void getData() {
        mRecyclerViewAdapter.addAllData(setList());
    }

    private void setPullActionCompleted(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullLoadMoreRecyclerView.setPullActionCompleted();
                    }
                });

            }
        }, 1000);
    }

    public void clearData() {
        mRecyclerViewAdapter.clearData();
        mRecyclerViewAdapter.notifyDataSetChanged();
    }


    private List<String> setList() {
        dataList = new ArrayList<>();
        int start = 20 * (mCount - 1);
        for (int i = start; i < 20 * mCount; i++) {
            dataList.add("Frist" + i);
        }
        return dataList;
    }

    @Override
    public void onRefresh() {
        Log.e("wxl", "onRefresh");
        //setRefresh();
        //getData();
        setRefreshDiff();
        setPullActionCompleted();
    }

    @Override
    public void onLoadMore() {
        Log.e("wxl", "onLoadMore");
        mCount = mCount + 1;
        getData();
        setPullActionCompleted();
    }

    private void setRefresh() {
        mRecyclerViewAdapter.clearData();
        mCount = 1;
    }

    private void setRefreshDiff(){
        List<String> newDatas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            newDatas.add("Item"+i);
        }
        //利用DiffUtil.calculateDiff()方法，传入一个规则DiffUtil.Callback对象，
        // 和是否检测移动item的 boolean变量，得到DiffUtil.DiffResult 的对象
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(dataList, newDatas), true);

        //利用DiffUtil.DiffResult对象的dispatchUpdatesTo（）方法，
        // 传入RecyclerView的Adapter，轻松成为文艺青年
        diffResult.dispatchUpdatesTo(mRecyclerViewAdapter);

        //别忘了将新数据给Adapter
        mRecyclerViewAdapter.setDataList(newDatas);
    }

    class DiffCallBack extends DiffUtil.Callback{

        List<String> mOldDatas,mNewDatas;

        public DiffCallBack(List<String> oldDatas,List<String> newDatas) {
            this.mOldDatas = oldDatas;
            this.mNewDatas = newDatas;
        }

        @Override
        public int getOldListSize() {
            return mOldDatas != null ? mOldDatas.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewDatas != null ? mNewDatas.size() : 0;
        }

        /**
         * 被DiffUtil调用，用来判断 两个对象是否是相同的Item
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldDatas.get(oldItemPosition).equals(mNewDatas.get(newItemPosition));
        }

        /**
         * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            String strOld = mOldDatas.get(oldItemPosition);
            String strNew = mNewDatas.get(newItemPosition);
            if (!strOld.equals(strNew)){
                return false;//如果有内容不同，就返回false
            }
            return true;//默认两个Data内容是相同的
        }
    }
}
