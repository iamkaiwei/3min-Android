package com.threemin.fragment;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.CommentActivity;
import com.threemin.app.DetailActivity;
import com.threemin.app.ListOfferActivty;
import com.threemin.app.ListUsersLikedActivity;
import com.threemin.app.PostOfferActivity;
import com.threemin.model.CommentModel;
import com.threemin.model.Conversation;
import com.threemin.model.ImageModel;
import com.threemin.model.ProductModel;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.CommonUti;
import com.threemin.uti.PreferenceHelper;
import com.threemin.webservice.CommentWebService;
import com.threemin.webservice.ConversationWebService;
import com.threemin.webservice.ProductWebservice;
import com.threemin.webservice.UserWebService;
import com.threemins.R;

public class DetailFragment extends Fragment {
    
    public static final String tag = "DetailFragment";
    
	private final int SHOW_DIALOG = 1;
	private final int HIDE_DIALOG = 2;
	private final int REQUEST_CHECK_OFFER_EXIST = 3;
	private final int REQUEST_GET_LIST_OFFER = 4;
	
	public static final String INTENT_PRODUCT_ID_FOR_COMMENT = "productIDForComment";
	public static final String INTENT_COMMENT_ACTION = "CommentAction";
	public static final String INTENT_JSON_INIT_DATA = "JsonInitData";
	public static final boolean ACTION_POST_COMMENT = true;
	public static final boolean ACTION_VIEW_COMMENTS = false;
	
	View rootView;
	ProductModel productModel;
	ViewPager pager;
	Button btnChatToBuy;
	LinearLayout lnImgs, btnLike;
	ProgressDialog dialog, dialogPushReceived;
	String conversationData;
	List<Conversation> conversations;
	
	//data is from prev activity or from webservice
	public final boolean IS_FROM_PREV_ACTIVITY = true;
	public final boolean IS_FROM_WEB_SERVICE = false;
	boolean mProductDataType;
	
	//test
	LinearLayout lnTopComments;
	TextView tvComment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Intent intent = getActivity().getIntent();
		
