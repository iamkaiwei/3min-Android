package com.threemin.fragment;

import java.util.List;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

import com.threemin.adapter.ProductAdapter;
import com.threemin.model.ProductModel;
import com.threemins.R;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class ProductFragmentList extends BaseProductFragment {
	PinnedHeaderListView list;
	ProductAdapter adapter;

	public ProductFragmentList() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_product_listview, null);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
		list = (PinnedHeaderListView) v.findViewById(R.id.lv_product);
		
		if (adapter == null) {
			adapter = new ProductAdapter(productModels);
		}
		list.setAdapter(adapter);
		
		page = 1;
		initListner();
	
		new GetProductTaks(this).execute(BaseProductFragment.STEP_INIT);
		
		return v;
	}

	private void initListner() {
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new GetProductTaks(ProductFragmentList.this).execute(BaseProductFragment.STEP_REFRESH);
			}
		});
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
				if (loadMore) {
					page++;
					new GetProductTaks(ProductFragmentList.this).execute(BaseProductFragment.STEP_ADDMORE);
				}
			}
		});
	}

	@Override
	protected void updateUI() {
		if (adapter == null) {
			adapter = new ProductAdapter(productModels);
		} 
		adapter.updateData(productModels);
	}
	
	public void updateData(List<ProductModel> newData) {
		setProductModels(newData);
	}
	
	public List<ProductModel> getData() {
		return this.productModels;
	}

}
