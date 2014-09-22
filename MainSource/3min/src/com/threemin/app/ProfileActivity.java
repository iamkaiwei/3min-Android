package com.threemin.app;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.threemin.fragment.ListProductFragment;
import com.threemin.fragment.RightFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class ProfileActivity extends SwipeBackActivity {
	public final String tag = "ProfileActivity";
    LoginButton mLoginButton;
    UserModel userModel;
    SwipeBackLayout mSwipeBack;
    Bundle savedInstanceState;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        this.savedInstanceState = savedInstanceState;
        
        // Init the swipe back mechanism
        mSwipeBack = getSwipeBackLayout();
        mSwipeBack.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        
        mLoginButton = (LoginButton) findViewById(R.id.activity_detail_btn_login_facebook);
        
        String userID = getIntent().getStringExtra(CommonConstant.INTENT_USER_DATA_VIA_ID);
        Log.i(tag, "onCreate userID: " + userID);
        
      //check if intent is from push notification
        boolean isFromPushNotification = getIntent().getBooleanExtra(CommonConstant.INTENT_IS_FROM_PUSH_NOTIFICATION, false);
        if (isFromPushNotification) {
            int numActivities = PreferenceHelper.getInstance(this).getNumberActivities();
            if (numActivities > 0) {
                numActivities--;
            }
            PreferenceHelper.getInstance(this).setNumberActivities(numActivities);
        }
        
        if (userID != null && userID.length() != 0) {
			new GetUserViaId().execute(userID);
		} else {
			userModel=new Gson().fromJson(getIntent().getStringExtra(CommonConstant.INTENT_USER_DATA), UserModel.class);
			addContentFragment();
		}
    }
    
    public void addContentFragment() {
    	initActionBar();
    	if (savedInstanceState == null) {
            RightFragment fragment=new RightFragment();
            Bundle bundle=new Bundle();
            bundle.putInt(CommonConstant.INTENT_PRODUCT_MODE, ListProductFragment.MODE_USER_PRODUCT);
            bundle.putString(CommonConstant.INTENT_USER_DATA, new Gson().toJson(userModel));
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }
    
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    	scrollToFinishActivity();
    }
    
    public void initActionBar() {
        getActionBar().setIcon(R.drawable.ic_back);
        getActionBar().setHomeButtonEnabled(true);
        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);
        setTitle(userModel.getFullName());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    public LoginButton getLoginButton(){
        return mLoginButton;
    }
    
    private class GetUserViaId extends AsyncTask<String, Void, UserModel> {
    	@Override
    	protected UserModel doInBackground(String... params) {
    		String token = PreferenceHelper.getInstance(ProfileActivity.this).getTokken();
    		UserModel model = new UserWebService().getUserViaId(token, params[0]);
    		Log.i(tag, "GetUserViaId doInBackground model: " + model.toString());
    		return model;
    	}
    	
    	@Override
    	protected void onPostExecute(UserModel result) {
    		if (result != null) {
				userModel = result;
				addContentFragment();
			}
    	}
    }
}
