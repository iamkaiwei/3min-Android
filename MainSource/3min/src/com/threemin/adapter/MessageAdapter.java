package com.threemin.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.MessageModel;
import com.threemins.R;

public class MessageAdapter extends BaseAdapter {
    
    public static final int MY_MESSAGE = 0;
    public static final int THEIR_MESSAGE = 1;
    public static final int COUNT_ITEM_TYPE = 2;
    
    static class ViewHolderMyMessage {
        public ImageView ivAvatar;
        public TextView tvChat;
        public TextView tvTime;
    }
    
    static class ViewHolderTheirMessage {
        public ImageView ivAvatar;
        public TextView tvChat;
        public TextView tvTime;
    }
    
	ArrayList<MessageModel> mData;
	Context mContext;
	
	public MessageAdapter(Context context, ArrayList<MessageModel> data) {
		mContext = context;
		mData = data;
	}
	
	@Override
	public int getCount() {
	    if (mData == null) {
            return 0;
        }
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
	    if (mData == null) {
            return null;
        }
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
	    return COUNT_ITEM_TYPE;
	}
	
	@Override
	public int getItemViewType(int position) {
	    if (mData == null) {
            return 0;
        }
	    MessageModel m = mData.get(position);
	    if (m.isTheirs()) {
            return THEIR_MESSAGE;
        } else {
            return MY_MESSAGE;
        }
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    MessageModel model = mData.get(position);
	    int type = getItemViewType(position);
	    
	    if (convertView == null) {
            if (type == MY_MESSAGE) {
                convertView = createMyMessageView(parent);
            } else if (type == THEIR_MESSAGE) {
                convertView = createTheirMessageView(parent);
            }
        }
	    
	    if (type == MY_MESSAGE) {
            bindMyMessage(convertView, parent, model);
        } else {
            bindTheirMessage(convertView, parent, model);
        }
	    
	    return convertView;
	}
	
	private View createMyMessageView(ViewGroup parent) {
	    LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.layout_chat_mine, parent, false);
        
        ViewHolderMyMessage vh = new ViewHolderMyMessage();
        vh.ivAvatar = (ImageView) v.findViewById(R.id.layout_chat_mine_avatar);
        vh.tvChat = (TextView) v.findViewById(R.id.layout_chat_mine_tv_chat);
        vh.tvTime = (TextView) v.findViewById(R.id.layout_chat_mine_tv_time);
        
        v.setTag(vh);
        return v;
	}
	
	private View createTheirMessageView(ViewGroup parent) {
	    LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.layout_chat_theirs, parent, false);
        
        ViewHolderTheirMessage vh = new ViewHolderTheirMessage();
        vh.tvChat = (TextView) v.findViewById(R.id.layout_chat_theirs_tv_chat);
        vh.ivAvatar =(ImageView) v.findViewById(R.id.layout_chat_theirs_avatar);
        vh.tvTime = (TextView) v.findViewById(R.id.layout_chat_theirs_tv_time_chat);
        
        v.setTag(vh);
        return v;
	}
	
	private void bindTheirMessage(View convertView, ViewGroup parent, MessageModel model) {
	    if (!(convertView.getTag() instanceof ViewHolderTheirMessage)) {
            convertView = createTheirMessageView(parent);
        }
	    ViewHolderTheirMessage vh = (ViewHolderTheirMessage) convertView.getTag();
        vh.tvChat.setText(model.getMsg());
        UrlImageViewHelper.setUrlDrawable(vh.ivAvatar, model.getUserModel().getFacebook_avatar(), R.drawable.avatar_loading);
        vh.tvTime.setText(DateUtils.getRelativeTimeSpanString(model.getTimestamp() * 1000,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
	}
	
	private void bindMyMessage(View convertView, ViewGroup parent, MessageModel model) {
	    if (!(convertView.getTag() instanceof ViewHolderMyMessage)) {
            convertView = createMyMessageView(parent);
        }
	    ViewHolderMyMessage vh = (ViewHolderMyMessage) convertView.getTag();
        vh.tvChat.setText(model.getMsg());
        
        if (model.getUserModel() != null) {
            UrlImageViewHelper.setUrlDrawable(vh.ivAvatar, model.getUserModel().getFacebook_avatar(), R.drawable.avatar_loading);
        }
        
        vh.tvTime.setText(DateUtils.getRelativeTimeSpanString(model.getTimestamp() * 1000,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
	}

	public void addData(MessageModel messageModel){
		mData.add(messageModel);
		notifyDataSetChanged();
	}
	
	public void addListData(ArrayList<MessageModel> messageModels){
		mData.addAll(messageModels);
		notifyDataSetChanged();
	}
	
	public void addOldMessages(ArrayList<MessageModel> oldMessages) {
	    mData.addAll(0, oldMessages);
	    notifyDataSetChanged();
	}
}
