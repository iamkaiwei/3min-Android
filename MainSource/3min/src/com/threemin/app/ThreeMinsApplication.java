package com.threemin.app;

import com.threemin.receiver.IntentReceiver;
import com.threemins.R;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

import android.app.Application;
import android.util.Log;

public class ThreeMinsApplication extends Application {
	
	public void onCreate() {
		
		AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
//		options.gcmSender = getString(R.string.gcmSender);
//	    options.transport = "gcm";
//	    options.developmentAppKey = getString(R.string.developmentAppKey);
//	    options.developmentAppSecret = getString(R.string.developmentAppSecret);
//	    options.productionAppKey = getString(R.string.productionAppKey);
//	    options.productionAppSecret = getString(R.string.productionAppSecret);
//	    options.inProduction = false;
//	    options.richPushEnabled = true;
		UAirship.takeOff(this, options);
		PushManager.enablePush();
		
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
	};

}
