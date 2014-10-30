package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.Conversation;
import com.threemin.model.MessageModel;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.WebserviceConstant;

public class ConversationWebService {
    
    public static final String tag = "ConversationWebService";

	public Conversation getConversation(String tokken, int product_id, int to) {
		String url = WebserviceConstant.CONVERSATION_EXIST + "?" + CommonConstant.KEY_ACCESS_TOKEN + "=" + tokken + "&"
				+ CommonConstant.KEY_PRODUCT_ID + "=" + product_id + "&" + CommonConstant.KEY_TO + "=" + to;
		Log.d("conversation", url);
		try {
			String result = WebServiceUtil.getData(url);
			Gson gson = new Gson();
			return gson.fromJson(result, Conversation.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Conversation createOffer(String tokken, int product_id, int to, float offer) {
		try {
			String url = WebserviceConstant.CONVERSATION;
			JSONObject data = new JSONObject();
			data.put(CommonConstant.KEY_ACCESS_TOKEN, tokken);
			data.put(CommonConstant.KEY_OFFER, offer);
			data.put(CommonConstant.KEY_PRODUCT_ID, product_id);
			data.put(CommonConstant.KEY_TO, to);
			String result = WebServiceUtil.postJson(url, data);
			Gson gson = new Gson();
			return gson.fromJson(result, Conversation.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getDetailConversation(String tokken, int conversationId) {
		String url = WebserviceConstant.CONVERSATION_GET_DETAIL + conversationId + ".json?"
				+ CommonConstant.KEY_ACCESS_TOKEN + "=" + tokken;
		Log.i(tag, "getDetailConversation link: " + url);
		try {
			String result = WebServiceUtil.getData(url);
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean postMessageOffline(int conversationId, String tokken, String message) {
		try {
			String url = String.format(WebserviceConstant.POST_MESSAGE_OFFLINE, "" + conversationId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(CommonConstant.KEY_ACCESS_TOKEN, tokken);
			jsonObject.put("message", message);
			String result = WebServiceUtil.postJson(url, jsonObject);
			Log.d("postOffline", result);
			JSONObject resultObject = new JSONObject(result);
			return resultObject.getString("status").equals("success");
		} catch (Exception e) {
			Log.e("error", e.getMessage());
			return false;
		}
	}

	public boolean postBulkMessage(int conversationId, String tokken, List<MessageModel> messageModels) {
		try {
			String url = String.format(WebserviceConstant.POST_BUNDLE_MESSAGE, "" + conversationId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(CommonConstant.KEY_ACCESS_TOKEN, tokken);
			JSONArray array = new JSONArray();
			for (MessageModel messageModel : messageModels) {
				array.put(messageModel.getbukMessage());
			}
			jsonObject.put("messages", array);
			String result = WebServiceUtil.postJson(url, jsonObject);
			Log.d("postOffline", result);
			JSONObject resultObject = new JSONObject(result);
			return resultObject.getString("status").equals("success");
		} catch (Exception e) {
			Log.e("ConversationWebService", "postBulkMessage error: " + e.toString());
			return false;
		}
	}

	public List<Conversation> getListMessage(String tokken) {
		String url = WebserviceConstant.CONVERSATION + "?access_token=" + tokken;
		Log.i("ConversationWebService", "URL: " + url);
		try {
			String result = WebServiceUtil.getData(url);
			Log.i("ConversationWebService", "getListMessage " + result);
			Type listType = new TypeToken<List<Conversation>>() {
			}.getType();
			List<Conversation> list = new Gson().fromJson(result, listType);
			return list;
		} catch (Exception e) {
			Log.e("ConversationWebService", "getListMessage error: " + e.toString());
			return null;
		}
	}

}
