package com.lanna.droidlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button {

	public CustomButton(final Context context) {
		super(context);
    	CustomTypeface.setCustomTypeface(context, null, this);
    }

    public CustomButton(final Context context, final AttributeSet attrs) {
    	super(context, attrs); 
    	CustomTypeface.setCustomTypeface(context, attrs, this);
    }

    public CustomButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        CustomTypeface.setCustomTypeface(context, attrs, this);
    }

}
