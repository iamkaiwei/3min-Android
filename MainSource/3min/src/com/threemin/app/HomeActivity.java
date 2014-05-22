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
import com.threemin.webservice.UploaderImageUlti;
import com.threemins.R;

public class HomeActivity extends Activity {

	Context mContext;
	ListView lvCategory;
	ProductFragmentList productFragmentList;
	DrawerLayout drawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	BaseProductFragment currentFragment;

	ImageButton btn_browse, btn_search, btn_sell, btn_activity, btn_me;
	
	//add grid view
	ProductFragmentGrid productFragmentGrid;
	
	//view mode: list view or grid view
	public static final int MODE_LIST_VIEW = 1;
	public static final int MODE_GRID_VIEW = 2;
	private static final int REQUEST_UPLOAD=3;
	public int mModeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;
		lvCategory = (ListView) findViewById(R.id.home_left_drawer);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		initActionBar();
		new InitCategory().execute();
		
		//init: list of products is shown in list view:
		mModeView = MODE_LIST_VIEW;
		productFragmentList = new ProductFragmentList();
		getFragmentManager().beginTransaction()
				.replace(R.id.content_fragment, productFragmentList).commit();
		currentFragment = productFragmentList;
		
		//create the fragment to switch between grid view and list view
		productFragmentGrid = new ProductFragmentGrid();
		
		lvCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CategoryModel categoryModel = (CategoryModel) lvCategory
						.getItemAtPosition(arg2);
				currentFragment.setCategoryData(categoryModel);
				drawerLayout.closeDrawer(lvCategory);
				getActionBar().setTitle(categoryModel.getName());
			}
		});
		intitNavigation();
	}

	private void intitNavigation() {
		btn_browse = (ImageButton) findViewById(R.id.img_navigation_browse);
		btn_search = (ImageButton) findViewById(R.id.img_navigation_search);
		btn_me = (ImageButton) findViewById(R.id.img_navigation_me);
		btn_sell = (ImageButton) findViewById(R.id.img_navigation_sell);
		btn_activity = (ImageButton) findViewById(R.id.img_navigation_activity);

		btn_browse.setOnClickListener(onNavigationClick());
		btn_search.setOnClickListener(onNavigationClick());
		btn_me.setOnClickListener(onNavigationClick());
		btn_sell.setOnClickListener(onNavigationClick());
		btn_sell.setOnClickListener(onNavigationClick());

		btn_browse.setSelected(true);
	}

	private class InitCategory extends
			AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance()
						.getAllCategory(mContext);
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
		R.drawable.layer_list, /* nav drawer image to replace 'Up' caret */
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

		ActionBar bar = getActionBar();
//		bar.setBackgroundDrawable(new ColorDrawable(Color
//				.parseColor(getString(R.color.orange))));
		bar.setIcon(new ColorDrawable(Color.TRANSPARENT));
		
//		int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		if (actionBarTitleId > 0) {
//		    TextView title = (TextView) findViewById(actionBarTitleId);
//		    if (title != null) {
//		        title.setTextColor(Color.WHITE);
//		    }
//		}
		
		//set padding between home icon and the title
		ImageView view = (ImageView)findViewById(android.R.id.home);
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
			if (mModeView == MODE_LIST_VIEW) { 
				mModeView = MODE_GRID_VIEW;
				item.setIcon(R.drawable.ic_listview);
				productFragmentGrid.setCategoryData(productFragmentList.getcurrenCate());
				productFragmentGrid.setProductModels(productFragmentList.getListpProductModels());
				productFragmentGrid.setCurrentPage(productFragmentList.getCurrentPage());
				getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentGrid).commit();
				currentFragment = productFragmentGrid;
			} else { 
				mModeView = MODE_LIST_VIEW;
				item.setIcon(R.drawable.ic_gridview);

				productFragmentList.setCategoryData(productFragmentGrid.getcurrenCate());
				productFragmentList.setProductModels(productFragmentGrid.getListpProductModels());
				productFragmentList.setCurrentPage(productFragmentGrid.getCurrentPage());
				getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentList).commit();
				currentFragment = productFragmentList;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onNavigationClick() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == btn_sell) {
					startActivityForResult(new Intent(mContext, ImageViewActivity.class),REQUEST_UPLOAD);
				} else {
					Toast.makeText(mContext, "to be continue",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_UPLOAD){
			if(resultCode==RESULT_OK){
				ProductModel productModel=new Gson().fromJson(data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
				new UploadProduct(this).execute(productModel);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private static class UploadProduct extends AsyncTask<ProductModel, Void, ProductModel>{
		HomeActivity activity;
		CategoryModel categoryModel;
		public UploadProduct(HomeActivity activity){
			this.activity=activity;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			activity.currentFragment.getRefreshLayout().setRefreshing(true);
		}
		
		@Override
		protected void onPostExecute(ProductModel result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(activity!=null && activity.currentFragment!=null){
			activity.currentFragment.getRefreshLayout().setRefreshing(false);
			activity.currentFragment.addNewProducts(result,categoryModel);
			}
		}
		
		@Override
		protected ProductModel doInBackground(ProductModel... params) {
			ProductModel model=params[0];
			String tokken=PreferenceHelper.getInstance(activity).getTokken();
			categoryModel=model.getCategory();
			HttpResponse reponese=new UploaderImageUlti().uploadUserPhoto(WebserviceConstant.CREATE_PRODUCT, model,tokken);
			HttpEntity r_entity = reponese.getEntity();
			String responseString;
			try {
				responseString = EntityUtils.toString(r_entity);
				JSONObject resultObject=new JSONObject(responseString);
				
				ProductModel list = new Gson().fromJson(resultObject.optJSONObject("product").toString(), ProductModel.class);
				Log.d("UPLOAD", responseString);
				return list;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JSONException e) {
				// TODO: handle exception
			}
			return null;
		}
	}
}
