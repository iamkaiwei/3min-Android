package com.threemin.model;

import com.facebook.model.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Conversation {

//	 "id": 77,
//	    "product_id": 128,
//	    "offer": 200,
//	    "channel_name": "channel-128-122-36",
//	    "user": {
//	        "id": 36,
//	        "full_name": "Slan Joke",
//	        "avatar": "https://graph.facebook.com/100001631944993/picture?type=large",
//	        "facebook_avatar": "https://graph.facebook.com/100001631944993/picture?type=large"
//	    }
	
	private int id;
	@SerializedName("product_id")
	private int productId;
	@SerializedName("channel_name")
	private String channel_Name;
	private UserModel user;
	private long lastest_update;
	private String lastest_message;
	private ProductModel product;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getChannel_Name() {
		return channel_Name;
	}
	public void setChannel_Name(String channel_Name) {
		this.channel_Name = channel_Name;
	}
	public UserModel getUser() {
		return user;
	}
	public void setUser(UserModel user) {
		this.user = user;
	}
	public long getLastest_update() {
		return lastest_update;
	}
	public void setLastest_update(long lastest_update) {
		this.lastest_update = lastest_update;
	}
	public String getLastest_message() {
		return lastest_message;
	}
	public void setLastest_message(String lastest_message) {
		this.lastest_message = lastest_message;
	}
	public ProductModel getProduct() {
		return product;
	}
	public void setProduct(ProductModel product) {
		this.product = product;
	}
	
	
}
