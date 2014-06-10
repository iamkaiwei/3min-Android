package com.threemin.fragment;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemins.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductImageFragment extends Fragment{
	public static final String ARG_PAGE = "page";
	String imagelink;
	public static ProductImageFragment create(String imageLink) {
		Log.d("link", imageLink);
		ProductImageFragment fm = new ProductImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, imageLink);
        fm.setArguments(args);
        return fm;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imagelink = getArguments().getString(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.inflate_image, container, false);
		ImageView img=(ImageView) rootView.findViewById(R.id.image);
		UrlImageViewHelper.setUrlDrawable(img, imagelink);
  	
      return rootView;
	}
	
	
	
}
