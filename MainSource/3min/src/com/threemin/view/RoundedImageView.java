package com.threemin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.threemins.R;

public class RoundedImageView extends ImageView {

    private Paint mPaint;
    private Paint mPaint2;

    private Bitmap mBitmap;

    private Canvas mCanvas;

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);

        mPaint2 = new Paint();
        mPaint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        mBitmap.eraseColor(Color.TRANSPARENT);

        float radius = getResources().getDimension(R.dimen.common_radius_pixel);

        Rect bottomRect = new Rect(0, (int) (h / 2), (int) (w), (int) h);
		RectF mRect = new RectF(0, 0, w, h);
		mCanvas.drawRoundRect(mRect, radius, radius, mPaint);
        mCanvas.drawRect(bottomRect, mPaint);
        mCanvas.saveLayer(0, 0, w, h, mPaint2, Canvas.ALL_SAVE_FLAG);
        super.onDraw(mCanvas);
        mCanvas.restore();

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
