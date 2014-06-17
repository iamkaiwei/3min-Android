package com.threemin.webservice;

import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.threemin.model.Conversation;
import com.threemin.uti.CommonConstant;
import com.threemin.uti.WebserviceConstant;

public class ConversationWebService {

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
		try {
			String result = WebServiceUtil.getData(url);
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
