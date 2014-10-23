package com.threemin.adapter;

import java.util.List;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.squareup.picasso.Picasso;
import com.threemin.app.ProfileActivity;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class ProductGridAdapter extends BaseAdapter {
	private List<ProductModel> mData;
	private Context mContext;
	private LoginButton mLoginButton;
	LayoutInflater inflater;
	int widthItem;

	public ProductGridAdapter(List<ProductModel> data, Context context, LoginButton btn, int widthItem) {
		this.mData = data;
		mContext = context;
		mLoginButton = btn;
		
	    inflater = (LayoutInflater) context.getSystemService(
	            Context.LAYOUT_INFLATER_SERVICE);
	    this.widthItem=widthItem;
	}

	public List<ProductModel> getListProducts() {
		return this.mData;
	}

	public void updateData(List<ProductModel> data) {
		this.mData = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(mData==null){
			return 0;
		}
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
    long logTime;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    
	   logTime = System.currentTimeMillis();
	        
		LinearLayout layout = null;

		if (convertView == null) {
			layout = (LinearLayout) inflater.inflate(R.layout.inflater_body_product_grid, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		Log.d("3min", "Inflation time = "+ (System.currentTimeMillis() - logTime));
		initBody(position, layout);
		Log.d("3min", "Complete time = "+ (System.currentTimeMillis() - logTime));
		return layout;
	}

	private void initBody(int position, View convertView) {
		if (mData == null || mData.size() == 0) {
			return;
		}
		final ProductModel model = mData.get(position);

		if (model != null) {
			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
//			LayoutParams param=image.getLayoutParams();
//			param.height=widthItem;
//			image.setLayoutParams(param);
			
			if (model.getImages().size() > 0) {
//			    Log.d("image", "height="+image.getHeight());
			    List<Integer> dimens = model.getImages().get(0).getDimensions();
			    if (dimens != null && dimens.size() == 2) {
			        Log.i("dimen", "size: " + dimens.size());
			        int w = model.getImages().get(0).getDimensions().get(0);
			        int h = model.getImages().get(0).getDimensions().get(1);
			        float ratio = (float)this.widthItem / (float)w;
			        int heightItem = (int) (h * ratio);
                    LayoutParams param = image.getLayoutParams();
                    param.height = heightItem;
                    image.setLayoutParams(param);
                    UrlImageViewHelper.setUrlDrawable(image, model.getImages().get(0).getMedium(),R.drawable.stuff_img);
                } else {
                    UrlImageViewHelper.setUrlDrawable(image, model.getImages().get(0).getMedium(), R.drawable.stuff_img, new UrlImageViewCallback() {
                        
                        @Override
                        public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                            float ratio = ( (float)widthItem / (float)loadedBitmap.getWidth() );
                            int heightItem = (int) ((float)loadedBitmap.getHeight() * ratio);
                            LayoutParams param=imageView.getLayoutParams();
                            param.height=heightItem;
                            imageView.setLayoutParams(param);
                            imageView.setImageBitmap(loadedBitmap);
                        }
                    });
                }
				
				
//				imageLoader.displayImage( model.getImages().get(0).getOrigin(),image,options);
//			    Log.d("3min", "Image: "+model.getImages().get(0).getSquare());
//			    Picasso.with(mContext).load(model.getImages().get(0).getOrigin()).placeholder(R.drawable.stuff_img).into(image);
			}

			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_name);
			tv_name.setText(model.getName());

			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_price);
			tv_price.setText(model.getPrice() + CommonConstant.CURRENCY);

			final TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_grid_tv_like);
			if (model.getLike() > 0) {
				tv_like.setText("" + model.getLike());
			} else {
				tv_like.setText("");
			}

			final ImageView img_like = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_img_like);
			if (model.isLiked()) {
				img_like.setSelected(true);
			} else {
				img_like.setSelected(false);
			}
			//allow user to like or unlike a product when tap on icon like
			LinearLayout layout_like = (LinearLayout) convertView.findViewById(R.id.inflater_body_product_grid_group_like);
			layout_like.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    final String tokken = PreferenceHelper.getInstance(mContext).getTokken();

                    model.setLiked(!model.isLiked());
                    if (model.isLiked()) {
                        model.setLike(model.getLike() + 1);
                    } else {
                        model.setLike(model.getLike() - 1);
                    }

                    if (model.getLike() > 0) {
                        tv_like.setText("" + model.getLike());
                    } else {
                        tv_like.setText("");
                    }

                    if (model.isLiked()) {
                        img_like.setSelected(true);
                    } else {
                        img_like.setSelected(false);
                    }

                    // mAdapter.notifyDataSetChanged();
                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            boolean result = new UserWebService().productLike(model.getId(), tokken, model.isLiked());
                            Log.d("ProductGridAdapter", "Request Like result = " + result);
                        }
                    });
                    t.start();
                }
            });
			
			if (model.getOwner() == null ) {
				convertView.findViewById(R.id.owner_view).setVisibility(View.GONE);
				convertView.findViewById(R.id.inflater_body_product_grid_divider).setVisibility(View.GONE);
			} else {
			    
				ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.inflater_header_product_grid_image);
				UrlImageViewHelper.setUrlDrawable(imageAvatar, model.getOwner().getFacebook_avatar());

				TextView tv_name_owner = (TextView) convertView.findViewById(R.id.inflater_header_product_grid_tv_name);
				tv_name_owner.setText(model.getOwner().getFullName());
				
				View owner_view = convertView.findViewById(R.id.owner_view);
				owner_view.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,ProfileActivity.class);
                        String data=new Gson().toJson(model.getOwner());
                        intent.putExtra(CommonConstant.INTENT_USER_DATA, data);
                        mContext.startActivity(intent);
                        CommonUti.addAnimationWhenStartActivity((Activity)mContext);
                        
//                        Bundle animation=ActivityOptionsCompat.makeCustomAnimation(mContext, R.anim.anim_right_in, R.anim.anim_no_animation).toBundle();
//                        act.overridePendingTransition(R.anim.anim_right_in,R.anim.anim_no_animation);
//                        ActivityCompat.startActivity((Activity)mContext, intent, animation);
                    }
                });
			}
			TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_header_product_grid_tv_time);
			tv_time.setText(DateUtils.getRelativeTimeSpanString(model.getUpdateTime() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
			
			if (mContext != null && mLoginButton != null) {
				final int finalPosition = position;
				ImageView img_share = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_img_share);
				img_share.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new CommonUti().doShareProductOnFacebook(mContext, mLoginButton, mData.get(finalPosition));
					}
				});
			}
			
		}

	}

}
