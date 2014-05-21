package com.threemin.app;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.threemin.model.CategoryModel;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;

public class ImageViewActivity extends Activity  {
	
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
	
	OnClickListener onImageViewClicked = new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			final CharSequence[] items = { "Take a photo",
					"Select from Gallery", "Delete" };
			final CharSequence[] itemsWithoutDelete = {"Take a photo",
					"Select from Gallery"};
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Add photo...");
			if ( ((ImageView) v).getDrawable() != null ) {
				builder.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
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
						if (items[which].equals("Take a photo")) {
							startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), requestCode_Camera);
						} else if (items[which].equals("Select from Gallery")) {
							openGallery(requestCode_SelectFile);
						} else if (items[which].equals("Delete")) {
							deleteImage(v.getId());
						}
					}
				});
				builder.show();
			} else {
				builder.setItems(itemsWithoutDelete, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
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
						if (itemsWithoutDelete[which].equals("Take a photo")) {
							startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), requestCode_Camera);
						} else if (itemsWithoutDelete[which].equals("Select from Gallery")) {
							openGallery(requestCode_SelectFile);
						}
					}
				});
				builder.show();
			}
			
		}
	};
	
	public void deleteImage (int resId) {
		ImageView img = (ImageView) findViewById(resId);
		if (img.getDrawable() != null) {
			img.setImageDrawable(null);
		}
	}
	
	//variables for spinner
	Spinner mSpnCategory;
	List<CategoryModel> mListCategory;
	ArrayAdapter<CategoryModel> mAdapter;
	CategoryModel mSelectedCategory;
	
	OnItemSelectedListener onItemSpinnerSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
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
		
		initWidgets();
		initSpiner();
		setEvents();
		initActionBar();
		
	}
	
	public void initWidgets() {
		mImg1 = (ImageView) findViewById(R.id.activity_imageview_img_1);
		mImg2 = (ImageView) findViewById(R.id.activity_imageview_img_2);
		mImg3 = (ImageView) findViewById(R.id.activity_imageview_img_3);
		mImg4 = (ImageView) findViewById(R.id.activity_imageview_img_4);
		
		mSpnCategory = (Spinner) findViewById(R.id.activity_imageview_spn_category);
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
	
	private class InitCategory extends
			AsyncTask<Void, Void, List<CategoryModel>> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<CategoryModel> doInBackground(Void... arg0) {
			try {
				return CategoryWebservice.getInstance()
						.getAllCategory(mContext);
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
					mImg1.setImageURI(uri);
				} else if (requestCode == REQUEST_CAMERA_IMG_2) {
					mImg2.setImageURI(uri);
				} else if (requestCode == REQUEST_CAMERA_IMG_3) {
					mImg3.setImageURI(uri);
				} else if (requestCode == REQUEST_CAMERA_IMG_4) {
					mImg4.setImageURI(uri);
				}
			} else if (requestCode >= REQUEST_SELECT_FILE_IMG_1 && requestCode <= REQUEST_SELECT_FILE_IMG_4) {
				Uri uri = data.getData();
				if (requestCode == REQUEST_SELECT_FILE_IMG_1) {
					mImg1.setImageURI(uri);
				} else if (requestCode == REQUEST_SELECT_FILE_IMG_2) {
					mImg2.setImageURI(uri);
				} else if (requestCode == REQUEST_SELECT_FILE_IMG_3) {
					mImg3.setImageURI(uri);
				} else if (requestCode == REQUEST_SELECT_FILE_IMG_4) {
					mImg4.setImageURI(uri);
				}
			}
			
		}
	}
}
