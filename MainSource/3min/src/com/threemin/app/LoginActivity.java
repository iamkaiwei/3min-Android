package com.threemin.app;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.threemins.R;
import com.threemin.fragment.SlidePageFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonUti;
import com.threemin.webservice.AuthorizeWebservice;

public class LoginActivity extends FragmentActivity {
	
	Context mContext;
	
	private static final int NUM_PAGES = 3;
	
	private ViewPager mViewPager;
	private PagerAdapter mAdapter;
	
	ImageView mImgLoading1;
	ImageView mImgLoading2;
	ImageView mImgLoading3;
	
//	Button mBtnLoginFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
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
        //=========================================================================
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(), 
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                }
//        } catch (NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
        //=========================================================================
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
        	doLogin(session.getAccessToken());
		}
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
		Session session = Session.getActiveSession();
		session.onActivityResult(this, requestCode, resultCode, data);
		doLogin(session.getAccessToken());
	}
	
	private void doLogin(String accessToken) {
		new StartingHomeActivity().execute(accessToken);
	}
	
	private class StartingHomeActivity extends AsyncTask<String, Void, UserModel> {

		@Override
		protected UserModel doInBackground(String... params) {
			try {
				UserModel user = new AuthorizeWebservice().login(params[0], CommonUti.getDeviceId(mContext), mContext);
				Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(intent);
				return user;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}

}
