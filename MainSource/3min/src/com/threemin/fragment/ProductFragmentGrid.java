package com.threemin.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.threemin.adapter.ProductGridAdapter;
import com.threemin.app.DetailActivity;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.view.QuickReturnGridView;
import com.threemin.view.QuickReturnGridView.OnItemDoubleTapLister;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class ProductFragmentGrid extends BaseProductFragment {

	
	private Context mContext;
	private LoginButton mLoginButton;

	// no items:
	private RelativeLayout rlNoItems;
	private boolean isSwitched;

	private QuickReturnGridView mGrid;
	private ProductGridAdapter mAdapter;
	View bottomView;

	private int mQuickReturnHeight;

	private static final int STATE_ONSCREEN = 0;
	private static final int STATE_OFFSCREEN = 1;
	private static final int STATE_RETURNING = 2;
	private int mState = STATE_ONSCREEN;
	private int mScrollY;
	private int mMinRawY = 0;
	private boolean isRequestLike;
	private int bottomHeight;
	
	public ProductFragmentGrid(View bottomView, Context context, LoginButton btn) {
		super();
		this.bottomView = bottomView;
		this.mContext = context;
		this.mLoginButton = btn;
		this.isSwitched = false;
	}

	public ProductFragmentGrid() {
		super();
		this.isSwitched = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_product_gridview, null);
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_gridview);
		int color = R.color.red_background;
		int color2 = R.color.common_grey;
        swipeLayout.setColorScheme(color, color2, color, color2);
		rlNoItems = (RelativeLayout) v.findViewById(R.id.fragment_product_gridview_layout_no_items);
		mGrid = (QuickReturnGridView) v.findViewById(R.id.gv_product);

		if (mAdapter == null) {
			// TODO
			mAdapter = new ProductGridAdapter(productModels, mContext, mLoginButton);
		}
		mGrid.setAdapter(mAdapter);

		initListner();
		if(homeFragment==null){
			homeFragment=(HomeFragment) getParentFragment();
		}
		homeFragment.setBottomView();
		if (isSwitched) {
			changeIfNoItem();
		}
		return v;
	}



	@Override
	public void updateUI() {
		if (mAdapter == null) {
			// TODO
			mAdapter = new ProductGridAdapter(productModels, mContext, mLoginButton);
		}
		mAdapter.updateData(productModels);
		changeIfNoItem();
	}

	@Override
	public void changeIfNoItem() {
		if (rlNoItems == null || mGrid == null) {
			return;
		}
		if (mAdapter.getListProducts() == null || mAdapter.getListProducts().size() == 0) {
			rlNoItems.setVisibility(View.VISIBLE);
			mGrid.setVisibility(View.GONE);
		} else {
			rlNoItems.setVisibility(View.GONE);
			mGrid.setVisibility(View.VISIBLE);
		}
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
	
	private void requestLike(final ProductModel productModel,View view){
		isRequestLike=true;
		final String tokken=PreferenceHelper.getInstance(getActivity()).getTokken();
		
		
		
		productModel.setLiked(!productModel.isLiked());
		if (productModel.isLiked()) {
			productModel.setLike(productModel.getLike() + 1);
		} else {
			productModel.setLike(productModel.getLike() - 1);
		}
		
		TextView tv_like = (TextView) view.findViewById(R.id.inflater_body_product_grid_tv_like);
		if (productModel.getLike() > 0) {
			tv_like.setText("" + productModel.getLike());
		} else {
			tv_like.setText("");
		}

		ImageView img_like = (ImageView) view.findViewById(R.id.inflater_body_product_grid_img_like);
		if (productModel.isLiked()) {
			img_like.setSelected(true);
		} else {
			img_like.setSelected(false);
		}
		
//		mAdapter.notifyDataSetChanged();
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean result=new UserWebService().productLike(productModel.getId(), tokken, productModel.isLiked());
				Log.d("result", "result="+result);
				isRequestLike=false;
			}
		});
		t.start();
	}
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        // mGrid.getViewTreeObserver().addOnGlobalLayoutListener(new
        // ViewTreeObserver.OnGlobalLayoutListener() {
        // @Override
        // public void onGlobalLayout() {
        // mQuickReturnHeight = mGrid.getHeight();
        // if (isRequestLike) {
        // return;
        // }
        // // mGrid.computeScrollY();
        // }
        // });
        bottomView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (bottomHeight == 0) {
                    bottomHeight = bottomView.getHeight();
                    Log.d("bottom", "height=" + bottomHeight);
                }
            }
        });
    }

    int mLastFirstVisibleItem;
    boolean mIsScrollingUp;

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

                if (scrollState == 0)
                    Log.i("a", "scrolling stopped...");

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
                if (loadMore && totalItemCount > 1 && thelasttotalCount != totalItemCount) {
                    thelasttotalCount = totalItemCount;
                    homeFragment.new GetProductTaks(ProductFragmentGrid.this).execute(HomeFragment.STEP_ADDMORE);
                }

                if (view.getId() == mGrid.getId()) {
                    final int currentFirstVisibleItem = mGrid.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        Log.i("a", "scrolling down...");
                        if (mIsScrollingUp) {
                            mIsScrollingUp = false;
                            bottomView.setTranslationY(bottomHeight);
                        }
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        Log.i("a", "scrolling up...");
                        if (!mIsScrollingUp) {
                            mIsScrollingUp = true;
                            bottomView.setTranslationY(0);
                        }
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
                // handleQuickReturn();
            }
        });

        mGrid.setOnItemDoubleClickListener(new OnItemDoubleTapLister() {

            @Override
            public void OnSingleTap(AdapterView parent, View view, int position, long id) {
                ProductModel model = (ProductModel) mGrid.getItemAtPosition(position);
                if (model != null) {
                    String data = new Gson().toJson(model);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_right_in,R.anim.anim_no_animation);
                }
            }

            @Override
            public void OnDoubleTap(AdapterView parent, View view, int position, long id) {
                ProductModel model = (ProductModel) mGrid.getItemAtPosition(position);
                if (model != null) {

                    requestLike(model, view);
                }
            }
        });
    }

}
