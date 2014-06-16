package com.threemin.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.threemin.app.HomeActivity;
import com.threemin.model.FilterModel;
import com.threemins.R;

public class LeftFragment extends Fragment implements OnClickListener {
	
	public static final String FONT_REGULAR = "OpenSans-Regular.ttf";
	public static final String FONT_BOLD = "OpenSans-Bold.ttf";
	public static final int FILTER_ID_NULL = -1;
	
	EditText etSearch, etMinPrice, etMaxPrice;
	
	RelativeLayout rlPopular, rlRecent, rlLowest, rlHighest, rlNearest;
	TextView tvPopular, tvRecent, tvLowest, tvHighest, tvNearest;
	ImageView imgPopular, imgRecent, imgLowest, imgHighest, imgNearest;
	
	ImageView imgResetFilter;
	
	int selectedFilterID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_filter, container, false);
		selectedFilterID = -1;
		initWidgets(rootView);
		
		return rootView;
	}
	
	public void setMenuVisibility(final boolean visible) {
		super.setMenuVisibility(visible);
		if (visible) {
			Log.i("tructran", "left fragment");
		}
	}

	private void initWidgets(ViewGroup rootView) {
		
		etSearch = (EditText) rootView.findViewById(R.id.fm_filter_et_search);
		etMinPrice = (EditText) rootView.findViewById(R.id.fm_filter_et_min);
		etMaxPrice = (EditText) rootView.findViewById(R.id.fm_filter_et_max);
		
		tvPopular = (TextView) rootView.findViewById(R.id.fm_filter_tv_popular);
		tvRecent = (TextView) rootView.findViewById(R.id.fm_filter_tv_recent);
		tvLowest = (TextView) rootView.findViewById(R.id.fm_filter_tv_lowest);
		tvHighest = (TextView) rootView.findViewById(R.id.fm_filter_tv_highest);
		tvNearest = (TextView) rootView.findViewById(R.id.fm_filter_tv_nearest);
		
		rlPopular = (RelativeLayout) rootView.findViewById(R.id.fm_filter_rl_popular);
		rlRecent = (RelativeLayout) rootView.findViewById(R.id.fm_filter_rl_recent);
		rlLowest = (RelativeLayout) rootView.findViewById(R.id.fm_filter_rl_lowest);
		rlHighest = (RelativeLayout) rootView.findViewById(R.id.fm_filter_rl_highest);
		rlNearest = (RelativeLayout) rootView.findViewById(R.id.fm_filter_rl_nearest);
		
		imgPopular = (ImageView) rootView.findViewById(R.id.fm_filter_img_check_popular);
		imgRecent = (ImageView) rootView.findViewById(R.id.fm_filter_img_check_recent);
		imgLowest = (ImageView) rootView.findViewById(R.id.fm_filter_img_check_lowest);
		imgHighest = (ImageView) rootView.findViewById(R.id.fm_filter_img_check_highest);
		imgNearest = (ImageView) rootView.findViewById(R.id.fm_filter_img_check_nearest);
		
		imgResetFilter = (ImageView) rootView.findViewById(R.id.fm_filter_img_reset_filter);
		
		setEvents(rootView);
	}
	
	private void setEvents(ViewGroup rootView) {
		rlPopular.setSelected(false);
		rlRecent.setSelected(false);
		rlLowest.setSelected(false);
		rlHighest.setSelected(false);
		rlNearest.setSelected(false);
		
		rlPopular.setOnClickListener(this);
		rlRecent.setOnClickListener(this);
		rlLowest.setOnClickListener(this);
		rlHighest.setOnClickListener(this);
		rlNearest.setOnClickListener(this);
		
		imgResetFilter.setOnClickListener(this);
	}


	public void setUnselectedAll() {
		
		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), FONT_REGULAR);
		tvPopular.setTypeface(typeface);
		tvRecent.setTypeface(typeface);
		tvLowest.setTypeface(typeface);
		tvHighest.setTypeface(typeface);
		tvNearest.setTypeface(typeface);
		
		imgPopular.setVisibility(View.INVISIBLE);
		imgRecent.setVisibility(View.INVISIBLE);
		imgLowest.setVisibility(View.INVISIBLE);
		imgHighest.setVisibility(View.INVISIBLE);
		imgNearest.setVisibility(View.INVISIBLE);
		
		rlPopular.setSelected(false);
		rlRecent.setSelected(false);
		rlLowest.setSelected(false);
		rlHighest.setSelected(false);
		rlNearest.setSelected(false);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fm_filter_rl_popular:
			setRowSelected(v, tvPopular, imgPopular);
			break;

		case R.id.fm_filter_rl_lowest:
			setRowSelected(v, tvLowest, imgLowest);
			break;

		case R.id.fm_filter_rl_highest:
			setRowSelected(v, tvHighest, imgHighest);
			break;

		case R.id.fm_filter_rl_recent:
			setRowSelected(v, tvRecent, imgRecent);
			break;

		case R.id.fm_filter_rl_nearest:
			setRowSelected(v, tvNearest, imgNearest);
			break;
			
		case R.id.fm_filter_img_reset_filter:
			setUnselectedAll();
			selectedFilterID = FILTER_ID_NULL;
			etMaxPrice.setText("");
			etMinPrice.setText("");
			break;

		default:
			break;
		}
	}
	
	public void setRowSelected(View v, TextView tv, ImageView img) {
		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), FONT_BOLD);
		Typeface typeface_disable = Typeface.createFromAsset(getActivity().getAssets(), FONT_REGULAR);
		if (v.isSelected()) {
			v.setSelected(false);
			selectedFilterID = FILTER_ID_NULL;
			tv.setTypeface(typeface_disable);
			img.setVisibility(View.INVISIBLE);
		} else {
			setUnselectedAll();
			v.setSelected(true);
			selectedFilterID = v.getId();
			tv.setTypeface(typeface);
			img.setVisibility(View.VISIBLE);
			((HomeActivity)getActivity()).doFilter();
		}
	}
	
	public FilterModel getFilterModel() {
		FilterModel model = new FilterModel();
		model.setFilterID(selectedFilterID);
		model.setMaxPrice(etMaxPrice.getText().toString());
		model.setMinPrice(etMinPrice.getText().toString());
		return model;
	}
}