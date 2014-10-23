package com.threemin.app;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.threemin.uti.CommonUti;
import com.threemins.R;

public class AboutActivity extends ThreeMinsBaseActivity {
	
	WebView mWvContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		initWidgets();
		initActionBar();
		
		mWvContent.loadUrl("file:///android_asset/about.html"); 
	}

	private void initWidgets() {
		mWvContent = (WebView) findViewById(R.id.activity_about_wv);
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
