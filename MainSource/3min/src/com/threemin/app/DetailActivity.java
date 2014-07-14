package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.threemins.R;

public class DetailActivity extends SwipeBackActivity {
	
	ImageView mImgBack;
	TextView mTvTitle;
	LoginButton mLoginButton;
	SwipeBackLayout mSwipeBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		mLoginButton = (LoginButton) findViewById(R.id.activity_detail_btn_login_facebook);
		String productID = getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID);
		Log.i("DetailActivity", "Product ID: " + productID);
		
		// Init the swipe back mechanism
//				SwipeBack.attach(this, Position.LEFT)
//				.setContentView(R.layout.activity_detail)
//				.setSwipeBackView(R.layout.swipeback_default);
		mSwipeBack = getSwipeBackLayout();
		mSwipeBack.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		
		initActionBar();

		if (savedInstanceState == null || (productID != null && productID.length() > 0)) {
			Log.i("DetailActivity", "Create Fragment");
			getSupportFragmentManager().beginTransaction().add(R.id.container, new DetailFragment()).commit();
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
	public void onBackPressed(){
		scrollToFinishActivity();
//		super.onBackPressed();
//		overridePendingTransition(R.anim.swipeback_stack_to_front,
//				R.anim.swipeback_stack_right_out);
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
