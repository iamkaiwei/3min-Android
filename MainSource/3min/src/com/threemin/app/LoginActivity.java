package com.threemin.app;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.threemin.fragment.SlidePageFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.AuthorizeWebservice;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class LoginActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener,
		OnClickListener {
	
	//public const:
	public static final String PREF_NAME = "3MinsPref";
	public static final String LOGIN_KEY = "LoginWith";
	public static final int LOGIN_KEY_FB = 11;
	public static final int LOGIN_KEY_GG = 22;
	
	//preference to track user login with facebook or google+
	SharedPreferences mSharedPreferences;

	Context mContext;

	private static final int NUM_PAGES = 3;

	private ViewPager mViewPager;
	private PagerAdapter mAdapter;

	ImageView mImgLoading1;
	ImageView mImgLoading2;
	ImageView mImgLoading3;

	// Variables for login G+
	// Button login
	SignInButton mBtnLoginGooglePlus;
	LoginButton btn_login_facebook;
	// Track whether the sign-in button has been clicked
	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	// Request code used to invoke sign in user interaction
	private static final int RC_SIGN_IN = 0;
	private static final int MY_ACTIVITYS_AUTH_REQUEST_CODE = 1;
	// Client used to interact with Google APIs
	private GoogleApiClient mGoogleApiClient;

	// Flag indicating that a PendingIntent is in progress
	private boolean mIntentInProgress;
	boolean isRequestPermisson;

	private void resolveSignInError() {
		if (mConnectionResult != null && mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				// startIntentSenderForResult(mConnectionResult.getIntentSender(),
				// RC_SIGN_IN, null, 0, 0, 0);
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mSharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

		// Hide the action bar
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		isRequestPermisson = false;
		mContext = this;

		mImgLoading1 = (ImageView) findViewById(R.id.img_login_loading_1);
		mImgLoading2 = (ImageView) findViewById(R.id.img_login_loading_2);
		mImgLoading3 = (ImageView) findViewById(R.id.img_login_loading_3);

		mImgLoading1.setImageResource(R.drawable.loading_1);
		mImgLoading2.setImageResource(R.drawable.loading_2);
		mImgLoading3.setImageResource(R.drawable.loading_2);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new SlidePagerAdapter(getSupportFragmentManager());
		if (mAdapter == null) {
			Toast.makeText(this, "Null", Toast.LENGTH_LONG).show();
		}
		btn_login_facebook=(LoginButton) findViewById(R.id.btn_login_facebook);
		btn_login_facebook.setPublishPermissions(Arrays.asList("email","user_photos","publish_stream"));
//		btn_login_facebook.setReadPermissions(Arrays.asList("basic_info"));
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					mImgLoading1.setImageResource(R.drawable.loading_1);
					mImgLoading2.setImageResource(R.drawable.loading_2);
					mImgLoading3.setImageResource(R.drawable.loading_2);
					break;

				case 1:
					mImgLoading1.setImageResource(R.drawable.loading_2);
					mImgLoading2.setImageResource(R.drawable.loading_1);
					mImgLoading3.setImageResource(R.drawable.loading_2);
					break;

				case 2:
					mImgLoading1.setImageResource(R.drawable.loading_2);
					mImgLoading2.setImageResource(R.drawable.loading_2);
					mImgLoading3.setImageResource(R.drawable.loading_1);
					break;

				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
        
        //prepare for login G+
        mBtnLoginGooglePlus = (SignInButton) findViewById(R.id.btn_login_google_plus);
        mBtnLoginGooglePlus.setOnClickListener(this);
        setGooglePlusButtonText(mBtnLoginGooglePlus, getResources().getString(R.string.activity_login_btn_login_gg));
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        	.addConnectionCallbacks(this)
        	.addOnConnectionFailedListener(this)
        	.addApi(Plus.API, null)
        	.addScope(Plus.SCOPE_PLUS_PROFILE)
        	.build();

        //check if user has logged in with facebook
        if (mSharedPreferences.contains(LOGIN_KEY)) {
			int loggedWith = mSharedPreferences.getInt(LOGIN_KEY, 0);
			Session session = Session.getActiveSession();
	        if (session != null && session.isOpened() && loggedWith == LOGIN_KEY_FB) {
				doLogin(session.getAccessToken());
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mGoogleApiClient.disconnect();
	}

	private class SlidePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

		public SlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return SlidePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == RC_SIGN_IN) {
				if (resultCode != RESULT_OK) {
					mSignInClicked = false;
				}

				mIntentInProgress = false;

				if (!mGoogleApiClient.isConnecting()) {
					mGoogleApiClient.connect();
				}
		} else if (requestCode == MY_ACTIVITYS_AUTH_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				getGooglePlusToken();
			} else {
				mGoogleApiClient.disconnect();
			}
		} else {
			Session session = Session.getActiveSession();
			session.onActivityResult(this, requestCode, resultCode, data);
			doLogin(session.getAccessToken());
		}

	}

	private void doLogin(String accessToken) {
		new LoginWithFacebookTask().execute(accessToken);
	}

	// AsyncTask to start HomeActivity after user login with Facebook
	private class LoginWithFacebookTask extends AsyncTask<String, Void, UserModel> {
		ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(mContext);
			if (!isFinishing()) {
				dialog.setMessage(getString(R.string.please_wait));
				dialog.show();
			}
		}
		@Override
		protected UserModel doInBackground(String... params) {
			try {
				UserModel user = new AuthorizeWebservice().login(params[0], CommonUti.getDeviceId(mContext), mContext,
						CommonConstant.TYPE_FACEBOOK_TOKEN);
				
				if (user != null) {
					CategoryWebservice.getInstance().getAllCategory(mContext);
				} 
				
				return user;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(UserModel result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if(result!=null){
				//save that user has logged in with facebook
				Editor editor = mSharedPreferences.edit();
				editor.putInt(LOGIN_KEY, LOGIN_KEY_FB);
				editor.commit();
				
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			} else {
				Log.i("tructran", "Can not authorize webservice: user returned is null");
				new AlertDialog.Builder(mContext)
					.setTitle("Authorization failed!")
					.setMessage("User is not authorized")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							logoutFacebook();
							dialog.dismiss();
						}
					})
					.show();
				
			}
		}

	}
	
	public void logoutFacebook() {
		Session session =  Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
			}
		} else {
			session = new Session(mContext);
			Session.setActiveSession(session);
			session.closeAndClearTokenInformation();
		}
	}

	// Methods of ConnectionCallbacks and OnConnectionFailedListener Interface
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		// Log.i("tructran","Account name: " +
		// Plus.AccountApi.getAccountName(mGoogleApiClient));
		if (isRequestPermisson == false)
			new LoginWithGoogleTask().execute();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	// onClickListener for Google plus login button
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_login_google_plus && !mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	// Get Google+ token:
	public String getGooglePlusToken() {
		final String SCOPES = "https://www.googleapis.com/auth/plus.profile.emails.read";
		String token = null;
		try {
			if (!mGoogleApiClient.isConnected()) {
				mGoogleApiClient.connect();
				isRequestPermisson = false;
				return null;
			}
			token = GoogleAuthUtil.getToken(mContext, Plus.AccountApi.getAccountName(mGoogleApiClient), "oauth2:"
					+ SCOPES);
			Log.i("tructran", "Get Token: " + token);
		} catch (UserRecoverableAuthException e) {
			e.printStackTrace();
			Log.i("Get Token exception", e.toString());
			isRequestPermisson = true;
			startActivityForResult(e.getIntent(), MY_ACTIVITYS_AUTH_REQUEST_CODE);
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("tructran", "Get Token exception IOException: " + e.toString());

		} catch (GoogleAuthException e) {
			e.printStackTrace();
			Log.i("tructran", "GoogleAuthException: " + e.toString());
		}
		return token;
	}

	private class LoginWithGoogleTask extends AsyncTask<Void, Void, UserModel> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(mContext);
			if (!isFinishing()) {
				dialog.setMessage(getString(R.string.please_wait));
				dialog.show();
			}
		}

		@Override
		protected UserModel doInBackground(Void... params) {
			String gg_token = getGooglePlusToken();
			UserModel user = null;
			if (gg_token == null) {
				return user;
			}
			try {
				Log.i("tructran", "Debug: token to start: " + gg_token);
				user = new AuthorizeWebservice().login(gg_token, CommonUti.getDeviceId(mContext), mContext,
						CommonConstant.TYPE_GOOGLE_TOKEN);
				if (user != null) {
					CategoryWebservice.getInstance().getAllCategory(mContext);
				}
				Log.i("tructran", "Debug: User name: " + user.getFullName());

				return user;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(UserModel result) {
			super.onPostExecute(result);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				if (result != null) {
					//save that user has logged with google plus
					Editor editor = mSharedPreferences.edit();
					editor.putInt(LOGIN_KEY, LOGIN_KEY_GG);
					editor.commit();
					
					Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("tructran", "Login GG exception" + e.toString());
			}
		}
	}

	
	//method set text to SignInButton of Google +
	protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
	    for (int i = 0; i < signInButton.getChildCount(); i++) {
	        View v = signInButton.getChildAt(i);
	        if (v instanceof TextView) {
	            TextView mTextView = (TextView) v;
	            mTextView.setText(buttonText);
	            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
	            return;
	        }
	    }
	}


}
