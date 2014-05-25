package com.threemin.app;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.threemin.adapter.CategoryAdapter;
import com.threemin.fragment.BaseProductFragment;
import com.threemin.fragment.ProductFragmentGrid;
import com.threemin.fragment.ProductFragmentList;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.CategoryWebservice;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UploaderImageUlti;
import com.threemins.R;

public class HomeActivity extends Activity {

	Context mContext;
	ListView lvCategory,lvfilter;
	ProductFragmentList productFragmentList;
	DrawerLayout drawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	BaseProductFragment currentFragment;

	// add grid view
	ProductFragmentGrid productFragmentGrid;

	// view mode: list view or grid view
	public static final int MODE_LIST_VIEW = 1;
	public static final int MODE_GRID_VIEW = 2;
	private static final int REQUEST_UPLOAD = 3;

	public static int STEP_INIT = 0;
	public static int STEP_ADDMORE = 1;
	public static int STEP_REFRESH = 2;

	public int mModeView;

	protected CategoryModel currentCate;
	protected List<ProductModel> productModels;
	int page;
	View vHighlightList, vHighlightThumb;
	View tabList, tabThumb;
	View bottomView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;
		lvCategory = (ListView) findViewById(R.id.home_left_drawer);
		lvfilter=(ListView) findViewById(R.id.home_right_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		vHighlightList = findViewById(R.id.highlight_list);
		vHighlightThumb = findViewById(R.id.highlight_thumbnail);
		vHighlightList.setVisibility(View.INVISIBLE);
		bottomView=findViewById(R.id.bottom);
		tabList = findViewById(R.id.tab_list);
		tabThumb = findViewById(R.id.tab_thumb);
		initActionBar();
		new InitCategory().execute();

		// init: list of products is shown in list view:
		mModeView = MODE_GRID_VIEW;
		productFragmentList = new ProductFragmentList(bottomView);
		productFragmentGrid = new ProductFragmentGrid(bottomView);
		getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentGrid).commit();
		currentFragment = productFragmentGrid;

		// create the fragment to switch between grid view and list view

		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CategoryModel categoryModel = (CategoryModel) lvCategory.getItemAtPosition(arg2);
				currentCate = categoryModel;
				new GetProductTaks(currentFragment).execute(STEP_INIT);
				drawerLayout.closeDrawer(lvCategory);
				getActionBar().setTitle(categoryModel.getName());
			}
		});
		
		findViewById(R.id.home_camera).setOnClickListener(onSellClick());
		new GetProductTaks(currentFragment).execute(STEP_INIT);

		tabList.setOnClickListener(onTabSwitch());
		tabThumb.setOnClickListener(onTabSwitch());
		tabThumb.setSelected(true);
		tabList.setSelected(false);
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == R.id.action_switch_view) {
			if(drawerLayout.isDrawerOpen(lvfilter)){
				drawerLayout.closeDrawer(lvfilter);
			} else {
				drawerLayout.openDrawer(lvfilter);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onTabSwitch() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v==tabList && mModeView==MODE_LIST_VIEW){
					return;
				}
				if(v==tabThumb && mModeView==MODE_GRID_VIEW){
					return;
				}
				switchMode();
			}
		};
	}

	private void switchMode() {
		if (mModeView == MODE_LIST_VIEW) {
			mModeView = MODE_GRID_VIEW;
			vHighlightThumb.setVisibility(View.VISIBLE);
			vHighlightList.setVisibility(View.INVISIBLE);
			tabThumb.setSelected(true);
			tabList.setSelected(false);
			productFragmentGrid.setProductModels(productModels);
			getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentGrid).commit();
			currentFragment = productFragmentGrid;
		} else {
			mModeView = MODE_LIST_VIEW;
			vHighlightThumb.setVisibility(View.INVISIBLE);
			vHighlightList.setVisibility(View.VISIBLE);
			tabList.setSelected(true);
			tabThumb.setSelected(false);
			productFragmentList.setProductModels(productModels);
			getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentList).commit();
			currentFragment = productFragmentList;
		}
	}

	private OnClickListener onSellClick() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(mContext, ImageViewActivity.class), REQUEST_UPLOAD);
			}
		};
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
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mContext != null && currentFragment != null) {
				currentFragment.getRefreshLayout().setRefreshing(false);
				if (result != null) {
					addNewProducts(result, categoryModel);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO: handle exception
				}
			}
			return null;
		}
	}

	public class GetProductTaks extends AsyncTask<Integer, Void, List<ProductModel>> {
		int currentStep;

		BaseProductFragment baseProductFragment;

		// flag check if asynctask is proccessing or not

		// constructor
		public GetProductTaks(BaseProductFragment baseProductFragment) {
			this.baseProductFragment = baseProductFragment;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (baseProductFragment.swipeLayout != null) {
				baseProductFragment.swipeLayout.setRefreshing(true);
			}
		}

		@Override
		protected List<ProductModel> doInBackground(Integer... params) {
			currentStep = params[0];
			if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
				page = 1;
			}
			if (currentStep == STEP_ADDMORE) {
				page++;
			}
			String tokken = PreferenceHelper.getInstance(baseProductFragment.getActivity()).getTokken();
			try {
				return new ProductWebservice().getProduct(tokken, currentCate, page);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<ProductModel> result) {
			super.onPostExecute(result);
			if (baseProductFragment.swipeLayout != null) {
				baseProductFragment.swipeLayout.setRefreshing(false);
			}
			if (result != null && result.size() > 0) {
				if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
					productModels = result;
					baseProductFragment.setProductModels(result);
				} else if (currentStep == STEP_ADDMORE) {
					productModels.addAll(result);
					baseProductFragment.setProductModels(productModels);
				} else {
				}

			} else if (currentStep == STEP_INIT) {
				baseProductFragment.setProductModels(result);
				productModels = result;
			}
		}
	}

	public void addNewProducts(ProductModel result, CategoryModel categoryModel) {
		if (currentCate == null || currentCate.getId() == categoryModel.getId()) {
			productModels.add(0, result);
		}
	}

	public void setBottomView() {
		productFragmentList.setBottomView(bottomView);
		productFragmentGrid.setBottomView(bottomView);
	}
}
