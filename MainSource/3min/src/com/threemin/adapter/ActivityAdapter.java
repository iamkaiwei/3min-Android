package com.threemin.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ActivityModel;
import com.threemin.model.UserModel;
import com.threemins.R;

public class ActivityAdapter extends BaseAdapter {
	
	Context mContext;
	List<ActivityModel> mData;
	
	static class ViewHolder {
	    public ImageView   ivAvatar;
	    public TextView    tvContent;
	    public TextView    tvMessage;
	    public TextView    tvTime;
	    public ImageView   ivProduct;
	}
	
	public ActivityAdapter(Context context, List<ActivityModel> data ){
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
	    if (mData == null) {
            return -1;
        }
		return mData.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.inflater_activity, parent, false);
			
			ViewHolder vh = new ViewHolder();
			vh.ivAvatar = (ImageView) convertView.findViewById(R.id.inflater_activity_avatar);
	        vh.tvContent = (TextView) convertView.findViewById(R.id.inflater_activity_tv_content);
	        vh.tvMessage = (TextView) convertView.findViewById(R.id.inflater_activity_tv_message);
	        vh.tvTime = (TextView) convertView.findViewById(R.id.inflater_activity_tv_time);
	        vh.ivProduct = (ImageView) convertView.findViewById(R.id.inflater_activity_product);
	        
	        convertView.setTag(vh);
		}
		
		ActivityModel model = mData.get(position);
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		UrlImageViewHelper.setUrlDrawable(holder.ivAvatar, model.getUser().getFacebook_avatar(), R.drawable.avatar_loading);
		
		String content = model.getContent();
		String fullName = model.getUser().getFullName();
		String newContent = content.substring(fullName.length(), content.length());
		Log.i("tructran", "content " + content + "\nFull name " + fullName + "\nNew content " + newContent);
		
//		bgRedColor = "ff401a";
//		bgGreyColor = "a9a9a9";
		holder.tvContent.setText(Html.fromHtml("<font color=\"#ff401a\">" + fullName + "</font>" + "<font color=\"#a9a9a9\">" + " " + newContent + "</font>"));
		
		holder.tvTime.setText(DateUtils.getRelativeTimeSpanString(model.getUpdateTime() * 1000,
				System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
		
		holder.tvMessage.setVisibility(View.GONE);
		
		String imgURL = model.getDisplayImageUrl();
		if (imgURL != null && imgURL.length() > 0) {
            holder.ivProduct.setVisibility(View.VISIBLE);
		    UrlImageViewHelper.setUrlDrawable(holder.ivProduct, model.getDisplayImageUrl(), R.drawable.stuff_img);
        } else {
            holder.ivProduct.setVisibility(View.GONE);
        }
		return convertView;
	}
	
	public void setListActivities(List<ActivityModel> list) {
        mData = list;
        this.notifyDataSetChanged();
    }

}
