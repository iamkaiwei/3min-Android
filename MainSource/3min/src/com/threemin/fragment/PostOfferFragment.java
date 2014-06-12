package com.threemin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class PostOfferFragment extends Fragment {
	ProductModel mProductModel;

	TextView title;
	EditText price;
	ImageView rotateImg;
	View changeView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_post_offer, container, false);
		initView(rootView);
		initData();
		initListner();
		return rootView;
	}

	private void initData() {
		mProductModel = new Gson().fromJson(getActivity().getIntent()
				.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
		String sTitle = String.format(getString(R.string.postoffer_title), mProductModel.getOwner().getFirstName(),
				mProductModel.getPrice());
		title.setText(sTitle);
		price.setText(mProductModel.getPrice());
	}

	private void initListner() {
		changeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

		        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_around_center_point);
		        animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						CommonUti.showKeyboard(price, getActivity());
					}
				});
		        rotateImg.startAnimation(animation);				
			}
		});
	}

	private void initView(View rootView) {
		title = (TextView) rootView.findViewById(R.id.post_offer_title);
		price = (EditText) rootView.findViewById(R.id.et_label_price);
		rotateImg = (ImageView) rootView.findViewById(R.id.rotate_img);
		changeView = rootView.findViewById(R.id.view_change);
	}
}
