package com.threemin.receiver;

import com.threemin.app.HomeActivity;
import com.urbanairship.actions.ActionUtils;
import com.urbanairship.actions.DeepLinkAction;
import com.urbanairship.actions.LandingPageAction;
import com.urbanairship.actions.OpenExternalUrlAction;
import com.urbanairship.push.PushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class IntentReceiver extends BroadcastReceiver {
    // A set of actions that launch activities when a push is opened. Update
    // with any custom actions that also start activities when a push is opened.
    private static String[] ACTIVITY_ACTIONS = new String[] {
        DeepLinkAction.DEFAULT_REGISTRY_NAME,
        OpenExternalUrlAction.DEFAULT_REGISTRY_NAME,
        LandingPageAction.DEFAULT_REGISTRY_NAME
    };

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.i("IntentReceiver", "IntentReciever");

        if (PushManager.ACTION_PUSH_RECEIVED.equals(intent.getAction())) {
          // Push received
        } if (intent.getAction().equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            Log.i("tructran","Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)+ ". Valid: "+ intent.getBooleanExtra( PushManager.EXTRA_REGISTRATION_VALID, false));
            // Notify any app-specific listeners
            String apid = PushManager.shared().getAPID();
            Log.i("tructran", "Receiver: App ID: " + apid);
        } else if (PushManager.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            // Push opened

            // Only launch the main activity if the payload does not contain any
            // actions that might have already opened an activity
            if (!ActionUtils.containsRegisteredActions(intent.getExtras(), ACTIVITY_ACTIONS)) {
//            	Bundle bundle = intent.getExtras();
//            	String message = bundle.getString("Message");
//            	Log.i("tructran", "Push recieved: " + message);
                Intent launch = new Intent(Intent.ACTION_MAIN);
//                launch.putExtra("Message", message);
                launch.setClass(context, HomeActivity.class);
                launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launch);
            }
        }
    }
}
