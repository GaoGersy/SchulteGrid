package com.gersion.schultegrid.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gersion.schultegrid.R;
import com.gersion.schultegrid.bean.ItemBean;
import com.gersion.smartrecycleviewlibrary.ptr2.IRVAdapter;

import java.util.List;

/**
 * Created by aa326 on 2017/9/25.
 */

public class MyAdapter extends BaseQuickAdapter<ItemBean, BaseViewHolder> implements IRVAdapter<ItemBean> {

    private int num;

    public MyAdapter(@Nullable List<ItemBean> data) {
        super(R.layout.item_content, data);
    }

    public MyAdapter(int layoutResId, @Nullable List<ItemBean> data) {
        super(layoutResId, data);
    }

    public void setNum(int num){
        this.num = num;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ItemBean bean) {
//        baseViewHolder.setVisible(R.id.tv_content,true);
        int color_1 = Color.parseColor("#9b59b6");
        int color_2 = Color.parseColor("#8e44ad");
        baseViewHolder.setText(R.id.tv_content, bean.content);
        baseViewHolder.addOnClickListener(R.id.tv_content);
        baseViewHolder.setBackgroundColor(R.id.item_container, getColor(bean.index,color_1,color_2));
        baseViewHolder.getView(R.id.tv_content).setVisibility(bean.isShow? View.VISIBLE:View.INVISIBLE);
    }

    private int getColor(int index, int color_1, int color_2) {
        int i = (index-1) / num;
        if (num%2==0){
            if (i % 2 == 0) {
                return index % 2 == 0 ? color_1 : color_2;
            } else {
                return index % 2 == 0 ? color_2 : color_1;
            }
        }else{
            return index % 2 == 0 ? color_1 : color_2;
        }
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return this;
    }

    @Override
    public void setNewData(List data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void addData(List data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void removeAll(List data) {
        mData.removeAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void remove(ItemBean data) {
        mData.remove(data);
        notifyDataSetChanged();
    }

    @Override
    public List<ItemBean> getData() {
        return mData;
    }
}
