package com.threemin.model;

public class MessageModel {
	
	private String msg;
	private boolean isTheirs;
	
	public MessageModel(String msg, boolean isTheirs) {
		this.msg = msg;
		this.isTheirs = isTheirs;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isTheirs() {
		return isTheirs;
	}
	public void setTheirs(boolean isTheirs) {
		this.isTheirs = isTheirs;
	}
	
}
