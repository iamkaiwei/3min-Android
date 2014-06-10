package com.threemin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.threemin.adapter.ProductGridAdapter;
import com.threemin.app.DetailActivity;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.view.QuickReturnGridView;
import com.threemins.R;

public class ProductFragmentGrid extends BaseProductFragment {
	
	private QuickReturnGridView mGrid;
	private ProductGridAdapter mAdapter;
	int thelasttotalCount;
	View bottomView;
	
	private int mQuickReturnHeight;

	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private int mState = STATE_ONSCREEN;
	private int mScrollY;
	private int mMinRawY = 0;
	public ProductFragmentGrid(View bottomView) {
		super();
		this.bottomView=bottomView;
	}	
	
	public ProductFragmentGrid() {
		super();
	}	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_product_gridview, null);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_gridview);
		mGrid = (QuickReturnGridView) v.findViewById(R.id.gv_product);
		
		if (mAdapter == null) {
			mAdapter = new ProductGridAdapter(productModels);
		}
		mGrid.setAdapter(mAdapter);
		

		initListner();
		homeFragment.setBottomView();
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mGrid.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mQuickReturnHeight = mGrid.getHeight();
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								mGrid.computeScrollY();
							}
						}).start();
					}
				});
	}
	private void initListner() {
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				homeFragment.new GetProductTaks(ProductFragmentGrid.this).execute(HomeFragment.STEP_REFRESH);
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
					thelasttotalCount=totalItemCount;
					homeFragment.new GetProductTaks(ProductFragmentGrid.this).execute(HomeFragment.STEP_ADDMORE);
				}
				
				handleQuickReturn();
			}
		});
		mGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ProductModel model =(ProductModel) mGrid.getItemAtPosition(position);
				if(model!=null){
					String data=new Gson().toJson(model);
					Intent intent=new Intent(getActivity(), DetailActivity.class);
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					getActivity().startActivity(intent);
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

	private void handleQuickReturn() {
		mScrollY = 0;
		int translationY = 0;

		if (mGrid.scrollYIsComputed()) {
			mScrollY = mGrid.getComputedScrollY();
		}

		int rawY = mScrollY;

		switch (mState) {
		case STATE_OFFSCREEN:
			if (rawY >= mMinRawY) {
				mMinRawY = rawY;
			} else {
				mState = STATE_RETURNING;
			}
			translationY = rawY;
			break;

		case STATE_ONSCREEN:
			if (rawY > mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;
			}
			translationY = rawY;
			break;

		case STATE_RETURNING:

			translationY = (rawY - mMinRawY) + mQuickReturnHeight;

			System.out.println(translationY);
			if (translationY < 0) {
				translationY = 0;
				mMinRawY = rawY + mQuickReturnHeight;
			}

			if (rawY == 0) {
				mState = STATE_ONSCREEN;
				translationY = 0;
			}

			if (translationY > mQuickReturnHeight) {
				mState = STATE_OFFSCREEN;
				mMinRawY = rawY;
			}
			break;
		}
		if(bottomView!=null)
			bottomView.setTranslationY(translationY);
	}

	@Override
	public void setBottomView(View bottomView) {
		this.bottomView=bottomView;
	}
}
