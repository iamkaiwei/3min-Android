package com.lanna.droidlib.view;

import android.view.View;

public interface IViewHolder<T> {
	public abstract int getLayoutId();
	public abstract void initView(View convertView);
	public abstract void resetView(int position);
	public abstract void initData(T modelNotNull, int position);
}