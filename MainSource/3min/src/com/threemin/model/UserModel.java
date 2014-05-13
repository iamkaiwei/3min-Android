package com.threemin.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {

	private int id;
	private String email;
	@SerializedName("created_at")
	private String createAt;
	@SerializedName("update_at")
	private String updateAt;
	@SerializedName("facebook_id")
	private String facebookId;
	private String username;
	@SerializedName("first_name")
	private String firstName;
	@SerializedName("full_name")
	private String fullName;
	@SerializedName("last_name")
	private String lastName;
	private String middleName;
	private String gender;
	private String birthday;
	private String udid;
	private String role;
	private String facebook_avatar;
	private ImageModel image;
	
	
	public ImageModel getImage() {
		return image;
	}

	public void setImage(ImageModel image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFacebook_avatar() {
		return facebook_avatar;
	}

	public void setFacebook_avatar(String faebook_avatar) {
		this.facebook_avatar = faebook_avatar;
	}

}
