package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import com.threemin.fragment.FolowersFollowingFragment;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemins.R;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FollowerFollowingActivity extends ThreeMinsBaseActivity {
	
	public final String tag = "FollowerFollowingActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follower_following);
		boolean isGetFollowers = getIntent().getBooleanExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, true);
		
		initActionBar();
		if (isGetFollowers) {
            setTitle(getString(R.string.followers));
        } else {
            setTitle(getString(R.string.following));
        }
		if (savedInstanceState == null) {
			Log.i(tag, "Create Fragment");
			getSupportFragmentManager().beginTransaction().add(R.id.activity_follower_following_container, new FolowersFollowingFragment()).commit();
		}
	}
	
	private void initActionBar() {
        ActionBar bar = getActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_back);
        bar.setDisplayShowTitleEnabled(true);
        
        ((ImageView)findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);
        
    }
	
}
