package com.threemin.adapter;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.UserModel;
import com.threemins.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FollowAdapter extends BaseAdapter {
    
    private List<UserModel> mData;
    
    public FollowAdapter(List<UserModel> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public UserModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.inflater_follow, parent, false);
        }
        UserModel model = mData.get(position);
        
        ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.inflater_follow_avatar);
        UrlImageViewHelper.setUrlDrawable(imgAvatar, model.getFacebook_avatar());
        
        TextView tvName = (TextView) convertView.findViewById(R.id.inflater_follow_tv_name);
        tvName.setText(model.getFullName());
        
        ImageView imgFollow = (ImageView) convertView.findViewById(R.id.inflater_follow_img_follow);
        imgFollow.setSelected(model.isFollowed());
        return convertView;
    }

}
