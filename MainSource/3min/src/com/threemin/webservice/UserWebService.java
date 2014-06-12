package com.threemin.webservice;

import org.json.JSONObject;

import android.text.TextUtils;

import com.threemin.uti.WebserviceConstant;

public class UserWebService {

	public boolean productLike(int productid, String tokken, boolean isLike) {
		try {
			String requestURL = String.format(WebserviceConstant.LIKE, "" + productid);
			String result;
			JSONObject jsonTokken = new JSONObject();
			jsonTokken.put("access_token", tokken);
			if (isLike) {
				result = WebServiceUtil.postJson(requestURL, jsonTokken);
			} else {
				result = WebServiceUtil.deleteJson(requestURL, jsonTokken);
			}
			if (!TextUtils.isEmpty(result)) {
				JSONObject jsonResult=new JSONObject(result);
				return jsonResult.getString("status").equals("success");
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
