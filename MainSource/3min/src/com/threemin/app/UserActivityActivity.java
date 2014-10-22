package com.threemin.app;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.threemin.fragment.UserActivityFragment;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class UserActivityActivity extends ThreeMinsBaseActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_activity);
		if (arg0 == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new UserActivityFragment()).commit();
		}
		
		initActionBar();
	}

	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setIcon(R.drawable.ic_close);
		bar.setDisplayShowTitleEnabled(true);

		((ImageView) findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {

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
