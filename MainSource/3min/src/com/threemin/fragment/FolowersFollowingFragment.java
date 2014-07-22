package com.threemin.fragment;

import java.util.List;

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

import com.google.gson.Gson;
import com.threemin.adapter.FollowAdapter;
import com.threemin.app.ProfileActivity;
import com.threemin.fragment.ListProductFragment.GetProductTaks;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class FolowersFollowingFragment extends Fragment {
    
    public final String tag = "FolowersFollowingFragment";
    public final int STEP_INIT = 1;
    public final int STEP_REFRESH = 2;
    public final int STEP_LOADMORE = 3;
    
    private View mRootView;
    private SwipeRefreshLayout mSwipe;
    private ListView mLvFollow;
    private List<UserModel> mData;
    private FollowAdapter mAdapter;
    private int mUserID;
    private int mCountFollow;
    private int mPage;
    private int mTheLastTotalCount;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Intent intent = getActivity().getIntent();
        mUserID = intent.getIntExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, 0);
        mCountFollow = intent.getIntExtra(CommonConstant.INTENT_GET_FOLLOW_COUNT, 0);
        final boolean isGetFollowers = intent.getBooleanExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, true);
        Log.i(tag, "onCreateView userID: " + mUserID + " getFollers: " + (isGetFollowers?"true":"false") + " countFollow: " + mCountFollow);
        
        mRootView = inflater.inflate(R.layout.fragment_follower_following, null);
        
        mLvFollow = (ListView) mRootView.findViewById(R.id.fragment_follower_following_lv);
        mLvFollow.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
                if (loadMore && totalItemCount > 1 && mTheLastTotalCount != totalItemCount && totalItemCount < mCountFollow) {
                    mTheLastTotalCount = totalItemCount;
                    new GetFollowListTask(isGetFollowers).execute(STEP_LOADMORE);
                }
            }
        });
        
        
        mSwipe = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_follower_following_swipe);
        int color1 = R.color.red_background;
        int color2 = R.color.common_grey;
        mSwipe.setColorScheme(color1, color2, color1, color2);
        mSwipe.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetFollowListTask(isGetFollowers).execute(STEP_REFRESH);
            }
        });
        
        if (mUserID != 0) {
            new GetFollowListTask(isGetFollowers).execute(STEP_INIT);
        }
        
        return mRootView;
    }
    
  //call webservice to get list followers or followings
    private class GetFollowListTask extends AsyncTask<Integer, Void, List<UserModel>> {
        
        int currentStep;
        
        private boolean isGetFollowers;
        
        public GetFollowListTask(boolean isGetFollowers) {
            this.isGetFollowers = isGetFollowers;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipe != null) {
                mSwipe.setRefreshing(true);
            }
        }
        
        @Override
        protected List<UserModel> doInBackground(Integer... params) {
            
            currentStep = params[0];
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            
            if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                mPage = 1;
                mTheLastTotalCount = 0;
            } else if(currentStep == STEP_LOADMORE) {
                mPage++;
            }
            
            if (isGetFollowers) {
                return new UserWebService().getFollowers(token, mUserID, mPage);
            } else {
                return new UserWebService().getFollowings(token, mUserID, mPage);
            }
        }
        
        @Override
        protected void onPostExecute(List<UserModel> result) {
            super.onPostExecute(result);
            if (mSwipe != null) {
                mSwipe.setRefreshing(false);
            }
            
            if (result != null && result.size() > 0) {
                if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                    mData = result;
                    mAdapter = new FollowAdapter(getActivity(), result);
                    mLvFollow.setAdapter(mAdapter);
                } else if (currentStep == STEP_LOADMORE) { 
                    mData.addAll(result);
                    mAdapter.setListUsers(mData);
                }
            } else if (currentStep == STEP_INIT) {
                if (mAdapter == null) {
                    mAdapter = new FollowAdapter(getActivity(), result);
                } else {
                    mAdapter.setListUsers(result);
                }
            }
            Log.i(tag, "GetFollowListTask result: " + new Gson().toJson(result));
            

        }
    }
    

}
