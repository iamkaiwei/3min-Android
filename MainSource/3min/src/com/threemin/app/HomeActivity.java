package com.threemin.app;

import java.util.List;

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
import android.widget.ListView;
import android.widget.Toast;

import com.threemin.adapter.CategoryAdapter;
import com.threemin.fragment.BaseProductFragment;
import com.threemin.fragment.ProductFragmentGrid;
import com.threemin.fragment.ProductFragmentList;
import com.threemin.model.CategoryModel;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class HomeActivity extends Activity {

	Context mContext;
	ListView lvCategory;
	ProductFragmentList productFragmentList;
	DrawerLayout layout;
	ActionBarDrawerToggle mDrawerToggle;
	BaseProductFragment currentFragment;

	ImageButton btn_browse, btn_search, btn_sell, btn_activity, btn_me;
	
	//add grid view
	ProductFragmentGrid productFragmentGrid;
	
	//view mode: list view or grid view
	public static final int MODE_LIST_VIEW = 1;
	public static final int MODE_GRID_VIEW = 2;
	public int mModeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;
		lvCategory = (ListView) findViewById(R.id.home_left_drawer);
		layout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
				layout.closeDrawer(lvCategory);
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
		layout, /* DrawerLayout object */
		R.drawable.navigation_icon, /* nav drawer image to replace 'Up' caret */
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
		layout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(getString(R.color.orange))));
		bar.setDisplayShowHomeEnabled(false);
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
				productFragmentGrid.updateData(productFragmentList.getData());
				getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentGrid).commit();
				currentFragment = productFragmentGrid;
			} else { 
				mModeView = MODE_LIST_VIEW;
				item.setIcon(R.drawable.ic_gridview);
				productFragmentList.updateData(productFragmentGrid.getData());
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
					startActivity(new Intent(mContext, ImageViewActivity.class));
				} else {
					Toast.makeText(mContext, "to be continue",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

}
