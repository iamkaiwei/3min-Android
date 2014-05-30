package com.threemin.fragment;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.threemin.app.ImageViewActivity;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ProductWebservice;
import com.threemins.R;

public class HomeFragment extends Fragment {
	public static final int MODE_LIST_VIEW = 1;
	public static final int MODE_GRID_VIEW = 2;
	private static final int REQUEST_UPLOAD = 3;

	public static int STEP_INIT = 0;
	public static int STEP_ADDMORE = 1;
	public static int STEP_REFRESH = 2;
	
	ProductFragmentGrid productFragmentGrid;
	BaseProductFragment currentFragment;
	ProductFragmentList productFragmentList;
	int page;
	View vHighlightList, vHighlightThumb;
	View tabList, tabThumb;
	View bottomView;
	public int mModeView;
	protected CategoryModel currentCate;
	protected List<ProductModel> productModels;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, null);
		
		
		vHighlightList = v.findViewById(R.id.highlight_list);
		vHighlightThumb = v.findViewById(R.id.highlight_thumbnail);
		vHighlightList.setVisibility(View.INVISIBLE);
		bottomView = v.findViewById(R.id.bottom);
		tabList = v.findViewById(R.id.tab_list);
		tabThumb = v.findViewById(R.id.tab_thumb);

		// init: list of products is shown in list view:
		mModeView = MODE_GRID_VIEW;
		productFragmentList = new ProductFragmentList(bottomView);
		productFragmentGrid = new ProductFragmentGrid(bottomView);
		productFragmentGrid.setHomeFragment(this);
		productFragmentList.setHomeFragment(this);
		getFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentGrid).commit();
		currentFragment = productFragmentGrid;

		// create the fragment to switch between grid view and list view


		v.findViewById(R.id.home_camera).setOnClickListener(onSellClick());
		new GetProductTaks(currentFragment).execute(STEP_INIT);

		tabList.setOnClickListener(onTabSwitch());
		tabThumb.setOnClickListener(onTabSwitch());
		tabThumb.setSelected(true);
		tabList.setSelected(false);
		return v;
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
	
	private OnClickListener onTabSwitch() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == tabList && mModeView == MODE_LIST_VIEW) {
					return;
				}
				if (v == tabThumb && mModeView == MODE_GRID_VIEW) {
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
				startActivityForResult(new Intent(getActivity(), ImageViewActivity.class), REQUEST_UPLOAD);
			}
		};
	}
	
	public void onSwichCategory(CategoryModel categoryModel){
		currentCate=categoryModel;
		new GetProductTaks(currentFragment).execute(STEP_INIT);
	}

}
