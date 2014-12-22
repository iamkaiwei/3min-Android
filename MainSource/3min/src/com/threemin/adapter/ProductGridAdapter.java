package com.threemin.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.threemin.app.ProfileActivity;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class ProductGridAdapter extends BaseAdapter {
    
    static class ViewHolder {
        public ImageView ivProduct;
        public TextView tvProductName;
        public TextView tvProductPrice;
        public TextView tvProductLike;
        public TextView tvProductComment;
        public ImageView ivLikeIcon;
        public LinearLayout llGroupLike;
        public View vOwnerView;
        public View vDevider;
        public ImageView ivOwnerAvatar;
        public TextView tvOwnerName;
        public TextView tvOwnerTime;
        public ImageView ivShareIcon;
    }
    
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
	    if (mData == null) {
            return null;
        }
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflater_body_product_grid, null);
            
            ViewHolder vh = new ViewHolder();
            vh.ivProduct = (ImageView) convertView.findViewById(R.id.inflater_product_iv_product_image);
            vh.tvProductName = (TextView) convertView.findViewById(R.id.inflater_product_tv_product_name);
            vh.tvProductPrice = (TextView) convertView.findViewById(R.id.inflater_product_tv_product_price);
            vh.tvProductLike = (TextView) convertView.findViewById(R.id.inflater_product_tv_product_like);
            vh.tvProductComment = (TextView) convertView.findViewById(R.id.inflater_product_tv_product_comment);
            vh.ivLikeIcon = (ImageView) convertView.findViewById(R.id.inflater_product_iv_like);
            vh.llGroupLike = (LinearLayout) convertView.findViewById(R.id.inflater_product_ll_group_like);
            vh.vOwnerView = convertView.findViewById(R.id.inflater_product_ll_owner_view);
            vh.vDevider = convertView.findViewById(R.id.inflater_product_v_divider);
            vh.ivOwnerAvatar = (ImageView) convertView.findViewById(R.id.inflater_product_iv_owner_avatar);
            vh.tvOwnerName =     (TextView) convertView.findViewById(R.id.inflater_product_tv_owner_time);
            vh.tvOwnerTime = (TextView) convertView.findViewById(R.id.inflater_product_tv_owner_time);
            vh.ivShareIcon = (ImageView) convertView.findViewById(R.id.inflater_product_iv_share_icon);
            
            convertView.setTag(vh);
        }
	    
	    initBody(position, convertView);
	    
		return convertView;
	}

	private void initBody(int position, View convertView) {
		if (mData == null || mData.size() == 0) {
			return;
		}
		final ProductModel model = mData.get(position);
		final ViewHolder vh = (ViewHolder) convertView.getTag();

		if (model != null) {
			if (model.getImages().size() > 0) {
//			    Log.d("image", "height="+image.getHeight());
			    List<Integer> dimens = model.getImages().get(0).getDimensions();
			    if (dimens != null && dimens.size() == 2) {
			        Log.i("dimen", "size: " + dimens.size());
			        int w = model.getImages().get(0).getDimensions().get(0);
			        int h = model.getImages().get(0).getDimensions().get(1);
			        float ratio = (float)this.widthItem / (float)w;
			        int heightItem = (int) (h * ratio);
                    LayoutParams param = vh.ivProduct.getLayoutParams();
                    param.height = heightItem;
                    vh.ivProduct.setLayoutParams(param);
                    UrlImageViewHelper.setUrlDrawable(vh.ivProduct, model.getImages().get(0).getMedium(),R.drawable.stuff_img);
                } else {
                    UrlImageViewHelper.setUrlDrawable(vh.ivProduct, model.getImages().get(0).getMedium(), R.drawable.stuff_img, new UrlImageViewCallback() {
                        
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
			}

			vh.tvProductName.setText(model.getName());

			vh.tvProductPrice.setText(model.getPrice() + CommonConstant.CURRENCY);

			if (model.getLike() > 0) {
				vh.tvProductLike.setText("" + model.getLike());
			} else {
			    vh.tvProductLike.setText("");
			}
			
			if (model.getCommentsCount() > 0) {
                vh.tvProductComment.setText("" + model.getCommentsCount());
            } else {
                vh.tvProductComment.setText("");
            }

			if (model.isLiked()) {
				vh.ivLikeIcon.setSelected(true);
			} else {
			    vh.ivLikeIcon.setSelected(false);
			}
			
			//allow user to like or unlike a product when tap on icon like
			vh.llGroupLike.setOnClickListener(new OnClickListener() {
                
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
                        vh.tvProductLike.setText("" + model.getLike());
                    } else {
                        vh.tvProductLike.setText("");
                    }

                    if (model.isLiked()) {
                        vh.ivLikeIcon.setSelected(true);
                    } else {
                        vh.ivLikeIcon.setSelected(false);
                    }

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
				vh.vOwnerView.setVisibility(View.GONE);
				vh.vDevider.setVisibility(View.GONE);
			} else {
				UrlImageViewHelper.setUrlDrawable(vh.ivOwnerAvatar, model.getOwner().getFacebook_avatar(), R.drawable.avatar_loading);
				vh.tvOwnerName.setText(model.getOwner().getFullName());
				vh.vOwnerView.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,ProfileActivity.class);
                        String data=new Gson().toJson(model.getOwner());
                        intent.putExtra(CommonConstant.INTENT_USER_DATA, data);
                        mContext.startActivity(intent);
                        CommonUti.addAnimationWhenStartActivity((Activity)mContext);
                    }
                });
			}
			vh.tvOwnerTime.setText(DateUtils.getRelativeTimeSpanString(model.getUpdateTime() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));
			
			if (mContext != null && mLoginButton != null) {
				final int finalPosition = position;
				vh.ivShareIcon.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new CommonUti().doShareProductOnFacebook(mContext, mLoginButton, mData.get(finalPosition));
					}
				});
			}
			
		}

	}

}
