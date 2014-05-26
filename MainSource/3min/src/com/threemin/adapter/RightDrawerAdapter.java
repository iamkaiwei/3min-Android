package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RightDrawerAdapter extends ArrayAdapter<String> {
	
	public RightDrawerAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
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
		
		return convertView;
	}
}
