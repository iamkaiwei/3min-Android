package com.threemin.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.CategoryModel;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;
import com.threemin.view.SquareImageView;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UploaderImageUlti;
import com.threemins.R;
import com.threemins.R.id;

public class ImageViewActivity extends Activity {
    
    public static final String tag = "ImageViewActivity";

	public final static int REQUEST_CAMERA_ON_CREATE = 10;
	
	public final static int REQUEST_CAMERA_IMG_1 = 11;
	public final static int REQUEST_CAMERA_IMG_2 = 12;
	public final static int REQUEST_CAMERA_IMG_3 = 13;
	public final static int REQUEST_CAMERA_IMG_4 = 14;
	
	private final static int REQUEST_LOCATION = 31;
	private final static int REQUEST_CATEGORY = 32;
    private static final int REQUEST_PRODUCT_INPUT_ITEM = 33;


	SquareImageView mImg1, mImg2, mImg3, mImg4;
	Context mContext;
	
	ArrayAdapter<CategoryModel> mAdapter;
	CategoryModel mSelectedCategory;

	EditText etPrice;
	String mProductName, mProductDescription;
	
	
	List<ImageModel> imageModels;
	TextView locationName;
	Venue venue;
    Switch mSwShareOnFacebook;
    TextView tv_Category,tvName;
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
		ImageModel model = (ImageModel)img.getTag();
		
		Log.i(tag, new Gson().toJson(model).toString());
		
