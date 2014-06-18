package com.threemin.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CategoryModel {
	private int id;
	private String name;
	private String description;
	@SerializedName("created_at")
	private String createAt;
	@SerializedName("update_at")
	private String updateAt;
	@SerializedName("specific_type")
	private String specificType;
	private ImageModel image;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreateAt() {
		return createAt;
	}
	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	public String getUpdateAt() {
		return updateAt;
	}
	public void setUpdateAt(String updateAt) {
		this.updateAt = updateAt;
	}
	public String getSpecificType() {
		return specificType;
	}
	public void setSpecificType(String specificType) {
		this.specificType = specificType;
	}
	public ImageModel getImage() {
		return image;
	}
	public void setImage(ImageModel image) {
		this.image = image;
	}
	@Override
	public String toString() {
		return this.name;
	}
	
	
	
}
