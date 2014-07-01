package com.threemin.app;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.threemins.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SettingActivity extends Activity implements OnClickListener {
	
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
		
		mImgShareFacebook.setOnClickListener(this);
		mImgShareEmail.setOnClickListener(this);
		mImgShareMessage.setOnClickListener(this);
		
	}
	
	private void initListener() {
		mRlAbout.setOnClickListener(this);
		mRlSendSuggestions.setOnClickListener(this);
		mRlRate.setOnClickListener(this);
		mLlLogOut.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.act_setting_rl_about:
			doAbout();
			break;

		case R.id.act_setting_rl_send_suggestions:
			doSendSuggestions();
			break;

		case R.id.act_setting_rl_rate:
			doRate();
			break;

		case R.id.act_setting_ll_log_out:
			doLogOut();
			break;
			
		case R.id.act_setting_img_share_facebook:
			doShareFacebook();
			break;
			
		case R.id.act_setting_img_share_email:
			doShareEmail();
			break;	
			
		case R.id.act_setting_img_share_message:
			doShareMessage();
			break;
			
		default:
			break;
		}

	}
	
	public void doAbout() {
	}
	
	public void doSendSuggestions() {
	}
	
	public void doRate() {
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
		finish();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public void doShareFacebook() {
		if (mImgShareFacebook.isSelected()) {
			mImgShareFacebook.setSelected(false);
		} else {
			mImgShareFacebook.setSelected(true);
		}
	}
	
	public void doShareEmail() {
		if (mImgShareEmail.isSelected()) {
			mImgShareEmail.setSelected(false);
		} else {
			mImgShareEmail.setSelected(true);
		}
	}
	
	public void doShareMessage() {
		if (mImgShareMessage.isSelected()) {
			mImgShareMessage.setSelected(false);
		} else {
			mImgShareMessage.setSelected(true);
		}
	}
	
}
