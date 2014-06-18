package com.threemin.adapter;

import java.util.ArrayList;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.MessageModel;
import com.threemins.R;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter {
	
	ArrayList<MessageModel> mData;
	Context mContext;
	
	public MessageAdapter(Context context, ArrayList<MessageModel> data) {
		mContext = context;
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mData.get(position).isTheirs()) {
			return initTheirMessage(position, convertView, parent);
		} else {
			return initMyMessage(position, convertView, parent);
		}
		
	}

	private View initMyMessage(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(R.layout.layout_chat_mine, parent, false);
		TextView tvChat = (TextView) convertView.findViewById(R.id.layout_chat_mine_tv_chat);
		tvChat.setText(mData.get(position).getMsg());
		
		ImageView avatar=(ImageView) convertView.findViewById(R.id.layout_chat_mine_avatar);
		UrlImageViewHelper.setUrlDrawable(avatar, mData.get(position).getUserModel().getFacebook_avatar());
		
		TextView tv_time = (TextView) convertView.findViewById(R.id.layout_chat_mine_tv_time);
		tv_time.setText(DateUtils.getRelativeTimeSpanString(mData.get(position).getTimestamp() * 1000,
				System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
		return convertView;
	}

	private View initTheirMessage(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(R.layout.layout_chat_theirs, parent, false);
		TextView tvChat = (TextView) convertView.findViewById(R.id.layout_chat_theirs_tv_chat);
		tvChat.setText(mData.get(position).getMsg());
		
		ImageView avatar=(ImageView) convertView.findViewById(R.id.layout_chat_theirs_avatar);
		if (mData.get(position).getUserModel() != null) {
			UrlImageViewHelper.setUrlDrawable(avatar, mData.get(position).getUserModel().getFacebook_avatar());
		}
		
		TextView tv_time = (TextView) convertView.findViewById(R.id.layout_chat_theirs_tv_time_chat);
		tv_time.setText(DateUtils.getRelativeTimeSpanString(mData.get(position).getTimestamp() * 1000,
				System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
		return convertView;
	}
	
	public void addData(MessageModel messageModel){
		mData.add(messageModel);
		notifyDataSetChanged();
	}
	
	public void addListData(ArrayList<MessageModel> messageModels){
		mData.addAll(messageModels);
		notifyDataSetChanged();
	}
}
