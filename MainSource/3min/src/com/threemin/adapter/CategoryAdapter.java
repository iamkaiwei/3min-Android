package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class CategoryAdapter extends BaseAdapter {

	Context mContext;
	List<CategoryModel> data;
	OnClickListener onLogout;
	public CategoryAdapter(Context context, List<CategoryModel> data) {
		mContext = context;
		this.data = data;
	}
	
	public void setOnLogout(OnClickListener onLogout){
		this.onLogout=onLogout;
	}

	@Override
	public int getCount() {
		return data.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position < data.size()) {
			return data.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < data.size()) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.inflater_category, parent, false);
			ImageView img = (ImageView) convertView.findViewById(R.id.inflater_cate_image);
			if (position > 0) {
				Log.d("postion", ""+position);
				UrlImageViewHelper.setUrlDrawable(img, data.get(position).getImage().getUrl());
			} else {
				img.setImageResource(R.drawable.ic_browse);
			}

			TextView tvName = (TextView) convertView.findViewById(R.id.inflater_cate_tv_name);
			tvName.setText(data.get(position).getName());
			return convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View v= inflater.inflate(R.layout.inflater_navigation_share, parent, false);
			v.findViewById(R.id.share_facebook).setOnClickListener(CommonUti.shareFacebook());
			v.findViewById(R.id.share_email).setOnClickListener(CommonUti.shareEmail(mContext));
			v.findViewById(R.id.share_message).setOnClickListener(CommonUti.shareMessage(mContext));
			if(onLogout!=null){
				v.findViewById(R.id.btn_logout).setOnClickListener(onLogout);
			}
			return v;
		}
	}

}
