package com.threemin.fragment;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.UserModel;
import com.threemin.uti.PreferenceHelper;
import com.threemins.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RightFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_right, container, false);
		UserModel userModel=PreferenceHelper.getInstance(getActivity()).getCurrentUser();
		ImageView img = (ImageView) rootView.findViewById(R.id.nav_avatar);
		UrlImageViewHelper.setUrlDrawable(img, userModel.getFacebook_avatar());
		
		TextView tv_name = (TextView) rootView.findViewById(R.id.navigation_name);
		tv_name.setText(userModel.getFullName());
		
		TextView tv_email = (TextView) rootView.findViewById(R.id.navigation_email);
		tv_email.setText(userModel.getEmail());
		return rootView;
	}

}