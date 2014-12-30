package com.threemin.fragment;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.internal.db;
import com.google.gson.Gson;
import com.threemin.adapter.ActivityAdapter;
import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.DetailActivity;
import com.threemin.app.HomeActivity;
import com.threemin.app.ProfileActivity;
import com.threemin.database.DatabaseAccessHelper;
import com.threemin.model.ActivityModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.ActivityWebService;
import com.threemins.R;

public class UserActivityFragment extends Fragment {
    
    public static final String tag = "UserActivityFragment";
	
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
                    new GetActivitiesListTask().execute(STEP_LOADMORE);
                }
            }
        });
		
		listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = getActivity();
                if (activity instanceof HomeActivity) {
                    ((HomeActivity)activity).clearNumberActivities();
                }
                ActivityModel model = data.get(position);
                int type = model.getCategory();
                
                if (type == CommonConstant.TYPE_CHAT || type == CommonConstant.TYPE_OFFER) {
                    startActivityForConversation(model);
                } else if (type == CommonConstant.TYPE_COMMENT || type == CommonConstant.TYPE_LIKE) {
                    startActivityForProduct(model);
                } else if (type == CommonConstant.TYPE_FOLLOW || type == CommonConstant.TYPE_FEEDBACK) {
                    startActivityForRelationship(model);
                }
                
            }
        });
		
		int color1 = R.color.red_background;
        int color2 = R.color.common_grey;
        mSwipe.setColorScheme(color1, color2, color1, color2);
        mSwipe.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetActivitiesListTask().execute(STEP_REFRESH);
            }
        });
        
        //get cache data
        getCachedData();
        
        new GetActivitiesListTask().execute(STEP_INIT);
		
		return rootView;
	}
	
	public void getCachedData() {
	    DatabaseAccessHelper dbHeler = new DatabaseAccessHelper(getActivity());
	    dbHeler.openDatabase();
	    List<ActivityModel> cached = dbHeler.getListActivities();
	    adapter = new ActivityAdapter(getActivity(), cached);
	    listview.setAdapter(adapter);
	    dbHeler.closeDatabase();
	}
	
	//call webservice to get list activities
    private class GetActivitiesListTask extends AsyncTask<Integer, Void, List<ActivityModel>> {
        
        int currentStep;
        DatabaseAccessHelper mDBHelper;
        
        public GetActivitiesListTask() {
            mDBHelper = new DatabaseAccessHelper(getActivity());
            mDBHelper.openDatabase();
        }
        
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
                    
                    mDBHelper.deleteAllActivities();
                } else if (currentStep == STEP_LOADMORE) { 
                    data.addAll(result);
                    adapter.setListActivities(data);
                }
                mDBHelper.insertListActivities(result);
            } else if (currentStep == STEP_INIT) {
                mDBHelper.deleteAllActivities();
                if (adapter == null) {
                    adapter = new ActivityAdapter(getActivity(), result);
                } else {
                    adapter.setListActivities(result);
                }
            }
            Log.i(tag, "GetActivitiesListTask result: " + new Gson().toJson(result));
            mDBHelper.closeDatabase();
        }
    }
    
    private void startActivityForProduct(ActivityModel model) {
        Log.i(tag, "startActivityForProduct");
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, "" + model.getSubjectID());
        startActivity(intent);
        CommonUti.addAnimationWhenStartActivity(getActivity());
    }

    private void startActivityForConversation(ActivityModel model) {
        Log.i(tag, "startActivityForConversation");
        Intent intent = new Intent(getActivity(), ChatToBuyActivity.class);
        intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA_VIA_ID, "" + model.getSubjectID());
        startActivity(intent);
        CommonUti.addAnimationWhenStartActivity(getActivity());
    }

    private void startActivityForRelationship(ActivityModel model) {
        Log.i(tag, "startActivityForRelationship");
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, "" + model.getUser().getId());
        startActivity(intent);
        CommonUti.addAnimationWhenStartActivity(getActivity());
    }
	
}
