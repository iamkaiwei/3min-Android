package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.FeedbackModel;
import com.threemins.R;

public class FeedbackAdapter extends BaseAdapter {
    
    static class ViewHolder {
        public ImageView ivAvatar;
        public TextView tvName;
        public TextView tvTime;
        public TextView tvContent;
    }
    
    private Context mContext;
    private List<FeedbackModel> mData;
    
    public FeedbackAdapter(Context context, List<FeedbackModel> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
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
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.inflater_feedback_content, parent, false);
            
            ViewHolder vh = new ViewHolder();
            vh.ivAvatar = (ImageView) convertView.findViewById(R.id.inf_feedback_avatar);
            vh.tvName = (TextView) convertView.findViewById(R.id.inf_feedback_tv_name);
            vh.tvTime = (TextView) convertView.findViewById(R.id.inf_feedback_tv_time);
            vh.tvContent = (TextView) convertView.findViewById(R.id.inf_feedback_tv_content);
            
            convertView.setTag(vh);
        }
        
        FeedbackModel m = mData.get(position);
        ViewHolder vh = (ViewHolder) convertView.getTag();
        
        
//        ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.inf_feedback_avatar);
//        TextView tvName = (TextView) convertView.findViewById(R.id.inf_feedback_tv_name);
//        TextView tvTime = (TextView) convertView.findViewById(R.id.inf_feedback_tv_time);
//        TextView tvContent = (TextView) convertView.findViewById(R.id.inf_feedback_tv_content);
        
        String avatarUrl = m.getUser().getFacebook_avatar();
        String fullName = m.getUser().getFullName();
        String content = m.getContent();
        CharSequence time = DateUtils.getRelativeTimeSpanString(
                m.getUpdate_time() * 1000, 
                System.currentTimeMillis(),
                0L, 
                DateUtils.FORMAT_ABBREV_RELATIVE);
        
        UrlImageViewHelper.setUrlDrawable(vh.ivAvatar, avatarUrl, R.drawable.avatar_loading);
        vh.tvName.setText(fullName);
        vh.tvTime.setText(time);
        vh.tvContent.setText(content);
        
        return convertView;
    }
    
    public void setListFeedback(List<FeedbackModel> list) {
        mData = list;
        notifyDataSetChanged();
    }

}
