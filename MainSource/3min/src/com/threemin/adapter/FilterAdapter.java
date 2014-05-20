package com.threemin.adapter;

import com.threemins.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.inflater_image_filter, parent, false);
		}
		ImageView image = (ImageView) convertView.findViewById(R.id.inflater_filter_img);
		TextView text = (TextView) convertView.findViewById(R.id.inflater_filter_text);
		initItem(position, image, text);
		return convertView;
	}

	private void initItem(int position, ImageView image, TextView text) {
		//it's just a dummy text and waiting for confrim from Kaiwei about the text
		switch (position) {
		case 0:
			image.setImageResource(R.drawable.contrast_house);
			text.setText("Contrast");
			break;
		case 1:
			image.setImageResource(R.drawable.sepia);
			text.setText("Sepia");
			break;
		case 2:
			image.setImageResource(R.drawable.amatorka);
			text.setText("Amatorka");
			break;
		case 3:
			image.setImageResource(R.drawable.grayscale);
			text.setText("Grayscale");
			break;
		case 4:
			image.setImageResource(R.drawable.sobel_edge);
			text.setText("Sobel Edge");
			break;

		default:
			break;
		}
	}

}
