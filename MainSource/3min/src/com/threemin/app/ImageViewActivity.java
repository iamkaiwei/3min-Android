package com.threemin.app;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import br.com.condesales.models.Venue;

import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.view.SquareImageView;
import com.threemin.webservice.CategoryWebservice;
import com.threemins.R;
import com.threemins.R.id;

public class ImageViewActivity extends Activity {

	public final static int REQUEST_CAMERA_ON_CREATE = 10;
	
	public final static int REQUEST_CAMERA_IMG_1 = 11;
	public final static int REQUEST_CAMERA_IMG_2 = 12;
	public final static int REQUEST_CAMERA_IMG_3 = 13;
	public final static int REQUEST_CAMERA_IMG_4 = 14;
	
	private final static int REQUEST_LOCATION = 31;
	private final static int REQUEST_CATEGORY = 32;

	SquareImageView mImg1, mImg2, mImg3, mImg4;
	Context mContext;
	
	ArrayAdapter<CategoryModel> mAdapter;
	CategoryModel mSelectedCategory;
	EditText etName, etPrice, etDescription;
	List<ImageModel> imageModels;
	TextView locationName;
	Venue venue;
    Switch mSwShareOnFacebook;
    TextView tv_Category,tvName;
//    Button mBtnSubmit, mBtnCancel;
    LoginButton mBtnLoginFacebook;
    View viewContentProduct;
    String[] permissions = {"email","user_photos","publish_stream"};