		String productID = intent.getStringExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID);
		Log.i("DetailFragment", "Product ID: " + productID);
		
		rootView = inflater.inflate(R.layout.fragment_detail, null);
		
		if (productID == null) {
		    mProductDataType = IS_FROM_PREV_ACTIVITY;
			productModel = new Gson().fromJson(getActivity().getIntent().getStringExtra(CommonConstant.INTENT_PRODUCT_DATA), ProductModel.class);
			initBody(rootView);
		} else {
		    mProductDataType = IS_FROM_WEB_SERVICE;
			rootView.setVisibility(View.INVISIBLE);
			dialogPushReceived = new ProgressDialog(getActivity());
			dialogPushReceived.setMessage(getString(R.string.please_wait));
			dialogPushReceived.show();
			new GetProductViaIdTask().execute(productID);
		}
		
		return rootView;
	}

	private void initBody(View convertView) {

		if (productModel != null) {
//			ImageView image = (ImageView) convertView.findViewById(R.id.inflater_body_product_grid_image);
			// if (productModel.getImages().size() > 0) {
			// UrlImageViewHelper.setUrlDrawable(image,
			// model.getImages().get(0).getMedium());
			// }

		    //product 's name
			TextView tv_name = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_name);
			tv_name.setText(productModel.getName());
			getActivity().setTitle(productModel.getName());

			//product 's price
			TextView tv_price = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_price);
			tv_price.setText(productModel.getPrice() + CommonConstant.CURRENCY);

			//product 's like number
			TextView tv_like = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_like);
			int numLike = productModel.getLike();
			if (numLike > 0) {
			    if (numLike == 1) {
			        tv_like.setText("" + productModel.getLike() + " " + getActivity().getString(R.string.fm_detail_1_person_liked));
                } else {
                    tv_like.setText("" + productModel.getLike() + " " + getActivity().getString(R.string.fm_detail_many_people_liked));
                }
			} else {
				tv_like.setVisibility(View.GONE);
			}
			tv_like.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    doGetListUsersLikedProduct();
                }
            });
			
			//location
			TextView tv_locaion = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_location);
			if (!TextUtils.isEmpty(productModel.getVenueName())) {
				tv_locaion.setText(productModel.getVenueName());
			} else {
				tv_locaion.setVisibility(View.GONE);
			}

			//description
			TextView tv_description = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_description);
			if (!TextUtils.isEmpty(productModel.getDescription())) {
				tv_description.setText(productModel.getDescription());
			} else {
				tv_description.setVisibility(View.GONE);
			}

			//owner 's info
			ImageView imageAvatar = (ImageView) convertView.findViewById(R.id.inflater_header_product_image);
			UrlImageViewHelper.setUrlDrawable(imageAvatar, productModel.getOwner().getFacebook_avatar());

			TextView tv_name_owner = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_name);
			tv_name_owner.setText(productModel.getOwner().getFullName());

			TextView tv_time = (TextView) convertView.findViewById(R.id.inflater_header_product_tv_time);
			tv_time.setText(DateUtils.getRelativeTimeSpanString(productModel.getUpdateTime() * 1000,
					System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE));

			//product images
			lnImgs = (LinearLayout) convertView.findViewById(R.id.ln_img);
			initImage();
			
			//button chat to buy
			btnChatToBuy = (Button) convertView.findViewById(R.id.fragment_detail_btn_chat_to_buy);
			UserModel currentUser = PreferenceHelper.getInstance(getActivity()).getCurrentUser();

			// if current user is not the owner of this product
			if (currentUser.getId() != productModel.getOwner().getId()) {
				btnChatToBuy.setBackgroundResource(R.drawable.selector_btn_chat_to_buy);
				btnChatToBuy.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						checkOffer();
					}
				});
			} else {
			    btnChatToBuy.setVisibility(View.INVISIBLE);
				checkListOffer();
				btnChatToBuy.setBackgroundResource(R.drawable.selector_btn_view_offers);
				btnChatToBuy.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String data = new Gson().toJson(productModel);
						Intent intent = new Intent(getActivity(), ListOfferActivty.class);
						intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
						intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversationData);
						getActivity().startActivity(intent);
					}
				});
			}
			
			//buton like (bottom)
			btnLike = (LinearLayout) convertView.findViewById(R.id.btn_like);
			btnLike.setSelected(productModel.isLiked());
			btnLike.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					requestLike();
				}
			});
			
			//button share (botton)
			LinearLayout lnShare = (LinearLayout) convertView.findViewById(R.id.fm_detail_ln_share);
			lnShare.setOnClickListener(new OnClickListener() {
			    
			    @Override
			    public void onClick(View v) {
			        new CommonUti().doShareProductOnFacebook(getActivity(), ( (DetailActivity)getActivity() ).getLoginButton(), productModel);
			    }
			});
			
			//button comment (bottom)
			LinearLayout lnComment = (LinearLayout) convertView.findViewById(R.id.fm_detail_ln_comment);
			lnComment.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    //TODO
//                    doCommentAction(ACTION_POST_COMMENT);
                    new GetInitCommentData(ACTION_POST_COMMENT).execute();
                }
            });
			
			//3 first comments:
			tvComment = (TextView) convertView.findViewById(R.id.inflater_body_product_tv_comment);
			tvComment.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    //TODO
