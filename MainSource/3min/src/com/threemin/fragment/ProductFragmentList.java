package com.threemin.fragment;

import java.util.ArrayList;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.threemin.adapter.ProductAdapter;
import com.threemin.app.HomeActivity;
import com.threemin.app.HomeActivity.GetProductTaks;
import com.threemins.R;

public class ProductFragmentList extends BaseProductFragment {
	ListView list;
	ProductAdapter adapter;
	int thelasttotalCount;
	HomeActivity homeActivity;
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
		homeActivity=(HomeActivity) getActivity();
		initListner();

		return v;
	}

	private void initListner() {
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				homeActivity.new GetProductTaks(ProductFragmentList.this).execute(HomeActivity.STEP_REFRESH);
			}
		});
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
				if (loadMore && totalItemCount>1 && thelasttotalCount!=totalItemCount) {
					thelasttotalCount=totalItemCount;
					homeActivity.new GetProductTaks(ProductFragmentList.this).execute(HomeActivity.STEP_ADDMORE);
				}
			}
		});
	}

	@Override
	public void updateUI() {
		if (adapter == null) {
			adapter = new ProductAdapter(productModels);
		} 
		adapter.updateData(productModels);
	}
}
