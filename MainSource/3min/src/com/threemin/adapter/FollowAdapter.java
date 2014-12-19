package com.threemin.adapter;

import java.util.List;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.ProfileActivity;
import com.threemin.fragment.RightFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.RelationshipWebService;
import com.threemins.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FollowAdapter extends BaseAdapter {
    
    static class ViewHolder {
        public ImageView imgAvatar;
        public TextView tvName;
        public ImageView imgFollow;
    }
    
    private List<UserModel> mData;
    Context mContext;
    
    public FollowAdapter(Context context, List<UserModel> data) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public UserModel getItem(int position) {
        if (mData == null) {
            return null;
        }
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
            
            ViewHolder vh = new ViewHolder();
            vh.imgAvatar = (ImageView) convertView.findViewById(R.id.inflater_follow_avatar);
            vh.imgFollow = (ImageView) convertView.findViewById(R.id.inflater_follow_img_follow);
            vh.tvName = (TextView) convertView.findViewById(R.id.inflater_follow_tv_name);
            
            convertView.setTag(vh);
        }
        
        final UserModel model = mData.get(position);
        ViewHolder vh = (ViewHolder) convertView.getTag();
        convertView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA, new Gson().toJson(model));
                mContext.startActivity(intent);
            }
        });
        
        UrlImageViewHelper.setUrlDrawable(vh.imgAvatar, model.getFacebook_avatar(), R.drawable.avatar_loading);
        
        vh.tvName.setText(model.getFullName());
        
        vh.imgFollow.setSelected(model.isFollowed());
        return convertView;
    }
    
    public void setListUsers(List<UserModel> list) {
        mData = list;
        this.notifyDataSetChanged();
    }

}
