package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.model.ActivityModel;
import com.threemin.uti.WebserviceConstant;

public class ActivityWebService {
	
	public List<ActivityModel>  getActivities(String tokken) {
		String url = WebserviceConstant.GET_ACTIVITIES + "?access_token=" +  tokken;
		Log.i("ActivityWebService", "Activity: " + url);
		try {
			String result = WebServiceUtil.getData(url);
			Log.i("ActivityWebService", "Result: " + result);
			
			Type listType = new TypeToken<List<ActivityModel>>() {
			}.getType();
			List<ActivityModel> list = new Gson().fromJson(result, listType);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("ActivityWebService", "Exception: " + e.toString());
			Log.i("ActivityWebService", "Exception msg: " + e.getMessage());
		}
		return null;
	}
	
	public List<ActivityModel>  getActivities(String tokken, int page) {
        String url = WebserviceConstant.GET_ACTIVITIES + "?access_token=" +  tokken + "&page=" + page;
        Log.i("ActivityWebService", "Activity: " + url);
        try {
            String result = WebServiceUtil.getData(url);
            Log.i("ActivityWebService", "Result: " + result);
            
            Type listType = new TypeToken<List<ActivityModel>>() {
            }.getType();
            List<ActivityModel> list = new Gson().fromJson(result, listType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ActivityWebService", "Exception: " + e.toString());
            Log.i("ActivityWebService", "Exception msg: " + e.getMessage());
        }
        return null;
    }

}
