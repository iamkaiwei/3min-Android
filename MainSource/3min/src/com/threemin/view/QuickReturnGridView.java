/*
 * Copyright 2013 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.threemin.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;

import com.etsy.android.grid.StaggeredGridView;

public class QuickReturnGridView extends StaggeredGridView {

    private int mItemCount;
    private int mItemOffsetY[];
    private boolean scrollIsComputed = false;
    private int mHeight;

    private final static int DOUBLE_TAP = 2; // constant for double tap event
    private final static int SINGLE_TAP = 1; // constant for single tap event
    private final static int DELAY = ViewConfiguration.getDoubleTapTimeout();
    private int mPositionHolder = -1;
    private int mPosition = -1;
    private OnItemDoubleTapLister mOnDoubleTapListener = null;
    private AdapterView mParent = null;
    private View mView = null;
    private long mId = 12315;
    private Message mMessage = null;
    private static String TAG = "DoubleTapListView";
    private boolean mTookFirstEvent = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case SINGLE_TAP:
                Log.i(TAG, "Single tap entry");
                mPositionHolder = -1;
                mTookFirstEvent = false;
                mOnDoubleTapListener.OnSingleTap(mParent, mView, mPosition, mId);
                break;
            case DOUBLE_TAP:
                mPositionHolder = -1;
                mTookFirstEvent = false;
                Log.i(TAG, "Double tap entry");
                mOnDoubleTapListener.OnDoubleTap(mParent, mView, mPosition, mId);
                break;
            }
        }
    };

    public QuickReturnGridView(Context context) {
        super(context);
    }

    public QuickReturnGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getListHeight() {
        return mHeight;
    }

    public void computeScrollY() {
        Log.d("height", "height=" + getHeight());
        scrollIsComputed = false;
        mHeight = 0;
        mItemCount = getAdapter().getCount();
        if (mItemCount <= 0) {
            return;
        }
        if (mItemOffsetY == null || mItemCount != mItemOffsetY.length) {
            mItemOffsetY = new int[mItemCount];
        }
        for (int i = 0; i < mItemCount; i++) {
            View view = getAdapter().getView(i, null, this);
            view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mItemOffsetY[i] = mHeight;
            mHeight += view.getMeasuredHeight();
            // System.out.println(mHeight);
        }
        scrollIsComputed = true;
    }

    public boolean scrollYIsComputed() {
        return scrollIsComputed;
    }

    public int getComputedScrollY() {
        int pos, nScrollY, nItemY;
        View view = null;
        pos = getFirstVisiblePosition();
        view = getChildAt(0);
        if (view == null) {
            nItemY = 0;
        } else {
            nItemY = view.getTop();
        }
        if (mItemOffsetY.length == 0) {
            return 0;
        }
        nScrollY = mItemOffsetY[pos] - nItemY;
        return nScrollY;
    }

    public void setOnItemDoubleClickListener(OnItemDoubleTapLister listener) {
        mOnDoubleTapListener = listener;
        // If the listener is null then throw exception
        if (mOnDoubleTapListener == null)
            throw new IllegalArgumentException("OnItemDoubleTapListener cannot be null");
        else {
            // register the default onItemClickListener to proceed with
            // listening
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    mParent = parent;
                    mView = view;
                    mPosition = position;
                    mId = id;
                    if (!mTookFirstEvent) // Testing if first tap occurred
                    {
                        mPositionHolder = position;
                        // this will hold the position variable from first
                        // event.
                        // In case user presses any other item (position)
                        mTookFirstEvent = true;
                        mMessage = mMessage == null ? new Message() : mHandler.obtainMessage();
                        // ÓRecyclingÓ the message, instead creating new
                        // instance we get the old one
                        mHandler.removeMessages(SINGLE_TAP);
                        mMessage.what = SINGLE_TAP;
                        mHandler.sendMessageDelayed(mMessage, DELAY);
                    } else {
                        if (mPositionHolder == position) {
                            mHandler.removeMessages(SINGLE_TAP);
                            // Removing the message that was queuing for
                            // scheduled
                            // sending after elapsed time > DELAY,
                            // immediately when we have second event,
                            // when the time is < DELAY
                            mPosition = position;
                            mMessage = mHandler.obtainMessage();
                            // obtaining old message instead creating new one
                            mMessage.what = DOUBLE_TAP;
                            mHandler.sendMessageAtFrontOfQueue(mMessage);
                            // Sending the message immediately when we have
                            // second event,
                            // when the time is < DELAY
                            mTookFirstEvent = false;
                        } else {
                            /*
                             * When the position is different from previously
                             * stored position in mPositionHolder
                             * (mPositionHolder!=position). Wait for double tap
                             * on the new item at position which is different
                             * from mPositionHolder. Setting the flag
                             * mTookFirstEvent back to false.
                             * 
                             * However we can ignore the case when we have
                             * mPosition!=position, when we want, to do
                             * something when onSingleTap/onItemClickListener
                             * are called.
                             */
                            mMessage = mHandler.obtainMessage();
                            mHandler.removeMessages(SINGLE_TAP);
                            mTookFirstEvent = true;
                            mMessage.what = SINGLE_TAP;
                            mPositionHolder = position;
                            mHandler.sendMessageDelayed(mMessage, DELAY);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemDoubleTapLister {
        public void OnDoubleTap(AdapterView parent, View view, int position, long id);

        public void OnSingleTap(AdapterView parent, View view, int position, long id);
    }
}