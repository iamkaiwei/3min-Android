package com.threemin.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

//public class ThreeMinsBaseActivity extends FragmentActivity {
public class ThreeMinsBaseActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scrollToFinishActivity();
    }
    
    public void disableSwipeBack() {
        getSwipeBackLayout().setEnableGesture(false);
    }
    
}
