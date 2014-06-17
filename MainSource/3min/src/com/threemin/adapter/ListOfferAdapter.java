package com.threemin.adapter;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.Conversation;
import com.threemins.R;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListOfferAdapter extends BaseAdapter {

	List<Conversation> data;
	public ListOfferAdapter(List<Conversation> data){
		this.data=data;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Conversation conversation=data.get(position);
		LayoutInflater inflater=LayoutInflater.from(parent.getContext());
		convertView=inflater.inflate(R.layout.inflater_conversation, parent, false);
//		
		if(conversation.getUser()!=null){
		ImageView avatar=(ImageView) convertView.findViewById(R.id.inflater_heeader_product_image);
		UrlImageViewHelper.setUrlDrawable(avatar, conversation.getUser().getFacebook_avatar());
		
		TextView name=(TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_name);
		name.setText(conversation.getUser().getFullName());
		
		TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_time);
		tv_time.setText(DateUtils.getRelativeTimeSpanString(conversation.getLastest_update() * 1000,
				System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
		}
		
		return convertView;
	}

}
