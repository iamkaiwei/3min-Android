package com.threemin.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

import com.threemin.adapter.ProductGridAdapter;
import com.threemin.app.HomeActivity;
import com.threemin.app.HomeActivity.GetProductTaks;
import com.threemins.R;

public class ProductFragmentGrid extends BaseProductFragment {
	
	private GridView mGrid;
	private ProductGridAdapter mAdapter;
	int thelasttotalCount;
	HomeActivity homeActivity;

	public ProductFragmentGrid() {
		super();
	}	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_product_gridview, null);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_gridview);
		mGrid = (GridView) v.findViewById(R.id.gv_product);
		
		if (mAdapter == null) {
			mAdapter = new ProductGridAdapter(productModels);
		}
		mGrid.setAdapter(mAdapter);
		

		initListner();
		homeActivity=(HomeActivity) getActivity();
		return v;
	}

	private void initListner() {
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				homeActivity.new GetProductTaks(ProductFragmentGrid.this).execute(HomeActivity.STEP_REFRESH);
			}
		});
		
		mGrid.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
				if (loadMore && totalItemCount>1 && thelasttotalCount!=totalItemCount) {
					homeActivity.new GetProductTaks(ProductFragmentGrid.this).execute(HomeActivity.STEP_ADDMORE);
				}
			}
		});
	}

	@Override
	public void updateUI() {
		if (mAdapter == null) {
			mAdapter = new ProductGridAdapter(productModels);
		} 
		mAdapter.updateData(productModels);
	}
}
