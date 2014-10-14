package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.UserModel;
import com.threemins.R;

public class UserLikedProductAdapter extends BaseAdapter {
    
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
        }
        
        TextView name = (TextView) convertView.findViewById(R.id.inflater_user_who_liked_product_tv_name);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.inflater_user_who_liked_product_avatar);
        
        UserModel model = mData.get(position);
        name.setText(model.getFullName());
        UrlImageViewHelper.setUrlDrawable(avatar, model.getFacebook_avatar(), R.drawable.stuff_img);
        
        return convertView;
    }
    
    public void setList(List<UserModel> list) {
        mData = list;
        notifyDataSetChanged();
    }

}
