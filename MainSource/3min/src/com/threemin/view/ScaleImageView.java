package com.threemin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ScaleImageView extends ImageView {
    
    public ScaleImageView(Context context) {
        super(context);
    }
    
    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int widthDrawable = getDrawable().getIntrinsicWidth();
        int heightDrawable = getDrawable().getIntrinsicHeight();
        int widthImageView = getMeasuredWidth();
        float ratio = (float)widthImageView/(float)widthDrawable;
        int heightImageView = (int)(heightDrawable * ratio);
        
        setMeasuredDimension(widthImageView, heightImageView);
    }

}
