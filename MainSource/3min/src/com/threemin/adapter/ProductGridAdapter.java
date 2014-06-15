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
		if(mData==null){
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
		LinearLayout layout = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflater.inflate(R.layout.inflater_body_product_grid, null);
		} else {
			layout = (LinearLayout) convertView;
		}

		initBody(position, layout);
		return layout;
	}

	private void initBody(int position, View convertView) {
		if (mData == null || mData.size() == 0) {
			return;
		}
		ProductModel model = mData.get(position);

		if (model != null) {
			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
			if (model.getImages().size() > 0) {
				UrlImageViewHelper.setUrlDrawable(image, model.getImages().get(0).getOrigin(), R.drawable.stuff_img);
//				imageLoader.displayImage( model.getImages().get(0).getOrigin(),image,options);
			}

			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_name);
			tv_name.setText(model.getName());

			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_price);
			tv_price.setText(model.getPrice() + CommonConstant.CURRENCY);

			TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_like);
			if (model.getLike() > 0) {
				tv_like.setText("" + model.getLike());
			} else {
				tv_like.setText("");
			}

			ImageView img_like = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_img_like);
			if (model.isLiked()) {
				img_like.setSelected(true);
			} else {
				img_like.setSelected(false);
			}
			if (model.getOwner() == null) {
				convertView.findViewById(R.id.owner_view).setVisibility(View.GONE);
			} else {
				ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.inflater_header_product_grid_image);
				UrlImageViewHelper.setUrlDrawable(imageAvatar, model.getOwner().getFacebook_avatar());

				TextView tv_name_owner = (TextView) convertView.findViewById(R.id.inflater_header_product_grid_tv_name);
				tv_name_owner.setText(model.getOwner().getFullName());
			}
			TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_header_product_grid_tv_time);
			tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getUpdateTime() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));

		}

	}

}
