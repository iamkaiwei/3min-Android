package com.threemin.app;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.webservice.CategoryWebservice;
import com.threemin.webservice.UploaderImageUlti;
import com.threemins.R;

public class ImageViewActivity extends Activity {

	public final static int REQUEST_CAMERA_ON_CREATE = 10;
	
	public final static int REQUEST_CAMERA_IMG_1 = 11;
	public final static int REQUEST_CAMERA_IMG_2 = 12;
	public final static int REQUEST_CAMERA_IMG_3 = 13;
	public final static int REQUEST_CAMERA_IMG_4 = 14;
	

	public final static int REQUEST_SELECT_FILE_IMG_1 = 21;
	public final static int REQUEST_SELECT_FILE_IMG_2 = 22;
	public final static int REQUEST_SELECT_FILE_IMG_3 = 23;
	public final static int REQUEST_SELECT_FILE_IMG_4 = 24;

	ImageView mImg1, mImg2, mImg3, mImg4;
	Context mContext;
	
	//variables for spinner
	Spinner mSpnCategory;
	List<CategoryModel> mListCategory;
	ArrayAdapter<CategoryModel> mAdapter;
	CategoryModel mSelectedCategory;
	EditText etName, etPrice, etDescription;
	List<ImageModel> imageModels;

	OnClickListener onImageViewClicked = new OnClickListener() {

		@Override
		public void onClick(final View v) {
//			final CharSequence[] items = { "Take a photo",
//											"Select from Gallery", 
//											"Delete" };
			
			final CharSequence[] items = { 	getResources().getString(R.string.activity_imageview_take_a_photo),
											getResources().getString(R.string.activity_imageview_select_from_gallery), 
											getResources().getString(R.string.activity_imageview_delete) };
			
			final CharSequence[] itemsWithoutDelete = { getResources().getString(R.string.activity_imageview_take_a_photo),
														getResources().getString(R.string.activity_imageview_select_from_gallery) };
			
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(getResources().getString(R.string.activity_imageview_add_photo));
			
			if ( ((ImageView) v).getDrawable() != null ) {
				builder.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						configDialog(v, items, dialog, which);
					}
				});
				builder.show();
			} else {
				builder.setItems(itemsWithoutDelete, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						configDialog(v, itemsWithoutDelete, dialog, which);
					}
				});
				builder.show();
			}
			
		}
		
		public void configDialog(View v, CharSequence[] items, DialogInterface dialog, int which) {
			// which imageView is tapped
			int requestCode_Camera = 0;
			int requestCode_SelectFile = 0;
			switch (v.getId()) {
			case R.id.activity_imageview_img_1:
				requestCode_Camera = REQUEST_CAMERA_IMG_1;
				requestCode_SelectFile = REQUEST_SELECT_FILE_IMG_1;
				break;

			case R.id.activity_imageview_img_2:
				requestCode_Camera = REQUEST_CAMERA_IMG_2;
				requestCode_SelectFile = REQUEST_SELECT_FILE_IMG_2;
				break;

			case R.id.activity_imageview_img_3:
				requestCode_Camera = REQUEST_CAMERA_IMG_3;
				requestCode_SelectFile = REQUEST_SELECT_FILE_IMG_3;
				break;

			case R.id.activity_imageview_img_4:
				requestCode_Camera = REQUEST_CAMERA_IMG_4;
				requestCode_SelectFile = REQUEST_SELECT_FILE_IMG_4;
				break;

			default:
				break;
			}
			
			if (which == 0) {
				startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), requestCode_Camera);
			} else if (which == 1) {
				openGallery(requestCode_SelectFile);
			} else if (which == 2) {
				deleteImage(v.getId());
			}
		}
	};

	public void deleteImage (int resId) {
		ImageView img = (ImageView) findViewById(resId);
		imageModels.remove((ImageModel)img.getTag());
		if (img.getDrawable() != null) {
			img.setImageDrawable(null);
		}
	}
	
	

	OnItemSelectedListener onItemSpinnerSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
			mSelectedCategory = mListCategory.get(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	
	private void openGallery(int requestCode) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, requestCode);
	 }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);

		mContext = ImageViewActivity.this;
		imageModels=new ArrayList<ImageModel>();
		initActionBar();
		initWidgets();
		initSpiner();
		setEvents();
		
		startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), REQUEST_CAMERA_ON_CREATE);
	}

	public void initWidgets() {
		mImg1 = (ImageView) findViewById(R.id.activity_imageview_img_1);
		mImg2 = (ImageView) findViewById(R.id.activity_imageview_img_2);
		mImg3 = (ImageView) findViewById(R.id.activity_imageview_img_3);
		mImg4 = (ImageView) findViewById(R.id.activity_imageview_img_4);

		etDescription = (EditText) findViewById(R.id.activity_imageview_et_description);
		etName = (EditText) findViewById(R.id.activity_imageview_et_item);
		etPrice = (EditText) findViewById(R.id.activity_imageview_et_price);

		mSpnCategory = (Spinner) findViewById(R.id.activity_imageview_spn_category);
		findViewById(R.id.activity_imageview_btn_submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProductModel result=validateInput();
				if(result!=null){
					String data=new Gson().toJson(result);
					Intent intent=new Intent();
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					setResult(RESULT_OK, intent);
					finish();
				}
			}

		});
		
		findViewById(R.id.activity_imageview_btn_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}

	private ProductModel validateInput() {
		String description = etDescription.getText().toString();
		String name = etName.getText().toString();
		String price = etPrice.getText().toString();
		
		ProductModel result=new ProductModel();
		if(imageModels.isEmpty()){
			Toast.makeText(mContext, R.string.error_empty_image, Toast.LENGTH_SHORT).show();
		}
		if(TextUtils.isEmpty(name)){
			Toast.makeText(mContext, R.string.error_miss_field, Toast.LENGTH_SHORT).show();
			return null;
		} else {
			result.setName(name);
		}
		
		if(TextUtils.isEmpty(price)){
			Toast.makeText(mContext, R.string.error_miss_field, Toast.LENGTH_SHORT).show();
			return null;
		} else {
			result.setPrice(price);
		}
		
		if(!TextUtils.isEmpty(description)){
			result.setDescription(description);
		} 
		result.setCategory(mSelectedCategory);
		result.setOwner(PreferenceHelper.getInstance(mContext).getCurrentUser());
		result.setImages(imageModels);
		return result;
	}

	public void setEvents() {
		mImg1.setOnClickListener(onImageViewClicked);
		mImg2.setOnClickListener(onImageViewClicked);
		mImg3.setOnClickListener(onImageViewClicked);
		mImg4.setOnClickListener(onImageViewClicked);
	}

	public void initSpiner() {
		new InitCategory().execute();
	}

	private class InitCategory extends AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance().getTaggableCategory(mContext);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<CategoryModel> result) {
			if (result != null) {
				Log.i("tructran", "Set adapter");
				mListCategory = result;
				mSelectedCategory = mListCategory.get(0);
				mAdapter = new ArrayAdapter<CategoryModel>(mContext, android.R.layout.simple_spinner_item, mListCategory);
				mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
				mSpnCategory.setAdapter(mAdapter);
				mSpnCategory.setOnItemSelectedListener(onItemSpinnerSelected);
			}
			super.onPostExecute(result);
		}
	}

	public void initActionBar() {
		ActionBar bar = getActionBar();
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		bar.setCustomView(R.layout.actionbar_activity_imageview_custom_view);
		bar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode >= REQUEST_CAMERA_IMG_1 && requestCode <= REQUEST_CAMERA_IMG_4) {
				Uri uri = Uri.parse(data.getStringExtra("imageUri"));
				if (requestCode == REQUEST_CAMERA_IMG_1) {
					setImageURI(uri,mImg1);
				} else if (requestCode == REQUEST_CAMERA_IMG_2) {
					setImageURI(uri,mImg2);
				} else if (requestCode == REQUEST_CAMERA_IMG_3) {
					setImageURI(uri,mImg3);
				} else if (requestCode == REQUEST_CAMERA_IMG_4) {
					setImageURI(uri,mImg4);
				}
			} else if (requestCode >= REQUEST_SELECT_FILE_IMG_1 && requestCode <= REQUEST_SELECT_FILE_IMG_4) {
				Uri uri = data.getData();
				if (requestCode == REQUEST_SELECT_FILE_IMG_1) {
					setImageURI(uri,mImg1);
				} else if (requestCode == REQUEST_SELECT_FILE_IMG_2) {
					setImageURI(uri,mImg2);
				} else if (requestCode == REQUEST_SELECT_FILE_IMG_3) {
					setImageURI(uri,mImg3);
				} else if (requestCode == REQUEST_SELECT_FILE_IMG_4) {
					setImageURI(uri,mImg4);
				}
			} else if (requestCode == REQUEST_CAMERA_ON_CREATE) {
				Uri uri = Uri.parse(data.getStringExtra("imageUri"));
				setImageURI(uri,mImg1);
			}

		}
	}

	public String getPath(Uri uri) {

		ContentResolver cr = this.getContentResolver();
		String[] projection = { MediaStore.MediaColumns.DATA };
		Cursor cur = cr.query(uri, projection, null, null, null);
		if (cur != null) {
			cur.moveToFirst();
			String filePath = cur.getString(0);
			return filePath;
		}
		return null;
	}

	public void setImageURI(Uri uri,ImageView imageView) {
			UrlImageViewHelper.setUrlDrawable(imageView, uri.toString(), getImageCallback());

	}

	private UrlImageViewCallback getImageCallback() {
		return new UrlImageViewCallback() {

			@Override
			public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
				Log.d("url", url);
				ImageModel imageModel=new ImageModel();
				if (url.contains("content://com.google.android.apps.photos.content")) {
					imageModel.setUrl(saveImagetoLocal(loadedBitmap));
					Log.d("path", imageModel.getUrl());
				} else {
					String pathImage=getPath(Uri.parse(url));
					Log.d("path", pathImage)
;					imageModel.setUrl(pathImage);
				}
				imageView.setTag(imageModel);
				imageModels.add(imageModel);
			}
		};
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
	
}
