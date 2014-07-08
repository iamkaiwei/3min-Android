package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

public class ProductAdapter extends BaseAdapter {
	List<ProductModel> data;
	
	public ProductAdapter(List<ProductModel> data) {
		this.data = data;
	}
	
	public List<ProductModel> getListProducts() {
		return this.data;
	}
	
	public void updateData(List<ProductModel> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator.inflate(R.layout.inflater_body_product, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		initBody(position, layout);
		return layout;
	}
	
	private void initBody(int section, View convertView) {
		if (data == null || data.size() == 0) {
			return;
		}
		ProductModel model = data.get(section);

		ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_image);
		if (model.getImages().size() > 0) {
			UrlImageViewHelper.setUrlDrawable(image, model.getImages().get(0).getMedium());
		}

		TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_name);
		tv_name.setText(model.getName());

		TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_price);
		tv_price.setText(model.getPrice() + CommonConstant.CURRENCY);

		TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_like);
		if (model.getLike() > 0) {
			tv_like.setText("" + model.getLike());
		} else {
			tv_like.setText("");
		}

		ImageView img_like = (ImageView) convertView.findViewById(R.id.inflater_body_product_img_like);
		if (model.isLiked()) {
			img_like.setSelected(true);
		} else {
			img_like.setSelected(false);
		}
		
		//old header info

		ImageView imageOwner = (ImageView) convertView.findViewById(R.id.inflater_heeader_product_image);
		UrlImageViewHelper.setUrlDrawable(imageOwner, model.getOwner().getFacebook_avatar());

		TextView tv_nameOwner = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_name);
		tv_nameOwner.setText(model.getOwner().getFullName());

		TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_time);
		tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getUpdateTime() * 1000, System.currentTimeMillis(),0L, DateUtils.FORMAT_ABBREV_RELATIVE));
	}

//	public ProductAdapter(List<ProductModel> data) {
//		this.data = data;
//	}
//	
//	public List<ProductModel> getListProducts() {
//		return this.data;
//	}
//
//	public void updateData(List<ProductModel> data) {
//		this.data = data;
//		notifyDataSetChanged();
//	}
//
//	@Override
//	public Object getItem(int section, int position) {
//		if(data==null){
//			return null;
//		}
//		return data.get(section);
//	}
//
//	@Override
//	public long getItemId(int section, int position) {
//		return section;
//	}
//
//	@Override
//	public int getSectionCount() {
//		if (data == null) {
//			return 0;
//		}
//		return data.size();
//	}
//
//	@Override
//	public int getCountForSection(int section) {
//		return 1;
//	}
//
//	@Override
//	public View getItemView(int section, int position, View convertView, ViewGroup parent) {
//		LinearLayout layout = null;
//		if (convertView == null) {
//			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(
//					Context.LAYOUT_INFLATER_SERVICE);
//			layout = (LinearLayout) inflator.inflate(R.layout.inflater_body_product, null);
//		} else {
//			layout = (LinearLayout) convertView;
//		}
//		initBody(section, layout);
//		return layout;
//	}
//
//	private void initBody(int section, View convertView) {
//		if (data == null || data.size() == 0) {
//			return;
//		}
//		ProductModel model = data.get(section);
//
//		ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_image);
//		if (model.getImages().size() > 0) {
//			UrlImageViewHelper.setUrlDrawable(image, model.getImages().get(0).getMedium());
//		}
//
//		TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_name);
//		tv_name.setText(model.getName());
//
//		TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_price);
//		tv_price.setText(model.getPrice() + CommonConstant.CURRENCY);
//
//		TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_like);
//		if (model.getLike() > 0) {
//			tv_like.setText("" + model.getLike());
//		} else {
//			tv_like.setText("");
//		}
//
//		ImageView img_like = (ImageView) convertView.findViewById(R.id.inflater_body_product_img_like);
//		if (model.isLiked()) {
//			img_like.setSelected(true);
//		} else {
//			img_like.setSelected(false);
//		}
//	}
//
//	@Override
//	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
//		LinearLayout layout = null;
//		if (convertView == null) {
//			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(
//					Context.LAYOUT_INFLATER_SERVICE);
//			layout = (LinearLayout) inflator.inflate(R.layout.inflater_header, null);
//		} else {
//			layout = (LinearLayout) convertView;
//		}
//		initHeaderView(section, layout);
//		return layout;
//	}
//
//	private void initHeaderView(int section, View convertView) {
//		if (data == null || data.size() == 0) {
//			return;
//		}
//		ProductModel model = data.get(section);
//
//		ImageView image = (ImageView) convertView.findViewById(R.id.inflater_heeader_product_image);
//		UrlImageViewHelper.setUrlDrawable(image, model.getOwner().getFacebook_avatar());
//
//		TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_name);
//		tv_name.setText(model.getOwner().getFullName());
//
//		TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_time);
//		tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getUpdateTime() * 1000, System.currentTimeMillis(),
//				0L, DateUtils.FORMAT_ABBREV_RELATIVE));
//	}

}
