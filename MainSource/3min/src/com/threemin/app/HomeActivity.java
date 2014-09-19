package com.threemin.app;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.threemin.adapter.CategoryAdapter;
import com.threemin.fragment.HomeFragment;
import com.threemin.fragment.LeftFragment;
import com.threemin.fragment.RightFragment;
import com.threemin.model.CategoryModel;
import com.threemin.model.FilterModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.view.BaseViewPagerAdapter;
import com.threemin.view.CustomSpinner;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class HomeActivity extends SwipeBackActivity {

	// action bar widgets
    RelativeLayout mRLActionbarProfile;
    TextView mTvActionbarProfile;
	ImageView mImgActionbarSearch, mImgActionbarProfile;
//	CustomSpinner mSpnActionbarCenterTitle;
	Button mBtnActionbarCenterTitle;
	
	//dimmed bacground, use when we replace spinner on action bar with textview and popup window
	FrameLayout mFlDimmedBackground;
	TextView mTvActionBarCenter;
	PopupWindow mPopupWindowCategories;
	ListView mLvListCategories;
	Animation mAnimDimbackground;

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

//	GoogleApiClient mGoogleApiClient;
	HomeFragment homeFragment;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	public static final String TAG_HOME_FRAGMENT = "HomeFragment";
	public static final String TAG_LEFT_FRAGMENT = "LeftFragment";
	public static final String TAG_RIGHT_FRAGMENT = "RightFragment";
	public static final String TAG_SAVED_FRAGMENT = "HomeActivitySavedFragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
	    Log.i("LifeCycle", "onCreate");
	    
		super.onCreate(savedInstanceState);
		Log.i("access_token", PreferenceHelper.getInstance(this).getTokken());
		mContext = this;
		setContentView(R.layout.activity_home);
		
		//disable swipe back
		getSwipeBackLayout().setEnableGesture(false);

//		mGoogleApiClient = new GoogleApiClient.Builder(this)
//				.addApi(Plus.API, null).addScope(Plus.SCOPE_PLUS_PROFILE)
//				.build();
//		mGoogleApiClient.connect();
		

		// button login facebook
		mBtnLoginFacebook = (LoginButton) findViewById(R.id.activity_home_btn_login_facebook);

		// view pager implementation
		currentPage = PAGE_CENTER;
		prevPage = -1;
		
		//TODO: old implementation to save fragment===========================================================================
//		if (savedInstanceState != null) {
//		    homeFragment = (HomeFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_HOME_FRAGMENT);
//		    leftFragment = (LeftFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_LEFT_FRAGMENT);
//		    rightFragment = (RightFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_RIGHT_FRAGMENT);
//        } 
        //old implementation============================================================================================
		
		//TODO: new implementation to save fragment===========================================================================
		
		homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_SAVED_FRAGMENT + PAGE_CENTER);
		Log.i("Saved", "HomeAct: " + TAG_SAVED_FRAGMENT + PAGE_CENTER);
        leftFragment = (LeftFragment) getSupportFragmentManager().findFragmentByTag(TAG_SAVED_FRAGMENT + PAGE_LEFT);
        Log.i("Saved", "HomeAct: " + TAG_SAVED_FRAGMENT + PAGE_LEFT);
        rightFragment = (RightFragment) getSupportFragmentManager().findFragmentByTag(TAG_SAVED_FRAGMENT + PAGE_RIGHT);
        Log.i("Saved", "HomeAct: " + TAG_SAVED_FRAGMENT + PAGE_RIGHT);
        //new implementation to save fragment===========================================================================
        
		if (homeFragment == null) {
		    homeFragment = new HomeFragment();
        }
		if (leftFragment == null) {
		    leftFragment = new LeftFragment();
        }
		if (rightFragment == null) {
		    rightFragment = new RightFragment();
        }
		
		mViewPagerMainContent = (ViewPager) findViewById(R.id.activity_home_view_pager);
		mViewPagerAdapter = new PagerAdapter(getSupportFragmentManager(), TAG_SAVED_FRAGMENT);
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
	protected void onStart() {
	    Log.i("LifeCycle", "onStart");
	    super.onStart();
//	    mGoogleApiClient.connect();
	}
	
	@Override
	protected void onResume() {
        Log.i("LifeCycle", "onResume");
        int numActivities = PreferenceHelper.getInstance(this).getNumberActivities();
        mTvActionbarProfile.setText("" + numActivities);
        if (numActivities > 0) {
            mTvActionbarProfile.setVisibility(View.VISIBLE);
        } else {
            mTvActionbarProfile.setVisibility(View.GONE);
        }
        
	    super.onResume();
	}
	
	@Override
	protected void onPause() {
        Log.i("LifeCycle", "onPause");
	    super.onPause();
	}
	
	@Override
	protected void onStop() {
        Log.i("LifeCycle", "onStop");
	    super.onStop();
//	    mGoogleApiClient.disconnect();
	}
	
	@Override
	protected void onRestart() {
        Log.i("LifeCycle", "onRestart");
	    super.onRestart();
	}
	
	@Override
	protected void onDestroy() {
        Log.i("LifeCycle", "onDestroy");
	    super.onDestroy();
	}

    //TODO: old implementation to save fragment=========================================================================
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//	    super.onSaveInstanceState(outState);
//	    getSupportFragmentManager().putFragment(outState, TAG_HOME_FRAGMENT, homeFragment);
//	    getSupportFragmentManager().putFragment(outState, TAG_LEFT_FRAGMENT, leftFragment);
//	    getSupportFragmentManager().putFragment(outState, TAG_RIGHT_FRAGMENT, rightFragment);
//	}
    //old implementation to save fragment===============================================================================
	
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
			mRLActionbarProfile.setSelected(false);
			break;

		case PAGE_CENTER:
			mImgActionbarSearch.setSelected(false);
			setSpinnerSelected(true);
            mRLActionbarProfile.setSelected(false);
			leftFragment.hideKeyboard();

			if (prevPage == PAGE_LEFT) {
				doFilter();
			}

			break;

		case PAGE_RIGHT:
			mImgActionbarSearch.setSelected(false);
			setSpinnerSelected(false);
            mRLActionbarProfile.setSelected(true);
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
//		RelativeLayout rl = (RelativeLayout)mSpnActionbarCenterTitle.getChildAt(0);
//		TextView tv = null;
//		if (rl != null) {
//			tv = (TextView)rl.getChildAt(0);
//		}
		
		if (isSelected) {
//			mSpnActionbarCenterTitle.setSelected(true);
			mTvActionBarCenter.setSelected(true);
//			if (tv != null) {
//				tv.setTextColor(getResources().getColor(R.color.home_action_bar_text_color_enable));
//			}
//			mSpnActionbarCenterTitle.setEnabled(true);
			mTvActionBarCenter.setEnabled(true);
			mBtnActionbarCenterTitle.setVisibility(View.GONE);
		} else {
//			mSpnActionbarCenterTitle.setSelected(false);
            mTvActionBarCenter.setSelected(false);
//			if (tv != null) {
//				tv.setTextColor(getResources().getColor(R.color.home_action_bar_text_color_disable));
//			}
//			mSpnActionbarCenterTitle.setEnabled(false);
            mTvActionBarCenter.setEnabled(false);
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

		// action bar profile
		mRLActionbarProfile = (RelativeLayout) findViewById(R.id.home_activity_action_bar_center_rl_profile);
		mTvActionbarProfile = (TextView) findViewById(R.id.home_activity_action_bar_tv_number_activities);
		mImgActionbarProfile = (ImageView) findViewById(R.id.home_activity_action_bar_center_img_profile);
		mRLActionbarProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_RIGHT, true);
			}
		});
		mTvActionbarProfile.setText("" + PreferenceHelper.getInstance(this).getNumberActivities());

		mImgActionbarSearch = (ImageView) findViewById(R.id.home_activity_action_bar_center_img_search);
		mImgActionbarSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_LEFT, true);
			}
		});

		//action bar center spinner
