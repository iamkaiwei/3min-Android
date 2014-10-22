package com.threemin.app;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.threemin.adapter.FeedbackAdapter;
import com.threemin.model.FeedbackModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.FeedbackWebservice;
import com.threemins.R;

public class FeedbackDialogActivity extends Activity {
    
    public static final String tag = "FeedbackDialogActivity";
    
    public static final int STEP_INIT = 1;
    public static final int STEP_REFRESH = 2;
    public static final int STEP_LOAD_MORE = 3;
    
    private int mUserID;
    private boolean mFromPushNotification;
    
    private List<FeedbackModel> mFeedbackList;
    private FeedbackAdapter mFeedbackAdapter;
    private int mFeedbackLastCount;
    private int mFeedbackPage;
    private ListView mLvFeedback;
    private ImageView mImgFeedbackClose;
    private SwipeRefreshLayout mSwipeFeedback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);
        
        mFromPushNotification = getIntent().getBooleanExtra(CommonConstant.INTENT_IS_FROM_PUSH_NOTIFICATION, false);
        if (mFromPushNotification) {
            UserModel currentUser = PreferenceHelper.getInstance(this).getCurrentUser();
            mUserID = currentUser.getId();
        } else {
            mUserID = getIntent().getIntExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, 0);
        }
        
        initWidgets();
        initListeners();
        initFirstStart();
    }
    
    public void initWidgets() {
        mLvFeedback = (ListView) findViewById(R.id.dialog_feedback_lv);
        mImgFeedbackClose = (ImageView) findViewById(R.id.dialog_feedback_img_close);
        
        mSwipeFeedback = (SwipeRefreshLayout) findViewById(R.id.dialog_feedback_swipe);
        int color = R.color.red_background;
        int color2 = R.color.common_grey;
        mSwipeFeedback.setColorScheme(color, color2, color, color2);
    }
    
    public void initListeners() {
        mLvFeedback.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount -1;
                if (loadMore && totalItemCount > 1 && mFeedbackLastCount != totalItemCount) {
                    mFeedbackLastCount = totalItemCount;
                    new GetListFeedbackTask(STEP_LOAD_MORE).execute();
                }
            }
        });
        
        mSwipeFeedback.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetListFeedbackTask(STEP_REFRESH).execute();
            }
        });
        
        mImgFeedbackClose.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                FeedbackDialogActivity.this.onBackPressed();
            }
        });
    }
    
    public void initFirstStart() {
        new GetListFeedbackTask(STEP_INIT).execute();
    }
    
    public class GetListFeedbackTask extends AsyncTask<Void, Void, List<FeedbackModel>> {
        
        private int step;
        
        public GetListFeedbackTask(int step) {
            this.step = step;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipeFeedback != null) {
                mSwipeFeedback.setRefreshing(true);
            }
            if (step == STEP_INIT || step == STEP_REFRESH) {
                mFeedbackPage = 1;
                mFeedbackLastCount = 0;
            } else {
                mFeedbackPage++;
            }
        }
        
        @Override
        protected List<FeedbackModel> doInBackground(Void... params) {
            String token = PreferenceHelper.getInstance(FeedbackDialogActivity.this).getTokken();
            List<FeedbackModel> result = new FeedbackWebservice().getFeedbackOfUser(token, mUserID, mFeedbackPage);
            Log.i(tag, "List feedback size: " + result.size());
            return result;
        }
        
        @Override
        protected void onPostExecute(List<FeedbackModel> result) {
            super.onPostExecute(result);
            
            if (mSwipeFeedback != null && mSwipeFeedback.isRefreshing()) {
                mSwipeFeedback.setRefreshing(false);
            }
            
            if (result != null && result.size() != 0) {
                if (step == STEP_INIT || step == STEP_REFRESH) {
                    mFeedbackList = result;
                    mFeedbackAdapter = new FeedbackAdapter(FeedbackDialogActivity.this, mFeedbackList);
                    mLvFeedback.setAdapter(mFeedbackAdapter);
                } else {
                    mFeedbackList.addAll(result);
                    mFeedbackAdapter.setListFeedback(mFeedbackList);
                }
            }
        }
    }

}
