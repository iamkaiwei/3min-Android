package com.threemin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.UserLikeProductActivity;
import com.threemin.model.UserModel;
import com.threemin.uti.PreferenceHelper;
import com.threemins.R;

public class RightFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_right, container, false);
		UserModel userModel = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
		ImageView img = (ImageView) rootView.findViewById(R.id.nav_avatar);
		UrlImageViewHelper.setUrlDrawable(img, userModel.getFacebook_avatar());

		TextView tv_name = (TextView) rootView.findViewById(R.id.navigation_name);
		tv_name.setText(userModel.getFullName());
		userProduct();
		initListener(rootView);
		return rootView;
	}

	private void initListener(View rootView) {
		rootView.findViewById(R.id.btn_liked).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), UserLikeProductActivity.class));
			}
		});

	}

	private void userProduct() {
		ListProductFragment productFragmentGrid = new ListProductFragment();
		productFragmentGrid.setMode(ListProductFragment.MODE_USER_PRODUCT);
		getFragmentManager().beginTransaction().replace(R.id.content_list, productFragmentGrid).commit();
	}

}