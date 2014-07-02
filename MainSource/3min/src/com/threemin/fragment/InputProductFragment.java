package com.threemin.fragment;

import com.google.gson.Gson;
import com.threemin.app.LocationActivity;
import com.threemin.uti.CommonConstant;
import com.threemins.R;

import br.com.condesales.models.Venue;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class InputProductFragment extends Fragment {
    private final static int REQUEST_LOCATION = 31;

    View rootView;
    String productName;
    String productDescription;
    Venue loction;

    EditText etName, etDescription;
    TextView tvLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_input_item, container, false);
        initView();
        initData();
        return rootView;
    }

    private void initData() {
        productName = getArguments().getString(CommonConstant.INTENT_PRODUCT_NAME);
        productDescription = getArguments().getString(CommonConstant.INTENT_PRODUCT_DESCRIPTION);
        loction = new Gson().fromJson(getArguments().getString(CommonConstant.INTENT_PRODUCT_DATA), Venue.class);

        etName.setText(productName);
        etDescription.setText(productDescription);
        if (loction != null) {
            tvLocation.setText(loction.getName());
        }

        tvLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), LocationActivity.class), REQUEST_LOCATION);
            }
        });
    }

    private void initView() {
        etName = (EditText) rootView.findViewById(R.id.et_name);
        etDescription = (EditText) rootView.findViewById(R.id.et_description);
        tvLocation = (TextView) rootView.findViewById(R.id.tv_location);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA);
            loction = new Gson().fromJson(result, Venue.class);
            if (loction != null) {
                tvLocation.setText(loction.getName());
            }
        }
    }
    
    public Bundle returnResult(){
        Bundle bundle=new Bundle();
        bundle.putString(CommonConstant.INTENT_PRODUCT_NAME, etName.getText().toString());
        bundle.putString(CommonConstant.INTENT_PRODUCT_DESCRIPTION, etDescription.getText().toString());
        String dataLocation= new Gson().toJson(loction);
        bundle.putString(CommonConstant.INTENT_PRODUCT_DATA, dataLocation);
        return bundle;
    }
}
