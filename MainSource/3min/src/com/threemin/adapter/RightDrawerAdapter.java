package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RightDrawerAdapter extends ArrayAdapter<String> {
	
	public static final String FONT_REGULAR = "OpenSans-Regular.ttf";
	public static final String FONT_BOLD = "OpenSans-Bold.ttf";
	
	int mSelectedPosition;
	
	public RightDrawerAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		mSelectedPosition = -1;
	}
	
	public void setSelectedPosition(int position) {
		mSelectedPosition = position;
		notifyDataSetChanged();
	}
	
	public int getSelectedPosition() {
		return mSelectedPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(com.threemins.R.layout.inflater_right_drawer_listview_item, parent, false);
		}
		
		TextView textview = (TextView) convertView.findViewById(com.threemins.R.id.inflater_right_drawer_listview_item_tv);
		ImageView check = (ImageView) convertView.findViewById(com.threemins.R.id.inflater_right_drawer_listview_item_chk);
		
		textview.setText(getItem(position));
		
		if (position == mSelectedPosition) {
			Typeface typeface = Typeface.createFromAsset(parent.getContext().getAssets(), FONT_BOLD);
			textview.setTypeface(typeface);
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			check.setVisibility(View.VISIBLE);
		} else {
			Typeface typeface = Typeface.createFromAsset(parent.getContext().getAssets(), FONT_REGULAR);
			textview.setTypeface(typeface);
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			check.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
}
