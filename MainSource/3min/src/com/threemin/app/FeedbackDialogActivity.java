package com.threemin.app;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.threemin.adapter.FeedbackAdapter;
import com.threemin.model.FeedbackModel;
import com.threemins.R;

public class FeedbackDialogActivity extends Activity {
    
    public static final String tag = "FeedbackDialogActivity";
    
    public static final int STEP_INIT = 1;
    public static final int STEP_REFRESH = 2;
    public static final int STEP_LOAD_MORE = 3;
    
    private List<FeedbackModel> mFeedbackList;
    private FeedbackAdapter mFeedbackAdapter;
    private int mFeedbackLastCount;
    private int mFeedbackPage;
    private ListView mLvFeedback;
    private ImageView mImgFeedbackClose;
    private SwipeRefreshLayout mSwipeFeedback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);
        
        initWidgets();
        initListeners();
    }
    
    public void initWidgets() {
        mLvFeedback = (ListView) findViewById(R.id.dialog_feedback_lv);
        mImgFeedbackClose = (ImageView) findViewById(R.id.dialog_feedback_img_close);
        mSwipeFeedback = (SwipeRefreshLayout) findViewById(R.id.dialog_feedback_swipe);
    }
    
    public void initListeners() {
        mLvFeedback.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //TODO: detect end of list and load more
            }
        });
        
        mSwipeFeedback.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                // TODO refresh
                
            }
        });
    }
    
    public class GetListFeedbackTask extends AsyncTask<Void, Void, List<FeedbackModel>> {
        
        private int step;
        
        public GetListFeedbackTask(int step) {
            this.step = step;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        
        @Override
        protected List<FeedbackModel> doInBackground(Void... params) {
            return null;
        }
        
        @Override
        protected void onPostExecute(List<FeedbackModel> result) {
            super.onPostExecute(result);
        }
    }

}
