package com.threemin.adapter;

import java.util.ArrayList;

import com.threemin.model.MessageModel;
import com.threemins.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
		return convertView;
	}

	private View initTheirMessage(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(R.layout.layout_chat_theirs, parent, false);
		TextView tvChat = (TextView) convertView.findViewById(R.id.layout_chat_theirs_tv_chat);
		tvChat.setText(mData.get(position).getMsg());
		return convertView;
	}
	
	public void addData(MessageModel messageModel){
		mData.add(messageModel);
		notifyDataSetChanged();
	}

}
