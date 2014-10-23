package com.threemin.app;

import java.util.List;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.FeedbackWebservice;
import com.threemin.webservice.ProductWebservice;
import com.threemins.R;

public class FeedbackActivity extends ThreeMinsBaseActivity {
    
    public static final String tag = "FeedbackActivity";
    
    private RelativeLayout mRLNotSatisfied, mRLNoComment, mRLSatisfied;
    private ImageView mImgCheckNotSatisfied, mImgCheckNoComment, mImgCheckSatisfied;
    
    private ImageView mImgABBack, mImgABSubmit;
    
    private ImageView mImgProduct;
    private EditText mEtFeedbackContent;
    
    public static final int CHECK_NOT_SATISFIED = 1;
    public static final int CHECK_NO_COMMENT = 2;
    public static final int CHECK_SATISFIED = 3;
    
    private boolean mIsFromPushNotification;
    private String mProductID;
    private String mScheduleID;
    private int mFeedbackStatus;
    private ProductModel mProduct;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        
        initActionBar();
        initWidgets();
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
        
        mImgProduct = (ImageView) findViewById(R.id.act_feedback_img_product);
        mEtFeedbackContent = (EditText) findViewById(R.id.act_feedback_et_input);
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
            mFeedbackStatus = CHECK_NO_COMMENT;
            break;

        case CHECK_NOT_SATISFIED:
            mImgCheckNotSatisfied.setVisibility(View.VISIBLE);
            mFeedbackStatus = CHECK_NOT_SATISFIED;
            break;
            
        case CHECK_SATISFIED:
            mImgCheckSatisfied.setVisibility(View.VISIBLE);
            mFeedbackStatus = CHECK_SATISFIED;
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
        Intent intent = getIntent();
        mIsFromPushNotification = intent.getBooleanExtra(CommonConstant.INTENT_IS_FROM_PUSH_NOTIFICATION, false);
        
        if (mIsFromPushNotification) {
            mProductID = intent.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID);
            mScheduleID = intent.getStringExtra(CommonConstant.INTENT_SCHEDULE_ID);
        }
        
        mFeedbackStatus = CHECK_NOT_SATISFIED;
        new GetProductViaIdTask().execute(mProductID);
    }
    
    
    
    private class GetProductViaIdTask extends AsyncTask<String, Void, ProductModel> {
        
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(FeedbackActivity.this);
                dialog.setMessage(getString(R.string.please_wait));
            }
            dialog.show();
        }
        
        @Override
        protected ProductModel doInBackground(String... params) {
            String tokken = PreferenceHelper.getInstance(FeedbackActivity.this).getTokken();
            return new ProductWebservice().getProductViaID(tokken, params[0]);
        }
        
        @Override
        protected void onPostExecute(ProductModel result) {
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result != null) {
                mProduct = result;
                initData();
            }
        }
        
    }
    
    public void initData() {
        List<ImageModel> imgs = mProduct.getImages();
        if (imgs != null && imgs.size() > 0) {
            UrlImageViewHelper.setUrlDrawable(mImgProduct, imgs.get(0).getOrigin(), R.drawable.stuff_img);
        } else {
            mImgProduct.setBackgroundResource(R.drawable.stuff_img);
        }
        initListeners();
    }
    
    public void submitFeedback() {
        String content = mEtFeedbackContent.getText().toString();
        if (TextUtils.isEmpty(content) && mFeedbackStatus == CHECK_NOT_SATISFIED) {
            showToast(getString(R.string.act_feedback_empty_feedback_warning));
            return;
        } else {
            new SendFeedbackTask().execute();
        }
    }
    
    private class SendFeedbackTask extends AsyncTask<Void, Void, Integer> {
        
        private ProgressDialog dialog;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(FeedbackActivity.this);
                dialog.setMessage(getString(R.string.please_wait));
            }
            dialog.show();
        }
        
        @Override
        protected Integer doInBackground(Void... params) {
            String token = PreferenceHelper.getInstance(FeedbackActivity.this).getTokken();
            int responseCode = new FeedbackWebservice().sendFeedback(
                    token, 
                    mProduct.getId(), 
                    Integer.parseInt(mScheduleID), 
                    mEtFeedbackContent.getText().toString(), 
                    mFeedbackStatus, 
                    mProduct.getOwner().getId());
            Log.i(tag, "SendFeedbackTask code: " + responseCode);
            return responseCode;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result == WebserviceConstant.RESPONSE_CODE_SUCCESS) {
                showToast(getString(R.string.act_feedback_sent_successfully));
                mImgABSubmit.setEnabled(false);
            } else {
                showToast(getString(R.string.act_feedback_sent_fail));
            }
        }
    }
    
    public void showToast(String msg) {
        Toast.makeText(FeedbackActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}
