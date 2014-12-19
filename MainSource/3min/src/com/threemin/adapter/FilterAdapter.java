package com.threemin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.threemins.R;

public class FilterAdapter extends BaseAdapter {
    
    public static final int NUMBER_FILTER = 6;
    
    static class ViewHolder {
        public ImageView ivImage;
        public TextView tvFilterName;
    }

    private int selectedPositon;
	@Override
	public int getCount() {
		return NUMBER_FILTER;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.inflater_image_filter, parent, false);
			
			ViewHolder vh = new ViewHolder();
			vh.tvFilterName = (TextView) convertView.findViewById(R.id.inflater_filter_text);
			vh.ivImage = (ImageView) convertView.findViewById(R.id.inflater_filter_img);
			
			convertView.setTag(vh);
		}
		
		ViewHolder vh = (ViewHolder) convertView.getTag();
		
		initItem(position, vh.ivImage, vh.tvFilterName);
		if(position==selectedPositon){
		    convertView.setSelected(true);
		} else {
		    convertView.setSelected(false);
		}
		return convertView;
	}

	private void initItem(int position, ImageView image, TextView text) {
		//it's just a dummy text and waiting for confirm from Kaiwei about the text
		switch (position) {
		case 0:
			image.setImageResource(R.drawable.effect_original);
			text.setText("Original");
			break;
		case 1:
			image.setImageResource(R.drawable.effect_contrast);
			text.setText("Contrast");
			break;
		case 2:
			image.setImageResource(R.drawable.effect_sepia);
			text.setText("Sepia");
			break;
		case 3:
			image.setImageResource(R.drawable.effect_amatoka);
			text.setText("Amatorka");
			break;
		case 4:
			image.setImageResource(R.drawable.effect_grayscale);
			text.setText("Grayscale");
			break;
		case 5:
			image.setImageResource(R.drawable.effect_vignette);
			text.setText("Vignette");
			break;

		default:
			break;
		}
	}

    public int getSelectedPositon() {
        return selectedPositon;
    }

    public void setSelectedPositon(int selectedPositon) {
        this.selectedPositon = selectedPositon;
    }

}
