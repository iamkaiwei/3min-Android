package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.CategoryModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;

public class CategoryWebservice {

	List<CategoryModel> currCategoryModels;
	private static CategoryWebservice instance;

	private CategoryWebservice() {
		currCategoryModels = new ArrayList<CategoryModel>();
	}

	public static CategoryWebservice getInstance() {
		if (instance == null) {
			instance = new CategoryWebservice();
		}
		return instance;
	}

	public List<CategoryModel> getTaggableCategory(Context context)
			throws Exception {
		String tokken = PreferenceHelper.getInstance(context).getTokken();
		String requestLink = WebserviceConstant.GET_CATE_TAGGABLE
				+ "?access_token=" + tokken;
		String result = WebServiceUtil.getData(requestLink);
		Type listType = new TypeToken<List<CategoryModel>>() {
		}.getType();
		List<CategoryModel> list = new Gson().fromJson(result, listType);
		currCategoryModels.addAll(list);
		return list;
	}

	public List<CategoryModel> getDisplayCategory(Context context)
			throws Exception {
		String tokken = PreferenceHelper.getInstance(context).getTokken();
		String requestLink = WebserviceConstant.GET_CATE_DISPLAY
				+ "?access_token=" + tokken;
		String result = WebServiceUtil.getData(requestLink);
		Type listType = new TypeToken<List<CategoryModel>>() {
		}.getType();
		List<CategoryModel> list = new Gson().fromJson(result, listType);
		currCategoryModels.addAll(list);
		return list;
	}

	public List<CategoryModel> getAllCategory(Context context) throws Exception {
		if (currCategoryModels.isEmpty()) {
			getDisplayCategory(context);
			getTaggableCategory(context);
		}
		return currCategoryModels;
	}

}
