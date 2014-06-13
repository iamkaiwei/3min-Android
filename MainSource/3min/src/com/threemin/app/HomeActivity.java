package com.threemin.app;

import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.threemin.adapter.CategoryAdapter;
import com.threemin.fragment.HomeFragment;
import com.threemin.fragment.LeftFragment;
import com.threemin.fragment.RightFragment;
import com.threemin.model.CategoryModel;
import com.threemin.view.CustomSpinner;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class HomeActivity extends FragmentActivity {
	
	//action bar widgets
	RelativeLayout mRLActionbarLeft, mRLActionbarCenter, mRLActionbarRight;
	ImageView mImgActionbarLeftClose, mImgActionbarRightClose;
	ImageView mImgActionbarSearch, mImgActionbarProfile;
	
	RelativeLayout mRLActionbarCenterTitle;
	CustomSpinner mSpnActionbarCenterTitle;
	Button mBtnActionbarCenterTitle;
	ImageView mImgActionbarCenterTitleArrow;
	
	
	
	//view pager
	public static final int NUM_PAGES = 3;
	public static final int PAGE_LEFT = 0;
	public static final int PAGE_CENTER = 1;
	public static final int PAGE_RIGHT = 2;
	ViewPager mViewPagerMainContent;
	PagerAdapter mViewPagerAdapter;
	CategoryAdapter categoryAdapter;

	Context mContext;
	private static final int REQUEST_UPLOAD = 3;

	//right drawer
	RelativeLayout layoutFilter;

	GoogleApiClient mGoogleApiClient;
	HomeFragment homeFragment;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;
		
//		initAvatar();
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Plus.API, null).addScope(Plus.SCOPE_PLUS_PROFILE)
				.build();
		mGoogleApiClient.connect();
		initActionBar();
//		homeFragment=new HomeFragment();
//		getFragmentManager().beginTransaction().replace(R.id.content_layout, homeFragment).commit();

		//view pager implementation
		homeFragment=new HomeFragment();
		leftFragment=new LeftFragment();
		rightFragment = new RightFragment();
		mViewPagerMainContent = (ViewPager) findViewById(R.id.activity_home_view_pager);
		mViewPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mViewPagerMainContent.setAdapter(mViewPagerAdapter);
		mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
		
		mSpnActionbarCenterTitle.setSelected(true);
		mImgActionbarCenterTitleArrow.setSelected(true);
		mImgActionbarProfile.setSelected(false);
		mImgActionbarSearch.setSelected(false);
		
		mViewPagerMainContent.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case PAGE_LEFT:
					mImgActionbarSearch.setSelected(true);
					
					mSpnActionbarCenterTitle.setSelected(false);
					mImgActionbarCenterTitleArrow.setSelected(false);
					categoryAdapter.notifyDataSetChanged();
					mSpnActionbarCenterTitle.setEnabled(false);
					mRLActionbarCenterTitle.setEnabled(false);
					mBtnActionbarCenterTitle.setVisibility(View.VISIBLE);
					
					mImgActionbarProfile.setSelected(false);
					break;
					
				case PAGE_CENTER:				
					mImgActionbarSearch.setSelected(false);
					
					mSpnActionbarCenterTitle.setSelected(true);
					mImgActionbarCenterTitleArrow.setSelected(true);
					categoryAdapter.notifyDataSetChanged();
					mSpnActionbarCenterTitle.setEnabled(true);
					mRLActionbarCenterTitle.setEnabled(true);
					mBtnActionbarCenterTitle.setVisibility(View.GONE);
					
					mImgActionbarProfile.setSelected(false);
					break;
					
				case PAGE_RIGHT:
					mImgActionbarSearch.setSelected(false);
					
					mSpnActionbarCenterTitle.setSelected(false);
					mImgActionbarCenterTitleArrow.setSelected(false);
					categoryAdapter.notifyDataSetChanged();
					mSpnActionbarCenterTitle.setEnabled(false);
					mRLActionbarCenterTitle.setEnabled(false);
					mBtnActionbarCenterTitle.setVisibility(View.VISIBLE);
					
					mImgActionbarProfile.setSelected(true);
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
		
	}



	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setHomeButtonEnabled(false);
		bar.setCustomView(R.layout.layout_custom_action_bar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		
		mRLActionbarLeft = (RelativeLayout) findViewById(R.id.rl_actionbar_left);
		mRLActionbarCenter = (RelativeLayout) findViewById(R.id.rl_actionbar_center);
		mRLActionbarRight = (RelativeLayout) findViewById(R.id.rl_actionbar_right);
		
		mImgActionbarLeftClose = (ImageView) findViewById(R.id.home_activity_action_bar_left_close);
		mImgActionbarRightClose = (ImageView) findViewById(R.id.home_activity_action_bar_right_close);
		
		mImgActionbarLeftClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
			}
		});
		
		mImgActionbarRightClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
			}
		});
		
		
		//action bar center
		mImgActionbarProfile = (ImageView) findViewById(R.id.home_activity_action_bar_center_img_profile);
		mImgActionbarProfile.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_RIGHT);
			}
		});
		
		mImgActionbarSearch = (ImageView) findViewById(R.id.home_activity_action_bar_center_img_search);
		mImgActionbarSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_LEFT);
			}
		});
		
		mSpnActionbarCenterTitle = (CustomSpinner) findViewById(R.id.home_activity_action_bar_center_title);
		new InitCategory().execute();
		mSpnActionbarCenterTitle.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				CategoryModel categoryModel=(CategoryModel) parent.getItemAtPosition(position);
				if(categoryModel.getName().equals(getString(R.string.browse))){
					onSwitchCate(null);
				} else {
				onSwitchCate(categoryModel);
				}
				categoryAdapter.swapView(position);
