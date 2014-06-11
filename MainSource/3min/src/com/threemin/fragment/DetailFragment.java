package com.threemin.fragment;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	View rootView;
	ProductModel productModel;
	ViewPager pager;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		productModel = new Gson().fromJson(
				getActivity().getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
		rootView = inflater.inflate(R.layout.fragment_detail, null);
		initBody( rootView);
		return rootView;
	}
	
	private void initBody( View convertView) {

		if (productModel != null) {
			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
//			if (productModel.getImages().size() > 0) {
//				UrlImageViewHelper.setUrlDrawable(image,
//						model.getImages().get(0).getMedium());
//			}

			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_name);
			tv_name.setText(productModel.getName());
			getActivity().setTitle(productModel.getName());

			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_price);
			tv_price.setText(productModel.getPrice() + CommonConstant.CURRENCY);

			TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_like);
			if (productModel.getLike() > 0) {
				tv_like.setText("" + productModel.getLike());
			} else {
				tv_like.setText("");
			}
			
			TextView tv_locaion = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_location);
			tv_locaion.setText(productModel.getVenueName());
			
			TextView tv_description = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_description);
			tv_description.setText(productModel.getDescription());

			
			ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.inflater_header_product_image);
			UrlImageViewHelper.setUrlDrawable(imageAvatar, productModel.getOwner().getFacebook_avatar());
			
			TextView tv_name_owner = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_name);
			tv_name_owner.setText(productModel.getOwner().getFullName());
			
			TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_time);
			tv_time.setText(DateUtils.getRelativeTimeSpanString(productModel.getUpdateTime() * 1000, System.currentTimeMillis(),
					0L, DateUtils.FORMAT_ABBREV_RELATIVE));
			pager=(ViewPager) convertView.findViewById(R.id.pager);
			pager.setAdapter(new SlidePagerAdapter(getFragmentManager()));
		}

	}
	
	private class SlidePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ProductImageFragment.create(productModel.getImages().get(position).getOrigin());
		}

		@Override
		public int getCount() {
			return productModel.getImages().size();
		}
	}
}
