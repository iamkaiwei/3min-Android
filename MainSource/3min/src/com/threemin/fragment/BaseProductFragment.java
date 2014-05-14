package com.threemin.fragment;

import java.util.ArrayList;
import java.util.List;

import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ProductWebservice;
import android.support.v4.widget.SwipeRefreshLayout;
import android.app.Fragment;
import android.os.AsyncTask;

public abstract class BaseProductFragment extends Fragment {

	protected CategoryModel currentCate;
	protected List<ProductModel> productModels;
	int page;
	public static int STEP_INIT = 0;
	public static int STEP_ADDMORE = 1;
	public static int STEP_REFRESH = 2;
	protected SwipeRefreshLayout swipeLayout;

	public BaseProductFragment() {
		productModels = new ArrayList<ProductModel>();
	}

	protected abstract void updateUI();

	public void setCategoryData(CategoryModel model) {
		currentCate = model;
		new GetProductTaks().execute(STEP_INIT);
	}

	public void setProductModels(List<ProductModel> productModels) {
		this.productModels = productModels;
		updateUI();
	}

	public class GetProductTaks extends AsyncTask<Integer, Void, List<ProductModel>> {
		int currentStep;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (swipeLayout != null) {
				swipeLayout.setRefreshing(true);
			}
		}

		@Override
		protected List<ProductModel> doInBackground(Integer... params) {
			currentStep = params[0];
			if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
				page=1;
			}
			String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
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
			if (swipeLayout != null) {
				swipeLayout.setRefreshing(false);
			}
			if (result != null && result.size() > 0) {
				if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
					setProductModels(result);
				} else if (currentStep == STEP_ADDMORE) {
					productModels.addAll(result);
					updateUI();
				} else {
				}
			} else if(currentStep==STEP_INIT){
				setProductModels(result);
			}
		}
	}

}
