package com.threemin.app;

import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.threemin.adapter.CommentAdapter;
import com.threemin.fragment.DetailFragment;
import com.threemin.model.CommentModel;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.CommentWebService;
import com.threemins.R;

public class CommentActivity extends SwipeBackActivity{
    
    private int mProductID;
    private List<CommentModel> mListComment;
    private CommentAdapter mAdapter;
    private ListView mLvComment;
    private int mPage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
        mProductID = getIntent().getIntExtra(DetailFragment.INTENT_PRODUCT_ID_FOR_COMMENT, -1);
        
        mPage = 1;
        
        initWidgets();
        initListener();
        initActionBar();
        
        if (mProductID != -1) {
            new GetCommentsTask().execute(mPage);
        }
    }

    public void initWidgets() {
        mLvComment = (ListView) findViewById(R.id.activity_comment_lv);
    }
    
    public void initListener() {
        
    }
    
    private void initActionBar() {
        ActionBar bar = getActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_back);
        bar.setDisplayShowTitleEnabled(true);
        
        ((ImageView)findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);
        
    }
    
    @Override
    public void onBackPressed() {
        scrollToFinishActivity();
    }
    
    private class GetCommentsTask extends AsyncTask<Integer, Void, List<CommentModel>> {

        @Override
        protected List<CommentModel> doInBackground(Integer... params) {
            String token = PreferenceHelper.getInstance(CommentActivity.this).getTokken();
            return new CommentWebService().getComments(token, mProductID , params[0]);
        }
        
        @Override
        protected void onPostExecute(List<CommentModel> result) {
            if (result != null && result.size() != 0) {
                mListComment = result;
                mAdapter = new CommentAdapter(CommentActivity.this, mListComment);
                mLvComment.setAdapter(mAdapter);
            }
            super.onPostExecute(result);
        }
        
    }

}
