package com.threemin.model;

public class ReplyModel {
//	{
//        "id": 580,
//        "conversation_id": 85,
//        "user_id": 122,
//        "reply": "jjjjj",
//        "timestamp": 1403062100
//    },
	private int id;
	private int conversation_id;
	private int user_id;
	private String reply;
	private long timestamp;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getConversation_id() {
		return conversation_id;
	}
	public void setConversation_id(int conversation_id) {
		this.conversation_id = conversation_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
