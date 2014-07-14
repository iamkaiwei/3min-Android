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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
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

	public static OnClickListener shareFacebook() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						"Hi Guys, Try this app https://play.google.com/store/apps/details?id=com.threemins");
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
						"Hi Guys, Try this app https://play.google.com/store/apps/details?id=com.threemins");
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
						"Hi Guys, Try this app https://play.google.com/store/apps/details?id=com.threemins");
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
	public static final int HIDE_LOADING_DIALOG = 2;
	public static final int LOAD_IMG = 3;
	public List<Bitmap> mListBitmap = new ArrayList<Bitmap>();
	public int mNumBitmap;
	public Context mShareContext;
	public ProgressDialog mProgressDialog;
	
	public Handler mShareHandler = new Handler() {
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
			case LOAD_IMG:
				mListBitmap.add((Bitmap)msg.obj);
				if (mListBitmap.size() == mNumBitmap) {
					doShowShareDialog(mShareContext, mListBitmap);
				}
				break;
			case SHOW_LOADING_DIALOG:
				mProgressDialog = new ProgressDialog(mShareContext);
				mProgressDialog.setMessage(mShareContext.getResources().getString(R.string.please_wait));
				mProgressDialog.show();
				break;
				
			case HIDE_LOADING_DIALOG:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
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
			builder.setTitle("Login Facebook");
			builder.setMessage("Please login Facebook to share on it.");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							loginBtn.performClick();
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("Cancel",
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
			mShareContext = context;
			for (int i = 0; i < mNumBitmap; i++) {
				final String url = product.getImages().get(i).getOrigin();
				Thread threadLoadImage = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Bitmap cachedBitmap = UrlImageViewHelper.getCachedBitmap(url);
						if (cachedBitmap != null) {
							mShareHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);
							Message msg = new Message();
							msg.what = LOAD_IMG;
							msg.obj = cachedBitmap;
							mShareHandler.sendMessage(msg);
						} else {
							UrlImageViewHelper.setUrlDrawable(new ImageView(mShareContext), url, new UrlImageViewCallback() {
								
								@Override
								public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
										boolean loadedFromCache) {
									mShareHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);
									Message msg = new Message();
									msg.what = LOAD_IMG;
									msg.obj = loadedBitmap;
									mShareHandler.sendMessage(msg);
								}
							});
						}
					}
				});
				threadLoadImage.start();
			}
			
//			Thread threadTimeOut = new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					mShareHandler.sendEmptyMessageDelayed(TIME_OUT, 10000);
//				}
//			});
//			threadTimeOut.start();
		} else {
			Toast.makeText(context, "Please install Facebook for Android to share easier!", Toast.LENGTH_LONG).show();
		}
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
}
