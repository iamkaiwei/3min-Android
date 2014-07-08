package com.threemin.uti;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.threemin.model.ProductModel;

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
	
	//share product on facebook ********************************************************
	public static void doShareProductOnFacebook(Context context, LoginButton loginButton, ProductModel product) {
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
		} 
		
		if (session != null && session.isOpened()){
			doPostToWall(context, session, product);
		} else {
			Log.i("CommonUti", "session null or not opened");
		}
	}
	
	public static void doPostToWall(final Context context, final Session session, ProductModel product) {
		Log.i("CommonUti", "start doPostToWall");
		final String caption = "Check out " + product.getName() + " on 3mins app (available for Android and iOS)";
		final String imgURL = product.getImages().get(0).getOrigin();
		final String link = "https://play.google.com/store/apps/details?id=com.threemins";

		
		Bitmap bitmap = UrlImageViewHelper.getCachedBitmap(imgURL);
		if (bitmap != null) {
			Request request = Request.newUploadPhotoRequest(session, bitmap, new Callback() {
				
				@Override
				public void onCompleted(Response response) {
					// TODO Auto-generated method stub
					if (response.getError() == null) {
			        	Log.i("CommonUti", "doPostToWall done");
			        	Toast.makeText(context, "Post success", Toast.LENGTH_LONG).show();
			        }
				}
			});
			
			Bundle bundle = request.getParameters();
			bundle.putString("message", caption + "\n" + link);
			request.setParameters(bundle);
			request.executeAsync();
			Log.i("CommonUti", "request.executeAsync()");
		} else {
			Log.i("CommonUti", "bitmap null");
			UrlImageViewHelper.setUrlDrawable(new ImageView(context), imgURL, new UrlImageViewCallback() {
				
				@Override
				public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
						boolean loadedFromCache) {
					Request request = Request.newUploadPhotoRequest(session, loadedBitmap, new Callback() {
						
						@Override
						public void onCompleted(Response response) {
							// TODO Auto-generated method stub
							if (response.getError() == null) {
					        	Log.i("CommonUti", "Loaded, doPostToWall done");
					        	Toast.makeText(context, "Post success", Toast.LENGTH_LONG).show();
					        }
						}
					});
					
					Bundle bundle = request.getParameters();
					bundle.putString("message", caption + "\n" + link);
					request.setParameters(bundle);
					request.executeAsync();
					Log.i("CommonUti", "Loaded, request.executeAsync()");
				}
			});
		}
		
		
		Log.i("CommonUti", "end doPostToWall");
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
