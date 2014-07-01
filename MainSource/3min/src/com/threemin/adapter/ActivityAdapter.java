package com.threemin.adapter;

import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ActivityModel;
import com.threemin.model.CategoryModel;
import com.threemins.R;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityAdapter extends BaseAdapter {
	
	Context mContext;
	List<ActivityModel> mData;
	
	public ActivityAdapter(Context context, List<ActivityModel> data ){
		mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mData.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.inflater_activity, parent, false);
		}
		
		ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.inflater_activity_avatar);
		TextView tvContent = (TextView) convertView.findViewById(R.id.inflater_activity_tv_content);
		TextView tvMessage = (TextView) convertView.findViewById(R.id.inflater_activity_tv_message);
		TextView tvTime = (TextView) convertView.findViewById(R.id.inflater_activity_tv_time);
		ImageView imgProduct = (ImageView) convertView.findViewById(R.id.inflater_activity_product);
		
		UrlImageViewHelper.setUrlDrawable(imgAvatar, mData.get(position).getUser().getFacebook_avatar());
		
		String content = mData.get(position).getContent();
		String fullName = mData.get(position).getUser().getFullName();
		String newContent = content.substring(fullName.length(), content.length());
		Log.i("tructran", "content " + content + "\nFull name " + fullName + "\nNew content " + newContent);
//		bgRedColor = "ff401a";
//		bgGreyColor = "a9a9a9";
		tvContent.setText(Html.fromHtml("<font color=\"#ff401a\">" + fullName + "</font>" + "<font color=\"#a9a9a9\">" + " " + newContent + "</font>"));
		
		tvTime.setText(DateUtils.getRelativeTimeSpanString(mData.get(position).getUpdateTime() * 1000,
				System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
		
		tvMessage.setVisibility(View.GONE);
		imgProduct.setVisibility(View.INVISIBLE);
		return convertView;
	}

}
