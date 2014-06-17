package com.threemin.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.threemin.app.ChatToBuyActivity;
import com.threemin.model.Conversation;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ConversationWebService;
import com.threemins.R;

public class PostOfferFragment extends Fragment {
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int POST_OFFER = 3;
	ProductModel mProductModel;
	ProgressDialog dialog;

	TextView title;
	EditText price;
	ImageView rotateImg;
	View changeView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_post_offer, container, false);
		initView(rootView);
		initData();
		initListner();
		return rootView;
	}

	private void initData() {
		mProductModel = new Gson().fromJson(getActivity().getIntent()
				.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
		String sTitle = String.format(getString(R.string.postoffer_title), mProductModel.getOwner().getFirstName(),
				mProductModel.getPrice());
		title.setText(sTitle);
		price.setText(mProductModel.getPrice());
	}

	private void initListner() {
		changeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_around_center_point);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						CommonUti.showKeyboard(price, getActivity());
					}
				});
				rotateImg.startAnimation(animation);
			}
		});
	}

	private void initView(View rootView) {
		title = (TextView) rootView.findViewById(R.id.post_offer_title);
		price = (EditText) rootView.findViewById(R.id.et_label_price);
		rotateImg = (ImageView) rootView.findViewById(R.id.rotate_img);
		changeView = rootView.findViewById(R.id.view_change);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_DIALOG:
				dialog = new ProgressDialog(getActivity());
				dialog.setMessage(getString(R.string.please_wait));
				dialog.show();
				break;
			case HIDE_DIALOG:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				break;
			case POST_OFFER:
				Conversation conversation = (Conversation) msg.obj;
				String data = new Gson().toJson(mProductModel);
				if (conversation == null || conversation.getChannel_Name() == null) {
					// Intent intent = new Intent(getActivity(),
					// PostOfferActivity.class);
					// intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA,
					// data);
					// getActivity().startActivity(intent);
				} else {
					String conversationData = new Gson().toJson(conversation);
					Intent intent = new Intent(getActivity(), ChatToBuyActivity.class);
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversationData);
					getActivity().startActivity(intent);
				}
				break;
			default:
				break;
			}
		};
	};

	public void postOffer() {
		if (TextUtils.isEmpty(price.getText())) {

		} else {
			mHandler.sendEmptyMessage(SHOW_DIALOG);
			final float offer = Float.parseFloat(price.getText().toString());
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
					int product_id = mProductModel.getId();
					int to = mProductModel.getOwner().getId();
					Conversation conversation = new ConversationWebService().createOffer(tokken, product_id, to, offer);
					Message msg = new Message();
					msg.what = POST_OFFER;
					msg.obj = conversation;
					mHandler.sendEmptyMessage(HIDE_DIALOG);
					mHandler.sendMessage(msg);
				}
			});
			t.start();
		}
	}
	public ProductModel getProductModel() {
		return mProductModel;
	}
	
	public String getOffer() {
		return price.getText().toString();
	}
}
