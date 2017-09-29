package com.gersion.schultegrid.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gersion.schultegrid.bean.ItemBean;
import com.gersion.schultegrid.adapter.MyAdapter;
import com.gersion.schultegrid.R;
import com.gersion.smartrecycleviewlibrary.SmartRecycleView;
import com.gersion.smartrecycleviewlibrary.SmartRecycler;
import com.gersion.smartrecycleviewlibrary.ptr2.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by aa326 on 2017/9/28.
 */

public class SchulteGridView extends LinearLayout{

    private static final int GERANL_MODE = 0;//普通模式,点击正确的数字会消失
    private static final int EVER_CHANGING_MODE = 1;//千变万化,点击正确的数字会消失,剩下的数字会重新排
    private static final int NO_HIDE_MODE = 2;//点击正确的数字不会消失
    private static final int EVER_CHANGING_NO_HIDE_MODE = 3;//千变万化2,点击正确的数字不会消失,剩下的数字会重新排
    private View mView;
    private Context mContext;
    private Chronometer mChronometer;
    private SmartRecycleView mSmartRecycleView;
    private MyAdapter mAdapter;
    private ArrayList<ItemBean> mData;
    private ArrayList<String> mContents = new ArrayList<>();
    private ArrayList<Integer> mIndexs = new ArrayList<>();
    private int mCurrentIndex ;
    private int num = 5;
    private int mTotalCount;
    private TextView mTvResult;
    private TextView mTvRetry;
    private FrameLayout mFlContainer;
    private long mStartTime;
    private long mEndTime;
    private int mode = GERANL_MODE;
    private Random mRandom;
    private RadioGroup mRadioGroup;

    public SchulteGridView(Context context) {
        this(context,null);
    }

    public SchulteGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mView = LayoutInflater.from(context).inflate(R.layout.view_schulte_grid, this);
        mContext = context;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        mFlContainer = (FrameLayout) mView.findViewById(R.id.fl_container);
        mTvResult = (TextView) mView.findViewById(R.id.tv_result);
        mTvRetry = (TextView) mView.findViewById(R.id.tv_retry);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mRandom = new Random();
        mData = new ArrayList<>();
        mAdapter = new MyAdapter(mData);
        mAdapter.setNum(num);
        mTotalCount = num * num;
        mStartTime = System.currentTimeMillis();
        mSmartRecycleView = new SmartRecycler.Builder(mContext, container, mAdapter)
                .setFirstPage(1)
                .setAutoRefresh(true)
                .setPageSize(10)
                .setLoadMore(false)
                .setRefresh(true)
                .setLayoutManagerType(SmartRecycleView.LayoutManagerType.GRID_LAYOUT)
                .setSpanCount(num)
                .setRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh(int i) {
                        resetData();
                    }

                    @Override
                    public void onLoadMore(int i) {

                    }
                })
                .build();

    }

    private void initData() {
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int index = mCurrentIndex + 1;
                if (mData.get(position).content.equals(index+"")){
                    setItemVisible(view);
                    mCurrentIndex++;
                    setItemPosition();
                    if (mTotalCount<=mCurrentIndex){
                        mEndTime = System.currentTimeMillis();
                        onComplete();
                    }
                }
            }
        });

        mTvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRetry();
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                Log.d("aa",i+"");
                int mode = -1;
                if (i==R.id.general){
                    mode=GERANL_MODE;
                }else if (i==R.id.ever_changing){
                    mode=EVER_CHANGING_MODE;
                }else if (i==R.id.no_hide){
                    mode=NO_HIDE_MODE;
                }else if (i==R.id.ever_changing_no_hide){
                    mode=EVER_CHANGING_NO_HIDE_MODE;
                }
                setMode(mode);
                mChronometer.setBase(SystemClock.elapsedRealtime());
            }
        });
    }

    private void setItemVisible(View view) {
        if (mode==GERANL_MODE||mode==EVER_CHANGING_MODE) {
            view.setVisibility(INVISIBLE);
        }
    }

    private void resetData() {
        mData.clear();
        mContents.clear();
        mCurrentIndex = 0;
        mStartTime = System.currentTimeMillis();
        getList();
    }

    private void onRetry() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mSmartRecycleView.setVisibility(View.VISIBLE);
        mFlContainer.setVisibility(View.GONE);
        resetData();
    }

    private void onComplete(){
        mChronometer.stop();
        mSmartRecycleView.setVisibility(View.GONE);
        mFlContainer.setVisibility(View.VISIBLE);
        mTvResult.setText("总耗时："+(mEndTime-mStartTime)/1000+" s");
    }

    private void setItemPosition(){
        if (mode==EVER_CHANGING_MODE||mode==EVER_CHANGING_NO_HIDE_MODE) {
            mData.clear();
            mContents.clear();
            getList();
        }
    }

    private void getList() {
        for (int i = 1; i <= mTotalCount; i++) {
            ItemBean itemBean = new ItemBean();
            itemBean.content = getContent(mTotalCount+1,itemBean);
            itemBean.index = i;
            mData.add(itemBean);
        }
        mSmartRecycleView.handleData(mData);
    }

    private String getContent(int totalCount, ItemBean itemBean){
        int i = mRandom.nextInt(totalCount);
        String content = i + "";
        if (i !=0&&!mContents.contains(content)){
            mContents.add(content);
            setItemSatus(itemBean, i);
            return content;
        }else {
            return getContent(totalCount, itemBean);
        }
    }

    private void setItemSatus(ItemBean itemBean, int i) {
        if (mode==GERANL_MODE||mode==EVER_CHANGING_MODE) {
            if (mCurrentIndex >= i) {
                itemBean.isShow = false;
            }
        }
    }

    private int getIndex(int totalCount, Random random){
        int i = random.nextInt(totalCount);
        if (i !=0&&!mIndexs.contains(i)){
            mIndexs.add(i);
            return i;
        }else {
            return getIndex(totalCount,random);
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        resetData();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mChronometer.stop();
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
