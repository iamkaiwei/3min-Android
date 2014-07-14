package com.threemin.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.User;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.util.HttpAuthorizer;
import com.threemin.adapter.MessageAdapter;
import com.threemin.model.Conversation;
import com.threemin.model.MessageModel;
import com.threemin.model.ProductModel;
import com.threemin.model.ReplyModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.ConversationWebService;
import com.threemin.webservice.ProductWebservice;
import com.threemins.R;

public class ChatToBuyActivity extends Activity {
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int REQUEST_CHECK_OFFER_EXIST = 3;
	private final int MESSAGE_TOTAL = 20;
	
	public static final boolean IS_THEIR_MESSAGE = true;
	public static final boolean IS_MY_MESSAGE = false;
	
	String mProductID;
	String mConversationID;
	String mTokken;
	
	ImageView mImgProduct;
	TextView mTvProductName;
	TextView mTvProductPrice;
	EditText mEtChatInput;
	TextView mTvOfferedPrice;
	TextView mTvLocation;
	ImageView mImgSelling;
	TextView mTvChatContentLabel;

	ListView mLvChatContent;
	ArrayList<MessageModel> mListMessage;
	MessageAdapter mMessageAdapter;

	ProductModel mProductModel;
	String mOfferedPrice;
	Conversation conversation;
	PresenceChannel presenceChannel;
	UserModel currentUser;
	Pusher pusher;
	List<MessageModel> userChats;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_to_buy);

		initWidgets();
		initData();

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

	// demo data
	private void initListview() {
		mListMessage = new ArrayList<MessageModel>();
		// MessageModel temp1 = new MessageModel("Hello,  how are you?",
		// true,conversation.getUser());
		// MessageModel temp2 = new MessageModel("I'm fine, thanks",
		// false,currentUser);
		// mListMessage.add(temp1);
		// mListMessage.add(temp2);
		mMessageAdapter = new MessageAdapter(this, mListMessage);
		mLvChatContent.setAdapter(mMessageAdapter);
	}

	private void initData() {
		userChats = new ArrayList<MessageModel>();
		mTokken = PreferenceHelper.getInstance(ChatToBuyActivity.this).getTokken();
		
		Intent intent = getIntent();
		mProductID = intent.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID);
		mConversationID = intent.getStringExtra(CommonConstant.INTENT_CONVERSATION_DATA_VIA_ID);
		
		if (mProductID != null && mConversationID != null) {
			//get from webservice
			new GetConversationViaIdTask().execute(mConversationID);
		} else {
			mProductModel = new Gson().fromJson(this.getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA),
					ProductModel.class);
			// mOfferedPrice =
			// this.getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_OFFER);
			conversation = new Gson().fromJson(getIntent().getStringExtra(CommonConstant.INTENT_CONVERSATION_DATA),
					Conversation.class);
			setData();
			
		}
	}
	
	private void setData() {
		mOfferedPrice = conversation.getOffer() + "";
		int size = mProductModel.getImages().size();
		if (size > 0) {
			UrlImageViewHelper.setUrlDrawable(mImgProduct, mProductModel.getImages().get(0).getOrigin());
		} else {
			mImgProduct.setImageResource(R.drawable.stuff_img);
		}
		mTvProductName.setText(mProductModel.getName());
		mTvProductPrice.setText(mProductModel.getPrice() + CommonConstant.CURRENCY);
		mTvOfferedPrice.setText(mOfferedPrice + CommonConstant.CURRENCY);
		mTvLocation.setText(mProductModel.getVenueName());
		mTvChatContentLabel.setText(getString(R.string.activity_chat_chat_content_label) + " "
				+ conversation.getUser().getFullName());
		getActionBar().setTitle(conversation.getUser().getFullName());
		currentUser = PreferenceHelper.getInstance(this).getCurrentUser();
		
		initActionBar();
		initPusher();
		initListview();
		initHistory();
	}

	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setIcon(R.drawable.ic_back);
		bar.setDisplayShowTitleEnabled(true);

		((ImageView) findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {

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

	private void initPusher() {
		HttpAuthorizer authorizer = new HttpAuthorizer(WebserviceConstant.PUSH_AUTHORIZE);
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("Authorization", "Bearer " + PreferenceHelper.getInstance(ChatToBuyActivity.this).getTokken());
		authorizer.setHeaders(header);
		PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
		pusher = new Pusher(CommonConstant.PUSHER_KEY, options);
		pusher.connect();

		// Disconnect from the service (or become disconnected my network
		// conditions)

		// Reconnect, with all channel subscriptions and event bindings
		// automatically recreated
		presenceChannel = pusher.subscribePresence(conversation.getChannel_Name());
		presenceChannel.bind(CommonConstant.PUSHER_CHAT_EVENT_NAME, new PresenceChannelEventListener() {

			@Override
			public void onEvent(String arg0, final String arg1, final String arg2) {
				Log.d("onEvent", "arg1=" + arg2);
				runOnUiThread(new Runnable() {
					public void run() {
						MessageModel messageModel = new MessageModel(arg2, conversation.getUser(), IS_THEIR_MESSAGE);
						mMessageAdapter.addData(messageModel);
					}
				});
			}

			@Override
			public void onSubscriptionSucceeded(String arg0) {
				Log.d("onSubscriptionSucceeded", arg0);
			}

			@Override
			public void onAuthenticationFailure(String arg0, Exception arg1) {
				Log.d("onAuthenticationFailure", arg0);
			}

			@Override
			public void userUnsubscribed(String arg0, User arg1) {
				Log.d("userUnsubscribed", arg0);
			}

			@Override
			public void userSubscribed(String arg0, User arg1) {
				Log.d("userSubscribed", arg1.getInfo());
			}

			@Override
			public void onUsersInformationReceived(String arg0, Set<User> arg1) {
				Log.d("onUsersInformationReceived", arg0);
			}
		});
	}

	public void onSendChat(View v) {
		String msg = mEtChatInput.getText().toString();
		JsonObject data = new JsonObject();
		data.addProperty("name", currentUser.getFullName());
		data.addProperty("message", msg);
		data.addProperty("timestamp", System.currentTimeMillis() / 1000);
		// Log.d("size",""+ presenceChannel.getUsers().size());
		MessageModel messageModel = new MessageModel(data.toString(), currentUser, IS_MY_MESSAGE);
		if (presenceChannel.getUsers().size() > 1) {
			if(pusher.getConnection().getState()==ConnectionState.DISCONNECTED){
				pusher.connect();
			}
			presenceChannel.trigger(CommonConstant.PUSHER_CHAT_EVENT_NAME, data.toString());
			handleLocalData(messageModel);
		} else {
			sendOfflineMessage(messageModel);
		}
		mMessageAdapter.addData(messageModel);
		mEtChatInput.setText("");

	}

	private void handleLocalData(MessageModel messageModel) {
		userChats.add(messageModel);
		if (userChats.size() == MESSAGE_TOTAL) {
			postBundleMessageToServer();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		postBundleMessageToServer();
	}
	
	@Override
	protected void onDestroy() {
		pusher.disconnect();
		super.onDestroy();
	}

	private void sendOfflineMessage(final MessageModel messageModel) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(ChatToBuyActivity.this).getTokken();
				String message = messageModel.getMsg();
				boolean result = new ConversationWebService().postMessageOffline(conversation.getId(), tokken, message);
				Log.d("resultPostMessage", "" + result);
			}
		});
		t.start();
	}

	private void postBundleMessageToServer() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(ChatToBuyActivity.this).getTokken();
				boolean result = new ConversationWebService().postBulkMessage(conversation.getId(), tokken, userChats);
				userChats.clear();
				Log.d("resultPostMessage", "" + result);
			}
		});
		t.start();
	}

	private void initHistory() {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(ChatToBuyActivity.this).getTokken();
				String conversationData = new ConversationWebService().getDetailConversation(tokken,
						conversation.getId());
				Message msg = new Message();
				msg.what = REQUEST_CHECK_OFFER_EXIST;
				msg.obj = conversationData;
				mHandler.sendEmptyMessage(HIDE_DIALOG);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_DIALOG:
				dialog = new ProgressDialog(ChatToBuyActivity.this);
				dialog.setMessage(getString(R.string.please_wait));
				dialog.show();
				break;
			case HIDE_DIALOG:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				break;
			case REQUEST_CHECK_OFFER_EXIST:
				String conversationData = (String) msg.obj;
				conversation = new Gson().fromJson(conversationData, Conversation.class);
				initLogChat();
				break;
			default:
				break;
			}
		}

		private void initLogChat() {
			ArrayList<MessageModel> historyChat=new ArrayList<MessageModel>();
			for (ReplyModel replyModel : conversation.getReplies()) {
				if (replyModel.getUser_id() == currentUser.getId()) {
					MessageModel messageModel = new MessageModel(replyModel, currentUser, false);
					historyChat.add(messageModel);
				} else {
					MessageModel messageModel = new MessageModel(replyModel, conversation.getUser(), true);
					historyChat.add(messageModel);
				}
			}
			Collections.reverse(historyChat); 
			mMessageAdapter.addListData(historyChat);
		};
	};
	
//	private class GetProductViaIdTask extends AsyncTask<String, Void, ProductModel> {
//		
//		@Override
//		protected ProductModel doInBackground(String... params) {
//			return new ProductWebservice().getProductViaID(mTokken, params[0]);
//		}
//		
//		@Override
//		protected void onPostExecute(ProductModel result) {
//			super.onPostExecute(result);
//			if (result != null) {
//				mProductModel = result;
//				new GetConversationViaIdTask().execute(mConversationID);
//			}
//		}
//	}
	
	private class GetConversationViaIdTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String data = new ConversationWebService().getDetailConversation(mTokken, Integer.parseInt(mConversationID));
			Log.i("ChatToBuyActivity", data);
			conversation = new Gson().fromJson(data, Conversation.class);
			mProductModel=new ProductWebservice().getProductViaID(mTokken, "" + conversation.getProductId());
			return data;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				setData();
			}
		}
		
	}

}
