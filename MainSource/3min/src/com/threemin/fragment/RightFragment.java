package com.threemin.fragment;

import android.content.Intent;
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
import com.threemin.app.HomeActivity;
import com.threemin.app.ListMessageActivity;
import com.threemin.app.ProfileActivity;
import com.threemin.app.SettingActivity;
import com.threemin.app.UserActivityActivity;
import com.threemin.app.UserLikeProductActivity;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemins.R;

public class RightFragment extends Fragment {
    int mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_right, container, false);
        if (this.getArguments() != null) {
            mode = this.getArguments().getInt(CommonConstant.INTENT_PRODUCT_MODE);
        }
        UserModel userModel;
        if (mode == ListProductFragment.MODE_USER_PRODUCT) {
            userModel = new Gson().fromJson(this.getArguments().getString(CommonConstant.INTENT_USER_DATA),
                    UserModel.class);
            TextView listing = (TextView) rootView.findViewById(R.id.tv_listing);
            listing.setText(R.string.profile_list);
        } else {
            userModel = PreferenceHelper.getInstance(getActivity()).getCurrentUser();
            Log.i("RightFragment", userModel.getFirstName());
        }
        ImageView img = (ImageView) rootView.findViewById(R.id.nav_avatar);
        UrlImageViewHelper.setUrlDrawable(img, userModel.getFacebook_avatar());

        TextView tv_name = (TextView) rootView.findViewById(R.id.navigation_name);
        tv_name.setText(userModel.getFullName());
        userProduct(userModel);
        initListener(rootView);
        return rootView;
    }

    private void initListener(View rootView) {
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

        if (mode == ListProductFragment.MODE_USER_PRODUCT) {
            rootView.findViewById(R.id.inf_avt_setting).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.ln_group_control).setVisibility(View.GONE);
        }
    }

    private void userProduct(UserModel userModel) {
        ListProductFragment productFragmentGrid = null;
        if (mode == ListProductFragment.MODE_USER_PRODUCT) {
            productFragmentGrid = new ListProductFragment(getActivity(),
                    ((ProfileActivity) getActivity()).getLoginButton());
            productFragmentGrid.setMode(ListProductFragment.MODE_USER_PRODUCT);
            productFragmentGrid.setUserModel(userModel);
        } else {
            productFragmentGrid = new ListProductFragment(getActivity(),
                    ((HomeActivity) getActivity()).getLoginButton());
            productFragmentGrid.setMode(ListProductFragment.MODE_MY_PRODUCT);
        }
        getFragmentManager().beginTransaction().replace(R.id.content_list, productFragmentGrid).commit();
    }

}