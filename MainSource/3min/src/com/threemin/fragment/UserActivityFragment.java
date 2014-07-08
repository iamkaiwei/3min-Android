package com.threemin.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.threemin.adapter.ActivityAdapter;
import com.threemin.model.ActivityModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ActivityWebService;
import com.threemins.R;

public class UserActivityFragment extends Fragment {
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int INIT_DATA = 3;

	ProgressDialog dialog;
	
	View rootView;
	ListView listview;
	List<ActivityModel> data;
	ActivityAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_activity, null);
		initBody(rootView);
		return rootView;
	}

	private void initBody(View convertView) {
		initData();
		listview = (ListView) convertView.findViewById(R.id.fragment_activity_lv);
	}
	
	private void initData() {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
				data = new ActivityWebService().getActivities(tokken);
				Message msg = new Message();
				msg.what = INIT_DATA;
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
				dialog = new ProgressDialog(getActivity());
				dialog.setMessage(getString(R.string.please_wait));
				dialog.show();
				break;
			case HIDE_DIALOG:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				break;
			
			case INIT_DATA:
				if (data != null ) {
					adapter = new ActivityAdapter(getActivity(), data);
					listview.setAdapter(adapter);
				}
				break;
			default:
				break;
			}
		};
	};
}