//				mSpnActionbarCenterTitle.setSelected(false);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		mBtnActionbarCenterTitle = (Button) findViewById(R.id.home_activity_action_bar_center_btn_title);
		mBtnActionbarCenterTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
				mSpnActionbarCenterTitle.setSelected(true);
				categoryAdapter.notifyDataSetChanged();
				mSpnActionbarCenterTitle.setEnabled(true);
				mRLActionbarCenterTitle.setEnabled(true);
				mBtnActionbarCenterTitle.setVisibility(View.GONE);
			}
		});
		
		mImgActionbarCenterTitleArrow = (ImageView) findViewById(R.id.home_activity_action_bar_center_title_arrow);
		
		mRLActionbarCenterTitle = (RelativeLayout) findViewById(R.id.home_activity_action_bar_center_title_rl);
		disableSpinnerBackground();
		mRLActionbarCenterTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSpnActionbarCenterTitle.performClick();
			}
		});
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
	}

	public OnClickListener doLogout() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
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
				startActivity(new Intent(mContext, LoginActivity.class));
			}
		};
	}
	
	//view pager implementation
	
	@Override
	public void onBackPressed() {
		if (mViewPagerMainContent.getCurrentItem() == PAGE_CENTER) {
			super.onBackPressed();
		} else {
			mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
		}
	}
	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 1) {
				return homeFragment;
			}
			if (position == 0) {
				return leftFragment;
			}
			return rightFragment;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}
	
	public void onSwitchCate(CategoryModel categoryModel){
		mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
		homeFragment.onSwichCategory(categoryModel);
		if(categoryModel==null){
			getActionBar().setTitle(R.string.browse);
		} else {
			getActionBar().setTitle(categoryModel.getName());
		}
	}
	
	//create list category to add to spinner
	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getAllCategory(HomeActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {
				
				 categoryAdapter = new CategoryAdapter(HomeActivity.this, result,true, mSpnActionbarCenterTitle);
				mSpnActionbarCenterTitle.setAdapter(categoryAdapter);
				mSpnActionbarCenterTitle.setSelected(true);
				mImgActionbarCenterTitleArrow.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && mSpnActionbarCenterTitle.isDropdownShowing()) {
			mSpnActionbarCenterTitle.setDropdownShowing(false);
			disableSpinnerBackground();
		} else {
			if (hasFocus) {
				Log.i("tructran", "window has no focus");
			} else {
				Log.i("tructran", "window has focus");
			}
			
			if (mSpnActionbarCenterTitle.isDropdownShowing()) {
				Log.i("tructran", "drop down is showing");
			} else {
				Log.i("tructran", "drop down is not showing");
			}
		}
	}
	
	//functions to set the background of spinner
	public void enableSpinnerBackground() {
		mRLActionbarCenterTitle.setBackgroundResource(R.drawable.bt_category);
	}
	
	public void disableSpinnerBackground() {
		mRLActionbarCenterTitle.setBackgroundColor(Color.TRANSPARENT);
	}
}
