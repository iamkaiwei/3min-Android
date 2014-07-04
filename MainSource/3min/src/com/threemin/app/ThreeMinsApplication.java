package com.threemin.app;

import android.app.Application;

import com.threemin.receiver.ThreeMinsNotificationBuilder;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

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
		
		options.gcmSender = "1096090152384";
	    options.transport = "gcm";
	    options.developmentAppKey = "-IT-qB3pRBiec3liyxk4cQ";
	    options.developmentAppSecret = "rp9clmV1RzGi0GfPUiO0iA";
	    options.inProduction = false;
	    options.richPushEnabled = true;
		
		UAirship.takeOff(this, options);
		PushManager.enablePush();
		
		PushManager.shared().setNotificationBuilder(new ThreeMinsNotificationBuilder());
	};

}
