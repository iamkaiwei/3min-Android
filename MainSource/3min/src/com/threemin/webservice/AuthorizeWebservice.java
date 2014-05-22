package com.threemin.webservice;

import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.threemin.model.UserModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;

public class AuthorizeWebservice {

	
	public UserModel login(String token,String deviceid,Context context, int tokenType) throws Exception{
		JSONObject header=new JSONObject();
		header.put(CommonConstant.KEY_CLIENT_ID, CommonConstant.CLIENT_ID);
		header.put(CommonConstant.KEY_CLIENT_SECRET, CommonConstant.CLIENT_SECRET);
		
		//check token type is facebook or google
		if (tokenType == CommonConstant.TYPE_GOOGLE_TOKEN) {
			header.put(CommonConstant.KEY_GG_TOKEN, token);
		} else {
			header.put(CommonConstant.KEY_FB_TOKEN, token);
		}
		
		header.put(CommonConstant.KEY_GRANT_TYPE, CommonConstant.GRANT_TYPE);
		header.put(CommonConstant.KEY_ID, deviceid);
		String result=WebServiceUtil.postJson(WebserviceConstant.LOGIN, header);
		JSONObject jsonResult=new JSONObject(result);
		PreferenceHelper.getInstance(context).setTokken(jsonResult.optString(CommonConstant.KEY_ACCESS_TOKEN));
		JSONObject jsonUser=jsonResult.optJSONObject(CommonConstant.KEY_USER);
		UserModel user=new Gson().fromJson(jsonUser.toString(), UserModel.class);
		PreferenceHelper.getInstance(context).setCurrentUser(jsonUser.toString());
		return user;
	}
}
