package com.threemin.adapter;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemins.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {

	Context mContext;
	List<CategoryModel> data;

	public CategoryAdapter(Context context, List<CategoryModel> data) {
		mContext = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater=LayoutInflater.from(mContext);
			convertView=inflater.inflate(R.layout.inflater_category, null);
		}
		ImageView img=(ImageView) convertView.findViewById(R.id.inflater_cate_image);
		UrlImageViewHelper.setUrlDrawable(img, data.get(position).getImage().getUrl());
		
		TextView tvName=(TextView) convertView.findViewById(R.id.inflater_cate_tv_name);
		tvName.setText(data.get(position).getName());
		return convertView;
	}

}
