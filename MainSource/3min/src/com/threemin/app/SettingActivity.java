package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemins.R;

public class SettingActivity extends ThreeMinsBaseActivity implements OnClickListener {
	
	public static final int ACTIONS_LOG_OUT = 99;
	
	RelativeLayout mRlAbout, mRlSendSuggestions, mRlRate;
	LinearLayout mLlLogOut;
	ImageView mImgShareFacebook, mImgShareEmail, mImgShareMessage;
	
	GoogleApiClient mGoogleApiClient;
	Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Plus.API, null).addScope(Plus.SCOPE_PLUS_PROFILE).build();
		mGoogleApiClient.connect();
		mContext = this;
		
		initWidgets();
		initListener();
	}
	
	private void initWidgets() {
		mRlAbout = (RelativeLayout) findViewById(R.id.act_setting_rl_about);
		mRlSendSuggestions = (RelativeLayout) findViewById(R.id.act_setting_rl_send_suggestions);
		mRlRate = (RelativeLayout) findViewById(R.id.act_setting_rl_rate);
		mLlLogOut = (LinearLayout) findViewById(R.id.act_setting_ll_log_out);
		
		mRlAbout.setSelected(false);
		mRlSendSuggestions.setSelected(false);
		mRlRate.setSelected(false);
		
		mImgShareFacebook = (ImageView) findViewById(R.id.act_setting_img_share_facebook);
		mImgShareEmail = (ImageView) findViewById(R.id.act_setting_img_share_email);
		mImgShareMessage = (ImageView) findViewById(R.id.act_setting_img_share_message);
		
		mImgShareFacebook.setOnClickListener(CommonUti.shareFacebook(mContext));
		mImgShareEmail.setOnClickListener(CommonUti.shareEmail(mContext));
		mImgShareMessage.setOnClickListener(CommonUti.shareMessage(mContext));
		
	}
	
	private void initListener() {
		mRlAbout.setOnClickListener(this);
		mRlSendSuggestions.setOnClickListener(CommonUti.feedbackClick(mContext));
		mRlRate.setOnClickListener(this);
		mLlLogOut.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.act_setting_rl_about:
			doAbout();
			break;


		case R.id.act_setting_rl_rate:
			doRate();
			break;

		case R.id.act_setting_ll_log_out:
			doLogOut();
			break;
		default:
			break;
		}

	}
	
	public void doAbout() {
		startActivity(new Intent(SettingActivity.this, AboutActivity.class));
	}
	
	public void doRate() {
	    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
	    try {
	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
	    } catch (android.content.ActivityNotFoundException anfe) {
	        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
	    }
	}
	
	public void doLogOut() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			session.closeAndClearTokenInformation();
		}

		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
		}
		
		SharedPreferences  sharedPreferences = getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.remove(LoginActivity.LOGIN_KEY);
		editor.commit();
		
		PreferenceHelper.getInstance(mContext).setCurrentUser("");
		PreferenceHelper.getInstance(mContext).setTokken("");
		PreferenceHelper.getInstance(mContext).setNumberActivities(0);
		
		finish();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
	}
}
