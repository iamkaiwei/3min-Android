package com.threemin.app;

import com.threemins.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {

	ImageView img;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		img=(ImageView) findViewById(R.id.img);
		startActivityForResult(new Intent(this,ActivityCamera.class), 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			Uri uri=Uri.parse(data.getStringExtra("imageUri"));
			img.setImageURI(uri);
		}
	}
}
