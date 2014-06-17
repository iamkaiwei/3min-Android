package com.threemin.fragment;

import java.util.List;

import com.google.gson.Gson;
import com.threemin.adapter.ListOfferAdapter;
import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.PostOfferActivity;
import com.threemin.model.Conversation;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ConversationWebService;
import com.threemin.webservice.ProductWebservice;
import com.threemins.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListOfferFragment extends Fragment {
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int REQUEST_CHECK_OFFER_EXIST = 3;

	ListView listView;
	ProgressDialog dialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_list, null);
		listView = (ListView) v.findViewById(R.id.list);
		initData();
		initListener();
		return v;
	}

	private void initListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Conversation conversation = (Conversation) listView.getItemAtPosition(position);
//				String conversationData = new Gson().toJson(conversation);
//				Intent intent = new Intent(getActivity(), ChatToBuyActivity.class);
//				intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA,
//						getActivity().getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA));
//				intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversationData);
//				getActivity().startActivity(intent);
				checkListOffer(conversation.getId());
			}
		});
	}

	private void initData() {
		String conversationData = getActivity().getIntent().getStringExtra(CommonConstant.INTENT_CONVERSATION_DATA);
		List<Conversation> data = new ProductWebservice().parseListConversation(conversationData);
		ListOfferAdapter adapter = new ListOfferAdapter(data);
		listView.setAdapter(adapter);
	};

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
				String conversation = (String) msg.obj;
				Intent intent = new Intent(getActivity(), ChatToBuyActivity.class);
				intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA,
						getActivity().getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA));
				intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversation);
				getActivity().startActivity(intent);
				break;
			default:
				break;
			}
		};
	};

	private void checkListOffer(final int conversationId) {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
				String conversationData = new ConversationWebService().getDetailConversation(tokken, conversationId);
				Message msg = new Message();
				msg.what = REQUEST_CHECK_OFFER_EXIST;
				msg.obj = conversationData;
				mHandler.sendEmptyMessage(HIDE_DIALOG);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}
}
