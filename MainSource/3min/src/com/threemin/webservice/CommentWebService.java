package com.threemin.webservice;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.CommentModel;
import com.threemin.uti.WebserviceConstant;

public class CommentWebService {

    public static final String tag = "CommentWebService";
    public static final int NUMBER_OF_TOP_COMMENTS = 3;
    /**
     * http://threemins-server-staging.herokuapp.com/api/v1/products/268/comments.json?access_token=8bb0c68bcf791a49d2f2475c5f87d0eb61d90c6af22b0a764e2d0406cdbaa21b&page=1
     * */
    public List<CommentModel> getComments(String accessToken, int productID, int page) {
        String url = String.format(WebserviceConstant.GET_COMMENTS_OF_PRODUCT, "" + productID) + "?access_token=" + accessToken + "&page=" + page + "&per_page=20";
        Log.i(tag, "getComments url: " + url);
        try {
            String result = WebServiceUtil.getData(url);
            Log.i(tag, "getComments result: " + result);
            Type listType = new TypeToken<List<CommentModel>>() {
            }.getType();
            List<CommentModel> list = new Gson().fromJson(result, listType);
            return list;
        } catch (Exception e) {
            Log.i(tag, "getComments ex: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * http://threemins-server-staging.herokuapp.com/api/v1/products/268/comments.json?access_token=8bb0c68bcf791a49d2f2475c5f87d0eb61d90c6af22b0a764e2d0406cdbaa21b&page=1
     * */
    public List<CommentModel> getTopComments(String accessToken, int productID) {
        String url = String.format(WebserviceConstant.GET_COMMENTS_OF_PRODUCT, "" + productID) + "?access_token=" + accessToken + "&page=1&per_page=" + NUMBER_OF_TOP_COMMENTS;
        Log.i(tag, "getTopComments url: " + url);
        try {
            String result = WebServiceUtil.getData(url);
            Log.i(tag, "getTopComments result: " + result);
            Type listType = new TypeToken<List<CommentModel>>() {
            }.getType();
            List<CommentModel> list = new Gson().fromJson(result, listType);
            return list;
        } catch (Exception e) {
            Log.i(tag, "getTopComments ex: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * http://threemins-server-staging.herokuapp.com/api/v1/products/268/comments.json?access_token=8bb0c68bcf791a49d2f2475c5f87d0eb61d90c6af22b0a764e2d0406cdbaa21b&comment[content]=1
     * @return: response code
     * 200: success
     * -1: exception
     * */
    public int postComments(String accessToken, int productID, String content) {
        try {
        String contentEncode = URLEncoder.encode(content, "UTF-8");
        String url = String.format(WebserviceConstant.POST_COMMENT, "" + productID, accessToken, contentEncode);
        Log.i(tag, "postComments url: " + url);
        Log.i(tag, "postComments validURL: " + url);
        
            int responseCode = WebServiceUtil.postRequest(url);
            Log.i(tag, "postComments response code: " + responseCode);
            return responseCode;
        } catch (Exception e) {
            Log.i(tag, "postComments ex: " + e.toString());
            e.printStackTrace();
        }
        return WebserviceConstant.RESPONSE_CODE_EXCEPTION;
    }
    
    public String getJSONComments(String accessToken, int productID) {
        String url = String.format(WebserviceConstant.GET_COMMENTS_OF_PRODUCT, "" + productID) + "?access_token=" + accessToken + "&page=1&per_page=20";
        Log.i(tag, "getJSONComments url: " + url);
        try {
            String result = WebServiceUtil.getData(url);
            Log.i(tag, "getJSONComments result: " + result);
            return result;
        } catch (Exception e) {
            Log.i(tag, "getJSONComments ex: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
    
}
