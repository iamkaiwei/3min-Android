package com.threemin.receiver;

import java.util.Set;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.threemin.app.ChatToBuyActivity;
import com.threemin.app.DetailActivity;
import com.threemin.app.FeedbackActivity;
import com.threemin.app.FeedbackDialogActivity;
import com.threemin.app.ProfileActivity;
import com.threemin.app.ThreeMinsApplication;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.urbanairship.actions.ActionUtils;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.actions.OpenExternalUrlAction;
import com.urbanairship.push.PushManager;

public class IntentReceiver extends BroadcastReceiver {
    
    private static final String tag = "IntentReceiver";
    
    public static final String ACTION_NOTIFY_UPDATE_NUMBER_ACTIVITIES = "com.threemin.receiver.IntentReceiver.NotifyUpdateNumberActivities";

    // A set of actions that launch activities when a push is opened.  Update
    // with any custom actions that also start activities when a push is opened.
    private static String[] ACTIVITY_ACTIONS = new String[] {
        DeepLinkAction.DEFAULT_REGISTRY_NAME,
        OpenExternalUrlAction.DEFAULT_REGISTRY_NAME,
        LandingPageAction.DEFAULT_REGISTRY_NAME
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "Received intent: " + intent.toString());
        String action = intent.getAction();

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {

            int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

            Log.i(tag, "Received push notification. Alert: "
                    + intent.getStringExtra(PushManager.EXTRA_ALERT)
                    + " [NotificationID="+id+"]");
            
            //increase the number of unread activities
            int numActivities = PreferenceHelper.getInstance(context).getNumberActivities();
            Log.i(tag, "ACTION_PUSH_RECEIVED numActivities: " + numActivities);
            numActivities ++;
            PreferenceHelper.getInstance(context).setNumberActivities(numActivities);
            notifyUpdateNumberActivities(context);
            
            //TODO: testing - check if the app is in forceground.
            //if app is active: cancel the notification
            //else: do nothing
            if (ThreeMinsApplication.isActive) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(id);
                String msg = intent.getStringExtra(PushManager.EXTRA_ALERT);
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }

            logPushExtras(intent);

        } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

            Log.i(tag, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT));

            logPushExtras(intent);

            // Only launch the main activity if the payload does not contain any
            // actions that might have already opened an activity
            if (!ActionUtils.containsRegisteredActions(intent.getExtras(), ACTIVITY_ACTIONS)) {
                
                //decrease the number of unread activities
                int numActivities = PreferenceHelper.getInstance(context).getNumberActivities();
                Log.i(tag, "ACTION_NOTIFICATION_OPENED numActivities: " + numActivities);
                numActivities --;
                PreferenceHelper.getInstance(context).setNumberActivities(numActivities);
                notifyUpdateNumberActivities(context);
                
                Intent launch = createIntent(context, intent);
                launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launch);
            }

//            Intent launch = new Intent(Intent.ACTION_MAIN);
//            launch.setClass(UAirship.shared().getApplicationContext(), HomeActivity.class);
//            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            UAirship.shared().getApplicationContext().startActivity(launch);

        } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            Log.i(tag, "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)
                    + ". Valid: " + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));
        }
    }
    
    private void logPushExtras(Intent intent) {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {
            Log.i(tag, "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]\n");
        }
    }
    
    private Intent createIntent(Context context, Intent data) {
        Bundle extras = data.getExtras();
        Intent showAppIntent = new Intent();
        
        showAppIntent.putExtra(CommonConstant.INTENT_IS_FROM_PUSH_NOTIFICATION, true);
        
        //new field: type:
        int type = Integer.parseInt(extras.getString("notification_type"));
        Log.i(tag, "Notification type: " + type);
        
        switch (type) {
        case CommonConstant.TYPE_REMIND_BUYER_FEEDBACK:
            String productIDFeedback = extras.getString("product_id");
            String scheduleID = extras.getString("schedule_id");
            showAppIntent.putExtra(CommonConstant.INTENT_SCHEDULE_ID, scheduleID);
            showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productIDFeedback);
            showAppIntent.setClass(context, FeedbackActivity.class);
            break;

        case CommonConstant.TYPE_CHAT:
            String productIDChat = extras.getString("product_id");
            String conversationID = extras.getString("conversation_id");
            showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productIDChat);
            showAppIntent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA_VIA_ID, conversationID);
            showAppIntent.setClass(context, ChatToBuyActivity.class);
            break;

        case CommonConstant.TYPE_OFFER:
            String productIDOffer = extras.getString("product_id");
            String conversationIDOffer = extras.getString("conversation_id");
            showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productIDOffer);
            showAppIntent.putExtra(CommonConstant.INTENT_CONVERSATION_DATA_VIA_ID, conversationIDOffer);
            showAppIntent.setClass(context, ChatToBuyActivity.class);
            break;

        case CommonConstant.TYPE_LIKE:
            String productIDLiked = extras.getString("product_id");
            showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productIDLiked);
            showAppIntent.setClass(context, DetailActivity.class);
            break;

        case CommonConstant.TYPE_FOLLOW:
            String userIDFollow = extras.getString("user_id");
            showAppIntent.putExtra(CommonConstant.INTENT_USER_DATA_VIA_ID, userIDFollow);
            showAppIntent.setClass(context, ProfileActivity.class);
            break;

        case CommonConstant.TYPE_COMMENT:
            String productIDComment = extras.getString("product_id");
            showAppIntent.putExtra(CommonConstant.INTENT_PRODUCT_DATA_VIA_ID, productIDComment);
            showAppIntent.setClass(context, DetailActivity.class);
            break;

        case CommonConstant.TYPE_FEEDBACK:
            showAppIntent.setClass(context, FeedbackDialogActivity.class);
            break;

        default:
            break;
        }
        
        return showAppIntent;
    }
    
    private void notifyUpdateNumberActivities(Context context) {
        Intent intent = new Intent(ACTION_NOTIFY_UPDATE_NUMBER_ACTIVITIES);
        context.sendBroadcast(intent);
    }

}