//		mSpnActionbarCenterTitle = (CustomSpinner) findViewById(R.id.home_activity_action_bar_center_title);
//		mSpnActionbarCenterTitle
//				.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//					@Override
//					public void onItemSelected(AdapterView<?> parent,
//							View view, int position, long id) {
//
//						CategoryModel categoryModel = (CategoryModel) parent
//								.getItemAtPosition(position);
//						if (categoryModel.getId() == 0) {
//							onSwitchCate(null);
//						} else {
//							onSwitchCate(categoryModel);
//						}
//						categoryAdapter.swapView(position);
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> parent) {
//
//					}
//				});

		mBtnActionbarCenterTitle = (Button) findViewById(R.id.home_activity_action_bar_center_btn_title);
		mBtnActionbarCenterTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mViewPagerMainContent.setCurrentItem(PAGE_CENTER, true);
				setSpinnerSelected(true);
			}
		});
		
		//relpace spinner with textview and popup window
		//textview
		mTvActionBarCenter = (TextView) findViewById(R.id.home_activity_action_bar_center_tv_title);
		mTvActionBarCenter.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showOrHideDropdownList();
            }
        });
		initPopupWindow();

//		disableSpinnerBackground();
//        mSpnActionbarCenterTitle.setSelected(true);
        mTvActionBarCenter.setSelected(true);
        mImgActionbarProfile.setSelected(false);
        mImgActionbarSearch.setSelected(false);
        

        new InitCategory().execute();
  	}
	
	public void initPopupWindow() {
	    LayoutInflater inflater = LayoutInflater.from(this);
	    View dropdownView = inflater.inflate(R.layout.layout_custom_action_bar_dropdown_list, null);
	    mPopupWindowCategories = new PopupWindow(dropdownView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    mPopupWindowCategories.setBackgroundDrawable(new BitmapDrawable(getResources()));
	    mPopupWindowCategories.setOutsideTouchable(true);
	    mPopupWindowCategories.setTouchable(true);
	    mPopupWindowCategories.setFocusable(true);
	    mPopupWindowCategories.setAnimationStyle(R.style.activity_home_popup_animation);
	    mPopupWindowCategories.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss() {
                dimBackground(false);
                mTvActionBarCenter.setBackgroundResource(R.drawable.selector_home_action_bar_spn_bg);
            }
        });
	    
	  //dimmed background
        mFlDimmedBackground = (FrameLayout) findViewById(R.id.activity_home_fm_dimmed_background);
        mFlDimmedBackground.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPopupWindowCategories.isShowing()) {
                    mPopupWindowCategories.dismiss();
                }
                return false;
            }
        });
        
        mAnimDimbackground = AnimationUtils.loadAnimation(this, R.anim.anim_popup_dim_background);
        mAnimDimbackground.setDuration(getResources().getInteger(R.integer.activity_home_popup_animation_duration) * 2);
        mAnimDimbackground.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
                mFlDimmedBackground.setVisibility(View.VISIBLE);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        
	    mLvListCategories = (ListView) dropdownView.findViewById(R.id.home_activity_action_bar_dropdown_list);
	    mLvListCategories.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryModel cateModel = (CategoryModel) parent.getItemAtPosition(position);
                if (position == 0) {
                    mTvActionBarCenter.setText(getResources().getString(R.string.browse));
                    onSwitchCate(null);
                } else {
                    mTvActionBarCenter.setText(cateModel.getName());
                    onSwitchCate(cateModel);
                }
                mPopupWindowCategories.dismiss();
                categoryAdapter.swapView(position);
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

	@Override
	public void onBackPressed() {
	    if (mViewPagerMainContent.getCurrentItem() == PAGE_CENTER) {
	        super.onBackPressed();
	    } else {
	        mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
	    }
	}

	// view pager implementation

	private class PagerAdapter extends BaseViewPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		public PagerAdapter(FragmentManager fm, String tag) {
            super(fm, tag);
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

	public void onSwitchCate(CategoryModel categoryModel) {
		mViewPagerMainContent.setCurrentItem(PAGE_CENTER);
		if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
		homeFragment.onSwichCategory(categoryModel);
	}

	// create list category to add to spinner
	private class InitCategory extends
			AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getAllCategory(
						HomeActivity.this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {

//				categoryAdapter = new CategoryAdapter(HomeActivity.this,
//						result, true, mSpnActionbarCenterTitle);
//				mSpnActionbarCenterTitle.setAdapter(categoryAdapter);
//				mSpnActionbarCenterTitle.setSelected(true);
//				mSpnActionbarCenterTitle
//						.setBackgroundResource(R.drawable.selector_home_action_bar_spn_bg);
				categoryAdapter = new CategoryAdapter(HomeActivity.this, result, false, null);
				mLvListCategories.setAdapter(categoryAdapter);
				mTvActionBarCenter.setText(getResources().getString(R.string.browse));
//				mLvListCategories.performItemClick(null, 0, 0);
				onSwitchCate(result.get(2));
			}
			super.onPostExecute(result);
		}
	}

	// use to hide the spinner border when the drop down list is closed
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus && mSpnActionbarCenterTitle.isDropdownShowing()) {
//			mSpnActionbarCenterTitle.setDropdownShowing(false);
//			disableSpinnerBackground();
//		} else {
//			if (hasFocus) {
//				Log.i("tructran", "window has no focus");
//			} else {
//				Log.i("tructran", "window has focus");
//			}
//
//			if (mSpnActionbarCenterTitle.isDropdownShowing()) {
//				Log.i("tructran", "drop down is showing");
//			} else {
//				Log.i("tructran", "drop down is not showing");
//			}
//		}
//	}
	
	public void dimBackground(boolean dimmed) {
        if (dimmed) {
//            mFlDimmedBackground.setVisibility(View.VISIBLE);
            mFlDimmedBackground.startAnimation(mAnimDimbackground);
        }
        else {
            mFlDimmedBackground.setVisibility(View.GONE);
        }
	}
	
	//show or hide dropdown list when user tap on textview categories
	boolean test = true;
	public void showOrHideDropdownList() {
	    if (mPopupWindowCategories.isShowing()) {
	        mPopupWindowCategories.dismiss();
        } else {
            mPopupWindowCategories.showAsDropDown(findViewById(R.id.home_activity_action_bar_root), 0, 0);
            mTvActionBarCenter.setBackgroundResource(R.drawable.bg_spn_border_enable_arrow_enable);
            dimBackground(true);
        }
	}

	// functions to set the background of spinner
//	public void disableSpinnerBackground() {
//		mSpnActionbarCenterTitle
//				.setBackgroundResource(R.drawable.selector_home_action_bar_spn_bg);
//	}
	
	public LoginButton getLoginButton() {
		return mBtnLoginFacebook;
	}
	
	public void clearNumberActivities() {
	    int numActivities = PreferenceHelper.getInstance(this).getNumberActivities();
	    if (numActivities != 0) {
            PreferenceHelper.getInstance(this).setNumberActivities(0);
        }
	    mTvActionbarProfile.setVisibility(View.GONE);
	}
}
