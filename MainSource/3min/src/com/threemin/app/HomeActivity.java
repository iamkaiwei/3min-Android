package com.threemin.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.threemin.adapter.AvatarAdapter;
import com.threemin.adapter.CategoryAdapter;
import com.threemin.adapter.RightDrawerAdapter;
import com.threemin.fragment.BaseProductFragment;
import com.threemin.fragment.HomeFragment;
import com.threemin.fragment.LeftFragment;
import com.threemin.fragment.ProductFragmentGrid;
import com.threemin.fragment.ProductFragmentList;
import com.threemin.fragment.RightFragment;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.CategoryWebservice;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UploaderImageUlti;
import com.threemins.R;

public class HomeActivity extends FragmentActivity {
	
	//view pager
	public static final int NUM_PAGES = 3;
	public static final int PAGE_LEFT = 0;
	public static final int PAGE_CENTER = 1;
	public static final int PAGE_RIGHT = 2;
	ViewPager mViewPagerMainContent;
	PagerAdapter mViewPagerAdapter;
	

	Context mContext;
	ListView lvCategory, lvfilter;
	DrawerLayout drawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	BaseProductFragment currentFragment;
	private static final int REQUEST_UPLOAD = 3;

	//right drawer
	RelativeLayout layoutFilter;
	RightDrawerAdapter adapterRightDrawer;

	GoogleApiClient mGoogleApiClient;
	HomeFragment homeFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;
		
		//right drawer
		lvfilter=(ListView) findViewById(R.id.home_right_drawer_listview);
		layoutFilter = (RelativeLayout) findViewById(R.id.home_right_drawer_layout);
		ArrayList<String> listFilter = new ArrayList<String>();
		listFilter.add("Popular");
		listFilter.add("Recent");
		listFilter.add("Lowest Price");
		listFilter.add("Highest Price");
		listFilter.add("Nearest");
		adapterRightDrawer = new RightDrawerAdapter(mContext, R.layout.inflater_right_drawer_listview_item, listFilter);
		lvfilter.setAdapter(adapterRightDrawer);
		lvfilter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				adapterRightDrawer.setSelectedPosition(position);
			}
		});
		new InitCategory().execute();
		
		lvCategory = (ListView) findViewById(R.id.navigation_list);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		// create the fragment to switch between grid view and list view

		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CategoryModel categoryModel = (CategoryModel) lvCategory.getItemAtPosition(position);
				if (position == 0) {
					categoryModel = null;
				}
				homeFragment.onSwichCategory(categoryModel);
				drawerLayout.closeDrawer(Gravity.START);
				getActionBar().setTitle(categoryModel.getName());
			}
		});

		initAvatar();
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Plus.API, null).addScope(Plus.SCOPE_PLUS_PROFILE)
				.build();
		mGoogleApiClient.connect();
		initActionBar();
//		homeFragment=new HomeFragment();
//		getFragmentManager().beginTransaction().replace(R.id.content_layout, homeFragment).commit();

		//view pager implementation
		homeFragment=new HomeFragment();
		mViewPagerMainContent = (ViewPager) findViewById(R.id.activity_home_view_pager);
		mViewPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mViewPagerMainContent.setAdapter(mViewPagerAdapter);
		mViewPagerMainContent.setCurrentItem(1);
		
	}

	private void initAvatar() {
		Spinner mSpinner = (Spinner) findViewById(R.id.avatar);
		mSpinner.setAdapter(new AvatarAdapter(mContext, PreferenceHelper.getInstance(mContext).getCurrentUser()));
	}

	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getAllCategory(mContext);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {
				
				CategoryAdapter adapter = new CategoryAdapter(mContext, result);
				adapter.setOnLogout(doLogout());
				lvCategory.setAdapter(adapter);
			}
			super.onPostExecute(result);
		}
	}

	private void initActionBar() {
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_menu, /* nav drawer image to replace 'Up' caret */
		R.string.app_name, /* "open drawer" description for accessibility */
		R.string.app_name /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				// onPrepareOptionsMenu()
			}
		};
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// set padding between home icon and the title
		ImageView view = (ImageView) findViewById(android.R.id.home);
		view.setPadding(0, 0, 20, 0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_switch_view, menu);
		MenuItem itemSearch = menu.findItem(R.id.action_search);
		
		SearchView searchView = (SearchView) itemSearch.getActionView();
		
		int searchButtonID = getResources().getIdentifier("android:id/search_button", null, null);
		ImageView searchButtonImage = (ImageView) searchView.findViewById(searchButtonID);
		searchButtonImage.setImageResource(R.drawable.ic_search);
		
		int closeButtonID = getResources().getIdentifier("android:id/search_close_btn", null, null);
		ImageView closeButtonImage = (ImageView) searchView.findViewById(closeButtonID);
		closeButtonImage.setImageResource(R.drawable.ic_search_close);
		
		int searchEditTextID = getResources().getIdentifier("android:id/search_src_text", null, null); 
		EditText searchEditText = (EditText) searchView.findViewById(searchEditTextID);
		searchEditText.setTextColor(Color.WHITE);
		searchEditText.setHint("");
		
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			if(drawerLayout.isDrawerOpen(Gravity.END)){
				drawerLayout.closeDrawer(Gravity.END);
			}
			return true;
		}

		
		if (item.getItemId() == R.id.action_switch_view) {
			if(drawerLayout.isDrawerOpen(layoutFilter)){
				drawerLayout.closeDrawer(layoutFilter);
			} else {
				drawerLayout.openDrawer(layoutFilter);
			}
		}
		
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
				return new LeftFragment();
			}
			return new RightFragment();
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

	}
}
