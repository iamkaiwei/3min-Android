package com.threemin.app;

import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.threemin.adapter.UserLikedProductAdapter;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

/**
 * This actiivty is called by DetailFragment
 * when user tap on textview which show number of user who liked this product
 * */

public class ListUsersLikedActivity extends SwipeBackActivity {
    
    public static final String tag = "ListUsersLikedActivity";
    public static final int STEP_INIT = 0;
    public static final int STEP_REFRESH = 1;
    public static final int STEP_LOAD_MORE = 2;
    
    private ListView mLvContent;
    private List<UserModel> mListUser;
    private UserLikedProductAdapter mAdapter;
    private SwipeRefreshLayout mSwipe;
    
    private int mProductID;
    private int mPage;
    private int mLastTotalCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users_liked);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
        initWidgets();
        initListeners();
        initFirstData();
    }
    
    public void initWidgets() {
        mLvContent = (ListView) findViewById(R.id.act_list_users_liked_lv_content);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.act_list_users_liked_swipe);
        int color = R.color.red_background;
        int color2 = R.color.common_grey;
        mSwipe.setColorScheme(color, color2, color, color2);
    }
    
    public void initListeners() {
        mSwipe.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetUsersWhoLikedProductTask(STEP_REFRESH).execute();
            }
        });
        
        mLvContent.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount -1;
                if (loadMore && totalItemCount > 1 && totalItemCount != mLastTotalCount) {
                    new GetUsersWhoLikedProductTask(STEP_LOAD_MORE).execute();
                }
            }
        });
    }
    
    public void initFirstData() {
        mProductID = getIntent().getIntExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, 0);
        new GetUsersWhoLikedProductTask(STEP_INIT).execute();
    }
    
    /**
     * This user model only has infomation of user ID, user avatar, user full name
     * */
    public class GetUsersWhoLikedProductTask extends AsyncTask<Void, Void, List<UserModel>> {
        
        private int currentStep;
        
        public GetUsersWhoLikedProductTask(int step) {
            currentStep = step;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipe != null) {
                mSwipe.setRefreshing(true);
            }
            
        }
        
        @Override
        protected List<UserModel> doInBackground(Void... params) {
            switch (currentStep) {
            case STEP_INIT:
                mPage = 1;
                mLastTotalCount = 0;
                break;
                
            case STEP_LOAD_MORE:
                mPage++;
                break;
                
            case STEP_REFRESH:
                mPage = 1;
                mLastTotalCount = 0;
                break;

            default:
                break;
            }
            String token = PreferenceHelper.getInstance(ListUsersLikedActivity.this).getTokken();
            return new UserWebService().getListUserWhoLikedAProduct(mProductID, token, mPage);
        }
        
        @Override
        protected void onPostExecute(List<UserModel> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (mSwipe.isRefreshing()) {
                mSwipe.setRefreshing(false);
            }
            if (result != null && result.size() > 0) {
                if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                    mListUser = result;
                    mAdapter = new UserLikedProductAdapter(ListUsersLikedActivity.this, mListUser);
                    mLvContent.setAdapter(mAdapter);
                } else {
                    mListUser.addAll(result);
                    mAdapter.setList(mListUser);
                }
            }
        }
    }

}
