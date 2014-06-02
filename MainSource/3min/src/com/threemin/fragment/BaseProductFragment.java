package com.threemin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ProductWebservice;

public abstract class BaseProductFragment extends Fragment {

	protected List<ProductModel> productModels;
	
	public SwipeRefreshLayout swipeLayout;
	protected HomeFragment homeFragment;
	
	public BaseProductFragment() {
		productModels = new ArrayList<ProductModel>();
	}
	
	public void setHomeFragment(HomeFragment homeFragment){
		this.homeFragment=homeFragment;
	}
	public abstract void setBottomView(View bottomView);
	
	public SwipeRefreshLayout getRefreshLayout(){
		return swipeLayout;
	}

	public abstract void updateUI();
	
	public abstract void changeIfNoItem() ;


	public void setProductModels(List<ProductModel> productModels) {
		this.productModels = productModels;
		updateUI();
	}
	
	public List<ProductModel> getListpProductModels(){
		return productModels;
	}
	

	


}
