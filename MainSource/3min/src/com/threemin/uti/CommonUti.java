package com.threemin.uti;

import android.content.Context;
import android.provider.Settings.Secure;

public class CommonUti {

	public static String getDeviceId(Context context){
		return Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
	}
}
