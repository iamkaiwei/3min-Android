package com.threemin.adapter;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductGridAdapter extends BaseAdapter {
	private List<ProductModel> mData;
	
	public ProductGridAdapter(List<ProductModel> data) {
		this.mData = data;
	}
	
	public List<ProductModel> getListProducts() {
		return this.mData;
	}

	public void updateData(List<ProductModel> data) {
		this.mData = data;
		notifyDataSetChanged();
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = null;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflater.inflate(R.layout.inflater_body_product_grid, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		
		initBody(position, layout);
		return layout;
	}

	private void initBody(int position, View convertView) {
		ProductModel model = mData.get(position);
		
		if (model != null) {
			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
			if (model.getImages().size() > 0) {
				UrlImageViewHelper.setUrlDrawable(image, model.getImages().get(0).getMedium());
			}
			
			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_name);
			tv_name.setText(model.getName());
			
			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_price);
			tv_price.setText(model.getPrice() + CommonConstant.CURRENCY);
		}
		
	}

}
