package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.threemin.model.FeedbackModel;

public class FeedbackAdapter extends BaseAdapter {
    
    private Context mContext;
    private List<FeedbackModel> mData;

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        return null;
    }
    
    public void setListFeedback(List<FeedbackModel> list) {
        mData = list;
        notifyDataSetChanged();
    }

}
