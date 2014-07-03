package com.threemin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

import com.threemins.R;

public class CustomSpinner extends Spinner {

	Context context;
	boolean isDropDownShown;

	public CustomSpinner(Context context) {
		super(context);
		this.context = context;
		isDropDownShown = false;
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDropdownShowing(boolean isShowing) {
		isDropDownShown = isShowing;
	}

	public boolean isDropdownShowing() {
		return isDropDownShown;
	}

	@Override
	public boolean performClick() {
		Log.i("tructran", "" + "performClick");
		this.isDropDownShown = true;
//		((View)getParent()).setBackgroundResource(R.drawable.bt_category);
		setBackgroundResource(R.drawable.bg_spn_border_enable_arrow_enable);
		return super.performClick();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

}
