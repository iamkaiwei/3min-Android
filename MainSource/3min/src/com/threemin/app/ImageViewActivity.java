package com.threemin.app;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	public final static int REQUEST_CAMERA = 1;
	public final static int REQUEST_SELECT_FILE = 2;

	ImageView mImg1, mImg2, mImg3, mImg4;
	private int mNumOfImage;
	Context mContext;
	
	OnClickListener onImageViewClicked = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mNumOfImage >= 4) {
				return;
			}
			final CharSequence[] items = {"Take a photo", "Select from Gallery", "Cancel"};
			AlertDialog.Builder builder = new AlertDialog.Builder(ImageViewActivity.this);
			builder.setTitle("Add photo...");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (items[which].equals("Take a photo")) {
						startActivityForResult(new Intent(ImageViewActivity.this,ActivityCamera.class), REQUEST_CAMERA);
					} else if (items[which].equals("Select from Gallery")) {
						openGallery();
					} else if (items[which].equals("Cancel")) {
						dialog.dismiss();
					}
				}
			});
			builder.show();
		}
	};
	
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
	
	private void openGallery() {
//		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		i.setType("image/*");
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_SELECT_FILE);
	 }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);

		mNumOfImage = 0;
		mContext = ImageViewActivity.this;
		
		initWidgets();
		initSpiner();
		setEvents();
		initActionBar();
		
//		startActivityForResult(new Intent(this,ActivityCamera.class), 1);
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
			if (requestCode == REQUEST_CAMERA) {
				Uri uri = Uri.parse(data.getStringExtra("imageUri"));
				setImageURI(uri);
			} else if (requestCode == REQUEST_SELECT_FILE) {
				Uri selectedFileUri = data.getData();
				String tempPath = getPath(selectedFileUri, ImageViewActivity.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                setImageBitmap(bm);
			}
			
		}
	}
	
	 public String getPath(Uri uri, Activity activity) {
		 String[] projection = { MediaColumns.DATA };
	        Cursor cursor = activity
	                .managedQuery(uri, projection, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	 }
	 
	 public void setImageURI (Uri uri) {
		 switch (mNumOfImage) {
		case 0:
			mImg1.setImageURI(uri);
			mNumOfImage++;
			break;

		case 1:
			mImg2.setImageURI(uri);
			mNumOfImage++;
			break;

		case 2:
			mImg3.setImageURI(uri);
			mNumOfImage++;
			break;

		case 3:
			mImg4.setImageURI(uri);
			mNumOfImage++;
			break;
		default:
			break;
		}
	 }
	 
	 public void setImageBitmap (Bitmap bm) {
		 switch (mNumOfImage) {
		case 0:
			mImg1.setImageBitmap(bm);
			mNumOfImage++;
			break;

		case 1:
			mImg2.setImageBitmap(bm);
			mNumOfImage++;
			break;

		case 2:
			mImg3.setImageBitmap(bm);
			mNumOfImage++;
			break;

		case 3:
			mImg4.setImageBitmap(bm);
			mNumOfImage++;
			break;
		default:
			break;
		}
	 }
}
