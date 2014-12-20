package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.ProfileActivity;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

public class UserLikedProductAdapter extends BaseAdapter {

    static class ViewHolder {
        public ImageView ivAvatar;
        public TextView tvName;
    }

    private Context mContext;
    private List<UserModel> mData;

    public UserLikedProductAdapter(Context context, List<UserModel> data) {
        mContext = context;
        mData = data;
    }

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
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.inflater_user_who_liked_product, parent, false);

            ViewHolder vh = new ViewHolder();
            vh.ivAvatar = (ImageView) convertView.findViewById(R.id.inflater_user_who_liked_product_avatar);
            vh.tvName = (TextView) convertView.findViewById(R.id.inflater_user_who_liked_product_tv_name);

            convertView.setTag(vh);
        }

        final UserModel model = mData.get(position);
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.tvName.setText(model.getFullName());
        UrlImageViewHelper.setUrlDrawable(vh.ivAvatar, model.getFacebook_avatar(), R.drawable.avatar_loading);

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA, new Gson().toJson(model));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void setList(List<UserModel> list) {
        mData = list;
        notifyDataSetChanged();
    }

}
