package com.threemin.uti;

import com.google.gson.Gson;
import com.threemin.model.UserModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PreferenceHelper {
	// default value
	public static String DEFAULT_STRING = "";
	public static final int DEFAULT_NUMBER = 0;
	public static final boolean DEFAULT_BOOLEAN = false;

	private static final String PREFERENCE_NAME = "threemins";
	protected static SharedPreferences settingPreferences;
	protected static PreferenceHelper instance = new PreferenceHelper();

	public static PreferenceHelper getInstance(Context context) {
		init(context);
		return instance;
	}

	private static void putString(String key, String value) {
		Editor editor = settingPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private static String getString(String key, String defaultValue) {
		return settingPreferences.getString(key, defaultValue);
	}

	private static void init(Context context) {
		if (settingPreferences == null) {
			settingPreferences = context.getSharedPreferences(PREFERENCE_NAME,
					Context.MODE_PRIVATE);
		}
	}

	private static void putInteger(String key, int value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	private static Long getLong(String key, long defaultValue) {
		if (settingPreferences != null) {
			return settingPreferences.getLong(key, defaultValue);
		} else
			return defaultValue;
	}

	private static void putLong(String key, long value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}
	
	private static double getDouble(String key, double defaultValue) {
		if (settingPreferences != null) {
			return (double)settingPreferences.getFloat(key, (float) defaultValue);
		} else
			return defaultValue;
	}

	private static void putDouble(String key, double value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putFloat(key, (float) value);
			editor.commit();
		}
	}

	private static Integer getInteger(String key, int defaultValue) {
		if (settingPreferences != null) {
			return settingPreferences.getInt(key, defaultValue);
		} else
			return defaultValue;
	}

	private static void putBoolean(String key, boolean value) {
		if (settingPreferences != null) {
			Editor editor = settingPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}

	private static Boolean getBoolean(String key, boolean defaultValue) {
		if (settingPreferences != null) {
			return settingPreferences.getBoolean(key, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public void setTokken(String tokken){
		putString(CommonConstant.PREF_TOKEN, tokken);
	}
	
	public String getTokken(){
		return getString(CommonConstant.PREF_TOKEN, "720da8de7f8f030804b6f169f2d1210b18223892ae590fd79db89db0cfa40d72"); //for testing only
	}
	
	public void setCurrentUser(String currentUser){
		putString(CommonConstant.PREF_USER, currentUser);
	}
	
	UserModel currentUserModel;
	public UserModel getCurrentUser(){
		if(currentUserModel!=null){
			return currentUserModel;
		} else {
			currentUserModel=new Gson().fromJson( getString(CommonConstant.PREF_USER, ""),UserModel.class);
			return currentUserModel;
		}
	}
}