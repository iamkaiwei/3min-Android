package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
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
import com.threemin.fragment.DetailFragment;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemins.R;

public class DetailActivity extends ThreeMinsBaseActivity {
	
	ImageView mImgBack;
	TextView mTvTitle;
	LoginButton mLoginButton;
	SwipeBackLayout mSwipeBack;
	DetailFragment mDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		mLoginButton = (LoginButton) findViewById(R.id.activity_detail_btn_login_facebook);
		String productID = getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID);
		Log.i("DetailActivity", "Product ID: " + productID);
		
		//check if intent is from push notification
        boolean isFromPushNotification = getIntent().getBooleanExtra(CommonConstant.INTENT_IS_FROM_PUSH_NOTIFICATION, false);
        
		initActionBar();

		if (savedInstanceState == null || (productID != null && productID.length() > 0)) {
			Log.i("DetailActivity", "Create Fragment");
			mDetailFragment = new DetailFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.container, mDetailFragment).commit();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(DetailActivity.this, requestCode, resultCode, data);
        } else {
            Log.i("tructran", "DetailActivity session null");
        }
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if (mDetailFragment != null) {
            mDetailFragment.refreshTopComment();
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
	
	public LoginButton getLoginButton() {
		return mLoginButton;
	}

}
