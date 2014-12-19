package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CommentModel;
import com.threemins.R;

public class CommentAdapter extends BaseAdapter {
    
    static class ViewHolder {
        public ImageView ivAvatar;
        public TextView tvContent;
        public TextView tvTime;
    }
    
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
            
            ViewHolder vh = new ViewHolder();
            vh.ivAvatar = (ImageView) convertView.findViewById(R.id.inflater_comment_avatar);
            vh.tvContent = (TextView) convertView.findViewById(R.id.inflater_comment_tv_name_and_content);
            vh.tvTime = (TextView) convertView.findViewById(R.id.inflater_comment_tv_time);
            
            convertView.setTag(vh);
        }
        
        CommentModel model = mData.get(position);
        ViewHolder holder = (ViewHolder)convertView.getTag();
        
        UrlImageViewHelper.setUrlDrawable(holder.ivAvatar, model.getUser().getFacebook_avatar(), R.drawable.avatar_loading);
        holder.tvContent.setText(Html.fromHtml("<font color=\"#ff401a\">" + model.getUser().getFullName() + "</font>" 
                                        + "<font color=\"#a9a9a9\">" + " " + model.getContent() + "</font>"));
        
        holder.tvTime.setText(DateUtils.getRelativeTimeSpanString(model.getUpdated_at() * 1000,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
        return convertView;
    }
    
    public void setListComments(List<CommentModel> list) {
        mData = list;
        this.notifyDataSetChanged();
    }

}
