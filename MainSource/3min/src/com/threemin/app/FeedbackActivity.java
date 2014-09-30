package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.threemins.R;

public class FeedbackActivity extends SwipeBackActivity {
    
    private RelativeLayout mRLNotSatisfied, mRLNoComment, mRLSatisfied;
    private ImageView mImgCheckNotSatisfied, mImgCheckNoComment, mImgCheckSatisfied;
    
    private ImageView mImgABBack, mImgABSubmit;
    
    public static final int CHECK_NOT_SATISFIED = 1;
    public static final int CHECK_NO_COMMENT = 2;
    public static final int CHECK_SATISFIED = 3;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
        initActionBar();
        initWidgets();
        initListeners();
        initFirstStart();
    }
    
    public void initActionBar() {
        ActionBar bar = getActionBar();
        bar.setDisplayShowHomeEnabled(false);
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowCustomEnabled(true);
        bar.setCustomView(R.layout.layout_custom_action_bar_act_feedback);
        
        mImgABBack = (ImageView) findViewById(R.id.act_feedback_action_bar_back);
        mImgABSubmit = (ImageView) findViewById(R.id.act_feedback_action_bar_submit);
    }

    public void initWidgets() {
        mRLNoComment = (RelativeLayout) findViewById(R.id.act_feedback_option_rl_no_comment);
        mRLNotSatisfied = (RelativeLayout) findViewById(R.id.act_feedback_option_rl_not_satisfied);
        mRLSatisfied = (RelativeLayout) findViewById(R.id.act_feedback_option_rl_satisfied);
        
        mImgCheckNoComment = (ImageView) findViewById(R.id.act_feedback_option_check_no_comment);
        mImgCheckNotSatisfied = (ImageView) findViewById(R.id.act_feedback_option_check_not_satisfied);
        mImgCheckSatisfied = (ImageView) findViewById(R.id.act_feedback_option_check_satisfied);
    }
    
    public void initListeners() {
        mRLNoComment.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doCheck(CHECK_NO_COMMENT);
            }
        });
        
        mRLNotSatisfied.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doCheck(CHECK_NOT_SATISFIED);
            }
        });
        
        mRLSatisfied.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doCheck(CHECK_SATISFIED);
            }
        });
        
        mImgABBack.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        mImgABSubmit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }
    
    public void doCheck(int check) {
        unselectAll();
        switch (check) {
        case CHECK_NO_COMMENT:
            mImgCheckNoComment.setVisibility(View.VISIBLE);
            break;

        case CHECK_NOT_SATISFIED:
            mImgCheckNotSatisfied.setVisibility(View.VISIBLE);
            break;
            
        case CHECK_SATISFIED:
            mImgCheckSatisfied.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }
    }
    
    public void unselectAll() {
        mImgCheckNoComment.setVisibility(View.GONE);
        mImgCheckNotSatisfied.setVisibility(View.GONE);
        mImgCheckSatisfied.setVisibility(View.GONE);
    }
    
    public void initFirstStart() {
//        unselectAll();
//        mImgCheckNotSatisfied.setVisibility(View.VISIBLE);
    }
    
    public void submitFeedback() {
        
    }
}
