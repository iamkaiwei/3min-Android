package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.threemin.fragment.ListProductFragment;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class UserLikeProductActivity extends SwipeBackActivity {
	
	LoginButton mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mLoginButton = (LoginButton) findViewById(R.id.activity_detail_btn_login_facebook);
		
		//swipe back
		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		
		initActionBar();

		if (savedInstanceState == null) {
			// TODO
//			ListProductFragment listProductFragment=new ListProductFragment(UserLikeProductActivity.this, mLoginButton);
		    ListProductFragment listProductFragment=new ListProductFragment();
		    
		    int mode = getIntent().getIntExtra(CommonConstant.INTENT_PRODUCT_MODE, ListProductFragment.MODE_MY_PRODUCT);
		    if (mode == ListProductFragment.MODE_MY_PRODUCT) {
                setTitle(getString(R.string.my_items));
            } else {
                setTitle(getString(R.string.my_likes));
            }
			listProductFragment.setMode(mode);
			getSupportFragmentManager().beginTransaction().add(R.id.container,listProductFragment).commit();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(UserLikeProductActivity.this, requestCode, resultCode, data);
        } else {
            Log.i("tructran", "UserLikeProductActivity session null");
        }
	}
	
	@Override
	public void onBackPressed() {
	    // TODO Auto-generated method stub
	    scrollToFinishActivity();
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
	
	public LoginButton getLoginButton() {
	    return mLoginButton;
	}
}
