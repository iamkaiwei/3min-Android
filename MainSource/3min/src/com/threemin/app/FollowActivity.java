package com.threemin.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.threemin.fragment.DetailFragment;
import com.threemin.fragment.ListProductFragment;
import com.threemin.fragment.ProductFragmentGrid;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.RelationshipWebService;
import com.threemins.R;

public class FollowActivity extends FragmentActivity {
	public final String tag = "FollowActivity";
	
	protected ListProductFragment mFragmentContent;
	LoginButton mLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follow);
		mLoginButton = (LoginButton)findViewById(R.id.activity_follow_btn_login_facebook);
		mFragmentContent = new ListProductFragment(this, mLoginButton);
		mFragmentContent.setMode(ListProductFragment.MODE_FOLLOWED_PRODUCT);
		
		if (savedInstanceState == null) {
			Log.i(tag, "Create Fragment");
			getSupportFragmentManager().beginTransaction().add(R.id.container, mFragmentContent).commit();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(FollowActivity.this, requestCode, resultCode, data);
        } else {
            Log.i(tag, "FollowActivity: session null");
        }
	}
	
}
