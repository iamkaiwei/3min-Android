package com.threemin.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.threemin.adapter.CategoryAdapter;
import com.threemin.fragment.ProductFragmentList;
import com.threemin.model.CategoryModel;
import com.threemin.uti.CommonUti;
import com.threemin.webservice.AuthorizeWebservice;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class HomeActivity extends Activity {

	Context mContext;
	ListView lvCategory;
	ProductFragmentList productFragmentList;
	DrawerLayout layout;
	String fb_tokken = "CAACq3f9R9LsBAF6tNZBLDg3eGnXQPrwQLnSVjZCVIt4AZBnxvKhebLWBk65ZAGarUElG5ZC4vZBmP0ECYILkBhNEqzkzxX4BOZBY0DHEi4rN3GcuGqfx6EqWjAeE1ZAZApYUhe4aEIYqvauu393iTJ9k5sIC5Gyc4atubV9xCNxNthB1PhaUPEwZAZAZCycHweGqM9ZCe6ivJk3LfjaU9DZBmRFjO1kvYN05O2TrvqwrheoYp7lgZDZD";
	ActionBarDrawerToggle mDrawerToggle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mContext = this;
		lvCategory = (ListView) findViewById(R.id.home_left_drawer);
		layout=(DrawerLayout) findViewById(R.id.drawer_layout);
		initActionBar();
		new InitCategory().execute();
		productFragmentList = new ProductFragmentList();
		getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentList).commit();
	}

	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				new AuthorizeWebservice().login(fb_tokken, CommonUti.getDeviceId(mContext), mContext);
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
		        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.orange))));
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
	
}