//                    doCommentAction(ACTION_VIEW_COMMENTS);
                    new GetInitCommentData(ACTION_VIEW_COMMENTS).execute();
                }
            });
			lnTopComments = (LinearLayout) convertView.findViewById(R.id.inflater_body_product_lnl_top_comments);
			if (mProductDataType == IS_FROM_PREV_ACTIVITY || productModel.getComments() == null) {
                new GetTopCommentsTask().execute();
            } else {
                initListTopComments(productModel.getComments());
            }
		}
		

	}

	private void initImage() {
		for (ImageModel imageModel : productModel.getImages()) {
            final int imageViewWidth = CommonUti.getWidthInPixel(getActivity())
                    - getActivity().getResources().getDimensionPixelSize(R.dimen.common_spacing) * 2;
            List<Integer> dimens = imageModel.getDimensions();
            
            if (dimens != null && dimens.size() > 1) {
                int imageWidth = dimens.get(0);
                int imageHeight = dimens.get(1);
                float ratio = (float) imageViewWidth / (float) imageWidth;
                int imageViewHeight = (int) (imageHeight * ratio);
                
                ImageView imageView = new ImageView(getActivity());
                LayoutParams params = new LayoutParams(imageViewWidth, imageViewHeight);
                int spacing = (int) getResources().getDimension(R.dimen.common_spacing);
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(0, spacing, 0, spacing);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ScaleType.FIT_XY);
                lnImgs.addView(imageView);
                UrlImageViewHelper.setUrlDrawable(imageView, imageModel.getMedium(), R.drawable.stuff_img);
            } else {
                ImageView imageView = new ImageView(getActivity());
                
                UrlImageViewHelper.setUrlDrawable(imageView, imageModel.getMedium(), R.drawable.stuff_img, new UrlImageViewCallback() {
                    
                    @Override
                    public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                        int imageWidth = loadedBitmap.getWidth();
                        int imageHeight = loadedBitmap.getHeight();
                        float ratio = (float) imageViewWidth / (float) imageWidth;
                        int imageViewHeight = (int) (imageHeight * ratio);
                        
                        LayoutParams params = new LayoutParams(imageViewWidth, imageViewHeight);
                        int spacing = (int) getResources().getDimension(R.dimen.common_spacing);
                        imageView.setAdjustViewBounds(true);
                        imageView.setPadding(0, spacing, 0, spacing);
                        imageView.setLayoutParams(params);
                        imageView.setScaleType(ScaleType.FIT_XY);
                        lnImgs.addView(imageView);
                    }
                });
            }
            
		}
	}

	private void requestLike() {
		final String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();

		productModel.setLiked(!productModel.isLiked());
		if (productModel.isLiked()) {
			productModel.setLike(productModel.getLike() + 1);
		} else {
			productModel.setLike(productModel.getLike() - 1);
		}

		if (productModel.isLiked()) {
			btnLike.setSelected(true);
		} else {
			btnLike.setSelected(false);
		}

		// mAdapter.notifyDataSetChanged();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				boolean result = new UserWebService().productLike(productModel.getId(), tokken, productModel.isLiked());
				Log.d("result", "result=" + result);
			}
		});
		t.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_DIALOG:
				dialog = new ProgressDialog(getActivity());
				dialog.setMessage(getString(R.string.please_wait));
				dialog.show();
				break;
			case HIDE_DIALOG:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				break;
			case REQUEST_CHECK_OFFER_EXIST:
				Conversation conversation = (Conversation) msg.obj;
				String data = new Gson().toJson(productModel);
				if (conversation == null || conversation.getId() == 0) {
					Intent intent = new Intent(getActivity(), PostOfferActivity.class);
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					getActivity().startActivity(intent);
				} else {
					String conversationData = new Gson().toJson(conversation);
					Intent intent = new Intent(getActivity(), ChatToBuyActivity.class);
					intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA, data);
					intent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA, conversationData);
					getActivity().startActivity(intent);
				}
				break;
			case REQUEST_GET_LIST_OFFER:
				if(conversations==null || conversations.isEmpty()){
					btnChatToBuy.setBackgroundResource(R.drawable.selector_btn_no_offer_yet);
				} else if (conversations.size() == 1) {
                    btnChatToBuy.setText(getActivity().getString(R.string.fm_detail_view_1_offer));
                } else {
                    String str = String.format(getActivity().getString(R.string.fm_detail_view_many_offers), "" + conversations.size()) ;
                    btnChatToBuy.setText(str);
                }
				btnChatToBuy.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};

	private void checkOffer() {
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
				int product_id = productModel.getId();
				int to = productModel.getOwner().getId();
				Conversation conversation = new ConversationWebService().getConversation(tokken, product_id, to);
				Message msg = new Message();
				msg.what = REQUEST_CHECK_OFFER_EXIST;
				msg.obj = conversation;
				mHandler.sendEmptyMessage(HIDE_DIALOG);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}
	
	private void checkListOffer(){
		mHandler.sendEmptyMessage(SHOW_DIALOG);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
				int product_id = productModel.getId();
				conversationData = new ProductWebservice().getListOffer(tokken, product_id);
				conversations=new ProductWebservice().parseListConversation(conversationData);
				Message msg = new Message();
				msg.what = REQUEST_GET_LIST_OFFER;
				msg.obj = conversations;
				mHandler.sendEmptyMessage(HIDE_DIALOG);
				mHandler.sendMessage(msg);
			}
		});
		t.start();
	}
	
	private class GetProductViaIdTask extends AsyncTask<String, Void, ProductModel> {

		@Override
		protected ProductModel doInBackground(String... params) {
			String tokken = PreferenceHelper.getInstance(getActivity()).getTokken();
			return new ProductWebservice().getProductViaID(tokken, params[0]);
		}
		
		@Override
		protected void onPostExecute(ProductModel result) {
			super.onPostExecute(result);
			if (rootView.getVisibility() == View.INVISIBLE) {
				rootView.setVisibility(View.VISIBLE);
			}
			if (dialogPushReceived != null && dialogPushReceived.isShowing()) {
				dialogPushReceived.dismiss();
			}
			if (result != null) {
				productModel = result;
				initBody(rootView);
			}
		}
		
	}
	
	/**
	 * View all comments or post comment
	 * @param commentAction = ACTION_VIEW_COMMENTS(false) or ACTION_POST_COMMENT(true)
	 * */
	public void doCommentAction(boolean commentAction) {
	    Intent intent = new Intent(getActivity(), CommentActivity.class);
	    intent.putExtra(INTENT_PRODUCT_ID_FOR_COMMENT, productModel.getId());
	    intent.putExtra(INTENT_COMMENT_ACTION, commentAction);
	    startActivity(intent);
	    CommonUti.addAnimationWhenStartActivity(getActivity());
	}
	
	private class GetTopCommentsTask extends AsyncTask<Void, Void, List<CommentModel>> {

        @Override
        protected List<CommentModel> doInBackground(Void... params) {
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            if (productModel != null) {
                return new CommentWebService().getTopComments(token, productModel.getId());
            } else {
                Log.i(tag, "GetTopCommentsTask product null");
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(List<CommentModel> result) {
            if (result != null && result.size() != 0) {
                initListTopComments(result);
            }
            super.onPostExecute(result);
        }
        
    }
	
	public void initListTopComments(List<CommentModel> list) {
	    //we have to remove all views bc when onResume of the activity is called, we will add 3 newest comments
	    
	    //first check if have comment or not
	    if (list != null && list.size() > 0) {
	        lnTopComments.removeAllViews();
	        for (int i = 0; i < list.size(); i++) {
	            LayoutInflater inflater = LayoutInflater.from(getActivity());
	            View view = inflater.inflate(R.layout.fragment_detail_layout_comment, null);
	            addDataToView(view, list.get(i));
	            lnTopComments.addView(view);
	        }
        } else {
            lnTopComments.setVisibility(View.GONE);
            tvComment.setVisibility(View.GONE);
        }
	}
	
	public void addDataToView (View view, CommentModel model) {
	    ImageView imgAvatar = (ImageView) view.findViewById(R.id.fm_detail_layout_comment_avatar);
	    TextView tvName = (TextView) view.findViewById(R.id.fm_detail_layout_comment_tv_name);
	    TextView tvConent = (TextView) view.findViewById(R.id.fm_detail_layout_comment_tv_comment);
	    TextView tvTime = (TextView) view.findViewById(R.id.fm_detail_layout_comment_tv_time);
	    
	    UrlImageViewHelper.setUrlDrawable(imgAvatar, model.getUser().getFacebook_avatar(), R.drawable.stuff_img);
	    tvName.setText(model.getUser().getFullName());
	    tvConent.setText(model.getContent());
	    CharSequence time = DateUtils.getRelativeTimeSpanString(model.getUpdated_at() * 1000,
                System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_RELATIVE);
	    tvTime.setText(time);
	}
	
	public void refreshTopComment() {
	    new GetTopCommentsTask().execute();
	}
	
	private class GetInitCommentData extends AsyncTask<Void, Void, String> {
	    
	    private boolean commentAction;
	    private ProgressDialog dialog;
	    
	    public GetInitCommentData(boolean action) {
	        commentAction = action;
        }
	    
	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	        dialog = new ProgressDialog(getActivity());
	        dialog.setMessage(getResources().getString(R.string.please_wait));
	        dialog.show();
	    }

        @Override
        protected String doInBackground(Void... params) {
            String token = PreferenceHelper.getInstance(getActivity()).getTokken();
            return new CommentWebService().getJSONComments(token, productModel.getId());
        }
        
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result != null && result.length() > 0) {
                doStartCommentActivity(commentAction, result);
            }
        }
        
	}
	
	public void doStartCommentActivity(boolean commentAction, String result) {
	    Intent intent = new Intent(getActivity(), CommentActivity.class);
        intent.putExtra(INTENT_PRODUCT_ID_FOR_COMMENT, productModel.getId());
        intent.putExtra(INTENT_COMMENT_ACTION, commentAction);
        intent.putExtra(INTENT_JSON_INIT_DATA, result);
        startActivity(intent);
        CommonUti.addAnimationWhenStartActivity(getActivity());
	}
	
	public void doGetListUsersLikedProduct() {
	    Intent intent = new Intent(getActivity(), ListUsersLikedActivity.class);
	    intent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productModel.getId());
	    startActivity(intent);
	    CommonUti.addAnimationWhenStartActivity(getActivity());
	}
	
}
