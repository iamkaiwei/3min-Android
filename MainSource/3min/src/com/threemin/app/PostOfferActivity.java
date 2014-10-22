package com.threemin.app;

import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.threemin.fragment.PostOfferFragment;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class PostOfferActivity extends ThreeMinsBaseActivity {
    public static final String TAG_POST_OFFER_FRAGMENT = "PostOfferFragment";
	PostOfferFragment postOfferFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		
		initActionBar();
		
		//TODO: old implementation to save fragment==========================================
//		if (savedInstanceState == null) {
//			postOfferFragment = new PostOfferFragment();
//			getSupportFragmentManager().beginTransaction().add(R.id.container, postOfferFragment).commit();
//		} else {
//            postOfferFragment = (PostOfferFragment) getSupportFragmentManager().getFragment(savedInstanceState, TAG_POST_OFFER_FRAGMENT);
//        }
		//old implementation to save fragment==========================================
		
		postOfferFragment = (PostOfferFragment) getSupportFragmentManager().findFragmentByTag(TAG_POST_OFFER_FRAGMENT);
		if (postOfferFragment == null) {
            postOfferFragment = new PostOfferFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, postOfferFragment, TAG_POST_OFFER_FRAGMENT).commit();
        }
	}

	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setIcon(R.drawable.btn_cancel);
		bar.setDisplayShowTitleEnabled(true);
		
		((ImageView)findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);
		
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            return true;
        case R.id.action_submit:
            postOfferFragment.postOffer();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post_product, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    //old implementation to save fragment
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        // TODO Auto-generated method stub
//        super.onSaveInstanceState(outState);
//        getSupportFragmentManager().putFragment(outState, TAG_POST_OFFER_FRAGMENT, postOfferFragment);
//    }
}
