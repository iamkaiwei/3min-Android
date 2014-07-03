package com.threemin.fragment;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.threemin.app.HomeActivity;
import com.threemin.app.ImageViewActivity;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UploaderImageUlti;
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
	int page;
	View bottomView;
	public int mModeView;
	protected CategoryModel currentCate;
	protected List<ProductModel> productModels;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_home, null);		
		bottomView = v.findViewById(R.id.bottom);

		// init: list of products is shown in list view:
		mModeView = MODE_GRID_VIEW;
		productFragmentGrid = new ProductFragmentGrid(bottomView);
		productFragmentGrid.setHomeFragment(this);
		getChildFragmentManager().beginTransaction().replace(R.id.content_fragment, productFragmentGrid).commit();
		currentFragment = productFragmentGrid;

		// create the fragment to switch between grid view and list view


		v.findViewById(R.id.home_camera).setOnClickListener(onSellClick());
//		new GetProductTaks(currentFragment).execute(STEP_INIT);

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
				baseProductFragment.thelasttotalCount = 0;
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
			
			baseProductFragment.changeIfNoItem();
		}
	}

	public void addNewProducts(ProductModel result, CategoryModel categoryModel) {
		if (currentCate == null || currentCate.getId() == categoryModel.getId()) {
			productModels.add(0, result);
		}
	}

	public void setBottomView() {
		productFragmentGrid.setBottomView(bottomView);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_UPLOAD) {
			if (resultCode == Activity.RESULT_OK) {
				ProductModel productModel = new Gson().fromJson(
						data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
				new UploadProduct((HomeActivity) getActivity()).execute(productModel);
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
			if (getActivity() != null && currentFragment != null) {
				currentFragment.getRefreshLayout().setRefreshing(false);
				if (result != null) {
					addNewProducts(result, categoryModel);
				} else {
					Toast.makeText(getActivity(), R.string.error_upload, Toast.LENGTH_SHORT).show();
				}
			}
		}

		@Override
		protected ProductModel doInBackground(ProductModel... params) {
			ProductModel model = params[0];
			String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
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
}
