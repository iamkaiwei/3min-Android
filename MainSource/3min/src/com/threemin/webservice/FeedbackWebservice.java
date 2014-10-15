package com.threemin.webservice;

import android.util.Log;

import com.threemin.app.FeedbackActivity;
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
        
        Log.i(tag, "sendFeedback link: " + link);
        int responseCode;
        try {
            responseCode = WebServiceUtil.postRequest(link);
            Log.i(tag, "sendFeedback responseCode: " + responseCode);
            return responseCode;
        } catch (Exception e) {
            Log.i(tag, "sendFeedback ex: " + e.toString());
            e.printStackTrace();
            return WebserviceConstant.RESPONSE_CODE_EXCEPTION;
        }
    }
}
