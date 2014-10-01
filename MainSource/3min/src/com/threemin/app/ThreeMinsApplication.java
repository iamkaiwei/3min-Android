package com.threemin.app;

import android.app.Application;

import com.threemin.receiver.IntentReceiver;
import com.threemin.receiver.ThreeMinsNotificationBuilder;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class ThreeMinsApplication extends Application {
	
	public void onCreate() {
		
		AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
		
		UAirship.takeOff(this, options);
		PushManager.enablePush();
		
		PushManager.shared().setNotificationBuilder(new ThreeMinsNotificationBuilder());
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
	};

}
