package com.threemin.receiver;

import java.util.Map;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.DetailActivity;
import com.threemin.app.HomeActivity;
import com.threemin.uti.CommonConstant;
import com.threemins.R;
import com.urbanairship.UAirship;
import com.urbanairship.push.BasicPushNotificationBuilder;

public class ThreeMinsNotificationBuilder extends BasicPushNotificationBuilder {

	@Override
	public Notification buildNotification(String alert, Map<String, String> extras) {
		Notification notification = buildBasicNotification(alert, extras);
		if (notification == null) {
			return super.buildNotification(alert, extras);
		} else {
			return notification;
		}
	}

	private Notification buildBasicNotification(String alert, Map<String, String> extras) {
		Context context = UAirship.shared().getApplicationContext();
		if (context != null) {
			
			String data = "Extras content:\n";
			for (String key : extras.keySet()) {
				data += key + " : " + extras.get(key);
			}
			
			Log.i("ThreeMinsNotificationBuilder", data);
			
			int iconId = R.drawable.ic_launcher;
			Bitmap oriIcon = BitmapFactory.decodeResource(context.getResources(), iconId);
			int height = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
			int width = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
			Bitmap largeIcon = Bitmap.createScaledBitmap(oriIcon, width, height, false);
			oriIcon.recycle();
			NotificationCompat.Builder builder = new Builder(context);
			Intent showAppIntent = new Intent();
			
			String productID = extras.get("product_id");
			String conversationID = extras.get("conversation_id");
			
			Log.i("ThreeMinsNotificationBuilder", "Key: " + "product_id: " + productID + "conversation_id: " + conversationID);
			
			if (productID != null && productID.length() > 0) {
				if (conversationID != null && conversationID.length() > 0) {
					showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productID);
					showAppIntent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA_VIA_ID, conversationID);
					showAppIntent.setClass(context, ChatToBuyActivity.class);
				} else {
					showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productID);
					showAppIntent.setClass(context, DetailActivity.class);
				}
			} else {
				showAppIntent.setClass(context, HomeActivity.class);
			}
//			showAppIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

			builder.setContentTitle(context.getString(R.string.app_name))
					.setContentText(alert).setSmallIcon(iconId)
					.setLargeIcon(largeIcon).setAutoCancel(true)
					.setDefaults(Notification.DEFAULT_ALL)
			 		.setContentIntent(PendingIntent.getActivity(context, 0, showAppIntent, Intent.FLAG_ACTIVITY_NEW_TASK));
			return builder.build();
		}
		return null;
	}
}
