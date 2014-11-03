package com.threemin.app;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.uti.CommonUti;
import com.threemins.R;

public class CropImageActivity extends ThreeMinsBaseActivity {
	
	ImageView img;
	CropImageView cropImg;
	Bitmap croppedBitmap;
	String mPathImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_image);
		
		initWidgets();
		initData();
		initListener();
		initActionBar();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_act_crop_img, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		if (item.getItemId() == R.id.action_crop_done) {
			
			doCropDone();
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void initWidgets() {
		img = (ImageView) findViewById(R.id.act_crop_img);
		cropImg = (CropImageView) findViewById(R.id.act_crop_crop_img);
	}
	
	public void initData() {
		Intent intent = getIntent();
		Uri uri = Uri.parse(intent.getStringExtra("imageUri"));
		setImageURI(uri, img);
	}
	
	public void initListener() {

	}
	
	private void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(true);
		bar.setIcon(R.drawable.ic_back);
		bar.setDisplayShowTitleEnabled(true);
		
		((ImageView)findViewById(android.R.id.home)).setOnClickListener(new OnClickListener() {
			
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
        txtTitle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doCrop();
				v.setEnabled(false);
			}
		});
		
	}
	
	public void setImageURI(Uri uri,ImageView imageView) {
			UrlImageViewHelper.setUrlDrawable(imageView, uri.toString(), getImageCallback());
	}
	
	

	private UrlImageViewCallback getImageCallback() {
		return new UrlImageViewCallback() {

			@Override
			public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
				Log.d("url", url);
				Log.i("Bitmap", "LoadedBmp: W x H: " + loadedBitmap.getWidth() + " x " + loadedBitmap.getHeight());				
				
				if (url.contains("content://com.google.android.apps.photos.content")) {
					mPathImage = saveImagetoLocal(loadedBitmap);
					Log.d("path", mPathImage);
				} else {
					mPathImage=getPath(Uri.parse(url));
					Log.d("path", mPathImage);
				}
				File f = new File(mPathImage);
				if (f.exists()) {
					Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
					Log.i("Bitmap", "myBitmap: W x H: " + myBitmap.getWidth() + " x " + myBitmap.getHeight());
					cropImg.setImageBitmap(myBitmap);
				}
			}
		};
	}
	
	public String getPath(Uri uri) {

		ContentResolver cr = this.getContentResolver();
		String[] projection = { MediaStore.MediaColumns.DATA };
		Cursor cur = cr.query(uri, projection, null, null, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				String filePath = cur.getString(0);
				cur.close();
				return filePath;
			} else {
				cur.close();
			}
		}
		return null;
	}

	private String saveImagetoLocal(Bitmap bitmap) {
		File filename = getOutputMediaFile();

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
		return filename.getAbsolutePath();
	}

	private File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"threemins");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}
	
	public void doCrop() {
		croppedBitmap = cropImg.getCroppedImage();
		img.setImageBitmap(croppedBitmap);
		img.setVisibility(View.VISIBLE);
		cropImg.setVisibility(View.GONE);
	}
	
	public void doCropDone() {
		if (croppedBitmap == null) {
			croppedBitmap = cropImg.getCroppedImage();
		}
		
		String path = saveCroppedImageToLocal(croppedBitmap);
		Intent intent = new Intent();
		intent.putExtra("imagePath", path);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public String saveCroppedImageToLocal(Bitmap bitmap) {
		File file = new File(mPathImage);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("CropImageActivity", "saveCroppedImageToLocal ex: " + e.toString());
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
		return file.getAbsolutePath();
	}

}
