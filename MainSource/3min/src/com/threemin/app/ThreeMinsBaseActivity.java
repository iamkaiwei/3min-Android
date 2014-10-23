package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.os.Bundle;

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
