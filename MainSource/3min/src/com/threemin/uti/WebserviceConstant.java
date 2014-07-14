package com.threemin.uti;

public class WebserviceConstant {

	private static final String URL_HOST = "http://threemins-server-staging.herokuapp.com";// for
																							// staging
	private static final String API = "/api/v1";
	public static final String LOGIN = URL_HOST + "/oauth/token";
	public static final String GET_CATE_TAGGABLE = URL_HOST + API + "/categories/taggable.json";
	public static final String GET_CATE_DISPLAY = URL_HOST + API + "/categories/display.json";
	public static final String GET_PRODUCT = URL_HOST + API + "/products.json";
	public static final String GET_PRODUCT_VIA_ID = URL_HOST + API + "/products";
	public static final String GET_PRODUCT_FOLLOWED = URL_HOST + API + "/products/followed.json";
	public static final String CREATE_PRODUCT = URL_HOST + API + "/products";
	public static final String LIKE = URL_HOST + API + "/products/%s/likes";
	public static final String GET_MY_PRODUCT = URL_HOST + API + "/products/me.json";
	public static final String GET_USER_LIKED_PRODUCT = URL_HOST + API + "/products/liked.json";
	public static final String CONVERSATION_EXIST = URL_HOST + API + "/conversations/exist.json";
	public static final String CONVERSATION = URL_HOST + API + "/conversations.json";
	public static final String CONVERSATION_GET_DETAIL = URL_HOST + API + "/conversations/";
	public static final String GET_CONVERSATION_VIA_ID = URL_HOST + API + "/conversations";
	public static final String PUSH_AUTHORIZE = URL_HOST + API + "/pushers/auth";
	public static final String GET_LIST_OFFER = URL_HOST + API + "/products/%s/show_offer.json";
	public static final String POST_MESSAGE_OFFLINE = URL_HOST + API + "/conversations/%s/conversation_replies.json";
	public static final String POST_BUNDLE_MESSAGE=URL_HOST + API + "/conversations/%s/conversation_replies/bulk_create.json";
	public static final String GET_ACTIVITIES = URL_HOST + API + "/activities.json/";
	public static final String GET_USER_PRODUCT=URL_HOST + API +"/users/%s/products.json";
	public static final String GET_USER_VIA_ID=URL_HOST + API + "/users";
	public static final String FOLLOW_USER = URL_HOST + API + "/relationships.json";
	public static final String UNFOLLOW_USER = URL_HOST + API + "/relationships/unfollow";
}
