package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.threemin.receiver.IntentReceiver;
import com.threemin.uti.CommonUti;
import com.threemins.R;
import com.urbanairship.push.PushManager;

/**
 * All activities of 3mins app are sub-classes of this class
 * There are some exceptions:
 * + CategoryActivity: ListActivity
 * + FeedbackDialogActivity: Activity
 * + ImageViewActivity: Activity
 * 
 * In these activities, we have to override onPause and onResume to update value of var ThreeMinsApplication.isActive
 * */

public class ThreeMinsBaseActivity extends SwipeBackActivity {
    
    protected PopupWindow mBaseActivity_PopupFakeNotification;
    protected View mBaseActivity_LayoutPopup;
    protected TextView mBaseActivity_TvPopupContent;
    protected BroadcastReceiver mBaseActivity_BroadcastReceiver_NewNotification = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            mBasicActivity_CurrentNotificationTimestamp = System.currentTimeMillis();
            showNewNotification(intent);
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ThreeMinsApplication.isActive = true;
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mBaseActivity_BroadcastReceiver_NewNotification, 
                new IntentFilter(IntentReceiver.ACTION_NOTIFY_NEW_NOTIFICATION)
                );
    }
    
    @Override
    protected void onPause() {
        ThreeMinsApplication.isActive = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBaseActivity_BroadcastReceiver_NewNotification);
        super.onPause();
    }
    
    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
    }
    
    public void disableSwipeBack() {
        getSwipeBackLayout().setEnableGesture(false);
    }
    
    //Fake notification
    protected final Handler mBasicActivityHandler = new Handler();
    protected static long mBasicActivity_CurrentNotificationTimestamp;
    
    public void showNewNotification(Intent intent) {
        final long notificationTimestamp = mBasicActivity_CurrentNotificationTimestamp;
        
        if (mBaseActivity_PopupFakeNotification == null) {
            createFakeNotification();
        }
        
        setDataToFakeNotification(intent);
        
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
            mBaseActivity_PopupFakeNotification.showAtLocation(mBaseActivity_LayoutPopup, Gravity.NO_GRAVITY, 0, result);
            mBasicActivityHandler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    if (mBaseActivity_PopupFakeNotification != null 
                            && mBaseActivity_PopupFakeNotification.isShowing()
                            && notificationTimestamp == mBasicActivity_CurrentNotificationTimestamp) {
                        mBaseActivity_PopupFakeNotification.dismiss();
                    }
                }
            }, 4000);
        }
    }
    
    public void createFakeNotification() {
        mBaseActivity_LayoutPopup = LayoutInflater.from(this).inflate(R.layout.layout_fake_notification, null);
        mBaseActivity_PopupFakeNotification = new PopupWindow(mBaseActivity_LayoutPopup, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mBaseActivity_PopupFakeNotification.setBackgroundDrawable(new BitmapDrawable(getResources()));
        mBaseActivity_PopupFakeNotification.setOutsideTouchable(true);
        mBaseActivity_PopupFakeNotification.setTouchable(true);
        mBaseActivity_PopupFakeNotification.setAnimationStyle(R.style.activity_home_popup_animation);
        mBaseActivity_TvPopupContent = (TextView) mBaseActivity_LayoutPopup.findViewById(R.id.fake_notification_tv_content);
    }
    
    public void setDataToFakeNotification(Intent intent) {
        String msg = intent.getStringExtra(PushManager.EXTRA_ALERT);
        mBaseActivity_TvPopupContent.setText(msg);
        final Intent intentToStartActivity = IntentReceiver.createIntent(this, intent);
        mBaseActivity_TvPopupContent.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (mBaseActivity_PopupFakeNotification != null && mBaseActivity_PopupFakeNotification.isShowing()) {
                    mBaseActivity_PopupFakeNotification.dismiss();
                    startActivity(intentToStartActivity);
                    CommonUti.addAnimationWhenStartActivity(ThreeMinsBaseActivity.this);
                }
            }
        });
    }
    
}
