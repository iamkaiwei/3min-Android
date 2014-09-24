package com.threemin.uti;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ProductModel;
import com.threemins.R;

public class CommonUti {

	public static String getDeviceId(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static OnClickListener shareFacebook(final Context context) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						context.getString(R.string.share_three_min_info));
				PackageManager pm = v.getContext().getPackageManager();
				List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
				for (final ResolveInfo app : activityList) {
					if ((app.activityInfo.name).contains("facebook")) {
						final ActivityInfo activity = app.activityInfo;
						final ComponentName name = new ComponentName(activity.applicationInfo.packageName,
								activity.name);
						shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						shareIntent.setComponent(name);
						v.getContext().startActivity(shareIntent);
						break;
					}
				}
			}
		};
	}

	public static OnClickListener shareEmail(final Context context) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				i.putExtra(Intent.EXTRA_SUBJECT, "Three mins");
				i.putExtra(Intent.EXTRA_TEXT,
				        context.getString(R.string.share_three_min_info));
				try {
					context.startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				}
			}
		};
	}
	
	public static OnClickListener feedbackClick(final Context context){
	    return new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[] { "iamkaiwei@gmail.com" });
                i.putExtra(Intent.EXTRA_SUBJECT, "Three mins Sugesstion");
                i.putExtra(Intent.EXTRA_TEXT,
                        "");
                try {
                    context.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                }
            }
        };

	}

	public static OnClickListener shareMessage(final Context context) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri smsUri = Uri.parse("sms:");
				Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
				if(intent!=null){
				intent.setType("vnd.android-dir/mms-sms");
				intent.putExtra("sms_body",
				        context.getString(R.string.share_three_min_info));
				context.startActivity(intent);
				}
			}
		};
	}
	
	public static int getWidthInPixel(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics ();
		display.getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
	
	public static void showKeyboard(EditText editText, Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public static void hideKeyboard(View stuff, Context context){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(
		      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(stuff.getWindowToken(), 0);
	}
	//TODO
	//share product on facebook ********************************************************
	public static final int SHOW_LOADING_DIALOG = 1;
	public static final int LOAD_IMG = 3;
	public static final int LOAD_IMG_FAIL = 4;
	public static final int MAX_RETRY_TIMES = 3;
	public List<Bitmap> mListBitmap = new ArrayList<Bitmap>();
	public int mNumBitmap;
	public int mNumBitmapLoaded;
	public Context mShareContext;
	public ProductModel mProducModel;
	public ProgressDialog mProgressDialog;
	public int[] mRetryCount ;
	public class UrlPosition {
	    public String url;
	    public int position;
	}
	
	public Handler mShareHandler = new Handler() {
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
			case LOAD_IMG:
				mListBitmap.add((Bitmap)msg.obj);
				mNumBitmapLoaded ++;
				if (mNumBitmapLoaded == mNumBitmap && mListBitmap.size() > 0) {
				    if (mProgressDialog != null && mProgressDialog.isShowing()) {
	                    mProgressDialog.dismiss();
	                }
					doShowShareDialog(mShareContext, mListBitmap);
				}
				break;
				
			case LOAD_IMG_FAIL:
			    UrlPosition urlPos = (UrlPosition) msg.obj;
			    mRetryCount[urlPos.position] ++;
			    Log.i("LoadBmp", "retry: " + mRetryCount[urlPos.position]);
			    if (mRetryCount[urlPos.position] < MAX_RETRY_TIMES) {
                    loadBimapToLocal(urlPos.position, urlPos.url);
                } else {
                    mNumBitmapLoaded++;
                    if (mNumBitmapLoaded == mNumBitmap) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        if (mListBitmap.size() == 0) {
                            mListBitmap.add(UrlImageViewHelper.getCachedBitmap(mProducModel.getImages().get(0).getMedium()));
                            Toast.makeText(mShareContext, mShareContext.getText(R.string.cannot_load_image_error), Toast.LENGTH_LONG).show();
                        }
                        if (mListBitmap.size() != 0) {
                            doShowShareDialog(mShareContext, mListBitmap);
                        }
                    }
                }
                break;
				
			case SHOW_LOADING_DIALOG:
				mProgressDialog = new ProgressDialog(mShareContext);
				mProgressDialog.setMessage(mShareContext.getResources().getString(R.string.please_wait));
				mProgressDialog.show();
				break;
				
			default:
				break;
			}
		}
	};
	
	
	public void doShareProductOnFacebook(Context context, LoginButton loginButton, ProductModel product) {
		// check logged in or not:
		final LoginButton loginBtn = loginButton;
		Session session = Session.getActiveSession();
		if (session == null || !session.isOpened()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(context.getString(R.string.activity_imageview_facebook_dialog_title));
			builder.setMessage(context.getString(R.string.activity_imageview_facebook_dialog_message));
			builder.setPositiveButton(context.getString(R.string.activity_imageview_facebook_dialog_btn_ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							loginBtn.performClick();
							dialog.dismiss();
						}
					});
			builder.setNegativeButton(context.getString(R.string.activity_imageview_facebook_dialog_btn_cancel),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.show();
		} else {
			showShareDialog(context, session, product);
		}
	}
	
	public void showShareDialog(final Context context, final Session session, final ProductModel product) {
		if (FacebookDialog.canPresentShareDialog(context,FacebookDialog.ShareDialogFeature.PHOTOS)) {
			mShareHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);
			mNumBitmap = product.getImages().size();
			mNumBitmapLoaded = 0;
			mShareContext = context;
			mProducModel = product;
			mRetryCount = new int[] {0, 0, 0, 0};
			for (int i = 0; i < mNumBitmap; i++) {
			    Log.i("LoadBmp", "loop: "  + i);
				final String url = product.getImages().get(i).getOrigin();
				loadBimapToLocal(i, url);
			}
		} else {
			Toast.makeText(context, context.getString(R.string.facebook_app_missed_error), Toast.LENGTH_LONG).show();
		}
	}
	
	public void loadBimapToLocal(final int position, final String url) {
	    Log.i("LoadBmp", "Pos: " + position + " url: " + url);
	    Thread threadLoadImage = new Thread(new Runnable() {
            
            @Override
            public void run() {
                Bitmap cachedBitmap = UrlImageViewHelper.getCachedBitmap(url);
                if (cachedBitmap != null) {
                    Message msg = new Message();
                    msg.what = LOAD_IMG;
                    msg.obj = cachedBitmap;
                    mShareHandler.sendMessage(msg);
                } else {
                    UrlImageViewHelper.setUrlDrawable(new ImageView(mShareContext), url, new UrlImageViewCallback() {
                        
                        @Override
                        public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
                                boolean loadedFromCache) {
                            if (loadedBitmap != null) {
                                Message msg = new Message();
                                msg.what = LOAD_IMG;
                                msg.obj = loadedBitmap;
                                mShareHandler.sendMessage(msg);
                                Log.i("LoadBmp", "Pos: " + position + " success");
                            } else {
                                Message msg = new Message();
                                UrlPosition urlPos = new UrlPosition();
                                urlPos.position = position;
                                urlPos.url = url;
                                msg.obj = urlPos;
                                msg.what = LOAD_IMG_FAIL;
                                mShareHandler.sendMessage(msg);
                                Log.i("LoadBmp", "Pos: " + position + " fail");
                            }
                        }
                    });
                }
            }
        });
        threadLoadImage.start();
	}
	
	public static void doShowShareDialog(Context context, List<Bitmap> data) {
		 FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder((Activity)context)
		 									.addPhotos(data)
		 									.build();
		 shareDialog.present();
	}
	
	//share product on facebook ********************************************************
	
	public static String bundle2String(Bundle bundle) {
	    String string = "Bundle{";
	    for (String key : bundle.keySet()) {
	        string += " " + key + " => " + bundle.get(key) + ";\n";
	    }
	    string += " }Bundle";
	    return string;
	}
	
	public static int calcWidthItem(Context context){
	    if (context == null) {
            Log.i("CommonUti", "Context null");
            return 0;
        }
	        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	        Display display = wm.getDefaultDisplay();
	        Point point = new Point();
	        display.getSize(point);
	        int width = point.x - context.getResources().getDimensionPixelSize(R.dimen.fragment_product_spacing) * 3;
	        width = width / 2;
	        Log.d("width", "width=" + width);
	        return width;
	}
	
	public static void addAnimationWhenStartActivity(Activity act) {
	    act.overridePendingTransition(R.anim.anim_right_in,R.anim.anim_no_animation);
	}
}
