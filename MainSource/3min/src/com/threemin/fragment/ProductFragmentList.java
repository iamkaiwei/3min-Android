package com.threemin.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout;

import com.threemin.adapter.ProductAdapter;
import com.threemin.view.QuickReturnListView;
import com.threemins.R;

public class ProductFragmentList extends BaseProductFragment {
	
	//no items
	RelativeLayout rlNoItems;
	private boolean isSwitched;
	
	QuickReturnListView list;
	ProductAdapter adapter;
	int thelasttotalCount;
	private int mQuickReturnHeight;

	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private int mState = STATE_ONSCREEN;
	private int mScrollY;
	private int mMinRawY = 0;
	View bottomView;

	public ProductFragmentList(View bottomView) {
		super();
		this.bottomView = bottomView;
		this.isSwitched = false;
	}

	public ProductFragmentList() {
		super();
		this.isSwitched = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_product_listview, null);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
		int color = R.color.red_background;
		swipeLayout.setColorScheme(color, color, color, color);
		rlNoItems = (RelativeLayout) v.findViewById(R.id.fragment_product_listview_layout_no_items);
		list = (QuickReturnListView) v.findViewById(R.id.lv_product);
		if (adapter == null) {
			adapter = new ProductAdapter(productModels);
		}
		list.setAdapter(adapter);
		homeFragment.setBottomView();
		initListner();
		if (isSwitched) {
			changeIfNoItem();
		}
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mQuickReturnHeight = list.getHeight();
				new Thread(new Runnable() {

					@Override
					public void run() {
						list.computeScrollY();
					}
				}).start();
			}
		});
	}

	private void initListner() {
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				homeFragment.new GetProductTaks(ProductFragmentList.this).execute(HomeFragment.STEP_REFRESH);
			}
		});
		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
				if (loadMore && totalItemCount > 1 && thelasttotalCount != totalItemCount) {
					thelasttotalCount = totalItemCount;
					homeFragment.new GetProductTaks(ProductFragmentList.this).execute(HomeFragment.STEP_ADDMORE);
				}
				handleQuickReturn();
			}
		});
	}

	@Override
	public void updateUI() {
		if (adapter == null) {
			adapter = new ProductAdapter(productModels);
		}
		adapter.updateData(productModels);
		changeIfNoItem();
	}
	
	public  void changeIfNoItem()  {
		if (rlNoItems == null || list == null) {
			return;
		}
		if (adapter.getListProducts() == null || adapter.getListProducts().size() == 0) {
			rlNoItems.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
		} else {
			rlNoItems.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
		}
	}

	private void handleQuickReturn() {
		mScrollY = 0;
		int translationY = 0;

		if (list.scrollYIsComputed()) {
			mScrollY = list.getComputedScrollY();
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
		if (bottomView != null)
			bottomView.setTranslationY(translationY);
	}

	@Override
	public void setBottomView(View bottomView) {
		this.bottomView = bottomView;
	}

	public void setIsSwitched(boolean isSwitched) {
		this.isSwitched = isSwitched;
	}
}
