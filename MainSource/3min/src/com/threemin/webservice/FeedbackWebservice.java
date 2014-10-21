package com.threemin.webservice;

import java.lang.reflect.Type;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.threemin.app.FeedbackActivity;
import com.threemin.model.CommentModel;
import com.threemin.model.FeedbackModel;
import com.threemin.uti.WebserviceConstant;

public class FeedbackWebservice {
    
    public static final String tag = "FeedbackWebservice";

    public static final String FEEDBACK_NEGATIVE = "negative";
    public static final String FEEDBACK_POSITIVE = "positive";
    public static final String FEEDBACK_NORMAL = "normal";

    /**
     * /feedbacks ?access_token=<token> &product_id=<productID>
     * &schedule_id=<scheduleID> &feedback[content]=<content>
     * &feedback[status]=<negative || positive || normal>
     * &feedback[user_id]=<current user ID>"
     * */

    public int sendFeedback(String token, int productID, int scheduleID, String content, int status, int userID){
        String strStatus = "";
        switch (status) {
        case FeedbackActivity.CHECK_NO_COMMENT:
            strStatus = FEEDBACK_NORMAL;
            break;
            
        case FeedbackActivity.CHECK_NOT_SATISFIED:
            strStatus = FEEDBACK_NEGATIVE;
            break;
            
        case FeedbackActivity.CHECK_SATISFIED:
            strStatus = FEEDBACK_POSITIVE;
            break;

        default:
            break;
        }
        String link = String.format(
                WebserviceConstant.SEND_FEEDBACK, 
                token,
                "" + productID,
                "" + scheduleID,
                content,
                strStatus,
                "" + userID);
        
        String validLink = WebServiceUtil.getHttpUrl(link);
        
        Log.i(tag, "sendFeedback link: " + link);
        Log.i(tag, "sendFeedback valid link: " + validLink);
        int responseCode;
        try {
            responseCode = WebServiceUtil.postRequest(validLink);
            Log.i(tag, "sendFeedback responseCode: " + responseCode);
            return responseCode;
        } catch (Exception e) {
            Log.i(tag, "sendFeedback ex: " + e.toString());
            e.printStackTrace();
            return WebserviceConstant.RESPONSE_CODE_EXCEPTION;
        }
    }
    
    public List<FeedbackModel> getFeedbackOfUser(String token, int userID, int page) {
        String link = String.format(WebserviceConstant.GET_FEEDBACK_OF_USER, token, "" + userID, "" + page);
        Log.i(tag, "getFeedbackOfUser link: " + link);
        
        try {
            String result = WebServiceUtil.getData(link);
            Log.i(tag, "getFeedbackOfUser result: " + result);
            Type listType = new TypeToken<List<FeedbackModel>>() {
            }.getType();
            List<FeedbackModel> list = new Gson().fromJson(result, listType);
            return list;
        } catch (Exception e) {
            Log.i(tag, "getFeedbackOfUser Exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<FeedbackModel> getFirstFeedbackOfUser(String token, int userID, int page) {
        String link = String.format(WebserviceConstant.GET_FEEDBACK_OF_USER, token, "" + userID, "" + page) + "&per_page=1";
        Log.i(tag, "getFirstFeedbackOfUser link: " + link);
        
        try {
            String result = WebServiceUtil.getData(link);
            Log.i(tag, "getFirstFeedbackOfUser result: " + result);
            Type listType = new TypeToken<List<FeedbackModel>>() {
            }.getType();
            List<FeedbackModel> list = new Gson().fromJson(result, listType);
            return list;
        } catch (Exception e) {
            Log.i(tag, "getFirstFeedbackOfUser Exception: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}
