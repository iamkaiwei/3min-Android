package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.ProductModel;
import com.threemin.model.UserModel;
import com.threemin.uti.WebserviceConstant;

public class UserWebService {
	
	public final String tag = "UserWebService";

    public boolean productLike(int productid, String tokken, boolean isLike) {
        try {
            String requestURL = String.format(WebserviceConstant.LIKE, "" + productid);
            String result;
            JSONObject jsonTokken = new JSONObject();
            jsonTokken.put("access_token", tokken);
            if (isLike) {
                result = WebServiceUtil.postJson(requestURL, jsonTokken);
            } else {
                requestURL = requestURL + "?access_token=" + tokken;
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

    public List<ProductModel> getMyProduct(String accessToken, int page) throws Exception {
        String requestLink = WebserviceConstant.GET_MY_PRODUCT + "?access_token=" + accessToken;
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

    public List<ProductModel> getUserProduct(String accessToken, int userId, int page) throws Exception {
        String requestLink = String.format(WebserviceConstant.GET_USER_PRODUCT, "" + userId);
        requestLink = requestLink + "?access_token=" + accessToken;
        if (page > 0) {
            requestLink += "&page=" + page;
        }
        String result = WebServiceUtil.getData(requestLink);
        Type listType = new TypeToken<List<ProductModel>>() {
        }.getType();
        List<ProductModel> list = new Gson().fromJson(result, listType);
        return list;
    }
    
    
    //http://threemins-server-staging.herokuapp.com/api/v1/users/6.json/?access_token=9932cdeb3494585d093e847ae2f1fed1f0f57bf0e638d1d1d18f84a8adb962df
    public UserModel getUserViaId(String accessToken, String userId) {
    	try {
    		String requestLink = WebserviceConstant.GET_USER_VIA_ID + "/" + userId + ".json/?access_token=" + accessToken;
    		Log.i(tag, "getUserViaId url: " + requestLink);
    		String result;
			result = WebServiceUtil.getData(requestLink);
			Log.i(tag, "getUserViaId result: " + result);
			Gson gson = new Gson();
			UserModel model = gson.fromJson(result, UserModel.class);
			return model;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(tag, "getUserViaId ex: " + e.toString());
		}
    	return null;
    }
    
    //http://threemins-server-staging.herokuapp.com/api/v1/users/125/followers/?access_token=2220b698852d4946140fa7d384cc65e795923b69157dbef35196a78c798eecce&page=1
    public List<UserModel> getFollowers(String accessToken, int userID, int page) {
        try {
            String requestLink = String.format(WebserviceConstant.GET_FOLLOWERS, "" + userID) + "/?access_token=" + accessToken + "&page=" + page;
            Log.i(tag, "getFollowers url: " + requestLink);
            String result = WebServiceUtil.getData(requestLink);
            Log.i(tag, "getFolloers result: " + result);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserModel>>() {}.getType();
            List<UserModel> list = gson.fromJson(result, listType);
            Log.i(tag, "getFollowers list size: " + list.size());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(tag, "getFollowers ex: " + e.toString());
        }
        return null;
    }
    
    //http://threemins-server-staging.herokuapp.com/api/v1/users/125/followings/?access_token=2220b698852d4946140fa7d384cc65e795923b69157dbef35196a78c798eecce&page=1
    public List<UserModel> getFollowings(String accessToken, int userID, int page) {
        try {
            String requestLink = String.format(WebserviceConstant.GET_FOLLOWINGS, "" + userID) + "/?access_token=" + accessToken + "&page=" + page;
            Log.i(tag, "getFollowings url: " + requestLink);
            String result = WebServiceUtil.getData(requestLink);
            Log.i(tag, "getFollowings result: " + result);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserModel>>() {}.getType();
            List<UserModel> list = gson.fromJson(result, listType);
            Log.i(tag, "getFollowings list size: " + list.size());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(tag, "getFollowings ex: " + e.toString());
        }
        return null;
    }
    

    /**
     * http://threemins-server-staging.herokuapp.com/api/v1/products/<productID>/likes?
     * access_token=<token>
     * &page=<page>
     * 
     * Get list users who liked a product
     * 
     * This user model only has infomation of user ID, user avatar, user full name
     * 
     * */
    
    public List<UserModel> getListUserWhoLikedAProduct(int productID, String token, int page) {
        try {
            String link = String.format(WebserviceConstant.GET_USERS_WHO_LIKED_PRODUCT, "" + productID, token, "" + page);
            Log.i(tag, "getListUserWhoLikedAProduct url: " + link);
            String result = WebServiceUtil.getData(link);
            Log.i(tag, "getListUserWhoLikedAProduct result: " + result);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserModel>>() {}.getType();
            List<UserModel> list = gson.fromJson(result, listType);
            Log.i(tag, "getListUserWhoLikedAProduct list size: " + list.size());
            return list;
        } catch (Exception e) {
            Log.i(tag, "getListUserWhoLikedAProduct ex: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
