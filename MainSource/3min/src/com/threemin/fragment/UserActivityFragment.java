package com.threemin.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.threemin.adapter.ActivityAdapter;
import com.threemin.model.ActivityModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ActivityWebService;
import com.threemins.R;

public class UserActivityFragment extends Fragment {
    
    public static final String tag = "UserActivityFragment";
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int INIT_DATA = 3;
	
	public final int STEP_INIT = 1;
    public final int STEP_REFRESH = 2;
    public final int STEP_LOADMORE = 3;

	ProgressDialog dialog;
	
	View rootView;
	ListView listview;
	List<ActivityModel> data;
	ActivityAdapter adapter;
	private SwipeRefreshLayout mSwipe;
	private int mPage;
    private int mTheLastTotalCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_activity, null);
		listview = (ListView)rootView.findViewById(R.id.fragment_activity_lv);
		mSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_activity_swipe);
		listview.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
                if (loadMore && totalItemCount > 1 && mTheLastTotalCount != totalItemCount) {
                    mTheLastTotalCount = totalItemCount;
                    new GetFollowListTask().execute(STEP_LOADMORE);
                }
            }
        });
		
		int color1 = R.color.red_background;
        int color2 = R.color.common_grey;
        mSwipe.setColorScheme(color1, color2, color1, color2);
        mSwipe.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetFollowListTask().execute(STEP_REFRESH);
            }
        });
        
        new GetFollowListTask().execute(STEP_INIT);
		
		return rootView;
	}
	
	//call webservice to get list followers or followings
    private class GetFollowListTask extends AsyncTask<Integer, Void, List<ActivityModel>> {
        
        int currentStep;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipe != null) {
                mSwipe.setRefreshing(true);
            }
        }
        
        @Override
        protected List<ActivityModel> doInBackground(Integer... params) {
            
            currentStep = params[0];
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            
            if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                mPage = 1;
                mTheLastTotalCount = 0;
            } else if(currentStep == STEP_LOADMORE) {
                mPage++;
            }
            
            return new ActivityWebService().getActivities(token, mPage);
        }
        
        @Override
        protected void onPostExecute(List<ActivityModel> result) {
            super.onPostExecute(result);
            if (mSwipe != null) {
                mSwipe.setRefreshing(false);
            }
            
            if (result != null && result.size() > 0) {
                if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                    data = result;
                    adapter = new ActivityAdapter(getActivity(), result);
                    listview.setAdapter(adapter);
                } else if (currentStep == STEP_LOADMORE) { 
                    data.addAll(result);
                    adapter.setListUsers(data);
                }
            } else if (currentStep == STEP_INIT) {
                if (adapter == null) {
                    adapter = new ActivityAdapter(getActivity(), result);
                } else {
                    adapter.setListUsers(result);
                }
            }
            Log.i(tag, "GetFollowListTask result: " + new Gson().toJson(result));
            

        }
    }
	
//	@Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_activity, null);
//      initBody(rootView);?
//        return rootView;
//    }

//	private void initBody(View convertView) {
//		initData();
//		listview = (ListView) convertView.findViewById(R.id.fragment_activity_lv);
//	}
//	
//	private void initData() {
//		mHandler.sendEmptyMessage(SHOW_DIALOG);
//		Thread t = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
//				data = new ActivityWebService().getActivities(tokken);
//				Message msg = new Message();
//				msg.what = INIT_DATA;
//				mHandler.sendEmptyMessage(HIDE_DIALOG);
//				mHandler.sendMessage(msg);
//			}
//		});
//		t.start();
//	}
//
//	Handler mHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case SHOW_DIALOG:
//				dialog = new ProgressDialog(getActivity());
//				dialog.setMessage(getString(R.string.please_wait));
//				dialog.show();
//				break;
//			case HIDE_DIALOG:
//				if (dialog != null && dialog.isShowing()) {
//					dialog.dismiss();
//				}
//				break;
//			
//			case INIT_DATA:
//				if (data != null ) {
//					adapter = new ActivityAdapter(getActivity(), data);
//					listview.setAdapter(adapter);
//				}
//				break;
//			default:
//				break;
//			}
//		};
//	};
}
