package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.ProductModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.WebserviceConstant;

public class RelationshipWebService {
	
	public final String tag = "RelationshipWebService";

	public String followUser(String token, int userID) {
		try {
			String url = WebserviceConstant.FOLLOW_USER;
			Log.i(tag, "followUser url: " + url);
			JSONObject data = new JSONObject();
			data.put(CommonConstant.KEY_ACCESS_TOKEN, token);
			data.put(CommonConstant.KEY_USER_ID, userID);
			String result = WebServiceUtil.postJson(url, data);
			Log.i(tag, "followUser result: " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(tag, "followUser ex: " + e.toString());
		}
		
		return null;
	}
	
	public String unfollowUser(String token, int userID) {
		try {
			String url = WebserviceConstant.UNFOLLOW_USER + "/?access_token=" + token + "&user_id=" + userID;
			Log.i(tag, "unfollowUser url: " + url);
			String result = WebServiceUtil.deleteJson(url);
			Log.i(tag, "unfollowUser result: " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(tag, "unfollowUser ex: " + e.toString());
		}
		return null;
	}
	
	public List<ProductModel> getProductsFollowed(String token, int page) {
		String url = WebserviceConstant.GET_PRODUCT_FOLLOWED + "/?access_token=" + token + "&page=" + page;
		Log.i(tag, "getProductsFollowed url" + url);
		try {
			String result = WebServiceUtil.getData(url);
			Log.i(tag, "getProductsFollowed result" + result);
			Type listType = new TypeToken<List<ProductModel>>() {
	        }.getType();
	        List<ProductModel> list = new Gson().fromJson(result, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(tag, "getProductsFollowed ex" + e.toString());
		}
		return null;
	}
	
}
