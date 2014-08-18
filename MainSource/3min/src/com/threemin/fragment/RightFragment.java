package com.threemin.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.FollowerFollowingActivity;
import com.threemin.app.SettingActivity;
import com.threemin.app.UserLikeProductActivity;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.RelationshipWebService;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class RightFragment extends Fragment {
    int mode;
    UserModel userModel;
    public static boolean FOLLOW = true;
    public static boolean UNFOLLOW = false;
    public static boolean GET_FOLLOWERS = true;
    public static boolean GET_FOLLOWINGS = false;
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_right, container, false);
        if (this.getArguments() != null) {
            mode = this.getArguments().getInt(CommonConstant.INTENT_PRODUCT_MODE);
        }
        
        if (mode == ListProductFragment.MODE_USER_PRODUCT) {
            userModel = new Gson().fromJson(this.getArguments().getString(CommonConstant.INTENT_USER_DATA),
                    UserModel.class);
            TextView listing = (TextView) rootView.findViewById(R.id.tv_listing);
            listing.setText(R.string.profile_list);
            
            UserModel currentUser = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
            if (currentUser != null && userModel != null && currentUser.getId() != userModel.getId()) {
                rootView.findViewById(R.id.fm_right_group_control_divider).setVisibility(View.GONE);
                rootView.findViewById(R.id.fm_right_group_control).setVisibility(View.GONE);
			}
        } else {
            userModel = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
            Log.i("RightFragment", userModel.getFirstName());
        }
        ImageView img = (ImageView) rootView.findViewById(R.id.fm_right_avatar);
        UrlImageViewHelper.setUrlDrawable(img, userModel.getFacebook_avatar());

        TextView tv_name = (TextView) rootView.findViewById(R.id.fm_right_tv_username);
        tv_name.setText(userModel.getFullName());
        
        Log.i("RightFragment: ", userModel.getFirstName() + "isFollowed: " + (userModel.isFollowed()?"true":"false"));
        
        new GetFollowInfoTask().execute(userModel.getId());
//        userProduct(userModel);
//        initListener(rootView);
        return rootView;
    }
    
  //get the number of followers and followings, and the the state that this user is followed or not
    private class GetFollowInfoTask extends AsyncTask<Integer, Void, UserModel> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        
        @Override
        protected UserModel doInBackground(Integer... params) {
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            UserModel model = new UserWebService().getUserViaId(token, "" + userModel.getId());
            Log.i("RightFragment", model.getFullName() + " followers: " + model.getCountFollowers() + " following: " + model.getCountFollowing());
            return model;
        }
        
        @Override
        protected void onPostExecute(UserModel result) {
            if (result != null) {
                TextView tv_followers_number = (TextView) rootView.findViewById(R.id.fm_right_tv_follower_number);
                tv_followers_number.setText("" + result.getCountFollowers());

                TextView tv_followings_number = (TextView) rootView.findViewById(R.id.fm_right_tv_following_number);
                tv_followings_number.setText("" + result.getCountFollowing());

                Button btn_follow = (Button) rootView.findViewById(R.id.fm_right_btn_follow);
                btn_follow.setSelected(result.isFollowed());
                if (mode == ListProductFragment.MODE_USER_PRODUCT
                        && (PreferenceHelper.getInstance(getActivity()).getCurrentUser().getId() != userModel.getId())) {
                    btn_follow.setVisibility(View.VISIBLE);
                }
                if (result.isFollowed()) {
                    btn_follow.setText(getActivity().getString(R.string.unfollow));
                }

                Log.i("RightFragment: ", result.getFirstName() + "isFollowed: "
                        + (result.isFollowed() ? "true" : "false"));

                userProduct(result);
                userModel = result;
                initListener(rootView);
            }
        }
    }

    private void initListener(View rootView) {
        rootView.findViewById(R.id.fm_right_tv_my_likes).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserLikeProductActivity.class);
                intent.putExtra(CommonConstant.INTENT_PRODUCT_MODE, ListProductFragment.MODE_USER_LIKED_PRODUCT);
                startActivityWithAnimation(intent);
            }
        });
        rootView.findViewById(R.id.fm_right_tv_my_items).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserLikeProductActivity.class);
                intent.putExtra(CommonConstant.INTENT_PRODUCT_MODE, ListProductFragment.MODE_MY_PRODUCT);
                startActivityWithAnimation(intent);
            }
        });
        rootView.findViewById(R.id.fm_right_tv_edit).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityWithAnimation(new Intent(getActivity(), SettingActivity.class));
            }
        });
        Button btn_follow = (Button) rootView.findViewById(R.id.fm_right_btn_follow);
        btn_follow.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doFollowFunction(v);
            }
        });
        rootView.findViewById(R.id.fm_right_ln_follower).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowerFollowingActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, userModel.getId());
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, GET_FOLLOWERS);
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_COUNT, userModel.getCountFollowers());
                startActivityWithAnimation(intent);
            }
        });
        rootView.findViewById(R.id.fm_right_ln_following).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowerFollowingActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, userModel.getId());
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, GET_FOLLOWINGS);
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_COUNT, userModel.getCountFollowing());
                startActivityWithAnimation(intent);
            }
        });
    }

    //init list product of user
    private void userProduct(UserModel userModel) {
        ListProductFragment productFragmentGrid = null;
        if (mode == ListProductFragment.MODE_USER_PRODUCT) {
            productFragmentGrid = new ListProductFragment();
            productFragmentGrid.setMode(ListProductFragment.MODE_USER_PRODUCT);
            productFragmentGrid.setUserModel(userModel);
            getFragmentManager().beginTransaction().replace(R.id.content_list, productFragmentGrid).commit();
        } else {
            getFragmentManager().beginTransaction().add(R.id.content_list, new UserActivityFragment()).commit();
        }
    }
    
    //follow or unfollow a user
    public void doFollowFunction(View v) {
    	if (v.isSelected()) {
			v.setSelected(false);
			((Button)v).setText(getActivity().getString(R.string.follow));
			new FollowUserTask(UNFOLLOW).execute(userModel.getId());
		} else {
			v.setSelected(true);
            ((Button)v).setText(getActivity().getString(R.string.unfollow));
			new FollowUserTask(FOLLOW).execute(userModel.getId());
		}
    }
    
    //call web service to follow or unfollow a user
    private class FollowUserTask extends AsyncTask<Integer, Void, String> {

    	boolean followMode;
    	ProgressDialog dialog;
    	
    	public FollowUserTask(boolean followMode) {
    		this.followMode = followMode;
		}
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		dialog = new ProgressDialog(getActivity());
    		dialog.setMessage(getActivity().getString(R.string.please_wait));
    		dialog.show();
    	}
    	
		@Override
		protected String doInBackground(Integer... params) {
			String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
			String result;
			if (followMode == FOLLOW) {
				result = new RelationshipWebService().followUser(tokken, params[0]);
			} else {
				result = new RelationshipWebService().unfollowUser(tokken, params[0]);
			}
				 
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}
    }
    
    public void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_no_animation);
    }

}