package com.threemin.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.threemin.fragment.InputProductFragment;
import com.threemins.R;

public class InputProductActivity extends FragmentActivity {
    InputProductFragment inputProductFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        initActionBar();
        if (savedInstanceState == null) {
             inputProductFragment = new InputProductFragment();
            inputProductFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, inputProductFragment).commit();
        }
    }
    
    public void initActionBar() {
        getActionBar().setIcon(R.drawable.btn_cancel);
        getActionBar().setHomeButtonEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        case R.id.action_submit:
            Bundle bundle=inputProductFragment.returnResult();
              Intent intent=new Intent();
              intent.putExtras(bundle);
              setResult(RESULT_OK, intent);
              finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post_product, menu);
         menu.getItem(0).setIcon(R.drawable.ic_done);
        return super.onCreateOptionsMenu(menu);
    }
}
