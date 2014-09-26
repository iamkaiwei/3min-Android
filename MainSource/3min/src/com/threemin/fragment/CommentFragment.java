package com.threemin.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.adapter.CommentAdapter;
import com.threemin.model.CommentModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.CommentWebService;
import com.threemins.R;

public class CommentFragment extends Fragment {
    
    public static final String tag = "CommentFragment";
    
    public final int STEP_INIT = 1;
    public final int STEP_REFRESH = 2;
    public final int STEP_LOADMORE = 3;

    private int mProductID;
    private View mRootView;
    private RelativeLayout mRlCommentInput;
    private EditText mEtCommentInput;
    private Button mBtnSend;
    private ListView mLvComment;
    private List<CommentModel> mListComment;
    private CommentAdapter mAdapter;
    private int mPage;
    private SwipeRefreshLayout mSwipe;
    private int mTheLastTotalCount;
    private boolean mCommentAction;
    private String mInitData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(tag, "onCreateView");
        mRootView = inflater.inflate(R.layout.fragment_comment, null);
        mProductID = getActivity().getIntent().getIntExtra(DetailFragment.INTENT_PRODUCT_ID_FOR_COMMENT, -1);
        mCommentAction = getActivity().getIntent().getBooleanExtra(DetailFragment.INTENT_COMMENT_ACTION, DetailFragment.ACTION_VIEW_COMMENTS);
        mInitData = getActivity().getIntent().getStringExtra(DetailFragment.INTENT_JSON_INIT_DATA);
        mPage = 1;

        initWidgets();
        initListener();
        
//        if (mProductID != -1) {
//            new GetCommentsTask().execute(STEP_INIT);
//        }
        initData();
        return mRootView;
    }
    
    public void initWidgets() {
        mRlCommentInput = (RelativeLayout) mRootView.findViewById(R.id.fm_comment_rl_comment_input);
        mEtCommentInput = (EditText) mRootView.findViewById(R.id.fm_comment_et_comment_input);
        mBtnSend = (Button) mRootView.findViewById(R.id.fm_comment_btn_send);
        mLvComment = (ListView) mRootView.findViewById(R.id.fm_comment_lv);
        mSwipe = (SwipeRefreshLayout) mRootView.findViewById(R.id.fragment_comment_swipe);
        
        if (mCommentAction == DetailFragment.ACTION_VIEW_COMMENTS) {
            mRlCommentInput.setVisibility(View.GONE);
        } else {
            mRlCommentInput.setVisibility(View.VISIBLE);
        }
        
//        createTestData();
    }
    
    public void initListener() {
        mBtnSend.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doPostComment();
            }
        });
        
        int color1 = R.color.red_background;
        int color2 = R.color.common_grey;
        mSwipe.setColorScheme(color1, color2, color1, color2);
        mSwipe.setOnRefreshListener(new OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                new GetCommentsTask().execute(STEP_REFRESH);
            }
        });
        
        mLvComment.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
                if (loadMore && totalItemCount > 1 && mTheLastTotalCount != totalItemCount) {
                    mTheLastTotalCount = totalItemCount;
                    new GetCommentsTask().execute(STEP_LOADMORE);
                }
            }
        });
    }
    
    public void initData() {
        Type listType = new TypeToken<List<CommentModel>>() {
        }.getType();
        mListComment = new Gson().fromJson(mInitData, listType);
        mAdapter = new CommentAdapter(getActivity(), mListComment);
        mLvComment.setAdapter(mAdapter);
        mPage = 1;
    }
    
    public void doPostComment() {
        String cmt = mEtCommentInput.getText().toString();
        if (cmt != null && cmt.length() > 0) {
            new PostCommentTask().execute(cmt);
            mEtCommentInput.setText("");
            CommonUti.hideKeyboard(mEtCommentInput, getActivity());
        }
    }
    
    //call web service to get list comment
    private class GetCommentsTask extends AsyncTask<Integer, Void, List<CommentModel>> {
        
        int currentStep;
        
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (mSwipe != null) {
                mSwipe.setRefreshing(true);
            }
        }

        @Override
        protected List<CommentModel> doInBackground(Integer... params) {
            currentStep = params[0];
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            
            if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                mPage = 1;
                mTheLastTotalCount = 0;
            } else if(currentStep == STEP_LOADMORE) {
                mPage++;
            }
            
            return new CommentWebService().getComments(token, mProductID, mPage);
        }
        
        @Override
        protected void onPostExecute(List<CommentModel> result) {
            super.onPostExecute(result);
            if (mSwipe != null) {
                mSwipe.setRefreshing(false);
            }
            
            if (result != null && result.size() > 0) {
                if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                    mListComment = result;
                    mAdapter = new CommentAdapter(getActivity(), result);
                    mLvComment.setAdapter(mAdapter);
                } else if (currentStep == STEP_LOADMORE) { 
                    mListComment.addAll(result);
                    mAdapter.setListComments(mListComment);
                }
            } else if (currentStep == STEP_INIT) {
                if (mAdapter == null) {
                    mAdapter = new CommentAdapter(getActivity(), result);
                } else {
                    mAdapter.setListComments(result);
                }
            }
        }
        
    }
    
    private class PostCommentTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            int responseCode = new CommentWebService().postComments(token, mProductID, params[0]);
            return responseCode;
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == WebserviceConstant.RESPONSE_CODE_EXCEPTION) {
                Log.i(tag, "PostCommentTask: exception");
            } else if (result == WebserviceConstant.RESPONSE_CODE_SUCCESS) {
                Log.i(tag, "PostCommentTask: success");
                new GetCommentsTask().execute(STEP_INIT);
            } else {
                Log.i(tag, "PostCommentTask: response code: " + result);
            }
        }
        
    }
    
//    public void createTestData() {
//        String avatarUrl = "https://graph.facebook.com/896011980424511/picture?type=large";
//        String comment = "Comment: ";
//        String username = "User: ";
//        mListComment = new ArrayList<CommentModel>();
//        for (int i = 0; i < 20; i++) {
//            CommentModel m = new CommentModel();
//            m.setContent(comment + i);
//            m.setCreated_at(System.currentTimeMillis() / 1000);
//            m.setUpdated_at(System.currentTimeMillis() / 1000);
//            m.setId(i);
//            UserModel user = new UserModel();
//            user.setFullName(username + i);
//            user.setFacebook_avatar(avatarUrl);
//            m.setUser(user);
//            mListComment.add(m);
//        }
//        
//        mAdapter = new CommentAdapter(getActivity(), mListComment);
//        mLvComment.setAdapter(mAdapter);
//    }

}
