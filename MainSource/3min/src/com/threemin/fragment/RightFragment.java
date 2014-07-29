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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.FollowerFollowingActivity;
import com.threemin.app.HomeActivity;
import com.threemin.app.ListMessageActivity;
import com.threemin.app.ProfileActivity;
import com.threemin.app.SettingActivity;
import com.threemin.app.UserActivityActivity;
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
            
            rootView.findViewById(R.id.inf_avt_setting).setVisibility(View.INVISIBLE);
            UserModel currentUser = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
            if (currentUser != null && userModel != null && currentUser.getId() != userModel.getId()) {
            	rootView.findViewById(R.id.btn_msg).setVisibility(View.GONE);
            	rootView.findViewById(R.id.btn_msg_divider).setVisibility(View.GONE);
            	rootView.findViewById(R.id.btn_liked).setVisibility(View.GONE);
            	rootView.findViewById(R.id.img_follow).setVisibility(View.VISIBLE);
			}
        } else {
            userModel = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
            Log.i("RightFragment", userModel.getFirstName());
        }
        ImageView img = (ImageView) rootView.findViewById(R.id.nav_avatar);
        UrlImageViewHelper.setUrlDrawable(img, userModel.getFacebook_avatar());

        TextView tv_name = (TextView) rootView.findViewById(R.id.navigation_name);
        tv_name.setText(userModel.getFullName());
        
        Log.i("RightFragment: ", userModel.getFirstName() + "isFollowed: " + (userModel.isFollowed()?"true":"false"));
        
        new GetFollowInfoTask(this).execute(userModel.getId());
//        userProduct(userModel);
//        initListener(rootView);
        return rootView;
    }
    
  //get the number of followers and followings, and the the state that this user is followed or not
    private static class GetFollowInfoTask extends AsyncTask<Integer, Void, UserModel> {
    	RightFragment rightFragment;
    	public GetFollowInfoTask(RightFragment rightFragment){
    		this.rightFragment=rightFragment;
    	}
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        
        @Override
        protected UserModel doInBackground(Integer... params) {
            String token = PreferenceHelper.getInstance(rightFragment.getActivity()).getTokken();
            UserModel model = new UserWebService().getUserViaId(token, "" + rightFragment.userModel.getId());
            return model;
        }
        
        @Override
        protected void onPostExecute(UserModel result) {
            if (result != null) {
                TextView tv_followers_number = (TextView) rightFragment.rootView.findViewById(R.id.btn_follower);
                tv_followers_number.setText("" + result.getCountFollowers());
                
                TextView tv_followings_number = (TextView) rightFragment.rootView.findViewById(R.id.btn_following);
                tv_followings_number.setText("" + result.getCountFollowing());
                
                ImageView img_follow = (ImageView) rightFragment.rootView.findViewById(R.id.img_follow);
                img_follow.setSelected(result.isFollowed());
                
                Log.i("RightFragment: ", result.getFirstName() + "isFollowed: " + (result.isFollowed()?"true":"false"));
                
                rightFragment.userProduct(result);
                rightFragment.userModel = result;
                rightFragment.initListener(rightFragment.rootView);
            }
        }
    }

    public void initListener(View rootView) {
        rootView.findViewById(R.id.btn_liked).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserLikeProductActivity.class));
            }
        });
        rootView.findViewById(R.id.btn_msg).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ListMessageActivity.class));
            }
        });
        rootView.findViewById(R.id.nav_avatar).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserActivityActivity.class));
            }
        });
        rootView.findViewById(R.id.inf_avt_setting).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
        ImageView img_follow = (ImageView) rootView.findViewById(R.id.img_follow);
        img_follow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doFollowFunction(v);
			}
		});
        rootView.findViewById(R.id.btn_follower).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                new GetFollowListTask(GET_FOLLOWERS).execute(1);
                Intent intent = new Intent(getActivity(), FollowerFollowingActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, userModel.getId());
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, GET_FOLLOWERS);
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_COUNT, userModel.getCountFollowers());
                startActivity(intent);
            }
        });
        rootView.findViewById(R.id.btn_following).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                new GetFollowListTask(GET_FOLLOWINGS).execute(1);
                Intent intent = new Intent(getActivity(), FollowerFollowingActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, userModel.getId());
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_LIST, GET_FOLLOWINGS);
                intent.putExtra(CommonConstant.INTENT_GET_FOLLOW_COUNT, userModel.getCountFollowing());
                startActivity(intent);
            }
        });
    }

    //init list product of user
    private void userProduct(UserModel userModel) {
        ListProductFragment productFragmentGrid = null;
        if (mode == ListProductFragment.MODE_USER_PRODUCT) {
//            productFragmentGrid = new ListProductFragment(getActivity(),
//                    ((ProfileActivity) getActivity()).getLoginButton());
            productFragmentGrid = new ListProductFragment();
            productFragmentGrid.setMode(ListProductFragment.MODE_USER_PRODUCT);
            productFragmentGrid.setUserModel(userModel);
        } else {
//            productFragmentGrid = new ListProductFragment(getActivity(),
//                    ((HomeActivity) getActivity()).getLoginButton());
            productFragmentGrid = new ListProductFragment();
            productFragmentGrid.setMode(ListProductFragment.MODE_MY_PRODUCT);
        }
        getFragmentManager().beginTransaction().replace(R.id.content_list, productFragmentGrid).commit();
    }
    
    //follow or unfollow a user
    public void doFollowFunction(View v) {
    	if (v.isSelected()) {
			v.setSelected(false);
			new FollowUserTask(UNFOLLOW,this).execute(userModel.getId());
		} else {
			v.setSelected(true);
			new FollowUserTask(FOLLOW,this).execute(userModel.getId());
		}
    }
    
    //call web service to follow or unfollow a user
    private static class FollowUserTask extends AsyncTask<Integer, Void, String> {

    	boolean followMode;
    	ProgressDialog dialog;
    	RightFragment rightFragment;
    	
    	public FollowUserTask(boolean followMode,RightFragment rightFragment) {
    		this.followMode = followMode;
    		this.rightFragment=rightFragment;
		}
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		dialog = new ProgressDialog(rightFragment.getActivity());
    		dialog.setMessage(rightFragment.getActivity().getString(R.string.please_wait));
    		dialog.show();
    	}
    	
		@Override
		protected String doInBackground(Integer... params) {
			String tokken = PreferenceHelper.getInstance(rightFragment.getActivity()).getTokken();
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

}