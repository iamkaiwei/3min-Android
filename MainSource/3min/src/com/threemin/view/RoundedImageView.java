package com.threemin.view;


import com.threemins.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
	
	public RoundedImageView(Context context) {
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		float radius = getResources().getDimension(R.dimen.common_radius_pixel); // angle of round corners
		int w = this.getWidth();
		int h = this.getHeight();
		Path clipPath = new Path();
		RectF rect = new RectF(0, 0, w, h);
		
		clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
		clipPath.addRect(0, radius, w, h, Path.Direction.CW);
		canvas.clipPath(clipPath);
		super.onDraw(canvas);
		
	}
		
}
