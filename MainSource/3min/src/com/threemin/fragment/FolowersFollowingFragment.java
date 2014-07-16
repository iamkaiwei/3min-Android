package com.threemin.fragment;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.threemin.adapter.FollowAdapter;
import com.threemin.app.ProfileActivity;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class FolowersFollowingFragment extends Fragment {
    
    public final String tag = "FolowersFollowingFragment";
    
    private View mRootView;
    private ListView mLvFollow;
    private List<UserModel> mData;
    private FollowAdapter mAdapter;
    private int mUserID;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Intent intent = getActivity().getIntent();
        mUserID = intent.getIntExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, 0);
        boolean isGetFollowers = intent.getBooleanExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, true);
        Log.i(tag, "onCreateView userID: " + mUserID + " getFollers: " + (isGetFollowers?"true":"false"));
        mRootView = inflater.inflate(R.layout.fragment_follower_following, null);
        mLvFollow = (ListView) mRootView.findViewById(R.id.fragment_follower_following_lv);
        
        if (mUserID != 0) {
            new GetFollowListTask(isGetFollowers).execute(1);
        }
        
        return mRootView;
    }
    
  //call webservice to get list followers or followings
    private class GetFollowListTask extends AsyncTask<Integer, Void, List<UserModel>> {
        
        private boolean isGetFollowers;
        
        public GetFollowListTask(boolean isGetFollowers) {
            this.isGetFollowers = isGetFollowers;
        }
        
        @Override
        protected List<UserModel> doInBackground(Integer... params) {
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            if (isGetFollowers) {
                return new UserWebService().getFollowers(token, mUserID, params[0]);
            } else {
                return new UserWebService().getFollowings(token, mUserID, params[0]);
            }
        }
        
        @Override
        protected void onPostExecute(List<UserModel> result) {
            super.onPostExecute(result);
            Log.i(tag, "GetFollowListTask result: " + new Gson().toJson(result));
            mData = result;
            mAdapter = new FollowAdapter(result);
            mLvFollow.setAdapter(mAdapter);
            mLvFollow.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    String data = new Gson().toJson(mData.get(position));
                    intent.putExtra(CommonConstant.INTENT_USER_DATA, data);
                    startActivity(intent);
                }
            });
        }
    }
    

}
