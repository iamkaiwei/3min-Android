package com.threemin.receiver;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.threemin.app.HomeActivity;
import com.urbanairship.UAirship;
import com.urbanairship.actions.ActionUtils;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.actions.OpenExternalUrlAction;
import com.urbanairship.push.PushManager;

public class IntentReceiver extends BroadcastReceiver {
    
    private static final String logTag = "PushSample";

    // A set of actions that launch activities when a push is opened.  Update
    // with any custom actions that also start activities when a push is opened.
    private static String[] ACTIVITY_ACTIONS = new String[] {
        DeepLinkAction.DEFAULT_REGISTRY_NAME,
        OpenExternalUrlAction.DEFAULT_REGISTRY_NAME,
        LandingPageAction.DEFAULT_REGISTRY_NAME
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(logTag, "Received intent: " + intent.toString());
        String action = intent.getAction();

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {

            int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

            Log.i(logTag, "Received push notification. Alert: "
                    + intent.getStringExtra(PushManager.EXTRA_ALERT)
                    + " [NotificationID="+id+"]");
            
            //TODO: testing - need to check some conditions to cancel the notification
//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(id);

            logPushExtras(intent);

        } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

            Log.i(logTag, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT));

            logPushExtras(intent);

            // Only launch the main activity if the payload does not contain any
            // actions that might have already opened an activity
//            if (!ActionUtils.containsRegisteredActions(intent.getExtras(), ACTIVITY_ACTIONS)) {
//                Intent launch = new Intent(Intent.ACTION_MAIN);
//                launch.setClass(context, HomeActivity.class);
//                launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(launch);
//            }
//
//
//            Intent launch = new Intent(Intent.ACTION_MAIN);
//            launch.setClass(UAirship.shared().getApplicationContext(), HomeActivity.class);
//            launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            UAirship.shared().getApplicationContext().startActivity(launch);

        } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            Log.i(logTag, "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)
                    + ". Valid: " + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));
        }
    }
    
    private void logPushExtras(Intent intent) {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {
//
//            //ignore standard extra keys (GCM + UA)
//            List<String> ignoredKeys = (List<String>)Arrays.asList(
//                    "collapse_key",//GCM collapse key
//                    "from",//GCM sender
//                    PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
//                    PushManager.EXTRA_PUSH_ID,//internal UA push id
//                    PushManager.EXTRA_ALERT);//ignore alert
//            if (ignoredKeys.contains(key)) {
//                continue;
//            }
            Log.i(logTag, "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]");
        }
    }

}
