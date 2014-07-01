package com.threemin.app;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.edmodo.cropper.CropImageView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ImageModel;
import com.threemins.R;

public class CropImageActivity extends Activity {
	
	ImageView img;
	CropImageView cropImg;
	Button btnCrop, btnDone;
	Bitmap croppedBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_image);
		
		initWidgets();
		initData();
		initListener();
	}
	
	public void initWidgets() {
		img = (ImageView) findViewById(R.id.act_crop_img);
		cropImg = (CropImageView) findViewById(R.id.act_crop_crop_img);
		btnCrop = (Button) findViewById(R.id.act_crop_img_btn_crop);
		btnDone = (Button) findViewById(R.id.act_crop_img_btn_done);
	}
	
	public void initData() {
		Intent intent = getIntent();
		Uri uri = Uri.parse(intent.getStringExtra("imageUri"));
		setImageURI(uri, img);
	}
	
	public void initListener() {
		btnCrop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				croppedBitmap = cropImg.getCroppedImage();
				img.setImageBitmap(croppedBitmap);
				img.setVisibility(View.VISIBLE);
				cropImg.setVisibility(View.GONE);
			}
		});
		
		btnDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (croppedBitmap != null) {
					String path = saveImagetoLocal(croppedBitmap);
					Intent intent = new Intent();
					intent.putExtra("imagePath", path);
					setResult(RESULT_OK, intent);
					finish();
				}
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
				cropImg.setImageBitmap(loadedBitmap);
				ImageModel imageModel=new ImageModel();
				if (url.contains("content://com.google.android.apps.photos.content")) {
					imageModel.setUrl(saveImagetoLocal(loadedBitmap));
					Log.d("path", imageModel.getUrl());
				} else {
					String pathImage=getPath(Uri.parse(url));
					Log.d("path", pathImage);
					imageModel.setUrl(pathImage);
				}
				imageView.setTag(imageModel);
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
	
	public Bitmap getCroppedBitmap(Bitmap bitmap) {
		return Bitmap.createBitmap(bitmap, 0, 10, bitmap.getWidth(), 350);
	}
	
	public Bitmap getBitmap(Drawable draw) {
        Bitmap bitmap;
        if (draw instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) draw).getBitmap();
        } else {
            Drawable d = draw;
            bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            d.draw(canvas);
        }
        return bitmap;
    }

}
