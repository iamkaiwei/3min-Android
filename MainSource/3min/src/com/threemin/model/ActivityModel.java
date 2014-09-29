package com.threemin.model;

import com.google.gson.annotations.SerializedName;

public class ActivityModel {
	
	private int id;
	private String content;
	
	@SerializedName("subject_id")
	private int subjectID;
	
	@SerializedName("subject_type")
	private String subjectType;
	
    @SerializedName("display_image_url")
    private String displayImageUrl;
	
	@SerializedName("update_time")
	private long updateTime;
	
	private UserModel user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(int subjectID) {
		this.subjectID = subjectID;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	
	

	public String getDisplayImageUrl() {
        return displayImageUrl;
    }

    public void setDisplayImageUrl(String displayImage) {
        this.displayImageUrl = displayImage;
    }

    public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}
	
	
}
