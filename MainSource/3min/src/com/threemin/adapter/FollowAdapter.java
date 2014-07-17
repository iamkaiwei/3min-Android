package com.threemin.adapter;

import java.util.List;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.ProfileActivity;
import com.threemin.fragment.RightFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.RelationshipWebService;
import com.threemins.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FollowAdapter extends BaseAdapter {
    
    private List<UserModel> mData;
    Context mContext;
    
    public FollowAdapter(Context context, List<UserModel> data) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public UserModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.inflater_follow, parent, false);
        }
        final UserModel model = mData.get(position);
        
        LinearLayout lvUserInfo = (LinearLayout) convertView.findViewById(R.id.inflater_follow_ln_user_info);
        lvUserInfo.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(CommonConstant.INTENT_USER_DATA, new Gson().toJson(model));
                mContext.startActivity(intent);
            }
        });
        
        ImageView imgAvatar = (ImageView) convertView.findViewById(R.id.inflater_follow_avatar);
        UrlImageViewHelper.setUrlDrawable(imgAvatar, model.getFacebook_avatar());
        
        TextView tvName = (TextView) convertView.findViewById(R.id.inflater_follow_tv_name);
        tvName.setText(model.getFullName());
        
        ImageView imgFollow = (ImageView) convertView.findViewById(R.id.inflater_follow_img_follow);
        imgFollow.setSelected(model.isFollowed());
        imgFollow.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doFollowFunction(v, model.getId());
            }
        });
        return convertView;
    }
    
  //follow or unfollow a user
    public void doFollowFunction(View v, int userId) {
        if (v.isSelected()) {
            v.setSelected(false);
            new FollowUserTask(RightFragment.UNFOLLOW).execute(userId);
        } else {
            v.setSelected(true);
            new FollowUserTask(RightFragment.FOLLOW).execute(userId);
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
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(mContext.getString(R.string.please_wait));
            dialog.show();
        }
        
        @Override
        protected String doInBackground(Integer... params) {
            String tokken = PreferenceHelper.getInstance(mContext).getTokken();
            String result;
            if (followMode == RightFragment.FOLLOW) {
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
    
    public void setListUsers(List<UserModel> list) {
        mData = list;
        this.notifyDataSetChanged();
    }

}
