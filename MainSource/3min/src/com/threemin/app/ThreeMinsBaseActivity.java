package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.os.Bundle;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ThreeMinsApplication.isActive = true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        ThreeMinsApplication.isActive = false;
    }
    
    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
    }
    
    public void disableSwipeBack() {
        getSwipeBackLayout().setEnableGesture(false);
    }
    
}
