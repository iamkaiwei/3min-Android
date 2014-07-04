package com.threemin.app;

import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.threemin.fragment.ListProductFragment;
import com.threemin.fragment.RightFragment;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemins.R;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfileActivity extends FragmentActivity {
    LoginButton mLoginButton;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mLoginButton = (LoginButton) findViewById(R.id.activity_detail_btn_login_facebook);
        userModel=new Gson().fromJson(getIntent().getStringExtra(CommonConstant.INTENT_USER_DATA), UserModel.class);
        initActionBar();
        if (savedInstanceState == null) {
            RightFragment fragment=new RightFragment();
            Bundle bundle=new Bundle();
            bundle.putInt(CommonConstant.INTENT_PRODUCT_MODE, ListProductFragment.MODE_USER_PRODUCT);
            bundle.putString(CommonConstant.INTENT_USER_DATA, getIntent().getStringExtra(CommonConstant.INTENT_USER_DATA));
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }
    
    public void initActionBar() {
        getActionBar().setIcon(R.drawable.btn_back);
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
}
