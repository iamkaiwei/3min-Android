package com.threemin.fragment;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.DetailActivity;
import com.threemin.app.PostOfferActivity;
import com.threemin.model.ProductModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemins.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	View rootView;
	ProductModel productModel;
	ViewPager pager;
	SlidePagerAdapter adapter;
	ImageView img0, img1, img2, img3;
	Button btnChatToBuy, btnViewOffers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		productModel = new Gson().fromJson(
				getActivity().getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
		rootView = inflater.inflate(R.layout.fragment_detail, null);
		initBody(rootView);
		return rootView;
	}

	private void initBody(View convertView) {

		if (productModel != null) {
			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
			// if (productModel.getImages().size() > 0) {
			// UrlImageViewHelper.setUrlDrawable(image,
			// model.getImages().get(0).getMedium());
			// }

			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_name);
			tv_name.setText(productModel.getName());
			getActivity().setTitle(productModel.getName());

			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_price);
			tv_price.setText(productModel.getPrice() + CommonConstant.CURRENCY);

			TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_like);
			if (productModel.getLike() > 0) {
				tv_like.setText("" + productModel.getLike() + " people like this");
			} else {
				tv_like.setVisibility(View.GONE);
			}
			TextView tv_locaion = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_location);
			if (!TextUtils.isEmpty(productModel.getVenueName())) {
				tv_locaion.setText(productModel.getVenueName());
			} else {
				tv_locaion.setVisibility(View.GONE);
			}

			TextView tv_description = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_description);
			if (!TextUtils.isEmpty(productModel.getDescription())) {
				tv_description.setText(productModel.getDescription());
			} else {
				tv_description.setVisibility(View.GONE);
			}

			ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.inflater_header_product_image);
			UrlImageViewHelper.setUrlDrawable(imageAvatar, productModel.getOwner().getFacebook_avatar());

			TextView tv_name_owner = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_name);
			tv_name_owner.setText(productModel.getOwner().getFullName());

			TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_time);
			tv_time.setText(DateUtils.getRelativeTimeSpanString(productModel.getUpdateTime() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
			pager = (ViewPager) convertView.findViewById(R.id.pager);
			adapter = new SlidePagerAdapter(getFragmentManager());
			pager.setAdapter(adapter);
			int numPages = adapter.getCount();

			img0 = (ImageView) convertView.findViewById(R.id.fragment_detail_img_page_ctr_0);
			img1 = (ImageView) convertView.findViewById(R.id.fragment_detail_img_page_ctr_1);
			img2 = (ImageView) convertView.findViewById(R.id.fragment_detail_img_page_ctr_2);
			img3 = (ImageView) convertView.findViewById(R.id.fragment_detail_img_page_ctr_3);

			initPageControl(numPages);

			pager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					// TODO Auto-generated method stub
					doPageControl(position);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}
			});

			btnChatToBuy = (Button) convertView.findViewById(R.id.fragment_detail_btn_chat_to_buy);
			
			UserModel currentUser = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
			
			//if current user is not the owner of this product
			if (currentUser.getId() != productModel.getOwner().getId()) {  
				btnChatToBuy.setBackgroundResource(R.drawable.bt_chat_to_buy);
				btnChatToBuy.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String data = new Gson().toJson(productModel);
						// Intent intent = new Intent(getActivity(),
						// ChatToBuyActivity.class);
						Intent intent = new Intent(getActivity(),
								PostOfferActivity.class);
						intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
						getActivity().startActivity(intent);
					}
				});
			} else {
				btnChatToBuy.setBackgroundResource(R.drawable.bt_view_offers);
			}
			
			


			

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

	public void initPageControl(int numPages) {

		img0.setBackgroundResource(R.drawable.page_ctr_on);
		img1.setBackgroundResource(R.drawable.page_ctr_off);
		img2.setBackgroundResource(R.drawable.page_ctr_off);
		img3.setBackgroundResource(R.drawable.page_ctr_off);

		switch (numPages) {

		case 1:
			img0.setVisibility(View.VISIBLE);
			img1.setVisibility(View.GONE);
			img2.setVisibility(View.GONE);
			img3.setVisibility(View.GONE);
			break;

		case 2:
			img0.setVisibility(View.VISIBLE);
			img1.setVisibility(View.VISIBLE);
			img2.setVisibility(View.GONE);
			img3.setVisibility(View.GONE);
			break;

		case 3:
			img0.setVisibility(View.VISIBLE);
			img1.setVisibility(View.VISIBLE);
			img2.setVisibility(View.VISIBLE);
			img3.setVisibility(View.GONE);
			break;

		case 4:
			img0.setVisibility(View.VISIBLE);
			img1.setVisibility(View.VISIBLE);
			img2.setVisibility(View.VISIBLE);
			img3.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	private void doPageControl(int position) {
		switch (position) {
		case 0:
			img0.setBackgroundResource(R.drawable.page_ctr_on);
			img1.setBackgroundResource(R.drawable.page_ctr_off);
			img2.setBackgroundResource(R.drawable.page_ctr_off);
			img3.setBackgroundResource(R.drawable.page_ctr_off);
			break;

		case 1:
			img0.setBackgroundResource(R.drawable.page_ctr_off);
			img1.setBackgroundResource(R.drawable.page_ctr_on);
			img2.setBackgroundResource(R.drawable.page_ctr_off);
			img3.setBackgroundResource(R.drawable.page_ctr_off);
			break;

		case 2:
			img0.setBackgroundResource(R.drawable.page_ctr_off);
			img1.setBackgroundResource(R.drawable.page_ctr_off);
			img2.setBackgroundResource(R.drawable.page_ctr_on);
			img3.setBackgroundResource(R.drawable.page_ctr_off);
			break;

		case 3:
			img0.setBackgroundResource(R.drawable.page_ctr_off);
			img1.setBackgroundResource(R.drawable.page_ctr_off);
			img2.setBackgroundResource(R.drawable.page_ctr_off);
			img3.setBackgroundResource(R.drawable.page_ctr_on);
			break;

		default:
			break;
		}
	}
}
