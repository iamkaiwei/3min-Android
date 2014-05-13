package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.CategoryModel;
import com.threemin.model.ProductModel;
import com.threemin.uti.PreferenceHelper;
import com.threemin.uti.WebserviceConstant;

public class ProductWebservice {

	public List<ProductModel> getProduct(String accessToken, CategoryModel categoryModel, int page) throws Exception {
		String requestLink = WebserviceConstant.GET_PRODUCT + "?access_token=" + accessToken;
		if(categoryModel!=null){
			requestLink+="&category_id="+categoryModel.getId();
		}
		if(page>0){
			requestLink+="&page="+page;
		}
		String result = WebServiceUtil.getData(requestLink);
		Type listType = new TypeToken<List<ProductModel>>() {
		}.getType();
		List<ProductModel> list = new Gson().fromJson(result, listType);
		return list;
	}
}
