package com.threemin.app;

import com.threemin.fragment.FolowersFollowingFragment;
import com.threemins.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class FollowerFollowingActivity extends FragmentActivity {
	
	public final String tag = "FollowerFollowingActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follower_following);
		
		if (savedInstanceState == null) {
			Log.i(tag, "Create Fragment");
			getSupportFragmentManager().beginTransaction().add(R.id.activity_follower_following_container, new FolowersFollowingFragment()).commit();
		}
	}
	
}
