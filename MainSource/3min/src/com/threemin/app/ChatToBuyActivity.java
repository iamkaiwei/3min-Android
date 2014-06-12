package com.threemin.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class ChatToBuyActivity extends Activity {
	
	ImageView mImgProduct;
	TextView mTvProductName;
	TextView mTvProductPrice;
	EditText mEtChatInput;
	TextView mTvOfferedPrice;
	TextView mTvLocation;
	ImageView mImgSelling;
	TextView mTvChatContentLabel;
	ListView mLvChatContent;
	
	ProductModel mProductModel;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_to_buy);
		
		initWidgets();
		initData();
		initActionBar();
	}

	private void initWidgets() {
		 mImgProduct = (ImageView) findViewById(R.id.activity_chat_img_product);
		 mTvProductName = (TextView) findViewById(R.id.activity_chat_tv_product_name);
		 mTvProductPrice = (TextView) findViewById(R.id.activity_chat_tv_product_price);
		 mEtChatInput = (EditText) findViewById(R.id.activity_chat_et_chat_input);
		 mTvOfferedPrice = (TextView) findViewById(R.id.activity_chat_tv_offered_price);
		 mTvLocation = (TextView) findViewById(R.id.activity_chat_tv_location);
		 mImgSelling = (ImageView) findViewById(R.id.activity_chat_selling);
		 mTvChatContentLabel = (TextView) findViewById(R.id.activity_chat_tv_chat_content_label);
		 mLvChatContent = (ListView) findViewById(R.id.activity_chat_lv_chat_content);
	}
	
	private void initData() {
		mProductModel = new Gson().fromJson(this.getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
		
		UrlImageViewHelper.setUrlDrawable(mImgProduct, mProductModel.getImages().get(0).getOrigin());
		mTvProductName.setText(mProductModel.getName());
		mTvProductPrice.setText(mProductModel.getPrice());
		mTvLocation.setText(mProductModel.getVenueName());
		mTvChatContentLabel.setText(getString(R.string.activity_chat_chat_content_label) + " " + mProductModel.getOwner().getFullName());
		getActionBar().setTitle(mProductModel.getOwner().getFullName());
	}
	
	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setIcon(R.drawable.ic_back);
		bar.setDisplayShowTitleEnabled(true);
		
		((ImageView)findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);
		
	}

}
