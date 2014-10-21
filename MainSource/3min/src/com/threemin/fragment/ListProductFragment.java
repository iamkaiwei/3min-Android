package com.threemin.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.adapter.ProductGridAdapter;
import com.threemin.app.DetailActivity;
import com.threemin.app.HomeActivity;
import com.threemin.app.ProfileActivity;
import com.threemin.app.UserLikeProductActivity;
import com.threemin.model.FeedbackModel;
import com.threemin.model.ProductModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.view.QuickReturnGridView;
import com.threemin.view.QuickReturnGridView.OnItemDoubleTapLister;
import com.threemin.webservice.FeedbackWebservice;
import com.threemin.webservice.RelationshipWebService;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class ListProductFragment extends BaseProductFragment {

    public static final String tag = "ListProductFragment";
    private Context mContext;
    private LoginButton mLoginButton;

    public static int MODE_MY_PRODUCT = 1;
    public static int MODE_USER_LIKED_PRODUCT = 2;
    public static int MODE_USER_PRODUCT = 3;
    public static int MODE_FOLLOWED_PRODUCT = 4;
    public static int STEP_INIT = 0;
    public static int STEP_ADDMORE = 1;
    public static int STEP_REFRESH = 2;

    private QuickReturnGridView mGrid;
    private ProductGridAdapter mAdapter;

//    private boolean isRequestLike;
    private int page;
    private int mode;
    private UserModel userModel;
    
    public ListProductFragment() {
        super();
    }

//    public ListProductFragment(Context context, LoginButton btn) {
//        super();
//        this.mContext = context;
//        this.mLoginButton = btn;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_gridview, null);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_gridview);
        int color = R.color.red_background;
        int color2 = R.color.common_grey;
        swipeLayout.setColorScheme(color, color2, color, color2);
        mGrid = (QuickReturnGridView) v.findViewById(R.id.gv_product);
        
        mContext = getActivity();
        if (mContext instanceof HomeActivity) {
            mLoginButton = ((HomeActivity)mContext).getLoginButton();
        } else if (mContext instanceof UserLikeProductActivity) {
            mLoginButton = ((UserLikeProductActivity)mContext).getLoginButton();
        } else if (mContext instanceof ProfileActivity) {
            mLoginButton = ((ProfileActivity)mContext).getLoginButton();
        }

        new GetFirstFeedbackTask().execute();
        return v;
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    private void initListner() {
        swipeLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                new GetProductTaks(ListProductFragment.this).execute(HomeFragment.STEP_REFRESH);
            }
        });

        mGrid.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 1;
                if (loadMore && totalItemCount > 1 && thelasttotalCount != totalItemCount) {
                    thelasttotalCount = totalItemCount;
                    new GetProductTaks(ListProductFragment.this).execute(HomeFragment.STEP_ADDMORE);
                }

            }
        });

        mGrid.setOnItemDoubleClickListener(new OnItemDoubleTapLister() {

            @Override
            public void OnSingleTap(AdapterView parent, View view, int position, long id) {
                ProductModel model = (ProductModel) mGrid.getItemAtPosition(position);
                if (model != null) {
                    if (model.getOwner() == null) {
                        if (mode == MODE_MY_PRODUCT) {
                            model.setOwner(PreferenceHelper.getInstance(getActivity()).getCurrentUser());
                        } else {
                            model.setOwner(userModel);
                        }
                    }
                    String data = new Gson().toJson(model);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
                    getActivity().startActivity(intent);
                    CommonUti.addAnimationWhenStartActivity(getActivity());
                }
            }

            @Override
            public void OnDoubleTap(AdapterView parent, View view, int position, long id) {
                ProductModel model = (ProductModel) mGrid.getItemAtPosition(position);
                if (model != null) {

                    // requestLike(model,view);
                }
            }
        });
    }

    @Override
    public void updateUI() {
        if (mAdapter == null) {
            // TODO
            mAdapter = new ProductGridAdapter(productModels, mContext, mLoginButton,CommonUti.calcWidthItem(mContext));
        }
        mAdapter.updateData(productModels);
    }

    @Override
    public void changeIfNoItem() {

    }

    public class GetProductTaks extends AsyncTask<Integer, Void, List<ProductModel>> {
        int currentStep;

        BaseProductFragment baseProductFragment;

        // constructor
        public GetProductTaks(BaseProductFragment baseProductFragment) {
            this.baseProductFragment = baseProductFragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (baseProductFragment.swipeLayout != null) {
                baseProductFragment.swipeLayout.setRefreshing(true);
            }
        }

        @Override
        protected List<ProductModel> doInBackground(Integer... params) {
            currentStep = params[0];
            if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                page = 1;
                baseProductFragment.thelasttotalCount = 0;
            }
            if (currentStep == STEP_ADDMORE) {
                page++;
            }
            String tokken = PreferenceHelper.getInstance(baseProductFragment.getActivity()).getTokken();
            try {
                if (mode == MODE_MY_PRODUCT) {
                    return new UserWebService().getMyProduct(tokken, page);
                } else if (mode == MODE_USER_LIKED_PRODUCT) {
                    return new UserWebService().getUserLikedProduct(tokken, page);
                } else if (mode == MODE_USER_PRODUCT) {
                    return new UserWebService().getUserProduct(tokken, userModel.getId(), page);
                } else if (mode == MODE_FOLLOWED_PRODUCT) {
                	return new RelationshipWebService().getProductsFollowed(tokken, page);
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ProductModel> result) {
            super.onPostExecute(result);
            if (baseProductFragment.swipeLayout != null) {
                baseProductFragment.swipeLayout.setRefreshing(false);
            }
            if (result != null && result.size() > 0) {
                if (currentStep == STEP_INIT || currentStep == STEP_REFRESH) {
                    productModels = result;
                    baseProductFragment.setProductModels(result);
                } else if (currentStep == STEP_ADDMORE) {
                    productModels.addAll(result);
                    baseProductFragment.setProductModels(productModels);
                } else {
                }

            } else if (currentStep == STEP_INIT) {
                baseProductFragment.setProductModels(result);
                productModels = result;
            }

        }
    }
    
    //feedback feature==================================================================================================
    private View mHeader;
    
    public class GetFirstFeedbackTask extends AsyncTask<Void, Void, List<FeedbackModel>> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        
        @Override
        protected List<FeedbackModel> doInBackground(Void... params) {
            if (mode == MODE_USER_PRODUCT) {
                String token = PreferenceHelper.getInstance(getActivity()).getTokken();
                int userID = userModel.getId();
                List<FeedbackModel> list = new FeedbackWebservice().getFirstFeedbackOfUser(token, userID, 1);
                Log.i(tag, "List feedback size: " + (list == null ? -1 : list.size()));
                return list;
            } else {
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(List<FeedbackModel> result) {
            super.onPostExecute(result);
            
            if (result != null && result.size() != 0) {
                createHeader(result.get(0));
            }
            
            if (mAdapter == null) {
                mAdapter = new ProductGridAdapter(productModels, mContext, mLoginButton,CommonUti.calcWidthItem(mContext));
            }
            
            mGrid.setAdapter(mAdapter);

            initListner();
            new GetProductTaks(ListProductFragment.this).execute(HomeFragment.STEP_INIT);
        }
    }
    
    public void createHeader(FeedbackModel m) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            mHeader = layoutInflater.inflate(R.layout.fragment_right_feedback_group, null);
            mGrid.addHeaderView(mHeader);
            TextView tvName = (TextView) mHeader.findViewById(R.id.fm_right_feedback_group_tv_username);
            TextView tvTime = (TextView) mHeader.findViewById(R.id.fm_right_feedback_group_tv_date);
            TextView tvContent = (TextView) mHeader.findViewById(R.id.fm_right_feedback_group_tv_content);
            ImageView imgAvatar = (ImageView) mHeader.findViewById(R.id.fm_right_feedback_group_img_avatar);
            Button btnViewFeedback = (Button) mHeader.findViewById(R.id.fm_right_feedback_group_btn_view_feedback);
            
            tvName.setText(m.getUser().getFullName());
            tvTime.setText(
                    DateUtils.getRelativeTimeSpanString(
                            m.getUpdate_time() * 1000, 
                            System.currentTimeMillis(),
                            0L, 
                            DateUtils.FORMAT_ABBREV_RELATIVE)
                    );
            
            tvContent.setText(m.getContent());
            UrlImageViewHelper.setUrlDrawable(imgAvatar, m.getUser().getFacebook_avatar(), R.drawable.stuff_img);
            btnViewFeedback.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    //TODO: show feedback dialog
                }
            });
    }
    
}
