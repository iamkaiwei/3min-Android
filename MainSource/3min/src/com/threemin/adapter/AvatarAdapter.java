package com.threemin.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.UserModel;
import com.threemins.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class AvatarAdapter extends BaseAdapter implements SpinnerAdapter {
	Context context;
	List<String> values;
	UserModel userModel;

	public AvatarAdapter(Context context, UserModel user) {
		this.context = context;
		values = new ArrayList<String>(Arrays.asList(this.context.getString(R.string.navigation_mylisting),
				this.context.getString(R.string.navigation_offer), this.context.getString(R.string.navigation_like)));
		this.userModel = user;
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.inflater_avatar, parent, false);
		ImageView img = (ImageView) convertView.findViewById(R.id.nav_avatar);
		UrlImageViewHelper.setUrlDrawable(img, userModel.getFacebook_avatar());
		
		TextView tv_name = (TextView) convertView.findViewById(R.id.navigation_name);
		tv_name.setText(userModel.getFullName());
		
		TextView tv_email = (TextView) convertView.findViewById(R.id.navigation_email);
		tv_email.setText(userModel.getEmail());
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		TextView textView = (TextView)inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);;
		textView.setText(values.get(position));
		return textView;
	}

}
