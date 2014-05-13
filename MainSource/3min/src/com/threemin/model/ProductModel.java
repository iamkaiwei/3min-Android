package com.threemin.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProductModel {

	private int id;
	private String name;
	private String description;
	private String price;
	private int like;
	@SerializedName("sold_out")
	boolean soldOut;
	@SerializedName("venue_id")
	private String venueId;
	@SerializedName("venue_name")
	private String venueName;
	@SerializedName("venue_long")
	private double venueLong;
	@SerializedName("venue_lat")
	private double venueLat;
	@SerializedName("create_time")
	long createTime;
	@SerializedName("update_time")
	long updateTime;
	private boolean liked;
	private List<ImageModel> images;
	private CategoryModel category;
	private UserModel owner;
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
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public int getLike() {
		return like;
	}
	public void setLike(int like) {
		this.like = like;
	}
	public boolean isSoldOut() {
		return soldOut;
	}
	public void setSoldOut(boolean soldOut) {
		this.soldOut = soldOut;
	}
	public String getVenueId() {
		return venueId;
	}
	public void setVenueId(String venueId) {
		this.venueId = venueId;
	}
	public String getVenueName() {
		return venueName;
	}
	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	public double getVenueLong() {
		return venueLong;
	}
	public void setVenueLong(double venueLong) {
		this.venueLong = venueLong;
	}
	public double getVenueLat() {
		return venueLat;
	}
	public void setVenueLat(double venueLat) {
		this.venueLat = venueLat;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public boolean isLiked() {
		return liked;
	}
	public void setLiked(boolean liked) {
		this.liked = liked;
	}
	public List<ImageModel> getImages() {
		return images;
	}
	public void setImages(List<ImageModel> images) {
		this.images = images;
	}
	public CategoryModel getCategory() {
		return category;
	}
	public void setCategory(CategoryModel category) {
		this.category = category;
	}
	public UserModel getOwner() {
		return owner;
	}
	public void setOwner(UserModel owner) {
		this.owner = owner;
	}
	
	
}
