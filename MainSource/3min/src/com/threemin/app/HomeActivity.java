package com.threemin.app;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.threemin.adapter.AvatarAdapter;
import com.threemin.fragment.BaseProductFragment;
import com.threemin.fragment.HomeFragment;
import com.threemin.fragment.LeftFragment;
import com.threemin.fragment.RightFragment;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.UploaderImageUlti;
import com.threemins.R;

public class HomeActivity extends FragmentActivity {
	
	//action bar widgets
	RelativeLayout mRLActionbarLeft, mRLActionbarCenter, mRLActionbarRight;
	ImageView mImgActionbarLeftClose, mImgActionbarRightClose;
	
	//view pager
	public static final int NUM_PAGES = 3;
	public static final int PAGE_LEFT = 0;
	public static final int PAGE_CENTER = 1;
	public static final int PAGE_RIGHT = 2;
	ViewPager mViewPagerMainContent;
	PagerAdapter mViewPagerAdapter;
	

	Context mContext;
	BaseProductFragment currentFragment;
	private static final int REQUEST_UPLOAD = 3;

	//right drawer
	RelativeLayout layoutFilter;

	GoogleApiClient mGoogleApiClient;
	HomeFragment homeFragment;
	LeftFragment leftFragment;
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
		mViewPagerMainContent = (ViewPager) findViewById(R.id.activity_home_view_pager);
		mViewPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mViewPagerMainContent.setAdapter(mViewPagerAdapter);
		mViewPagerMainContent.setCurrentItem(1);
		mViewPagerMainContent.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				switch (position) {
				case PAGE_LEFT:
					mRLActionbarLeft.setVisibility(View.VISIBLE);
					mRLActionbarCenter.setVisibility(View.GONE);
					mRLActionbarRight.setVisibility(View.GONE);
					break;
					
				case PAGE_CENTER:
					mRLActionbarLeft.setVisibility(View.GONE);
					mRLActionbarCenter.setVisibility(View.VISIBLE);
					mRLActionbarRight.setVisibility(View.GONE);
					break;
					
				case PAGE_RIGHT:
					mRLActionbarLeft.setVisibility(View.GONE);
					mRLActionbarCenter.setVisibility(View.GONE);
					mRLActionbarRight.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void initAvatar() {
		Spinner mSpinner = (Spinner) findViewById(R.id.avatar);
		mSpinner.setAdapter(new AvatarAdapter(mContext, PreferenceHelper.getInstance(mContext).getCurrentUser()));
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

//		// set padding between home icon and the title
//		ImageView view = (ImageView) findViewById(android.R.id.home);
//		view.setPadding(0, 0, 20, 0);
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
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_switch_view, menu);
//		MenuItem itemSearch = menu.findItem(R.id.action_search);
//		
//		SearchView searchView = (SearchView) itemSearch.getActionView();
//		
//		int searchButtonID = getResources().getIdentifier("android:id/search_button", null, null);
//		ImageView searchButtonImage = (ImageView) searchView.findViewById(searchButtonID);
//		searchButtonImage.setImageResource(R.drawable.ic_search);
//		
//		int closeButtonID = getResources().getIdentifier("android:id/search_close_btn", null, null);
//		ImageView closeButtonImage = (ImageView) searchView.findViewById(closeButtonID);
//		closeButtonImage.setImageResource(R.drawable.ic_close);
//		
//		int searchEditTextID = getResources().getIdentifier("android:id/search_src_text", null, null); 
//		EditText searchEditText = (EditText) searchView.findViewById(searchEditTextID);
//		searchEditText.setTextColor(Color.WHITE);
//		searchEditText.setHint("");
		
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_UPLOAD) {
			if (resultCode == RESULT_OK) {
				ProductModel productModel = new Gson().fromJson(
						data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
				new UploadProduct(this).execute(productModel);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class UploadProduct extends AsyncTask<ProductModel, Void, ProductModel> {
		CategoryModel categoryModel;

		public UploadProduct(HomeActivity activity) {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			currentFragment.getRefreshLayout().setRefreshing(true);
		}

		@Override
		protected void onPostExecute(ProductModel result) {
			super.onPostExecute(result);
			if (mContext != null && currentFragment != null) {
				currentFragment.getRefreshLayout().setRefreshing(false);
				if (result != null) {
					homeFragment.addNewProducts(result, categoryModel);
				} else {
					Toast.makeText(mContext, R.string.error_upload, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected ProductModel doInBackground(ProductModel... params) {
			ProductModel model = params[0];
			String tokken = PreferenceHelper.getInstance(mContext).getTokken();
			categoryModel = model.getCategory();
			HttpResponse reponese = new UploaderImageUlti().uploadUserPhoto(WebserviceConstant.CREATE_PRODUCT, model,
					tokken);
			if (reponese != null) {
				HttpEntity r_entity = reponese.getEntity();
				String responseString;
				try {
					responseString = EntityUtils.toString(r_entity);
					Log.d("UPLOAD", responseString);
					JSONObject resultObject = new JSONObject(responseString);

					ProductModel list = new Gson().fromJson(resultObject.optJSONObject("product").toString(),
							ProductModel.class);
					return list;
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
				}
			}
			return null;
		}
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
			return new RightFragment();
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
}
