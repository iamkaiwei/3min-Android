package com.threemin.app;

import java.io.IOException;

import android.R.bool;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.threemins.R;
import com.threemin.fragment.SlidePageFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.webservice.AuthorizeWebservice;
import com.threemin.webservice.CategoryWebservice;

public class LoginActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnClickListener {
	
	Context mContext;
	
	private static final int NUM_PAGES = 3;
	
	private ViewPager mViewPager;
	private PagerAdapter mAdapter;
	
	ImageView mImgLoading1;
	ImageView mImgLoading2;
	ImageView mImgLoading3;
	
	//Variables for login G+
	//Button login
	SignInButton mBtnLoginGooglePlus;
	
	// Track whether the sign-in button has been clicked
	private boolean mSignInClicked;
	
	private ConnectionResult mConnectionResult;
	
	//Request code used to invoke sign in user interaction
	private static final int RC_SIGN_IN = 0;
	
	//Client used to interact with Google APIs
	private GoogleApiClient mGoogleApiClient;
	
	//Flag indicating that a PendingIntent is in progress
	private boolean mIntentInProgress;
	
	private void resolveSignInError() {
		  if (mConnectionResult.hasResolution()) {
		    try {
		      mIntentInProgress = true;
//		      startIntentSenderForResult(mConnectionResult.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
		      mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
		    } catch (SendIntentException e) {
		      // The intent was canceled before it was sent.  Return to the default
		      // state and attempt to connect to get an updated ConnectionResult.
		      mIntentInProgress = false;
		      mGoogleApiClient.connect();
		    }
		  }
		}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        //Hide the action bar
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        
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
        	.addScope(Plus.SCOPE_PLUS_LOGIN)
        	.build();

        //check if user has logged in with facebook
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
        	doLogin(session.getAccessToken());
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



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
			
			if (requestCode == RC_SIGN_IN) {
			    if (resultCode != RESULT_OK) {
			      mSignInClicked = false;
			    }

			    mIntentInProgress = false;

			    if (!mGoogleApiClient.isConnecting()) {
			      mGoogleApiClient.connect();
			    }
			  }
		} else {
			Session session = Session.getActiveSession();
			session.onActivityResult(this, requestCode, resultCode, data);
			doLogin(session.getAccessToken());
		}
		
	}
	
	private void doLogin(String accessToken) {
		new StartingHomeActivity().execute(accessToken);
	}
	
	//AsyncTask to start HomeActivity after user login with Facebook
	private class StartingHomeActivity extends AsyncTask<String, Void, UserModel> {

		@Override
		protected UserModel doInBackground(String... params) {
			try {
				UserModel user = new AuthorizeWebservice().login(params[0], CommonUti.getDeviceId(mContext), mContext, CommonConstant.TYPE_FACEBOOK_TOKEN);
				if(user!=null){
					CategoryWebservice.getInstance().getAllCategory(mContext);
				}
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
				return user;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}

	
	//Methods of ConnectionCallbacks and OnConnectionFailedListener Interface
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
		
		new GoogleTokenGetter().execute();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	//onClickListener for Google plus login button
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_login_google_plus && !mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	//Get Google+ token:
	public String getGooglePlusToken() {
		final String SCOPES = "https://www.googleapis.com/auth/plus.login";
		String token = null;
		try {
			token = GoogleAuthUtil.getToken(mContext, Plus.AccountApi.getAccountName(mGoogleApiClient) , "oauth2:" + SCOPES);
			Log.i("tructran","Get Token: " + token);
		} catch (UserRecoverableAuthException e) {
			e.printStackTrace();
			Log.i("Get Token exception", e.toString());
			
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("tructran", "Get Token exception IOException: "+ e.toString());
		} catch (GoogleAuthException e) {
			e.printStackTrace();
			Log.i("tructran", "GoogleAuthException: "+ e.toString());
		}
		return token;
	}
	
	private class GoogleTokenGetter extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String gg_token = getGooglePlusToken();
			
			try {
				Log.i("tructran", "Debug: token to start: " + params[0]);
				UserModel user = new AuthorizeWebservice().login(gg_token, CommonUti.getDeviceId(mContext), mContext, CommonConstant.TYPE_GOOGLE_TOKEN);
				if(user!=null){
					CategoryWebservice.getInstance().getAllCategory(mContext);
				}
				Log.i("tructran", "Debug: User name: " + user.getFullName());
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return gg_token;
		}
	}
	
	//method set text to SignInButton of Google +
	protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
	    for (int i = 0; i < signInButton.getChildCount(); i++) {
	        View v = signInButton.getChildAt(i);
	        if (v instanceof TextView) {
	            TextView mTextView = (TextView) v;
	            mTextView.setText(buttonText);
	            mTextView.setTextSize(getResources().getDimension(R.dimen.login_screen_btn_login_google_plus_text_size));
	            return;
	        }
	    }
	}
}
