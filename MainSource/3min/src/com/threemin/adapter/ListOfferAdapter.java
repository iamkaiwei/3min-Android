package com.threemin.adapter;

import java.util.List;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.Conversation;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

public class ListOfferAdapter extends BaseAdapter {
    
    static class ViewHolder {
        public ImageView ivAvatar;
        public TextView tvName;
        public TextView tvTime;
        public TextView tvOffer;
    }

	List<Conversation> data;

	public ListOfferAdapter(List<Conversation> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
	    if (data == null) {
            return 0;
        }
		return data.size();
	}

	@Override
	public Object getItem(int position) {
	    if (data == null) {
            return null;
        }
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
	        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	        convertView = inflater.inflate(R.layout.inflater_conversation, parent, false);
	        
	        ViewHolder vh = new ViewHolder();
	        vh.ivAvatar = (ImageView) convertView.findViewById(R.id.inflater_heeader_product_image);
	        vh.tvName = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_name);
	        vh.tvTime = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_time);
	        vh.tvOffer = (TextView) convertView.findViewById(R.id.inflater_heeader_product_tv_offered_price);
	        convertView.setTag(vh);
        }
		Conversation conversation = data.get(position);
		ViewHolder vh = (ViewHolder) convertView.getTag();
		//
		if (conversation.getUser() != null) {
			UrlImageViewHelper.setUrlDrawable(vh.ivAvatar, conversation.getUser().getFacebook_avatar(), R.drawable.avatar_loading);

			vh.tvName.setText(conversation.getUser().getFullName());

			vh.tvTime.setText(DateUtils.getRelativeTimeSpanString(conversation.getLastest_update() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
			
			vh.tvOffer.setText(parent.getContext().getString(R.string.activity_chat_price_offered_label) + ": " + conversation.getOffer() + CommonConstant.CURRENCY);
			
		}

		return convertView;
	}

}
