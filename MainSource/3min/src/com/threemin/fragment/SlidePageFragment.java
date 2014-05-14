package com.threemin.fragment;

import com.threemins.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SlidePageFragment extends Fragment {
	
	public static final String ARG_PAGE = "page";
	private int mPageNumber;
	
	public static SlidePageFragment create(int pageNumber) {
        SlidePageFragment fm = new SlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fm.setArguments(args);
        return fm;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.slide_page, container, false);

      // Set the title view to show the page number.
		ImageView img = (ImageView) rootView.findViewById(R.id.img_slide_page);
		switch (mPageNumber) {
			case 0:
				img.setImageResource(R.drawable.login1);
				break;
				
			case 1:
				img.setImageResource(R.drawable.login2);
				break;
				
			case 2:
				img.setImageResource(R.drawable.login3);
				break;
	
			default:
				break;
		}
  	
  	
      return rootView;
	}
	
	
	public int getPageNumber() {
		return mPageNumber;
	}
	
}