	OnClickListener onImageViewClicked = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			final CharSequence[] items = { 	getResources().getString(R.string.activity_imageview_take_a_photo),
											getResources().getString(R.string.activity_imageview_delete) };
			
			
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
				int requestCode_Camera = 0;
				switch (v.getId()) {
				case R.id.activity_imageview_img_1:
					requestCode_Camera = REQUEST_CAMERA_IMG_1;
					break;

				case R.id.activity_imageview_img_2:
					requestCode_Camera = REQUEST_CAMERA_IMG_2;
					break;

				case R.id.activity_imageview_img_3:
					requestCode_Camera = REQUEST_CAMERA_IMG_3;
					break;

				case R.id.activity_imageview_img_4:
					requestCode_Camera = REQUEST_CAMERA_IMG_4;
					break;

				default:
					break;
				}
				startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), requestCode_Camera);
			}
			
		}
		
		public void configDialog(View v, CharSequence[] items, DialogInterface dialog, int which) {
			// which imageView is tapped
			int requestCode_Camera = 0;
			switch (v.getId()) {
			case R.id.activity_imageview_img_1:
				requestCode_Camera = REQUEST_CAMERA_IMG_1;
				break;

			case R.id.activity_imageview_img_2:
				requestCode_Camera = REQUEST_CAMERA_IMG_2;
				break;

			case R.id.activity_imageview_img_3:
				requestCode_Camera = REQUEST_CAMERA_IMG_3;
				break;

			case R.id.activity_imageview_img_4:
				requestCode_Camera = REQUEST_CAMERA_IMG_4;
				break;

			default:
				break;
			}
			
			if (which == 0) {
				startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), requestCode_Camera);
			} else {
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
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);

		mContext = ImageViewActivity.this;
		imageModels=new ArrayList<ImageModel>();
		initActionBar();
		initWidgets();
		setEvents();
		
		startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), REQUEST_CAMERA_ON_CREATE);
	}

	public void initWidgets() {
		mImg1 = (SquareImageView) findViewById(R.id.activity_imageview_img_1);
		mImg2 = (SquareImageView) findViewById(R.id.activity_imageview_img_2);
		mImg3 = (SquareImageView) findViewById(R.id.activity_imageview_img_3);
		mImg4 = (SquareImageView) findViewById(R.id.activity_imageview_img_4);
		
//        mBtnSubmit = (Button) findViewById(R.id.activity_imageview_btn_submit);
//        mBtnCancel = (Button) findViewById(R.id.activity_imageview_btn_cancel);
        mBtnLoginFacebook = (LoginButton) findViewById(R.id.activity_imageview_btn_login_facebook);
        mBtnLoginFacebook.setPublishPermissions(Arrays.asList("email","user_photos","publish_stream"));
        mSwShareOnFacebook = (Switch) findViewById(R.id.activity_imageview_switch_share_on_facebook);


		etDescription = (EditText) findViewById(R.id.activity_imageview_et_description);
		etName = (EditText) findViewById(R.id.activity_imageview_et_item);
		etPrice = (EditText) findViewById(R.id.activity_imageview_et_price);
		tv_Category=(TextView) findViewById(R.id.activity_imageview_category);
		tv_Category.setText("");
		
		locationName=(TextView) findViewById(R.id.activity_imageview_tv_location);
		
		findViewById(R.id.ln_location).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(mContext, LocationActivity.class), REQUEST_LOCATION);
			}
		});
		tvName=(TextView) findViewById(id.activity_imageview_tv_item);
		viewContentProduct=findViewById(R.id.view_product_content);
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
		if(venue!=null){
			result.setVenueId(venue.getId());
			result.setVenueLat(venue.getLocation().getLat());
			result.setVenueLong(venue.getLocation().getLng());
			result.setVenueName(venue.getName());
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
		
        
        mSwShareOnFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Session session = Session.getActiveSession();
                    if (session == null || !session.isOpened()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Login Facebook");
                        builder.setMessage("Please login Facebook to share on it.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnLoginFacebook.performClick();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSwShareOnFacebook.setChecked(false);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }
        });
        
        findViewById(R.id.view_cate).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(mContext,CategoryActivity.class), REQUEST_CATEGORY);
			}
		});
        
        findViewById(id.item_content).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(viewContentProduct.getVisibility()==View.GONE){
					viewContentProduct.setVisibility(View.VISIBLE);
					CommonUti.showKeyboard(etName, mContext);
				} else {
					viewContentProduct.setVisibility(View.GONE);
					CommonUti.hideKeyboard(etName, mContext);
					tvName.setText(etName.getText());
				}
			}
		});
	}


	public void initActionBar() {
		getActionBar().setIcon(R.drawable.btn_back);
		getActionBar().setHomeButtonEnabled(true);
//		bar.setCustomView(R.layout.actionbar_activity_imageview_custom_view);
//		bar.setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        Log.i("tructran", "Result Code is - " + resultCode +"");
        Session session = Session.getActiveSession();
        if (session != null) {
            session.onActivityResult(ImageViewActivity.this, requestCode, resultCode, data);
        } else {
            Log.i("tructran", "session null");
        }
        
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
			} 
			else if(requestCode== REQUEST_LOCATION){
				String result=data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA);
				venue=new Gson().fromJson(result, Venue.class);
				if(venue!=null){
					locationName.setText(venue.getName());
				}
			} else if (requestCode == REQUEST_CAMERA_ON_CREATE) {
				Uri uri = Uri.parse(data.getStringExtra("imageUri"));
				setImageURI(uri,mImg1);
			}
			else if(requestCode==REQUEST_CATEGORY){
				String json=data.getStringExtra(CommonConstant.INTENT_CATEGORY_DATA);
				mSelectedCategory=new Gson().fromJson(json, CategoryModel.class);
				tv_Category.setText(mSelectedCategory.getName());
			}
		}
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
					Log.d("path", pathImage);
					imageModel.setUrl(pathImage);
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
	
    public void doShareOnFacebook() {
    	Log.i("tructran", "doShareOnFacebook start");
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
        	postToFacebookWall();
        } else {
        	if (session == null) {
        		Log.i("tructran", "Session null");
			} else {
				Log.i("tructran", "Session closed");
			}
			
		}
        Log.i("tructran", "doShareOnFacebook done");
    }
    
    public void postToFacebookWall() {
        Log.i("tructran", "Post to facebook start");
        Drawable draw1 = mImg1.getDrawable();
        Drawable draw2 = mImg2.getDrawable();
        Drawable draw3 = mImg3.getDrawable();
        Drawable draw4 = mImg4.getDrawable();
        if (draw1 == null && draw2 == null && draw3 == null && draw4 == null) {
            return;
        }
        RequestBatch batch = new RequestBatch();
        String item = etName.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();
        String caption = "Name: " + item +
                        "\nPrice: " + price + 
                        "\nDescription: " + description;        
        if (draw1 != null) {
            batch.add(createRequest(draw1, caption));
        }
        if (draw2 != null) {
            batch.add(createRequest(draw2, caption));
        }
        if (draw3 != null) {
            batch.add(createRequest(draw3, caption));
        }
        if (draw4 != null) {
            batch.add(createRequest(draw4, caption));
        }
        
        batch.executeAsync();
        Log.i("tructran", "Post to facebook: done");
    }
    
    public Request createRequest(Drawable draw, String capion) {
    	Log.i("tructran", "createRequest: start");
        Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), getBitmap(draw), new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                Toast.makeText(ImageViewActivity.this, "Post photo success", Toast.LENGTH_LONG).show();
            }
        });
        Bundle params = request.getParameters();
        params.putString("message", capion);
        request.setParameters(params);
        Log.i("tructran", "createRequest: done");
        return request;
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
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_submit:
          ProductModel result=validateInput();
          if(result!=null){
              String data=new Gson().toJson(result);
              Intent intent=new Intent();
              intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
              setResult(RESULT_OK, intent);
              if (mSwShareOnFacebook.isChecked()) {
                  doShareOnFacebook();
              }
              finish();
          }
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
	
}
