package com.threemin.app;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.threemin.adapter.CategoryAdapter;
import com.threemin.fragment.HomeFragment;
import com.threemin.fragment.LeftFragment;
import com.threemin.fragment.RightFragment;
import com.threemin.model.CategoryModel;
import com.threemin.model.FilterModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.view.CustomSpinner;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class HomeActivity extends SwipeBackActivity {

	// action bar widgets
	ImageView mImgActionbarSearch, mImgActionbarProfile;
	CustomSpinner mSpnActionbarCenterTitle;
	Button mBtnActionbarCenterTitle;

	// button to login facebook
	LoginButton mBtnLoginFacebook;

	// view pager
	public static final int NUM_PAGES = 3;
	public static final int PAGE_LEFT = 0;
	public static final int PAGE_CENTER = 1;
	public static final int PAGE_RIGHT = 2;
	ViewPager mViewPagerMainContent;
	PagerAdapter mViewPagerAdapter;
	CategoryAdapter categoryAdapter;
	int currentPage, prevPage;

	// filter:
	public static final int POPULAR_ID = R.id.fm_filter_rl_popular;
	public static final int LOWEST_ID = R.id.fm_filter_rl_lowest;
	public static final int HIGHEST_ID = R.id.fm_filter_rl_highest;
	public static final int RECENT_ID = R.id.fm_filter_rl_recent;
	public static final int NEAREST_ID = R.id.fm_filter_rl_nearest;

	Context mContext;
	// right drawer
	RelativeLayout layoutFilter;

	GoogleApiClient mGoogleApiClient;
	static HomeFragment homeFragment;
	static LeftFragment leftFragment;
	static RightFragment rightFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("access_token", PreferenceHelper.getInstance(this).getTokken());
		mContext = this;
		setContentView(R.layout.activity_home);
		
		//disable swipe back
		getSwipeBackLayout().setEnableGesture(false);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Plus.API, null).addScope(Plus.SCOPE_PLUS_PROFILE)
				.build();
		mGoogleApiClient.connect();
		

		// button login facebook
		mBtnLoginFacebook = (LoginButton) findViewById(R.id.activity_home_btn_login_facebook);

		// view pager implementation
		currentPage = PAGE_CENTER;
		prevPage = -1;
		mViewPagerMainContent = (ViewPager) findViewById(R.id.activity_home_view_pager);
		mViewPagerAdapter = new PagerAdapter(getSupportFragmentManager(),this);
		mViewPagerMainContent.setOffscreenPageLimit(3);
		mViewPagerMainContent.setAdapter(mViewPagerAdapter);
		mViewPagerMainContent.setCurrentItem(PAGE_CENTER);


		mViewPagerMainContent.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(final int position) {
				doPageChange(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		initActionBar();
		
	}
	
	@Override
	protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(HomeActivity.this, requestCode, resultCode, data);
        } else {
            Log.i("HomeActivity", "session null");
        }
        
	}
	
	public void doPageChange(int position) {
		prevPage = currentPage;
		currentPage = position;
		switch (position) {
		case PAGE_LEFT:
			mImgActionbarSearch.setSelected(true);
			setSpinnerSelected(false);
			mImgActionbarProfile.setSelected(false);
			break;

		case PAGE_CENTER:
			mImgActionbarSearch.setSelected(false);
			setSpinnerSelected(true);
			mImgActionbarProfile.setSelected(false);
			leftFragment.hideKeyboard();

			if (prevPage == PAGE_LEFT) {
				doFilter();
			}

			break;

		case PAGE_RIGHT:
			mImgActionbarSearch.setSelected(false);
			setSpinnerSelected(false);
			mImgActionbarProfile.setSelected(true);
			leftFragment.hideKeyboard();
			break;

		default:
			Log.i("tructran", "Page position: " + position);
			break;
		}
	}

	public void doFilter() {
		FilterModel model = leftFragment.getFilterModel();
		switch (model.getFilterID()) {
		case POPULAR_ID:
			break;

		case RECENT_ID:
			break;

		case LOWEST_ID:
			break;

		case HIGHEST_ID:
			break;

		case NEAREST_ID:
			break;

		default:
			break;
		}
	}

	public void setSpinnerSelected(boolean isSelected) {
		RelativeLayout rl = (RelativeLayout)mSpnActionbarCenterTitle.getChildAt(0);
		TextView tv = null;
		if (rl != null) {
			tv = (TextView)rl.getChildAt(0);
		}
		
		if (isSelected) {
			mSpnActionbarCenterTitle.setSelected(true);
			if (tv != null) {
				tv.setTextColor(getResources().getColor(R.color.home_action_bar_text_color_enable));
			}
			mSpnActionbarCenterTitle.setEnabled(true);
			mBtnActionbarCenterTitle.setVisibility(View.GONE);
		} else {
			mSpnActionbarCenterTitle.setSelected(false);
			if (tv != null) {
				tv.setTextColor(getResources().getColor(R.color.home_action_bar_text_color_disable));
			}
			mSpnActionbarCenterTitle.setEnabled(false);
			mBtnActionbarCenterTitle.setVisibility(View.VISIBLE);
		}
	}

	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayHomeAsUpEnabled(false);
		bar.setHomeButtonEnabled(false);
		bar.setCustomView(R.layout.layout_custom_action_bar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);

		// action bar center
		mImgActionbarProfile = (ImageView) findViewById(R.id.home_activity_action_bar_center_img_profile);
		mImgActionbarProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_RIGHT, true);
			}
		});

		mImgActionbarSearch = (ImageView) findViewById(R.id.home_activity_action_bar_center_img_search);
		mImgActionbarSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_LEFT, true);
			}
		});

		mSpnActionbarCenterTitle = (CustomSpinner) findViewById(R.id.home_activity_action_bar_center_title);
		new InitCategory(this).execute();
		mSpnActionbarCenterTitle
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						CategoryModel categoryModel = (CategoryModel) parent
								.getItemAtPosition(position);
						String cate = getString(R.string.browse);
						if (categoryModel.getName().equals("Everything") || categoryModel.getName().equals("Tất cả")) {
							onSwitchCate(null);
						} else {
							onSwitchCate(categoryModel);
						}
						categoryAdapter.swapView(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		mBtnActionbarCenterTitle = (Button) findViewById(R.id.home_activity_action_bar_center_btn_title);
		mBtnActionbarCenterTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_CENTER, true);
				setSpinnerSelected(true);
			}
		});

		disableSpinnerBackground();
        mSpnActionbarCenterTitle.setSelected(true);
        mImgActionbarProfile.setSelected(false);
        mImgActionbarSearch.setSelected(false);
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

	// view pager implementation

	@Override
	public void onBackPressed() {
		if (mViewPagerMainContent.getCurrentItem() == PAGE_CENTER) {
			super.onBackPressed();
		} else {
			mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
		}
	}
	private static class PagerAdapter extends FragmentStatePagerAdapter {
		HomeActivity homeActivity;
		public PagerAdapter(FragmentManager fm, HomeActivity homeActivity) {
			super(fm);
			this.homeActivity=homeActivity;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 1) {
				if(homeActivity.homeFragment==null){
					Log.d("life", "new");
					homeActivity.homeFragment=new HomeFragment();
				}
				return homeActivity.homeFragment;
			}
			if (position == 0) {
				if(homeActivity.leftFragment==null){
					homeActivity.leftFragment=new LeftFragment();
				}
				return homeActivity.leftFragment;
			}
			if(homeActivity.rightFragment==null){
				homeActivity.rightFragment=new RightFragment();
			}
			return homeActivity.rightFragment;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}

	private void onSwitchCate(CategoryModel categoryModel) {
		mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
		if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
		homeFragment.onSwichCategory(categoryModel);
		if (categoryModel == null) {
			getActionBar().setTitle(R.string.browse);
		} else {
			getActionBar().setTitle(categoryModel.getName());
		}
	}

	// create list category to add to spinner
	private static class InitCategory extends
			AsyncTask<Void, Void, List<CategoryModel>> {
		HomeActivity homeActivity;
		public InitCategory(HomeActivity homeActivity){
			this.homeActivity=homeActivity;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getAllCategory(
						homeActivity);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {

				homeActivity.categoryAdapter = new CategoryAdapter(homeActivity,
						result, true, homeActivity.mSpnActionbarCenterTitle);
				homeActivity.mSpnActionbarCenterTitle.setAdapter(homeActivity.categoryAdapter);
				homeActivity.mSpnActionbarCenterTitle.setSelected(true);
				homeActivity.mSpnActionbarCenterTitle
						.setBackgroundResource(R.drawable.selector_home_action_bar_spn_bg);
			}
			super.onPostExecute(result);
		}
	}

	// use to hide the spinner border when the drop down list is closed
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

	// functions to set the background of spinner
	public void disableSpinnerBackground() {
		mSpnActionbarCenterTitle
				.setBackgroundResource(R.drawable.selector_home_action_bar_spn_bg);
	}
	
	public LoginButton getLoginButton() {
		return mBtnLoginFacebook;
	}
}