		if (mIsUpdateProduct && model.getId() != 0) {
            model.setTypeEditProduct(ImageModel.TYPE_EDIT_PRODUCT_DELETE);
            for (ImageModel imgM : imageModels) {
                if (imgM.getId() == model.getId()) {
                    imgM.setTypeEditProduct(ImageModel.TYPE_EDIT_PRODUCT_DELETE);
                }
            }
        } else {
            imageModels.remove(model);
        }
		
		
		if (img.getDrawable() != null) {
			img.setImageDrawable(null);
		}
	}
	
	//TODO: check edit product or create new one
	private boolean mIsUpdateProduct;
	private ProductModel mProductModel;
	private String mVenueID;
	private String mVenueName;
	private double mVenueLat;
	private double mVenueLong;
	private ImageView mImgDeleteListing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);

		mContext = ImageViewActivity.this;
		mIsUpdateProduct = getIntent().getBooleanExtra(CommonConstant.INTENT_EDIT_PRODUCT, false);
        
		imageModels=new ArrayList<ImageModel>();
		initActionBar();
		initWidgets();
		setEvents();
		
		if (mIsUpdateProduct) {
            doEditProduct();
        } else {
            mImgDeleteListing.setVisibility(View.GONE);
            startActivityForResult(new Intent(ImageViewActivity.this, ActivityCamera.class), REQUEST_CAMERA_ON_CREATE);
        }
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        ThreeMinsApplication.isActive = true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        ThreeMinsApplication.isActive = false;
    }

	public void initWidgets() {
		mImg1 = (SquareImageView) findViewById(R.id.activity_imageview_img_1);
		mImg2 = (SquareImageView) findViewById(R.id.activity_imageview_img_2);
		mImg3 = (SquareImageView) findViewById(R.id.activity_imageview_img_3);
		mImg4 = (SquareImageView) findViewById(R.id.activity_imageview_img_4);
		
        mBtnLoginFacebook = (LoginButton) findViewById(R.id.activity_imageview_btn_login_facebook);
        mBtnLoginFacebook.setPublishPermissions(Arrays.asList("email","user_photos","publish_stream"));
        mSwShareOnFacebook = (Switch) findViewById(R.id.activity_imageview_switch_share_on_facebook);

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
		
		mImgDeleteListing = (ImageView) findViewById(R.id.activity_imageview_img_delete_listing);
	}

	private ProductModel validateInput() {
	    String description = mProductDescription;
	    String name = mProductName;
	    
		String price = etPrice.getText().toString();
		
		ProductModel result=new ProductModel();
		if(imageModels.isEmpty()){
			Toast.makeText(mContext, R.string.error_empty_image, Toast.LENGTH_SHORT).show();
			Log.i("ImageViewActivity", "list img empty");
		} else {
			result.setImages(imageModels);
		}
		
		if(TextUtils.isEmpty(name)){
			Toast.makeText(mContext, R.string.error_miss_field, Toast.LENGTH_SHORT).show();
			Log.i("ImageViewActivity", "name empty");
			return null;
		} else {
			result.setName(name);
		}
		
		if(TextUtils.isEmpty(price)){
			Toast.makeText(mContext, R.string.error_miss_field, Toast.LENGTH_SHORT).show();
			Log.i("ImageViewActivity", "price empty");
			return null;
		} else {
			result.setPrice(price);
		}
		
		if(!TextUtils.isEmpty(description)){
			result.setDescription(description);
			Log.i("ImageViewActivity", "description empty");
		} 
		if(venue!=null){
			result.setVenueId(venue.getId());
			result.setVenueLat(venue.getLocation().getLat());
			result.setVenueLong(venue.getLocation().getLng());
			result.setVenueName(venue.getName());
		} else {
			Log.i("ImageViewActivity", "location empty");
			//check if it is edit product
			if (    mIsUpdateProduct &&
			        !TextUtils.isEmpty(mVenueID) &&
                    !TextUtils.isEmpty(mVenueID) &&
                    !TextUtils.isEmpty(mVenueID) &&
                    !TextUtils.isEmpty(mVenueID)
                ) 
			{
			    result.setVenueId(mVenueID);
	            result.setVenueLat(mVenueLat);
	            result.setVenueLong(mVenueLong);
	            result.setVenueName(mVenueName);
            }
		}
		
		if (mSelectedCategory == null) {
			Toast.makeText(mContext, R.string.error_miss_field, Toast.LENGTH_SHORT).show();
			Log.i("ImageViewActivity", "category empty");
			return null;
		} else {
			result.setCategory(mSelectedCategory);
		}
		
		result.setOwner(PreferenceHelper.getInstance(mContext).getCurrentUser());
		
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
                        builder.setTitle(mContext.getString(R.string.activity_imageview_facebook_dialog_title));
                        builder.setMessage(mContext.getString(R.string.activity_imageview_facebook_dialog_message));
                        builder.setPositiveButton(mContext.getString(R.string.activity_imageview_facebook_dialog_btn_ok), new DialogInterface.OnClickListener() {
                            
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnLoginFacebook.performClick();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton(mContext.getString(R.string.activity_imageview_facebook_dialog_btn_cancel), new DialogInterface.OnClickListener() {
                            
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
			    Bundle bundle=new Bundle();
			    bundle.putString(CommonConstant.INTENT_PRODUCT_NAME, mProductName);
			    bundle.putString(CommonConstant.INTENT_PRODUCT_DESCRIPTION, mProductDescription);
			    
			    String dataLocation= new Gson().toJson(venue);
			    bundle.putString(CommonConstant.INTENT_PRODUCT_DATA, dataLocation);
			    Intent intent=new Intent(mContext, InputProductActivity.class);
			    intent.putExtras(bundle);
			    startActivityForResult(intent, REQUEST_PRODUCT_INPUT_ITEM);
			}
		});
        
        mImgDeleteListing.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                doDeleteProduct();
            }
        });
	}


	public void initActionBar() {
		getActionBar().setIcon(R.drawable.btn_back);
		getActionBar().setHomeButtonEnabled(true);
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
				String uri = data.getStringExtra("imagePath");
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
				String uri = data.getStringExtra("imagePath");
				setImageURI(uri,mImg1);
			}
			else if(requestCode==REQUEST_CATEGORY){
				String json=data.getStringExtra(CommonConstant.INTENT_CATEGORY_DATA);
				mSelectedCategory=new Gson().fromJson(json, CategoryModel.class);
				tv_Category.setText(mSelectedCategory.getName());
			} else if(requestCode==REQUEST_PRODUCT_INPUT_ITEM){
			    String productName = data.getStringExtra(CommonConstant.INTENT_PRODUCT_NAME);
		        String productDescription = data.getStringExtra(CommonConstant.INTENT_PRODUCT_DESCRIPTION);
		        venue = new Gson().fromJson(data.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), Venue.class);
		        tvName.setText(productName);
		        mProductName = productName;
		        mProductDescription = productDescription;
		        
		        if(venue!=null){
                    locationName.setText(venue.getName());
                }
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

	public void setImageURI(String uri,ImageView imageView) {
		File imgFile = new  File(uri);
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    imageView.setImageBitmap(myBitmap);
		    ImageModel imgModel;
		    ImageModel tempModel = (ImageModel)imageView.getTag();
		    if (mIsUpdateProduct && tempModel != null) {
                imgModel = tempModel;
                imageModels.remove(imgModel);
                imgModel.setTypeEditProduct(ImageModel.TYPE_EDIT_PRODUCT_UPDATE);
            }else {
                imgModel = new ImageModel();
            }
		    imgModel.setUrl(imgFile.getAbsolutePath());
		    imageView.setTag(imgModel);
		    imageModels.add(imgModel);
		} else {
			Toast.makeText(this, "File does not exists", Toast.LENGTH_LONG).show();
		}
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
        
		ImageModel draw1 = (ImageModel) mImg1.getTag();
		ImageModel draw2 = (ImageModel) mImg2.getTag();
		ImageModel draw3 = (ImageModel) mImg3.getTag();
		ImageModel draw4 = (ImageModel) mImg4.getTag();
		if (draw1 == null && draw2 == null && draw3 == null && draw4 == null) {
			return;
		}
		RequestBatch batch = new RequestBatch();
		String item = mProductName;
		String price = etPrice.getText().toString();
		String description = mProductDescription;
		String caption = getString(R.string.activity_imageview_facebook_caption_name) + " " + item 
		        + getString(R.string.activity_imageview_facebook_caption_price) + " " + price + "k Vnd"
				+ getString(R.string.activity_imageview_facebook_caption_description) + " " + description;
		if (draw1 != null) {
			batch.add(createRequestFromFile(draw1.getUrl(), caption));
		}
		if (draw2 != null) {
			batch.add(createRequestFromFile(draw2.getUrl(), caption));
		}
		if (draw3 != null) {
			batch.add(createRequestFromFile(draw3.getUrl(), caption));
		}
		if (draw4 != null) {
			batch.add(createRequestFromFile(draw4.getUrl(), caption));
		}

		batch.executeAsync();
		
		Log.i("tructran", "Post to facebook: done");
	}
    
	public Request createRequestFromFile(String filePath, String caption) {
		Log.i("tructran", "createRequestFromFile: start");

		File file = new File(filePath);
		Request request = null;
		try {
			request = Request.newUploadPhotoRequest(Session.getActiveSession(),
					file, new Request.Callback() {

						@Override
						public void onCompleted(Response response) {
							Toast.makeText(ImageViewActivity.this, getString(R.string.activity_imageview_facebook_post_successfully), Toast.LENGTH_LONG).show();
							Log.i("tructran", "Post image done");
						}
					});
			Bundle params = request.getParameters();
			params.putString("message", caption);
			request.setParameters(params);
			Log.i("tructran", "createRequestFromFile: done");
			return request;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("tructran", "createRequestFromFile: " + e.toString());
		}

		Log.i("tructran", "createRequestFromFile: done, request null");
		return null;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_submit:
          ProductModel result=validateInput();
          
          if (mIsUpdateProduct) {
            //TODO: haven't implemented
              new UpdateProductTask().execute(result);
          } else {
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
	
	//TODO============== EDIT PRODUCT ================================
	public void doEditProduct() {
	    String strData = getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA);
	    if (strData != null && strData.length() > 0) {
            mProductModel = new Gson().fromJson(strData, ProductModel.class);
            
            //4 imgs
            ArrayList<SquareImageView> listImgV = new ArrayList<SquareImageView>();
            listImgV.add(mImg1);
            listImgV.add(mImg2);
            listImgV.add(mImg3);
            listImgV.add(mImg4);
            List<ImageModel> listImgModel = mProductModel.getImages();
            if (listImgModel.size() > 0) {
                imageModels.clear();
                imageModels.addAll(listImgModel);
                for (int i = 0; i < listImgModel.size(); i++) {
                    UrlImageViewHelper.setUrlDrawable(
                            listImgV.get(i), 
                            listImgModel.get(i).getMedium(), 
                            R.drawable.stuff_img);
                    listImgModel.get(i).setTypeEditProduct(ImageModel.TYPE_EDIT_PRODUCT_NO_CHANGE);
                    listImgV.get(i).setTag(listImgModel.get(i));
                }
            }
            
            //category:
            mSelectedCategory = mProductModel.getCategory();
            tv_Category.setText(mSelectedCategory.getName());
            
            //name:
            tvName.setText(mProductModel.getName());
            mProductName = mProductModel.getName();
            
            //description:
            mProductDescription = mProductModel.getDescription();
            
            //price:
            etPrice.setText(mProductModel.getPrice());
            
            //location:
            mVenueName = mProductModel.getVenueName();
            mVenueID = mProductModel.getVenueId();
            mVenueLong = mProductModel.getVenueLong();
            mVenueLat = mProductModel.getVenueLat();
        }
	}
	
	void doDeleteProduct() {
	    //TODO: haven't implemented
	    Dialog dialog = createDeleteProductDialog();
	    dialog.show();
	    
	}
	
	public Dialog createDeleteProductDialog() {
	    final Dialog dialog = new Dialog(this, R.style.FeedbackDialog);
        View dialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_delete_product, null);
        
        Button btnYes = (Button) dialogLayout.findViewById(R.id.dialog_delete_product_btn_yes);
        Button btnNo = (Button) dialogLayout.findViewById(R.id.dialog_delete_product_btn_no);
        ImageView ivProduct = (ImageView) dialogLayout.findViewById(R.id.dialog_delete_product_iv_product);
        
        btnYes.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new DeleteProductTask().execute();  
            }
        });
        
        btnNo.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        List<ImageModel> list = mProductModel.getImages();
        if (list != null && list.size() > 0) {
            UrlImageViewHelper.setUrlDrawable(ivProduct, list.get(0).getMedium(), R.drawable.stuff_img);
        } else {
            ivProduct.setImageResource(R.drawable.stuff_img);
        }
        
        dialog.setContentView(dialogLayout);
        return dialog;
	}
	
	private class UpdateProductTask extends AsyncTask<ProductModel, Void, ProductModel> {
	    
	    private ProgressDialog dialog;
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if (dialog == null) {
                dialog = new ProgressDialog(mContext);
                dialog.setMessage(getString(R.string.please_wait));
            }
	        dialog.show();
	    }
	    
	    @Override
	    protected ProductModel doInBackground(ProductModel... params) {
	        ProductModel model = params[0];
	        String token = PreferenceHelper.getInstance(mContext).getTokken();
	        String url = String.format(WebserviceConstant.UPDATE_PRODUCT, "" + mProductModel.getId());
	        Log.i(tag, "UpdateProductTask url: " + url);
	        
	        return new UploaderImageUlti().updateUserPhoto(url, model, token);
	    }
	    
	    @Override
	    protected void onPostExecute(ProductModel result) {
	        super.onPostExecute(result);
	        if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
	        Intent intent = new Intent();
	        if (result != null) {
                String strProductData = new Gson().toJson(result);
                Log.i(tag, "UpdateProductTask result: " + strProductData);
                intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, strProductData);
            }
	        setResult(RESULT_OK, intent);
	        finish();
	    }
	    
	}
	
	private class DeleteProductTask extends AsyncTask<Void, Void, Integer> {
	    private ProgressDialog dialog;
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if (dialog == null) {
                dialog = new ProgressDialog(ImageViewActivity.this);
                dialog.setMessage(getString(R.string.please_wait));
            }
	        
	        dialog.show();
	    }
	    
	    @Override
	    protected Integer doInBackground(Void... params) {
	        String token = PreferenceHelper.getInstance(ImageViewActivity.this).getTokken();
	        int id = mProductModel.getId();
	        int result = new ProductWebservice().deleteProduct(token, id);
	        Log.i(tag, "DeleteProductTask: result: " + result);
	        return result;
	    }
	    
	    @Override
	    protected void onPostExecute(Integer result) {
	        super.onPostExecute(result);
	        
	        if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
	        
	        if (result == WebserviceConstant.RESPONSE_CODE_SUCCESS) {
                doSuccessToDeleteProduct();
            } else {
                doFailToDeleteProduct();
            }
	    }
	}
	
	public void doSuccessToDeleteProduct() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        Toast.makeText(this, getString(R.string.notify_product_deleted), Toast.LENGTH_LONG).show();
	}
	
	public void doFailToDeleteProduct() {
        Toast.makeText(this, getString(R.string.notify_fail_to_delete_product), Toast.LENGTH_LONG).show();
	}
	
}
