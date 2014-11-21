package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.app.LoginActivity;
import com.threemin.model.CategoryModel;
import com.threemin.model.Conversation;
import com.threemin.model.ProductModel;
import com.threemin.uti.WebserviceConstant;

public class ProductWebservice {
    
    public static final String tag = "ProductWebservice";

	public List<ProductModel> getProduct(String accessToken, CategoryModel categoryModel, int page) throws Exception {
		String requestLink = WebserviceConstant.GET_PRODUCT + "?access_token=" + accessToken;
		if (categoryModel != null) {
			requestLink += "&category_id=" + categoryModel.getId();
		}
		if (page > 0) {
			requestLink += "&page=" + page;
		}
		String result = WebServiceUtil.getData(requestLink);
		Log.i("product", result);
		Type listType = new TypeToken<List<ProductModel>>() {
		}.getType();
		List<ProductModel> list = new Gson().fromJson(result, listType);
		return list;
	}

	public String getListOffer(String tokken, int id) {
		String url = String.format(WebserviceConstant.GET_LIST_OFFER, "" + id);
		url = url + "?access_token=" + tokken;
		try {
			String result = WebServiceUtil.getData(url);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public List<Conversation> parseListConversation(String input) {
		Type listType = new TypeToken<List<Conversation>>() {
		}.getType();
		List<Conversation> list = new Gson().fromJson(input, listType);
		return list;
	}
	
	/*http://threemins-server-staging.herokuapp.com/api/v1/products/239.json?access_token=a6a8f6b55036e7c6eca7776a4b56190d16fd2223a892f50775b8fc133dda2f46*/
	public ProductModel getProductViaID(String accessToken, String productID) {
		String url = WebserviceConstant.GET_PRODUCT_VIA_ID + "/" + productID + ".json?access_token=" + accessToken;
		Log.i("ProductWebservice", "getProductViaID url: " + url);
		try {
			String result = WebServiceUtil.getData(url);
			Log.i("ProductWebservice", "Product: " + result);
			Gson gson = new Gson();
			return gson.fromJson(result, ProductModel.class);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("ProductWebservice", "getProductViaID exception: " + e.toString());
		}
		return null;
	}
	
	/*http://threemins-server-staging.herokuapp.com/api/v1/products/<productID>/sold?access_token=<token>8&user_id=<userID>*/
	public String notifySellProduct(String accessToken, int productID, int buyerID) {
	    String url = String.format(WebserviceConstant.NOTIFY_SELL_PRODUCT, "" + productID, accessToken, "" + buyerID);
	    Log.i(tag, "notifySellProduct url:" + url );
	    
	    try {
            String result = WebServiceUtil.getData(url);
            Log.i(tag, "notifySellProduct result: " + result);
            return result;
        } catch (Exception e) {
            Log.i(tag, "notifySellProduct ex: " + e.toString());
            e.printStackTrace();
        }
	    return null;
	}
	
	public int deleteProduct(String accessToken, int productID) {
	    String url = String.format(WebserviceConstant.DELETE_PRODUCT, "" + productID, "" + accessToken);
	    Log.i(tag, "deleteProduct url: " + url);
	    
	    try {
            String result = WebServiceUtil.deleteJson(url);
            if (!TextUtils.isEmpty(result)) {
                Log.d("result", result);
                JSONObject jsonResult = new JSONObject(result);
                if ( jsonResult.getString("status").equals("success")) {
                    return WebserviceConstant.RESPONSE_CODE_SUCCESS;
                } else { 
                    return WebserviceConstant.RESPONSE_CODE_EXCEPTION;
                }
            } else {
                Log.i(tag, "deleteProduct result null: ");
                return WebserviceConstant.RESPONSE_CODE_EXCEPTION;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(tag, "deleteProduct ex: " + e.toString());
            return WebserviceConstant.RESPONSE_CODE_EXCEPTION;
        }
	}
}
