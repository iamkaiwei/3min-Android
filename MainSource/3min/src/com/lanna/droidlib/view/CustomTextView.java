package com.lanna.droidlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {
	
    public CustomTextView(final Context context) {
    	super(context);
    	CustomTypeface.setCustomTypeface(context, null, this);
    }

    public CustomTextView(final Context context, final AttributeSet attrs) {
    	super(context, attrs);
    	CustomTypeface.setCustomTypeface(context, attrs, this);
    }

    public CustomTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        CustomTypeface.setCustomTypeface(context, attrs, this);
    }

}
