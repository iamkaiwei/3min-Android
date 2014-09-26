package com.threemin.app;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.ActionBar;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.threemin.fragment.CommentFragment;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class CommentActivity extends SwipeBackActivity{
    
    public final String tag = "CommentActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initActionBar();
        
        if (savedInstanceState == null) {
            Log.i(tag, "Create Fragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_comment_container, new CommentFragment()).commit();
        }
        
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        scrollToFinishActivity();
    }
    
    private void initActionBar() {
        ActionBar bar = getActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_back);
        bar.setDisplayShowTitleEnabled(true);
        ((ImageView) findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView txtTitle = (TextView) findViewById(id);
        txtTitle.setGravity(Gravity.CENTER);
        int screenWidth = CommonUti.getWidthInPixel(this);
        txtTitle.setWidth(screenWidth);

    }

}
