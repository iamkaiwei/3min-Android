package com.threemin.uti;

public class WebserviceConstant {

	private static final String URL_HOST = "http://threemins-server-staging.herokuapp.com";// for
																							// staging
	private static final String API = "/api/v1";
	public static final String LOGIN = URL_HOST + "/oauth/token";
	public static final String GET_CATE_TAGGABLE = URL_HOST + API + "/categories/taggable.json";
	public static final String GET_CATE_DISPLAY = URL_HOST + API + "/categories/display.json";
	public static final String GET_PRODUCT = URL_HOST + API + "/products.json";
	public static final String CREATE_PRODUCT = URL_HOST + API + "/products";
	public static final String LIKE=URL_HOST+API+"/products/%s/likes";
}
