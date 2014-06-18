package com.threemin.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class MessageModel {

	private String message;
	private boolean isTheirs;
	private UserModel userModel;
	private long timestamp;
	private String name;

	public MessageModel(String msg, boolean isTheirs, UserModel userModel) {
		this.message = msg;
		this.isTheirs = isTheirs;
		this.userModel = userModel;
	}

	public MessageModel(String data, UserModel model, boolean isTheirs) {
		this.userModel = model;
		try {
			JSONObject jsonObject = new JSONObject(data);
			message = jsonObject.getString("message");
			timestamp = jsonObject.getLong("timestamp");
			name = jsonObject.getString("name");
			this.isTheirs = isTheirs;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MessageModel(ReplyModel replyModel, UserModel model, boolean isTheirs) {
		message = replyModel.getReply();
		timestamp = replyModel.getTimestamp();
		name = "";
		this.userModel = model;
		this.isTheirs = isTheirs;
	}

	public String getMsg() {
		return message;
	}

	public void setMsg(String msg) {
		this.message = msg;
	}

	public boolean isTheirs() {
		return isTheirs;
	}

	public void setTheirs(boolean isTheirs) {
		this.isTheirs = isTheirs;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public JSONObject getbukMessage(){
		JSONObject jsonObject=new JSONObject();
		try {
			jsonObject.put("reply", message);
			jsonObject.put("created_at", timestamp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

}
