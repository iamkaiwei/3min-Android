package com.threemin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.threemin.model.ProductModel;

public abstract class BaseProductFragment extends Fragment {

	protected List<ProductModel> productModels;
	
	public SwipeRefreshLayout swipeLayout;
	public int thelasttotalCount;

	public BaseProductFragment() {
		productModels = new ArrayList<ProductModel>();
	}
	
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
