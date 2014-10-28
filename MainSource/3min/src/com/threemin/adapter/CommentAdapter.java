package com.threemin.adapter;

import java.util.List;

import com.google.android.gms.internal.in;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ActivityModel;
import com.threemin.model.CommentModel;
import com.threemins.R;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
    
    Context mContext;
    private List<CommentModel> mData;
    
    public CommentAdapter(Context context, List<CommentModel> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.inflater_comment, null);
        }
        
        ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.inflater_comment_avatar);
        TextView tvContent = (TextView) convertView.findViewById(R.id.inflater_comment_tv_name_and_content);
        TextView tvTime = (TextView) convertView.findViewById(R.id.inflater_comment_tv_time);
        
        CommentModel model = mData.get(position);
        
        UrlImageViewHelper.setUrlDrawable(imgAvatar, model.getUser().getFacebook_avatar(), R.drawable.avatar_loading);
        tvContent.setText(Html.fromHtml("<font color=\"#ff401a\">" + model.getUser().getFullName() + "</font>" 
                                        + "<font color=\"#a9a9a9\">" + " " + model.getContent() + "</font>"));
        
        tvTime.setText(DateUtils.getRelativeTimeSpanString(model.getUpdated_at() * 1000,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
        return convertView;
    }
    
    public void setListComments(List<CommentModel> list) {
        mData = list;
        this.notifyDataSetChanged();
    }

}
