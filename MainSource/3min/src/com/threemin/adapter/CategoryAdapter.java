package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class CategoryAdapter extends BaseAdapter implements SpinnerAdapter {

	Context mContext;
	List<CategoryModel> data;
	OnClickListener onLogout;
	int selectedCate;
	boolean isSpinner;

	public CategoryAdapter(Context context, List<CategoryModel> data, boolean isSpinner) {
		mContext = context;
		selectedCate = 0;
		this.data = data;
		this.isSpinner=isSpinner;
	}

	public void setOnLogout(OnClickListener onLogout) {
		this.onLogout = onLogout;
	}

	@Override
	public int getCount() {
		return data.size();
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
		if (isSpinner) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.inflater_category_spinner, parent, false);
			TextView tvName = (TextView) convertView.findViewById(R.id.inflater_category_spinner_tv);
			tvName.setText(data.get(position).getName());
			// data.remove(position);
			// data.add(position, selectedCate);
			return convertView;
		} else {
			return getDropDownView(position, convertView, parent);
		}

		// ImageView img = (ImageView)
		// convertView.findViewById(R.id.inflater_cate_image);
		// if (position > 0) {
		// Log.d("postion", ""+position);
		// UrlImageViewHelper.setUrlDrawable(img,
		// data.get(position).getImage().getUrl());
		// } else {
		// img.setImageResource(R.drawable.ic_everything);
		// }
		//
		// TextView tvName = (TextView)
		// convertView.findViewById(R.id.inflater_cate_tv_name);
		// if (position < data.size()) {
		// LayoutInflater inflater = LayoutInflater.from(mContext);
		// convertView = inflater.inflate(R.layout.inflater_category, parent,
		// false);
		// ImageView img = (ImageView)
		// convertView.findViewById(R.id.inflater_cate_image);
		// if (position > 0) {
		// Log.d("postion", ""+position);
		// UrlImageViewHelper.setUrlDrawable(img,
		// data.get(position).getImage().getUrl());
		// } else {
		// img.setImageResource(R.drawable.ic_everything);
		// }
		//
		// TextView tvName = (TextView)
		// convertView.findViewById(R.id.inflater_cate_tv_name);
		// tvName.setText(data.get(position).getName());
		// return convertView;
		// } else {
		// LayoutInflater inflater = LayoutInflater.from(mContext);
		// View v= inflater.inflate(R.layout.inflater_navigation_share, parent,
		// false);
		// v.findViewById(R.id.share_facebook).setOnClickListener(CommonUti.shareFacebook());
		// v.findViewById(R.id.share_email).setOnClickListener(CommonUti.shareEmail(mContext));
		// v.findViewById(R.id.share_message).setOnClickListener(CommonUti.shareMessage(mContext));
		// if(onLogout!=null){
		// v.findViewById(R.id.btn_logout).setOnClickListener(onLogout);
		// }
		// return v;
		// }
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(R.layout.inflater_category, parent, false);
		ImageView img = (ImageView) convertView.findViewById(R.id.inflater_cate_image);
		if (!data.get(position).getName().equals(parent.getContext().getString(R.string.browse))) {
			Log.d("postion", "" + position);
			UrlImageViewHelper.setUrlDrawable(img, data.get(position).getImage().getUrl());
		} else {
			img.setImageResource(R.drawable.ic_everything);
		}

		TextView tvName = (TextView) convertView.findViewById(R.id.inflater_cate_tv_name);
		tvName.setText(data.get(position).getName());
		if (selectedCate == position && isSpinner) {
			LayoutParams params = convertView.getLayoutParams();
			params.height = 1;
			convertView.setLayoutParams(params);
		}
		return convertView;

	}

	public void swapView(int position) {
		selectedCate = position;
	}

}
