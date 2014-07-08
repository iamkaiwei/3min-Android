package com.threemin.app;

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
import com.threemin.fragment.ListProductFragment;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class UserLikeProductActivity extends FragmentActivity {
	
	LoginButton mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mLoginButton = (LoginButton) findViewById(R.id.activity_detail_btn_login_facebook);
		
		initActionBar();

		if (savedInstanceState == null) {
			// TODO
			ListProductFragment listProductFragment=new ListProductFragment(UserLikeProductActivity.this, mLoginButton);
			listProductFragment.setMode(ListProductFragment.MODE_USER_LIKED_PRODUCT);
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

	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setIcon(R.drawable.ic_back);
		bar.setDisplayShowTitleEnabled(true);
		
		((ImageView)findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);
		
	}
}
