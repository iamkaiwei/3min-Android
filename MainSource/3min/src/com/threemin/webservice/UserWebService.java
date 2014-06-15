package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
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
				requestURL=requestURL+"?access_token="+tokken;
				result = WebServiceUtil.deleteJson(requestURL);
			}
			if (!TextUtils.isEmpty(result)) {
				Log.d("result", result);
				JSONObject jsonResult = new JSONObject(result);
				return jsonResult.getString("status").equals("success");
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("error", e.getMessage());
			return false;
		}
	}

	public List<ProductModel> getUserProduct(String accessToken, int page) throws Exception {
		String requestLink = WebserviceConstant.GET_USER_PRODUCT + "?access_token=" + accessToken;
		if (page > 0) {
			requestLink += "&page=" + page;
		}
		String result = WebServiceUtil.getData(requestLink);
		Type listType = new TypeToken<List<ProductModel>>() {
		}.getType();
		List<ProductModel> list = new Gson().fromJson(result, listType);
		return list;
	}

	public List<ProductModel> getUserLikedProduct(String accessToken, int page) throws Exception {
		String requestLink = WebserviceConstant.GET_USER_LIKED_PRODUCT + "?access_token=" + accessToken;
		if (page > 0) {
			requestLink += "&page=" + page;
		}
		String result = WebServiceUtil.getData(requestLink);
		Type listType = new TypeToken<List<ProductModel>>() {
		}.getType();
		List<ProductModel> list = new Gson().fromJson(result, listType);
		for (ProductModel productModel : list) {
			productModel.setLiked(true);
		}
		return list;
	}
}
