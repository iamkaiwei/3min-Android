package com.threemin.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.DetailActivity;
import com.threemin.app.ListOfferActivty;
import com.threemin.app.PostOfferActivity;
import com.threemin.model.Conversation;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ConversationWebService;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class DetailFragment extends Fragment {
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int REQUEST_CHECK_OFFER_EXIST = 3;
	private final int REQUEST_GET_LIST_OFFER = 4;
	View rootView;
	ProductModel productModel;
	ViewPager pager;
	Button btnChatToBuy, btnViewOffers;
	LinearLayout lnImgs, btnLike;
	ProgressDialog dialog, dialogPushReceived;
	String conversationData;
	List<Conversation> conversations;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Intent intent = getActivity().getIntent();
		
		String productID = intent.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID);
		Log.i("DetailFragment", "Product ID: " + productID);
		
		rootView = inflater.inflate(R.layout.fragment_detail, null);
		
		if (productID == null) {
			productModel = new Gson().fromJson(getActivity().getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
			initBody(rootView);
		} else {
			rootView.setVisibility(View.INVISIBLE);
			dialogPushReceived = new ProgressDialog(getActivity());
			dialogPushReceived.setMessage(getString(R.string.please_wait));
			dialogPushReceived.show();
			new GetProductViaIdTask().execute(productID);
		}
		
		return rootView;
	}

	private void initBody(View convertView) {

		if (productModel != null) {
//			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
			// if (productModel.getImages().size() > 0) {
			// UrlImageViewHelper.setUrlDrawable(image,
			// model.getImages().get(0).getMedium());
			// }

			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_name);
			tv_name.setText(productModel.getName());
			getActivity().setTitle(productModel.getName());

			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_price);
			tv_price.setText(productModel.getPrice() + CommonConstant.CURRENCY);

			TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_like);
			if (productModel.getLike() > 0) {
				tv_like.setText("" + productModel.getLike() + " people like this");
			} else {
				tv_like.setVisibility(View.GONE);
			}
			TextView tv_locaion = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_location);
			if (!TextUtils.isEmpty(productModel.getVenueName())) {
				tv_locaion.setText(productModel.getVenueName());
			} else {
				tv_locaion.setVisibility(View.GONE);
			}

			TextView tv_description = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_description);
			if (!TextUtils.isEmpty(productModel.getDescription())) {
				tv_description.setText(productModel.getDescription());
			} else {
				tv_description.setVisibility(View.GONE);
			}

			ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.inflater_header_product_image);
			UrlImageViewHelper.setUrlDrawable(imageAvatar, productModel.getOwner().getFacebook_avatar());

			TextView tv_name_owner = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_name);
			tv_name_owner.setText(productModel.getOwner().getFullName());

			TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_time);
			tv_time.setText(DateUtils.getRelativeTimeSpanString(productModel.getUpdateTime() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));

			lnImgs = (LinearLayout) convertView.findViewById(R.id.ln_img);
			initImage();
			btnChatToBuy = (Button) convertView.findViewById(R.id.fragment_detail_btn_chat_to_buy);
			UserModel currentUser = PreferenceHelper.getInstance(getActivity()).getCurrentUser();

			// if current user is not the owner of this product
			if (currentUser.getId() != productModel.getOwner().getId()) {
				btnChatToBuy.setBackgroundResource(R.drawable.bt_chat_to_buy);
				btnChatToBuy.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						checkOffer();
					}
				});
			} else {
				checkListOffer();
				btnChatToBuy.setBackgroundResource(R.drawable.bt_view_offers);
				btnChatToBuy.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String data = new Gson().toJson(productModel);
						Intent intent = new Intent(getActivity(), ListOfferActivty.class);
						intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
						intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversationData);
						getActivity().startActivity(intent);
					}
				});
			}
			btnLike = (LinearLayout) convertView.findViewById(R.id.btn_like);
			btnLike.setSelected(productModel.isLiked());
			btnLike.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					requestLike();
				}
			});
		}
		
		LinearLayout lnShare = (LinearLayout) convertView.findViewById(R.id.fm_detail_ln_share);
		lnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new CommonUti().doShareProductOnFacebook(getActivity(), ( (DetailActivity)getActivity() ).getLoginButton(), productModel);
			}
		});

	}

	private void initImage() {
		for (ImageModel imageModel : productModel.getImages()) {
			ImageView imageView = new ImageView(getActivity());
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			int spacing = (int) getResources().getDimension(R.dimen.common_spacing);
			imageView.setAdjustViewBounds(true);
			imageView.setPadding(0, spacing, 0, spacing);
			imageView.setLayoutParams(params);
			lnImgs.addView(imageView);
			UrlImageViewHelper.setUrlDrawable(imageView, imageModel.getOrigin(), R.drawable.stuff_img);
		}
	}

	private void requestLike() {
		final String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();

		productModel.setLiked(!productModel.isLiked());
		if (productModel.isLiked()) {
			productModel.setLike(productModel.getLike() + 1);
		} else {
			productModel.setLike(productModel.getLike() - 1);
		}

		if (productModel.isLiked()) {
			btnLike.setSelected(true);
		} else {
			btnLike.setSelected(false);
		}

		// mAdapter.notifyDataSetChanged();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean result = new UserWebService().productLike(productModel.getId(), tokken, productModel.isLiked());
				Log.d("result", "result=" + result);
			}
		});
		t.start();
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
			case REQUEST_CHECK_OFFER_EXIST:
				Conversation conversation = (Conversation) msg.obj;
				String data = new Gson().toJson(productModel);
				if (conversation == null || conversation.getId() == 0) {
					Intent intent = new Intent(getActivity(), PostOfferActivity.class);
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					getActivity().startActivity(intent);
				} else {
					String conversationData = new Gson().toJson(conversation);
					Intent intent = new Intent(getActivity(), ChatToBuyActivity.class);
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversationData);
					getActivity().startActivity(intent);
				}
				break;
			case REQUEST_GET_LIST_OFFER:
				if(conversations==null || conversations.isEmpty()){
					btnChatToBuy.setBackgroundResource(R.drawable.bt_no_offer_yet);
				}
				break;
			default:
				break;
			}
		};
	};

	private void checkOffer() {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
				int product_id = productModel.getId();
				int to = productModel.getOwner().getId();
				Conversation conversation = new ConversationWebService().getConversation(tokken, product_id, to);
				Message msg = new Message();
				msg.what = REQUEST_CHECK_OFFER_EXIST;
				msg.obj = conversation;
				mHandler.sendEmptyMessage(HIDE_DIALOG);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}
	
	private void checkListOffer(){
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
				int product_id = productModel.getId();
				conversationData = new ProductWebservice().getListOffer(tokken, product_id);
				conversations=new ProductWebservice().parseListConversation(conversationData);
				Message msg = new Message();
				msg.what = REQUEST_GET_LIST_OFFER;
				msg.obj = conversations;
				mHandler.sendEmptyMessage(HIDE_DIALOG);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}
	
	private class GetProductViaIdTask extends AsyncTask<String, Void, ProductModel> {

		@Override
		protected ProductModel doInBackground(String... params) {
			String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
			return new ProductWebservice().getProductViaID(tokken, params[0]);
		}
		
		@Override
		protected void onPostExecute(ProductModel result) {
			super.onPostExecute(result);
			if (rootView.getVisibility() == View.INVISIBLE) {
				rootView.setVisibility(View.VISIBLE);
			}
			if (dialogPushReceived != null && dialogPushReceived.isShowing()) {
				dialogPushReceived.dismiss();
			}
			if (result != null) {
				productModel = result;
				initBody(rootView);
			}
		}
		
	}
}
